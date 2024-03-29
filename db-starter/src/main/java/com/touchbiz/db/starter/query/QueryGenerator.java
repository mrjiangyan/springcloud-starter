package com.touchbiz.db.starter.query;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.model.SysPermissionDataRuleModel;
import com.touchbiz.common.utils.date.DateTimeFormat;
import com.touchbiz.common.utils.date.LocalDateTimeUtils;
import com.touchbiz.common.utils.reflect.SpringUtils;
import com.touchbiz.common.utils.security.IDataAutor;
import com.touchbiz.common.utils.text.CommonConstant;
import com.touchbiz.common.utils.text.SymbolConstant;
import com.touchbiz.common.utils.text.oConvertUtils;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.db.starter.constant.DataBaseConstant;
import com.touchbiz.db.starter.utils.SqlInjectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 查询生成器
 * @author: jeecg-boot
 */
@Slf4j
@Component
public class QueryGenerator {

	@Autowired
	private IDataAutor dataAutor;

	private  String DB_TYPE = "";

	private  DbType dbTypeEnum = null;

	public  final String SQL_RULES_COLUMN = "SQL_RULES_COLUMN";

	private  final String BEGIN = "_begin";
	private  final String END = "_end";
	/**
	 * 数字类型字段，拼接此后缀 接受多值参数
	 */
	private  final String MULTI = "_MultiString";
	private  final String STAR = "*";
	private  final String COMMA = ",";
	/**
	 * 查询 逗号转义符 相当于一个逗号【作废】
	 */
	public  final String QUERY_COMMA_ESCAPE = "++";
	private  final String NOT_EQUAL = "!";
	/**页面带有规则值查询，空格作为分隔符*/
	private  final String QUERY_SEPARATE_KEYWORD = " ";
	/**高级查询前端传来的参数名*/
	private  final String SUPER_QUERY_PARAMS = "superQueryParams";
	/** 高级查询前端传来的拼接方式参数名 */
	private  final String SUPER_QUERY_MATCH_TYPE = "superQueryMatchType";
	/** 单引号 */
	public  static final String SQL_SQ = "'";
	/**排序列*/
	private  final String ORDER_COLUMN = "column";
	/**排序方式*/
	private  final String ORDER_TYPE = "order";
	private  final String ORDER_TYPE_ASC = "ASC";

	/**mysql 模糊查询之特殊字符下划线 （_、\）*/
	public  final String LIKE_MYSQL_SPECIAL_STRS = "_,%";

	/**日期格式化yyyy-MM-dd*/
	public  final String YYYY_MM_DD = "yyyy-MM-dd";

	/**to_date*/
	public  final String TO_DATE = "to_date";

	/**时间格式化 */
	private  final ThreadLocal<SimpleDateFormat> LOCAL = new ThreadLocal<>();
	private  SimpleDateFormat getTime(){
		SimpleDateFormat time = LOCAL.get();
		if(time == null){
			time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      LOCAL.set(time);
		}
		return time;
	}
	
	/**
	 * 获取查询条件构造器QueryWrapper实例 通用查询条件已被封装完成
	 * @param searchObj 查询实体
	 * @param parameterMap request.getParameterMap()
	 * @return QueryWrapper实例
	 */
	public  <T> QueryWrapper<T> initQueryWrapper(T searchObj,Map<String, String> parameterMap){
		long start = System.currentTimeMillis();
		QueryWrapper<T> queryWrapper = new QueryWrapper<>();
		installMplus(queryWrapper, searchObj, parameterMap);
		log.debug("---查询条件构造器初始化完成,耗时:"+(System.currentTimeMillis()-start)+"毫秒----");
		return queryWrapper;
	}

	/**
	 * 组装Mybatis Plus 查询条件
	 * <p>使用此方法 需要有如下几点注意:  
	 * <br>1.使用QueryWrapper 而非LambdaQueryWrapper;
	 * <br>2.实例化QueryWrapper时不可将实体传入参数  
	 * <br>错误示例:如QueryWrapper<JeecgDemo> queryWrapper = new QueryWrapper<JeecgDemo>(jeecgDemo);
	 * <br>正确示例:QueryWrapper<JeecgDemo> queryWrapper = new QueryWrapper<JeecgDemo>();
	 * <br>3.也可以不使用这个方法直接调用 {@link #initQueryWrapper}直接获取实例
	 */
	private void installMplus(QueryWrapper<?> queryWrapper,Object searchObj,Map<String, String> parameterMap) {
		
		/*
		 * 注意:权限查询由前端配置数据规则 当一个人有多个所属部门时候 可以在规则配置包含条件 orgCode 包含 #{sys_org_code}
		但是不支持在自定义SQL中写orgCode in #{sys_org_code} 
		当一个人只有一个部门 就直接配置等于条件: orgCode 等于 #{sys_org_code} 或者配置自定义SQL: orgCode = '#{sys_org_code}'
		*/
		
		//区间条件组装 模糊查询 高级查询组装 简单排序 权限查询
		PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(searchObj);
		Map<String,SysPermissionDataRuleModel> ruleMap = getRuleMap();
		
		//权限规则自定义SQL表达式
		for (String c : ruleMap.keySet()) {
			if(oConvertUtils.isNotEmpty(c) && c.startsWith(SQL_RULES_COLUMN)){
				queryWrapper.and(i ->i.apply(getSqlRuleValue(ruleMap.get(c).getRuleValue())));
			}
		}
		
		String name, type, column;
		// update-begin--Author:taoyan Date:20200923 for：issues/1671 如果字段加注解了@TableField(exist = false),不走DB查询-------
		//定义实体字段和数据库字段名称的映射 高级查询中 只能获取实体字段 如果设置TableField注解 那么查询条件会出问题
		Map<String,String> fieldColumnMap = new HashMap<>(5);
		for (PropertyDescriptor origDescriptor : origDescriptors) {
			//aliasName = origDescriptors[i].getName(); mybatis 不存在实体属性 不用处理别名的情况
			name = origDescriptor.getName();
			type = origDescriptor.getPropertyType().toString();
			try {
				if (judgedIsUselessField(name) || !PropertyUtils.isReadable(searchObj, name)) {
					continue;
				}

				Object value = PropertyUtils.getSimpleProperty(searchObj, name);
				column = getTableFieldName(searchObj.getClass(), name);
				if (column == null) {
					//column为null只有一种情况 那就是 添加了注解@TableField(exist = false) 后续都不用处理了
					continue;
				}
				fieldColumnMap.put(name, column);
				//数据权限查询
				if (ruleMap.containsKey(name)) {
					addRuleToQueryWrapper(ruleMap.get(name), column, origDescriptor.getPropertyType(), queryWrapper);
				}
				//区间查询
				doIntervalQuery(queryWrapper, parameterMap, type, name, column);
				//判断单值 参数带不同标识字符串 走不同的查询
				//TODO 这种前后带逗号的支持分割后模糊查询(多选字段查询生效) 示例：,1,3,
				if (null != value && value.toString().startsWith(COMMA) && value.toString().endsWith(COMMA)) {
					String multiLikeval = value.toString().replace(",,", COMMA);
					String[] vals = multiLikeval.substring(1).split(COMMA);
					final String field = oConvertUtils.camelToUnderline(column);
					if (vals.length > 1) {
						queryWrapper.and(j -> {
							log.info("---查询过滤器，Query规则---field:{}, rule:{}, value:{}", field, "like", vals[0]);
							j = j.like(field, vals[0]);
							for (int k = 1; k < vals.length; k++) {
								j = j.or().like(field, vals[k]);
								log.info("---查询过滤器，Query规则 .or()---field:{}, rule:{}, value:{}", field, "like", vals[k]);
							}
							//return j;
						});
					} else {
						log.info("---查询过滤器，Query规则---field:{}, rule:{}, value:{}", field, "like", vals[0]);
						queryWrapper.and(j -> j.like(field, vals[0]));
					}
				} else {
					//根据参数值带什么关键字符串判断走什么类型的查询
					QueryRuleEnum rule = convert2Rule(value);
					value = replaceValue(rule, value);
					// add -begin 添加判断为字符串时设为全模糊查询
					//if( (rule==null || QueryRuleEnum.EQ.equals(rule)) && "class java.lang.String".equals(type)) {
					// 可以设置左右模糊或全模糊，因人而异
					//rule = QueryRuleEnum.LIKE;
					//}
					// add -end 添加判断为字符串时设为全模糊查询
					addEasyQuery(queryWrapper, column, rule, value);
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		// 排序逻辑 处理
		doMultiFieldsOrder(queryWrapper, parameterMap, fieldColumnMap.keySet());
				
		//高级查询
		doSuperQuery(queryWrapper, parameterMap, fieldColumnMap);
		// update-end--Author:taoyan Date:20200923 for：issues/1671 如果字段加注解了@TableField(exist = false),不走DB查询-------
		
	}


	/**
	 * 区间查询
	 * @param queryWrapper query对象
	 * @param parameterMap 参数map
	 * @param type     字段类型
	 * @param filedName  字段名称
	 * @param columnName  列名称
	 */
	private void doIntervalQuery(QueryWrapper<?> queryWrapper, Map<String, String> parameterMap, String type, String filedName, String columnName) throws ParseException {
		// 添加 判断是否有区间值
		String endValue = null,beginValue = null;
		if (parameterMap != null && parameterMap.containsKey(filedName + BEGIN)) {
			beginValue = parameterMap.get(filedName + BEGIN).trim();
			addQueryByRule(queryWrapper, columnName, type, beginValue, QueryRuleEnum.GE);

		}
		if (parameterMap != null && parameterMap.containsKey(filedName + END)) {
			endValue = parameterMap.get(filedName + END).trim();
			addQueryByRule(queryWrapper, columnName, type, endValue, QueryRuleEnum.LE);
		}
		//多值查询
		if (parameterMap != null && parameterMap.containsKey(filedName + MULTI)) {
			endValue = parameterMap.get(filedName + MULTI).trim();
			addQueryByRule(queryWrapper, columnName.replace(MULTI,""), type, endValue, QueryRuleEnum.IN);
		}
	}
	
	private void doMultiFieldsOrder(QueryWrapper<?> queryWrapper,Map<String, String> parameterMap, Set<String> allFields) {
		String column=null,order=null;
		if(parameterMap!=null&& parameterMap.containsKey(ORDER_COLUMN)) {
			column = parameterMap.get(ORDER_COLUMN);
		}
		if(parameterMap!=null&& parameterMap.containsKey(ORDER_TYPE)) {
			order = parameterMap.get(ORDER_TYPE);
		}
    log.debug("排序规则>>列:" + column + ",排序方式:" + order);
		if (oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			//字典字段，去掉字典翻译文本后缀
			if(column.endsWith(CommonConstant.DICT_TEXT_SUFFIX)) {
				column = column.substring(0, column.lastIndexOf(CommonConstant.DICT_TEXT_SUFFIX));
			}

			//update-begin-author:taoyan date:2022-5-16 for: issues/3676 获取系统用户列表时，使用SQL注入生效
			//判断column是不是当前实体的
			log.info("当前字段有："+ allFields);
			if (!allColumnExist(column, allFields)) {
				throw new BizException("请注意，将要排序的列字段不存在：" + column);
			}
			//update-end-author:taoyan date:2022-5-16 for: issues/3676 获取系统用户列表时，使用SQL注入生效

			//SQL注入check
			SqlInjectionUtil.filterContent(column);

			//update-begin--Author:scott Date:20210531 for：36 多条件排序无效问题修正-------
			// 排序规则修改
			// 将现有排序 _ 前端传递排序条件{....,column: 'column1,column2',order: 'desc'} 翻译成sql "column1,column2 desc"
			// 修改为 _ 前端传递排序条件{....,column: 'column1,column2',order: 'desc'} 翻译成sql "column1 desc,column2 desc"
			if (order.toUpperCase().contains(ORDER_TYPE_ASC)) {
				//queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
				String columnStr = oConvertUtils.camelToUnderline(column);
				String[] columnArray = columnStr.split(",");
				queryWrapper.orderByAsc(Arrays.asList(columnArray));
			} else {
				//queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
				String columnStr = oConvertUtils.camelToUnderline(column);
				String[] columnArray = columnStr.split(",");
				queryWrapper.orderByDesc(Arrays.asList(columnArray));
			}
			//update-end--Author:scott Date:20210531 for：36 多条件排序无效问题修正-------
		}
	}

	//update-begin-author:taoyan date:2022-5-23 for: issues/3676 获取系统用户列表时，使用SQL注入生效
	/**
	 * 多字段排序 判断所传字段是否存在
	 * @return
	 */
	private boolean allColumnExist(String columnStr, Set<String> allFields){
		boolean exist = true;
		if(columnStr.contains(COMMA)){
			String[] arr = columnStr.split(COMMA);
			for(String column: arr){
				if(!allFields.contains(column)){
					exist = false;
					break;
				}
			}
		}else{
			exist = allFields.contains(columnStr);
		}
		return exist;
	}
	//update-end-author:taoyan date:2022-5-23 for: issues/3676 获取系统用户列表时，使用SQL注入生效
	
	/**
	 * 高级查询
	 * @param queryWrapper 查询对象
	 * @param parameterMap 参数对象
	 * @param fieldColumnMap 实体字段和数据库列对应的map
	 */
	private void doSuperQuery(QueryWrapper<?> queryWrapper,Map<String, String> parameterMap, Map<String,String> fieldColumnMap) {
		if(parameterMap!=null&& parameterMap.containsKey(SUPER_QUERY_PARAMS)){
			String superQueryParams = parameterMap.get(SUPER_QUERY_PARAMS);
			String superQueryMatchType = parameterMap.get(SUPER_QUERY_MATCH_TYPE) != null ? parameterMap.get(SUPER_QUERY_MATCH_TYPE) : MatchTypeEnum.AND.getValue();
      MatchTypeEnum matchType = MatchTypeEnum.getByValue(superQueryMatchType);
      // update-begin--Author:sunjianlei Date:20200325 for：高级查询的条件要用括号括起来，防止和用户的其他条件冲突 -------
      try {
        superQueryParams = URLDecoder.decode(superQueryParams, StandardCharsets.UTF_8);
        List<QueryCondition> conditions = JsonUtils.json2list(superQueryParams, QueryCondition.class);
        if (conditions == null || conditions.size() == 0) {
          return;
        }
				// update-begin-author:sunjianlei date:20220119 for: 【JTC-573】 过滤空条件查询，防止 sql 拼接多余的 and
				List<QueryCondition> filterConditions = conditions.stream().filter(
						rule -> oConvertUtils.isNotEmpty(rule.getField())
								&& oConvertUtils.isNotEmpty(rule.getRule())
								&& oConvertUtils.isNotEmpty(rule.getVal())
				).toList();
				if (filterConditions.size() == 0) {
					return;
				}
				// update-end-author:sunjianlei date:20220119 for: 【JTC-573】 过滤空条件查询，防止 sql 拼接多余的 and
        log.info("---高级查询参数-->" + filterConditions);

        queryWrapper.and(andWrapper -> {
          for (int i = 0; i < filterConditions.size(); i++) {
            QueryCondition rule = filterConditions.get(i);
            if (oConvertUtils.isNotEmpty(rule.getField())
                && oConvertUtils.isNotEmpty(rule.getRule())
                && oConvertUtils.isNotEmpty(rule.getVal())) {

              log.debug("SuperQuery ==> " + rule);

              //update-begin-author:taoyan date:20201228 for: 【高级查询】 oracle 日期等于查询报错
							Object queryValue = rule.getVal();
              if("date".equals(rule.getType())){
								queryValue = LocalDateTimeUtils.stringToTimestamp(rule.getVal(), DateTimeFormat.DATE_FORMAT_WITH_BAR);
							}else if("datetime".equals(rule.getType())){
								queryValue = LocalDateTimeUtils.stringToTimestamp(rule.getVal(), DateTimeFormat.DATE_FORMAT_FULL);
							}
							// update-begin--author:sunjianlei date:20210702 for：【/issues/I3VR8E】高级查询没有类型转换，查询参数都是字符串类型 ----
							String dbType = rule.getDbType();
							if (oConvertUtils.isNotEmpty(dbType)) {
								try {
									String valueStr = String.valueOf(queryValue);
									switch (dbType.toLowerCase().trim()) {
										case "int" -> queryValue = Integer.parseInt(valueStr);
										case "bigdecimal" -> queryValue = new BigDecimal(valueStr);
										case "short" -> queryValue = Short.parseShort(valueStr);
										case "long" -> queryValue = Long.parseLong(valueStr);
										case "float" -> queryValue = Float.parseFloat(valueStr);
										case "double" -> queryValue = Double.parseDouble(valueStr);
										case "boolean" -> queryValue = Boolean.parseBoolean(valueStr);
										default -> {
										}
									}
								} catch (Exception e) {
									log.error("高级查询值转换失败：", e);
								}
							}
							// update-begin--author:sunjianlei date:20210702 for：【/issues/I3VR8E】高级查询没有类型转换，查询参数都是字符串类型 ----
              addEasyQuery(andWrapper, fieldColumnMap.get(rule.getField()), QueryRuleEnum.getByValue(rule.getRule()), queryValue);
							//update-end-author:taoyan date:20201228 for: 【高级查询】 oracle 日期等于查询报错

              // 如果拼接方式是OR，就拼接OR
              if (MatchTypeEnum.OR == matchType && i < (filterConditions.size() - 1)) {
                andWrapper.or();
              }
            }
          }
          //return andWrapper;
        });
      } catch (UnsupportedEncodingException e) {
        log.error("--高级查询参数转码失败：" + superQueryParams, e);
      } catch (Exception e) {
        log.error("--高级查询拼接失败：" + e.getMessage());
        e.printStackTrace();
      }
      // update-end--Author:sunjianlei Date:20200325 for：高级查询的条件要用括号括起来，防止和用户的其他条件冲突 -------
		}
		//log.info(" superQuery getCustomSqlSegment: "+ queryWrapper.getCustomSqlSegment());
	}
	/**
	 * 根据所传的值 转化成对应的比较方式
	 * 支持><= like in !
	 * @param value
	 * @return
	 */
	public QueryRuleEnum convert2Rule(Object value) {
		// 避免空数据
		// update-begin-author:taoyan date:20210629 for: 查询条件输入空格导致return null后续判断导致抛出null异常
		if (value == null) {
			return QueryRuleEnum.EQ;
		}
		String val = (value + "").trim();
		if (val.length() == 0) {
			return QueryRuleEnum.EQ;
		}
		// update-end-author:taoyan date:20210629 for: 查询条件输入空格导致return null后续判断导致抛出null异常
		QueryRuleEnum rule =null;

		//update-begin--Author:scott Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284-------------------
		//TODO 此处规则，只适用于 le lt ge gt
		// step 2 .>= =<
    int length2 = 2;
    int length3 = 3;
		if (rule == null && val.length() >= length3) {
			if(QUERY_SEPARATE_KEYWORD.equals(val.substring(length2, length3))){
				rule = QueryRuleEnum.getByValue(val.substring(0, 2));
			}
		}
		// step 1 .> <
		if (rule == null && val.length() >= length2) {
			if(QUERY_SEPARATE_KEYWORD.equals(val.substring(1, length2))){
				rule = QueryRuleEnum.getByValue(val.substring(0, 1));
			}
		}
		//update-end--Author:scott Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284---------------------

		// step 3 like
		//update-begin-author:taoyan for: /issues/3382 默认带*就走模糊，但是如果只有一个*，那么走等于查询
		if(rule == null && val.equals(STAR)){
			rule = QueryRuleEnum.EQ;
		}
		//update-end-author:taoyan for: /issues/3382 默认带*就走模糊，但是如果只有一个*，那么走等于查询
		if (rule == null && val.contains(STAR)) {
			if (val.startsWith(STAR) && val.endsWith(STAR)) {
				rule = QueryRuleEnum.LIKE;
			} else if (val.startsWith(STAR)) {
				rule = QueryRuleEnum.LEFT_LIKE;
			} else if(val.endsWith(STAR)){
				rule = QueryRuleEnum.RIGHT_LIKE;
			}
		}

		// step 4 in
		if (rule == null && val.contains(COMMA)) {
			//TODO in 查询这里应该有个bug 如果一字段本身就是多选 此时用in查询 未必能查询出来
			rule = QueryRuleEnum.IN;
		}
		// step 5 != 
		if(rule == null && val.startsWith(NOT_EQUAL)){
			rule = QueryRuleEnum.NE;
		}
		// step 6 xx+xx+xx 这种情况适用于如果想要用逗号作精确查询 但是系统默认逗号走in 所以可以用++替换【此逻辑作废】
		if(rule == null && val.indexOf(QUERY_COMMA_ESCAPE)>0){
			rule = QueryRuleEnum.EQ_WITH_ADD;
		}

		//update-begin--Author:taoyan Date:20201229 for：initQueryWrapper组装sql查询条件错误 #284---------------------
		//特殊处理：Oracle的表达式to_date('xxx','yyyy-MM-dd')含有逗号，会被识别为in查询，转为等于查询
		if(rule == QueryRuleEnum.IN && val.contains(YYYY_MM_DD) && val.contains(TO_DATE)){
			rule = QueryRuleEnum.EQ;
		}
		//update-end--Author:taoyan Date:20201229 for：initQueryWrapper组装sql查询条件错误 #284---------------------

		return rule != null ? rule : QueryRuleEnum.EQ;
	}
	
	/**
	 * 替换掉关键字字符
	 * 
	 * @param rule
	 * @param value
	 * @return
	 */
	private Object replaceValue(QueryRuleEnum rule, Object value) {
		if (rule == null) {
			return null;
		}
		if (! (value instanceof String)){
			return value;
		}
		String val = (value + "").trim();
		//update-begin-author:taoyan date:20220302 for: 查询条件的值为等号（=）bug #3443
		if(QueryRuleEnum.EQ.getValue().equals(val)){
			return val;
		}
		//update-end-author:taoyan date:20220302 for: 查询条件的值为等号（=）bug #3443
		if (rule == QueryRuleEnum.LIKE) {
			value = val.substring(1, val.length() - 1);
			//mysql 模糊查询之特殊字符下划线 （_、\）
			value = specialStrConvert(value.toString());
		} else if (rule == QueryRuleEnum.LEFT_LIKE || rule == QueryRuleEnum.NE) {
			value = val.substring(1);
			//mysql 模糊查询之特殊字符下划线 （_、\）
			value = specialStrConvert(value.toString());
		} else if (rule == QueryRuleEnum.RIGHT_LIKE) {
			value = val.substring(0, val.length() - 1);
			//mysql 模糊查询之特殊字符下划线 （_、\）
			value = specialStrConvert(value.toString());
		} else if (rule == QueryRuleEnum.IN) {
			value = val.split(",");
		} else if (rule == QueryRuleEnum.EQ_WITH_ADD) {
			value = val.replaceAll("\\+\\+", COMMA);
		}else {
			//update-begin--Author:scott Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284-------------------
			if(val.startsWith(rule.getValue())){
				//TODO 此处逻辑应该注释掉-> 如果查询内容中带有查询匹配规则符号，就会被截取的（比如：>=您好）
				value = val.replaceFirst(rule.getValue(),"");
			}else if(val.startsWith(rule.getCondition()+QUERY_SEPARATE_KEYWORD)){
				value = val.replaceFirst(rule.getCondition()+QUERY_SEPARATE_KEYWORD,"").trim();
			}
			//update-end--Author:scott Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284-------------------
		}
		return value;
	}
	
	private  void addQueryByRule(QueryWrapper<?> queryWrapper,String name,String type,String value,QueryRuleEnum rule) throws ParseException {
		if(oConvertUtils.isNotEmpty(value)) {
			//update-begin--Author:sunjianlei Date:20220104 for：【JTC-409】修复逗号分割情况下没有转换类型，导致类型严格的数据库查询报错 -------------------
			// 针对数字类型字段，多值查询
			if(value.contains(COMMA)){
				Object[] temp = Arrays.stream(value.split(COMMA)).map(v -> {
					try {
						return parseByType(v, type, rule);
					} catch (ParseException e) {
						e.printStackTrace();
						return v;
					}
				}).toArray();
				addEasyQuery(queryWrapper, name, rule, temp);
				return;
			}
			Object temp = parseByType(value, type, rule);
			addEasyQuery(queryWrapper, name, rule, temp);
			//update-end--Author:sunjianlei Date:20220104 for：【JTC-409】修复逗号分割情况下没有转换类型，导致类型严格的数据库查询报错 -------------------
		}
	}

	/**
	 * 根据类型转换给定的值
	 * @param value
	 * @param type
	 * @param rule
	 * @return
	 * @throws ParseException
	 */
	private  Object parseByType(String value, String type, QueryRuleEnum rule) throws ParseException {
		Object temp = switch (type) {
			case "class java.lang.Integer" -> Integer.parseInt(value);
			case "class java.math.BigDecimal" -> new BigDecimal(value);
			case "class java.lang.Short" -> Short.parseShort(value);
			case "class java.lang.Long" -> Long.parseLong(value);
			case "class java.lang.Float" -> Float.parseFloat(value);
			case "class java.lang.Double" -> Double.parseDouble(value);
			case "class java.util.Date" -> getDateQueryByRule(value, rule);
			default -> value;
		};
		return temp;
	}
	
	/**
	 * 获取日期类型的值
	 * @param value
	 * @param rule
	 * @return
	 * @throws ParseException
	 */
	private  Date getDateQueryByRule(String value,QueryRuleEnum rule) throws ParseException {
		Date date = null;
		int length = 10;
		if(value.length()==length) {
			if(rule==QueryRuleEnum.GE) {
				//比较大于
				date = getTime().parse(value + " 00:00:00");
			}else if(rule==QueryRuleEnum.LE) {
				//比较小于
				date = getTime().parse(value + " 23:59:59");
			}
			//TODO 日期类型比较特殊 可能oracle下不一定好使
		}
		if(date==null) {
			date = getTime().parse(value);
		}
		return date;
	}
	
	/**
	 * 根据规则走不同的查询
	 * @param queryWrapper QueryWrapper
	 * @param name     字段名字
	 * @param rule     查询规则
	 * @param value    查询条件值
	 */
	public  void addEasyQuery(QueryWrapper<?> queryWrapper, String name, QueryRuleEnum rule, Object value) {
		if (value == null || rule == null || oConvertUtils.isEmpty(value)) {
			return;
		}
		name = oConvertUtils.camelToUnderline(name);
		log.info("---查询过滤器，Query规则---field:{}, rule:{}, value:{}",name,rule.getValue(),value);
		switch (rule) {
			case GT -> queryWrapper.gt(name, value);
			case GE -> queryWrapper.ge(name, value);
			case LT -> queryWrapper.lt(name, value);
			case LE -> queryWrapper.le(name, value);
			case EQ, EQ_WITH_ADD -> queryWrapper.eq(name, value);
			case NE -> queryWrapper.ne(name, value);
			case IN -> {
				if (value instanceof String) {
					queryWrapper.in(name, (Object[]) value.toString().split(COMMA));
				} else if (value instanceof String[]) {
					queryWrapper.in(name, (Object[]) value);
				}
				//update-begin-author:taoyan date:20200909 for:【bug】in 类型多值查询 不适配postgresql #1671
				else if (value.getClass().isArray()) {
					queryWrapper.in(name, (Object[]) value);
				} else {
					queryWrapper.in(name, value);
				}
			}
			//update-end-author:taoyan date:20200909 for:【bug】in 类型多值查询 不适配postgresql #1671
			case LIKE -> queryWrapper.like(name, value);
			case LEFT_LIKE -> queryWrapper.likeLeft(name, value);
			case RIGHT_LIKE -> queryWrapper.likeRight(name, value);
			default -> log.info("--查询规则未匹配到---");
		}
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	private  boolean judgedIsUselessField(String name) {
		return "class".equals(name) || "ids".equals(name)
				|| "page".equals(name) || "rows".equals(name)
				|| "sort".equals(name) || "order".equals(name);
	}

	

	/**
	 * 获取请求对应的数据权限规则 TODO 相同列权限多个 有问题
	 * @return
	 */
	public  Map<String, SysPermissionDataRuleModel> getRuleMap() {
		Map<String, SysPermissionDataRuleModel> ruleMap = new HashMap<>(5);
		List<SysPermissionDataRuleModel> list = dataAutor.loadDataSearchConditon();
		if(list != null && list.size()>0){
			if(list.get(0)==null){
				return ruleMap;
			}
			for (SysPermissionDataRuleModel rule : list) {
				String column = rule.getRuleColumn();
				if(QueryRuleEnum.SQL_RULES.getValue().equals(rule.getRuleConditions())) {
					column = SQL_RULES_COLUMN+rule.getId();
				}
				ruleMap.put(column, rule);
			}
		}
		return ruleMap;
	}
	
	private  void addRuleToQueryWrapper(SysPermissionDataRuleModel dataRule, String name, Class propertyType, QueryWrapper<?> queryWrapper) {
		QueryRuleEnum rule = QueryRuleEnum.getByValue(dataRule.getRuleConditions());
		if(rule.equals(QueryRuleEnum.IN) && ! propertyType.equals(String.class)) {
			String[] values = dataRule.getRuleValue().split(",");
			Object[] objs = new Object[values.length];
			for (int i = 0; i < values.length; i++) {
				objs[i] = NumberUtils.parseNumber(values[i], propertyType);
			}
			addEasyQuery(queryWrapper, name, rule, objs);
		}else {
			if (propertyType.equals(String.class)) {
				addEasyQuery(queryWrapper, name, rule, converRuleValue(dataRule.getRuleValue()));
			}else if (propertyType.equals(Date.class)) {
				String dateStr =converRuleValue(dataRule.getRuleValue());
        int length = 10;
				if(dateStr.length()==length){
					addEasyQuery(queryWrapper, name, rule, LocalDateTimeUtils.stringToTimestamp(rule.getValue(), DateTimeFormat.DATE_FORMAT_WITH_BAR));
				}else{
					addEasyQuery(queryWrapper, name, rule, LocalDateTimeUtils.stringToTimestamp(rule.getValue(), DateTimeFormat.DATE_FORMAT_FULL));
				}

			}else {
				addEasyQuery(queryWrapper, name, rule, NumberUtils.parseNumber(dataRule.getRuleValue(), propertyType));
			}
		}
	}
	
	public  String converRuleValue(String ruleValue) {
		String value = null;//JwtUtil.getUserSystemData(ruleValue,null);
		return value!= null ? value : ruleValue;
	}

	/**
	* @author: scott
	* @Description: 去掉值前后单引号
	* @date: 2020/3/19 21:26
	* @param ruleValue: 
	* @Return: java.lang.String
	*/
	public  String trimSingleQuote(String ruleValue) {
		if (oConvertUtils.isEmpty(ruleValue)) {
			return "";
		}
		if (ruleValue.startsWith(QueryGenerator.SQL_SQ)) {
			ruleValue = ruleValue.substring(1);
		}
		if (ruleValue.endsWith(QueryGenerator.SQL_SQ)) {
			ruleValue = ruleValue.substring(0, ruleValue.length() - 1);
		}
		return ruleValue;
	}
	
	public  String getSqlRuleValue(String sqlRule){
		try {
			Set<String> varParams = getSqlRuleParams(sqlRule);
			for(String var:varParams){
				String tempValue = converRuleValue(var);
				sqlRule = sqlRule.replace("#{"+var+"}",tempValue);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return sqlRule;
	}
	
	/**
	 * 获取sql中的#{key} 这个key组成的set
	 */
	public  Set<String> getSqlRuleParams(String sql) {
		if(oConvertUtils.isEmpty(sql)){
			return null;
		}
		Set<String> varParams = new HashSet<>();
		String regex = "\\#\\{\\w+\\}";
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(sql);
		while(m.find()){
			String var = m.group();
			varParams.add(var.substring(var.indexOf("{")+1,var.indexOf("}")));
		}
		return varParams;
	}
	
	/**
	 * 获取查询条件 
	 * @param field
	 * @param alias
	 * @param value
	 * @param isString
	 * @return
	 */
	public  String getSingleQueryConditionSql(String field,String alias,Object value,boolean isString) {
		return getSingleQueryConditionSql(field, alias, value, isString,null);
	}

	/**
	 * 报表获取查询条件 支持多数据源
	 * @param field
	 * @param alias
	 * @param value
	 * @param isString
	 * @param dataBaseType
	 * @return
	 */
	public  String getSingleQueryConditionSql(String field,String alias,Object value,boolean isString, String dataBaseType) {
		if (value == null) {
			return "";
		}
		field = alias+oConvertUtils.camelToUnderline(field);
		QueryRuleEnum rule = convert2Rule(value);
		return getSingleSqlByRule(rule, field, value, isString, dataBaseType);
	}

	/**
	 * 获取单个查询条件的值
	 * @param rule
	 * @param field
	 * @param value
	 * @param isString
	 * @param dataBaseType
	 * @return
	 */
	private  String getSingleSqlByRule(QueryRuleEnum rule,String field,Object value,boolean isString, String dataBaseType) {
		String res = switch (rule) {
			case GT -> field + rule.getValue() + getFieldConditionValue(value, isString, dataBaseType);
			case GE -> field + rule.getValue() + getFieldConditionValue(value, isString, dataBaseType);
			case LT -> field + rule.getValue() + getFieldConditionValue(value, isString, dataBaseType);
			case LE -> field + rule.getValue() + getFieldConditionValue(value, isString, dataBaseType);
			case EQ -> field + rule.getValue() + getFieldConditionValue(value, isString, dataBaseType);
			case EQ_WITH_ADD -> field + " = " + getFieldConditionValue(value, isString, dataBaseType);
			case NE -> field + " <> " + getFieldConditionValue(value, isString, dataBaseType);
			case IN -> field + " in " + getInConditionValue(value, isString);
			case LIKE -> field + " like " + getLikeConditionValue(value, QueryRuleEnum.LIKE);
			case LEFT_LIKE -> field + " like " + getLikeConditionValue(value, QueryRuleEnum.LEFT_LIKE);
			case RIGHT_LIKE -> field + " like " + getLikeConditionValue(value, QueryRuleEnum.RIGHT_LIKE);
			default -> field + " = " + getFieldConditionValue(value, isString, dataBaseType);
		};
		return res;
	}


	/**
	 * 获取单个查询条件的值
	 * @param rule
	 * @param field
	 * @param value
	 * @param isString
	 * @return
	 */
	private  String getSingleSqlByRule(QueryRuleEnum rule,String field,Object value,boolean isString) {
		return getSingleSqlByRule(rule, field, value, isString, null);
	}

	/**
	 * 获取查询条件的值
	 * @param value
	 * @param isString
	 * @param dataBaseType
	 * @return
	 */
	private  String getFieldConditionValue(Object value,boolean isString, String dataBaseType) {
		String str = value.toString().trim();
		if(str.startsWith(SymbolConstant.EXCLAMATORY_MARK)) {
			str = str.substring(1);
		}else if(str.startsWith(QueryRuleEnum.GE.getValue())) {
			str = str.substring(2);
		}else if(str.startsWith(QueryRuleEnum.LE.getValue())) {
			str = str.substring(2);
		}else if(str.startsWith(QueryRuleEnum.GT.getValue())) {
			str = str.substring(1);
		}else if(str.startsWith(QueryRuleEnum.LT.getValue())) {
			str = str.substring(1);
		}else if(str.indexOf(QUERY_COMMA_ESCAPE)>0) {
			str = str.replaceAll("\\+\\+", COMMA);
		}
		if(dataBaseType==null){
			dataBaseType = getDbType();
		}
		if(isString) {
			if(DataBaseConstant.DB_TYPE_SQLSERVER.equals(dataBaseType)){
				return " N'"+str+"' ";
			}else{
				return " '"+str+"' ";
			}
		}else {
			// 如果不是字符串 有一种特殊情况 popup调用都走这个逻辑 参数传递的可能是“‘admin’”这种格式的
			if(DataBaseConstant.DB_TYPE_SQLSERVER.equals(dataBaseType) && str.endsWith(SymbolConstant.SINGLE_QUOTATION_MARK) && str.startsWith(SymbolConstant.SINGLE_QUOTATION_MARK)){
				return " N"+str;
			}
			return value.toString();
		}
	}

	private  String getInConditionValue(Object value,boolean isString) {
		//update-begin-author:taoyan date:20210628 for: 查询条件如果输入,导致sql报错
		String[] temp = value.toString().split(",");
		if(temp.length==0){
			return "('')";
		}
		if(isString) {
			List<String> res = new ArrayList<>();
			for (String string : temp) {
				if(DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())){
					res.add("N'"+string+"'");
				}else{
					res.add("'"+string+"'");
				}
			}
			return "("+String.join("," ,res)+")";
		}else {
			return "("+ value +")";
		}
		//update-end-author:taoyan date:20210628 for: 查询条件如果输入,导致sql报错
	}

	/**
	 * 先根据值判断 走左模糊还是右模糊
	 * 最后如果值不带任何标识(*或者%)，则再根据ruleEnum判断
	 * @param value
	 * @param ruleEnum
	 * @return
	 */
	private  String getLikeConditionValue(Object value, QueryRuleEnum ruleEnum) {
		String str = value.toString().trim();
		if(str.startsWith(SymbolConstant.ASTERISK) && str.endsWith(SymbolConstant.ASTERISK)) {
			if(DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())){
				return "N'%"+str.substring(1,str.length()-1)+"%'";
			}else{
				return "'%"+str.substring(1,str.length()-1)+"%'";
			}
		}else if(str.startsWith(SymbolConstant.ASTERISK)) {
			if(DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())){
				return "N'%"+str.substring(1)+"'";
			}else{
				return "'%"+str.substring(1)+"'";
			}
		}else if(str.endsWith(SymbolConstant.ASTERISK)) {
			if(DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())){
				return "N'"+str.substring(0,str.length()-1)+"%'";
			}else{
				return "'"+str.substring(0,str.length()-1)+"%'";
			}
		}else {
			if(str.contains(SymbolConstant.PERCENT_SIGN)) {
				if(DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())){
					if(str.startsWith(SymbolConstant.SINGLE_QUOTATION_MARK) && str.endsWith(SymbolConstant.SINGLE_QUOTATION_MARK)){
						return "N"+str;
					}else{
						return "N"+"'"+str+"'";
					}
				}else{
					if(str.startsWith(SymbolConstant.SINGLE_QUOTATION_MARK) && str.endsWith(SymbolConstant.SINGLE_QUOTATION_MARK)){
						return str;
					}else{
						return "'"+str+"'";
					}
				}
			}else {

				//update-begin-author:taoyan date:2022-6-30 for: issues/3810 数据权限规则问题
				// 走到这里说明 value不带有任何模糊查询的标识(*或者%)
				if (ruleEnum == QueryRuleEnum.LEFT_LIKE) {
					if (DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())) {
						return "N'%" + str + "'";
					} else {
						return "'%" + str + "'";
					}
				} else if (ruleEnum == QueryRuleEnum.RIGHT_LIKE) {
					if (DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())) {
						return "N'" + str + "%'";
					} else {
						return "'" + str + "%'";
					}
				} else {
					if (DataBaseConstant.DB_TYPE_SQLSERVER.equals(getDbType())) {
						return "N'%" + str + "%'";
					} else {
						return "'%" + str + "%'";
					}
				}
				//update-end-author:taoyan date:2022-6-30 for: issues/3810 数据权限规则问题

			}
		}
	}
	
	/**
	 *  根据权限相关配置生成相关的SQL 语句
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  String installAuthJdbc(Class<?> clazz) {
		StringBuilder sb = new StringBuilder();
		//权限查询
		Map<String,SysPermissionDataRuleModel> ruleMap = getRuleMap();
		PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
		String sqlAnd = " and ";
		for (String c : ruleMap.keySet()) {
			if(oConvertUtils.isNotEmpty(c) && c.startsWith(SQL_RULES_COLUMN)){
				sb.append(sqlAnd).append(getSqlRuleValue(ruleMap.get(c).getRuleValue()));
			}
		}
		String name, column;
		for (PropertyDescriptor origDescriptor : origDescriptors) {
			name = origDescriptor.getName();
			if (judgedIsUselessField(name)) {
				continue;
			}
			if (ruleMap.containsKey(name)) {
				column = getTableFieldName(clazz, name);
				if (column == null) {
					continue;
				}
				SysPermissionDataRuleModel dataRule = ruleMap.get(name);
				QueryRuleEnum rule = QueryRuleEnum.getByValue(dataRule.getRuleConditions());
				Class propType = origDescriptor.getPropertyType();
				boolean isString = propType.equals(String.class);
				Object value;
				if (isString) {
					value = converRuleValue(dataRule.getRuleValue());
				} else {
					value = NumberUtils.parseNumber(dataRule.getRuleValue(), propType);
				}
				String filedSql = getSingleSqlByRule(rule, oConvertUtils.camelToUnderline(column), value, isString);
				sb.append(sqlAnd).append(filedSql);
			}
		}
		log.info("query auth sql is:"+ sb);
		return sb.toString();
	}
	
	/**
	 * 根据权限相关配置 组装mp需要的权限
	 * @param queryWrapper
	 * @param clazz
	 * @return
	 */
	public  void installAuthMplus(QueryWrapper<?> queryWrapper,Class<?> clazz) {
		//权限查询
		Map<String,SysPermissionDataRuleModel> ruleMap = getRuleMap();
		PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
		for (String c : ruleMap.keySet()) {
			if(oConvertUtils.isNotEmpty(c) && c.startsWith(SQL_RULES_COLUMN)){
				queryWrapper.and(i ->i.apply(getSqlRuleValue(ruleMap.get(c).getRuleValue())));
			}
		}
		String name, column;
		for (PropertyDescriptor origDescriptor : origDescriptors) {
			name = origDescriptor.getName();
			if (judgedIsUselessField(name)) {
				continue;
			}
			column = getTableFieldName(clazz, name);
			if (column == null) {
				continue;
			}
			if (ruleMap.containsKey(name)) {
				addRuleToQueryWrapper(ruleMap.get(name), column, origDescriptor.getPropertyType(), queryWrapper);
			}
		}
	}

	/**
	 * 转换sql中的系统变量
	 * @param sql
	 * @return
	 */
	public  String convertSystemVariables(String sql){
		return getSqlRuleValue(sql);
	}

	/**
	 * 获取所有配置的权限 返回sql字符串 不受字段限制 配置什么就拿到什么
	 * @return
	 */
	public  String getAllConfigAuth() {
		StringBuilder sb = new StringBuilder();
		//权限查询
		Map<String,SysPermissionDataRuleModel> ruleMap = getRuleMap();
		String sqlAnd = " and ";
		for (String c : ruleMap.keySet()) {
			SysPermissionDataRuleModel dataRule = ruleMap.get(c);
			String ruleValue = dataRule.getRuleValue();
			if(oConvertUtils.isEmpty(ruleValue)){
				continue;
			}
			if(oConvertUtils.isNotEmpty(c) && c.startsWith(SQL_RULES_COLUMN)){
				sb.append(sqlAnd).append(getSqlRuleValue(ruleValue));
			}else{
				boolean isString = false;
				ruleValue = ruleValue.trim();
				if(ruleValue.startsWith("'") && ruleValue.endsWith("'")){
					isString = true;
					ruleValue = ruleValue.substring(1,ruleValue.length()-1);
				}
				QueryRuleEnum rule = QueryRuleEnum.getByValue(dataRule.getRuleConditions());
				String value = converRuleValue(ruleValue);
				String filedSql = getSingleSqlByRule(rule, c, value,isString);
				sb.append(sqlAnd).append(filedSql);
			}
		}
		log.info("query auth sql is = "+ sb);
		return sb.toString();
	}



	/**
	 * 获取系统数据库类型
	 */
	private  String getDbType(){
		if(oConvertUtils.isNotEmpty(DB_TYPE)){
			return DB_TYPE;
		}
		DataSource dataSource = SpringUtils.getApplicationContext().getBean(DataSource.class);
		return getDatabaseTypeByDataSource(dataSource);

	}

	/**
	 * 获取数据库类型
	 * @param dataSource
	 * @return
	 */
	private  String getDatabaseTypeByDataSource(DataSource dataSource) {
		if("".equals(DB_TYPE)) {
			try (Connection connection = dataSource.getConnection()) {
				DatabaseMetaData md = connection.getMetaData();
				String dbType = md.getDatabaseProductName().toUpperCase();
				String sqlserver = "SQL SERVER";
				if (dbType.contains(DataBaseConstant.DB_TYPE_MYSQL)) {
					DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
				} else if (dbType.contains(DataBaseConstant.DB_TYPE_ORACLE) || dbType.contains(DataBaseConstant.DB_TYPE_DM)) {
					DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
				} else if (dbType.contains(DataBaseConstant.DB_TYPE_SQLSERVER) || dbType.contains(sqlserver)) {
					DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
				} else if (dbType.contains(DataBaseConstant.DB_TYPE_POSTGRESQL)) {
					DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
				} else if (dbType.contains(DataBaseConstant.DB_TYPE_MARIADB)) {
					DB_TYPE = DataBaseConstant.DB_TYPE_MARIADB;
				} else {
					log.error("数据库类型:[" + dbType + "]不识别!");
					//throw new JeecgBootException("数据库类型:["+dbType+"]不识别!");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return DB_TYPE;

	}



	/**
	 * 获取class的 包括父类的
	 * @param clazz
	 * @return
	 */
	private  List<Field> getClassFields(Class<?> clazz) {
		List<Field> list = new ArrayList<>();
		Field[] fields;
		do{
			fields = clazz.getDeclaredFields();
			Collections.addAll(list, fields);
			clazz = clazz.getSuperclass();
		}while(clazz!= Object.class&&clazz!=null);
		return list;
	}

	/**
	 * 获取表字段名
	 * @param clazz
	 * @param name
	 * @return
	 */
	private  String getTableFieldName(Class<?> clazz, String name) {
		try {
			//如果字段加注解了@TableField(exist = false),不走DB查询
			Field field = null;
			try {
				field = clazz.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				//e.printStackTrace();
			}

			//如果为空，则去父类查找字段
			if (field == null) {
				List<Field> allFields = getClassFields(clazz);
				List<Field> searchFields = allFields.stream().filter(a -> a.getName().equals(name)).toList();
				if(searchFields!=null && searchFields.size()>0){
					field = searchFields.get(0);
				}
			}

			if (field != null) {
				TableField tableField = field.getAnnotation(TableField.class);
				if (tableField != null){
					if(!tableField.exist()){
						//如果设置了TableField false 这个字段不需要处理
						return null;
					}else{
						String column = tableField.value();
						//如果设置了TableField value 这个字段是实体字段
						if(!"".equals(column)){
							return column;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * mysql 模糊查询之特殊字符下划线 （_、\）
	 *
	 * @param value:
	 * @Return: java.lang.String
	 */
	private  String specialStrConvert(String value) {
		if (DataBaseConstant.DB_TYPE_MYSQL.equals(getDbType()) || DataBaseConstant.DB_TYPE_MARIADB.equals(getDbType())) {
			String[] specialStr = LIKE_MYSQL_SPECIAL_STRS.split(",");
			for (String str : specialStr) {
				if (value.contains(str)) {
					value = value.replace(str, "\\" + str);
				}
			}
		}
		return value;
	}
}

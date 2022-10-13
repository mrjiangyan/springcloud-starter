package com.touchbiz.webflux.starter.security;

import com.touchbiz.common.entity.model.SysPermissionDataRuleModel;
import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.common.utils.security.IDataAutor;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataAutor implements IDataAutor {

    public static final String MENU_DATA_AUTHOR_RULES = "MENU_DATA_AUTHOR_RULES";

    public static final String MENU_DATA_AUTHOR_RULE_SQL = "MENU_DATA_AUTHOR_RULE_SQL";

    /**
     * 往链接请求里面，传入数据查询条件
     *
     * @param request
     * @param dataRules
     */
    @Override
    public void installDataSearchConditon(ServerHttpRequest request, List<SysPermissionDataRuleModel> dataRules) {
        List<SysPermissionDataRuleModel> list = loadDataSearchConditon();
        if (list==null) {
            // 2.如果不存在，则new一个list
            list = new ArrayList();
        }
        for (SysPermissionDataRuleModel tsDataRule : dataRules) {
            list.add(tsDataRule);
        }
        // 3.往list里面增量存指
        ReactiveRequestContextHolder.getLocalAttributes().put(MENU_DATA_AUTHOR_RULES, list);
    }

    /**
     * 获取请求对应的数据权限规则
     *
     * @return
     */
    @Override
    public List<SysPermissionDataRuleModel> loadDataSearchConditon() {
        return (List<SysPermissionDataRuleModel>) ReactiveRequestContextHolder.getLocalAttributes().get(MENU_DATA_AUTHOR_RULES);
    }

    /**
     * 获取请求对应的数据权限SQL
     *
     * @return
     */
    @Override
    public String loadDataSearchConditonSqlString() {
        return (String) ReactiveRequestContextHolder.getLocalAttributes().get(MENU_DATA_AUTHOR_RULE_SQL);
    }


    /**
     * 往链接请求里面，传入数据查询条件
     *
     * @param request
     * @param sql
     */
    @Override
    public void installDataSearchConditon(ServerHttpRequest request, String sql) {
        String ruleSql = loadDataSearchConditonSqlString();
        if (!StringUtils.hasText(ruleSql)) {
            ReactiveRequestContextHolder.getLocalAttributes().put(MENU_DATA_AUTHOR_RULE_SQL,sql);
        }
    }


    /**
     * 将用户信息存到request
     * @param userinfo
     */
    @Override
    public void installUserInfo(SysUserCacheInfo userinfo) {
        ReactiveRequestContextHolder.putUser(userinfo);
    }

    /**
     * 从request获取用户信息
     * @return
     */
    @Override
    public SysUserCacheInfo loadUserInfo() {
        return (SysUserCacheInfo) ReactiveRequestContextHolder.getUser();
    }

    /**
     * 从request获取租户ID信息
     * @return
     */
    public String loadTenantId() {
        return ReactiveRequestContextHolder.getTenantId();
    }

}


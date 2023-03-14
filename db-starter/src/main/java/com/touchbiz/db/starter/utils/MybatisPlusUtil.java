package com.touchbiz.db.starter.utils;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.touchbiz.common.entity.exception.ParamException;
import com.touchbiz.db.starter.query.OrderBy;

import java.util.List;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.*;

/**
 * @author wxm
 */
public class MybatisPlusUtil {

    /**
     * @param queryWrapper {@link LambdaQueryWrapper}
     * @param column       排序字段
     * @param asc          是否升序
     * @param <T>          泛型
     */
    public static <T> void setOrderOne(LambdaQueryWrapper<T> queryWrapper, String column, boolean asc) {
        try {
            ISqlSegment[] sqlSegments = {ORDER_BY, () -> column, asc ? ASC : DESC};
            queryWrapper.getExpression().add(sqlSegments);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParamException("设置排序出错：" + e.getMessage());
        }
    }

    /**
     * @param queryWrapper {@link LambdaQueryWrapper}
     * @param order        {@link OrderBy} 排序
     * @param <T>          泛型
     */
    public static <T> void setOrder(LambdaQueryWrapper<T> queryWrapper, OrderBy order) {
        setOrderOne(queryWrapper, order.getColumn(), order.isAsc());
    }

    /**
     * @param queryWrapper {@link LambdaQueryWrapper}
     * @param list         {@link OrderBy} 排序
     * @param <T>          泛型
     */
    public static <T> void setOrder(LambdaQueryWrapper<T> queryWrapper, List<OrderBy> list) {
        for (OrderBy order : list) {
            setOrder(queryWrapper, order);
        }
    }

    /**
     * @param queryWrapper {@link LambdaQueryWrapper}
     * @param orderItem    {@link OrderItem} 排序
     * @param <T>          泛型
     */
    public static <T> void setOrderItem(LambdaQueryWrapper<T> queryWrapper, OrderItem orderItem) {
        setOrderOne(queryWrapper, orderItem.getColumn(), orderItem.isAsc());
    }

    /**
     * @param queryWrapper {@link LambdaQueryWrapper}
     * @param list         {@link OrderItem} 排序
     * @param <T>          泛型
     */
    public static <T> void setOrderItem(LambdaQueryWrapper<T> queryWrapper, List<OrderItem> list) {
        for (OrderItem order : list) {
            setOrderItem(queryWrapper, order);
        }
    }
}
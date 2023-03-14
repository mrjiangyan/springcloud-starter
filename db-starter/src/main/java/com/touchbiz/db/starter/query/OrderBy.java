package com.touchbiz.db.starter.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrderBy {

    /**
     * 字段名称
     */
    @NotEmpty
    private String column;


    /**
     * 排序类型 升序：asc 降序：desc
     * 默认升序
     */
    private boolean asc = true;

    /**
     * 是否进行驼峰转下划线
     */
    private boolean toUnder = false;


    public String getColumn() {
        return isToUnder() ? StrUtil.toUnderlineCase(column) : column;
    }

    public String getColumn(boolean toUnder) {
        return toUnder ? StrUtil.toUnderlineCase(column) : column;
    }

    /**
     * 获取 OrderItem
     *
     * @return
     */
    public OrderItem getOrderItem() {
        return getOrderItem(isAsc(), isToUnder());
    }

    /**
     * 获取 OrderItem
     *
     * @param isAsc 是否升序
     * @return
     */
    public OrderItem getOrderItem(boolean isAsc) {
        return getOrderItem(isAsc, isToUnder());
    }

    /**
     * 获取 OrderItem
     *
     * @param isToUnder 是否进行驼峰转下划线
     * @return
     */
    public OrderItem getOrderItemToUnder(boolean isToUnder) {
        return getOrderItem(isAsc(), isToUnder);
    }

    /**
     * 获取 OrderItem
     *
     * @param sort      排序方式  升序：asc 降序：desc
     * @param isToUnder 是否进行驼峰转下划线
     * @return
     */
    public OrderItem getOrderItem(String sort, boolean isToUnder) {
        return getOrderItem(isAsc(), isToUnder);
    }

    /**
     * 获取 OrderItem
     *
     * @param isAsc     是否升序
     * @param isToUnder 是否进行驼峰转下划线
     * @return
     */
    public OrderItem getOrderItem(boolean isAsc, boolean isToUnder) {
        String col = isToUnder ? StrUtil.toUnderlineCase(column) : column;
        OrderItem item= new OrderItem();
        item.setAsc(isAsc);
        item.setColumn(col);
        return item;
    }

    /**
     * 获取sql
     *
     * @return
     */
    public String getSql() {
        return (isToUnder() ? StrUtil.toUnderlineCase(column) : column) + (this.isAsc() ? " asc" : " desc");
    }
}
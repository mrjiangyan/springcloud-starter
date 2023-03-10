package com.touchbiz.db.starter.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * BaseService
 *
 * @author steve
 */
public interface TkBaseService<T> extends IService<T> {

    /**
     * 根据主键获取数据
     *
     * @param id
     * @return
     */
    T selectByPrimaryKey(Serializable id);


    /**
     * 创建一条记录
     *
     * @param domain
     */
    void create(T domain);

    void saveAndFush(T domain);

    void batchCreate(List<T> domain);

    /**
     * 更新一条记录
     *
     * @param domain
     */
    void update(T domain);

    /**
     * 标记删除一条记录
     *
     * @param domain
     */
    void logicDelete(T domain);

    /**
     * 删除一条记录
     *
     * @param domain
     */
    void physicsDelete(T domain);

    void physicsDeleteByPrimaryKey(Long id);

    /**
     * 查询
     *
     * @param entity
     * @return
     */
//    T selectOne(T entity);


    /**
     * 查询总记录数
     *
     * @param entity
     * @return
     */
//    Long selectCount(T entity);

    /**
     * 添加
     *
     * @param entity
     */
    void insert(T entity);


    /**
     * 插入 不插入null字段
     *
     * @param entity
     */
    void insertSelective(T entity);


    /**
     * 不update null
     *
     * @param entity
     */
    void updateSelectiveById(T entity);

    List<T> selectAll();

    /**
     * 标记删除一条记录
     *
     * @param domain
     */
    void delete(T domain);

    boolean save(T entity);

    LambdaQueryWrapper<T> createQueryWrapper();
}

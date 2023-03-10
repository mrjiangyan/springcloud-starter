package com.touchbiz.db.starter.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchbiz.db.starter.domain.BaseDomain;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * BaseService
 *
 * @author steve
 */

public abstract class TkBaseServiceImpl<T, M extends BaseMapper<T>> extends ServiceImpl<M,T> implements TkBaseService<T> {

    private final Class<T> persistentClass;
    @Autowired
    @Getter
    private M mapper;

    public TkBaseServiceImpl() {
        this.persistentClass = (Class<T>) getSuperClassGenricType(getClass(), 0);
    }

    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            genType = clazz.getSuperclass().getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                return Object.class;
            }
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    @Override
    public LambdaQueryWrapper<T> createQueryWrapper() {
        return new LambdaQueryWrapper<>(persistentClass);
    }

    @Override
    public T selectByPrimaryKey(Serializable id) {
        if(id instanceof Long){
            return mapper.selectById(String.valueOf(id));
        }
        return mapper.selectById(id);
    }

    @Override
    public void create(T entity) {
        fillData(entity);
        mapper.insert(entity);
    }


    @Override
    public void saveAndFush(T entity) {
        boolean flag = false;
        if (entity instanceof BaseDomain domain) {
            if (domain.getId() != null) {
                flag = true;
            }
        }
        if (flag) {
            mapper.updateById(entity);
        } else {
            fillData(entity);
            mapper.insert(entity);
        }
    }

    @Override
    public void batchCreate(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(this::fillData);
        list.forEach(mapper::insert);

    }

    @Override
    public void insert(T entity) {
        mapper.insert(entity);
    }

    @Override
    public void delete(T entity) {
        if (entity instanceof BaseDomain domain) {
            domain.setDeleted(true);
        }
        update(entity);
    }

    @Override
    public void logicDelete(T entity) {
        if (entity instanceof BaseDomain domain) {
            domain.setDeleted(true);
        }
        updateSelectiveById(entity);
    }

    @Override
    public void physicsDelete(T entity) {
        getMapper().deleteById(entity);
    }

    @Override
    public void physicsDeleteByPrimaryKey(Long id) {
        getMapper().deleteById(id);
    }

    @Override
    public void update(T entity) {
        if (entity instanceof BaseDomain domain) {
            domain.setGmtModify(null);
        }
        mapper.updateById(entity);
    }

    @Override
    public void insertSelective(T entity) {
        mapper.insert(entity);
    }

    @Override
    public void updateSelectiveById(T entity) {
        if (entity instanceof BaseDomain domain) {
            domain.setGmtModify(null);
        }
        mapper.updateById(entity);
    }

    @Override
    public List<T> selectAll() {
        return mapper.selectList(createQueryWrapper());
    }

    @Override
    public boolean save(T entity) {
        if (entity instanceof BaseDomain domain) {
            if (domain.getId() == null) {
                fillData(entity);
                this.insertSelective(entity);
            } else {
                domain.setGmtModify(null);
                this.updateSelectiveById(entity);
            }
        }
        return false;
    }

    private void fillData(T entity) {
        if (entity instanceof BaseDomain domain) {
            if (domain.getDeleted() == null) {
                domain.setDeleted(false);
            }
            if (domain.getStatus() == null) {
                domain.setStatus(true);
            }
        }
    }
}

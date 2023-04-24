package com.touchbiz.starter.example;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchbiz.db.starter.domain.BaseDomain;
import com.touchbiz.db.starter.service.TkBaseServiceImpl;

public class SimpleTest {

    @org.junit.Test
    public void test(){
        Test d = new Test();
        d.selectByPrimaryKey("aaa");
    }

    public static class A extends BaseDomain{

    }

    public interface AMapper extends BaseMapper<A> {

    }

    public static class Test extends TkBaseServiceImpl<A,AMapper> {

    }
}

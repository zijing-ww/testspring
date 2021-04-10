package com.yc.dao;

import com.yc.biz.StudentBizImpl;
import junit.framework.TestCase;

public class StudentDaoTest extends TestCase {

    private StudentDao studentDao;
    private StudentBizImpl studentBizImpl;

    @Override
    public void setUp() throws Exception {

        //1、能否自动完成  实例化对象   -》IOC   控制反转    -》由容器实例化对象，由容器来完成
        studentDao = new StudentDaoJpaImpl();

        studentBizImpl = new StudentBizImpl();
        //2、能否自动完成  装配过程    -》  DI  依赖注入    -》由容器装配对象
        studentBizImpl.setStudentDao(studentDao);
    }

    public void testAdd() {
        studentDao.add("张三");
    }

    public void testUpdate() {
        studentDao.update("张三");
    }

    public void testBizAdd(){
        studentBizImpl.add("张三");
    }


}
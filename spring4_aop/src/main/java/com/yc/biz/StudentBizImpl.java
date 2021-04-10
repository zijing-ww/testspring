package com.yc.biz;

import com.yc.dao.StudentDao;

/**
 * @program: testspring
 * @description:
 * @author: zz
 * @create: 2021-04-04 15:29
 */
public class StudentBizImpl {
    private StudentDao studentDao;

    public StudentBizImpl(StudentDao studentDao){
        this.studentDao = studentDao;
    }

    public StudentBizImpl(){}

    public void setStudentDao(StudentDao studentDao){
        this.studentDao = studentDao;
    }

    public int add(String name){
        System.out.println("======业务层======");
        System.out.println("=用户名是否重名");
        int result = studentDao.add(name);
        System.out.println("======业务层操作结束======");
        return result;
    }

    public void update(String name){
        System.out.println("======业务层======");
        System.out.println("=用户名是否重名");
        studentDao.update(name);
        System.out.println("======业务层操作结束======");

    }

    public void find(String name){
        System.out.println("======业务层======");
        System.out.println("=用户名是否存在");
        studentDao.find(name);
        System.out.println("======业务层操作结束======");

    }

}

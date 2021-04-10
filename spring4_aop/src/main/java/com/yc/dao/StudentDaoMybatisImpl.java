package com.yc.dao;

import java.util.Random;

/**
 * @program: testspring
 * @description:
 * @author: zz
 * @create: 2021-04-04 15:41
 */
public class StudentDaoMybatisImpl implements StudentDao{


    @Override
    public int add(String name) {
        {
            System.out.println("mybatis 添加学生："+name);
            Random r = new Random();
            return r.nextInt();
        }
    }

    @Override
    public void update(String name) {
        System.out.println("mybatis更新学生："+name);
    }
}

package com.yc;

import com.yc.bean.HelloWorld;
import com.yc.springframework.MyAppConfig;
import com.yc.springframework.context.MyAnnotationConfigApplicationContext;
import com.yc.springframework.context.MyApplicationContext;


import java.awt.datatransfer.StringSelection;

/**
 * @program: testspring
 * @description:
 * @author: zz
 * @create: 2021-04-05 11:57
 */
public class Test {

    public static void main(String[] args){
        MyApplicationContext ac = new MyAnnotationConfigApplicationContext(MyAppConfig.class);
        HelloWorld hw = (HelloWorld) ac.getBean("hw");
        hw.show();
    }
}

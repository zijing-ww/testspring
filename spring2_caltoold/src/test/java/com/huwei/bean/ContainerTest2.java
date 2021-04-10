package com.huwei.bean;

import com.AppConfig;
import com.mimi.bean.Person;
import com.mimi.bean.PersonBmiTool;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Random;
@RunWith(SpringJUnit4ClassRunner.class)//使用junit4进行测试
@ContextConfiguration(classes = {AppConfig.class})//加载配置文件

public class ContainerTest2 {

  //  private ApplicationContext ac;
    @Autowired
    private Container c;
    @Autowired
    private Random r;
    @Autowired
    private PersonBmiTool pbt;
    

    @Test
    public void testSave() {
        Person p1 = new Person("张三", 1.70, 80);
        Person p2 = new Person("李四", 1.70, 60);     //bmi最小
        Person p3 = new Person("王五", 1.60, 90);     // bmi值大
        Person p4 = new Person("赵六", 1.66, 90);
        Person p5 = new Person("田七", 1.65, 90);
        Person p6 = new Person("王八", 1.67, 190);

        c.save(p1);
        c.save(p2);
        c.save(p3);
        c.save(p4);
        c.save(p5);
        c.save(p6);

        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            //  Math.random()   生成 0-1之间的小数
            Person p7 = new Person("王八" + i, 1 + Math.random(), r.nextInt(80) + 30);
            c.save(p7);
        }


        Person max = (Person) c.getMax();   //取最大值
        Person min = (Person) c.getMin();   //最最小值

        System.out.println("最大值:" + max.getName());
        System.out.println("最小值:" + min.getName());

        System.out.println("平均bmi:" + c.getAvg());

        Object[] objs = c.getObjs();
        for (Object o : objs) {
            Person pp = (Person) o;
            System.out.println(pp.getName() + "\t" + pp.getHeight() + "\t" + pp.getWeight() + "\t" + pbt.measure(pp));
        }
    }
}
package com.yc.springframework.context;



import com.yc.springframework.stereotype.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Handler;

/**
 * @program: testspring
 * @description:
 * @author: zz
 * @create: 2021-04-05 14:15
 */
public class MyAnnotationConfigApplicationContext implements MyApplicationContext{

    private Map<String,Object> beanMap = new HashMap<>();

    public MyAnnotationConfigApplicationContext(Class<?>...componentClasses){
        try {
            register(componentClasses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register(Class<?>[] componentClasses) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException, ClassNotFoundException {
        if(componentClasses == null || componentClasses.length<=0){
            throw new RuntimeException("没有指定配置类");
        }
        for(Class cl:componentClasses){
            //只实现ioc    MyPostConstruct MyPreDestroy
            if(!cl.isAnnotationPresent(MyConfiguration.class)){
                continue;
            }
            String[] basePackages = getAppConfigPakages(cl);
            if(cl.isAnnotationPresent(MyComponentScan.class)){
                MyComponentScan mcs= (MyComponentScan)cl.getAnnotation(MyComponentScan.class);
                if(mcs.basePackages() != null && mcs.basePackages().length>0){
                    basePackages = mcs.basePackages();
                }
            }
            //处理@MyBean的情况
            Object obj = cl.newInstance();//obj就是当前解析的  MyAppConfig对象
            HandleAtMyBean(cl,obj);
            //处理    basePackages基础包下的所有托管bean
            for(String basePackage:basePackages){
                scanPackageAndSubPackageClasses(basePackage);
            }
            //继续其它托管bean
            handleManagedBean();
            //版本2：  循环  beanMap中的每个bean，找到他们每个类中的每个由@Autowired@Resource注解的方法以实现di实现DI
            handleDi(beanMap);
        }


    }

    /**
     * 循环  beanMap中的每个bean，找到他们每个类中的每个由@Autowired@Resource注解的方法以实现di
     * @param beanMap
     */
    private void handleDi(Map<String, Object> beanMap) throws InvocationTargetException, IllegalAccessException {
        Collection<Object> objectCollection = beanMap.values();
        for(Object obj:objectCollection){
            Class cls = obj.getClass();
            Method[] ms = cls.getDeclaredMethods();
            for(Method m:ms){
                if(m.isAnnotationPresent(MyAutowired.class) && m.getName().startsWith("set")){
                    invokeAutoWiredMethod(m,obj);
                }else if(m.isAnnotationPresent(MyResource.class) && m.getName().startsWith("set")){
                    invokeResourceMethod(m,obj);
                }
            }
            Field[] fs = cls.getDeclaredFields();
            for(Field field:fs){
                if (field.isAnnotationPresent(MyAutowired.class)){

                }else if (field.isAnnotationPresent(MyResource.class)){

                }
            }
        }
    }

    private void invokeResourceMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
        //1、取出  MyResource中的name属性值，当成beanId
        MyResource mr = m.getAnnotation(MyResource.class);
        String beanId = mr.name();
        //2、如果没有，则去除m方法中的参数的类型名，改成首字小写，当成beanId
        if(beanId == null||beanId.equalsIgnoreCase("")){
            String pname = m.getParameterTypes()[0].getSimpleName();
            beanId = pname.substring(0,1).toLowerCase() + pname.substring(1);
        }
        //3、从beeanMap去取出
        Object o = beanMap.get(beanId);
        //4、invok
        m.invoke(obj,o);
    }

    private void invokeAutoWiredMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
        //1、取出m的参数的类型
        Class typeClass = m.getParameterTypes()[0];
        //2、从beanMap中循环所有的object
        Set<String> keys = beanMap.keySet();
        for(String key : keys){
            //4、如果是，则从beanMap中取出
            Object o = beanMap.get(key);
            //3、判断这些object是否为 参数类型的实例instanceof
            if(o.getClass().getName().equalsIgnoreCase(typeClass.getName())){
                //5、invoke
                m.invoke(obj,o);
            }
        }
    }

    /**
     * 处理managedBeanClasses 所有的class类，筛选出所有的@Component @Service,@Repository的类，并实例化，存到beanMap中
     */
    private void handleManagedBean() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for(Class c:manageBeanClasses){
            if(c.isAnnotationPresent(MyComponent.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyService.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyRepository.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyController.class)){
                saveManagedBean(c);
            }else{

            }
        }
    }

    private void saveManagedBean(Class c) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object o = c.newInstance();
        handlePostConstruct(o,c);
        String beanId = c.getSimpleName().substring(0,1).toLowerCase() + c.getSimpleName().substring(1);
        beanMap.put(beanId,o);
    }

    /**
     * 扫描包和子包
     * @param basePackage
     * @throws IOException
     */
    private void scanPackageAndSubPackageClasses(String basePackage) throws IOException, ClassNotFoundException {
        String packagePath = basePackage.replaceAll("\\.","/");
        System.out.println("扫描包路径:"+basePackage +",替换后："+packagePath);  //com.yc.bean   ->com/yc/bean
        Enumeration<URL> files = Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while (files.hasMoreElements()){
            URL url = files.nextElement();
            System.out.println("配置的扫描路径为："+url.getFile());
            //递归这些目录，查找 .class文件
            findClassesInpackages(url.getFile(),basePackage);//第二个参数：com.yc.bean
        }
    }

    private Set<Class> manageBeanClasses = new HashSet<Class>();
    /**
     * 查找file下及子包所有要托管的class，存到一个Set(managedBeanClasses)中
     * @param file
     * @param basePackage
     */
    private void findClassesInpackages(String file, String basePackage) throws ClassNotFoundException {

        File f = new File(file);
       // File[] ff = f.listFiles();
        File[] classFiles = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".class")||file.isDirectory()   ;
            }
        });
        for(File cf : classFiles){
            if(cf.isDirectory()){
                basePackage += "." +cf.getName().substring(cf.getName().lastIndexOf("/") +1);
                findClassesInpackages(cf.getAbsolutePath(),basePackage);
            }else{
                //加载cf 作为 class 文件
                URL[] urls = new URL[]{};
                URLClassLoader ucl = new URLClassLoader(urls);
                Class c = ucl.loadClass(basePackage+"."+cf.getName().replace(".class",""));
                manageBeanClasses.add(c);

                //managedBeanClasses.add();
            }
        }
    }

    private void HandleAtMyBean(Class cls, Object obj) throws InvocationTargetException, IllegalAccessException {
        //1、获取cls中所有的method
        Method[] ms = cls.getDeclaredMethods();
        //2、循环、判断 每个method是否有@MyBean注解
        for(Method m:ms){
            if(m.isAnnotationPresent(MyBean.class)){
                //3、有，则invoke它，它有返回值，将空返回值存到    beanMap，键是返回名，值 是返回值 对象
                Object o = m.invoke(obj);
                //加入处理  @MyBean注解对应的方法所实例化的类中的@MyPostConstruct对应的方法
                handlePostConstruct(o,o.getClass());//o在这里指 o.getClass()它的反射对象
                beanMap.put(m.getName(),o);

            }
        }
    }

    /**
     * 处理一个Bean中的 @MyPostConstruct对应的方法
     * @param o
     * @param
     */
    private void handlePostConstruct(Object o, Class<?> cls) throws InvocationTargetException, IllegalAccessException {
        Method[]ms = cls.getDeclaredMethods();
        for(Method m:ms){
            if (m.isAnnotationPresent(MyPostConstruct.class)){
                m.invoke(o);
            }
        }
    }

    /*
    获取当前    AppConfig类所在的包路径
     */
    private String[] getAppConfigPakages(Class cl){

        String[] paths = new String[1];
        paths[0] = cl.getPackage().getName();
        return paths;
    }

    @Override
    public Object getBean(String id){
        return beanMap.get(id);
    }

}

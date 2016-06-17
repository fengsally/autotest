package com.example.administrator.testit.utils;

import android.test.InstrumentationTestSuite;


import com.example.administrator.testit.testcases.login.LoginActivityTest;


import junit.framework.TestSuite;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian on 2015/12/24.
 */
public class TestRunner extends JUnitReportTestRunner{

    @Override
    public TestSuite getAllTests() {
        InstrumentationTestSuite testSuite=new InstrumentationTestSuite(this);

        testSuite.addTestSuite(LoginActivityTest.class);

        return testSuite;
    }

    @Override
    public ClassLoader getLoader()
    {
        return TestRunner.class.getClassLoader();
    }


    public static List<Class> getTestcases(String packageName){
        List<Class> classes=new ArrayList<>();
        String path=System.getProperty("user.dir");
    //    LogUtil.e("cccc",path);
        packageName="app\\build\\generated\\res\\resValues\\androidTest\\baidu";
        String packagePath=path+File.separator+packageName;
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return ( file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : dirfiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                getTestcases(packageName + "." + file.getName()
                );
            }
            else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                if(className.endsWith("Test")) {
                    try {
                        //添加到集合中去
                        classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return classes;
    }

}

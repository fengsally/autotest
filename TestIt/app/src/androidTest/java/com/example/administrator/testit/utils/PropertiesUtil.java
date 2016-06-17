package com.example.administrator.testit.utils;

import android.content.Context;

import java.util.Properties;

/**
 * Created by vivian on 2015/12/28.
 */
public class PropertiesUtil {


    public static String  getProperty(Context c,String key) {
        String value=null;
        Properties properties = new Properties();
        try {
            properties.load(c.getAssets().open("test.properties"));
            value=properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}

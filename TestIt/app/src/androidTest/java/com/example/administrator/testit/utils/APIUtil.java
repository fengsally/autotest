package com.example.administrator.testit.utils;

import junit.framework.Assert;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vivian on 2015/9/24.
 */
public class APIUtil {
    public final static String API_SERVER = "http://api.octopus.test.ippjr-inc.com";

    public static int post(String url) {
        HttpURLConnection httpurlconnection = null;
        int code = 0;
        try {
            URL u = new URL(url);

            //以post方式请求
            httpurlconnection = (HttpURLConnection) u.openConnection();
            httpurlconnection.setDoOutput(true);
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.getOutputStream().flush();
            httpurlconnection.getOutputStream().close();

            //获取响应代码
            code = httpurlconnection.getResponseCode();
            if (code != 200) {
                Assert.fail("调用接口失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpurlconnection != null)
                httpurlconnection.disconnect();
        }

        return code;
    }
}

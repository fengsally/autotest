package com.example.administrator.testit.testcases.login;

import com.example.administrator.testit.elements.activities.auth.ElementLoginActivity;
import com.example.administrator.testit.testcases.BaseTestCase;
import com.example.administrator.testit.utils.OracleUtils;
import com.example.administrator.testit.utils.PropertiesUtil;


public class LoginActivityTest extends BaseTestCase {

    //设置ippjr账号的邮箱和手机号
    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testName() {
        //测试正常用户名登录
        uiHelper.getElementLoginActivity().doLoginWithAccount(username, password);
        assertTrue("显示姓名：ippjr_account_test", solo.waitForText("ippjr_account_test"));
    }

    public void testPassword() {
        //测试正常用户名登录
        uiHelper.getElementLoginActivity().doLoginWithAccount("", "q1w2e3");
        assertTrue("显示密码：q1w2e3", solo.waitForText("q1w2e3"));
    }

}

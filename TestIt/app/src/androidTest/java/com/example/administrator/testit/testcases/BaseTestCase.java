package com.example.administrator.testit.testcases;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.example.administrator.testit.utils.PropertiesUtil;
import com.example.administrator.testit.utils.UIHelper;
import com.example.administrator.testit.MainActivity;



/**
 * Created by vivian on 2015/12/22.
 */
abstract public class BaseTestCase extends ActivityInstrumentationTestCase2 {
    protected Solo solo;

    protected UIHelper uiHelper;

    protected String username, password,tradePassword;

    public BaseTestCase() {
        super(MainActivity.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.solo = new Solo(getInstrumentation(), getActivity());
        this.uiHelper = new UIHelper(solo);
        solo.unlockScreen();
        username = PropertiesUtil.getProperty(getActivity(), "username");
        password = PropertiesUtil.getProperty(getActivity(), "password");
        tradePassword = PropertiesUtil.getProperty(getActivity(), "tradePassword");
    }

    @Override
    public void tearDown() throws Exception {
        getActivity().finish();
       solo.finishOpenedActivities();
    }
}

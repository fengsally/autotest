package com.example.administrator.testit.elements.activities;

import com.robotium.solo.Solo;

/**
 * Created by vivian on 2015/12/28.
 */
public abstract class ElementBaseActivity {
    protected Solo solo;

    public ElementBaseActivity(Solo solo){
        this.solo=solo;
    }

    /**
     * 获取登录页上的元素
     */
    public abstract void initView();
}

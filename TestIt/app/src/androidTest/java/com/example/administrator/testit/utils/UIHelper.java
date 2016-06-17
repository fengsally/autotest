package com.example.administrator.testit.utils;

import com.robotium.solo.Solo;

import com.example.administrator.testit.elements.activities.auth.ElementLoginActivity;


/**
 * Created by s on 2015/12/22.
 */
public class UIHelper {

    private Solo solo;

    private ElementLoginActivity elementLoginActivity;



    public UIHelper(Solo solo) {
        this.solo = solo;
    }

    public Solo getSolo() {
        return solo;
    }


    public ElementLoginActivity getElementLoginActivity() {
        if (elementLoginActivity == null) {
            elementLoginActivity = new ElementLoginActivity(solo);
        }
        elementLoginActivity.initView();
        return elementLoginActivity;
    }



}

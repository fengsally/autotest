package com.example.administrator.testit.elements.activities.auth;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;
import com.example.administrator.testit.R;
import com.example.administrator.testit.elements.activities.ElementBaseActivity;


/**
 * Created by vivian on 2015/12/22.
 */
public class ElementLoginActivity extends ElementBaseActivity {

    private EditText usernameEditText, pwdEditText;

    private Button loginButton;

    private TextView registerView,forgetPwdView;

    public ElementLoginActivity(Solo solo) {
        super(solo);
    }

    @Override
    public void initView() {
        this.usernameEditText = (EditText) solo.getView(R.id.txtName);
        this.pwdEditText = (EditText) solo.getView(R.id.txtPassword);
        this.loginButton = (Button) solo.getCurrentActivity().findViewById(R.id.btnLogin);

    }

    public static ElementLoginActivity instance(Solo solo){
        ElementLoginActivity loginActivity=new ElementLoginActivity(solo);
        loginActivity.initView();
        return loginActivity;
    }

    /**
     * 获取username EditText 对象
     */
    public EditText getUsernameEditText() {
        return usernameEditText;
    }

    /**
     * 获取password EditText 对象
     */
    public EditText getPwdEditText() {
        return pwdEditText;
    }

    /**
     * 获取登陆按钮 Button 对象
     */
    public Button getLoginButton() {
        return loginButton;
    }


    /**
     * 清空用户名文本并输入测试用户名
     */
    public void enterUsername(String username) {
        solo.clearEditText(usernameEditText);
        solo.enterText(usernameEditText, username);
    }

    /**
     * 清空密码文本并输入测试密码
     */
    public void enterPwd(String pwd) {
        solo.clearEditText(pwdEditText);
        solo.enterText(pwdEditText, pwd);
    }

    /**
     * 点击登录按钮
     */
    public void clickLoginButoon() {
        solo.clickOnView(loginButton);
    }

    /**
     * 点击立即注册
     */


    /**
     * 点击忘记密码
     */


    /**
     * 输入用户名，密码，点击登录按钮
     */
    public void doLoginWithAccount(String username, String password) {
        enterUsername(username);
        enterPwd(password);
        clickLoginButoon();
    }
}

package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.util.MethodUtils;

public class LoginActivity extends BaseActivity {

    private TextView btn_Reg;
    private EditText user_phone;
    private EditText user_password;
    private ImageButton sys_pass;
    private Button btn_login;
    private Boolean isChecked=false;

    @Override
    int provideContentViewId() {
        return R.layout.login_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mHomeBtn.setVisibility(View.GONE);
        btn_Reg = findViewById(R.id.btn_reg);
        user_phone = findViewById(R.id.user_phone);
        user_password = findViewById(R.id.user_password);
        sys_pass = findViewById(R.id.sys_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_Reg.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        sys_pass.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reg:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_Home:
                new MethodUtils(this).getHome();
                break;
            case R.id.btn_Return:
                finish();
                break;
            case R.id.btn_login://登陆  请求后台
                break;
            case  R.id.sys_pass:
                if(isChecked){
                    user_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isChecked=false;
                }else{
                    user_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isChecked=true;
                          }

                break;

        }
    }
}

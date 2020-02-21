package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.view.CustomDialog;

public class LoginActivity extends BaseActivity {

    private TextView btn_Reg;
    private EditText user_phone;
    private EditText user_password;
    private ImageButton sys_pass;
    private Button btn_login;
    private Boolean isChecked = false;

    @Override
    int provideContentViewId() {
        return R.layout.login_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mHomeBtn.setVisibility(View.GONE);
        mMenus.setVisibility(View.GONE);
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
                MethodUtils.hideSoftInput(LoginActivity.this);
                finish();
                break;
            case R.id.btn_login://登陆  请求后台
                RetrofitManager.getInstance().loginByPassword(user_phone.getText().toString(),
                        user_password.getText().toString(),
                        "1234567890123456", new RetrofitManager.ICallBack() {
                    @Override
                    public void successData(String result) {
                        //customDialog.dismiss();
                        Gson gson2 = new Gson();
                        LoginResult c = gson2.fromJson(result, LoginResult.class);
                        HvApplication.TOKEN = c.getToken();
                        SharedPreferencesUtils.saveStringSharePrefer(LoginActivity.this, SharedPreferencesUtils.TOKEN, c.getToken());
                        Log.e("AA", "onResponse: " + result + "返回值");
                        MethodUtils.hideSoftInput(LoginActivity.this);
                        finish();
                    }

                    @Override
                    public void failureData(String error) {
                        Log.e("AA", "error: " + error + "错");
                        //customDialog.dismiss();
                    }
                });
                break;
            case R.id.sys_pass:
                if (isChecked) {
                    user_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isChecked = false;
                } else {
                    user_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isChecked = true;
                }

                break;

        }
    }
    CustomDialog customDialog;
    private void showProgrssDialog() {
        customDialog = new CustomDialog(this);
        //customDialog.setTitle("提醒");
        customDialog.setMessage(getResources().getString(R.string.loading));
        customDialog.setCancel("cancel", new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {
                Toast.makeText(LoginActivity.this, "取消成功！",Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.setConfirm("confirm", new CustomDialog.IOnConfirmListener(){
            @Override
            public void onConfirm(CustomDialog dialog) {
                Toast.makeText(LoginActivity.this, "确认成功！",Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.show();
    }
}

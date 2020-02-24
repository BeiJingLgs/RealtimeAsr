package com.hanvon.speech.realtime.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.R;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.DaoTimer;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.ToastUtils;

import androidx.annotation.NonNull;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity {
    private TextView get_mob;
    private Button user_register;
    private EventHandler eh;
    private EditText set_mob;
    private EditText reg_phone, mPassWordEd;
    private TimeCount time;
    private String msg, intentKey = "from", intentValue = "findPass";

    @Override
    int provideContentViewId() {
        return R.layout.register_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        //registerSDK();
        mHomeBtn.setVisibility(View.GONE);
        mMenus.setVisibility(View.GONE);

        reg_phone = findViewById(R.id.reg_phone);
        get_mob = findViewById(R.id.get_mob);
        set_mob = findViewById(R.id.set_mob);
        mPassWordEd = findViewById(R.id.reg_password);
        user_register = findViewById(R.id.user_register);
        get_mob.setOnClickListener(this);
        user_register.setOnClickListener(this);
        time = new TimeCount(60000, 1000);
        Intent intent = getIntent();
        msg = intent.getStringExtra(intentKey);
        if (TextUtils.equals(msg, intentValue)) {
            user_register.setText(getResources().getString(R.string.setPassword));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Home:
                new MethodUtils(this).getHome();
                break;
            case R.id.btn_Return:
                finish();
                break;

            case R.id.get_mob:
                time.start();
                RetrofitManager.getInstance().getVerificationCode(reg_phone.getText().toString(), new RetrofitManager.ICallBack() {
                    @Override
                    public void successData(String result) {
                        Gson gson2 = new Gson();
                        VerificationResult c = gson2.fromJson(result, VerificationResult.class);
                        if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                            ToastUtils.show(RegisterActivity.this, c.getMsg());
                        } else {
                            ToastUtils.show(RegisterActivity.this, c.getMsg());
                        }
                    }

                    @Override
                    public void failureData(String error) {
                        //Log.e("AA", "error: " + error + "错");
                        get_mob.setText("重新获取验证码");
                        get_mob.setClickable(true);
                        time.onFinish();
                    }
                });
                break;
            case R.id.user_register:
                HashMap<String,String> map = new HashMap<>();
                map.put("phone", reg_phone.getText().toString());
                map.put("smscode", set_mob.getText().toString());
                if (TextUtils.equals(msg, intentValue)) {
                    map.put("newPassword", mPassWordEd.getText().toString());
                    RetrofitManager.getInstance().changePasswordBySms(map, new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            //Log.e("AA", "onResponse: " + result + "返回值");
                            Gson gson2 = new Gson();
                            VerificationResult c = gson2.fromJson(result, VerificationResult.class);
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                ToastUtils.show(RegisterActivity.this, c.getMsg());
                                MethodUtils.hideSoftInput(RegisterActivity.this);
                                finish();
                            } else {
                                ToastUtils.show(RegisterActivity.this, c.getMsg());
                            }

                        }

                        @Override
                        public void failureData(String error) {
                        }
                    });
                } else {
                    map.put("password", mPassWordEd.getText().toString());
                    RetrofitManager.getInstance().registerByPhone(map, new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            Gson gson2 = new Gson();
                            VerificationResult c = gson2.fromJson(result, VerificationResult.class);
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                ToastUtils.show(RegisterActivity.this, c.getMsg());
                                MethodUtils.hideSoftInput(RegisterActivity.this);
                                finish();
                            } else {
                                ToastUtils.show(RegisterActivity.this, c.getMsg());
                            }
                        }

                        @Override
                        public void failureData(String error) {
                        }
                    });
                }

                break;

        }
    }

    // 使用完EventHandler需注销，否则可能出现内存泄漏
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }


    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            get_mob.setBackgroundColor(Color.parseColor("#B6B6D8"));
            get_mob.setClickable(false);
            get_mob.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            get_mob.setText("重新获取验证码");
            get_mob.setClickable(true);
            //get_mob.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }
}

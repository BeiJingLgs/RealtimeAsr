package com.hanvon.speech.realtime.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telecom.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.R;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.MobMessage;
import com.hanvon.speech.realtime.util.DaoTimer;
import com.hanvon.speech.realtime.util.MethodUtils;

import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity {
    private TextView get_mob;
    private Button user_register;
    private EventHandler eh;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
//                    Toast.makeText(RegisterActivity.this, "提交验证码成功", Toast.LENGTH_LONG).show();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
//                    Toast.makeText(RegisterActivity.this, "获取验证码成功", Toast.LENGTH_LONG).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else if (result == SMSSDK.RESULT_ERROR) {
                ((Throwable)data).printStackTrace();
                /**
                 * 下边代码是Mob后台回调的相关信息
                 */
//                String message = ((Throwable) data).getMessage();
//                Gson gson = new Gson();
//                MobMessage mobMessage = gson.fromJson(message, MobMessage.class);
//                if (mobMessage.getStatus() == 462) {
//                    Toast.makeText(RegisterActivity.this, "请求过于频繁", Toast.LENGTH_LONG).show();
//                }
            }
        }
    };
    private EditText set_mob;
    private EditText reg_phone;
    private DaoTimer timer;


    @Override
    int provideContentViewId() {
        return R.layout.register_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        registerSDK();
        mHomeBtn.setVisibility(View.GONE);
        reg_phone = findViewById(R.id.reg_phone);
        get_mob = findViewById(R.id.get_mob);
        set_mob = findViewById(R.id.set_mob);
        user_register = findViewById(R.id.user_register);
        get_mob.setOnClickListener(this);
        user_register.setOnClickListener(this);
        timer = new DaoTimer(DaoTimer.MAX_S, DaoTimer.COUNT_S, get_mob, this);
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
                // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
                SMSSDK.getVerificationCode("86", reg_phone.getText().toString());
                timer.start();
                break;
            case R.id.user_register:
                // 提交验证码，其中的code表示验证码，如“1357”
                SMSSDK.submitVerificationCode("86", reg_phone.getText().toString(), set_mob.getText().toString());
                break;

        }
    }

    private void registerSDK() {

        // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);

            }
        };

        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);
    }

    // 使用完EventHandler需注销，否则可能出现内存泄漏
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}

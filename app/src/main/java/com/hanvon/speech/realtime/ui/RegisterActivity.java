package com.hanvon.speech.realtime.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.DaoTimer;
import com.hanvon.speech.realtime.util.MethodUtils;

import androidx.annotation.NonNull;

import java.util.HashMap;

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
    private EditText reg_phone, mPassWordEd;
    //private DaoTimer timer;
    private TimeCount time;

    @Override
    int provideContentViewId() {
        return R.layout.register_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        registerSDK();
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
        //timer = new DaoTimer(DaoTimer.MAX_S, DaoTimer.COUNT_S, get_mob, this);
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
                        //Log.e("AA", "onResponse: " + result + "返回值");
                       // time.start();
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
                map.put("password", mPassWordEd.getText().toString());
                map.put("smscode", set_mob.getText().toString());
                RetrofitManager.getInstance().registerByPhone(map, new RetrofitManager.ICallBack() {
                    @Override
                    public void successData(String result) {
                        //Log.e("AA", "onResponse: " + result + "返回值");
                        MethodUtils.hideSoftInput(RegisterActivity.this);
                        finish();
                    }

                    @Override
                    public void failureData(String error) {
                        //Log.e("AA", "error: " + error + "错");

                    }
                });
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

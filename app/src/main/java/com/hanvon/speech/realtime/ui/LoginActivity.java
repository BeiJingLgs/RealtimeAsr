package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.Const;
import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.view.CustomDialog;

public class LoginActivity extends BaseActivity {

    private TextView btn_Reg, mFindPassTv, mSendVcCode, mVcCodeTv;
    private EditText user_phone, user_password, mUserPhoneEd, mVcCodeEd ;
    private ImageButton sys_pass;
    private Button btn_login;
    private Boolean isChecked = false;
    private View mPassLayout, mVcCodeLayout;

    @Override
    int provideContentViewId() {
        return R.layout.login_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mHomeBtn.setVisibility(View.GONE);
        mMenus.setVisibility(View.GONE);
        mPassLayout = findViewById(R.id.pass_login_layout);
        mVcCodeLayout = findViewById(R.id.vc_code_layout);
        mVcCodeTv = findViewById(R.id.vc_code_tv);
        mUserPhoneEd = findViewById(R.id.reg_phone);
        mVcCodeEd = findViewById(R.id.set_mob);
        mSendVcCode = findViewById(R.id.get_mob);
        btn_Reg = findViewById(R.id.btn_reg);
        user_phone = findViewById(R.id.user_phone);
        user_password = findViewById(R.id.user_password);
        sys_pass = findViewById(R.id.sys_pass);
        btn_login = findViewById(R.id.btn_login);
        mFindPassTv = findViewById(R.id.findpasswordBtn);
        time = new TimeCount(60000, 1000);
        btn_Reg.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        sys_pass.setOnClickListener(this);
        mFindPassTv.setOnClickListener(this);
        mSendVcCode.setOnClickListener(this);
        mVcCodeTv.setOnClickListener(this);
        mVcCodeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reg:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("from", "register");
                startActivity(intent);
                break;
            case R.id.btn_Home:
                new MethodUtils(this).getHome();
                break;
            case R.id.btn_Return:
                MethodUtils.hideSoftInput(LoginActivity.this);
                finish();
                break;
            case R.id.vc_code_tv:
                mPassLayout.setVisibility(View.GONE);
                mFindPassTv.setVisibility(View.GONE);
                mVcCodeTv.setVisibility(View.GONE);
                mVcCodeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.get_mob:
                time.start();
                RetrofitManager.getInstance().getVerificationCode(mUserPhoneEd.getText().toString(), new RetrofitManager.ICallBack() {
                    @Override
                    public void successData(String result) {
                        Gson gson2 = new Gson();
                        VerificationResult c = gson2.fromJson(result, VerificationResult.class);
                        if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                            ToastUtils.show(LoginActivity.this, c.getMsg());
                        } else {
                            ToastUtils.show(LoginActivity.this, c.getMsg());
                        }
                    }

                    @Override
                    public void failureData(String error) {
                        //Log.e("AA", "error: " + error + "错");
                        mSendVcCode.setText(getString(R.string.getVcode));
                        mSendVcCode.setClickable(true);
                        time.onFinish();
                    }
                });
                break;
            case R.id.btn_login://登陆  请求后台
                /*if (TextUtils.isEmpty(MethodUtils.getDeviceId()) && !Const.IS_DEBUG) {
                    ToastUtils.show(this, getString(R.string.tips5));
                    return;
                }*/
                showProgrssDialog();
                if (mVcCodeEd.getVisibility() == View.VISIBLE) {
                    RetrofitManager.getInstance().loginByPassword(user_phone.getText().toString(),
                            user_password.getText().toString(),
                            DEVICEID, new RetrofitManager.ICallBack() {
                                @Override
                                public void successData(String result) {
                                   // customDialog.dismiss();
                                    Gson gson2 = new Gson();
                                    LoginResult c = gson2.fromJson(result, LoginResult.class);
                                    Log.e("A", "onResponse: " + result + "返回值");
                                    if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                        HvApplication.TOKEN = c.getToken();
                                        SharedPreferencesUtils.saveLoginStatesSharePrefer(LoginActivity.this, SharedPreferencesUtils.LOGIN);
                                        SharedPreferencesUtils.saveStringSharePrefer(LoginActivity.this, SharedPreferencesUtils.TOKEN, c.getToken());
                                        Log.e("AA", "onResponse: " + result + "返回值");
                                        MethodUtils.hideSoftInput(LoginActivity.this);
                                        Intent intent=new Intent(LoginActivity.this, MeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        customDialog.dismiss();
                                        ToastUtils.showLong(LoginActivity.this, c.getMsg());
                                    }
                                }

                                @Override
                                public void failureData(String error) {
                                    Log.e("AA", "error: " + error + "错");
                                    customDialog.dismiss();
                                }
                            });
                } else {
                    RetrofitManager.getInstance().loginBySMS(mUserPhoneEd.getText().toString(),
                            mVcCodeEd.getText().toString(),
                            DEVICEID, new RetrofitManager.ICallBack() {
                                @Override
                                public void successData(String result) {
                                    //customDialog.dismiss();
                                    Gson gson2 = new Gson();
                                    LoginResult c = gson2.fromJson(result, LoginResult.class);
                                    Log.e("A", "onResponse: " + result + "返回值");
                                    if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                        HvApplication.TOKEN = c.getToken();
                                        SharedPreferencesUtils.saveLoginStatesSharePrefer(LoginActivity.this, SharedPreferencesUtils.LOGIN);
                                        SharedPreferencesUtils.saveStringSharePrefer(LoginActivity.this, SharedPreferencesUtils.TOKEN, c.getToken());
                                        Log.e("AA", "onResponse: " + result + "返回值");
                                        MethodUtils.hideSoftInput(LoginActivity.this);
                                        Intent intent=new Intent(LoginActivity.this, MeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        customDialog.dismiss();
                                        ToastUtils.showLong(LoginActivity.this, c.getMsg());
                                    }
                                }

                                @Override
                                public void failureData(String error) {
                                    Log.e("AA", "error: " + error + "错");
                                    customDialog.dismiss();
                                }
                            });
                }
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
            case R.id.findpasswordBtn:
                Intent intent2 = new Intent(this, RegisterActivity.class);
                intent2.putExtra("from", "findPass");
                startActivity(intent2);
                break;
        }
    }
    CustomDialog customDialog;
    private void showProgrssDialog() {
        customDialog = new CustomDialog(this);
        customDialog.setMessage(getResources().getString(R.string.loading));
        customDialog.setCancel("取消", new CustomDialog.IOnCancelListener() {
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
    private TimeCount time;
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //get_mob.setBackgroundColor(Color.parseColor("#B6B6D8"));
            mSendVcCode.setClickable(false);
            mSendVcCode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            mSendVcCode.setText("重新获取验证码");
            mSendVcCode.setClickable(true);
            //get_mob.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }
}

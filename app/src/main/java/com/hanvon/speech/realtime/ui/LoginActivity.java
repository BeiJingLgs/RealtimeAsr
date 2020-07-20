package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.asr.ai.speech.realtime.Const;
import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.DialogUtil;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.ToastUtils;

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
        mTime = new TimeCount(60000, 1000);
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
                new Handler().postDelayed(() -> {finish();}, 800);
                break;
            case R.id.vc_code_tv:
                mPassLayout.setVisibility(View.GONE);
                mFindPassTv.setVisibility(View.GONE);
                mVcCodeTv.setVisibility(View.GONE);
                mVcCodeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.get_mob:
                mTime.start();
                RetrofitManager.getInstance(this).getVerificationCode(mUserPhoneEd.getText().toString(), new RetrofitManager.ICallBack() {
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
                        mTime.onFinish();
                    }
                });
                break;
            case R.id.btn_login://登陆  请求后台
                String id = "";
                if (HvApplication.ISDEBUG) {
                    id = DEVICEID;
                } else {
                    if ((TextUtils.isEmpty(MethodUtils.getDeviceId()) || TextUtils.equals("unavailable", MethodUtils.getDeviceId())) && !Const.IS_DEBUG) {
                        ToastUtils.show(this, getString(R.string.tips5));
                        return;
                    }
                    id = MethodUtils.getDeviceId();
                }

                if (mPassLayout.getVisibility() == View.VISIBLE) {
                    if (TextUtils.isEmpty(user_phone.getText().toString()) ||
                            TextUtils.isEmpty(user_password.getText().toString())) {
                        ToastUtils.show(this, getString(R.string.nonullPass));
                        return;
                    }
                    if (user_phone.getText().toString().length()!=11) {
                        ToastUtils.show(this, getString(R.string.noPhone));
                        return;
                    }
                    DialogUtil.getInstance().showProgressDialog(LoginActivity.this);
                    RetrofitManager.getInstance(this).loginByPassword(user_phone.getText().toString(),
                            user_password.getText().toString(),
                            id, new RetrofitManager.ICallBack() {
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
                                        mTime.cancel();
                                        mSendVcCode.setText(getString(R.string.getVcode));
                                        mSendVcCode.setClickable(true);
                                        DialogUtil.getInstance().disProgressDialog();
                                        ToastUtils.showLong(LoginActivity.this, c.getMsg());
                                    }
                                }

                                @Override
                                public void failureData(String error) {
                                    Log.e("AA", "error: " + error + "错");
                                    DialogUtil.getInstance().disProgressDialog();
                                }
                            });
                } else {
                    if (TextUtils.isEmpty(mUserPhoneEd.getText().toString())) {
                        ToastUtils.show(this, getString(R.string.noPhone2));
                        return;
                    }
                    DialogUtil.getInstance().showProgressDialog(LoginActivity.this);
                    RetrofitManager.getInstance(this).loginBySMS(mUserPhoneEd.getText().toString(),
                            mVcCodeEd.getText().toString(),
                            id, new RetrofitManager.ICallBack() {
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
                                        DialogUtil.getInstance().disProgressDialog();
                                        ToastUtils.showLong(LoginActivity.this, c.getMsg());
                                    }
                                }

                                @Override
                                public void failureData(String error) {
                                    Log.e("AA", "error: " + error + "错");
                                    DialogUtil.getInstance().disProgressDialog();
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


    private TimeCount mTime;
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mSendVcCode.setClickable(false);
            mSendVcCode.setText("("+millisUntilFinished / 1000 +") " + getString(R.string.reSendCode));
        }

        @Override
        public void onFinish() {
            mSendVcCode.setText(getString(R.string.getVcode));
            mSendVcCode.setClickable(true);
        }
    }
}

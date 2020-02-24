package com.hanvon.speech.realtime.ui;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.bean.Result.PayList;
import com.hanvon.speech.realtime.bean.Result.ShopTypeList;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.MethodUtils;

public class MeActivity extends BaseActivity {

    private TextView login_or_register, mLastTimeTv, mShopListTv, mBindDevicesTv, mUpdateTv, mUsageRecordTv;
    private Button mLogOutBtn;

    @Override
    int provideContentViewId() {
        return R.layout.me_activity;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mHomeBtn.setVisibility(View.GONE);
        mMenus.setVisibility(View.GONE);
        login_or_register = findViewById(R.id.login_or_register);
        mLastTimeTv = findViewById(R.id.last_time);
        mShopListTv = findViewById(R.id.shop_list);
        mBindDevicesTv = findViewById(R.id.bind_deviceList);
        mUpdateTv = findViewById(R.id.update_check);
        mLogOutBtn = findViewById(R.id.btn_logout);
        mUsageRecordTv = findViewById(R.id.usage_record);
        login_or_register.setOnClickListener(this);
        mLastTimeTv.setOnClickListener(this);
        mShopListTv.setOnClickListener(this);
        mBindDevicesTv.setOnClickListener(this);
        mUpdateTv.setOnClickListener(this);
        mLogOutBtn.setOnClickListener(this);
        mUsageRecordTv.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (TextUtils.isEmpty(HvApplication.TOKEN)) {
            mLogOutBtn.setVisibility(View.GONE);
        } else {
            mLogOutBtn.setVisibility(View.VISIBLE);
            login_or_register.setText(getResources().getString(R.string.hasLogined));
        }*/
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.login_or_register:
                 Intent intent=new Intent(this,LoginActivity.class);
                 startActivity(intent);
                 break;
             case R.id.btn_Home:
                 new MethodUtils(this).getHome();
                 break;
             case R.id.btn_Return:
                 finish();
                 break;
             case R.id.last_time:
                 RetrofitManager.getInstance().getPacks(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {

                         Gson gson2 = new Gson();
                         ShopTypeList c = gson2.fromJson(result, ShopTypeList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getShopType().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {

                         } else {

                         }
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");

                     }
                 });
                 break;
             case R.id.shop_list:
                 RetrofitManager.getInstance().getPayChannels(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Gson gson2 = new Gson();
                         PayList c = gson2.fromJson(result, PayList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPayType().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {

                         } else {

                         }
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");

                     }
                 });
                 break;
             case R.id.bind_deviceList:
                 RetrofitManager.getInstance().getUserPacks(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Log.e("AA", "onResponse: " + result + "返回值");
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error);

                     }
                 });
                 break;
             case R.id.update_check:
                 RetrofitManager.getInstance().bindDevices(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Log.e("AA", "onResponse: " + result + "返回值");
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");

                     }
                 });
                 break;
             case R.id.btn_logout:
                 RetrofitManager.getInstance().logout(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Log.e("AA", "onResponse: " + result + "返回值");
                         login_or_register.setText("未登陆");
                         mLogOutBtn.setVisibility(View.GONE);
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");

                     }
                 });
                 break;
             case R.id.usage_record:
                 RetrofitManager.getInstance().getOrders(0 + "", 5 + "", "asc",new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Log.e("AA2", "onResponse: " + result + "返回值");

                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");

                     }
                 });
                 break;
         }
    }
}

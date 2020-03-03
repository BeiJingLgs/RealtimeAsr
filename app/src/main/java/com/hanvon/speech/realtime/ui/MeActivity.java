package com.hanvon.speech.realtime.ui;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.DeviceBeanList;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.bean.Result.OrderList;
import com.hanvon.speech.realtime.bean.Result.PackList;
import com.hanvon.speech.realtime.bean.Result.PayList;
import com.hanvon.speech.realtime.bean.Result.ShopTypeList;
import com.hanvon.speech.realtime.bean.Result.UsageBeenList;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.ToastUtils;

import java.util.HashMap;

public class MeActivity extends BaseActivity {

    private TextView login_or_register, mLastTimeTv, mShopListTv, mBindDevicesTv, mUpdateTv, mUsageRecordTv, mUsagePackTv;
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
        mUsagePackTv = findViewById(R.id.usage_pack);
        login_or_register.setOnClickListener(this);
        mLastTimeTv.setOnClickListener(this);
        mShopListTv.setOnClickListener(this);
        mBindDevicesTv.setOnClickListener(this);
        mUpdateTv.setOnClickListener(this);
        mLogOutBtn.setOnClickListener(this);
        mUsageRecordTv.setOnClickListener(this);
        mUsagePackTv.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(HvApplication.TOKEN)) {
            mLogOutBtn.setVisibility(View.GONE);
        } else {
            mLogOutBtn.setVisibility(View.VISIBLE);
            login_or_register.setText(getResources().getString(R.string.hasLogined));
        }
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
                 RetrofitManager.getInstance().getDevicePacks(HvApplication.TOKEN, new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Gson gson2 = new Gson();
                         PackList c = gson2.fromJson(result, PackList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                             TranslateBean.getInstance().setPackList(c.getPackBean());
                             Intent intent = new Intent(MeActivity.this, PurchaseActivity.class);
                             intent.putExtra("type", "PackBeen");
                             startActivity(intent);
                         } else {
                             ToastUtils.showLong(MeActivity.this, c.getMsg());
                         }
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error);

                     }
                 });
                 break;
             case R.id.shop_list:
                 RetrofitManager.getInstance().getOrders(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "asc", new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Gson gson2 = new Gson();
                         OrderList c = gson2.fromJson(result, OrderList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getOrder().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                             TranslateBean.getInstance().setOrderList(c.getOrder());
                             Intent intent = new Intent(MeActivity.this, PurchaseActivity.class);
                             intent.putExtra("type", "OrderBeen");
                             startActivity(intent);
                         } else {
                             ToastUtils.showLong(MeActivity.this, c.getMsg());
                         }
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");
                     }
                 });
                 break;
             case R.id.bind_deviceList:
                 RetrofitManager.getInstance().getBindDevices(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Gson gson2 = new Gson();
                         DeviceBeanList c = gson2.fromJson(result, DeviceBeanList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getDeviceBean().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                             TranslateBean.getInstance().setDeviceList(c.getDeviceBean());
                             Intent intent = new Intent(MeActivity.this, PurchaseActivity.class);
                             intent.putExtra("type", "OrderBeen");
                             startActivity(intent);
                         } else {
                             ToastUtils.showLong(MeActivity.this, c.getMsg());
                         }
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error);

                     }
                 });

                 break;
             case R.id.update_check:
                 break;
             case R.id.btn_logout:
                 RetrofitManager.getInstance().logout(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Log.e("AA", "onResponse: " + result + "返回值");
                         SharedPreferencesUtils.clear(MeActivity.this, SharedPreferencesUtils.TOKEN);
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
                 RetrofitManager.getInstance().getUseRecord(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "asc",new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Gson gson2 = new Gson();
                         UsageBeenList c = gson2.fromJson(result, UsageBeenList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getUsageBeen().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                             TranslateBean.getInstance().setUsageList(c.getUsageBeen());
                             Intent intent = new Intent(MeActivity.this, PurchaseActivity.class);
                             intent.putExtra("type", "UsageBeen");
                             startActivity(intent);
                         } else {
                             ToastUtils.showLong(MeActivity.this, c.getMsg());
                         }
                     }
                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");
                     }
                 });
                 break;
             case R.id.usage_pack:
                 RetrofitManager.getInstance().getPacks(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {

                         Gson gson2 = new Gson();
                         ShopTypeList c = gson2.fromJson(result, ShopTypeList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getShopType().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                             TranslateBean.getInstance().setShopTypes(c.getShopType());
                             Intent intent = new Intent(MeActivity.this, PurchaseActivity.class);
                             intent.putExtra("type", "ShopType");
                             startActivity(intent);
                         } else {
                             ToastUtils.showLong(MeActivity.this, c.getMsg());
                         }
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

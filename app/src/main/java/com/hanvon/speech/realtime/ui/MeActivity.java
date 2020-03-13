package com.hanvon.speech.realtime.ui;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ai.speech.realtime.Const;
import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
        mMenus.setVisibility(View.GONE);
        login_or_register = findViewById(R.id.login_or_register);
        mLastTimeTv = findViewById(R.id.last_time);
        mShopListTv = findViewById(R.id.shop_list);
        mBindDevicesTv = findViewById(R.id.bind_deviceList);
        mBindDevicesTv.setVisibility(View.GONE);
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
        init();
        initStates();
    }

    private void init() {
        SpannableString spanStr = new SpannableString(mUsagePackTv.getText().toString());
        spanStr.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 8, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new RelativeSizeSpan(0.6f), 8, spanStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mUsagePackTv.setText(spanStr);
    }

    private void initStates() {
        if (TextUtils.isEmpty(MethodUtils.getDeviceId())&& !Const.IS_DEBUG) {
            ToastUtils.show(this, getString(R.string.tips5));
            return;
        }
        RetrofitManager.getInstance().getBindUser(DEVICEID, new RetrofitManager.ICallBack() {
            @Override
            public void successData(String result) {
                JSONObject json = JSONObject.parseObject(result);
                if (json.get("Model") == null) {
                    mLogOutBtn.setVisibility(View.GONE);
                    login_or_register.setText(getResources().getString(R.string.unLogined));
                } else {
                    mLogOutBtn.setVisibility(View.VISIBLE);
                    login_or_register.setText(getResources().getString(R.string.hasLogined));
                }
            }

            @Override
            public void failureData(String error) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initStates();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                 RetrofitManager.getInstance().getAccountPacks(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Gson gson2 = new Gson();
                         PackList c = gson2.fromJson(result, PackList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                             if (c.getPackBean().size() == 0) {
                                 ToastUtils.showLong(MeActivity.this, "当前已经没有可用时长");
                             } else {
                                 TranslateBean.getInstance().setPackList(c.getPackBean());
                                 Intent intent = new Intent(MeActivity.this, PurchaseActivity.class);
                                 intent.putExtra("type", "PackBeen");
                                 startActivity(intent);
                             }

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
                 RetrofitManager.getInstance().getOrders(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         JSONObject json = JSONObject.parseObject(result);
                         if (!TextUtils.equals((String)json.get("Code"), Constant.SUCCESSCODE)) {
                             ToastUtils.showLong(MeActivity.this, (String)json.get("Msg"));
                             return;
                         }
                         Gson gson = new GsonBuilder().serializeNulls().create();
                         OrderList c = gson.fromJson(result, OrderList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size()==: " + c.getOrder().size());
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
                 ToastUtils.showLong(MeActivity.this, "該功能還沒上綫哦");
                 break;
             case R.id.btn_logout:
                 RetrofitManager.getInstance().logout(new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Log.e("AA", "onResponse: " + result + "返回值");
                         SharedPreferencesUtils.saveLoginStatesSharePrefer(MeActivity.this, "");
                         SharedPreferencesUtils.clear(MeActivity.this, SharedPreferencesUtils.TOKEN);
                         login_or_register.setText("未登陆");
                         HvApplication.TOKEN = "";
                         mLogOutBtn.setVisibility(View.GONE);
                     }

                     @Override
                     public void failureData(String error) {
                         Log.e("AA", "error: " + error + "错");

                     }
                 });
                 break;
             case R.id.usage_record:
                 RetrofitManager.getInstance().getUseRecord(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc",new RetrofitManager.ICallBack() {
                     @Override
                     public void successData(String result) {
                         Gson gson2 = new Gson();
                         UsageBeenList c = gson2.fromJson(result, UsageBeenList.class);
                         Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getUsageBeen().size());
                         if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                             if(c.getUsageBeen().size() == 0) {
                                 ToastUtils.showLong(MeActivity.this, "当前暂时没有使用记录");
                             } else {
                                 TranslateBean.getInstance().setUsageList(c.getUsageBeen());
                                 Intent intent = new Intent(MeActivity.this, PurchaseActivity.class);
                                 intent.putExtra("type", "UsageBeen");
                                 startActivity(intent);
                             }

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

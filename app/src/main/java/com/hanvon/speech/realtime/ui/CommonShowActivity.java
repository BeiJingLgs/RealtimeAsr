package com.hanvon.speech.realtime.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.OrderDetail;
import com.hanvon.speech.realtime.bean.Result.OrderList;
import com.hanvon.speech.realtime.bean.Result.PayResultBean;
import com.hanvon.speech.realtime.bean.Result.ShopType;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.ZXingUtils;

import java.util.HashMap;

public class CommonShowActivity extends BaseActivity {
    private TextView mPackNameTv, mPackPrice, mPackDescripe, mPackDuration, mPayBtn, mValidPeriod;
    private ImageView mVcCode;
    private ShopType mShopType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void init() {
        mShopType = TranslateBean.getInstance().getShopType();
        mPackNameTv.setText(mShopType.getName());
        mPackDuration.setText(mShopType.getDuration());
        mPackPrice.setText(String.valueOf(mShopType.getPrice()) + " 元");
        mPackDescripe.setText(mShopType.getDescribe());
        mValidPeriod.setText(mShopType.getValidPeriod());
    }

    @Override
    int provideContentViewId() {
        return R.layout.activity_common_show;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mMenus.setVisibility(View.GONE);
        mPackNameTv = findViewById(R.id.title_name);
        mPackDuration = findViewById(R.id.duration_time);
        mPackPrice = findViewById(R.id.price);
        mPackDescripe = findViewById(R.id.describe);
        mVcCode = findViewById(R.id.qr_code_img);
        mPayBtn = findViewById(R.id.purchase);
        mValidPeriod = findViewById(R.id.validPeriod_time);
        mPayBtn.setOnClickListener(this);
        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Return:
                onBackPressed();
                break;
            case R.id.btn_Home:
                new MethodUtils(this).getHome();
                break;
            case R.id.purchase:
                HashMap<String,String> map = new HashMap<>();
                map.put("packId", String.valueOf(TranslateBean.getInstance().getShopType().getID()));
                RetrofitManager.getInstance().createOrderByPack(map, new RetrofitManager.ICallBack() {
                    @Override
                    public void successData(String result) {
                        Gson gson2 = new Gson();
                        OrderDetail c = gson2.fromJson(result, OrderDetail.class);
                        if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {

                            RetrofitManager.getInstance().payOrder(String.valueOf(c.getOrderModal().getID()),
                                    Constant.WEIXIN_CODE, new RetrofitManager.ICallBack() {
                                @Override
                                public void successData(String result) {
                                    Gson gson2 = new Gson();
                                    PayResultBean c = gson2.fromJson(result, PayResultBean.class);
                                    if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                        Bitmap bitmap = ZXingUtils.createQRImage(c.getUrlBean().getUrl(),250,250);
                                        mVcCode.setImageBitmap(bitmap);
                                        mVcCode.setVisibility(View.VISIBLE);
                                    } else {
                                        ToastUtils.showLong(CommonShowActivity.this, c.getMsg());
                                    }
                                }

                                @Override
                                public void failureData(String error) {
                                    Log.e("AA", "error: " + error + "错");

                                }
                            });
                        } else {
                            ToastUtils.showLong(CommonShowActivity.this, c.getMsg());
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

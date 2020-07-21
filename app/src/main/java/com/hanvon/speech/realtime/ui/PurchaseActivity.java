package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.adapter.OrderAdapter;
import com.hanvon.speech.realtime.adapter.PackAdapter;
import com.hanvon.speech.realtime.adapter.UsagePurchaseAdapter;
import com.hanvon.speech.realtime.adapter.UsageRecordAdapter;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.Order;
import com.hanvon.speech.realtime.bean.Result.OrderList;
import com.hanvon.speech.realtime.bean.Result.PackBean;
import com.hanvon.speech.realtime.bean.Result.PackList;
import com.hanvon.speech.realtime.bean.Result.ShopType;
import com.hanvon.speech.realtime.bean.Result.UsageBeen;
import com.hanvon.speech.realtime.bean.Result.UsageBeenList;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PurchaseActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private Button mPreBtn, mNextBtn;
    private OrderAdapter mOrderAdapter;
    private PackAdapter mPackAdapter;
    private UsageRecordAdapter mUsageRecordAdapter;
    private UsagePurchaseAdapter mUsagePuraseAdapter;
    private ListView mListView;
    private List<UsageBeen> mUsageRecordList;
    private List<ShopType> mShopTypeList;
    private List<Order> mOrderList;
    private List<PackBean> mPackList;


    private static String TAG = "PurchaseActivity";
    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 8;// 每页显示几个
    private TextView mPagetTv;
    private static Logger logger = Logger.getLogger("IatListActivity");
    private String msg, intentKey = "type", intentUsageValue = "UsageBeen", intentPackValue = "PackBeen", intentOrderValue = "OrderBeen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    int provideContentViewId() {
        return R.layout.activity_purchase;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mMenus.setVisibility(View.GONE);
        mPreBtn = (Button) findViewById(R.id.ivpre_page);
        mNextBtn = (Button) findViewById(R.id.ivnext_page);
        mPagetTv = (TextView) findViewById(R.id.tvprogress);
        mPagetTv.setVisibility(View.INVISIBLE);
        mListView = (ListView) findViewById(R.id.listView);
        mPreBtn.setBackgroundResource(R.drawable.pre_page_grey);

        View emptyView = findViewById(R.id.emptyList);
        mListView.setEmptyView(emptyView);
        mNextBtn.setOnClickListener(this);
        mPreBtn.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void init() {
        mUsageRecordList = new ArrayList<>();
        mShopTypeList = new ArrayList<>();
        mOrderList = new ArrayList<>();
        mPackList = new ArrayList<>();
        Intent intent = getIntent();
        msg = intent.getStringExtra(intentKey);
        if (TextUtils.equals(msg, intentUsageValue)) {
            mUsageRecordList.clear();
            mUsageRecordList.addAll(TranslateBean.getInstance().getUsageList());
            mUsageRecordAdapter = new UsageRecordAdapter(mUsageRecordList, this);
            mListView.setAdapter(mUsageRecordAdapter);
            if (mUsageRecordList.size() < Constant.PAGE_SIZE)
                mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
        } else if (TextUtils.equals(msg, intentPackValue)) {
            mPackList.clear();
            mPackList.addAll(TranslateBean.getInstance().getPackList());
            mPackAdapter = new PackAdapter(mPackList, this);
            mListView.setAdapter(mPackAdapter);
            if (mPackList.size() < Constant.PAGE_SIZE)
                mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
        } else if (TextUtils.equals(msg, intentOrderValue)) {
            mOrderList.clear();
            mOrderList.addAll(TranslateBean.getInstance().getOrderList());
            mOrderAdapter = new OrderAdapter(mOrderList, this);
            mListView.setAdapter(mOrderAdapter);
            if (mOrderList.size() < Constant.PAGE_SIZE)
                mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
        } else {
            mShopTypeList.clear();
            mShopTypeList.addAll(TranslateBean.getInstance().getShopTypes());
            mUsagePuraseAdapter = new UsagePurchaseAdapter(mShopTypeList, this);
            mListView.setAdapter(mUsagePuraseAdapter);
            if (mShopTypeList.size() < Constant.PAGE_SIZE)
                mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Return:
                onBackPressed();
                break;
            case R.id.btn_Home:
                new MethodUtils(this).getHome();
                break;
            case R.id.ivpre_page:
                if (Constant.PAGE_INDEX == 1) {
                    ToastUtils.showLong(PurchaseActivity.this, getString(R.string.first_page));
                    mPreBtn.setBackgroundResource(R.drawable.pre_page_grey);
                    return;
                } else {
                    mNextBtn.setBackgroundResource(R.drawable.next_page);
                    Constant.PAGE_INDEX--;
                }
                if (TextUtils.equals(msg, intentUsageValue)) {
                    RetrofitManager.getInstance(this).getUseRecord(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            Gson gson2 = new Gson();
                            UsageBeenList c = gson2.fromJson(result, UsageBeenList.class);
                            Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getUsageBeen().size());
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                if(c.getUsageBeen() == null)
                                    return;
                                mUsageRecordList.clear();
                                mUsageRecordList.addAll(c.getUsageBeen());
                                mUsageRecordAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtils.showLong(PurchaseActivity.this, c.getMsg());
                            }
                        }

                        @Override
                        public void failureData(String error) {
                            Log.e("AA", "error: " + error + "错");
                        }
                    });
                } else if (TextUtils.equals(msg, intentPackValue)) {
                    RetrofitManager.getInstance(this).getAccountPacks(String.valueOf(HvApplication.Recognition_Engine), Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            Gson gson2 = new Gson();
                            PackList c = gson2.fromJson(result, PackList.class);
                            Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                if(c.getPackBean() == null)
                                    return;
                                mPackList.clear();
                                mPackList.addAll(c.getPackBean());
                                mPackAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtils.showLong(PurchaseActivity.this, c.getMsg());
                            }
                        }

                        @Override
                        public void failureData(String error) {
                            Log.e("AA", "error: " + error);

                        }
                    });
                } else if (TextUtils.equals(msg, intentOrderValue)) {
                    RetrofitManager.getInstance(this).getOrders(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            Gson gson2 = new Gson();
                            OrderList c = gson2.fromJson(result, OrderList.class);
                            Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getOrder().size());
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                if(c.getOrder() == null)
                                    return;
                                mOrderList.clear();
                                mOrderList.addAll(c.getOrder());
                                mOrderAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtils.showLong(PurchaseActivity.this, c.getMsg());
                            }
                        }

                        @Override
                        public void failureData(String error) {
                            Log.e("AA", "error: " + error + "错");
                        }
                    });
                } else {

                }
                break;
            case R.id.ivnext_page:
                if (TextUtils.equals(msg, intentUsageValue)) {
                    Log.e(TAG, "Constant.PAGE_INDEX: " + Constant.PAGE_INDEX);
                    if (mUsageRecordList.size() == Constant.PAGE_SIZE) {
                        Constant.PAGE_INDEX++;
                    } else {
                        ToastUtils.showLong(PurchaseActivity.this, getString(R.string.last_page));
                        return;
                    }
                    RetrofitManager.getInstance(this).getUseRecord(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            Gson gson2 = new Gson();
                            UsageBeenList c = gson2.fromJson(result, UsageBeenList.class);
                            Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getUsageBeen().size());
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                if (c.getUsageBeen().size() == Constant.PAGE_SIZE) {
                                    mPreBtn.setBackgroundResource(R.drawable.pre_page);
                                } else {
                                    ToastUtils.showLong(PurchaseActivity.this, getString(R.string.last_page));
                                    mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
                                    return;
                                }
                                if(c.getUsageBeen() == null)
                                    return;
                                mUsageRecordList.clear();
                                mUsageRecordList.addAll(c.getUsageBeen());
                                mUsageRecordAdapter.notifyDataSetChanged();

                            } else {
                                ToastUtils.showLong(PurchaseActivity.this, c.getMsg());
                            }
                        }

                        @Override
                        public void failureData(String error) {
                            Log.e("AA", "error: " + error + "错");
                        }
                    });
                } else if (TextUtils.equals(msg, intentPackValue)) {
                    if (mPackList.size() == Constant.PAGE_SIZE) {
                        Constant.PAGE_INDEX++;
                    } else {
                        ToastUtils.showLong(PurchaseActivity.this, getString(R.string.last_page));
                        return;
                    }
                    RetrofitManager.getInstance(this).getAccountPacks(String.valueOf(HvApplication.Recognition_Engine), Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            Gson gson2 = new Gson();
                            PackList c = gson2.fromJson(result, PackList.class);
                            Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                if (c.getPackBean().size() == Constant.PAGE_SIZE) {
                                    mPreBtn.setBackgroundResource(R.drawable.pre_page);
                                } else {
                                    ToastUtils.showLong(PurchaseActivity.this, getString(R.string.last_page));
                                    mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
                                    return;
                                }
                                if(c.getPackBean() == null)
                                    return;
                                mPackList.clear();
                                mPackList.addAll(c.getPackBean());
                                mPackAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtils.showLong(PurchaseActivity.this, c.getMsg());
                            }
                        }

                        @Override
                        public void failureData(String error) {
                            Log.e("AA", "error: " + error);

                        }
                    });
                } else if (TextUtils.equals(msg, intentOrderValue)) {
                    if (mOrderList.size() == Constant.PAGE_SIZE) {
                        Constant.PAGE_INDEX++;
                    } else {
                        ToastUtils.showLong(PurchaseActivity.this, getString(R.string.last_page));
                        return;
                    }
                    RetrofitManager.getInstance(this).getOrders(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                        @Override
                        public void successData(String result) {
                            Gson gson2 = new Gson();
                            OrderList c = gson2.fromJson(result, OrderList.class);
                            Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getOrder().size());
                            if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                if (c.getOrder().size() == Constant.PAGE_SIZE) {
                                    mPreBtn.setBackgroundResource(R.drawable.pre_page);
                                } else {
                                    ToastUtils.showLong(PurchaseActivity.this, getString(R.string.last_page));
                                    mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
                                    return;
                                }
                                if(c.getOrder() == null)
                                    return;
                                mOrderList.clear();
                                mOrderList.addAll(c.getOrder());
                                mOrderAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtils.showLong(PurchaseActivity.this, c.getMsg());
                            }
                        }

                        @Override
                        public void failureData(String error) {
                            Log.e("AA", "error: " + error + "错");
                        }
                    });
                } else {

                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Constant.PAGE_INDEX = 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        if (TextUtils.equals(msg, intentUsageValue)) {
        } else if (TextUtils.equals(msg, intentPackValue)) {
        } else if (TextUtils.equals(msg, intentOrderValue)) {
        } else {
            TranslateBean.getInstance().setShopType(mShopTypeList.get(i));
            Intent intent = new Intent(PurchaseActivity.this, CommonShowActivity.class);
            startActivity(intent);
        }
    }
}

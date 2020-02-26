package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.adapter.OrderAdapter;
import com.hanvon.speech.realtime.adapter.PackAdapter;
import com.hanvon.speech.realtime.adapter.UsagePurchaseAdapter;
import com.hanvon.speech.realtime.adapter.UsageRecordAdapter;
import com.hanvon.speech.realtime.bean.Result.ShopType;
import com.hanvon.speech.realtime.bean.Result.UsageBeen;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.util.MethodUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PurchaseActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private Button mPreBtn, mNextBtn;
    private OrderAdapter mOrderAdapter;
    private PackAdapter mPackAdapter;
    private UsageRecordAdapter mUsageRecordAdapter;
    private UsagePurchaseAdapter mUsagePuraseAdapter;
    private ListView mListView;
    private List<UsageBeen> mUsageRecordList;
    private List<ShopType> mShopTypeList;
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
        mListView = (ListView) findViewById(R.id.listView);

        View emptyView = findViewById(R.id.emptyList);
        mListView.setEmptyView(emptyView);
        mNextBtn.setOnClickListener(this);
        mPreBtn.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void init() {
        mUsageRecordList = new ArrayList<>();
        mShopTypeList = new ArrayList<>();
        Intent intent = getIntent();
        msg = intent.getStringExtra(intentKey);
        if (TextUtils.equals(msg, intentUsageValue)) {
            mUsageRecordAdapter = new UsageRecordAdapter(TranslateBean.getInstance().getUsageList(), this);
            mListView.setAdapter(mUsageRecordAdapter);
        } else if (TextUtils.equals(msg, intentPackValue)) {
            mPackAdapter = new PackAdapter(TranslateBean.getInstance().getPackList(), this);
            mListView.setAdapter(mPackAdapter);
        } else if (TextUtils.equals(msg, intentOrderValue)) {
            mOrderAdapter = new OrderAdapter(TranslateBean.getInstance().getOrderList(), this);
            mListView.setAdapter(mOrderAdapter);
        } else {
            mShopTypeList.clear();
            mShopTypeList.addAll(TranslateBean.getInstance().getShopTypes());
            mUsagePuraseAdapter = new UsagePurchaseAdapter(mShopTypeList, this);
            mListView.setAdapter(mUsagePuraseAdapter);
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
        }
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

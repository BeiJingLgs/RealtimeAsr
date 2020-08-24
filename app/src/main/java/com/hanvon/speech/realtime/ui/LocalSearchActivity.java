package com.hanvon.speech.realtime.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asr.ai.speech.realtime.R;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.ToastUtils;

import java.util.ArrayList;

public class LocalSearchActivity extends BaseActivity {

    private ArrayList<FileBean> mTotalFileList, mTempBookList, mSearchResultList;
    private EditText mSearchEd;
    private Button mSearchBtn, mPreFilePageBtn, mNextFilePageBtn;
    private TextView mClearHisTv, mPageNumTv;
    private GridView mHistoryGrid;
    private ListView mSearchListResult;
    private ImageView mClearImg;


    @Override
    int provideContentViewId() {
        return R.layout.activity_local_search;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mSearchBtn.setVisibility(View.GONE);
        mSearchEd = findViewById(R.id.search_edit);
        mSearchBtn = findViewById(R.id.search_btn);
        mClearImg = findViewById(R.id.clear_img);
        mClearHisTv = findViewById(R.id.clear_history);
        mHistoryGrid = findViewById(R.id.gridview_history);
        mSearchListResult = findViewById(R.id.search_file_list);

        mSearchBtn.setOnClickListener(this);
        mClearImg.setOnClickListener(this);
        mClearHisTv.setOnClickListener(this);
    }

    private void init() {
        mTotalFileList = (ArrayList<FileBean>) TranslateBean.getInstance().getmFileBeanList();
        mTempBookList = new ArrayList<>();
        mSearchResultList = new ArrayList<>();
    }

    private void search(String searchWord) {
        if (TextUtils.isEmpty(searchWord)) {
            ToastUtils.showLong(this, getResources().getString(R.string.content_null));
        } else {
            SharedPreferencesUtils.saveLocalSearchHistory(getApplicationContext(), SharedPreferencesUtils.LOCAL_SEARCH_HISTORY, searchWord);
            mSearchResultList.clear();
            mSearchResultList.addAll(DatabaseUtils.getInstance(this).queryLocalBookShelfByKeyFromDB(searchWord));

            if (mSearchResultList == null || mSearchResultList.size() == 0) {
                ToastUtils.showLong(getApplicationContext(), getResources().getString(R.string.no_search_result));
                return;
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Home:
                new MethodUtils(this).getHome();
                break;
            case R.id.btn_Return:
                finish();
                break;
            case R.id.search_btn:
                finish();
                break;
            case R.id.clear_img:
                finish();
                break;
            case R.id.clear_history:
                finish();
                break;
        }
    }
}

package com.hanvon.speech.realtime.ui;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asr.ai.speech.realtime.R;
import com.hanvon.speech.realtime.adapter.FileAdapter;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.util.CommonUtils;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.view.HistoryGridAdapter;

import java.util.ArrayList;

public class LocalSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ArrayList<FileBean> mTotalFileList, mTempBookList, mSearchResultList;
    private ArrayList<String>mHistoryList;
    private EditText mSearchEd;
    private Button mSearchBtn, mPreFilePageBtn, mNextFilePageBtn, mReturnBtn2;
    private TextView mClearHisTv, mPageNumTv;
    private GridView mHistoryGrid;
    private ListView mSearchListView;
    private ImageView mClearImg;
    private FileAdapter mFileAdapter;
    private HistoryGridAdapter mHistoryAdapter;
    private View mSearchHistoryView, mSearchResultView, mHeaderView;


    @Override
    int provideContentViewId() {
        return R.layout.activity_local_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void initView(Bundle savedInstanceState, View view) {
        mMineBtn.setVisibility(View.GONE);
        mMenus.setVisibility(View.GONE);
        mSearchHistoryView = findViewById(R.id.search_info);
        mSearchResultView = findViewById(R.id.search_result_layout);
        mSearchEd = findViewById(R.id.search_edit);
        mSearchBtn = findViewById(R.id.search_btn);
        mClearImg = findViewById(R.id.clear_img);
        mClearHisTv = findViewById(R.id.clear_history);
        mHistoryGrid = findViewById(R.id.gridview_history);
        mSearchListView = findViewById(R.id.search_file_list);
        mReturnBtn2 = findViewById(R.id.btnReturn2);
        mHeaderView = findViewById(R.id.activity_header);

        mPreFilePageBtn = (Button) findViewById(R.id.ivpre_page);
        mNextFilePageBtn = (Button) findViewById(R.id.ivnext_page);
        mPageNumTv = (TextView) findViewById(R.id.tvprogress);

        mSearchListView.setOnItemClickListener(this);
        mHistoryGrid.setOnItemClickListener(this);
        mSearchBtn.setOnClickListener(this);
        mClearImg.setOnClickListener(this);
        mClearHisTv.setOnClickListener(this);
        mReturnBtn2.setOnClickListener(this);

        mSearchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    mClearImg.setVisibility(View.GONE);
                    setSearchStatus(false);

                } else {
                    mClearImg.setVisibility(View.VISIBLE);
                }
            }
        });


        init();
    }

    private void init() {
        //mTotalFileList = (ArrayList<FileBean>) TranslateBean.getInstance().getmFileBeanList();
        mHistoryList = new ArrayList<>();
        mTempBookList = new ArrayList<>();
        mSearchResultList = new ArrayList<>();
        mFileAdapter = new FileAdapter(mSearchResultList, this);
        mSearchListView.setAdapter(mFileAdapter);
        mHistoryList.addAll(SharedPreferencesUtils.getLocalSearchHistory(getApplication(), SharedPreferencesUtils.LOCAL_SEARCH_HISTORY));
        mHistoryAdapter = new HistoryGridAdapter(mHistoryList, getApplication());
        mHistoryGrid.setAdapter(mHistoryAdapter);
        if (mHistoryList.size() == 0) {
            setSearchStatus(false);
            mSearchHistoryView.setVisibility(View.GONE);
        } else {
            setSearchStatus(false);
        }
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
            } else {
                setSearchStatus(true);
                mFileAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setSearchStatus(boolean isResult) {
        if (isResult) {
            mHeaderView.setVisibility(View.GONE);
            mReturnBtn2.setVisibility(View.VISIBLE);
            mSearchHistoryView.setVisibility(View.GONE);
            mSearchResultView.setVisibility(View.VISIBLE);
            CommonUtils.hideIME();
        } else {
            mSearchHistoryView.setVisibility(View.VISIBLE);
            mSearchResultView.setVisibility(View.GONE);
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
                search(mSearchEd.getText().toString().trim());
                break;
            case R.id.clear_img:
            case R.id.btnReturn2:
                mSearchEd.setText("");
                CommonUtils.showIME();
                mHeaderView.setVisibility(View.VISIBLE);
                mReturnBtn2.setVisibility(View.GONE);
                if (SharedPreferencesUtils.getLocalSearchHistory(getApplicationContext(),
                        SharedPreferencesUtils.LOCAL_SEARCH_HISTORY) == null)
                    return;
                mHistoryList.clear();
                mHistoryList.addAll(SharedPreferencesUtils.getLocalSearchHistory(getApplicationContext(),
                        SharedPreferencesUtils.LOCAL_SEARCH_HISTORY));
                mHistoryAdapter.notifyDataSetChanged();
                break;
            case R.id.clear_history:
                mSearchHistoryView.setVisibility(View.GONE);
                mHistoryList.clear();
                mHistoryAdapter.notifyDataSetChanged();
                SharedPreferencesUtils.clearAll(getApplicationContext(), SharedPreferencesUtils.LOCAL_SEARCH_HISTORY);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.gridview_history:
                mSearchEd.setText(mHistoryList.get(position));
                mSearchEd.setSelection(mHistoryList.get(position).length());
                search(mHistoryList.get(position));
                break;
            case R.id.search_file_list:
                //mSearchEd.setText(mHistoryList.get(position));
                //search(mHistoryList.get(mHistoryList.get(position)));
                break;
        }
    }
}

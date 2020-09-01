package com.hanvon.speech.realtime.ui;


import android.content.Intent;
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
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.util.CommonUtils;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.view.HistoryGridAdapter;

import java.util.ArrayList;

public class LocalSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ArrayList<FileBean> mTotalFileList, mTempFileList, mSearchResultList;
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

    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 8;// 每页显示几个

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
        mPreFilePageBtn = findViewById(R.id.ivpre_page);
        mNextFilePageBtn = findViewById(R.id.ivnext_page);
        mPageNumTv = findViewById(R.id.tvprogress);

        mSearchListView.setOnItemClickListener(this);
        mHistoryGrid.setOnItemClickListener(this);
        mSearchBtn.setOnClickListener(this);
        mClearImg.setOnClickListener(this);
        mClearHisTv.setOnClickListener(this);
        mReturnBtn2.setOnClickListener(this);
        mPreFilePageBtn.setOnClickListener(this);
        mNextFilePageBtn.setOnClickListener(this);

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
        PAGE_CATEGORY = getResources().getInteger(R.integer.item_num);
        mHistoryList = new ArrayList<>();
        mTempFileList = new ArrayList<>();
        mSearchResultList = new ArrayList<>();
        mFileAdapter = new FileAdapter(mTempFileList, this);
        mSearchListView.setAdapter(mFileAdapter);
        if (SharedPreferencesUtils.getLocalSearchHistory(getApplication(), SharedPreferencesUtils.LOCAL_SEARCH_HISTORY) != null) {
            mHistoryList.addAll(SharedPreferencesUtils.getLocalSearchHistory(getApplication(), SharedPreferencesUtils.LOCAL_SEARCH_HISTORY));
        }
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
               // mFileAdapter.setSpannable(searchWord);
                setSearchStatus(true);
                nPageCount = getTotalqlPageCount(mSearchResultList.size());
                freshFileList(nPageIsx);
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
                LogUtils.printErrorLog(TAG, "");
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
            case R.id.ivpre_page:
                if ((nPageIsx - 1) >= 0) {
                    nPageIsx--;
                    freshFileList(nPageIsx);
                }
                break;
            case R.id.ivnext_page:
                if ((nPageIsx) < (nPageCount - 1)) {
                    nPageIsx++;
                    freshFileList(nPageIsx);
                }
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
                Intent intent = new Intent(this, IatActivity.class);
                intent.putExtra("isNew", false);
                TranslateBean.getInstance().setFileBean(mTempFileList.get(position));
                startActivityForResult(intent, 11);
                break;
        }
    }

    private int getTotalqlPageCount(int size) {
        size = size % PAGE_CATEGORY == 0 ? size / PAGE_CATEGORY : size / PAGE_CATEGORY + 1;
        if (size == 0)
            return size + 1;
        else
            return size;
    }


    private void freshFileList(int currentPage) {
        if (mTempFileList == null) {
            mTempFileList = new ArrayList<FileBean>();
        } else {
            mTempFileList.clear();
        }

        for (int i = currentPage * PAGE_CATEGORY; i < mSearchResultList.size()
                && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
            mTempFileList.add(mSearchResultList.get(i));
        }

        if (mTempFileList.size() == 0) {
            if ((currentPage - 1) >= 0) {
                currentPage--;
                for (int i = currentPage * PAGE_CATEGORY; i < mTotalFileList.size()
                        && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
                    mTempFileList.add(mSearchResultList.get(i));
                }
            }
        }

        if (mFileAdapter == null) {
            mFileAdapter = new FileAdapter(mTempFileList, this);
            mSearchListView.setAdapter(mFileAdapter);
        } else {
            //Log.e("tag", "getCreatemillis: " + mTotalFileList.get(0).getCreatemillis());
            mFileAdapter.notifyDataSetChanged();
        }
        mPageNumTv.setText((currentPage + 1) + "/" + nPageCount);
        if (currentPage == 0 && nPageCount > 1) {
            mPreFilePageBtn.setBackgroundResource(R.drawable.pre_page_grey);
            mNextFilePageBtn.setBackgroundResource(R.drawable.next_page);
        } else if (currentPage == 0 && nPageCount == 1) {
            mPreFilePageBtn.setBackgroundResource(R.drawable.pre_page_grey);
            mNextFilePageBtn.setBackgroundResource(R.drawable.next_page_grey);
        } else if ((currentPage + 1) == nPageCount) {
            mPreFilePageBtn.setBackgroundResource(R.drawable.pre_page);
            mNextFilePageBtn.setBackgroundResource(R.drawable.next_page_grey);
        } else {
            mPreFilePageBtn.setBackgroundResource(R.drawable.pre_page);
            mNextFilePageBtn.setBackgroundResource(R.drawable.next_page);
        }
    }
}

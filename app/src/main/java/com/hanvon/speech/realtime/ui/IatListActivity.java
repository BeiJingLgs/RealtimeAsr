package com.hanvon.speech.realtime.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.Const;
import com.baidu.ai.speech.realtime.ConstBroadStr;
import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.baidu.ai.speech.realtime.android.LoggerUtil;
import com.baidu.ai.speech.realtime.full.util.TimeUtil;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.adapter.FileAdapter;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.FileUtils;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.hvFileCommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class IatListActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private Button  mPreBtn, mNextBtn, mSelectAllBtn, mDeleteBtn;
    public static final int READ_DIALOG_REQUEST = 11;
    private ArrayList<FileBean> mTotalFileList, mTempBookList;
    private FileAdapter mFileAdapter;
    private ListView mFileList;
    private static String TAG;
    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 8;// 每页显示几个
    private TextView mPagetTv;
    private static Logger logger = Logger.getLogger("IatListActivity");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.printErrorLog("onNewIntent", "===onCreate");
        init();
    }

    @Override
    int provideContentViewId() {
        return R.layout.activity_iat_list;
    }

    public void initView(Bundle savedInstanceState,View view) {
        mFileList = (ListView) findViewById(R.id.file_list);
        mPreBtn = (Button) findViewById(R.id.ivpre_page);
        mNextBtn = (Button) findViewById(R.id.ivnext_page);
        mPagetTv = (TextView) findViewById(R.id.tvprogress);
        mSelectAllBtn = findViewById(R.id.select_all);
        mDeleteBtn = findViewById(R.id.delete_btn);
        View emptyView = findViewById(R.id.emptyList);
        mFileList.setEmptyView(emptyView);
        mNextBtn.setOnClickListener(this);
        mPreBtn.setOnClickListener(this);
        mFileList.setOnItemClickListener(this);
        mFileList.setOnItemLongClickListener(this);
        mSelectAllBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
    }

    private void init() {
        TAG = this.getLocalClassName();
        PAGE_CATEGORY = getResources().getInteger(R.integer.item_num);
        if (mTotalFileList == null) {
            mTotalFileList = new ArrayList<FileBean>();
        }
        freshPage();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.printErrorLog("onNewIntent", "===onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.equals(SharedPreferencesUtils.getLoginStatesprefer(this, SharedPreferencesUtils.LOGIN), "login")) {
            if ((TextUtils.isEmpty(MethodUtils.getDeviceId()) || TextUtils.equals("unavailable", MethodUtils.getDeviceId())) && !Const.IS_DEBUG) {
                ToastUtils.show(this, getString(R.string.tips5));
                return;
            }
            if (!TextUtils.isEmpty(HvApplication.TOKEN))
                return;
            RetrofitManager.getInstance().loginByDeviceId(DEVICEID, new RetrofitManager.ICallBack() {
                @Override
                public void successData(String result) {
                    Gson gson2 = new Gson();
                    LoginResult c = gson2.fromJson(result, LoginResult.class);

                    if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                        Log.e("A", "onResponse: " + result + "返回值");
                        HvApplication.TOKEN = c.getToken();
                        SharedPreferencesUtils.saveLoginStatesSharePrefer(IatListActivity.this, SharedPreferencesUtils.LOGIN);
                    }
                }
                @Override
                public void failureData(String error) {
                    Log.e("AA", "error: " + error + "错");
                }
            });
        }
        LogUtils.printErrorLog("onNewIntent", "===onResume");
    }

    private void freshPage() {
        logger.info("DatabaseUtils.getInstance(this).findAll(): " + DatabaseUtils.getInstance(this).findAll().size());
        mTotalFileList.clear();
        mTotalFileList.addAll(DatabaseUtils.getInstance(this).findAll());
        nPageCount = getTotalqlPageCount(mTotalFileList.size());
        initPage();
        freshFileList(nPageIsx);
    }

    private void initPage() {
        mPreBtn.setBackgroundResource(R.drawable.pre_page_grey);
        if (nPageCount == 1) {
            mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
        }
    }

    private void freshFileList(int currentPage) {
        if (mTempBookList == null) {
            mTempBookList = new ArrayList<FileBean>();
        } else {
            mTempBookList.clear();
        }

        for (int i = currentPage * PAGE_CATEGORY; i < mTotalFileList.size()
                && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
            mTempBookList.add(mTotalFileList.get(i));
        }

        if (mTempBookList.size() == 0) {
            if ((currentPage - 1) >= 0) {
                currentPage--;
                for (int i = currentPage * PAGE_CATEGORY; i < mTotalFileList.size()
                        && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
                    mTempBookList.add(mTotalFileList.get(i));

                }
            }
        }

        if (mFileAdapter == null) {
            mFileAdapter = new FileAdapter(mTempBookList, this);
            mFileList.setAdapter(mFileAdapter);
        } else {
            //Log.e("tag", "getCreatemillis: " + mTotalFileList.get(0).getCreatemillis());
            mFileAdapter.notifyDataSetChanged();
        }
        mPagetTv.setText((currentPage + 1) + "/" + nPageCount);
        if (currentPage == 0 && nPageCount > 1) {
            mPreBtn.setBackgroundResource(R.drawable.pre_page_grey);
            mNextBtn.setBackgroundResource(R.drawable.next_page);
        } else if (currentPage == 0 && nPageCount == 1) {
            mPreBtn.setBackgroundResource(R.drawable.pre_page_grey);
            mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
        } else if ((currentPage + 1) == nPageCount) {
            mPreBtn.setBackgroundResource(R.drawable.pre_page);
            mNextBtn.setBackgroundResource(R.drawable.next_page_grey);
        } else {
            mPreBtn.setBackgroundResource(R.drawable.pre_page);
            mNextBtn.setBackgroundResource(R.drawable.next_page);
        }
    }

    private int getTotalqlPageCount(int size) {
        size = size % PAGE_CATEGORY == 0 ? size / PAGE_CATEGORY : size / PAGE_CATEGORY + 1;
        if (size == 0)
            return size + 1;
        else
            return size;
    }


    private void updateAdapter() {
        logger.info("findAll: " + DatabaseUtils.getInstance(this).findAll());
        mTotalFileList.addAll(DatabaseUtils.getInstance(this).findAll());
        if (mFileAdapter == null) {
            mFileAdapter = new FileAdapter(mTotalFileList, this);
            mFileList.setAdapter(mFileAdapter);
        } else {
            mFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Return:
                onBackPressed();
                break;
            case R.id.btn_Home:
                HvApplication.TOKEN = "";
                new MethodUtils(this).getHome();
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
            case R.id.select_all:
                if (mFileAdapter.getmSelectStates().size() != mTotalFileList.size()) {
                    mFileAdapter.checkClear();
                    for (FileBean f : mTotalFileList) {
                        mFileAdapter.addCheck(f.getCreatemillis());
                    }
                } else {
                    mFileAdapter.checkClear();
                }
                mFileAdapter.notifyDataSetChanged();
                break;
            case R.id.delete_btn:
                Set<String> mNameSet = new HashSet();
                DatabaseUtils databaseUtils1 = DatabaseUtils.getInstance(this);
                mNameSet = mFileAdapter.getmSelectStates().keySet();
                for (String s : mNameSet) {
                    databaseUtils1.deleteBymillis(s);
                    for (FileBean fileBean : mTotalFileList) {
                        if (fileBean.getCreatemillis() == s) {
                            //FileUtils.deleteDirectory(ConstBroadStr.GetAudioRootPath(this,
                            //        TextUtils.equals(fileBean.mSd,"sd") ? true : false) + s + ".pcm");
                            FileUtils.deleteDirectory(ConstBroadStr.GetAudioRootPath(this,
                                            TextUtils.equals(fileBean.mSd,"sd") ? true : false) + s);
                            break;
                        }
                    }
                }
                freshPage();
                setCheckGone();
                break;
            case R.id.btn_option_menus:
                PopupWindow popupWindow = showPopupWindow();
                Log.i("tag", "onClick: " + popupWindow.isShowing());
                if (popupWindow != null) {
                    popupWindow.setFocusable(true);
                }
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(mMenus);
                }
                break;
            default:
                break;
        }
    }

    private void newFile() {
        Intent intent = new Intent(this, IatActivity.class);
        long time = System.currentTimeMillis();
        DatabaseUtils databaseUtils = DatabaseUtils.getInstance(this);
        String title = TimeUtil.getCurrentTime();
        String create = TimeUtil.getTime(time);
        String modify = TimeUtil.getTime(time);
        String content = "";
        intent.putExtra("isNew", true);
        FileBean fileBean = new FileBean(title, content, "", create, modify, String.valueOf(System.currentTimeMillis()), "", 0, 0);
        databaseUtils.insert(fileBean);
        TranslateBean.getInstance().setFileBean(fileBean);
        startActivityForResult(intent, READ_DIALOG_REQUEST);
    }


    private PopupWindow showPopupWindow() {
        final PopupWindow popupWindow = new PopupWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.main_menu, null);
        TextView menuItem1 = view.findViewById(R.id.popup_build);
        menuItem1.setOnClickListener(view13 -> {
            if (popupWindow != null) {
                newFile();
                popupWindow.dismiss();
            }
        });
        TextView menuItem2 = view.findViewById(R.id.popup_me);
        menuItem2.setOnClickListener(view12 -> {
            if (popupWindow != null) {
                Intent intent=new Intent(this,MeActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setWidth(240);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        return popupWindow;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logger.info("onActivityResult: ");
        freshPage();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mFileAdapter.ismShowCheck()) {
            mFileAdapter.addCheck(String.valueOf(mTempBookList.get(position).createmillis));
            mFileAdapter.notifyDataSetChanged();
        } else {
            Intent intent = new Intent(this, IatActivity.class);
            intent.putExtra("isNew", false);
            TranslateBean.getInstance().setFileBean(mTotalFileList.get(position));
            startActivityForResult(intent, READ_DIALOG_REQUEST);
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        logger.info("onItemLongClick");
        mFileAdapter.setmShowCheck(true, position);
        mFileAdapter.notifyDataSetChanged();
        mSelectAllBtn.setVisibility(View.VISIBLE);
        mDeleteBtn.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mFileAdapter.ismShowCheck()) {
            setCheckGone();
        } else {
            HvApplication.TOKEN = "";
            finish();
        }
    }

    private void setCheckGone() {
        mFileAdapter.setmCheckGone();
        mSelectAllBtn.setVisibility(View.GONE);
        mDeleteBtn.setVisibility(View.GONE);
    }
}

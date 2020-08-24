package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.asr.ai.speech.realtime.ConstBroadStr;
import com.asr.ai.speech.realtime.Constants;
import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.android.HvApplication;
import com.asr.ai.speech.realtime.full.util.TimeUtil;
import com.hanvon.speech.realtime.adapter.FileAdapter;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.UpdateUtil;
import com.hanvon.speech.realtime.util.WifiUtils;
import com.hanvon.speech.realtime.util.hvFileCommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public class IatListActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private Button  mPreBtn, mNextBtn, mSelectAllBtn, mDeleteBtn, mReNameBtn;
    public static final int TOIAT_RECORD = 11, TORENAME_DIALOGACTIVITY = 12;
    private ArrayList<FileBean> mTotalFileList, mTempFileList;
    private FileAdapter mFileAdapter;
    private ListView mFileList;
    private static String TAG;
    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 8;// 每页显示几个
    private TextView mPagetTv;
    private FileBean mFileTitle;
    private static Logger logger = Logger.getLogger("IatListActivity");
    private boolean IS_FIRST = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HvApplication.IS_NEEDIALOG = true;
        LogUtils.printErrorLog("onNewIntent", "===onCreate");
        init();
    }

    @Override
    int provideContentViewId() {
        return R.layout.activity_iat_list;
    }

    public void initView(Bundle savedInstanceState,View view) {
        mMenus.setVisibility(View.GONE);
        mCreateFile.setVisibility(View.VISIBLE);
        mMineBtn.setVisibility(View.GONE);
        mFileList = (ListView) findViewById(R.id.file_list);
        mPreBtn = (Button) findViewById(R.id.ivpre_page);
        mNextBtn = (Button) findViewById(R.id.ivnext_page);
        mPagetTv = (TextView) findViewById(R.id.tvprogress);
        mSelectAllBtn = findViewById(R.id.select_all);
        mDeleteBtn = findViewById(R.id.delete_btn);
        mReNameBtn = findViewById(R.id.rename_btn);
        mSearchBtn.setVisibility(View.GONE);
        View emptyView = findViewById(R.id.emptyList);
        mFileList.setEmptyView(emptyView);
        mReNameBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mPreBtn.setOnClickListener(this);
        mFileList.setOnItemClickListener(this);
        mFileList.setOnItemLongClickListener(this);
        mSelectAllBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
    }

    private void init() {
        IS_FIRST = true;
        TAG = this.getLocalClassName();
        PAGE_CATEGORY = getResources().getInteger(R.integer.item_num);
        if (mTotalFileList == null) {
            mTotalFileList = new ArrayList<FileBean>();
        }

        Display display = getWindowManager().getDefaultDisplay();
        Constants.WIDTH = display.getWidth();
        Constants.HEIGHT = display.getHeight();


        freshPage();
    }

    @Override
    public void onWindowFocusChanged(boolean b) {
        super.onWindowFocusChanged(b);
        if (IS_FIRST) {
            if (WifiUtils.isWifiConnected(this) && WifiUtils.isNetWorkConneted(this)) {
                LogUtils.printLog("update_check", "UpdateUtil");
                IS_FIRST = false;
                UpdateUtil updateUtil = new UpdateUtil(this, false);
                UpdateUtil.CheckApkTask checkApkTask = updateUtil.new CheckApkTask();
                checkApkTask.execute();
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.printErrorLog("onNewIntent", "===onNewIntent: " + HvApplication.TOKEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.printErrorLog("onNewIntent", "===onStop");
        //
    }

    private void freshPage() {
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
        if (mTempFileList == null) {
            mTempFileList = new ArrayList<FileBean>();
        } else {
            mTempFileList.clear();
        }

        for (int i = currentPage * PAGE_CATEGORY; i < mTotalFileList.size()
                && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
            mTempFileList.add(mTotalFileList.get(i));
        }

        if (mTempFileList.size() == 0) {
            if ((currentPage - 1) >= 0) {
                currentPage--;
                for (int i = currentPage * PAGE_CATEGORY; i < mTotalFileList.size()
                        && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
                    mTempFileList.add(mTotalFileList.get(i));
                }
            }
        }

        if (mFileAdapter == null) {
            mFileAdapter = new FileAdapter(mTempFileList, this);
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
        Intent intent = null;
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
                            deleteFileTxt(fileBean.title);
                            hvFileCommonUtils.recursiveDeleteAll(this, ConstBroadStr.GetAudioRootPath(this,
                                    TextUtils.equals(fileBean.mSd,"sd") ? true : false) + s);
                            //FileUtils.deleteDirectory(ConstBroadStr.GetAudioRootPath(this,
                            //               TextUtils.equals(fileBean.mSd,"sd") ? true : false) + s);
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
            case R.id.btn_option_create:
                newFile();
                break;
            case R.id.btn_option_mine:
                intent = new Intent(this, MeActivity.class);
                startActivity(intent);
                break;
            case R.id.rename_btn:
                if (mFileAdapter.getmSelectStates().size() == 0) {
                    ToastUtils.showLong(this, "请选择重命名文件");
                    return;
                }
                if (mFileAdapter.getmSelectStates().size() > 1) {
                    ToastUtils.showLong(this, "重命名只支持一个文件，请重新选择");
                    return;
                }
                intent = new Intent(this, RenameActivity.class);
                TranslateBean.getInstance().setFileBean(mFileTitle);
                startActivityForResult(intent, TORENAME_DIALOGACTIVITY);
                break;
            case R.id.btn_option_search:
                intent = new Intent(this, LocalSearchActivity.class);
                TranslateBean.getInstance().setmFileBeanList(mTotalFileList);
                startActivity(intent);
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
        FileBean fileBean = new FileBean(title, content, "", create, modify, String.valueOf(System.currentTimeMillis()), "", 0, 0, HvApplication.Recognition_Engine);
        databaseUtils.insert(fileBean);
        TranslateBean.getInstance().setFileBean(fileBean);
        startActivityForResult(intent, TOIAT_RECORD);
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
        LogUtils.printErrorLog(TAG, "onActivityResult  requestCode: " + requestCode);
        if (requestCode == TOIAT_RECORD) {
            freshPage();
        } else if (requestCode == TORENAME_DIALOGACTIVITY) {
            setCheckGone();
            freshPage();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mFileAdapter.ismShowCheck()) {
            mFileAdapter.addCheck(String.valueOf(mTempFileList.get(position).createmillis));
            mFileAdapter.notifyDataSetChanged();

            if (mFileAdapter.getmSelectStates().size() == 1) {
                //mReNameBtn.setEnabled(true);
                Set<String> nameset= mFileAdapter.getmSelectStates().keySet();
                for (FileBean bean : mTotalFileList) {
                    if (nameset.contains(String.valueOf(bean.createmillis))) {
                        mFileTitle = bean;
                        break;
                    }
                }
                //mReNameBtn.setTextColor(R.color.black);
                LogUtils.printErrorLog(TAG, "mReNameBtn.setEnabled(true)");
            } else {
                LogUtils.printErrorLog(TAG, "mReNameBtn.setEnabled(false)");
                //mReNameBtn.setEnabled(false);
                //mReNameBtn.setTextColor(R.color.gray);
            }
        } else {
            Intent intent = new Intent(this, IatActivity.class);
            intent.putExtra("isNew", false);
            TranslateBean.getInstance().setFileBean(mTempFileList.get(position));
            startActivityForResult(intent, TOIAT_RECORD);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mFileAdapter.setmShowCheck(true, position);
        mFileAdapter.notifyDataSetChanged();
        mSelectAllBtn.setVisibility(View.VISIBLE);
        mDeleteBtn.setVisibility(View.VISIBLE);
        mReNameBtn.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onBackPressed() {
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
        mReNameBtn.setVisibility(View.GONE);
    }

    private void deleteFileTxt(String title) {
        String path = ConstBroadStr.ROOT_PATH + getResources().getString(R.string.recog_recordtxt);
        String srcTmpFilePath = path + title.replace(" ", "_").replace(":", "_") + ".txt";
       // LogUtils.printErrorLog(TAG, "srcTmpFilePath: " + srcTmpFilePath);
        hvFileCommonUtils.recursiveDeleteAll(this, srcTmpFilePath);
    }
}

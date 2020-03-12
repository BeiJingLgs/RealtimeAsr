package com.hanvon.speech.realtime.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.baidu.ai.speech.realtime.ConstBroadStr;
import com.baidu.ai.speech.realtime.Constants;
import com.baidu.ai.speech.realtime.MiniMain;
import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.baidu.ai.speech.realtime.android.MyMicrophoneInputStream;
import com.baidu.ai.speech.realtime.full.connection.Runner;
import com.baidu.ai.speech.realtime.full.download.Result;
import com.baidu.ai.speech.realtime.full.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanvon.speech.realtime.adapter.SequenceAdapter;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.PackList;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.IatResults;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.FileUtils;
import com.hanvon.speech.realtime.util.MediaPlayerManager;
import com.hanvon.speech.realtime.util.MediaRecorderManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.hvFileCommonUtils;
import com.hanvon.speech.realtime.view.HVTextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


import static com.baidu.ai.speech.realtime.full.connection.Runner.MODE_REAL_TIME_STREAM;

public class IatActivity extends BaseActivity {

    // ============== 以下参数请勿修改 ================

    private volatile boolean isRunning = false;
    private InputStream is = null;

    private volatile MiniMain miniRunner;
    private volatile Runner fullRunner;


    private static Logger logger = Logger.getLogger("IatActivity");
    protected static int mode;
    static {
        mode = MODE_REAL_TIME_STREAM;
    }

    private SequenceAdapter mSequenceAdapter;
    private Button mTextBegin, mEditBtn, mAudioPlayBtn, mEditPrePageBtn, mEditNextPageBtn, mResultPreBtn, mResultNextBtn;
    private TextView mTimeTv;
    private HVTextView mRecogResultTv;
    private SeekBar mSeekBar;
    public CheckBox mCheckbox;

    private FileBean mFileBean;
    private ListView mEditListView;
    private View mEditLayout, mResultLayout;
    private boolean isNEW;

    private boolean rubberEnableFlag = false;


    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 10;// 每页显示几个
    private int TIME_DELAY = 1;
    private ArrayList<Result> mTotalResultList, mTempResultList;

    private Bitmap mBitmap;
    private Timer timer;
    private boolean isSeekbarChaning;
    private boolean isRecording = false;
    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private String mRecordFilePath, mTempPath;
    private int mAudioOffset;
    private Handler mHandler;
    private long mUseTime;
    private int duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        init();
    }

    @Override
    int provideContentViewId() {
        return R.layout.activity_main;

    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        //TODO  初始化
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back2, bfoOptions);
        mTextBegin = (Button) findViewById(R.id.text_begin);
        mTimeTv = (TextView) findViewById(R.id.time_tv);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(seekListener);
        mRecogResultTv = findViewById(R.id.iatContent_tv);
        mEditLayout = findViewById(R.id.edit_layout);
        mEditPrePageBtn = findViewById(R.id.ivpre_page);
        mEditNextPageBtn = findViewById(R.id.ivnext_page);
        mEditBtn = (Button) findViewById(R.id.text_edit);
        mEditBtn.setVisibility(View.VISIBLE);
        mEditListView = (ListView) findViewById(R.id.sentence_list);
        mAudioPlayBtn = (Button) findViewById(R.id.iat_play);
        mResultLayout = findViewById(R.id.result_layout);
        mResultPreBtn = findViewById(R.id.result_ivpre_page);
        mResultNextBtn = findViewById(R.id.result_ivnext_page);
       // mNoteView = view.findViewById(R.id.MyNoteView);
        mCheckbox = findViewById(R.id.checkbox);
        mEditBtn.setOnClickListener(this);
        mTextBegin.setOnClickListener(this);
        mAudioPlayBtn.setOnClickListener(this);
        mEditPrePageBtn.setOnClickListener(this);
        mEditNextPageBtn.setOnClickListener(this);
        mResultPreBtn.setOnClickListener(this);
        mResultNextBtn.setOnClickListener(this);

    }

    private void initData() {
        RecordReceiver recordReceiver = new RecordReceiver();
        IntentFilter mBtFilter = new IntentFilter();
        mBtFilter.addAction(ConstBroadStr.SHOW_BACKLOGO);//ConstBroadStr.UPDATERECOG
        mBtFilter.addAction(ConstBroadStr.UPDATERECOG);
        registerReceiver(recordReceiver, mBtFilter);
    }

    protected class RecordReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ConstBroadStr.SHOW_BACKLOGO)) {
                stopPlayRecord();
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.UPDATERECOG)) {
                mRecogResultTv.setText(IatResults.getResultsStr());
                freshResultPage();
                mRecogResultTv.gotoLastPage();
            }
        }
    }

    private void init() {
        mHandler = new AudioHandler(this);
        mTotalResultList = new ArrayList<>();
        mTempResultList = new ArrayList<>();
        mFileBean = TranslateBean.getInstance().getFileBean();
        duration = mFileBean.getDuration();


        if (duration == 0) {
            mSeekBar.setVisibility(View.GONE);
        } else {
            mSeekBar.setMax(duration);
        }
        isNEW = getIntent().getBooleanExtra("isNew", false);
        if (hvFileCommonUtils.hasSdcard(this)) {
            mCheckbox.setVisibility(View.VISIBLE);
        }
        if (isNEW)
            return;
        else
            mCheckbox.setVisibility(View.GONE);

        mRecordFilePath = ConstBroadStr.GetAudioRootPath(this,
                TextUtils.equals(mFileBean.getmSd(), "sd")) + mFileBean.getCreatemillis() + "/" + mFileBean.getCreatemillis() + ".amr";
        Log.e("mRecordFilePath", "mRecordFilePath: " + mRecordFilePath);
        if (TextUtils.isEmpty(mFileBean.getJson()))
            return;
        IatResults.addAllResult(new Gson().fromJson(mFileBean.getJson(), new TypeToken<ArrayList<Result>>() {
        }.getType()));
        mRecogResultTv.setText(mFileBean.getContent());
        mRecogResultTv.getPageCount();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                freshResultPage();
            }
        }, 200);

    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mAudioOffset = seekBar.getProgress();
            logger.info("===onStopTrackingTouch seekBar.mAudioOffset(): " + mAudioOffset);
            isSeekbarChaning = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            logger.info("===onStartTrackingTouch: ");
            stopPlayRecord();
            isSeekbarChaning = true;
            mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            logger.info("===onProgressChanged: " + seekBar.getProgress());
            mTimeTv.setText(getResources().getString(R.string.progress) + (100 * seekBar.getProgress()) / duration + "%");

        }
    };

    private void updateEditList() {
        for (int i = 0; i < mTempResultList.size(); i++)
            for (int j = nPageIsx * PAGE_CATEGORY; j < ((nPageIsx + 1) * PAGE_CATEGORY); j++) {
                if (TextUtils.equals(mTotalResultList.get(j).getSn(), mTempResultList.get(i).getSn())) {
                    if (TextUtils.isEmpty(mTempResultList.get(i).getResult())) {
                        mTotalResultList.remove(j);
                    }
                    break;
                }
            }
        IatResults.addAllResult(mTotalResultList);
    }

    private void onReturn() {
        if (mEditLayout.getVisibility() == View.VISIBLE) {
            mEditLayout.setVisibility(View.GONE);
            mResultLayout.setVisibility(View.VISIBLE);
            nPageIsx = 0;
            updateEditList();
            mRecogResultTv.setText(IatResults.getResultsStr());
            mRecogResultTv.getPageCount();
        } else {
            DatabaseUtils databaseUtils = DatabaseUtils.getInstance(this);
            String con = mRecogResultTv.getText() == null ? "" : mRecogResultTv.getText().toString();
            if (!TextUtils.equals(con, mFileBean.getContent())) {
                mFileBean.setContent(con);
                mFileBean.setJson(JSON.toJSONString(IatResults.getResults()));
                mFileBean.setModifytime(TimeUtil.getTime(System.currentTimeMillis()));
                mFileBean.setDuration(duration);
                databaseUtils.updataByContent(mFileBean);
            }
            exitActivity();
            finish();
        }
    }

    private void exitActivity() {
        stopPlayRecord();
        IatResults.clearResults();
        close(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Return:
                onBackPressed();
                break;
            case R.id.btn_Home:
                exitActivity();
                new MethodUtils(this).getHome();
                break;
            case R.id.text_begin:
                if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                    Toast.makeText(IatActivity.this,getResources().getString(R.string.playingAudio),Toast.LENGTH_LONG).show();
                    return;
                }
                chheckUsageTime();
                break;
            case R.id.text_edit:
                if (TextUtils.equals(mTextBegin.getText(), getResources().getString(R.string.text_end))) {
                    Toast.makeText(IatActivity.this,"正在转写音频，结束后才能编辑",Toast.LENGTH_LONG).show();
                    return;
                }
                freEditSentenceshPage();
                break;
            case R.id.iat_play:
                if (TextUtils.equals(mTextBegin.getText(), getResources().getString(R.string.text_end))) {
                    Toast.makeText(IatActivity.this,"正在录制音频，播放失败",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                    if (timer != null)
                        timer.cancel();
                    MediaPlayerManager.getInstance().stop();
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
                } else {
                    Log.e("mRecordFilePath", "mRecordFilePath: " + mRecordFilePath);
                    if (mRecordFilePath == null) {
                        ToastUtils.showLong(this, "当前文件为空");
                        return;
                    }
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_stop));
                    playRecord();
                }
                break;
            case R.id.ivpre_page:
                if ((nPageIsx - 1) >= 0) {
                    nPageIsx--;
                    freshSentenceList(nPageIsx);
                }
                break;
            case R.id.ivnext_page:
                if ((nPageIsx) < (nPageCount - 1)) {
                    nPageIsx++;
                    freshSentenceList(nPageIsx);
                }
                break;
            case R.id.result_ivpre_page:
                preResultPage();
                break;
            case R.id.result_ivnext_page:
                nextResultPage();
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

    private void stopPlayRecord() {
        if(timer != null)
            timer.cancel();
        mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
        MediaPlayerManager.getInstance().stop();
    }

    private void playRecord() {
        mSeekBar.setVisibility(View.VISIBLE);
        if(!MediaPlayerManager.getInstance().isPlaying()){
            mAudioOffset = mSeekBar.getProgress();
            Log.e("playRecord", "mAudioOffset: " + mAudioOffset);
            MediaPlayerManager.getInstance().play(mRecordFilePath, mAudioOffset, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayRecord();
                    mSeekBar.setProgress(0);
                }
            });//开始播放
            duration = MediaPlayerManager.getInstance().getDuration();//获取音乐总时间
            if (duration == 0)
                return;
            mFileBean.setDuration(duration);
            DatabaseUtils.getInstance(this).updataDurationByContent(mFileBean);
            mSeekBar.setMax(duration);//将音乐总时间设置为Seekbar的最大值
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!isSeekbarChaning) {
                        mSeekBar.setProgress(MediaPlayerManager.getInstance().getCurrentPosition());
                    }
                }
            },0,500);

        }
    }

    private void startRecord() {
        if (!isRecording) {
            String tmpName = mFileBean.getCreatemillis();
            if (isNEW) {
                if (mCheckbox.isChecked()) {
                    mFileBean.mSd = "sd";
                }
            }
            createFile(tmpName);
            isRecording = true;
            Log.e("startRecord", "before File: ");
            //File file = new File(mRecordFilePath);
            Log.e("startRecord", "mRecordFilePath: " + mRecordFilePath);
            Log.e("startRecord", "mTempPath: " + mTempPath);

            if (hvFileCommonUtils.isFileExist(mRecordFilePath)) {

                MediaRecorderManager.getInstance().start(mTempPath);
            } else {
                MediaRecorderManager.getInstance().start(mRecordFilePath);
            }
            mTimeTv.setVisibility(View.VISIBLE);
            mTimeTv.setText("");
            Toast.makeText(IatActivity.this,getResources().getString(R.string.startrecording),Toast.LENGTH_LONG).show();
        } else {
            if (isRecording) {
                Toast.makeText(IatActivity.this, getResources().getString(R.string.hasend), Toast.LENGTH_LONG).show();
            }
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mTempPath))
                        return;
                    Log.e("startRecord", "file.exists(): " + hvFileCommonUtils.isFileExist(mTempPath));
                    if (hvFileCommonUtils.isFileExist(mTempPath)) {
                        FileUtils.copyRecordFile(mRecordFilePath, mTempPath);
                    }
                }
            });
            isRecording = false;
            MediaRecorderManager.getInstance().stop();
        }
    }

    private class AudioHandler extends Handler {
        WeakReference<IatActivity> weakReference ;

        public AudioHandler(IatActivity activity ){
            weakReference  = new WeakReference<IatActivity>( activity) ;
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

        }
    }

    private void recognize() {
        new Thread(() -> {
            // IO 操作都在新线程
            try {
                if (isRunning) {
                    logger.info("点击停止");
                    runOnUiThread(() -> {
                        mTextBegin.setText(R.string.text_begin);
                        uploadUsageTime();
                    });
                    close(false);
                } else {
                    runOnUiThread(() -> {
                        mUseTime = System.currentTimeMillis();
                        mTextBegin.setText(R.string.text_end);
                        //Log.e("startRecord", "getCurrrentRecordTime(): " + getCurrrentRecordTime());
                    });
                    start();
                    pollCheckStop();

                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getClass().getSimpleName() + ":" + e.getMessage(), e);
            }
        }).start();
    }

    private void freEditSentenceshPage() {
        mEditLayout.setVisibility(View.VISIBLE);
        mResultLayout.setVisibility(View.GONE);
        mTotalResultList.clear();
        mTotalResultList.addAll(IatResults.getResults());
        nPageCount = getTotalqlPageCount(mTotalResultList.size());
        initPage();
        freshSentenceList(nPageIsx);
    }

    private long getCurrrentRecordTime() {
        return mFileBean.getTime() + System.currentTimeMillis() - mUseTime;
    }

    private void uploadUsageTime() {
        HashMap<String,String> map2 = new HashMap<>();
        long tempTime = System.currentTimeMillis() - mUseTime;
        Log.e("startRecord", "tempTime(): " + tempTime);
        mFileBean.setTime(mFileBean.getTime() + tempTime);

        DatabaseUtils.getInstance(HvApplication.getContext()).updataTime(mFileBean);

        map2.put("duration", String.valueOf(tempTime / 1000));
        RetrofitManager.getInstance().submitUsedTime(map2, new RetrofitManager.ICallBack() {
            @Override
            public void successData(String result) {
                Gson gson2 = new Gson();
                VerificationResult c = gson2.fromJson(result, VerificationResult.class);
                if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                    ToastUtils.show(IatActivity.this, c.getMsg());
                } else {
                    ToastUtils.show(IatActivity.this, c.getMsg());
                }
            }

            @Override
            public void failureData(String error) {
                Log.e("AA", "error: " + error);

            }
        });
    }


    private void chheckUsageTime() {
        RetrofitManager.getInstance().getAccountPacks(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
            @Override
            public void successData(String result) {
                Gson gson2 = new Gson();
                PackList c = gson2.fromJson(result, PackList.class);
                Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());
                if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE) && (c.getPackBean().size() > 0)) {
                    startRecord();
                    recognize();
                } else {
                    ToastUtils.showLong(IatActivity.this, "剩余时长不足，当前服务不可用，请及时购买服务包");
                }
            }

            @Override
            public void failureData(String error) {
                Log.e("AA", "error: " + error);
            }
        });
    }

    private void createFile(String name) {
        String dirPath = ConstBroadStr.GetAudioRootPath(this, mCheckbox.isChecked()) + name + "/";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.e("tag", "file.exists(): " + file.exists());
        mRecordFilePath = dirPath + name + ".amr";
        if (hvFileCommonUtils.isFileExist(mRecordFilePath)) {
            mTempPath = dirPath + System.currentTimeMillis() + ".amr";
        }
    }

    private int getTotalqlPageCount(int size) {
        size = size % PAGE_CATEGORY == 0 ? size / PAGE_CATEGORY : size / PAGE_CATEGORY + 1;
        if (size == 0)
            return size + 1;
        else
            return size;
    }

    private void initPage() {
        mEditPrePageBtn.setBackgroundResource(R.drawable.pre_page_grey);
        if (nPageCount == 1) {
            mEditNextPageBtn.setBackgroundResource(R.drawable.next_page_grey);
        }
    }

    private PopupWindow showPopupWindow() {
        final PopupWindow popupWindow = new PopupWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
        TextView menuItem1 = view.findViewById(R.id.popup_savePic);
        menuItem1.setOnClickListener(view13 -> {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        });
        TextView menuItem2 = view.findViewById(R.id.popup_delete);
        menuItem2.setOnClickListener(view12 -> {
            if (popupWindow != null) {
               // mNoteView.clear(false);
                //popupWindow.dismiss();
            }
        });
        TextView menuItem3 = view.findViewById(R.id.popup_rubber);
        menuItem3.setOnClickListener(view1 -> {
            if (popupWindow != null) {
               // rubberEnableFlag = !rubberEnableFlag;
               // mNoteView.setRubberMode(rubberEnableFlag);
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


    private void freshSentenceList(int currentPage) {
        if (mTempResultList == null) {
            mTempResultList = new ArrayList<Result>();
        } else {
            mTempResultList.clear();
        }

        for (int i = currentPage * PAGE_CATEGORY; i < mTotalResultList.size()
                && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
            mTempResultList.add(mTotalResultList.get(i));
        }

        if (mTempResultList.size() == 0) {
            if ((currentPage - 1) >= 0) {
                currentPage--;
                for (int i = currentPage * PAGE_CATEGORY; i < mTotalResultList.size()
                        && i < ((currentPage + 1) * PAGE_CATEGORY); i++) {
                    mTempResultList.add(mTotalResultList.get(i));
                }
            }
        }

        if (mSequenceAdapter == null) {
            mSequenceAdapter = new SequenceAdapter(mTempResultList, this);
            mEditListView.setAdapter(mSequenceAdapter);
        } else {
            mSequenceAdapter.notifyDataSetChanged();
        }
        if (currentPage == 0 && nPageCount > 1) {
            mEditPrePageBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mEditNextPageBtn.setBackgroundResource(R.drawable.arrow_right_black);
        } else if (currentPage == 0 && nPageCount == 1) {
            mEditPrePageBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mEditNextPageBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else if ((currentPage + 1) == nPageCount) {
            mEditPrePageBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mEditNextPageBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else {
            mEditPrePageBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mEditNextPageBtn.setBackgroundResource(R.drawable.arrow_right_black);
        }
    }

    private void freshResultPage() {
        int index = mRecogResultTv.getPageIdx();
        int count = mRecogResultTv.getPageCount();
        if (index == 0 && count > 1) {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_black);
        } else if (index == 0 && count == 1) {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else if ((index + 1) == count) {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_black);
        }
    }

    private void preResultPage() {
        logger.info("preResultPage");
        int curPage = mRecogResultTv.getPageIdx();
        logger.info("getPageCount: " + mRecogResultTv.getPageCount());
        if (curPage > 0) {
            mRecogResultTv.pagerPrev();
        }
        freshResultPage();
    }

    private void nextResultPage() {
        logger.info("nextResultPage");
        int curPage = mRecogResultTv.getPageIdx();
        logger.info("getPageCount: " + mRecogResultTv.getPageCount());
        if (curPage < mRecogResultTv.getPageCount() - 1) {
            mRecogResultTv.pagerNext();
        }
        freshResultPage();
    }

    /**
     * 开始识别
     *
     * @throws IOException Assets 文件异常
     */
    private void start() throws IOException {
        logger.info("try to start " + mode);
        isRunning = true;
        if (mode == Constants.MINI_DEMO_MODE || mode == Runner.MODE_FILE_STREAM
                || mode == Runner.MODE_SIMULATE_REAL_TIME_STREAM) {
            is = getAssets().open(Constants.ASSET_PCM_FILENAME);
            // pcm 文件流
        } else if (mode == Runner.MODE_REAL_TIME_STREAM) {
            is = MyMicrophoneInputStream.getInstance();
            // 麦克风
        }
        if (mode == Constants.MINI_DEMO_MODE) {
            miniRunner = new MiniMain(is); // 精简版
            miniRunner.run();
        } else {
            fullRunner = new Runner(is, mode); // 完整版
            fullRunner.run();
        }
    }

    /**
     * 轮询检测websocket是否关闭
     */
    private void pollCheckStop() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if ((miniRunner != null && miniRunner.isClosed()) ||
                        (fullRunner != null && fullRunner.isClosed())) {
                    logger.info("switch to start 开始");
                    isRunning = false;
                    runOnUiThread(() -> {

                    });

                    if (!isRunning) {
                    }
                    cancel();
                    timer.cancel();
                    close(true);
                }
            }
        };
        timer.schedule(timerTask, 500, 500);
    }

    /**
     * 流程：关闭InputStream-> uploader 结束-> websocket 关闭-> activity 里UI及参数重置
     * 关闭inputStream
     *
     * @param isRemoveRunners 是否设置为null
     */
    private void close(boolean isRemoveRunners) {
        logger.info("try to close");
        try {
            is.close();
        } catch (IOException | RuntimeException e) {
            logger.log(Level.SEVERE, e.getClass().getSimpleName() + ":" + e.getMessage(), e);
        } finally {
            if (isRemoveRunners) {
                is = null;
                miniRunner = null;
                fullRunner = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        onReturn();
    }
}

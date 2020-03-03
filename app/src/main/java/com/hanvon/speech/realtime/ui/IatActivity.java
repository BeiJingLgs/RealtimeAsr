package com.hanvon.speech.realtime.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.NetworkInfo;
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


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.hanvon.speech.realtime.model.IatThread;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.EPDHelper;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.WifiOpenHelper;
import com.hanvon.speech.realtime.util.WifiUtils;
import com.hanvon.speech.realtime.util.hvFileCommonUtils;
import com.hanvon.speech.realtime.view.HVTextView;
import com.hanvon.speech.realtime.view.HandWriteNoteView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


import static com.baidu.ai.speech.realtime.ConstBroadStr.AUDIO_FORMAT;
import static com.baidu.ai.speech.realtime.ConstBroadStr.SAMPLE_RATE_INHZ;
import static com.baidu.ai.speech.realtime.full.connection.Runner.MODE_REAL_TIME_STREAM;
import static com.hanvon.speech.realtime.model.IatThread.EVENT_AVAILABLEE_MEMO;
import static com.hanvon.speech.realtime.model.IatThread.EVENT_PLAY_PROGRESS;
import static com.hanvon.speech.realtime.model.IatThread.EVENT_PLAY_STOP;

public class IatActivity extends BaseActivity {

    // ============== 以下参数请勿修改 ================

    private volatile boolean isRunning = false;
    private InputStream is = null;

    private volatile MiniMain miniRunner;
    private volatile Runner fullRunner;


    private static Logger logger = Logger.getLogger("IatActivity");
    protected static int mode;
    protected static long FILE_LENGTH;
    static {
        mode = MODE_REAL_TIME_STREAM;
    }


    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private SequenceAdapter mSequenceAdapter;
    private Button mTextBegin, mEditBtn, mAudioPlayBtn, mEditPrePageBtn, mEditNextPageBtn, mResultPreBtn, mResultNextBtn;
    private TextView mTimeTv;
    private HVTextView mRecogResultTv;
    private SeekBar mSeekBar;
    public CheckBox mCheckbox;

    private FileBean mFileBean;
    private ListView mEditListView;
    private LocalReceiver localReceiver;
    private View mEditLayout, mResultLayout;
    private boolean isNEW, isTips = false;

    private boolean rubberEnableFlag = false;


    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 10;// 每页显示几个
    private ArrayList<Result> mTotalResultList, mTempResultList;
    private Thread mThread = null;
    private byte[] data = null;
    private HandWriteNoteView mNoteView;
    private Bitmap mBitmap;

    /*默认数据*/
    private int mSampleRateInHZ = 16000; //采样率
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;  //位数
    private int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;   //声道
    private AudioRecord mAudioRecord;
    private int mRecorderBufferSize;
    private byte[] mAudioData;
    private boolean isRecording = false;
    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private String tmpFile;
    private int mAudioOffset;
    private AudioHandler mHandler;
    private long mUseTime;
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
        EPDHelper.getInstance().setWindowRefreshMode(getWindow(), EPDHelper.Mode.GU16_RECT);
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
        mNoteView = view.findViewById(R.id.MyNoteView);
        mCheckbox = findViewById(R.id.checkbox);
        mEditBtn.setOnClickListener(this);
        mTextBegin.setOnClickListener(this);
        mAudioPlayBtn.setOnClickListener(this);
        mEditPrePageBtn.setOnClickListener(this);
        mEditNextPageBtn.setOnClickListener(this);
        mResultPreBtn.setOnClickListener(this);
        mResultNextBtn.setOnClickListener(this);
        mNoteView.setZOrderOnTop(true);
        mNoteView.setReflushDrityEnable(true);
        mNoteView.setRubberMode(rubberEnableFlag);
        mNoteView.setBackground(mBitmap);
        //mSeekBar.setElevation();

    }

    private void initData() {
        mRecorderBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHZ, mChannelConfig, mAudioFormat);
        mAudioData = new byte[320];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, mSampleRateInHZ, mChannelConfig, mAudioFormat, mRecorderBufferSize);
    }

    private void init() {
        mHandler = new AudioHandler(this);
        mTotalResultList = new ArrayList<>();
        mTempResultList = new ArrayList<>();
        localReceiver = new LocalReceiver();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(localReceiver, new IntentFilter(ConstBroadStr.UPDATERECOG));
        mFileBean = TranslateBean.getInstance().getFileBean();
        isNEW = getIntent().getBooleanExtra("isNew", false);
        if (hvFileCommonUtils.hasSdcard(this)) {
            mCheckbox.setVisibility(View.VISIBLE);
        }

        if (isNEW)
            return;
        else
            mCheckbox.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mFileBean.getJson()))
            return;
        tmpFile = ConstBroadStr.GetAudioRootPath(this,false) + mFileBean.getCreatemillis() + "/" + mFileBean.getCreatemillis() + ".pcm";

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

    public class LocalReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ConstBroadStr.UPDATERECOG)) {
                mRecogResultTv.setText(IatResults.getResultsStr());
                freshResultPage();
                mRecogResultTv.gotoLastPage();
            }
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
            if ( weakReference.get() != null ){
                if (message.what == IatThread.EVENT_PLAY_OVER) {
                    //audioStop();
                    Log.e("mHandler", "pro: EVENT_PLAY_OVER");
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
                    mSeekBar.setProgress(0);
                    mAudioOffset = 0;
                } else if (message.what == EVENT_PLAY_PROGRESS) {
                    mAudioOffset = message.arg1;
                    Log.e("mHandler", "pro: " + mAudioOffset +
                            "  FILE_LENGTH: " + FILE_LENGTH +
                            "  pro * 100: " + mAudioOffset * 100 +
                            "    (int)(pro * 100) / FILE_LENGTH: " + (int)(mAudioOffset * 100) / FILE_LENGTH);
                    mSeekBar.setProgress((int)((mAudioOffset * 100) / FILE_LENGTH));
                    if ((int)(mAudioOffset * 100) / FILE_LENGTH >= 100) {
                        mSeekBar.setProgress(0);
                        mAudioOffset = 0;
                    }

                } else if (EVENT_AVAILABLEE_MEMO == message.what) {
                    Toast.makeText(IatActivity.this,getResources().getString(R.string.insufficientmemory),Toast.LENGTH_LONG).show();
                } else if (EVENT_PLAY_STOP == message.what) {
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));

                }
            }
        }
    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            logger.info("===onStopTrackingTouch: ");

            //data = Recordutil.getPCMData(tmpFile);
            logger.info("===seekBar.getProgress(): " + seekBar.getProgress());
            mAudioOffset = (int) (seekBar.getProgress() /100.0 * FILE_LENGTH);
            if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
               // audioPlay();
                playInModeStream(tmpFile);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            logger.info("===onStartTrackingTouch: ");
          //audioStop();
            mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            logger.info("===onProgressChanged: ");

            mTimeTv.setText(getResources().getString(R.string.progress) + progress + "%");
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
                databaseUtils.updataByContent(mFileBean);
            }
            exitActivity();
            finish();
        }
    }

    private void exitActivity() {
        IatResults.clearResults();
       // audioStop();
        close(false);
        mAudioRecord.stop();
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
                if (WifiUtils.getWifiConnectState(this) == NetworkInfo.State.DISCONNECTED) {
                    Toast.makeText(IatActivity.this,getResources().getString(R.string.checkNet),Toast.LENGTH_LONG).show();
                    WifiOpenHelper wifi = new WifiOpenHelper(this);
                    wifi.openWifi();
                    this.startActivity(new Intent(
                            android.provider.Settings.ACTION_WIFI_SETTINGS));
                    return;
                }
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

                /*if (TextUtils.equals(mTextBegin.getText(), getResources().getString(R.string.text_end))) {
                    Toast.makeText(IatActivity.this,"正在录制音频，播放失败",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
                } else {
                    Log.e("tmpFile", "tmpFile: " + tmpFile);
                    if (tmpFile == null)
                        return;
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_stop));
                    playInModeStream(tmpFile);
                }*/
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

    private void startRecord() {
        if (!isRecording) {
            startLu();
            Toast.makeText(IatActivity.this,getResources().getString(R.string.startrecording),Toast.LENGTH_LONG).show();
        } else {
            if (isRecording) {
                Toast.makeText(IatActivity.this, getResources().getString(R.string.hasend), Toast.LENGTH_LONG).show();
            }
            isRecording = false;
            mAudioRecord.stop();
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

    private void uploadUsageTime() {
        HashMap<String,String> map2 = new HashMap<>();
        mUseTime = System.currentTimeMillis() - mUseTime;
        map2.put("duration", String.valueOf(mUseTime / 100));
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
        RetrofitManager.getInstance().getDevicePacks(HvApplication.TOKEN, new RetrofitManager.ICallBack() {
            @Override
            public void successData(String result) {
                Gson gson2 = new Gson();
                PackList c = gson2.fromJson(result, PackList.class);
                Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());
                if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                    startRecord();
                    recognize();
                } else {
                    ToastUtils.showLong(IatActivity.this, c.getMsg());
                }
            }

            @Override
            public void failureData(String error) {
                Log.e("AA", "error: " + error);

            }
        });
    }


    public void startLu() {
        if (isRecording) {
            return;
        }
        String tmpName = mFileBean.getCreatemillis();
        if (mCheckbox.isChecked()) {
            mFileBean.mSd = "sd";
        }
        tmpFile = createFile(tmpName);
        isRecording = true;
        mAudioRecord.startRecording();
        Log.e("tmpFile", "startLu tmpFile: " + tmpFile);
        mExecutor.execute(() -> {
            try {
                FileOutputStream outputStream = new FileOutputStream(tmpFile, true);
                while (isRecording) {
                    /*if (hvFileCommonUtils.getAvailableInternalMemorySize() < 300 && ! isTips) {
                        Message msg = Message.obtain(mHandler, EVENT_AVAILABLEE_MEMO);
                        mHandler.sendMessage(msg);
                        isTips = true;
                    }*/
                    int readSize = mAudioRecord.read(mAudioData, 0, mAudioData.length);
                    Log.i("tag", "run: ------>" + readSize);
                    outputStream.write(mAudioData);
                }
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String createFile(String name) {
        String dirPath = ConstBroadStr.GetAudioRootPath(this, mCheckbox.isChecked()) + name + "/";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.e("tag", "file.exists(): " + file.exists());
        String filePath = dirPath + name + ".pcm";
        return filePath;
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
                mNoteView.saveBitmap(ConstBroadStr.GetAudioRootPath(this,false) + mFileBean.getCreatemillis() + "/" );
                popupWindow.dismiss();
            }
        });
        TextView menuItem2 = view.findViewById(R.id.popup_delete);
        menuItem2.setOnClickListener(view12 -> {
            if (popupWindow != null) {
                mNoteView.clear(false);
                popupWindow.dismiss();
            }
        });
        TextView menuItem3 = view.findViewById(R.id.popup_rubber);
        menuItem3.setOnClickListener(view1 -> {
            if (popupWindow != null) {
                rubberEnableFlag = !rubberEnableFlag;
                mNoteView.setRubberMode(rubberEnableFlag);
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

   /* public void audioPlay() {
        if (data == null) {
            Toast.makeText(this, "No File...", Toast.LENGTH_LONG).show();
            mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
            return;
        }
        if (mThread == null) {
            mThread = new Thread(new IatThread(data, mHandler, mAudioOffset));
            mThread.start();
        }
    }*/

    /**
     * 播放，使用stream模式
     */
    private void playInModeStream(String path) {
        /*
         * SAMPLE_RATE_INHZ 对应pcm音频的采样率
         * channelConfig 对应pcm音频的声道
         * AUDIO_FORMAT 对应pcm音频的格式
         * */
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT) * 5;
        AudioTrack audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                        .setEncoding(AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.play();

        File file = new File(path);
        FILE_LENGTH = file.length();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        byte[] tempBuffer = new byte[minBufferSize];
                        fileInputStream.skip(mAudioOffset);
                        while (fileInputStream.available() > 0 &&
                                TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {

                            int readCount = fileInputStream.read(tempBuffer);
                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                    readCount == AudioTrack.ERROR_BAD_VALUE) {
                                continue;
                            }
                            if (readCount != 0 && readCount != -1) {
                                mAudioOffset += readCount;
                                audioTrack.write(tempBuffer, 0, readCount);
                            }

                            Message msg = Message.obtain(mHandler, EVENT_PLAY_PROGRESS);
                            msg.arg1 = mAudioOffset;
                            mHandler.sendMessage(msg);
                        }
                        Message msg = null;
                        if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                             msg = Message.obtain(mHandler, IatThread.EVENT_PLAY_OVER);
                        } else {
                             msg = Message.obtain(mHandler, EVENT_PLAY_STOP);
                        }
                        mHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void audioStop() {
        if (data == null) {
            return;
        }
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }*/


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

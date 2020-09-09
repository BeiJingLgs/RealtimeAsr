package com.hanvon.speech.realtime.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hwebook.HANVONEBK;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.asr.ai.speech.realtime.ConstBroadStr;
import com.asr.ai.speech.realtime.Constants;
import com.asr.ai.speech.realtime.MiniMain;
import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.android.HvApplication;
import com.asr.ai.speech.realtime.android.MyMicrophoneInputStream;
import com.asr.ai.speech.realtime.full.connection.Runner;
import com.asr.ai.speech.realtime.full.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanvon.inputmethod.library.Native;
import com.hanvon.speech.realtime.adapter.SequenceAdapter;
import com.hanvon.speech.realtime.adapter.SpeechSequenceAdapter;
import com.hanvon.speech.realtime.alspeech.AlSpeechEngine;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.PackBean;
import com.hanvon.speech.realtime.bean.Result.PackList;
import com.hanvon.speech.realtime.bean.Result.PayResultBean;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;
import com.hanvon.speech.realtime.bean.speechBean.AsrEditSentence;
import com.hanvon.speech.realtime.bean.speechBean.SpeechResult;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.IatResults;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.model.note.NoteBaseData;
import com.hanvon.speech.realtime.model.note.TraFile;
import com.hanvon.speech.realtime.model.note.TraPage;
import com.hanvon.speech.realtime.model.note.Trace;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.CommonUtils;
import com.hanvon.speech.realtime.util.DialogUtil;
import com.hanvon.speech.realtime.util.EPDHelper;
import com.hanvon.speech.realtime.bean.FTBlock;
import com.hanvon.speech.realtime.bean.FTLine;
import com.hanvon.speech.realtime.util.FileBeanUils;
import com.hanvon.speech.realtime.util.FileUtils;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.MediaPlayerManager;
import com.hanvon.speech.realtime.util.MediaRecorderManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.model.PictureReco;
import com.hanvon.speech.realtime.bean.RecoResult;
import com.hanvon.speech.realtime.util.ShareUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
import com.hanvon.speech.realtime.util.TextViewUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.WifiUtils;
import com.hanvon.speech.realtime.util.ZXingUtils;
import com.hanvon.speech.realtime.util.hvFileCommonUtils;
import com.hanvon.speech.realtime.view.CommonDialog;
import com.hanvon.speech.realtime.view.HVTextView;
import com.hanvon.speech.realtime.view.HandWriteNoteView;
import com.hanvon.speech.realtime.view.MyRuber;
import com.hanvon.speech.realtime.view.UpLoadDialog;
import com.hanvon.speech.realtime.view.VolumeBar;
import com.xrz.Pencil;
import com.xrz.SimplePen;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


import okhttp3.RequestBody;

import static com.asr.ai.speech.realtime.ConstBroadStr.ROOT_OCR_BIN_PATH;
import static com.asr.ai.speech.realtime.Constants.BAIDU_ENGINE;
import static com.asr.ai.speech.realtime.Constants.MESSAGE_WHAT1;
import static com.asr.ai.speech.realtime.Constants.MESSAGE_WHAT2;
import static com.asr.ai.speech.realtime.Constants.MESSAGE_WHAT3;
import static com.asr.ai.speech.realtime.Constants.SPEECH_ENGINE;
import static com.asr.ai.speech.realtime.full.connection.Runner.MODE_REAL_TIME_STREAM;
import static com.hanvon.speech.realtime.util.MethodUtils.hideSoftInput;
import static com.hanvon.speech.realtime.util.MethodUtils.parseMapKey;
import static com.hanvon.speech.realtime.util.MethodUtils.parseRequestBody;

public class IatActivity extends BaseActivity implements DialogUtil.NoteChanged, CompoundButton.OnCheckedChangeListener,
        View.OnTouchListener, AdapterView.OnItemClickListener, SequenceAdapter.OnCall {

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
    private SpeechSequenceAdapter mSpeechSequenceAdapter;
    private Button mTextBegin, mEditBtn, mAudioPlayBtn, mEditPrePageBtn,
            mEditNextPageBtn, mResultPreBtn, mResultNextBtn, mNoteNextBtn, mNotePreBtn, mSuspendRecord, mPreLargeBtn, mNextLargeBtn;
    private TextView mTimeTv, mNotePageInfo, mRecognizeStatusTv, mExitUndisturbTv, mCloseLargeTv, mUnDisturpTv, mFullScreesnTv;
    private HVTextView mRecogResultTv, mLargeRecogTv;
    private SeekBar mSeekBar;
    public CheckBox mCheckbox, mNoRecogCheckbox, mReadCheckbox;
    private ImageView mRecordStatusImg, mIncreaseVolImg, mDecreaseVolImg;

    private FileBean mFileBean;
    private ListView mEditListView;
    private View mEditLayout, mResultLayout, mWriteLayout, mBottomLayout, mRecordLayout, mIatLayout, mVolumLayout, mUndisturb_layout, mViewTips, mLargeLayout, mInnerLayout, mOperationLayout;
    private boolean isNEW, isTips = false;

    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 10;// 每页显示几个
    private ArrayList<AsrEditSentence> mTotalResultList, mTempResultList;

    private ArrayList<SpeechResult> mTotalSpeechResultList, mTempSpeechResultList;
    private HandWriteNoteView mNoteView;
    // 当前page页的序号，从0开始
    protected int mNotePageIndex;
    // 打开便笺文件后是否有修改
    protected boolean isTraNoteModified = false;
    private Bitmap mBitmap;
    private Timer mTimer;
    private boolean isSeekbarChaning;
    //private boolean isRecording = false;
    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private String mRecordFilePath, mTempPath, mPath;
    private int mAudioOffset;
    private AudioHandler mHandler;
    private long mStartAsrTime, mUsageRecordTime, mStartPlayTime, mUsagePlayTime;
    private int mDuration;
    private VolumeBar mCtlVolBar;
    private AudioManager mAudioManager;
    private RecordReceiver recordReceiver;
    private boolean isRegisterTag = false;

    private int index = 0;

    private HANVONEBK hv_ebk;
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
        mSearchBtn.setVisibility(View.GONE);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back2, bfoOptions);
        mTextBegin = (Button) findViewById(R.id.text_begin);
        mTimeTv = (TextView) findViewById(R.id.time_tv);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(seekListener);
        mRecogResultTv = findViewById(R.id.iatContent_tv);
        mRecogResultTv.getPaint().setAntiAlias(false);
        mRecogResultTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        //mRecogResultTv.setOnTouchListener(this);
        mEditLayout = findViewById(R.id.edit_layout);
        mEditPrePageBtn = findViewById(R.id.ivpre_page);
        mEditNextPageBtn = findViewById(R.id.ivnext_page);
        mEditBtn = (Button) findViewById(R.id.text_edit);
        mSuspendRecord = findViewById(R.id.suspendRecord);
        mIatLayout = findViewById(R.id.iat_layout);
        mRecognizeStatusTv = findViewById(R.id.recording);
        mOperationLayout = findViewById(R.id.operation_layout);
        mUnDisturpTv = findViewById(R.id.undisturbbtn);
        mFullScreesnTv = findViewById(R.id.fullScreenbtn);
        mEditBtn.setVisibility(View.VISIBLE);
        mEditListView = (ListView) findViewById(R.id.sentence_list);
        //mEditListView.setOnItemClickListener(this);
        mAudioPlayBtn = (Button) findViewById(R.id.iat_play);
        mResultLayout = findViewById(R.id.result_layout);
        mUndisturb_layout = findViewById(R.id.undisturb_layout);
        mResultPreBtn = findViewById(R.id.result_ivpre_page);
        mResultNextBtn = findViewById(R.id.result_ivnext_page);
        mRecordLayout = view.findViewById(R.id.record_layout);
        mCheckbox = findViewById(R.id.checkbox);
        mNoRecogCheckbox = findViewById(R.id.recog_checkbox);
        mReadCheckbox = findViewById(R.id.readcheckbox);
        mReadCheckbox.setOnCheckedChangeListener(this);
        // mUnDisturbCheckox = findViewById(R.id.undisturb);
        mRecordStatusImg = findViewById(R.id.suspendImg);
        mIncreaseVolImg = findViewById(R.id.increase_volume);
        mDecreaseVolImg = findViewById(R.id.decre_volume);
        mCtlVolBar = findViewById(R.id.ctrl_vol);
        mVolumLayout = findViewById(R.id.volum_layout);
        mExitUndisturbTv = findViewById(R.id.undisturb_tv);
        mViewTips = findViewById(R.id.tip_view);
        mLargeLayout = findViewById(R.id.large_tv_layout);//inner_Layout
        mInnerLayout = findViewById(R.id.inner_Layout);
        mLargeRecogTv = findViewById(R.id.large_iatContent_tv);
        mLargeRecogTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mCloseLargeTv = findViewById(R.id.close_largeTv);
        mPreLargeBtn = findViewById(R.id.large_result_ivpre_page);
        mNextLargeBtn = findViewById(R.id.large_result_ivnext_page);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPreLargeBtn.setOnClickListener(this);
        mNextLargeBtn.setOnClickListener(this);
        mCloseLargeTv.setOnClickListener(this);
        mIncreaseVolImg.setOnClickListener(this);
        mDecreaseVolImg.setOnClickListener(this);
        mRecordStatusImg.setOnClickListener(this);
        mEditBtn.setOnClickListener(this);
        mTextBegin.setOnClickListener(this);
        mAudioPlayBtn.setOnClickListener(this);
        mEditPrePageBtn.setOnClickListener(this);
        mEditNextPageBtn.setOnClickListener(this);
        mResultPreBtn.setOnClickListener(this);
        mResultNextBtn.setOnClickListener(this);
        mSuspendRecord.setOnClickListener(this);
        mExitUndisturbTv.setOnClickListener(this);
        mFullScreesnTv.setOnClickListener(this);
        mUnDisturpTv.setOnClickListener(this);
        mWriteLayout = findViewById(R.id.write_layout);
        mBottomLayout = findViewById(R.id.bottom_layout);
        mNoteView = view.findViewById(R.id.MyNoteView);
        mNoteNextBtn = (Button) findViewById(R.id.note_nextpage);
        mNotePreBtn = (Button) findViewById(R.id.note_prevpage);
        mNotePageInfo = (TextView) findViewById(R.id.note_pg_info);
        mNoteNextBtn.setOnClickListener(this);
        mNotePreBtn.setOnClickListener(this);
        mNotePageInfo.setOnClickListener(this);

        mNoteView.setZOrderOnTop(true);
        SimplePen pencil = new SimplePen();
        mNoteView.setPen(pencil);
        mNoteView.setBackground(mBitmap);

    }

    @Override
    protected void onStop() {
        super.onStop();

        LogUtils.printErrorLog(TAG, "===onStop");
        forbiddenDropDown(0);
    }

    private void forbiddenDropDown(int i) {
        Intent mIntentDrop = new Intent();
        mIntentDrop.setAction("hanvon.intent.action.disabledropdown");
        mIntentDrop.putExtra("hanvon_disabledropdown", i);
        mIntentDrop.setComponent(new ComponentName("hanvon.aebr.hvsettings", "hanvon.aebr.hvsettings.ScreencapBroadCastRecevier"));
        sendBroadcast(mIntentDrop);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.printErrorLog(TAG, "==onNewIntent: " + HvApplication.TOKEN);
    }

    private void initData() {
        mUsageRecordTime = SharedPreferencesUtils.getUsageTimeSharedprefer(this, SharedPreferencesUtils.USAGETIME);
        SharedPreferencesUtils.clear(this, SharedPreferencesUtils.USAGETIME);
        LogUtils.printErrorLog(TAG, "==mUsagePlayTime: " + mUsageRecordTime);
        recordReceiver = new RecordReceiver();
        IntentFilter mBtFilter = new IntentFilter();
        mBtFilter.addAction(ConstBroadStr.SHOW_BACKLOGO);//ConstBroadStr.UPDATERECOG
        mBtFilter.addAction(ConstBroadStr.UPDATERECOG);
        mBtFilter.addAction(ConstBroadStr.ACTION_HOME_PAGE);
        mBtFilter.addAction(ConstBroadStr.UPDATEALSPEECHRECOG);
        mBtFilter.addAction(ConstBroadStr.HIDE_BACKLOGO);//ConstBroadStr.SPEENCH_AUTH
        mBtFilter.addAction(ConstBroadStr.SPEENCH_AUTH);
        mBtFilter.addAction(ConstBroadStr.SPEENCH_CLOSE);
        registerReceiver(recordReceiver, mBtFilter);
        isRegisterTag = true;
    }

    private void init() {
        TAG = getLocalClassName();
        hv_ebk = new HANVONEBK();
        mAudioManager = (AudioManager) HvApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        mCtlVolBar.AdjustVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), true);
        mHandler = new AudioHandler(this);
        mNoteView.setHandler(mHandler);
        mTotalResultList = new ArrayList<>();
        mTempResultList = new ArrayList<>();
        mTotalSpeechResultList = new ArrayList<>();
        mTempSpeechResultList = new ArrayList<>();
        mFileBean = TranslateBean.getInstance().getFileBean();
        if (mFileBean == null) {
            mFileBean = new FileBean();
        }

        FileBeanUils.setmFileBean(mFileBean);
        mDuration = mFileBean.getDuration();
        if (mDuration == 0) {
            mVolumLayout.setVisibility(View.GONE);
            mSeekBar.setVisibility(View.GONE);
            mTimeTv.setVisibility(View.INVISIBLE);
            mViewTips.setVisibility(View.GONE);
        } else {
            mSeekBar.setMax(mDuration);
        }
        isNEW = getIntent().getBooleanExtra("isNew", false);
        if (false) {
            mCheckbox.setVisibility(View.VISIBLE);
        }
        // wwn 如果是新建，创建便签文件
        if (isNEW) {
            createNewNote();
        } else {
            mNotePageIndex = 0;
            String tmpName = mFileBean.getCreatemillis();
            String pathName = ConstBroadStr.GetAudioRootPath(this, mCheckbox.isChecked()) + tmpName + "/" + tmpName + NoteBaseData.NOTE_SUFFIX;
            NoteBaseData.gTraFile = TraFile.readTraFile(false, pathName, this);

            if (NoteBaseData.gTraFile == null) {
                createNewNote();
                ToastUtils.show(this, getString(R.string.destroy_file));
                return;
            }
            if (NoteBaseData.gTraFile.pages == null) {
                createNewNote();
                ToastUtils.show(this, getString(R.string.destroy_file));
                return;
            }

            if (NoteBaseData.gTraFile.pages.size() > 0) {
                updateNoteCurPage();
            } else {
                AddNoteNewEmptyPage(mNotePageIndex);
                mNoteView.initialize((NoteBaseData.gTraFile.getPage(mNotePageIndex)).traces);
                updateNotePageInfo(mNotePageIndex, NoteBaseData.gTraFile.getCount());
            }
        }
        if (isNEW)
            return;
        else
            mCheckbox.setVisibility(View.GONE);

        mRecordFilePath = ConstBroadStr.GetAudioRootPath(this,
                TextUtils.equals(mFileBean.getmSd(), "sd")) + mFileBean.getCreatemillis() + "/" + mFileBean.getCreatemillis() + ".amr";
        if (TextUtils.isEmpty(mFileBean.getJson()))
            return;
        if (mFileBean.getSign() == 1) {
            IatResults.addAllAsrEditResult(new Gson().fromJson(mFileBean.getJson(), new TypeToken<ArrayList<AsrEditSentence>>() {
            }.getType()));
        } else {
            IatResults.addAllAsrEditResult(new Gson().fromJson(mFileBean.getJson(), new TypeToken<ArrayList<AsrEditSentence>>() {
            }.getType()));
        }

       mRecogResultTv.setText(mFileBean.getContent());
        if (TextUtils.isEmpty(mFileBean.getContent())) {
            mFullScreesnTv.setVisibility(View.INVISIBLE);
        }

        mRecogResultTv.getPageCount();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                freshResultPage();
            }
        }, 200);
    }

    private void createNewNote() {
        mNotePageIndex = 0;
        NoteBaseData.gTraFile = new TraFile();
        String tmpName = mFileBean.getCreatemillis();
        String dirPath = ConstBroadStr.GetAudioRootPath(this, mCheckbox.isChecked()) + tmpName + "/";
        NoteBaseData.gTraFile.createNewTraFile(dirPath, tmpName);
        // 添加一个新页
        AddNoteNewEmptyPage(mNotePageIndex);
        mNoteView.initialize((NoteBaseData.gTraFile.getPage(mNotePageIndex)).traces);
        updateNotePageInfo(mNotePageIndex, NoteBaseData.gTraFile.getCount());
    }


    @Override
    protected void onResume() {
        super.onResume();
        forbiddenDropDown(1);
        LogUtils.printErrorLog(TAG, "===onResume： " + HvApplication.TOKEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.printErrorLog(TAG, "===onPause");
    }

    @Override
    public void notifyNoteChanged(int i) {
        jumpToPage(i);
    }

    @Override
    public void startOcr(int i) {
        if (i == 0) {
            //todo:添加点的坐标
            ArrayList<Trace> traces = NoteBaseData.gTraFile.getPage(mNotePageIndex).traces;
            ArrayList<Short> points = new ArrayList<>();
            if (traces == null)
                return;
            DialogUtil.getInstance().showRecogDialog(IatActivity.this);
            for (Trace trace : traces) {
                for (Point point : trace.getPoints()) {
                    //LogUtils.printErrorLog("trace.getPoints", "Short s : point.y: " + point.y + "  point.x: " + point.x);
                    if (point.x < 0)
                        break;
                    points.add((short) (mNoteView.getHeight() - point.y));
                    points.add((short) point.x);
                }
                points.add((short) -1);
                points.add((short) 0);
            }
            points.add((short) -1);
            points.add((short) 0);
            points.add((short) -1);
            points.add((short) -1);
            Short[] traceBuf = new Short[points.size()];
            traceBuf2 = new short[points.size()];
            traceBuf = points.toArray(traceBuf);

            for (int j = 0; j < traceBuf.length; j++) {
                traceBuf2[j] = traceBuf[j].shortValue();
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LogUtils.printErrorLog(TAG, "isOCRing: " + isOCRing);
                    if (!isOCRing) {
                        isOCRing = true;
                        isShowOCRing = true;
                        LogUtils.printErrorLog(TAG, "before reco(traceBuf2)");
                        reco(traceBuf2);
                        LogUtils.printErrorLog(TAG, "after reco(traceBuf2)");
                        cancel();
                    } else {
                        isShowOCRing = false;
                    }
                }
            }, 0, 5000);
        } else {
            RecognmizeImageAyncTask recognmizeImageAyncTask = new RecognmizeImageAyncTask();
            recognmizeImageAyncTask.execute();
        }
    }
    short[] traceBuf2;
    boolean isOCRing = false;
    boolean isShowOCRing = true;


    void reco(short[] traceBuf) {
        try {
            Native.nativeHwSetDicAndLanguage(ROOT_OCR_BIN_PATH, 1);
            ArrayList<String> strings = new ArrayList<>();
            ArrayList<Integer> rects = new ArrayList<>();
            int cnt = Native.nativeHwRecoDoc(traceBuf, strings, rects);
            for (String s : strings) {
                LogUtils.printErrorLog(TAG, "===strings: " + s);
            }
            isOCRing = false;
            if (isShowOCRing) {
                Message message = Message.obtain();
                message.what = MESSAGE_WHAT3;
                message.obj = strings;
                mHandler.sendMessage(message);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mNoteView.setQuickReadChecked(b);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mReadCheckbox.getVisibility() != View.GONE) {
                    String string = TextViewUtils.getTextViewSelectionByTouch(mRecogResultTv, (int) event.getX(), (int) event.getY(), mRecogResultTv.getPageIdx());
                    LogUtils.printErrorLog("append", "event.getX(): " + event.getX() + " event.getY()" + event.getY());
                    LogUtils.printErrorLog("append", "string: " + string);
                    if (TextUtils.isEmpty(string)) {
                        ToastUtils.showLong(this, "语音跳转失败");
                        break;
                    }
                    if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE) {
                        List<AsrEditSentence> list = IatResults.getAsrEditResults();
                        for (AsrEditSentence result : list) {
                            if (result.getContent().contains(string)) {
                                long startTime = result.getRecordtime();
                                jumpPlayAmrRecord(startTime);
                                break;
                            }
                        }
                    } else {
                        List<AsrEditSentence> list = IatResults.getAsrEditResults();
                        for (AsrEditSentence result : list) {
                            if (result.getContent().contains(string)) {
                                long startTime = result.getRecordtime();
                                jumpPlayAmrRecord(startTime);
                                break;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    private void jumpPlayAmrRecord(long startTime) {
        if (mTimer != null)
            mTimer.cancel();
        MediaPlayerManager.getInstance().stop();
        mAudioPlayBtn.setText(getResources().getString(R.string.iat_stop));
        mUsagePlayTime = startTime;
        seekToProgress(startTime);
        playRecord();
    }

    private void seekToProgress(long startTime) {
        float index = (float) ((startTime * 1.0f) / mFileBean.getTime());
        int du = (int) (mFileBean.getDuration() * index);
        mSeekBar.setProgress(du);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (CommonUtils.isNoFastClick())
            showEditDialog(((TextView)view.findViewById(R.id.sentence_edit)).getText().toString(), position);
    }

    @Override
    public void setOnEditClick(int position) {
        showEditDialog(mTempResultList.get(position).getContent(), position);
    }

    protected class RecordReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ConstBroadStr.SHOW_BACKLOGO)) {
                LogUtils.printErrorLog(TAG, "ConstBroadStr.SHOW_BACKLOGO");
                if (FileBeanUils.isRecoding()) {
                    mRecordStatusImg.setBackgroundResource(R.drawable.ps_play);
                    pauseRecognize();
                }
                mFileBean.setTime(mFileBean.getTime() + mUsageRecordTime);
                mUsageRecordTime = 0;
                LogUtils.printErrorLog(TAG, "pauseRecognize  mFileBean.getTime()：" + mFileBean.getTime() + "   mUsageRecordTime:  " + mUsageRecordTime);
                if (MediaPlayerManager.getInstance().isPlaying()) {
                    SharedPreferencesUtils.saveUsageTimeSharePrefer(HvApplication.mContext, SharedPreferencesUtils.PLAYTIME, (System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime));
                    LogUtils.printErrorLog(TAG, "pauseRecognize  mFileBean.getTime()： "  + "System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime: " + (System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime) );
                    LogUtils.printErrorLog(TAG, "pauseRecognize  mFileBean.getTime()： "  + mUsagePlayTime +  " + mUsagePlayTime");
                } else if (mUsagePlayTime > 0) {
                    LogUtils.printErrorLog(TAG, "pauseRecognize  mFileBean.getTime()： "  + mUsagePlayTime +  " + mUsagePlayTime");
                    SharedPreferencesUtils.saveUsageTimeSharePrefer(HvApplication.mContext, SharedPreferencesUtils.PLAYTIME, mUsagePlayTime);
                }
                stopPlayRecord();
                CommonUtils.setOnValidate(true, getApplicationContext());
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.UPDATERECOG)) {
                if (mNoteView.canBeFresh() && mResultLayout.getVisibility() == View.VISIBLE) {
                    mNoteView.setInputEnabled(false);
                    freshRecogContent();
                    mHandler.postDelayed(() ->{mNoteView.setInputEnabled(true);}, 100);
                }
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.UPDATEALSPEECHRECOG)) {
                if (mNoteView.canBeFresh() && mResultLayout.getVisibility() == View.VISIBLE) {
                    mNoteView.setInputEnabled(false);
                    freshRecogContent();
                    mHandler.postDelayed(() ->{mNoteView.setInputEnabled(true);}, 100);
                }
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.ACTION_HOME_PAGE)) {
                saveAndExitActivity();
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.HIDE_BACKLOGO)) {
                LogUtils.printErrorLog(TAG, "===HIDE_BACKLOGO");//SPEENCH_AUTH
                mUsagePlayTime = SharedPreferencesUtils.getUsageTimeSharedprefer(HvApplication.mContext, SharedPreferencesUtils.PLAYTIME);
                LogUtils.printErrorLog(TAG, "mUsagePlayTime: " + mUsagePlayTime);
                SharedPreferencesUtils.clear(HvApplication.mContext, SharedPreferencesUtils.PLAYTIME);
                seekToProgress(mUsagePlayTime);
                mTimeTv.setText(TimeUtil.calculateTime((int) (mUsagePlayTime / 1000)) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.SPEENCH_AUTH)) {
                if (HvApplication.HaveAuth) {
                    setRecordStatus();
                    startRecord();
                    mViewTips.setVisibility(View.GONE);
                    mReadCheckbox.setChecked(false);
                    AlSpeechEngine.getInstance().startSpeechRecog();
                } else {
                    if (mDuration != 0) {
                        mViewTips.setVisibility(View.VISIBLE);
                    }
                    stopRecognizeIfFailue();
                    ToastUtils.showLong(getApplicationContext(), "授权失败");
                }
                mTextBegin.setEnabled(true);
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.SPEENCH_CLOSE)) {
                if (FileBeanUils.isRecoding()) {
                    mIatLayout.setVisibility(View.VISIBLE);
                    mRecordLayout.setVisibility(View.GONE);
                    stopRecognizeIfFailue();
                    AlSpeechEngine.getInstance().cancelSpeechRecog();
                    ToastUtils.showLong(getApplicationContext(), intent.getStringExtra(ConstBroadStr.SPEENCH_ERROR));
                }
            }
        }
    }

    private void freshRecogContent() {
        if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE) {
            if (TextUtils.equals(IatResults.getAsrEditResultsStr(), mRecogResultTv.getText().toString()))
                return;
            LogUtils.printErrorLog(TAG, "freshRecogContent");
            mRecogResultTv.setText(IatResults.getAsrEditResultsStr());
        } else {
            if (TextUtils.equals(IatResults.getSpeechResultsStr(), mRecogResultTv.getText().toString()))
                return;
            LogUtils.printErrorLog(TAG, "freshRecogContent");
            mRecogResultTv.setText(IatResults.getAsrEditResultsStr());
        }
        mRecogResultTv.gotoLastPage();
        freshResultPage();
    }


    private class AudioHandler extends Handler {
        WeakReference<IatActivity> weakReference;

        public AudioHandler(IatActivity activity) {
            weakReference = new WeakReference<IatActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case MESSAGE_WHAT1:
                    if (FileBeanUils.isRecoding()) {
                        if (mNoteView.canBeFresh())
                            freshRecogContent();
                    } else {
                        if (mDuration == 0)
                            return;
                        LogUtils.printErrorLog(TAG, "mNoteView.canBeFresh(): " + mNoteView.canBeFresh());
                        LogUtils.printErrorLog(TAG, "MediaPlayerManager.getInstance().getCurrentPosition(): " + MediaPlayerManager.getInstance().getCurrentPosition());
                        if (mNoteView.canBeFresh()) {//(100 * mSeekBar.getProgress()) / mDuration + "%"

                            if (MediaPlayerManager.getInstance().isPlaying()) {
                                mSeekBar.setProgress(MediaPlayerManager.getInstance().getCurrentPosition());
                                if (MediaPlayerManager.getInstance().getCurrentPosition() == 0) {
                                    mTimeTv.setText(TimeUtil.calculateTime(0) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
                                } else {
                                    mTimeTv.setText(TimeUtil.calculateTime((int) ((System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
                                }
                            }
                        }
                    }
                    break;
                case MESSAGE_WHAT2:
                    long startTime = (long) message.obj;
                    LogUtils.printErrorLog(TAG, "startTime: " + startTime);
                    jumpPlayAmrRecord(startTime);
                    break;
                case MESSAGE_WHAT3:
                    DialogUtil.getInstance().disRecogDialog();
                    StringBuffer stringBuffer = new StringBuffer();
                    ArrayList<String> list = (ArrayList<String>) message.obj;
                    for (String s : list)
                        stringBuffer.append(s);
                    if (TextUtils.isEmpty(stringBuffer.toString())) {
                        ToastUtils.showLong(getApplicationContext(), getResources().getString(R.string.noresult));
                        return;
                    }
                    showOcrResultDialog(stringBuffer.toString());
                    break;
            }

        }
    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mAudioOffset = seekBar.getProgress();
            //MediaPlayerManager.getInstance().seekTo(mAudioOffset);
            if (mFileBean.getDuration() == 0)
                return;
            mUsagePlayTime = (mFileBean.getTime() * seekBar.getProgress()) / mFileBean.getDuration();
            mTimeTv.setText(TimeUtil.calculateTime((int) (mUsagePlayTime / 1000)) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));

            LogUtils.printErrorLog(TAG, "===onStopTrackingTouch (): ");
            LogUtils.printErrorLog(TAG, "===onStopTrackingTouch mUsagePlayTime(): " + mUsagePlayTime);
            isSeekbarChaning = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            LogUtils.printErrorLog(TAG, "===onStartTrackingTouch: ");
            stopPlayRecord();
            isSeekbarChaning = true;
            mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtils.printErrorLog(TAG, "===onProgressChanged: " + seekBar.getProgress());
            if (mNoteView.canBeFresh()) {
                if (mDuration == 0)
                    return;
                if (seekBar.getProgress() == 0) {
                    mTimeTv.setText(TimeUtil.calculateTime(0) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
                }
            }
        }
    };

    private void updateEditList() {
        for (int i = 0; i < mTotalResultList.size(); i++) {
            if (TextUtils.isEmpty(mTotalResultList.get(i).getContent()))
                mTotalResultList.remove(i);
        }
        IatResults.addAllAsrEditResult(mTotalResultList);
    }



    private void onReturn() {
        if (mEditLayout.getVisibility() == View.VISIBLE) {
            hideSoftInput(this);
            mEditLayout.setVisibility(View.GONE);
            if (mGotoUndisturb) {
                mUndisturb_layout.setVisibility(View.VISIBLE);
                mGotoUndisturb = false;
            } else {
                mResultLayout.setVisibility(View.VISIBLE);
            }
            mMenus.setVisibility(View.VISIBLE);
            mWriteLayout.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
            mNoteView.setVisibility(View.VISIBLE);
            mOperationLayout.setVisibility(View.VISIBLE);
            updateNoteCurPage();
            nPageIsx = 0;
            if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE) {
                updateEditList();
                mRecogResultTv.setText(IatResults.getAsrEditResultsStr());
            } else {
                updateEditList();
                mRecogResultTv.setText(IatResults.getAsrEndEditResult());
            }
            mRecogResultTv.getPageCount();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    freshResultPage();
                }
            }, 200);
        } else {
            saveAndExitActivity();
            finish();
        }
    }

    private void saveAndExitActivity() {
        if (!HvApplication.ISDEBUG)
            hv_ebk.ASRVolumeEnable(0);
        DatabaseUtils databaseUtils = DatabaseUtils.getInstance(this);
        String con = mRecogResultTv.getText() == null ? "" : mRecogResultTv.getText().toString();
        if (!TextUtils.equals(con, mFileBean.getContent())) {
            mFileBean.setContent(con);
            if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE)
                mFileBean.setJson(JSON.toJSONString(IatResults.getAsrEditResults()));
            else
                mFileBean.setJson(JSON.toJSONString(IatResults.getAsrEditResults()));
            mFileBean.setModifytime(TimeUtil.getTime(System.currentTimeMillis()));
            mFileBean.setDuration(mDuration);
            LogUtils.printErrorLog(TAG, mFileBean.getJson());
            databaseUtils.updateByContent(mFileBean);
        }
        // 保存笔迹
        saveNoteCurTracePage();
        saveCurTraNoteFile();
        saveAsrTxt();
        exitActivity();
    }

    private void stopRecognize() {
        if (FileBeanUils.isRecoding()) {
            mRecognizeStatusTv.setText(R.string.recognizing);
            mRecordStatusImg.setBackgroundResource(R.drawable.ps_pause);
            CommonUtils.setOnValidate(true, this);
            if (!mNoRecogCheckbox.isChecked()) {
                uploadUsageTime();
                if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE)
                    close(false);
                else
                    AlSpeechEngine.getInstance().cancelSpeechRecog();
            } else {
                setFileRecordTime();
                mTimeTv.setText(TimeUtil.calculateTime(((int) (mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
                if ((int) (mUsagePlayTime) / 1000 > 0) {
                    mSeekBar.setVisibility(View.VISIBLE);
                    mTimeTv.setVisibility(View.VISIBLE);
                }
                DatabaseUtils.getInstance(HvApplication.getContext()).updateTime(mFileBean);
            }
            if (mDuration != 0) {
                mViewTips.setVisibility(View.VISIBLE);
            }
            MediaRecorderManager.getInstance().stop();
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mTempPath))
                        return;
                    if (hvFileCommonUtils.isFileExist(mTempPath)) {
                        FileUtils.copyRecordFile(mRecordFilePath, mTempPath);
                    }
                }
            });
            FileBeanUils.setRecoding(false);
            if (HvApplication.Recognition_Engine == SPEECH_ENGINE) {
                LogUtils.printErrorLog(TAG, "getAsrEndEditResult:  " + IatResults.getAsrEndEditResult());
                LogUtils.printErrorLog(TAG, "getAsrEditResultsStr:  " + IatResults.getAsrEditResultsStr());
                mHandler.postDelayed(() -> {
                    mRecogResultTv.setText(IatResults.getAsrEndEditResult());
                    mRecogResultTv.gotoLastPage();
                }, 400);

            }
        } else {
            LogUtils.printErrorLog(TAG, "842 mFileBean.getTime(): " + mFileBean.getTime() + "   mUsageRecordTime: " + mUsageRecordTime);

            mFileBean.setTime(mFileBean.getTime() + mUsageRecordTime);
            FileBeanUils.setmFileBean(mFileBean);
            mUsageRecordTime = 0;
            mTimeTv.setText(TimeUtil.calculateTime(((int) (mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
            if ((int) (mUsagePlayTime) / 1000 > 0) {
                mSeekBar.setVisibility(View.VISIBLE);
                mTimeTv.setVisibility(View.VISIBLE);
            }
            DatabaseUtils.getInstance(HvApplication.getContext()).updateTime(mFileBean);
        }
    }

    private void stopRecognizeIfFailue() {
        if (FileBeanUils.isRecoding()) {
            long tempTime = System.currentTimeMillis() - mStartAsrTime + mUsageRecordTime;
            LogUtils.printErrorLog(TAG, "stopRecognizeIfFailue  mFileBean.getTime(): " + mFileBean.getTime() + "  tempTime: " + tempTime);
            mFileBean.setTime(mFileBean.getTime() + tempTime);
            mStartAsrTime = 0;

            FileBeanUils.setmStartRecordTime(mStartAsrTime);

            FileBeanUils.setRecoding(false);
            //AlSpeechEngine.getInstance().cancelSpeechRecog();
            if (mDuration != 0) {
                mViewTips.setVisibility(View.VISIBLE);
            }
            mRecognizeStatusTv.setText(getString(R.string.pausing));
            MediaRecorderManager.getInstance().stop();
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mTempPath))
                        return;
                    //Log.e("startRecognize", "file.exists(): " + hvFileCommonUtils.isFileExist(mTempPath));
                    if (hvFileCommonUtils.isFileExist(mTempPath)) {
                        FileUtils.copyRecordFile(mRecordFilePath, mTempPath);
                    }
                }
            });
        }
    }


    private void pauseRecognize() {
        if (FileBeanUils.isRecoding()) {
            CommonUtils.setOnValidate(true, this);
            mUsageRecordTime = System.currentTimeMillis() - mStartAsrTime + mUsageRecordTime;
            mStartAsrTime = 0;
            FileBeanUils.setmStartRecordTime(mStartAsrTime);
            LogUtils.printErrorLog(TAG, "pauseRecognize  mFileBean.getTime()：" + mFileBean.getTime() + "   mUsageRecordTime:  " + mUsageRecordTime);

            if (!mNoRecogCheckbox.isChecked()) {
                if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE)
                    close(false);
                else
                    AlSpeechEngine.getInstance().cancelSpeechRecog();
            }

            if (mDuration != 0) {
                mViewTips.setVisibility(View.VISIBLE);
            }
            mRecognizeStatusTv.setText(getString(R.string.pausing));
            MediaRecorderManager.getInstance().stop();
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mTempPath))
                        return;
                    LogUtils.printErrorLog("startRecognize", "file.exists(): " + hvFileCommonUtils.isFileExist(mTempPath));
                    if (hvFileCommonUtils.isFileExist(mTempPath)) {
                        FileUtils.copyRecordFile(mRecordFilePath, mTempPath);
                    }
                }
            });
            FileBeanUils.setRecoding(false);
        }
    }

    private void continueRecognize() {
        if (!FileBeanUils.isRecoding()) {

            if (HvApplication.Recognition_Engine == BAIDU_ENGINE) {
            if (!mNoRecogCheckbox.isChecked()) {
                    mRecognizeStatusTv.setText(getString(R.string.recognizing));
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                start();
                                pollCheckStop();
                            } catch (IOException e) {
                                ToastUtils.show(getApplicationContext(), getString(R.string.tryagain));
                            }
                        }
                    });
                }
                startRecord();
                FileBeanUils.setRecoding(true);
                ToastUtils.show(getApplicationContext(), getResources().getString(R.string.startrecording));
            } else {
                //mRecordStatusImg.setEnabled(false);
                AlSpeechEngine.getInstance().initSpeech();
            }
        }
    }

    private void exitActivity() {
        exitRecoging();
        stopRecognize();
        stopPlayRecord();
        IatResults.clearAsrEditResults();
        if (isRegisterTag) {
            unregisterReceiver(recordReceiver);
            isRegisterTag = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_option_menus:
                enterHandwrite(false);
                PopupWindow popupWindow = showPopupWindow();
                if (popupWindow != null) {
                    popupWindow.setFocusable(true);
                }
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(mMenus);
                }
                break;
            case R.id.btn_Return:
                onBackPressed();
                break;
            case R.id.btn_Home:
                saveAndExitActivity();
                new MethodUtils(this).getHome();
                break;
            case R.id.text_begin:
                if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                    ToastUtils.show(getApplicationContext(), getResources().getString(R.string.playingAudio));
                    return;
                }
                checkUsageTime();
                break;
            case R.id.text_edit:
                if (FileBeanUils.isRecoding()) {
                    ToastUtils.show(getApplicationContext(), getResources().getString(R.string.tips1));
                    return;
                }
                freEditSentenceshPage();
                break;
            case R.id.iat_play:
                if (FileBeanUils.isRecoding()) {
                    ToastUtils.show(getApplicationContext(), getResources().getString(R.string.tips2));
                    return;
                }
                if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                    if (mTimer != null)
                        mTimer.cancel();
                    if (!HvApplication.ISDEBUG)
                        hv_ebk.ASRVolumeEnable(0);
                    CommonUtils.setOnValidate(true, this);
                    MediaPlayerManager.getInstance().stop();
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
                    mUsagePlayTime = System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime;
                } else {
                    if (mRecordFilePath == null || !hvFileCommonUtils.isFileExist(mRecordFilePath)) {
                        ToastUtils.show(getApplicationContext(), getResources().getString(R.string.tips3));
                        return;
                    }
                    if (!HvApplication.ISDEBUG)
                        hv_ebk.ASRVolumeEnable(1);
                    CommonUtils.setOnValidate(false, this);
                    mStartPlayTime = System.currentTimeMillis();
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
                preResultPage(false);
                break;
            case R.id.result_ivnext_page:
                nextResultPage(false);
                break;
            case R.id.suspendRecord:
                stopRecognize();
                if (mUndisturb_layout.getVisibility() != View.VISIBLE) {
                    mOperationLayout.setVisibility(View.VISIBLE);
                    mFullScreesnTv.setVisibility(View.VISIBLE);
                    mUnDisturpTv.setVisibility(View.GONE);
                }
                mIatLayout.setVisibility(View.VISIBLE);
                mRecordLayout.setVisibility(View.GONE);
                break;
            case R.id.note_nextpage:
                gotoNoteNextPage();
                break;
            case R.id.note_prevpage:
                gotoNotePrevPage();
                break;
            case R.id.suspendImg:
                mRecordStatusImg.setEnabled(false);
                mHandler.postDelayed(() ->{mRecordStatusImg.setEnabled(true);}, 3000);
                if (!FileBeanUils.isRecoding()) {
                    if (!mNoRecogCheckbox.isChecked()) {
                        if (!WifiUtils.isWifiOpened()) {
                            DialogUtil.getInstance().showNetWorkDialog(this);
                            return;
                        }
                        if (WifiUtils.getWifiConnectState(HvApplication.getContext()) == NetworkInfo.State.DISCONNECTED) {
                            ToastUtils.show(this, getString(R.string.checkNeterror));
                            LogUtils.printErrorLog(TAG, "getWifiConnectState: 网络未连接");
                            return;
                        }
                    }
                    if (mNoRecogCheckbox.isChecked()) {
                        FileBeanUils.setRecoding(true);
                        startRecord();
                        mRecognizeStatusTv.setText(R.string.recognizing);
                        mRecordStatusImg.setBackgroundResource(R.drawable.ps_pause);
                        return;
                    }
                    continueRecognize();
                    mRecordStatusImg.setBackgroundResource(R.drawable.ps_pause);
                } else {
                    mRecordStatusImg.setBackgroundResource(R.drawable.ps_play);
                    pauseRecognize();
                }
                break;
            case R.id.increase_volume:
                adjustVolume(true);
                break;
            case R.id.decre_volume:
                adjustVolume(false);
                break;
            case R.id.note_pg_info:
                saveNoteCurTracePage();
                enterHandwrite(false);
                DialogUtil dialogUtil = DialogUtil.getInstance();
                dialogUtil.regListener(this);
                dialogUtil.showJumpDialog(this, mNotePageIndex);
                break;
            case R.id.large_result_ivpre_page:
                preResultPage(true);
                break;
            case R.id.large_result_ivnext_page:
                nextResultPage(true);
                break;
            case R.id.undisturb_tv:
                exitUnDisturp();
                break;
            case R.id.close_largeTv:
                exitLargeLayout();
                break;
            case R.id.undisturbbtn:
                if (mEditLayout.getVisibility() == View.VISIBLE) {
                    ToastUtils.showLong(IatActivity.this, "编辑状态无法进入免打扰模式");
                    return;
                }
                if (mUndisturb_layout.getVisibility() == View.GONE) {
                    mUndisturb_layout.setVisibility(View.VISIBLE);
                    mResultLayout.setVisibility(View.GONE);
                    mOperationLayout.setVisibility(View.INVISIBLE);
                } else {
                    exitUnDisturp();
                }

                break;
            case R.id.fullScreenbtn:
                if (mLargeLayout.getVisibility() == View.GONE) {
                    if (TextUtils.isEmpty(mRecogResultTv.getText().toString())) {
                        ToastUtils.showLong(this, "当前内容为空");
                        return;
                    }
                    saveNoteCurTracePage();
                    mMenus.setVisibility(View.GONE);
                    mInnerLayout.setVisibility(View.GONE);
                    mNoteView.setVisibility(View.GONE);
                    mLargeRecogTv.setText(mRecogResultTv.getText());
                    mLargeLayout.setVisibility(View.VISIBLE);
                    mOperationLayout.setVisibility(View.GONE);
                    mHandler.postDelayed(() ->{freshFullScreenResultPage();}, 200);
                } else {
                    exitLargeLayout();
                }
                break;
            default:
                break;
        }
    }

    private void addNoteView() {

    }

    private void removeNoteView() {
        WindowManager windowManager = (WindowManager)this.getSystemService("window");
        if (mNoteView != null) {
            windowManager.removeView(mNoteView);
            mNoteView = null;
        }
    }

    private void exitUnDisturp() {
        mUndisturb_layout.setVisibility(View.GONE);
        mResultLayout.setVisibility(View.VISIBLE);
        mOperationLayout.setVisibility(View.VISIBLE);
        if (mRecordLayout.getVisibility() != View.VISIBLE) {
            mFullScreesnTv.setVisibility(View.VISIBLE);
            mUnDisturpTv.setVisibility(View.GONE);
        } else {
            mFullScreesnTv.setVisibility(View.GONE);
            mUnDisturpTv.setVisibility(View.VISIBLE);
        }
        mOperationLayout.setVisibility(View.VISIBLE);
        if (FileBeanUils.isRecoding())
            enterHandwrite(true);
        freshRecogContent();
    }

    public void adjustVolume(boolean up) {
        if (up) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        } else {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }
        mCtlVolBar.AdjustVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), true);
    }

    private void stopPlayRecord() {
        if (mTimer != null)
            mTimer.cancel();
        mUsagePlayTime = 0;
        mTimeTv.setText(TimeUtil.calculateTime(0) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
        mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
        MediaPlayerManager.getInstance().stop();
    }

    private void playRecord() {
        MethodUtils.closeRecord();
        mVolumLayout.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.VISIBLE);
        mTimeTv.setVisibility(View.VISIBLE);
        if (!MediaPlayerManager.getInstance().isPlaying()) {
            mAudioOffset = mSeekBar.getProgress();
            MediaPlayerManager.getInstance().play(mRecordFilePath, mAudioOffset, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (!HvApplication.ISDEBUG)
                        hv_ebk.ASRVolumeEnable(0);
                    mFileBean.setTime(System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime);
                    stopPlayRecord();
                    mSeekBar.setProgress(0);
                }
            });//开始播放
            mDuration = MediaPlayerManager.getInstance().getDuration();//获取音乐总时间
            if (mDuration == 0)
                return;

            mViewTips.setVisibility(View.VISIBLE);
            if (mAudioOffset == 0) {
                mStartPlayTime = System.currentTimeMillis();
            } else {
                mStartPlayTime = System.currentTimeMillis();
            }
            mFileBean.setDuration(mDuration);
            DatabaseUtils.getInstance(this).updateDurationByContent(mFileBean);
            mSeekBar.setMax(mDuration);//将音乐总时间设置为Seekbar的最大值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isSeekbarChaning) {
                        if (mNoteView.canBeFresh()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSeekBar.setProgress(MediaPlayerManager.getInstance().getCurrentPosition());
                                    mTimeTv.setText(TimeUtil.calculateTime((int) ((System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
                                }
                            });

                        }
                    }
                }
            }, 0, 1000);

        }
    }

    private void startRecord() {
        CommonUtils.setOnValidate(false, this);
        String tmpName = mFileBean.getCreatemillis();
        if (isNEW) {
            if (mCheckbox.isChecked()) {
                mFileBean.mSd = "sd";
            }
        }
        createFile(tmpName);
        FileBeanUils.setRecoding(true);
        mStartAsrTime = System.currentTimeMillis();
        FileBeanUils.setmStartRecordTime(mStartAsrTime);
        if (hvFileCommonUtils.isFileExist(mRecordFilePath)) {
            MediaRecorderManager.getInstance().start(mTempPath);
        } else {
            MediaRecorderManager.getInstance().start(mRecordFilePath);
        }
    }

    private void startRecognize() {
        if (!FileBeanUils.isRecoding()) {

            if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE) {
                startRecord();
                if (!mNoRecogCheckbox.isChecked()) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                start();
                                pollCheckStop();
                            } catch (IOException e) {
                                ToastUtils.show(getApplicationContext(), getString(R.string.tryagain));
                            }
                        }
                    });
                }
                ToastUtils.show(getApplicationContext(), getResources().getString(R.string.startrecording));
            } else {
                FileBeanUils.setRecoding(true);
                mTextBegin.setEnabled(false);
                AlSpeechEngine.getInstance().initSpeech();
            }
        }
    }
    private boolean mGotoUndisturb;
    private void freEditSentenceshPage() {
        if (mNoteView.getVisibility() == View.VISIBLE)
            saveNoteCurTracePage();
        if (IatResults.getAsrEditResults().size() == 0 && HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE) {
            ToastUtils.show(this, getString(R.string.noEditText));
            return;
        } else if (IatResults.getAsrEditResults().size() == 0 && HvApplication.Recognition_Engine == 2) {
            ToastUtils.show(this, getString(R.string.noEditText));
            return;
        }
        if (mLargeLayout.getVisibility() == View.VISIBLE) {
            exitLargeLayout();
        }
        mMenus.setVisibility(View.GONE);
        mEditLayout.setVisibility(View.VISIBLE);
        mResultLayout.setVisibility(View.GONE);
        mWriteLayout.setVisibility(View.GONE);
        mBottomLayout.setVisibility(View.GONE);
        mNoteView.setVisibility(View.GONE);
        //removeNoteView();
        mOperationLayout.setVisibility(View.GONE);
        if (mUndisturb_layout.getVisibility() == View.VISIBLE)  {
            mGotoUndisturb = true;
            mUndisturb_layout.setVisibility(View.GONE);
        }

        if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE) {
            mTotalResultList.clear();
            mTotalResultList.addAll(IatResults.getAsrEditResults());
            nPageCount = getTotalqlPageCount(mTotalResultList.size());
            initPage();
            freshSentenceList(nPageIsx);
        } else {
            mTotalResultList.clear();
            mTotalResultList.addAll(IatResults.getAsrEditResults());
            nPageCount = getTotalqlPageCount(mTotalResultList.size());
            initPage();
            freshSentenceList(nPageIsx);
        }
    }

    public long getCurrrentRecordTime() {
        LogUtils.printErrorLog(TAG, "getCurrrentRecordTime  mUsageRecordTime:  " + mUsageRecordTime);
        return mFileBean.getTime() + System.currentTimeMillis() - mStartAsrTime + mUsageRecordTime;
    }

    private void uploadUsageTime() {
        HashMap<String, String> map2 = new HashMap<>();
        long tempTime = System.currentTimeMillis() - mStartAsrTime + mUsageRecordTime;
        setFileRecordTime();
        mTimeTv.setText(TimeUtil.calculateTime(((int) (mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int) (mFileBean.getTime() / 1000)));
        if ((int) (mUsagePlayTime) / 1000 > 0) {
            mSeekBar.setVisibility(View.VISIBLE);
            mTimeTv.setVisibility(View.VISIBLE);
        }
        DatabaseUtils.getInstance(HvApplication.getContext()).updateTime(mFileBean);

        if (!mNoRecogCheckbox.isChecked()) {
            DialogUtil.getInstance().showWaitingDialog(this);
            map2.put("duration", String.valueOf(tempTime / 1000));
            map2.put("voiceEngineTypeID", String.valueOf(HvApplication.Recognition_Engine));
            uploadTime(map2, tempTime);
        }
    }

    private void setFileRecordTime() {
        LogUtils.printErrorLog(TAG, "uploadUsageTime  mUsageRecordTime:  " + mUsageRecordTime);
        long tempTime = System.currentTimeMillis() - mStartAsrTime + mUsageRecordTime;
        LogUtils.printErrorLog(TAG, "uploadUsageTime mStartAsrTime: " + mStartAsrTime + "  tempTime:  " + tempTime);
        mFileBean.setTime(mFileBean.getTime() + tempTime);
        mUsageRecordTime = 0;
        FileBeanUils.setmFileBean(mFileBean);
    }

    public void exitRecoging() {
        if (mRecognizeStatusTv.getVisibility() == View.VISIBLE && TextUtils.equals(mRecognizeStatusTv.getText(),
                getResources().getString(R.string.pausing))) {
            HashMap<String, String> map2 = new HashMap<>();
            if (mUsageRecordTime == 0)
                return;
            map2.put("duration", String.valueOf(mUsageRecordTime / 1000));
            map2.put("voiceEngineTypeID", String.valueOf(HvApplication.Recognition_Engine));
            uploadTime(map2, mUsageRecordTime);
        }
    }

    private void uploadTime(HashMap<String, String> map2, long tempTime) {
        RetrofitManager.getInstance(this).submitUsedTime(map2, new RetrofitManager.ICallBack() {
            @Override
            public void successData(String result) {
                DialogUtil.getInstance().disWaitingDialog();
                Gson gson2 = new Gson();
                VerificationResult c = gson2.fromJson(result, VerificationResult.class);
                if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE) || TextUtils.equals(c.getCode(), Constant.NOTIME)) {
                    ToastUtils.show(IatActivity.this, c.getMsg());
                } else {
                    SharedPreferencesUtils.saveUsageTimeSharePrefer(HvApplication.mContext, SharedPreferencesUtils.USAGETIME, tempTime);
                    ToastUtils.show(IatActivity.this, c.getMsg());
                }
            }

            @Override
            public void failureData(String error) {
                Log.e("AA", "error: " + error);
                SharedPreferencesUtils.saveUsageTimeSharePrefer(HvApplication.mContext, SharedPreferencesUtils.USAGETIME, tempTime);
                DialogUtil.getInstance().disWaitingDialog();
            }
        });
    }

    private boolean checkUserStatus() {
        String id = checkId();
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        if (TextUtils.isEmpty(HvApplication.TOKEN)) {
            ToastUtils.show(this, getString(R.string.tips6));
            return false;
        }
        return true;
    }


    private void checkUsageTime() {
        if (!FileBeanUils.isRecoding()) {
            MethodUtils.closeRecord();
            if (mEditLayout.getVisibility() == View.VISIBLE) {
                onReturn();
            }
            if (mLargeLayout.getVisibility() == View.VISIBLE) {
                exitLargeLayout();
            }

            if (mNoRecogCheckbox.isChecked()) {
                mViewTips.setVisibility(View.GONE);
                FileBeanUils.setRecoding(true);
                startRecord();
                setRecordStatus();
                return;
            }

            if (!checkUserStatus())
                return;

            DialogUtil.getInstance().showWaitingDialog(this);
            RetrofitManager.getInstance(this).getAccountPacks(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                @Override
                public void successData(String result) {
                    DialogUtil.getInstance().disWaitingDialog();
                    Gson gson2 = new Gson();
                    PackList c = gson2.fromJson(result, PackList.class);
                    LogUtils.printErrorLog("A", "onResponse: " + "TextUtils.equals(c.getCode(), Constant.SUCCESSCODE): " + TextUtils.equals(c.getCode(), Constant.SUCCESSCODE));

                    LogUtils.printErrorLog("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());

                    if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                        if (c.getPackBean() != null) {
                            PackBean packBean = c.getPackBean().get(0);
                            LogUtils.printErrorLog("A", "onResponse: " + "packBean.VoiceEngineTypeID: " + packBean.VoiceEngineTypeID);
                            HvApplication.Recognition_Engine = packBean.VoiceEngineTypeID;
                            startASR();
                        } else {
                            ToastUtils.showLong(IatActivity.this, getString(R.string.tips4));
                        }
                    } else if (TextUtils.equals(c.getCode(), String.valueOf(1))) {
                        ToastUtils.showLong(IatActivity.this, getString(R.string.tips4));
                    } else if (TextUtils.equals(c.getCode(), String.valueOf(2))) {//时长用完
                        ToastUtils.showLong(IatActivity.this, getString(R.string.tips7));
                    } else {
                        ToastUtils.showLong(IatActivity.this, getString(R.string.tips4));
                    }
                }

                @Override
                public void failureData(String error) {
                    DialogUtil.getInstance().disWaitingDialog();
                    if (error.contains("null object reference")) {
                        ToastUtils.showLong(IatActivity.this, getString(R.string.tips4));
                    }
                    LogUtils.printErrorLog("AA", "error: " + error);
                }
            });
        } else {
            stopRecognize();
        }

    }

    private void startASR() {
        if (HvApplication.Recognition_Engine == Constants.BAIDU_ENGINE) {
            LogUtils.printErrorLog(TAG, "===Constants.BAIDU_ENGINE");
            setRecordStatus();
        }
        startRecognize();

    }

    private void setRecordStatus() {
        mIatLayout.setVisibility(View.GONE);
        mRecordLayout.setVisibility(View.VISIBLE);
        mRecognizeStatusTv.setText(R.string.recognizing);
        mRecordStatusImg.setBackgroundResource(R.drawable.ps_pause);

        mFullScreesnTv.setVisibility(View.GONE);
        mUnDisturpTv.setVisibility(View.VISIBLE);
    }

    private void createFile(String name) {
        String dirPath = ConstBroadStr.GetAudioRootPath(this, mCheckbox.isChecked()) + name + "/";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //LogUtils.printErrorLog("tag", "file.exists(): " + hvFileCommonUtils.isFileExist(dirPath));
        mRecordFilePath = dirPath + name + Constant.SUFFIX;
        if (hvFileCommonUtils.isFileExist(mRecordFilePath)) {
            mTempPath = dirPath + System.currentTimeMillis() + Constant.SUFFIX;
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
                saveNoteCurTracePage();
                saveCurTraNoteFile();
                saveAsrTxt();
                ToastUtils.show(this, getString(R.string.saved));
            }
        });
        TextView menuItem2 = view.findViewById(R.id.popup_delete);
        menuItem2.setOnClickListener(view12 -> {
            if (popupWindow != null) {
                popupWindow.dismiss();
                mNoteView.clearAllMemo();
            }
        });
        TextView menuItem3 = view.findViewById(R.id.popup_rubber);
        menuItem3.setOnClickListener(view1 -> {
            if (popupWindow != null) {
                popupWindow.dismiss();
                if (mNoteView.penType == mNoteView.TP_PEN) {

                    mNoteView.penType = mNoteView.TP_ERASER;

                    MyRuber rubber = new MyRuber();
                    mNoteView.setPen(rubber);
                } else {

                    mNoteView.penType = mNoteView.TP_PEN;
                    Pencil pencil = new Pencil();
                    mNoteView.setPen(pencil);
                }

            }
        });
        TextView menuItem4 = view.findViewById(R.id.popup_share);
        menuItem4.setOnClickListener(view12 -> {
            if (popupWindow != null) {
                popupWindow.dismiss();
                index = 4;
                String ht = generateShareHtml(mRecogResultTv.getText() == null ? "" : mRecogResultTv.getText().toString(), true);
                upLoadFile(ht);

            }
        });
        TextView menuItem5 = view.findViewById(R.id.popup_recog);
        menuItem5.setOnClickListener(view1 -> {
            if (popupWindow != null) {
                popupWindow.dismiss();
                index = 4;
                saveNoteCurTracePage();
                DialogUtil dialogUtil = DialogUtil.getInstance();
                dialogUtil.regListener(IatActivity.this);
                dialogUtil.showOCRSelectDialog(IatActivity.this);
            }
        });
        TextView menuItem6 = view.findViewById(R.id.popup_mail);
        menuItem6.setOnClickListener(view1 -> {
            if (popupWindow != null) {
                popupWindow.dismiss();
                index = 6;
                String ht = generateShareHtml(mRecogResultTv.getText() == null ? "" : mRecogResultTv.getText().toString(), true);

                upLoadFile(ht);

            }
        });

        if (mNoteView.penType == mNoteView.TP_PEN) {
            menuItem3.setText(getString(R.string.erase));

        } else {
            menuItem3.setText(getString(R.string.pen));
        }
        popupWindow.setContentView(view);
        popupWindow.setWidth((int) (CommonUtils.getScreenWidth(this) / 5.8));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        return popupWindow;
    }

    private void exitLargeLayout() {
        mNoteView.setVisibility(View.VISIBLE);
        mInnerLayout.setVisibility(View.VISIBLE);
        mLargeLayout.setVisibility(View.GONE);
        mOperationLayout.setVisibility(View.VISIBLE);
        mMenus.setVisibility(View.VISIBLE);
    }


    private String generateShareHtml(String str, boolean img) {

        showShareDialog();
        ArrayList<String> mBase64Img = new ArrayList<>();
        if (img)
            mBase64Img.addAll(generateBase64Img());
        String ht = ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() + ".html";
        ShareUtils.generateHtml(ht, str,
                mBase64Img);
        return ht;
    }

    private ArrayList<String> generateBase64Img() {
        String pa = ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() + "/" + mNotePageIndex + ".png";
        mNoteView.saveCanvasInfo(pa);
        File[] files = new File(ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis()).listFiles();
        LogUtils.printErrorLog("menuItem4", "files.length: " + files.length);
        ArrayList<String> mPathList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getPath().endsWith("png")) {
                mPathList.add(files[i].getPath());
            }
        }
        ArrayList<String> mBase64Img = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for (String path : mPathList) {
            sb.setLength(0);
            sb.append(Constant.PRE_HSUFFIX).append(ShareUtils.imageToBase64(path)).append(Constant.AFTER_HSUFFIX);
            mBase64Img.add(sb.toString());
        }
        return mBase64Img;
    }

    private void sendToMail(String url){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/octet-stream");

        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        boolean bHasK9 = false;
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targeted = new Intent(Intent.ACTION_SEND);
                targeted.setType("application/octet-stream");

                ActivityInfo activityInfo = info.activityInfo;
                targeted.setPackage(activityInfo.packageName);
                targeted.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
                targeted.putExtra(Intent.EXTRA_SUBJECT, "语音记录邮件分享");
                targeted.putExtra(Intent.EXTRA_TEXT, "敬启者：\n下面是语音记录文本内容：\n" + mFileBean.getContent()
                        + " \n\n请打开下面链接查看附件内容：\n" + url);

                if (activityInfo.name.contains("k9")){
                    targetedShareIntents.add(targeted);
                    bHasK9 = true;
                }
                else if(activityInfo.name.contains("gm") || activityInfo.name.contains("mail")) {
                    if(activityInfo.name.contains("qqmail") && activityInfo.name.contains("Upload")){
                        continue;
                    }else {
                        targetedShareIntents.add(targeted);
                    }
                } else {
                    continue;
                }
            }
        }

        if(targetedShareIntents.size() == 0){
            ToastUtils.showLong(this,  getString( R.string.unmailsetting));
            return;
        }else if(!bHasK9){
            ToastUtils.showLong(this,  "系统邮件客户端未进行配置，将用第三方邮件类应用发送");
        }
        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "请选择邮件类应用");
        if (chooserIntent == null) {
            return;
        }
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
        try {
            startActivity(chooserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtils.showLong(this,  getString( R.string.unmailsetting));
        }
    }

    UpLoadDialog upLoadDialog;

    private void showShareDialog() {
        upLoadDialog = new UpLoadDialog(this);
        setFinishOnTouchOutside(false);
        upLoadDialog.setCancel(this.getResources().getString(R.string.cancel), new UpLoadDialog.IOnCancelListener() {
            @Override
            public void onCancel(UpLoadDialog dialog) {
                upLoadDialog.dismiss();
            }
        });

        upLoadDialog.setOnDismiss(new UpLoadDialog.IOnDismisListener() {
            @Override
            public void onDismiss(UpLoadDialog dialog) {
                upLoadDialog.dismiss();
            }
        });
        upLoadDialog.show();
    }

    private void upLoadFile(String path) {
        HashMap<String, RequestBody> map = new HashMap<>();
        File logFile = new File(path);
        if (logFile != null && logFile.length() > 0) {
            map.put(parseMapKey("file", logFile.getName()), parseRequestBody(logFile));
        }
        RetrofitManager.getInstance(this).upLoadFile(map, new RetrofitManager.ICallBack() {
            @Override
            public void successData(String result) {

                LogUtils.printErrorLog(TAG, "String result: " + result);
                Gson gson2 = new Gson();
                PayResultBean payResultBean = gson2.fromJson(result, PayResultBean.class);
                if (TextUtils.equals(payResultBean.getCode(), Constant.SUCCESSCODE)) {
                    if (index == 4) {
                        Bitmap bitmap = ZXingUtils.createQRImage(payResultBean.getUrlBean().getUrl(), 500, 500);
                        upLoadDialog.setUpLoadStatus(bitmap);
                    } else if (index == 6) {
                        upLoadDialog.dismiss();
                        String url = payResultBean.getUrlBean().getUrl();
                        sendToMail(url);
                    } else {
                        Bitmap bitmap = ZXingUtils.createQRImage(payResultBean.getUrlBean().getUrl(), 500, 500);
                        upLoadDialog.setUpLoadStatus(bitmap);
                    }
                    String path = ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() + ".html";
                    hvFileCommonUtils.recursiveDeleteExcludeRoot(HvApplication.mContext, path);
                } else {
                    ToastUtils.show(HvApplication.mContext, payResultBean.getMsg());
                    upLoadDialog.dismiss();
                }
            }

            @Override
            public void failureData(String error) {
                upLoadDialog.dismiss();
            }
        });
    }

    private void freshSentenceList(int currentPage) {
        if (mTempResultList == null) {
            mTempResultList = new ArrayList<AsrEditSentence>();
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
            mSequenceAdapter.setOnCall(this);
        } else {
            mSequenceAdapter.notifyDataSetChanged();
        }
        freshEditPageState(currentPage);
    }



    private void freshEditPageState(int currentPage) {
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

    private void freshFullScreenResultPage() {
        int index = mLargeRecogTv.getPageIdx();
        int count = mLargeRecogTv.getPageCount();
        LogUtils.printErrorLog(TAG, "index: " + index);
        LogUtils.printErrorLog(TAG, "count: " + count);
        if (index == 0 && count > 1) {
            mPreLargeBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mNextLargeBtn.setBackgroundResource(R.drawable.arrow_right_black);
        } else if (index == 0 && count == 1) {
            mPreLargeBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mNextLargeBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else if ((index + 1) == count) {
            mPreLargeBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mNextLargeBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else if (index > 0 && count > 1) {
            mPreLargeBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mNextLargeBtn.setBackgroundResource(R.drawable.arrow_right_black);
        } else {
            mPreLargeBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mNextLargeBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        }
    }

    private void freshResultPage() {
        int index = mRecogResultTv.getPageIdx();
        int count = mRecogResultTv.getPageCount();
        LogUtils.printErrorLog(TAG, "index: " + index);
        LogUtils.printErrorLog(TAG, "count: " + count);
        if (index == 0 && count > 1) {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_black);
        } else if (index == 0 && count == 1) {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else if ((index + 1) == count) {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        } else if (index > 0 && count > 1) {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_black);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_black);
        } else {
            mResultPreBtn.setBackgroundResource(R.drawable.arrow_left_gray);
            mResultNextBtn.setBackgroundResource(R.drawable.arrow_right_gray);
        }
    }

    private void preResultPage(boolean isFullScreen) {

        if (isFullScreen) {
            int curPage = mLargeRecogTv.getPageIdx();
            if (curPage > 0) {
                mLargeRecogTv.pagerPrev();
            }
            freshFullScreenResultPage();
        } else {
            int curPage = mRecogResultTv.getPageIdx();
            if (curPage > 0) {
                mRecogResultTv.pagerPrev();
            }
            freshResultPage();
        }

    }

    private void nextResultPage(boolean isFullScreen) {
        if (isFullScreen) {
            int curPage = mLargeRecogTv.getPageIdx();
            if (curPage < mLargeRecogTv.getPageCount() - 1) {
                mLargeRecogTv.pagerNext();
            }
            freshFullScreenResultPage();
        } else {
            int curPage = mRecogResultTv.getPageIdx();
            if (curPage < mRecogResultTv.getPageCount() - 1) {
                mRecogResultTv.pagerNext();
            }
            freshResultPage();
        }

    }


    /**
     * 开始识别
     *
     * @throws IOException Assets 文件异常
     */
    private void start() throws IOException {
        logger.info("try to start " + mode);
        enterHandwrite(true);
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
                    isRunning = false;
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
        CommonUtils.setOnValidate(true, this);
        logger.info("try to close");
        runOnUiThread(() -> {
            enterHandwrite(false);
        });
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
        onReturn();
    }
    /**
     * 添加一页新的空白页
     *
     * @param index 要插入的空白页的位置
     * @return
     */
    protected boolean AddNoteNewEmptyPage(int index) {
        saveNoteCurTracePage();
        TraPage newPage = new TraPage();
        NoteBaseData.gTraFile.addPage(index, newPage);
        return true;
    }

    protected void updateNoteCurPage() {
        // 刷新便笺的页面
        mNoteView.updateImage(mNotePageIndex);
        updateNotePageInfo(mNotePageIndex, NoteBaseData.gTraFile.getCount());
    }

    protected void updateNotePageInfo(int curPage, int pageCount) {
        mNotePageInfo.setText(curPage + 1 + "/" + pageCount);
        if (curPage == 0 && pageCount > 1) {
            mNotePreBtn.setBackgroundResource(R.drawable.pre_page_2_grey);

        } else if (curPage == 0 && pageCount == 1) {
            mNotePreBtn.setBackgroundResource(R.drawable.pre_page_2_grey);
        } else if ((curPage + 1) == pageCount) {
            mNotePreBtn.setBackgroundResource(R.drawable.pre_page_2);
        } else {
            mNotePreBtn.setBackgroundResource(R.drawable.pre_page_2);
        }
    }

    /**
     * 把当前的便笺信息保存到文件中,制作封面页的缩略图
     *
     * @return 返回是否保存了文件
     */
    protected boolean saveCurTraNoteFile() {
        if (mNoteView == null)
            return true;
        NoteBaseData.gTraFile.setWidth(mNoteView.getWidth());
        NoteBaseData.gTraFile.setHeight(mNoteView.getHeight());
        isTraNoteModified = true;
        if (isTraNoteModified) { // 便笺内容有修改
            NoteBaseData.gTraFile.setLastIndex(mNotePageIndex);
            NoteBaseData.gTraFile.saveTraNote(NoteBaseData.gTraFile.getFilePathName());
            NoteBaseData.gIsTraNoteModified = true;
            isTraNoteModified = false;
        } else {
            NoteBaseData.gTraFile.setLastIndex(mNotePageIndex);
            NoteBaseData.gIsTraNoteModified = true;
            isTraNoteModified = false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (FileBeanUils.isRecoding()) {
            long tempTime = System.currentTimeMillis() - mStartAsrTime + mUsageRecordTime;
            SharedPreferencesUtils.saveUsageTimeSharePrefer(HvApplication.mContext, SharedPreferencesUtils.USAGETIME, tempTime);
            LogUtils.printErrorLog(TAG, "===onSaveInstanceState  tempTime: " + tempTime);
        }

    }

    /**
     * 保存当前页的笔迹信息
     */
    protected void saveNoteCurTracePage() {
        if (mNoteView == null) {
            return;
        }
        if (mNoteView.getVisibility() == View.GONE)
            return;
        // 如果是修改方式且没有被修改则不需要操作
        if (!mNoteView.isModified()) {
            return;
        }
        //修改最近的修改时间
        mFileBean.setModifytime(TimeUtil.getTime(System.currentTimeMillis()));
        DatabaseUtils.getInstance(this).updateModifyTime(mFileBean);
        // 构建便笺页信息
        TraPage page = (TraPage) NoteBaseData.gTraFile.getPage(mNotePageIndex);
        // 笔迹数据
        page.copyTraces(mNoteView.getTraces());
        // 保存以后设置笔迹没有修改

        String path = ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() + "/" + mNotePageIndex + ".png";
        mNoteView.saveCanvasInfo(path);
        mNoteView.setModified(false);
        isTraNoteModified = true;
    }

    /***
     * 跳转到下一页
     */
    public void gotoNoteNextPage() {
        mNoteView.setInputEnabled(false);
        // TODO Auto-generated method stub
        // 保存当前页

        // 如果当前页最后一页
        if (mNotePageIndex == NoteBaseData.gTraFile.getCount() - 1) {
            // 新建页
            LogUtils.printErrorLog(TAG, "NoteBaseData.gTraFile.getPage(mNotePageIndex).getTraces();" + NoteBaseData.gTraFile.getPage(mNotePageIndex).getTraces());
            if (NoteBaseData.gTraFile.getPage(mNotePageIndex).getTraces() == null) {
                String path = ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() + "/" + mNotePageIndex + ".png";
                mNoteView.saveCanvasInfo(path);
            }
            AddNoteNewEmptyPage(mNotePageIndex + 1);
        } else {
            saveNoteCurTracePage();
        }
        mNotePageIndex++;
        // 刷新便笺的页面
        updateNoteCurPage();

        mHandler.postDelayed(() -> {
            mNoteView.setInputEnabled(true);
        }, 800);
    }

    /***
     * 跳转到下一页
     */
    public void jumpToPage(int index) {
        mNoteView.setInputEnabled(false);
        if (index >= NoteBaseData.gTraFile.getCount())
            return;
        mNotePageIndex = index;
        // 刷新便笺的页面
        updateNoteCurPage();
        mHandler.postDelayed(() -> {
            mNoteView.setInputEnabled(true);
            if (FileBeanUils.isRecoding())
                enterHandwrite(true);
        }, 800);
    }

    /**
     * 跳转到上一页
     */
    public void gotoNotePrevPage() {
        // TODO Auto-generated method stub
        mNoteView.setInputEnabled(false);
        // 保存当前页
        saveNoteCurTracePage();
        if (mNotePageIndex == 0) {
            return;
        }
        mNotePageIndex--;
        updateNoteCurPage();

        mHandler.postDelayed(() -> {
            mNoteView.setInputEnabled(true);
        }, 800);
    }


    private class RecognmizeImageAyncTask extends AsyncTask<Void, Void, String> {

        //		@Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mPath = ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() + "/" + mNotePageIndex + ".png";
            DialogUtil.getInstance().showRecogDialog(IatActivity.this);
        }

        //		@Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            String pa = ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() + "/" + mNotePageIndex + ".png";
            mNoteView.saveCanvasInfo(pa);

            StringBuffer stringBuffer = new StringBuffer();
            LogUtils.printErrorLog(TAG, "doInBackground mPath: " + mPath);

            RecoResult recoResult = null;
            PictureReco reco = new PictureReco(IatActivity.this);
            if (mPath.endsWith("png")) {
                recoResult = reco.GetPicatureReco(mPath);
            } else {
                return "";
            }

            if (recoResult.result == null)
                return "";
            FTBlock ftBlock = recoResult.result;
            if (ftBlock.lines == null)
                return "";
            ArrayList<FTLine> lines = ftBlock.lines;
            for (FTLine line : lines) {
                stringBuffer.append(line.GetLineString());
            }
            LogUtils.printErrorLog(TAG, "stringBuffer.toString(): " + stringBuffer.toString());
            return stringBuffer.toString();
        }

        //		@Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            DialogUtil.getInstance().disRecogDialog();
            if (TextUtils.isEmpty(result)) {
                ToastUtils.show(IatActivity.this, getString(R.string.recognize_fail));
                return;
            }
            LogUtils.printErrorLog(TAG, "result: " + result);
            showOcrResultDialog(result);
        }
    }

    //String mTemp;
    private String saveAsrTxt() {
        if (TextUtils.isEmpty(mRecogResultTv.getText().toString().trim()))
            return "";
        File file, file1;
        String path = ConstBroadStr.ROOT_PATH + getResources().getString(R.string.recog_recordtxt);
        file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        String srcTmpFilePath = path + mFileBean.getTitle().replace(" ", "_").replace(":", "_") + ".txt";

        file1 = new File(srcTmpFilePath);
        if (!file1.exists()) {
            try {
                file1.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        CommonUtils.saveAsFileWriter(srcTmpFilePath, mRecogResultTv.getText().toString(), false, false);
        return srcTmpFilePath;
    }

    private void showOcrResultDialog(String result) {
        final CommonDialog dialog = new CommonDialog(IatActivity.this, 0);
        // 设置标题
        dialog.setTitle(getResources().getString(R.string.recognize_result));
        dialog.setDialogWidth((int) getResources().getDimension(R.dimen.navigate_menu_width));

        dialog.setInfo(result);

        dialog.setPositiveButton(getString(R.string.share), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String ht = generateShareHtml(result, false);
                LogUtils.printErrorLog(TAG, "String ht: " + ht);
                upLoadFile(ht);
                //ToastUtils.show(getApplicationContext(), "分享");
            }
        });
        dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        dialog.setNeutralWidthButton(getString(R.string.save_to_local), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                File file, file1;
                String path = ConstBroadStr.ROOT_PATH + getResources().getString(R.string.recog_picpath);
                file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String srcTmpFilePath = path + getString(R.string.recog_txt);

                file1 = new File(srcTmpFilePath);

                if (!file1.exists()) {
                    try {
                        file1.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                CommonUtils.saveAsFileWriter(srcTmpFilePath, result, true, true);
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] strings, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, strings, grantResults);
        LogUtils.printErrorLog("onRequestPermissionsResult", "strings: " + strings.length);
        LogUtils.printErrorLog("onRequestPermissionsResult", "grantResults: " + grantResults.length);
        switch (requestCode) {
            case OPEN_SET_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            LogUtils.printErrorLog("onRequestPermissionsResult", "PERMISSION_GRANTED: ");

                            ToastUtils.show(getApplicationContext(), getString(R.string.unpermission) + strings[1]);
                            return;
                        } else {
                            if (i == 1) {
                                checkUsageTime();

                                LogUtils.printErrorLog("onRequestPermissionsResult", "checkUsageTime: ");
                            }
                        }
                    }
                    //拥有权限执行操作
                } else {
                    LogUtils.printErrorLog("onRequestPermissionsResult", "未拥有相应权限: ");
                    ToastUtils.show(getApplicationContext(), getString(R.string.unpermission));
                }
        }
    }

    /**
     * 进入二值模式
     *
     * @param isEnter 是否进入二值模式，true为进入，false为退出
     */
    public void enterHandwrite(boolean isEnter) {
        Log.e(TAG, "**enterHandwrite, " + isEnter);

    }
    EditText mRenameEd;
    private void showEditDialog(String title, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_edit,null,false);
        builder.setView(view);
        builder.setCancelable(false);


        mRenameEd = view.findViewById(R.id.editTextInfor);
        mRenameEd.getPaint().setAntiAlias(false);
        mRenameEd.setText(title);
        mRenameEd.setSelection(title.length());
        AlertDialog dialog = builder.create();


        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.hideIME();
                dialog.dismiss();
                mSequenceAdapter.notifyDataSetChanged();
            }
        });
        view.findViewById(R.id.btnNeutral).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.hideIME();
                mTempResultList.get(index).setContent(mRenameEd.getText().toString());
                dialog.dismiss();
                freshSentenceList(nPageIsx);
            }
        });
        dialog.show();

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() * 5 / 6;
        dialog.getWindow().setAttributes(lp);
    }

}

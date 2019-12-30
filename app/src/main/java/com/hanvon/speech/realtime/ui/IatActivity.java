package com.hanvon.speech.realtime.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.baidu.ai.speech.realtime.ConstBroadStr;
import com.baidu.ai.speech.realtime.Constants;
import com.baidu.ai.speech.realtime.MiniMain;
import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.MyMicrophoneInputStream;
import com.baidu.ai.speech.realtime.full.connection.Runner;
import com.baidu.ai.speech.realtime.full.download.Result;
import com.baidu.ai.speech.realtime.full.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanvon.speech.realtime.adapter.SequenceAdapter;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.IatResults;
import com.hanvon.speech.realtime.model.IatThread;
import com.hanvon.speech.realtime.model.Recordutil;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.util.EPDHelper;
import com.hanvon.speech.realtime.view.HVTextView;
import com.hanvon.speech.realtime.view.MyNoteView;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.baidu.ai.speech.realtime.full.connection.Runner.MODE_REAL_TIME_STREAM;

public class IatActivity extends Activity implements OnClickListener {

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


    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private SequenceAdapter mSequenceAdapter;
    private Button mBackBtn, mTextBegin, mHomeBtn, mEditBtn, mAudioPlayBtn, mEditPrePageBtn, mEditNextPageBtn, mResultPreBtn, mResultNextBtn;
    private TextView mTimeTv;
    private HVTextView mRecogResultTv;
    private SeekBar mSeekBar;

    private FileBean mFileBean;
    private ListView mEditListView;
    private LocalReceiver localReceiver;
    private View mEditLayout, mResultLayout;
    private boolean isNEW;
    private Handler mHandler;
    private boolean rubberEnableFlag = false;


    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 10;// 每页显示几个
    private ArrayList<Result> mTotalResultList, mTempResultList;
    private Thread mThread = null;
    private byte[] data = null;
    private ImageButton mMenus;
    private MyNoteView myNoteView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO  初始化
        EPDHelper.getInstance().setWindowRefreshMode(getWindow(),EPDHelper.Mode.GU16_RECT);
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back2, bfoOptions);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView(this.getWindow().getDecorView());
        initLayout();
        init();
    }

    private void initView(View view) {
        mMenus = findViewById(R.id.option_menus);
        mBackBtn = (Button) findViewById(R.id.btnReturn);
        mHomeBtn = (Button) findViewById(R.id.btnHome);
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
        myNoteView = view.findViewById(R.id.MyNoteView);
        mEditBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mTextBegin.setOnClickListener(this);
        mAudioPlayBtn.setOnClickListener(this);
        mEditPrePageBtn.setOnClickListener(this);
        mEditNextPageBtn.setOnClickListener(this);
        mResultPreBtn.setOnClickListener(this);
        mResultNextBtn.setOnClickListener(this);
        mMenus.setOnClickListener(this);
        myNoteView.setZOrderOnTop(true);
        myNoteView.setReflushDrityEnable(true);
        myNoteView.setRubberMode(rubberEnableFlag);
        myNoteView.setBackground(bitmap);
    }


    private void init() {
        mHandler = new Handler();
        mTotalResultList = new ArrayList<>();
        mTempResultList = new ArrayList<>();
        localReceiver = new LocalReceiver();

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(localReceiver, new IntentFilter(ConstBroadStr.UPDATERECOG));
        mFileBean = TranslateBean.getInstance().getFileBean();
        isNEW = getIntent().getBooleanExtra("isNew", false);
        if (isNEW)
            return;
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
    public class LocalReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ConstBroadStr.UPDATERECOG)) {
                mRecogResultTv.setText(IatResults.getResultsStr());
                freshResultPage();
                mRecogResultTv.gotoLastPage();
            }
        }
    }


    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            logger.info("onStopTrackingTouch: ");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            logger.info("onStartTrackingTouch: ");
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            logger.info("onProgressChanged: ");
            mTimeTv.setText("当前值 为: " + progress);
        }
    };

    @SuppressLint("ShowToast")
    private void initLayout() {
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

    }

    private void updateEditList() {
        for (int i = 0; i < mTempResultList.size(); i++)
            for (int j = nPageIsx * PAGE_CATEGORY; j < ((nPageIsx + 1) * PAGE_CATEGORY); j++){
                if (TextUtils.equals(mTotalResultList.get(j).getSn(), mTempResultList.get(i).getSn())) {
                    if (TextUtils.isEmpty( mTempResultList.get(i).getResult())) {
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
            IatResults.clearResults();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturn:
                onBackPressed();
                break;
            case R.id.btnHome:
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory("android.intent.category.HOME_HW");
                startActivity(home);
                break;
            case R.id.text_begin:
                Recordutil.getInstance().startRecord(String.valueOf(mFileBean.getCreatemillis()));
                /*new Thread(() -> {
                    // IO 操作都在新线程
                    try {
                        if (isRunning) {
                            logger.info("点击停止");
                            runOnUiThread(() -> {
                                mTextBegin.setText(R.string.text_begin);
                                mAudioPlayBtn.setEnabled(true);
                                mEditBtn.setEnabled(true);
                            });
                            close(false);
                        } else {
                            runOnUiThread(() -> {
                                mTextBegin.setText(R.string.text_end);
                                mAudioPlayBtn.setEnabled(false);
                                mEditBtn.setEnabled(false);
                            });
                            start();
                            pollCheckStop();

                        }
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, e.getClass().getSimpleName() + ":" + e.getMessage(), e);
                    }
                }).start();*/

                /*ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isRunning) {
                                logger.info("点击停止");
                                close(false);
                            } else {
                                runOnUiThread(()->{

                                });

                                start();
                                pollCheckStop();

                            }
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, e.getClass().getSimpleName() + ":" + e.getMessage(), e);
                        }
                    }
                });*/
                break;

            case R.id.text_edit:
                freEditSentenceshPage();
                break;
            case R.id.iat_play:
                if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                    audioStop();
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
                } else {
                    data = Recordutil.getPCMData(ConstBroadStr.AUDIO_ROOT_PATH + mFileBean.getCreatemillis() +
                            ConstBroadStr.AUDIO_PATH);
                    audioPlay();
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_stop));
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
            case R.id.option_menus:
                PopupMenu popupMenu=new PopupMenu(this,v);//1.实例化PopupMenu
                getMenuInflater().inflate(R.menu.option_menu,popupMenu.getMenu());//2.加载Menu资源
                //3.为弹出菜单设置点击监听
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.popup_rubber:
                            rubberEnableFlag = !rubberEnableFlag;
                            myNoteView.setRubberMode(rubberEnableFlag);
                            return true;
                        case R.id.popup_delete:
                            myNoteView.clear(false);
                            return true;
                        case R.id.popup_savePic:
                            myNoteView.saveBitmap();
                            return true;
                        default:
                            return false;
                    }
                });
                popupMenu.show();//4.显示弹出菜单
                break;
            default:
                break;
        }
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

                }
                if (!isRunning) {
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

    public void audioPlay() {
        if (data == null) {
            Toast.makeText(this, "No File...", Toast.LENGTH_LONG).show();
            return;
        }
        if (mThread == null) {
            mThread = new Thread(new IatThread(data, mHandler));
            mThread.start();
        }
    }

    public void audioStop() {
        if (data == null) {
            return;
        }
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
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

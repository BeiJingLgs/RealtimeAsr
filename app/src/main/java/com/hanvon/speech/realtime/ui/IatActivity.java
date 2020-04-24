package com.hanvon.speech.realtime.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import android.view.ViewGroup.LayoutParams;

import androidx.core.app.ActivityCompat;

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
import com.hanvon.speech.realtime.adapter.JumpAdapter;
import com.hanvon.speech.realtime.adapter.SequenceAdapter;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.PackList;
import com.hanvon.speech.realtime.bean.Result.PayResultBean;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.IatResults;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.model.note.NoteBaseData;
import com.hanvon.speech.realtime.model.note.TraFile;
import com.hanvon.speech.realtime.model.note.TraPage;
import com.hanvon.speech.realtime.services.RetrofitManager;
import com.hanvon.speech.realtime.util.CommonUtils;
import com.hanvon.speech.realtime.util.DialogUtil;
import com.hanvon.speech.realtime.util.EPDHelper;
import com.hanvon.speech.realtime.bean.FTBlock;
import com.hanvon.speech.realtime.bean.FTLine;
import com.hanvon.speech.realtime.util.FileUtils;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.MediaPlayerManager;
import com.hanvon.speech.realtime.util.MediaRecorderManager;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.model.PictureReco;
import com.hanvon.speech.realtime.bean.RecoResult;
import com.hanvon.speech.realtime.util.ShareUtils;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;
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
import com.xrz.Rubber;
import com.xrz.SimplePen;


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


import okhttp3.RequestBody;

import static com.baidu.ai.speech.realtime.full.connection.Runner.MODE_REAL_TIME_STREAM;
import static com.hanvon.speech.realtime.util.MethodUtils.parseMapKey;
import static com.hanvon.speech.realtime.util.MethodUtils.parseRequestBody;

public class IatActivity extends BaseActivity implements DialogUtil.NoteChanged{

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
    private Button mTextBegin, mEditBtn, mAudioPlayBtn, mEditPrePageBtn,
            mEditNextPageBtn, mResultPreBtn, mResultNextBtn, mNoteNextBtn, mNotePreBtn, mSuspendRecord;
    private TextView mTimeTv, mNotePageInfo, mRecognizeStatusTv;
    private HVTextView mRecogResultTv;
    private SeekBar mSeekBar;
    public CheckBox mCheckbox;
    private ImageView mRecordStatusImg, mIncreaseVolImg, mDecreaseVolImg;

    private FileBean mFileBean;
    private ListView mEditListView;
    private View mEditLayout, mResultLayout, mWriteLayout, mBottomLayout, mRecordLayout, mIatLayout, mVolumLayout;
    private boolean isNEW, isTips = false;

    private int nPageCount = 0; // 当前分类的页总数
    private int nPageIsx = 0; // 当前显示的页idx
    protected static int PAGE_CATEGORY = 10;// 每页显示几个
    private ArrayList<Result> mTotalResultList, mTempResultList;

    private HandWriteNoteView mNoteView;
    // 当前page页的序号，从0开始
    protected int mNotePageIndex;
    // 打开便笺文件后是否有修改
    protected boolean isTraNoteModified = false;
    private Bitmap mBitmap;
    private Timer mTimer;
    private boolean isSeekbarChaning;
    private boolean isRecording = false;
    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private String mRecordFilePath, mTempPath, mPath;
    private int mAudioOffset;
    private AudioHandler mHandler;
    private long mStartRecordTime, mUsageRecordTime, mStartPlayTime, mUsagePlayTime;
    private int mDuration;
    private VolumeBar mCtlVolBar;
    private AudioManager mAudioManager;
    private RecordReceiver recordReceiver;

    private int index = 0;
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
        mRecogResultTv.getPaint().setAntiAlias(true);
        mEditLayout = findViewById(R.id.edit_layout);
        mEditPrePageBtn = findViewById(R.id.ivpre_page);
        mEditNextPageBtn = findViewById(R.id.ivnext_page);
        mEditBtn = (Button) findViewById(R.id.text_edit);
        mSuspendRecord = findViewById(R.id.suspendRecord);
        mIatLayout = findViewById(R.id.iat_layout);
        mRecognizeStatusTv = findViewById(R.id.recording);
        mEditBtn.setVisibility(View.VISIBLE);
        mEditListView = (ListView) findViewById(R.id.sentence_list);
        mAudioPlayBtn = (Button) findViewById(R.id.iat_play);
        mResultLayout = findViewById(R.id.result_layout);
        mResultPreBtn = findViewById(R.id.result_ivpre_page);
        mResultNextBtn = findViewById(R.id.result_ivnext_page);
        mRecordLayout = view.findViewById(R.id.record_layout);
        mCheckbox = findViewById(R.id.checkbox);
        mRecordStatusImg = findViewById(R.id.suspendImg);
        mIncreaseVolImg = findViewById(R.id.increase_volume);
        mDecreaseVolImg = findViewById(R.id.decre_volume);
        mCtlVolBar = findViewById(R.id.ctrl_vol);
        mVolumLayout = findViewById(R.id.volum_layout);

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
        mIntentDrop = new Intent();
        mIntentDrop.setAction("hanvon.intent.action.disabledropdown");
        mIntentDrop.putExtra("hanvon_disabledropdown", 0);
        mIntentDrop.setComponent(new ComponentName("hanvon.aebr.hvsettings", "hanvon.aebr.hvsettings.ScreencapBroadCastRecevier"));
        sendBroadcast(mIntentDrop);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.printErrorLog(TAG, "==onNewIntent");
    }

    private void initData() {
        mUsageRecordTime = SharedPreferencesUtils.getUsageTimeSharedprefer(this, SharedPreferencesUtils.USAGETIME);
        SharedPreferencesUtils.clear(this, SharedPreferencesUtils.USAGETIME);
        //LogUtils.printErrorLog(TAG, "==mUsagePlayTime: " + mUsageRecordTime);
        recordReceiver = new RecordReceiver();
        IntentFilter mBtFilter = new IntentFilter();
        mBtFilter.addAction(ConstBroadStr.SHOW_BACKLOGO);//ConstBroadStr.UPDATERECOG
        mBtFilter.addAction(ConstBroadStr.UPDATERECOG);
        mBtFilter.addAction(ConstBroadStr.ACTION_HOME_PAGE);
        mBtFilter.addAction(ConstBroadStr.HIDE_BACKLOGO);
        registerReceiver(recordReceiver, mBtFilter);

        mIntentDrop = new Intent();
        mIntentDrop.setAction("hanvon.intent.action.disabledropdown");
        mIntentDrop.putExtra("hanvon_disabledropdown", 1);
        mIntentDrop.setComponent(new ComponentName("hanvon.aebr.hvsettings", "hanvon.aebr.hvsettings.ScreencapBroadCastRecevier"));
        sendBroadcast(mIntentDrop);
    }
    private void init() {
        TAG = getLocalClassName();
        mAudioManager = (AudioManager) HvApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        mCtlVolBar.AdjustVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), true);
        mHandler = new AudioHandler(this);
        mNoteView.setHandler(mHandler);
        mTotalResultList = new ArrayList<>();
        mTempResultList = new ArrayList<>();
        mFileBean = TranslateBean.getInstance().getFileBean();
        if (mFileBean == null)
            mFileBean = new FileBean();
        mDuration = mFileBean.getDuration();
        Log.e("startRecognize", "203 mFileBean.getTime()(): " + mFileBean.getTime());
        if (mDuration == 0) {
            mVolumLayout.setVisibility(View.GONE);
            mSeekBar.setVisibility(View.GONE);
            mTimeTv.setVisibility(View.INVISIBLE);
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

            if(NoteBaseData.gTraFile == null) {
                createNewNote();
                ToastUtils.show(this, getString(R.string.destroy_file));
                return;
            }
            if(NoteBaseData.gTraFile.pages == null) {
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
        Log.e("mRecordFilePath", "mRecordFilePath: " + mRecordFilePath);
        if (TextUtils.isEmpty(mFileBean.getJson()))
            return;
        IatResults.addAllResult(new Gson().fromJson(mFileBean.getJson(), new TypeToken<ArrayList<Result>>() {
        }.getType()));
       /* String result = "不会。删掉同学真的是很大的损失，越长大，越这么觉得。我妈今年五十多了，前几天去一个公司的催债的时候，联系到高中一个“没说过几句话”的同学。他在当地是个小官，我妈时隔几十年给他打了个电话，他帮忙帮的很干脆。我十几岁以后，我妈开始几个月去一次高中同学聚会。按她的说法就是：四十多了，孩子都长大了，有了时间，能在一起聚一聚。我问她那你当年和这些叔叔阿姨很熟么？她说其实这群人里熟悉的就一两个，这是几十年后再慢慢拉起来的同城圈子，但毕竟是高中同学，现在大家在一起吃吃饭打打球挺开心的。我爸经常就跟我说，等你长大了，你会发现小时候同学之间的关系才是最可贵的，因为你们的感情并不惨杂任何利益。其实现在慢慢的也有点感觉了，上了大学，正经朋友越来越难交，你我都心知肚明的场面话反而越来越多。我刚上大学的时候，发朋友圈说想家，一个当年根本不熟，几年没丝毫联系的初中同学，在评论里安慰我之后，给我发了一个小猪佩奇的鬼畜视频，说让我开心开心。暑假里有朋友问我借高三的书，我在群里问了一句，后来是一个基本没说过话的同学给我送来的。前几天上网课需要vpn，我发票圈求助，也是一个高中话都没说过几句的同学，居然私聊我给我发了网址，还顺带特别自然的聊了聊网课的事儿。虽然原本可能并没有太多交际，但一说上话，就会有那种“我和这个人曾经是一个班的同学啊”的亲切感。这种亲切感还会随着时间而慢慢发酵。我后来有想过这是为什么。也许是因为人生中最无忧无虑的日子，一定会随着我们慢慢长大而再不复返，那种每天什么都不用操心的快乐我们一辈子都没机再体验，但又谁都不想失去它，于是只能拼命去寻找寄托。曾经用过的课桌，画的乱七八糟的校服，抄歌词的笔记本，还有我们的同学。等到大家都长大了，除了忙不完的生活琐事，你和你那个交际并不深的同学，心底最单纯的快乐记忆反而是相交的。就算再怎么不相熟，心里也一定有旁人没有的，一室共处好几年才能有的对彼此印象。逗比，好面子，有领导里，独特的口头禅……无论你变成了什么样子，你最初的模样都永远都留在了他们心里。这样的关系，在周围各种礼貌客套的“熟悉的陌生人”的对比下，会变得越来越珍贵。内卷文化最大的毒瘤，就是人人都想做人上人，想的都魔怔了。(本来原文是“东亚内卷文化”的，搞不懂评论区，还是改成“内卷文化”吧，以后我多注意)如果做不到人上人，就把群体细分，分出个“人上人”和“人下人”出来，大搞鄙视链。总共就39所985，还能给你细分出清北、华五、top10、上流985、中流985、中下游985和末流985这种神奇的鄙视链。出国留学圈子里，美硕看不起英硕，英硕看不起澳硕。上海土著里，中环内的看不起中环外的；北京土著里，二环的瞧不上五环的。专业里面，学金融计算机的在鄙视链上游，传统理工科在鄙视链下游，至于文科都快被人忘了。累不累啊，jian不jian啊。套路一：百万医疗险续保需审核给大家举个真实的例子：（不便暴露姓名，下文用她来代替）她给自己的大儿子买了某寿如E康悦医疗保险。后来儿子不幸罹患了白血病，这个保险也成为了她“苦难”中的一点安慰。18年儿子大部分的治疗费都顺利在某寿报销了。可到了第二年，当她再次拿着医疗费用清单去报销时，保险公司不理赔了。原因是，审核不通过，不给续保。关键的是，第二年的保费已经自动扣费一个月了。。。（保险公司表示保费会原路退回）虽然我不是当事人，但是也非常非常愤怒！“某寿如E康悦医疗保险”从此一生黑！我把市场上能找到的类似产品也都找出来了，晒出来让大家一起吐口水扔臭鸡蛋！建议买过保险的朋友，回头找出你们的保险合同，去看看你们当时的健康告知那一页，到底是怎么填写的！很多人买保险，根本不重视健康告知的内容，随随便便，就填了，或者直接让保险销售代写！这是会出问题的！健康告知，很重要！如果身体有些既往病症或者检查异常项，属于健康告知问到的地方，那就要告知。记得买前翻翻自己的病历本！告知之后，保险公司要不要保你，会给出核保结果，要么让你正常投保，要么除外承保或者加费承保，要么拒保你。如果你买保险，很简单，多半被人忽悠了！保险的理赔，一定会看三样东西：一，买的时候，有没有如实告知。没有的话，特别是涉及的出险情况是既往病症的话，直接拒赔！保险法规定的！你到时就是哑巴吃黄莲！二，看保障条款和出险情况是否一致。没有的话，当然赔不了。比如，有人拿着意外险去申请理赔疾病治疗费用，当然赔不了。三，看报案流程是否规范。出险之后，多久之内报案告诉保险公司，是有规定的，按流程来，别大意。否则，坑爹的不是你买了一款垃圾保险，而是垃圾保险也拒赔你！如果想更轻松看懂保险，可以点击阅读【手把手教你买保险】，这是我多年总结的保险课程，经过多次反复的培训实践，半小时就能里里外外搞懂保险问题。作者：匿名用户工资: 新人入职到手工资6k，年终2w，谈金钱属实弟弟。食宿: 饭补油补话费1k多一点，食堂2元，新员工单人间(啥闪闪发亮人才公寓别想了，就破旧家属区，让人找到八九十年代的感觉，跟老头老太为伍，爱了爱了)社保: 公积金2k，补充2k。职业年金具体怎么操作的，疫情影响还没入职不清楚(相当于补充养老保险，从这个词可知是事业编制，但奉劝准备校招的同学不要被编制干扰了自己的求职计划，现在除了政府口都是伪编制，企业就是企业，交社保都并轨了，反正不作死也不开你)。其他米面油之类的工会阿姨式福利就不了解了，我也没成家用不上，阿姨发发慈悲给介绍对象才是福利啊(\"▔□▔)理论作息时间855，但项目忙的话也会加班到七点。(有项目谁不欢迎呢，就怕没项目)————————————————————————回复一下小部分急吼吼地来喊“何不食肉糜”的同志，我只是陈述一下情况，没有啥抱怨吧，还不准人调侃自己单位么。我们这行业也算是比较讲理想讲奉献的了，211本科的硕士起步，跟去社会上工作的同学比，总收入绝对是比较低的(已经啥都算上别再提福利了啊，室友签的外企二十多万还社保交满朝九晚五呢)，涨得也慢，但我乐意干这行呀，准备了七年，不就在等今天么。知乎上为航天人打抱不平的声音很热吧，我们其实平均还不如航天，还好不在北京还能混下去，请人民群众放心。但也是一分耕耘一分收获，没有占谁便宜，拒绝陌生人的阴阳怪气哈。————————————————————————新人只保护两年，之后每月工资还会降低，收入增长主要靠年终奖大幅上调(现在很多国企都实行这样骚操作)。这也意味着如果混日子，虽然不好直接撵你滚蛋，但可以用死工资和很低的年终奖逼你自己圆润离开。所以尬吹国企躺着喝茶看报吃福利的可以歇歇了，国企跟某些标榜战狼热血的私企相比，确实牺牲了一部分效率去换取公平，比如顶着老员工的压力给菜鸡新人设保护价，给非常菜的员工保留个饭碗，用疫情之下过山车一样的网络舆论中比较流行的那句话说:绝对的自由必然造成强者对弱者的剥削，国企是营造了一种强者感觉施展不开，弱者感觉安乐窝的氛围。但国企同样也没有追求绝对的公平，搬砖积极小钱钱大大滴有，你想混日子吃福利，照样有办法搞得你浑身难受。现在的国企领导大多211985毕业，真的很有水平，当然明白解决大锅饭问题的重要性。进去还是要好好干呀，奥利给！作者：弦歌缓缓\n" +
                "链接：https://www.zhihu.com/question/386784209/answer/1165829734\n" +
                "来源：知乎\n" +
                "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。\n" +
                "任教于高中。在15级我带的那个班里的，有个男生，暂称L。L的爸妈，是卖包子的。15级我们班的家长，高知几乎占了一半，而学历在本科以下的，如果我没记错的话，仅有L的父母。这让L觉得非常自卑。L爸爸妈妈卖包子的地方，就在我上班的必经路上。大部分工作日的清晨，我都会到L爸爸妈妈的摊位买两个包子，一荤一素。有时候在家吃过早点了，路过时还是买两个，等到大课间或中午热着吃。不是为了照顾学生家长生意，而是他们家的包子实在太好吃了。反正我妈妈是包不出那么好吃的包子的。有一次家长会之前，L来找我，他说他自己参加家长会。我问原因，他小声说“不想让他们来……来了也是丢人……”其实早有同学和同学家长吃过L家的包子，也知道卖包子的是L的父母，这件事在我们班里并不是秘密。但没有人笑话过他，凡吃过的同学，还都觉得他们家的包子特好吃。可是我还是同意了L独自参加家长会的要求，也暂时没有和他讲任何道理（看到这儿，先别急，还有后文）因为我真的非常理解L。当然，作为成年人的我们，可以说出许多许多的大道理，可以义正辞严地告诉这个孩子什么是对、什么是错。但他真的是不知道对错吗？真的是不懂那些道理吗？不是的。年少时的心，是非常敏感的。我们都从那个年纪走过，大多数人都曾有过那样一颗敏感又脆弱的青涩之心。就算他懂得所有的道理，但他毕竟只是一个十几岁的男孩子。在家长不乏教授、研究员的同学们中间，他的敏感和自卑太正常了。换作你我，也许一样无法逃脱。虽然有的孩子可能就不会在乎，甚至可能会带着同学到爸妈那儿吃包子……但人和人是不一样的。能不在乎，当然最好，但在乎，也并不是错。在我还没想好怎么帮助L放得更轻松之前，我本能地想要保护和成全他那颗敏感的心，而不是站在大人和老师的角度，告诉他：“L，你这样想不对！”但这件事终归是要解决的。我想了很久，策划了一个系列主题班会——让家长们讲讲自己的职业并带着大家体验一番。每一种职业，请一位或一对儿家长代表。安排到L爸爸妈妈那期的时候，为了让时间足够，我和领导申请，把班会时间定在了周末。在我邀请L父母的时候，他们百般推辞，说什么都不愿意去。我费尽口舌，最后还是L和我一起说，他们才终于点头。我为这次班会找L聊的时候，原以为会特别艰难，我心里想了N种方案。没想到，我稍稍一提，他马上同意。他说：“我总得面对这件事。再说别人的家长都来了，就他们不来，想想，我也不高兴的。他们肯定讲不出什么，但我挺想听听的……”班会当天，我们班的讲台桌下，放着我提前准备好的盆、筷子、勺子、酵母、面粉、调料各种，还有韭菜、鸡蛋、肉馅（早上从冰箱拿出来，放到了装冰淇淋的袋子里。我当时很担心会坏，但到用上的时候，肉馅还完好）L的父母上台后，果然讲不出什么，只很不好意思地说自己就是卖包子的，没什么好讲的。没关系，我有备案。我仔细地针对他们每天的工作时间、工作程序、包包子的手艺是从什么时候学会的、卖了多久的包子、客源是否稳定等细节逐一提问。然后，我拿出提前和他们预订并付清全款、请他们在班会当天带来的当日清晨蒸好的包子，分给在座的同学和家长。尽管做了保温措施，但包子还是有些凉了，可是大家尝了，都夸好吃。我又问在座的同学和家长，有谁能独立包一顿包子，只有三两位家长举手。我半开玩笑地问举手家长的孩子，家里包的包子好吃，还是L爸爸妈妈的包子好吃，孩子都说：“当然是L家的包子好吃”我说：“那我们今天，就请L的爸爸妈妈，教我们大家包包子，好不好？”大家一阵欢呼。我们把桌子拼到一起，拿出我提前准备的东西，跟着L的爸爸妈妈一起，和面、发面、调馅儿、包，一步一步来，尽量让每个人都参与到，包括从来没和爸爸妈妈一起做过这些的L。这次疫情居家期间，我已能够独立完成一顿饺子和包子。而调馅儿的“秘诀”，就是当年L爸爸妈妈根据我的口味给我的。如果正在看的朋友里，有独立包过包子的，会知道这个过程真的挺不容易的。只说揉面、打馅儿，就都可以算体力活。而发面和包包子，又可以算技术活。包子包好了，我们小心带着，一行人浩浩荡荡，到住在学校家属院的某同学家去蒸。蒸熟后，每个人都吃得特别香。吃完后，我给大家讲了我当年上大学时每天都要吃的那家鸡蛋灌饼。大学的最后两年，每天早上，我都骑车到一个固定的鸡蛋灌饼摊儿买两个鸡蛋灌饼。其实一个也够，但因为太好吃了，所以必须吃两个才过瘾。卖鸡蛋灌饼的是一对儿和善又憨厚的年轻夫妻。因为我天天去，所以他们认识我。有时候，他们会送我一杯热豆浆。我不要，他们就直接扔到我车筐里。我给钱，他们说什么都不要。有时候，天特别冷，我摘手套拿钱的时候，他们会说：“算了算了，别摘了，今天太冷，钱再说”。有一次下雪，他们还直接和我说：“今天不要钱”。虽然我从没有真的赊过账，但这一句话，真的能一下暖到人心窝里。后来，我大学毕业，那家鸡蛋灌饼摊儿也没有了，那么好吃的鸡蛋灌饼，吃不到了。我非常想念那对儿夫妇，想念他们的鸡蛋灌饼。那么多个清晨，都是他们做的鸡蛋灌饼，温暖了我的胃和心，给我注入满满的活力。哪怕有时吃过了早餐，我还是会买。因为真的好好吃。吃一份鸡蛋灌饼，已经成了我每天起床的念想和动力。我告诉大家，没了那家鸡蛋灌饼，我已经好多年没有吃过那么香的早餐了，直到我又遇到了L爸爸妈妈的包子。L爸爸妈妈的包子，现在在我心里的地位，就等同于当年的鸡蛋灌饼。我特别感谢他们，感谢他们填补了我这几年每天清晨的一个略带失落的空白，感谢他们能让我每天早上都吃到那么香的早餐，胃满意足地开始一天的工作。我说：“我相信，和我一样对他们心怀感激的，还会有很多人——很多每天早上都同样靠着L爸爸妈妈的包子唤醒新一天的人。刚才我们体验过了，L爸爸妈妈的工作非常辛苦。同时，他们的工作也非常有意义。他们牺牲了自己每夜的好梦，成全了那么多人清晨的一顿热热乎乎的可口早饭。他们非常平凡，但也真的很了不起。都说众口难调，可是他们就偏偏有本事能让大多数吃过他们包子的人都赞不绝口甚至念念不忘，我真的很佩服他们，也很崇拜他们。今天，也谢谢L的爸爸妈妈教我们大家包包子，让我们有机会体验这份平时虽然常见但对它真正的了解却几乎没有的工作。他们头一回当老师，就能让每个第一次上手的人都学得会，单这一点，就不比我平时给大家讲课容易……”（我有根据现场视频和录音记录下的工作日记，这段话是从工作日记中复制下来的）我注意到，L掉眼泪了。从那儿以后，L经常会带包子给大家吃，去L家吃包子的同学和家长也愈来愈多。和我一样，不是刻意为了照顾他们家生意，他们家的生意并不需要我们照顾，就是因为好吃。L的心结，就此打开。当然，每个孩子，每个家庭，每件事情，都有它独一无二的地方。这个方法，可能只适于L，只适合这件事。但是，我想通过这故事分享的是什么呢？一、不要急于告诉孩子对错或者给孩子讲大道理，也不要用刻板的价值观和道德观轻易定义孩子的心态和行为。所谓“大道理人人都懂，小情绪难以自控”。会因为父母的职业产生自卑心的孩子，大多数都不是一个坏孩子，而只是一个敏感的孩子。他们不是虚荣，更不是不孝顺，他们只是害怕，害怕别人嘲笑自己，更害怕别人嘲笑自己的父母。这种敏感，我们每一个人成长的路上，或多或少都体验过。我们首先应该给予孩子理解，告诉他，你这种想法不是错，非常正常。二、在合适的时机，以适合的方式，尝试让孩子了解和体验父母的工作。很多事情，百闻不如一见，百见不如一干。比如卖鸡蛋饼，干过一次，他才有可能体会到，做出一个让大多数人都觉得好吃、钱没白花，今天吃了明天还想吃的鸡蛋饼，也不是一件很容易的事情。看似简单的工作，想干好，其实也不容易，也有它不可忽视的技术含量在其中。三、让孩子了解到这份工作的意义。每一份工作都有它的意义。比如卖鸡蛋饼，就是让很多人在来不及自己做饭的时候，也能吃上热乎、干净的饭。一份看似没那么体面的工作，其实意义同样不可轻视。同时，这份工作还能支撑这个家的日常生活，对我们这个小家来说，也是意义重大。爸爸妈妈可能没有那么高的学问，没办法去做一份办公室里的工作，但是我们也很喜欢我们现在的工作，一样每天都在竭尽全力认认真真地想要把它做得更好。四、最好不要告诉孩子：我们做着这么辛苦的工作就是为了供你上学，你一定要好好学习，以后有出息，不干和我们一样的工作。爸爸妈妈首先要对自己的工作有热爱、有尊重，不要因为这份工作辛苦，就把它转化成对孩子的一种无形的压力，也不要在孩子面前贬低自己的工作（比如告诉孩子“我们现在做这份工作，就是为让你以后不做这样的工作”）五、适度保护孩子的敏感和自尊引导孩子对父母的工作多一份了解，让他不为此感到自卑，可能是一个很漫长的过程。或许你说了再多、做了再多，他还是无法释怀。这种时候，要多理解，多共情。如第一条所说，不要轻易去下定义，比如：“你有这样的想法，你就是个什么什么样的孩子”。要给他时间，给他空间。因为包括我们成年人都一样，有时候想通一件事情并没有那么容易，需要一个能真正击中我们的点，但这个点有时候可遇不可求。如果孩子一时间无法转过这个弯，我们可以和他一起商量一个办法，比如答应他不在学校旁边卖鸡蛋饼等等。不要因此觉得孩子“伤父母心”，我们要相信他早晚有一天能想明白，只是需要多一点理解、包容和时间。真诚地希望每一位父母、老师，在教育孩子的同时，也能给成长中的孩子更多的理解和耐心，悦纳他们的“不完美”，也教会他们悦纳自己的“不完美”：在暂时无法变得更完美之前，能和自己的不完美和平共处。毕竟，一个悦纳自己的人，才能悦纳别人，悦纳生活，悦纳世界。群里一小伙说：王哥你喊了两年大危机，你看危机来了是不是有种“噫！我中了！”的快乐？我：尼玛，这份快乐的剂量有点上头啊。每年投资机构都要给第二年的互联网项目募资，18年募给19年的钱总量腰斩（个人小道消息），所以当年我就全面转入悲观情绪。没想到19年一打听，20年再腰斩。我擦咧，我去实业苟着吧。互联网这堆项目花里胡哨的，不知道能领几个月工资。衣食住行总不会出错的，钱少点无所谓啦。结果疫情来了。以前实业和互联网的起伏彼此错开。01年美帝新经济崩盘，传导到国内，那时候一群人正在鼓吹“给每粒沙子分配IP地址”。一巴掌呼下来，本土工程师的工资甚至跌到3000，和工厂熟练工差不多。而01年也是海外实体产业大举转移至中国的一年。欧美日韩资本通过香港人台湾人疯狂进入中国大陆，广东全省都在做外贸，广交会成为世界级展会，到处是工厂，会讲外语的人地位奇高。等到14/15年实体产业大规模失血时，互联网正是最热闹的时候。应届生有些甚至能拿到1w5的起薪，头衔是到今天也没搞懂的产品经理。一份PPT就能搞到几百上千万，这里的喧嚣掩盖了实体死亡的静悄悄。本质上，这就是金融资本拉高后对实体经济的挤出效应。人生在世靠康波，实体和虚拟也有各自的波段，顺势而为就好。但这次不一样，这次俩波都撞墙了。产能过剩，资本过剩，甚至劳动力也过剩。九十年代末，国内经济怪象丛生。林毅夫说这是双重过剩下的恶性循环。劳动力过剩与生产过剩，货卖不上价，人卖不上价，资源你又总体短缺，不就越繁荣越紧张么。好了，现在三重过剩，资本也过剩了。缺产能吗？中国的一般社会消费品产能吓死人，分分钟让全世界每个人都穿上新鞋新衣服，骑着小电驴玩着智能手机，每个人住上小公寓；缺人吗？照这个趋势，烧烤摊迟早要上硕士文凭，现在还不上是因为考研考博还能吸纳待业青年，70后高积蓄留下的底子还在；缺资本吗？来，你要几万亿？三重过剩下的恶性循环，已经在过程中了。大家天天在知乎上对肛，诞生很多名词。但归根结底，就是要为过剩找出口，不是内就是外。要么国家干预，全非洲人民明天开始，莫名其妙地只能用上中国货了。那产能、劳动力、资本统统有出路，搞不好是五十年的繁荣。可能吗？美帝表示，我产能是不过剩了，论劳动力过剩我不弱于你，论资本过剩我是你十倍。你在想什么，pong友？那只能是国家干预，大规模基础建设与内陆投资。再来一次县县有三甲医院有重点高中有物流支线大仓，村村有医疗站有物流点有生产队。五 小 工 业每个村、每个镇、每个县，都是独立的水密隔舱。平时可以接纳城市资本投资，资本获得收益，地区获得增长。但一旦城市资本出现严重波动，每一层都能保证一定的劳动力吸纳，减缓工业集群衰败，为中央调控争取时间。不好意思最近把温铁军和萨缪尔阿明又读了一遍，脑子里印象太深刻了，绕不开了。这条出路是可行的，但是，要等。等中央确定全面推行这个方向。当下这段时间，我们都在这个夹缝中求生存。外贸依存度高的、投资拉动的，崩一波无法避免。在这个夹缝时间里，能基本维系外贸的工业集群、维系战略产业的持续投入，让这两驾马车别把内需这驾给扯住了，就是胜利。真的只是刚刚开始。到村村都开了快手和淘宝，小镇青年嫌城市生活难受也不赚钱时，转机就到了。诶？那不就是日本农协嘛。不会。删掉同学真的是很大的损失，越长大，越这么觉得。我妈今年五十多了，前几天去一个公司的催债的时候，联系到高中一个“没说过几句话”的同学。他在当地是个小官，我妈时隔几十年给他打了个电话，他帮忙帮的很干脆。我十几岁以后，我妈开始几个月去一次高中同学聚会。按她的说法就是：四十多了，孩子都长大了，有了时间，能在一起聚一聚。我问她那你当年和这些叔叔阿姨很熟么？她说其实这群人里熟悉的就一两个，这是几十年后再慢慢拉起来的同城圈子，但毕竟是高中同学，现在大家在一起吃吃饭打打球挺开心的。我爸经常就跟我说，等你长大了，你会发现小时候同学之间的关系才是最可贵的，因为你们的感情并不惨杂任何利益。其实现在慢慢的也有点感觉了，上了大学，正经朋友越来越难交，你我都心知肚明的场面话反而越来越多。我刚上大学的时候，发朋友圈说想家，一个当年根本不熟，几年没丝毫联系的初中同学，在评论里安慰我之后，给我发了一个小猪佩奇的鬼畜视频，说让我开心开心。暑假里有朋友问我借高三的书，我在群里问了一句，后来是一个基本没说过话的同学给我送来的。前几天上网课需要vpn，我发票圈求助，也是一个高中话都没说过几句的同学，居然私聊我给我发了网址，还顺带特别自然的聊了聊网课的事儿。虽然原本可能并没有太多交际，但一说上话，就会有那种“我和这个人曾经是一个班的同学啊”的亲切感。这种亲切感还会随着时间而慢慢发酵。我后来有想过这是为什么。也许是因为人生中最无忧无虑的日子，一定会随着我们慢慢长大而再不复返，那种每天什么都不用操心的快乐我们一辈子都没机再体验，但又谁都不想失去它，于是只能拼命去寻找寄托。曾经用过的课桌，画的乱七八糟的校服，抄歌词的笔记本，还有我们的同学。等到大家都长大了，除了忙不完的生活琐事，你和你那个交际并不深的同学，心底最单纯的快乐记忆反而是相交的。就算再怎么不相熟，心里也一定有旁人没有的，一室共处好几年才能有的对彼此印象。逗比，好面子，有领导里，独特的口头禅……无论你变成了什么样子，你最初的模样都永远都留在了他们心里。这样的关系，在周围各种礼貌客套的“熟悉的陌生人”的对比下，会变得越来越珍贵。内卷文化最大的毒瘤，就是人人都想做人上人，想的都魔怔了。(本来原文是“东亚内卷文化”的，搞不懂评论区，还是改成“内卷文化”吧，以后我多注意)如果做不到人上人，就把群体细分，分出个“人上人”和“人下人”出来，大搞鄙视链。总共就39所985，还能给你细分出清北、华五、top10、上流985、中流985、中下游985和末流985这种神奇的鄙视链。出国留学圈子里，美硕看不起英硕，英硕看不起澳硕。上海土著里，中环内的看不起中环外的；北京土著里，二环的瞧不上五环的。专业里面，学金融计算机的在鄙视链上游，传统理工科在鄙视链下游，至于文科都快被人忘了。累不累啊，jian不jian啊。套路一：百万医疗险续保需审核给大家举个真实的例子：（不便暴露姓名，下文用她来代替）她给自己的大儿子买了某寿如E康悦医疗保险。后来儿子不幸罹患了白血病，这个保险也成为了她“苦难”中的一点安慰。18年儿子大部分的治疗费都顺利在某寿报销了。可到了第二年，当她再次拿着医疗费用清单去报销时，保险公司不理赔了。原因是，审核不通过，不给续保。关键的是，第二年的保费已经自动扣费一个月了。。。（保险公司表示保费会原路退回）虽然我不是当事人，但是也非常非常愤怒！“某寿如E康悦医疗保险”从此一生黑！我把市场上能找到的类似产品也都找出来了，晒出来让大家一起吐口水扔臭鸡蛋！建议买过保险的朋友，回头找出你们的保险合同，去看看你们当时的健康告知那一页，到底是怎么填写的！很多人买保险，根本不重视健康告知的内容，随随便便，就填了，或者直接让保险销售代写！这是会出问题的！健康告知，很重要！如果身体有些既往病症或者检查异常项，属于健康告知问到的地方，那就要告知。记得买前翻翻自己的病历本！告知之后，保险公司要不要保你，会给出核保结果，要么让你正常投保，要么除外承保或者加费承保，要么拒保你。如果你买保险，很简单，多半被人忽悠了！保险的理赔，一定会看三样东西：一，买的时候，有没有如实告知。没有的话，特别是涉及的出险情况是既往病症的话，直接拒赔！保险法规定的！你到时就是哑巴吃黄莲！二，看保障条款和出险情况是否一致。没有的话，当然赔不了。比如，有人拿着意外险去申请理赔疾病治疗费用，当然赔不了。三，看报案流程是否规范。出险之后，多久之内报案告诉保险公司，是有规定的，按流程来，别大意。否则，坑爹的不是你买了一款垃圾保险，而是垃圾保险也拒赔你！如果想更轻松看懂保险，可以点击阅读【手把手教你买保险】，这是我多年总结的保险课程，经过多次反复的培训实践，半小时就能里里外外搞懂保险问题。今天中午老爸和我说60年代物质匮乏时代所谓的人造肉——就是国企单位食堂煮肉时把这层沫子瓢出来，晒干，当福利发给职工，职工拿回家放上调料去腥吃。成分上说，这些就是猪的血沫，蛋白质营养充足。但味道腥骚，所以要去腥。在物质匮乏时代，这种人造肉无疑是精华。但现在，倒马桶里都得赶快冲走，不然味道太大了。———分割线————知乎杠精真的是见识了，算了，就当我扯淡好吧？你们都是世界中心，你们眼睛一闭世界就陷入黑暗了。认输，关闭评论了。万达是因为抢了国家的生意而受到限制吗？是原因之一，但是远远不止这一件事。万达从13年开始在全球投资，其中主要的有以下几项，主要分布影视，文化，和地产项目：1、万达以10亿美元收购美国北欧院线项目，以及以12亿美元收购美国卡麦克院线项目。2、3.2亿英镑收购英国游艇俱乐部。6.5亿美元收购美国铁人公司。入股马竞俱乐部。3、9亿美金建芝加哥第三高楼。20亿英镑在伦敦建城市综合体。30亿欧元在巴黎建设娱乐城。100亿美金在印度建设世界级产业园区。所有的投资项目，没有一项与核心科技技术有关。这显然，与我国鼓励企业该做的事情背道而驰。万达自13年投资海外项目到被叫停，短短几年时间，海外投资高达2200亿。而万达在国内银行的贷款是多少呢？2000亿。这也就是，他在拿着中国银行的钱，去搞海外投资。然而，在2015年，王总在哈佛大学公开演讲承认，海外投资是转移资产吗？是，但是关键是看他合法不合法。王总说这个话对吗？也对。他的投资是合法的。但是，他是借的国家的钱，借的中国银行的钱，而你现在在帮着国外建设产业园区。那国家就要你还钱了。打个比方，一个人借了你的钱，去你竞争对手那投资项目，而不在你这投资发展，你会开心吗？而且，王总可不是一般的企业家。他是首富，是中国顶级企业家的代表之一啊，他在这公开表示海外投资是合法转移财产，那别的企业家怎么想？会起到什么样的效应，这样的事情，放到哪个国家，国家都不会坐视不管。看到一句评论，说的挺有意思。这个社会最大的公平就在于：当一个人的财富大于自己认知的时候，这个社会有100种方法收割你，直到让你的认知和财富相匹配为止。”所以，万达会破产吗？不会。但是，首富的地位，一个亿小目标的风光，应该是一去不复返了。————————————————那个，前段时间写的回答，不知道今天怎么突然被翻牌子了。以上观点是之前看过吴晓波的视频，知乎刷到这个话题，然后大概回忆整理成文字，自己又查了点具体信息。在众多的观点中，以上是我最认可的观点，所以拿出来讨论分享。";
        if (HvApplication.ISDEBUG)
            mRecogResultTv.setText(result);
        else*/
            mRecogResultTv.setText(mFileBean.getContent());

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

    private void initPermissions() {
        if (lacksPermission()) {//判断是否拥有权限
            ActivityCompat.requestPermissions(this, permissions, OPEN_SET_REQUEST_CODE);
        } else {
            checkUsageTime();
        }
    }

    @Override
    public void notifyNoteChanged(int i) {
        jumpToPage(i);
    }

    protected class RecordReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ConstBroadStr.SHOW_BACKLOGO)) {
                LogUtils.printErrorLog(TAG, "===SHOW_BACKLOGO");
                if (isRecording) {
                    mRecordStatusImg.setBackgroundResource(R.drawable.ps_play);
                    pauseRecognize();
                }
                stopPlayRecord();
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.UPDATERECOG)) {
                if (mNoteView.canBeFresh()) {
                    freshRecogContent();
                }
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.ACTION_HOME_PAGE)) {
                saveAndExitActivity();
            } else if (TextUtils.equals(intent.getAction(), ConstBroadStr.HIDE_BACKLOGO)) {
                LogUtils.printErrorLog(TAG, "HIDE_BACKLOGO");
                //updateNoteCurPage();
            }
        }
    }

    private void freshRecogContent() {
        mRecogResultTv.setText(IatResults.getResultsStr());
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
                case 1:
                    if (isRecording) {
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
                                    mTimeTv.setText(TimeUtil.calculateTime(0) + "/" + TimeUtil.calculateTime((int)(mFileBean.getTime() / 1000)));
                                } else {
                                    mTimeTv.setText(TimeUtil.calculateTime((int)((System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int)(mFileBean.getTime() / 1000)));
                                }
                            }
                        }
                    }
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
            mTimeTv.setText(TimeUtil.calculateTime((int)(mUsagePlayTime / 1000)) + "/" + TimeUtil.calculateTime((int)(mFileBean.getTime() / 1000)));

            LogUtils.printErrorLog(TAG, "===onStopTrackingTouch (): " );
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
                    mTimeTv.setText(TimeUtil.calculateTime(0) + "/" + TimeUtil.calculateTime((int)(mFileBean.getTime() / 1000)));
                    //LogUtils.printErrorLog(TAG, "seekBar.getProgress(): ");
                    //LogUtils.printErrorLog(TAG, "mNoteView.getCurrentPosition(): ");
                }
            }
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
            mWriteLayout.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
            mNoteView.setVisibility(View.VISIBLE);
            updateNoteCurPage();
            nPageIsx = 0;
            updateEditList();
            mRecogResultTv.setText(IatResults.getResultsStr());
            mRecogResultTv.getPageCount();
        } else {
            saveAndExitActivity();
            finish();
        }
    }

    private void saveAndExitActivity() {
        DatabaseUtils databaseUtils = DatabaseUtils.getInstance(this);
        String con = mRecogResultTv.getText() == null ? "" : mRecogResultTv.getText().toString();
        if (!TextUtils.equals(con, mFileBean.getContent())) {
            mFileBean.setContent(con);
            mFileBean.setJson(JSON.toJSONString(IatResults.getResults()));
            mFileBean.setModifytime(TimeUtil.getTime(System.currentTimeMillis()));
            mFileBean.setDuration(mDuration);
            databaseUtils.updateByContent(mFileBean);
        }
        // 保存笔迹
        saveNoteCurTracePage();
        saveCurTraNoteFile();
        exitActivity();
    }

    private void stopRecognize() {
        if (isRecording) {
            mRecognizeStatusTv.setText(R.string.recognizing);
            mRecordStatusImg.setBackgroundResource(R.drawable.ps_pause);
            uploadUsageTime();
            close(false);
            MediaRecorderManager.getInstance().stop();
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mTempPath))
                        return;
                    Log.e("startRecognize", "file.exists(): " + hvFileCommonUtils.isFileExist(mTempPath));
                    if (hvFileCommonUtils.isFileExist(mTempPath)) {
                        FileUtils.copyRecordFile(mRecordFilePath, mTempPath);
                    }
                }
            });
            isRecording = false;
        }
    }


    private void pauseRecognize() {
        if (isRecording) {
            mUsageRecordTime = System.currentTimeMillis() - mStartRecordTime + mUsageRecordTime;
            close(false);
            mRecognizeStatusTv.setText(getString(R.string.pausing));
            MediaRecorderManager.getInstance().stop();
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mTempPath))
                        return;
                    Log.e("startRecognize", "file.exists(): " + hvFileCommonUtils.isFileExist(mTempPath));
                    if (hvFileCommonUtils.isFileExist(mTempPath)) {
                        FileUtils.copyRecordFile(mRecordFilePath, mTempPath);
                    }
                }
            });
            isRecording = false;
        }
    }

    private void continueRecognize() {
        if (!isRecording) {
            CommonUtils.setOnValidate(false, this);
            String tmpName = mFileBean.getCreatemillis();
            if (isNEW) {
                if (mCheckbox.isChecked()) {
                    mFileBean.mSd = "sd";
                }
            }
            createFile(tmpName);
            isRecording = true;
            mRecognizeStatusTv.setText(getString(R.string.recognizing));
            mStartRecordTime = System.currentTimeMillis();
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

            if (hvFileCommonUtils.isFileExist(mRecordFilePath)) {
                MediaRecorderManager.getInstance().start(mTempPath);
            } else {
                MediaRecorderManager.getInstance().start(mRecordFilePath);
            }
            ToastUtils.show(getApplicationContext(), getResources().getString(R.string.startrecording));
        }
    }

    private void exitActivity() {
        stopRecognize();
        stopPlayRecord();
        IatResults.clearResults();
        unregisterReceiver(recordReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_option_menus:
                Log.e("onClick", "btn_option_menus");
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
                initPermissions();

                break;
            case R.id.text_edit:
                if (isRecording) {
                    ToastUtils.show(getApplicationContext(), getResources().getString(R.string.tips1));
                    return;
                }
                freEditSentenceshPage();
                break;
            case R.id.iat_play:
                if (isRecording) {
                    ToastUtils.show(getApplicationContext(), getResources().getString(R.string.tips2));
                    return;
                }
                if (TextUtils.equals(mAudioPlayBtn.getText(), getResources().getString(R.string.iat_stop))) {
                    if (mTimer != null)
                        mTimer.cancel();
                    CommonUtils.setOnValidate(true, this);
                    MediaPlayerManager.getInstance().stop();
                    mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
                    mUsagePlayTime = System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime;
                } else {
                    Log.e("mRecordFilePath", "mRecordFilePath: " + mRecordFilePath);
                    if (mRecordFilePath == null) {
                        ToastUtils.show(getApplicationContext(), getResources().getString(R.string.tips3));
                        return;
                    }
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
                preResultPage();
                break;
            case R.id.result_ivnext_page:
                nextResultPage();
                break;
            case R.id.suspendRecord:
                stopRecognize();
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
                if (!isRecording) {
                    if (!WifiUtils.isWifiOpened()) {
                        DialogUtil.getInstance().showNetWorkDialog(this);
                        return;
                    }
                    if (WifiUtils.getWifiConnectState(HvApplication.getContext()) == NetworkInfo.State.DISCONNECTED) {
                        ToastUtils.show(this, getString(R.string.checkNeterror));
                        LogUtils.printErrorLog(TAG, "getWifiConnectState: 网络未连接");
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
                //pageJump();
                saveNoteCurTracePage();
                DialogUtil dialogUtil = DialogUtil.getInstance();
                dialogUtil.regListener(this);
                dialogUtil.showJumpDialog(this, mNotePageIndex);
                break;
            default:
                break;
        }
    }

    public void adjustVolume(boolean up) {
        if (up) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        } else {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }
        mCtlVolBar.AdjustVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), true);;
    }

    private void stopPlayRecord() {
        if (mTimer != null)
            mTimer.cancel();
        mUsagePlayTime = 0;
        mTimeTv.setText(TimeUtil.calculateTime(0) + "/" + TimeUtil.calculateTime((int)(mFileBean.getTime() / 1000)));
        mAudioPlayBtn.setText(getResources().getString(R.string.iat_play));
        MediaPlayerManager.getInstance().stop();
    }

    private void playRecord() {
        mVolumLayout.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.VISIBLE);
        mTimeTv.setVisibility(View.VISIBLE);
        if (!MediaPlayerManager.getInstance().isPlaying()) {
            mAudioOffset = mSeekBar.getProgress();
            Log.e("playRecord", "mAudioOffset: " + mAudioOffset);
            MediaPlayerManager.getInstance().play(mRecordFilePath, mAudioOffset, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayRecord();
                    mSeekBar.setProgress(0);
                }
            });//开始播放
            mDuration = MediaPlayerManager.getInstance().getDuration();//获取音乐总时间
            if (mDuration == 0)
                return;
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
                                    mTimeTv.setText(TimeUtil.calculateTime((int)((System.currentTimeMillis() - mStartPlayTime + mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int)(mFileBean.getTime() / 1000)));
                                }
                            });

                        }
                    }
                }
            }, 0, 1000);

        }
    }


    private void startRecognize() {
        if (!isRecording) {
            CommonUtils.setOnValidate(false, this);
            String tmpName = mFileBean.getCreatemillis();
            if (isNEW) {
                if (mCheckbox.isChecked()) {
                    mFileBean.mSd = "sd";
                }
            }
            createFile(tmpName);
            isRecording = true;
            mStartRecordTime = System.currentTimeMillis();
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

            if (hvFileCommonUtils.isFileExist(mRecordFilePath)) {
                MediaRecorderManager.getInstance().start(mTempPath);
            } else {
                MediaRecorderManager.getInstance().start(mRecordFilePath);
            }
            ToastUtils.show(getApplicationContext(), getResources().getString(R.string.startrecording));
        }
    }

    private void freEditSentenceshPage() {
        saveNoteCurTracePage();
        if (IatResults.getResults().size() == 0) {
            ToastUtils.show(this, getString(R.string.noEditText));
            return;
        }
        mEditLayout.setVisibility(View.VISIBLE);
        mResultLayout.setVisibility(View.GONE);
        mWriteLayout.setVisibility(View.GONE);
        mBottomLayout.setVisibility(View.GONE);
        mNoteView.setVisibility(View.GONE);
        mTotalResultList.clear();
        mTotalResultList.addAll(IatResults.getResults());
        nPageCount = getTotalqlPageCount(mTotalResultList.size());
        initPage();
        freshSentenceList(nPageIsx);
    }

    public long getCurrrentRecordTime() {
        return mFileBean.getTime() + System.currentTimeMillis() - mStartRecordTime;
    }

    private void uploadUsageTime() {
        DialogUtil.getInstance().showWaitingDialog(this);
        HashMap<String, String> map2 = new HashMap<>();
        long tempTime = System.currentTimeMillis() - mStartRecordTime + mUsageRecordTime;
        Log.e("startRecognize", "tempTime(): " + tempTime);
        mFileBean.setTime(mFileBean.getTime() + tempTime);
        mTimeTv.setText(TimeUtil.calculateTime(((int)(mUsagePlayTime) / 1000)) + "/" + TimeUtil.calculateTime((int)(mFileBean.getTime() / 1000)));
        if ((int)(mUsagePlayTime) / 1000 > 0) {
            mSeekBar.setVisibility(View.VISIBLE);
            mTimeTv.setVisibility(View.VISIBLE);
        }
        Log.e("startRecognize", "getCurrrentRecordTime(): " + getCurrrentRecordTime());
        DatabaseUtils.getInstance(HvApplication.getContext()).updateTime(mFileBean);

        map2.put("duration", String.valueOf(tempTime / 1000));
        RetrofitManager.getInstance(this).submitUsedTime(map2, new RetrofitManager.ICallBack() {
            @Override
            public void successData(String result) {
                DialogUtil.getInstance().disWaitingDialog();
                Gson gson2 = new Gson();
                VerificationResult c = gson2.fromJson(result, VerificationResult.class);
                if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
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


    private void checkUsageTime() {

        if (!isRecording) {
            if (mEditLayout.getVisibility() == View.VISIBLE) {
                onReturn();
            }
            DialogUtil.getInstance().showWaitingDialog(this);
            RetrofitManager.getInstance(this).getAccountPacks(Constant.PAGE_INDEX + "", Constant.PAGE_SIZE + "", "desc", new RetrofitManager.ICallBack() {
                @Override
                public void successData(String result) {
                    DialogUtil.getInstance().disWaitingDialog();
                    Gson gson2 = new Gson();
                    PackList c = gson2.fromJson(result, PackList.class);
                    Log.e("A", "onResponse: " + "c.getShopType().size(): " + c.getPackBean().size());
                    if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE) && (c.getPackBean().size() > 0)) {
                        mIatLayout.setVisibility(View.GONE);
                        mRecordLayout.setVisibility(View.VISIBLE);
                        mRecognizeStatusTv.setText(R.string.recognizing);
                        mRecordStatusImg.setBackgroundResource(R.drawable.ps_pause);
                        startRecognize();
                    } else {
                        ToastUtils.showLong(IatActivity.this, getString(R.string.tips4));
                    }
                }

                @Override
                public void failureData(String error) {
                    DialogUtil.getInstance().disWaitingDialog();
                    Log.e("AA", "error: " + error);
                }
            });
        } else {
            stopRecognize();
        }

    }

    private void createFile(String name) {
        String dirPath = ConstBroadStr.GetAudioRootPath(this, mCheckbox.isChecked()) + name + "/";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        LogUtils.printErrorLog("tag","file.exists(): " + hvFileCommonUtils.isFileExist(dirPath));
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
                ToastUtils.show(this, getString(R.string.saved));
            }
        });
        TextView menuItem2 = view.findViewById(R.id.popup_delete);
        menuItem2.setOnClickListener(view12 -> {
            if (popupWindow != null) {
                popupWindow.dismiss();
                mNoteView.clearAllMemo();
                //LogUtils.printErrorLog("setOnClickListener", "已经删除");
                //ToastUtils.show(this, "已经删除");
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
                RecognmizeImageAyncTask recognmizeImageAyncTask = new RecognmizeImageAyncTask();
                recognmizeImageAyncTask.execute();

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
        popupWindow.setWidth((int)(CommonUtils.getScreenWidth(this) / 5.8));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        return popupWindow;
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
        intent.putExtra(Intent.EXTRA_SUBJECT, "语音记录邮件分享");
        intent.putExtra(Intent.EXTRA_TEXT, "敬启者,\n下面是语音记录文本内容：\n" + mFileBean.getContent()
                + " \n\n请打开下面链接查看附件内容：\n" + url);
        intent.setType("application/octet-stream");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            ToastUtils.show(this, getString(R.string.unmailsetting));
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

    public boolean isRecording() {
        return isRecording;
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
        if (isRecording) {
            long tempTime = System.currentTimeMillis() - mStartRecordTime + mUsageRecordTime;
            SharedPreferencesUtils.saveUsageTimeSharePrefer(HvApplication.mContext, SharedPreferencesUtils.USAGETIME, tempTime);
            LogUtils.printErrorLog(TAG, "===onSaveInstanceState");
        }

    }

    /**
     * 保存当前页的笔迹信息
     */
    protected void saveNoteCurTracePage() {
        if (mNoteView == null) {
            return;
        }
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

        //NoteBaseData.gTraFile.pages.get(1).traces.get(1);
        // 如果当前页最后一页
        if (mNotePageIndex == NoteBaseData.gTraFile.getCount() - 1) {
            // 新建页
            AddNoteNewEmptyPage(mNotePageIndex + 1);
        } else {
            saveNoteCurTracePage();
        }
        mNotePageIndex++;
        // 刷新便笺的页面
        updateNoteCurPage();
        new Handler().postDelayed(new Runnable() {

            public void run() {
                mNoteView.setInputEnabled(true);
            }

        }, 500);
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
        new Handler().postDelayed(new Runnable() {

            public void run() {
                mNoteView.setInputEnabled(true);
            }

        }, 500);
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
        new Handler().postDelayed(new Runnable() {

            public void run() {
                mNoteView.setInputEnabled(true);
            }

        }, 500);
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
            final String content = result;
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
                    CommonUtils.saveAsFileWriter(srcTmpFilePath, content);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
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
}

package com.hanvon.speech.realtime.alspeech;

import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.DUILiteConfig;
import com.aispeech.DUILiteSDK;
import com.aispeech.common.AIConstant;
import com.aispeech.export.config.AICloudASRConfig;
import com.aispeech.export.engines2.AICloudLASRRealtimeEngine;
import com.aispeech.export.intent.AICloudLASRRealtimeIntent;
import com.aispeech.export.listeners.AILASRRealtimeListener;
import com.asr.ai.speech.realtime.ConstBroadStr;
import com.asr.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.speechBean.SpeechResult;
import com.hanvon.speech.realtime.bean.speechBean.SpeechVarResult;
import com.hanvon.speech.realtime.model.IatResults;
import com.hanvon.speech.realtime.util.FileBeanUils;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static android.security.KeyStore.getApplicationContext;

public class AlSpeechEngine {
    private static final String TAG = "AlSpeechEngine";

    private  AICloudLASRRealtimeEngine mEngine;
    private static AlSpeechEngine mAlSpeechEngine;
    private Gson gson;
    public static AlSpeechEngine getInstance() {
        if (mAlSpeechEngine == null) {
            synchronized (AlSpeechEngine.class) {
                if (mAlSpeechEngine == null) {
                    mAlSpeechEngine = new AlSpeechEngine();
                }
            }
        }
        return mAlSpeechEngine;
    }

    private AlSpeechEngine() {

    }



    public void initSpeech() {
        auth();
        AICloudASRConfig config = new AICloudASRConfig();
        config.setLocalVadEnable(false);
        gson = new Gson();
        //mEngine = AICloudLASRRealtimeEngine.createInstance();
        //mEngine.init(new AlSpeechEngine.AILASRRealtimeListenerImpl());
    }

    public void startSpeechRecog() {
        AICloudLASRRealtimeIntent mAICloudLASRRealtimeIntent = new AICloudLASRRealtimeIntent();
        mAICloudLASRRealtimeIntent.setUseTxtSmooth(false); // 口语顺滑，去掉 嗯、啊 这些词
        mAICloudLASRRealtimeIntent.setUseTProcess(true);    // 逆文本，识别出的数字转成阿拉伯数字
        //mAICloudLASRRealtimeIntent.setUseAlignment(false); // 是否输出词级别时间对齐信息
        mAICloudLASRRealtimeIntent.setUseSensitiveWdsNorm(false); // 是否使用内置敏感词
        //mAICloudLASRRealtimeIntent.setUseStreamPunc(false); // 是否启用流式标点

        mAICloudLASRRealtimeIntent.setRes(null); // res=lasr-cn-en使用中英文混合，不设置res字段使用中文在线
        mAICloudLASRRealtimeIntent.setForwardAddresses(null); // 当参数不为空时，启动转发模式。 当有转写结果时，会往注册的WebSocket地址实时推送转写结果。

        mAICloudLASRRealtimeIntent.setAudioType(AICloudLASRRealtimeIntent.PCM_ENCODE_TYPE.OGG);
        mAICloudLASRRealtimeIntent.setUseCustomFeed(false); // 设置是否自行feed数据, default is false
        mAICloudLASRRealtimeIntent.setServer("wss://lasr.duiopen.com/live/ws2");
        mEngine.start(mAICloudLASRRealtimeIntent);
    }

    public void cancelSpeechRecog() {
        mEngine.stop();
    }



    private class AILASRRealtimeListenerImpl implements AILASRRealtimeListener {
        public void onError(AIError error) {
            //ToastUtils.showLong(getApplicationContext(), error.getError());
            LogUtils.printErrorLog(TAG, "onError: " + error.getError());
            Intent intent = new Intent(ConstBroadStr.SPEENCH_CLOSE);
            intent.putExtra(ConstBroadStr.SPEENCH_ERROR, error.getError());
            HvApplication.getContext().sendBroadcast(intent);
        }

        /**
         * <ul>
         * <li>errno = 7，start响应成功</li>
         * <li>errno = 8， 表示本次返回为识别中间var结果</li>
         * <li>errno = 0，表示本次返回为识别中间rec结果</li>
         * <li>errno = 9，表示为客户端发完空帧后的最后一个rec，客户端可以断开链接</li>
         * <li>errno=10，客户端发送的数据错误</li>
         * <li>errno=11，服务异常，比如live模块初始化失败，没有可用的计算进程，计算进程退出，计算进程计算超时</li>
         * </ul>
         * <p>
         * errno 除了 0，7，8 外，收到其余code后 websocket 会断开，sdk会回调网络错误
         *
         * @param results 服务器返回的结果
         */
        public void onResults(AIResult results) {
            LogUtils.printErrorLog(TAG, "onResults： ");
            if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                String resultJson = (String) results.getResultObject();
                LogUtils.printErrorLog(TAG, resultJson);
                try {
                    JSONObject jsonObject = new JSONObject(resultJson);
                    int errno = jsonObject.getInt("errno");
                    if (errno != 0 && errno != 7 && errno != 8 && errno != 9) {
                        LogUtils.printErrorLog(TAG, "onResults: " + resultJson);
                        Intent intent = new Intent(ConstBroadStr.SPEENCH_CLOSE);
                        HvApplication.getContext().sendBroadcast(intent);
                        // errno 除了 0，7，8 外，收到其余code后 websocket 会断开，sdk会回调网络错误
                    } else if (errno == 0 || errno == 9) {
                        SpeechResult result = gson.fromJson(resultJson, SpeechResult.class);
                        if (TextUtils.isEmpty(result.getData().getOnebest())) {
                            return;
                        }
                        result.setRecordTime(FileBeanUils.getCurrrentRecordTime());
                        IatResults.addSpeechResult(result);
                        Intent intent = new Intent(ConstBroadStr.UPDATEALSPEECHRECOG);
                        HvApplication.getContext().sendBroadcast(intent);
                    } else if (errno == 8) {
                        if (HvApplication.REFRESH) {
                            SpeechVarResult result = gson.fromJson(resultJson, SpeechVarResult.class);
                            if (TextUtils.isEmpty(result.getData().getVar())) {
                                return;
                            }
                            IatResults.addSpeechTempResult(result);
                            Intent intent = new Intent(ConstBroadStr.UPDATEALSPEECHRECOG);
                            HvApplication.getContext().sendBroadcast(intent);
                        }
                    } else {

                       // LogUtils.printErrorLog(TAG, "onResults: " + resultJson);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onInit(int status) {
            LogUtils.printErrorLog(TAG, "onInit");
            if (status == AIConstant.OPT_SUCCESS) {

                LogUtils.printErrorLog(TAG, "onInit   AIConstant.OPT_SUCCESS");
            } else {
                LogUtils.printErrorLog(TAG, "onInit   ！AIConstant.OPT_SUCCESS");
            }
        }

        @Override
        public void onReadyForSpeech() {

            LogUtils.printErrorLog(TAG, "onReadyForSpeech");
        }

        @Override
        public void onResultDataReceived(byte[] buffer, int size) {
            LogUtils.printErrorLog(TAG, "onResultDataReceived");
            // 单路音频回调，单麦的话音频直接回调，多麦的话经过 beamforming 之后的单路音频
        }

        @Override
        public void onRawDataReceived(byte[] buffer, int size) {
            LogUtils.printErrorLog(TAG, "onRawDataReceived");
            // 多麦的原始音频
        }
    }

    /**
     * "57ef004d253ca19dae2522c95f17a852",
     *                 "279594186",
     *                 "ae625a43ec270c237a799334b9e5f29f",
     *                 "b6bd6fa75a70105df1919a17ab41d4bd"
     */
    /*
                "e341a0dbbca18e81be4e44aa5f2d229b",
                        "279596358",
                        "1d21d043ffb787fae93ec490a34a0dfd",
                        "6e60862844d44355acc7447491b76909" */
    private void auth() {//
        DUILiteConfig config = new DUILiteConfig(
                "e341a0dbbca18e81be4e44aa5f2d229b",
                "279596358",
                "1d21d043ffb787fae93ec490a34a0dfd",
                "6e60862844d44355acc7447491b76909");
        // 初始化数据及授权
        config.setAuthTimeout(5000);
        config.setAudioRecorderType(DUILiteConfig.TYPE_COMMON_MIC);

        //config.openLog();//仅输出SDK logcat日志，须在init之前调用.
        //config.openLog(Environment.getExternalStorageDirectory() + "/DUILite_SDK.log");//输出SDK logcat日志，同时保存日志文件在/sdcard/duilite/DUILite_SDK.log，须在init之前调用.


        String core_version = DUILiteSDK.getCoreVersion();//获取内核版本号
        //LogUtils.printErrorLog(TAG, "core version is: " + core_version);

        boolean isAuthorized = DUILiteSDK.isAuthorized(getApplicationContext());//查询授权状态，DUILiteSDK.init之后随时可以调
        //LogUtils.printErrorLog(TAG, "DUILite SDK is isAuthorized ？ " + isAuthorized);

        DUILiteSDK.init(getApplicationContext(), config, new DUILiteSDK.InitListener() {
            @Override
            public void success() {

                mEngine = AICloudLASRRealtimeEngine.createInstance();
                mEngine.init(new AlSpeechEngine.AILASRRealtimeListenerImpl());

                LogUtils.printErrorLog(TAG, "success 授权成功! ");
                HvApplication.HaveAuth = true;
                Intent intent = new Intent(ConstBroadStr.SPEENCH_AUTH);
                HvApplication.getContext().sendBroadcast(intent);
            }

            @Override
            public void error(String errorCode, String errorInfo) {
                LogUtils.printErrorLog(TAG, "授权失败, errorcode: " + errorCode + ",errorInfo:" + errorInfo);
                Intent intent = new Intent(ConstBroadStr.SPEENCH_AUTH);
                HvApplication.getContext().sendBroadcast(intent);
            }
        });
    }

    private void destroyEngine() {
        if (mEngine != null) {
            mEngine.destroy();
        }
    }
}

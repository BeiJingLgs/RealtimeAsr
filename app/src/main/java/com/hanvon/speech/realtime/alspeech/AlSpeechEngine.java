package com.hanvon.speech.realtime.alspeech;

import android.content.Intent;
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
import com.hanvon.speech.realtime.model.IatResults;
import com.hanvon.speech.realtime.util.FileBeanUils;

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
        initSpeech();
    }



    private void initSpeech() {
        auth();
        AICloudASRConfig config = new AICloudASRConfig();
        config.setLocalVadEnable(false);
        gson = new Gson();
        mEngine = AICloudLASRRealtimeEngine.createInstance();
        mEngine.init(new AlSpeechEngine.AILASRRealtimeListenerImpl());
    }

    private void startSpeechRecog() {
        AICloudLASRRealtimeIntent mAICloudLASRRealtimeIntent = new AICloudLASRRealtimeIntent();
        mAICloudLASRRealtimeIntent.setUseTxtSmooth(false); // 口语顺滑，去掉 嗯、啊 这些词
        mAICloudLASRRealtimeIntent.setUseTProcess(true);    // 逆文本，识别出的数字转成阿拉伯数字
        mAICloudLASRRealtimeIntent.setUseAlignment(false); // 是否输出词级别时间对齐信息
        mAICloudLASRRealtimeIntent.setUseSensitiveWdsNorm(false); // 是否使用内置敏感词
        mAICloudLASRRealtimeIntent.setUseStreamPunc(false); // 是否启用流式标点

        mAICloudLASRRealtimeIntent.setRes(null); // res=lasr-cn-en使用中英文混合，不设置res字段使用中文在线
        mAICloudLASRRealtimeIntent.setForwardAddresses(null); // 当参数不为空时，启动转发模式。 当有转写结果时，会往注册的WebSocket地址实时推送转写结果。

        mAICloudLASRRealtimeIntent.setAudioType(AICloudLASRRealtimeIntent.PCM_ENCODE_TYPE.OGG);
        mAICloudLASRRealtimeIntent.setUseCustomFeed(false); // 设置是否自行feed数据, default is false
        mAICloudLASRRealtimeIntent.setServer("wss://lasr.duiopen.com/live/ws2");
        mEngine.start(mAICloudLASRRealtimeIntent);
    }

    private void cancelSpeechRecog() {
        mEngine.cancel();
    }



    private class AILASRRealtimeListenerImpl implements AILASRRealtimeListener {
        public void onError(AIError error) {

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
            if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                String resultJson = (String) results.getResultObject();
                // Log.e(TAG, resultJson);
                try {
                    JSONObject jsonObject = new JSONObject(resultJson);
                    int errno = jsonObject.getInt("errno");
                    if (errno != 0 && errno != 7 && errno != 8) {
                        // errno 除了 0，7，8 外，收到其余code后 websocket 会断开，sdk会回调网络错误
                    } else if (errno == 0) {
                        SpeechResult result = gson.fromJson(resultJson, SpeechResult.class);
                        if (TextUtils.isEmpty(result.getData().getOnebest())) {
                            return;
                        }
                        //if (FileBeanUils.isRecoding())
                        result.setRecordTime(FileBeanUils.getCurrrentRecordTime());
                        IatResults.addSpeechResult(result);
                        Intent intent = new Intent(ConstBroadStr.UPDATEALSPEECHRECOG);
                        HvApplication.getContext().sendBroadcast(intent);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {

            } else {

            }
        }

        @Override
        public void onReadyForSpeech() {

        }

        @Override
        public void onResultDataReceived(byte[] buffer, int size) {
            // 单路音频回调，单麦的话音频直接回调，多麦的话经过 beamforming 之后的单路音频
        }

        @Override
        public void onRawDataReceived(byte[] buffer, int size) {
            // 多麦的原始音频
        }
    }

    private void auth() {
        DUILiteConfig config = new DUILiteConfig(
                "d3c265662929841215092b415c257bd6",
                "279594186",
                "ae625a43ec270c237a799334b9e5f29f",
                "b6bd6fa75a70105df1919a17ab41d4bd");
        // 初始化数据及授权
        DUILiteSDK.init(getApplicationContext(), config, new DUILiteSDK.InitListener() {
            @Override
            public void success() {
                Log.d(TAG, "授权成功! ");
            }

            @Override
            public void error(String errorCode, String errorInfo) {
                Log.d(TAG, "授权失败, errorcode: " + errorCode + ",errorInfo:" + errorInfo);
            }
        });
    }

    private void destroyEngine() {
        if (mEngine != null) {
            mEngine.destroy();
        }
    }
}

package com.baidu.ai.speech.realtime.full.download;

import com.baidu.ai.speech.realtime.full.util.Stat;
import com.hanvon.speech.realtime.util.LogUtils;

import java.util.logging.Logger;

/**
 * STEP 2.3 库接收识别结果
 */
public class SimpleDownloader {
    private static Logger logger = Logger.getLogger("SimpleDownloader");

    public void onMessage(Result result) {
        if (!result.isHeartBeat()) {
            if (result.getType().equals(Result.TYPE_FIN_TEXT)) {
                LogUtils.printErrorLog("SimpleDownloader", "Stat.formatResult(result).toString(): " + Stat.formatResult(result).toString());
                logger.fine(Stat.formatResult(result).toString());
            }
        }
    }
}

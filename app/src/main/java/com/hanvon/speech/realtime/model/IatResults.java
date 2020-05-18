package com.hanvon.speech.realtime.model;

import com.baidu.ai.speech.realtime.full.download.Result;
import com.hanvon.speech.realtime.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static android.app.ActivityThread.TAG;

public class IatResults {

    private static List<Result> mSentenceResults = Collections.synchronizedList(new ArrayList<>());
    private static StringBuffer mStringResult = new StringBuffer();
    private static StringBuffer mTempResult = new StringBuffer();
    private static StringBuffer mTotalResult = new StringBuffer();
    private static Logger logger = Logger.getLogger("IatResults");
    public static void addAllResult(List<Result> results) {
        clearResults();
        mSentenceResults.addAll(results);
        for (Result result : results)
            mStringResult.append(result.getResult());
    }


    public static void addResult(Result result) {
        mTempResult.setLength(0);
        mSentenceResults.add(result);
        mStringResult.append(result.getResult());
    }

    public static void addTempResult(Result result) {
        mTempResult.setLength(0);
        mTempResult.append(result.getResult());
    }



    public static List<Result> getResults() {
        return mSentenceResults;
    }

    public static String getResultsStr() {
        mTotalResult.setLength(0);
        return mTotalResult.append(mStringResult).append(mTempResult).toString();
    }

    public static void setResultsStr(String result) {
         mStringResult.append(result);
    }

    public static void clearResults() {
        mSentenceResults.clear();
        mStringResult.setLength(0);
    }



}

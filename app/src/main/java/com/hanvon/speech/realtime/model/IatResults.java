package com.hanvon.speech.realtime.model;

import com.baidu.ai.speech.realtime.full.download.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IatResults {

    private static List<Result> mSentenceResults = Collections.synchronizedList(new ArrayList<>());
    private static StringBuffer mStringResult = new StringBuffer();

    public static void addAllResult(List<Result> results) {
        clearResults();
        mSentenceResults.addAll(results);
        for (Result result : results)
            mStringResult.append(result.getResult());
    }


    public static void addResult(Result result) {
        mSentenceResults.add(result);
        mStringResult.append(result.getResult());
    }
    public static List<Result> getResults() {
        return mSentenceResults;
    }

    public static String getResultsStr() {
        return mStringResult.toString();
    }

    public static void clearResults() {
        mSentenceResults.clear();
        mStringResult.setLength(0);
    }



}

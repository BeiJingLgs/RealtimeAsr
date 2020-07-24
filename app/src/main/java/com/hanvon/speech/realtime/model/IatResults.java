package com.hanvon.speech.realtime.model;

import com.asr.ai.speech.realtime.full.download.Result;
import com.hanvon.speech.realtime.bean.speechBean.AsrEditSentence;
import com.hanvon.speech.realtime.bean.speechBean.SpeechResult;
import com.hanvon.speech.realtime.bean.speechBean.SpeechSentence;
import com.hanvon.speech.realtime.bean.speechBean.SpeechVarResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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
        /*mTempResult.setLength(0);
        mSentenceResults.add(result);
        mStringResult.append(result.getResult());*/
        addAsrEditResult(ConvertResult.Result2AsrEditSentence(result));
    }

    public static void addTempResult(Result result) {
        //mTempResult.setLength(0);
        //mTempResult.append(result.getResult());
        addAsrEditTempResult(ConvertResult.Result2AsrEditSentence(result));
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


    private static List<SpeechResult> mSpeechSentenceResults = Collections.synchronizedList(new ArrayList<>());
    public static void addAllSpeechResult(List<SpeechResult> results) {
        clearSpeechResults();
        mSpeechSentenceResults.addAll(results);
        for (SpeechResult result : results)
            mStringResult.append(result.getData() == null ? "" : result.getData().getOnebest());
    }


    public static void addSpeechResult(SpeechResult result) {
        //mTempResult.setLength(0);
        //mSpeechSentenceResults.add(result);
        //mStringResult.append(result.getData().getOnebest());
        addAsrEditResult(ConvertResult.SpeechSentence2AsrEditSentence(result.getData()));
    }

    public static void addSpeechTempResult(SpeechVarResult result) {
        //mTempResult.setLength(0);
        //mTempResult.append(result.getData().getOnebest());
        addAsrEditTempResult(ConvertResult.SpeechSentence2AsrEditSentence2(result.getData()));
    }



    public static List<SpeechResult> getSpeechResults() {
        return mSpeechSentenceResults;
    }

    public static String getSpeechResultsStr() {
        mTotalResult.setLength(0);
        return mTotalResult.append(mStringResult).append(mTempResult).toString();
    }

    public static void setSpeechResultsStr(String result) {
        mStringResult.append(result);
    }

    public static void clearSpeechResults() {
        mSpeechSentenceResults.clear();
        mStringResult.setLength(0);
    }






    private static List<AsrEditSentence> mAsrEditentenceResults = Collections.synchronizedList(new ArrayList<>());
    public static void addAllAsrEditResult(List<AsrEditSentence> results) {
        clearAsrEditResults();
        mAsrEditentenceResults.addAll(results);
        for (AsrEditSentence result : results)
            mStringResult.append(result.getContent() == null ? "" : result.getContent());
    }


    public static void addAsrEditResult(AsrEditSentence result) {
        mTempResult.setLength(0);
        mAsrEditentenceResults.add(result);
        mStringResult.append(result.getContent());
    }

    public static void addAsrEditTempResult(AsrEditSentence result) {
        mTempResult.setLength(0);
        mTempResult.append(result.getContent());
    }



    public static List<AsrEditSentence> getAsrEditResults() {
        return mAsrEditentenceResults;
    }

    public static String getAsrEditResultsStr() {
        mTotalResult.setLength(0);
        return mTotalResult.append(mStringResult).append(mTempResult).toString();
    }

    public static void setAsrEditResultsStr(String result) {
        mStringResult.append(result);
    }

    public static void clearAsrEditResults() {
        mAsrEditentenceResults.clear();
        mStringResult.setLength(0);
    }

}

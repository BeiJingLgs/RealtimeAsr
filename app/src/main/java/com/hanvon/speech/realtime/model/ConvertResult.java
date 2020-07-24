package com.hanvon.speech.realtime.model;

import com.asr.ai.speech.realtime.full.download.Result;
import com.hanvon.speech.realtime.bean.speechBean.AsrEditSentence;
import com.hanvon.speech.realtime.bean.speechBean.SpeechSentence;
import com.hanvon.speech.realtime.bean.speechBean.SpeechVarSentence;
import com.hanvon.speech.realtime.util.FileBeanUils;

public class ConvertResult {

    public static AsrEditSentence Result2AsrEditSentence(Result result) {
        AsrEditSentence asrEditSentence = new AsrEditSentence(result.getStartTime(), result.getEndTime(), result.getResult(), result.getSn(), FileBeanUils.getCurrrentRecordTime());
        return asrEditSentence;
    }

    public static AsrEditSentence SpeechSentence2AsrEditSentence(SpeechSentence result) {
        AsrEditSentence asrEditSentence = new AsrEditSentence(result.getBg(), result.getEd(), result.getOnebest(), result.getSessionId(), FileBeanUils.getCurrrentRecordTime());
        return asrEditSentence;
    }

    public static AsrEditSentence SpeechSentence2AsrEditSentence2(SpeechVarSentence result) {
        AsrEditSentence asrEditSentence = new AsrEditSentence(result.getBg(), 0, result.getVar(), result.getSessionId(), FileBeanUils.getCurrrentRecordTime());
        return asrEditSentence;
    }
}

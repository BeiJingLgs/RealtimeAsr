package com.hanvon.speech.realtime.bean.speechBean;

public class SpeechResult
{
    private int errno;

    private String error;



    private long recordTime;


    private SpeechSentence data;

    public void setErrno(int errno){
        this.errno = errno;
    }
    public int getErrno(){
        return this.errno;
    }
    public void setError(String error){
        this.error = error;
    }
    public String getError(){
        return this.error;
    }
    public void setData(SpeechSentence data){
        this.data = data;
    }
    public SpeechSentence getData(){
        return this.data;
    }
    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }
}

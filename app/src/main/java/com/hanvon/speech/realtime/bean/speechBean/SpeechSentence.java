package com.hanvon.speech.realtime.bean.speechBean;

public class SpeechSentence
{
    private String sessionId;

    private int bg;

    private int ed;

    private String onebest;

    private int speaker;

    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }
    public String getSessionId(){
        return this.sessionId;
    }
    public void setBg(int bg){
        this.bg = bg;
    }
    public int getBg(){
        return this.bg;
    }
    public void setEd(int ed){
        this.ed = ed;
    }
    public int getEd(){
        return this.ed;
    }
    public void setOnebest(String onebest){
        this.onebest = onebest;
    }
    public String getOnebest(){
        return this.onebest;
    }
    public void setSpeaker(int speaker){
        this.speaker = speaker;
    }
    public int getSpeaker(){
        return this.speaker;
    }
}
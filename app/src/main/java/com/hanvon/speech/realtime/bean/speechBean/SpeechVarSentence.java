package com.hanvon.speech.realtime.bean.speechBean;

public class SpeechVarSentence {

    private String sessionId;
    private String var;
    private int speaker;
    private int bg;
    public void setSessionId(String sessionId) {
         this.sessionId = sessionId;
     }
     public String getSessionId() {
         return sessionId;
     }

    public void setVar(String var) {
         this.var = var;
     }
     public String getVar() {
         return var;
     }

    public void setSpeaker(int speaker) {
         this.speaker = speaker;
     }
     public int getSpeaker() {
         return speaker;
     }

    public void setBg(int bg) {
         this.bg = bg;
     }
     public int getBg() {
         return bg;
     }

}
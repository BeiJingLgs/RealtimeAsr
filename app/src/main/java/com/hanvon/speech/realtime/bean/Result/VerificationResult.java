package com.hanvon.speech.realtime.bean.Result;

public class VerificationResult {
    private String Code;

    private String Msg;


    public void setCode(String Code){
        this.Code = Code;
    }
    public String getCode(){
        return this.Code;
    }
    public void setMsg(String Msg){
        this.Msg = Msg;
    }
    public String getMsg(){
        return this.Msg;
    }
}
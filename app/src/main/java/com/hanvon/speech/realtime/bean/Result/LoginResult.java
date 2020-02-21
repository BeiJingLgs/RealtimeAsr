package com.hanvon.speech.realtime.bean.Result;

public class LoginResult
{
    private String Token;

    private String Code;

    private String Msg;

    public void setToken(String Token){
        this.Token = Token;
    }
    public String getToken(){
        return this.Token;
    }
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

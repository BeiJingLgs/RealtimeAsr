package com.hanvon.speech.realtime.bean.Result;

public class PayResultBean
{
    private UrlBean Model;

    private String Code;

    private String Msg;

    public void setUrlBean(UrlBean Model){
        this.Model = Model;
    }
    public UrlBean getUrlBean(){
        return this.Model;
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

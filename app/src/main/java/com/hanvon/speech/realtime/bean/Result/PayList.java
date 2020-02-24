package com.hanvon.speech.realtime.bean.Result;

import java.util.List;

public class PayList
{
    private List<PayType> TModel;

    private String Code;

    private String Msg;

    public void setPayType(List<PayType> TModel){
        this.TModel = TModel;
    }
    public List<PayType> getPayType(){
        return this.TModel;
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

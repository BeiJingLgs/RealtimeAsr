package com.hanvon.speech.realtime.bean.Result;

import java.util.List;

public class PackList
{
    private List<PackBean> TModel;

    private String Code;

    private String Msg;

    public void setPackBean(List<PackBean> PackBean){
        this.TModel = TModel;
    }
    public List<PackBean> getPackBean(){
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
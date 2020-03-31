package com.hanvon.speech.realtime.bean.Result;

import java.util.List;

public class DeviceBeanList
{
    private List<DeviceBean> TModel;

    private String Code;

    private String Msg;

    public void setDeviceBean(List<DeviceBean> TModel){
        this.TModel = TModel;
    }
    public List<DeviceBean> getDeviceBean(){
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
package com.hanvon.speech.realtime.bean.Result;

import java.util.List;

public class OrderList
{
    private List<Order> TModel;

    private String Code;

    private String Msg;

    public void setOrder(List<Order> TModel){
        this.TModel = TModel;
    }
    public List<Order> getOrder(){
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

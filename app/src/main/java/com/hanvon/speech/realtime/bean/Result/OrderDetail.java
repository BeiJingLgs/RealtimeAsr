package com.hanvon.speech.realtime.bean.Result;

public class OrderDetail
{
    private OrderModal Model;

    private String Code;

    private String Msg;

    public void setOrderModal(OrderModal Model){
        this.Model = Model;
    }
    public OrderModal getOrderModal(){
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
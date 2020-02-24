package com.hanvon.speech.realtime.bean.Result;

public class Order
{
    private int ID;

    private String SerialNo;

    private String PackName;

    private int Amount;

    private int State;

    private String PayChannelName;

    private String CreateTime;

    private String UpdateTime;

    public void setID(int ID){
        this.ID = ID;
    }
    public int getID(){
        return this.ID;
    }
    public void setSerialNo(String SerialNo){
        this.SerialNo = SerialNo;
    }
    public String getSerialNo(){
        return this.SerialNo;
    }
    public void setPackName(String PackName){
        this.PackName = PackName;
    }
    public String getPackName(){
        return this.PackName;
    }
    public void setAmount(int Amount){
        this.Amount = Amount;
    }
    public int getAmount(){
        return this.Amount;
    }
    public void setState(int State){
        this.State = State;
    }
    public int getState(){
        return this.State;
    }
    public void setPayChannelName(String PayChannelName){
        this.PayChannelName = PayChannelName;
    }
    public String getPayChannelName(){
        return this.PayChannelName;
    }
    public void setCreateTime(String CreateTime){
        this.CreateTime = CreateTime;
    }
    public String getCreateTime(){
        return this.CreateTime;
    }
    public void setUpdateTime(String UpdateTime){
        this.UpdateTime = UpdateTime;
    }
    public String getUpdateTime(){
        return this.UpdateTime;
    }
}
package com.hanvon.speech.realtime.bean.Result;

public class OrderModal
{
    private int ID;

    private String SerialNo;

    private int PackID;

    private int Amount;

    private int State;

    private String ExpireTime;

    private String PaySuccTime;

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
    public void setPackID(int PackID){
        this.PackID = PackID;
    }
    public int getPackID(){
        return this.PackID;
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
    public void setExpireTime(String ExpireTime){
        this.ExpireTime = ExpireTime;
    }
    public String getExpireTime(){
        return this.ExpireTime;
    }
    public void setPaySuccTime(String PaySuccTime){
        this.PaySuccTime = PaySuccTime;
    }
    public String getPaySuccTime(){
        return this.PaySuccTime;
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
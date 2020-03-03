package com.hanvon.speech.realtime.bean.Result;

public class DeviceBean
{
    private int ID;

    private String SerialNo;

    private String Type;

    private int IsCommonlyUsed;

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
    public void setType(String Type){
        this.Type = Type;
    }
    public String getType(){
        return this.Type;
    }
    public void setIsCommonlyUsed(int IsCommonlyUsed){
        this.IsCommonlyUsed = IsCommonlyUsed;
    }
    public int getIsCommonlyUsed(){
        return this.IsCommonlyUsed;
    }
}
package com.hanvon.speech.realtime.bean.Result;

public class PayType
{
    private int ID;

    private String Name;

    private String Type;

    private String Remark;

    public void setID(int ID){
        this.ID = ID;
    }
    public int getID(){
        return this.ID;
    }
    public void setName(String Name){
        this.Name = Name;
    }
    public String getName(){
        return this.Name;
    }
    public void setType(String Type){
        this.Type = Type;
    }
    public String getType(){
        return this.Type;
    }
    public void setRemark(String Remark){
        this.Remark = Remark;
    }
    public String getRemark(){
        return this.Remark;
    }
}
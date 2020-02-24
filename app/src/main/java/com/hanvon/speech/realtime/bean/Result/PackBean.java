package com.hanvon.speech.realtime.bean.Result;

public class PackBean
{
    private int ID;

    private String RemainDuration;

    private String EndTime;

    public void setID(int ID){
        this.ID = ID;
    }
    public int getID(){
        return this.ID;
    }
    public void setRemainDuration(String RemainDuration){
        this.RemainDuration = RemainDuration;
    }
    public String getRemainDuration(){
        return this.RemainDuration;
    }
    public void setEndTime(String EndTime){
        this.EndTime = EndTime;
    }
    public String getEndTime(){
        return this.EndTime;
    }
}
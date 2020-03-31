package com.hanvon.speech.realtime.bean.Result;

/**
 * 使用记录实体类
 */
public class UsageBeen
{
    private int ID;

    private long UsedDuration;

    private String Time;

    public void setID(int ID){
        this.ID = ID;
    }
    public int getID(){
        return this.ID;
    }
    public void setUsedDuration(long UsedDuration){
        this.UsedDuration = UsedDuration;
    }
    public long getUsedDuration(){
        return this.UsedDuration;
    }
    public void setTime(String Time){
        this.Time = Time;
    }
    public String getTime(){
        return this.Time;
    }
}

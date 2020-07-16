package com.hanvon.speech.realtime.bean.Result;

import java.io.Serializable;

/**
 *获取可购买的服务包实体类
 */
public class ShopType {
    private int ID;

    private String Name;

    private int Price;

    private long Duration;

    private long ValidPeriod;

    private String Describe;



    private int VoiceEngineTypeID;

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
    public void setPrice(int Price){
        this.Price = Price;
    }
    public int getPrice(){
        return this.Price;
    }
    public void setDuration(long Duration){
        this.Duration = Duration;
    }
    public long getDuration(){
        return this.Duration;
    }
    public void setValidPeriod(long ValidPeriod){
        this.ValidPeriod = ValidPeriod;
    }
    public long getValidPeriod(){
        return this.ValidPeriod;
    }
    public void setDescribe(String Describe){
        this.Describe = Describe;
    }
    public String getDescribe(){
        return this.Describe;
    }
    public int getVoiceEngineTypeID() {
        return VoiceEngineTypeID;
    }

    public void setVoiceEngineTypeID(int voiceEngineTypeID) {
        VoiceEngineTypeID = voiceEngineTypeID;
    }
}

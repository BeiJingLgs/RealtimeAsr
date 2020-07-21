package com.hanvon.speech.realtime.bean.Result;

/**
 *获取设备关联有效的服务包实体类
 */
public class PackBean
{
    private int ID;

    private String PackName;

    private int RemainDuration;

    private String CreateTime;



    public int VoiceEngineTypeID;

    private String EndTime;

    private int Duration;

    private String Source;

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public void setID(int ID){
        this.ID = ID;
    }
    public int getID(){
        return this.ID;
    }
    public void setPackName(String PackName){
        this.PackName = PackName;
    }
    public String getPackName(){
        return this.PackName;
    }
    public void setRemainDuration(int RemainDuration){
        this.RemainDuration = RemainDuration;
    }
    public int getRemainDuration(){
        return this.RemainDuration;
    }
    public void setCreateTime(String CreateTime){
        this.CreateTime = CreateTime;
    }
    public String getCreateTime(){
        return this.CreateTime;
    }
    public void setEndTime(String EndTime){
        this.EndTime = EndTime;
    }
    public String getEndTime(){
        return this.EndTime;
    }
    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }
    public int getVoiceEngineTypeID() {
        return VoiceEngineTypeID;
    }

    public void setVoiceEngineTypeID(int voiceEngineTypeID) {
        VoiceEngineTypeID = voiceEngineTypeID;
    }
}
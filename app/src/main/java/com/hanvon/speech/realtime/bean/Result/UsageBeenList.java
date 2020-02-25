package com.hanvon.speech.realtime.bean.Result;

import java.util.List;

public class UsageBeenList
{
    private List<UsageBeen> TModel;

    private String Code;

    private String Msg;

    public void setUsageBeen(List<UsageBeen> TModel){
        this.TModel = TModel;
    }
    public List<UsageBeen> getUsageBeen(){
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

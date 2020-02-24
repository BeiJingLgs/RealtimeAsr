package com.hanvon.speech.realtime.bean.Result;

import java.util.ArrayList;
import java.util.List;
public class ShopTypeList
{
    private List<ShopType> TModel;

    private String Code;

    private String Msg;

    public void setShopType(List<ShopType> TModel){
        this.TModel = TModel;
    }
    public List<ShopType> getShopType(){
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

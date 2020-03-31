package com.hanvon.speech.realtime.model;


import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.bean.Result.DeviceBean;
import com.hanvon.speech.realtime.bean.Result.Order;
import com.hanvon.speech.realtime.bean.Result.PackBean;
import com.hanvon.speech.realtime.bean.Result.ShopType;
import com.hanvon.speech.realtime.bean.Result.UsageBeen;

import java.util.List;

/**
 * Created by guhongbo on 2019/11/25.
 */

public class TranslateBean {
    private volatile static TranslateBean instance = null;



    private FileBean fileBean;



    private ShopType shopType;
    private List<Order> orderList;
    private List<PackBean> packList;
    private List<UsageBeen> usageList;
    private List<ShopType> shopTypes;

    public List<DeviceBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceBean> deviceList) {
        this.deviceList = deviceList;
    }

    private List<DeviceBean> deviceList;

    private TranslateBean(){
    }

    public static TranslateBean getInstance(){
        //先检查实例是否存在，如果不存在才进入下面的同步块
        if(instance == null){
        //同步块，线程安全的创建实例
            synchronized(TranslateBean.class){
        //再次检查实例是否存在，如果不存在才真的创建实例
                if(instance == null){
                    instance = new TranslateBean();
                }
            }
        }
        return instance;
    }


    public FileBean getFileBean() {
        return fileBean;
    }

    public void setFileBean(FileBean fileBean) {
        this.fileBean = fileBean;
    }
    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<PackBean> getPackList() {
        return packList;
    }

    public void setPackList(List<PackBean> packList) {
        this.packList = packList;
    }

    public List<UsageBeen> getUsageList() {
        return usageList;
    }

    public void setUsageList(List<UsageBeen> usageList) {
        this.usageList = usageList;
    }

    public List<ShopType> getShopTypes() {
        return shopTypes;
    }

    public void setShopTypes(List<ShopType> shopTypes) {
        this.shopTypes = shopTypes;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }
}

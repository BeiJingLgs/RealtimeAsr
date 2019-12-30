package com.hanvon.speech.realtime.model;


import com.hanvon.speech.realtime.bean.FileBean;

/**
 * Created by guhongbo on 2019/11/25.
 */

public class TranslateBean {
    private volatile static TranslateBean instance = null;



    private FileBean fileBean;

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

}

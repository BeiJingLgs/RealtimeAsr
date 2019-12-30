package com.hanvon.speech.realtime.bean;
import java.io.Serializable;

/**
 * Created by guhongbo on 2019/11/21.
 */

public class FileBean implements Serializable{
    public String title;



    public String json;
    public String content;
    public String createtime;
    public String modifytime;

    public boolean mSelect;
    public boolean ismSelect() {
        return mSelect;
    }

    public void setmSelect(boolean mSelect) {
        this.mSelect = mSelect;
    }


    public void setCreatemillis(String createmillis) {
        this.createmillis = createmillis;
    }

    public String getCreatemillis() {
        return createmillis;
    }

    public String createmillis;

    public FileBean() {

    }



    public FileBean(String title, String content, String json, String createtime, String modifytime, String createmillis) {
        this.title = title;
        this.content = content;
        this.json = json;
        this.createtime = createtime;
        this.modifytime = modifytime;
        this.createmillis = createmillis;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
    public void setModifytime(String modifytime) {
        this.modifytime = modifytime;
    }
    public void setJson(String json) {
        this.json = json;
    }


    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getCreatetime() {
        return createtime;
    }
    public String getModifytime() {
        return modifytime;
    }
    public String getJson() {
        return json;
    }
}

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int duration;

    public boolean mSelect;

    public String getmSd() {
        return mSd;
    }

    public void setmSd(String mSd) {
        this.mSd = mSd;
    }

    public String mSd;
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



    public FileBean(String title, String content, String json, String createtime, String modifytime, String createmillis, String mSd, int duration) {
        this.title = title;
        this.content = content;
        this.json = json;
        this.createtime = createtime;
        this.modifytime = modifytime;
        this.createmillis = createmillis;
        this.mSd = mSd;
        this.duration = duration;
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


    @Override
    public int hashCode() {
        return createmillis.length();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileBean))
            return  false;
        if (obj == this)
            return true;
        return this.createmillis == ((FileBean)obj).createmillis;
    }
}

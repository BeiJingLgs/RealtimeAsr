/**
  * Copyright 2019 bejson.com 
  */
package com.hanvon.speech.realtime.bean;
import java.io.Serializable;
import java.util.List;

/**
 * Auto-generated: 2019-11-21 9:47:9
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class SentenceBean implements Serializable{

    private int index;
    private boolean ls;
    private int bg;
    private int ed;
    private List<WordBean> ws;
    private String sentence;
    public SentenceBean() {

    }

    public SentenceBean(int index, int bg, int ed, String sentence) {

        this.index = index;
        this.bg = bg;
        this.ed = ed;
        this.sentence = sentence;
    }


    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setIndex(int index) {
         this.index = index;
     }
     public int getIndex() {
         return index;
     }

    public void setLs(boolean ls) {
         this.ls = ls;
     }
     public boolean getLs() {
         return ls;
     }

    public void setBg(int bg) {
         this.bg = bg;
     }
     public int getBg() {
         return bg;
     }

    public void setEd(int ed) {
         this.ed = ed;
     }
     public int getEd() {
         return ed;
     }

    public void setWs(List<WordBean> ws) {
         this.ws = ws;
     }
     public List<WordBean> getWs() {
         return ws;
     }

}
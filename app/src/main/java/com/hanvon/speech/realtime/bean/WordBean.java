/**
  * Copyright 2019 bejson.com 
  */
package com.hanvon.speech.realtime.bean;

/**
 * Auto-generated: 2019-11-21 9:47:9
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class WordBean {

    private int sc;
    private String word;
    private int time;

    public WordBean(int sc, String word) {
        this.sc = sc;
        this.word = word;
    }



    public WordBean(int sc, String word, int time) {

        this.sc = sc;
        this.time = time;
        this.word = word;
    }

    public void setSc(int sc) {
         this.sc = sc;
     }
     public int getSc() {
         return sc;
     }

    public void setWord(String word) {
         this.word = word;
     }
     public String getWord() {
         return word;
     }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
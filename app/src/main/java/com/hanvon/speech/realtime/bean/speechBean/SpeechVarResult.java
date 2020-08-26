/**
  * Copyright 2020 bejson.com 
  */
package com.hanvon.speech.realtime.bean.speechBean;

/**
 * Auto-generated: 2020-07-22 17:5:49
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class SpeechVarResult {

    private int errno;
    private String error;
    private SpeechVarSentence data;
    public void setErrno(int errno) {
         this.errno = errno;
     }
     public int getErrno() {
         return errno;
     }

    public void setError(String error) {
         this.error = error;
     }
     public String getError() {
         return error;
     }

    public void setData(SpeechVarSentence data) {
         this.data = data;
     }
     public SpeechVarSentence getData() {
         return data;
     }

}
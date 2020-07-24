package com.hanvon.speech.realtime.bean.speechBean;

public class AsrEditSentence {
    private long bg;

    private long ed;

    private String content;
    private String sign;

    private long recordtime;



    public AsrEditSentence(long bg, long ed, String content, String sign, long recordtime) {
        this.bg = bg;
        this.ed = ed;
        this.content = content;
        this.sign = sign;
        this.recordtime = recordtime;
    }


    public AsrEditSentence(long bg, long ed, String content, String sign) {
        this.bg = bg;
        this.ed = ed;
        this.content = content;
        this.sign = sign;
    }

    public long getRecordtime() {
        return recordtime;
    }

    public void setRecordtime(long recordtime) {
        this.recordtime = recordtime;
    }

    public void setBg(long bg) {
        this.bg = bg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }



    public long getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public long getEd() {
        return ed;
    }

    public void setEd(long ed) {
        this.ed = ed;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}

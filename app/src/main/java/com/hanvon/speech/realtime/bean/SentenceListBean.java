package com.hanvon.speech.realtime.bean;

import java.util.List;

/**
 * Created by guhongbo on 2019/11/21.
 */

public class SentenceListBean {
    private List<SentenceBean> sentenceBeanList;

    public void setSentenceBeanList(List<SentenceBean> sentenceBeanList) {
        this.sentenceBeanList = sentenceBeanList;
    }


    public List<SentenceBean> getSentenceBeanList() {
        return sentenceBeanList;
    }
}

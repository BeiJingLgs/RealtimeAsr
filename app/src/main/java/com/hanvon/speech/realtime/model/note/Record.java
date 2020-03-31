package com.hanvon.speech.realtime.model.note;

public class Record {
    public long nTimeBegin = 0;  // 一条笔迹在录音中的开始时间
    public long nTimeEnd = 0;    // 一条笔迹在录音中的结束时间
    
    public Record() {
		// TODO Auto-generated constructor stub
	}

    public Record(long begin, long end) {
		// TODO Auto-generated constructor stub
    	nTimeBegin = begin;
    	nTimeEnd = end;
	}
}

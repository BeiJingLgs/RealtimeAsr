package com.hanvon.speech.realtime.bean;

import java.util.ArrayList;

public class FTLine {
    public ArrayList<FTChar> chars;	// �ֽṹ����
    public int[] coords;	// ���������
    public int type;		// �����ͣ�=0ˮƽ��=1��ֱ��
    public int score;		// �п��Ŷ�[0,1000]
    
    
    public FTLine(){
    	chars = null;
    	coords =  null;
    	type = 0;
    	score = 0;	
    }
    
    public void clear(){
    	chars.clear();
    	chars = null;
    	coords =  null;
    	type = 0;
    	score = 0;	
    }

	public String GetLineString(){
    	String result = "";
    	if(chars != null){
    		for(int i = 0; i < chars.size(); i++){
    			result += chars.get(i).code;
    		}
    	}
    	return result;
    }
    
}
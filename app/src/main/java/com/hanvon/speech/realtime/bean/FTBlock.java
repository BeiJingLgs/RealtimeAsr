package com.hanvon.speech.realtime.bean;

import java.util.ArrayList;

public class FTBlock {
    public ArrayList<FTLine> lines;	// �нṹ����
    public int[] coords;	// ���������
    public int rotate;		// ��ת�Ƕȣ�=0��=90��=180��=270��
    
    public FTBlock(){
    	lines = null;
    	coords = null;
    	rotate = 0;
    }
    
    public void clear(){
    	coords = null;
    	rotate = 0;
    	if(lines != null){
    		for(int i = 0; i < lines.size(); i++){
    			lines.get(i).clear();
    		}
    	}
    	lines.clear();
    	lines = null;
    }
    
    
    String GetBolckString(){
    	String result = "";
    	if(lines != null){
    		for(int i = 0; i < lines.size(); i++){
    			result += lines.get(i).GetLineString();
    			result +="\n";
    		}
    	}
    	return result;
    }
    
}

package com.hanvon.speech.realtime.bean;


public class RecoResult {
	 public int code;   // �����루=0 ������
     public FTBlock result;
     public String strErr;  // ��������벻��0, ��Ϊ������Ϣ
     
     public RecoResult(){
    	 code = -1;
    	 result = null; 
     }
     
     public void clear(){
    	 code = -1;
    	 if(result != null){
    		 result.clear();
    	 }
     }
     
     public String GetResultChars(){
    	 String resultStr = "";
    	 if(result != null){
    		 resultStr += result.GetBolckString();
    	 }
    	 return resultStr;
     }

}




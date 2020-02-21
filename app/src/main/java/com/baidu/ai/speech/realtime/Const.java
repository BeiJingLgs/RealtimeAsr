package com.baidu.ai.speech.realtime;

public interface Const {

    /* 下面2个是鉴权信息 ,具体参数在sendStartFrame() 方法内 */
    int APPID = 18072012;

    String APPKEY = "HZAImd5VA5QU6itoRurqhoE1";

    /* dev_pid 是语言模型 ， 可以修改为其它语言模型测试，如远场普通话 19362*/
    int DEV_PID = 19362;

    /* 可以改为wss:// */
    String URI = "ws://vop.baidu.com/realtime_asr";


    public static String TEST_URI = "http://edu.hwebook.cn:8008/";
    public static String CUSURI = "http://api.hwebook.cn/";
}

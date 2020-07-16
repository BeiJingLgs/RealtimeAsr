package com.baidu.ai.speech.realtime;

import java.util.logging.Level;

public class Constants {
    public final static int MINI_DEMO_MODE = 100;

    // ============ 以下参数可以修改 =============
    // 鉴权信息请修改Const类
    /**
     * 默认的录音pcm文件，在assets目录下
     */
    public final static String ASSET_PCM_FILENAME = "16k-0.pcm";

    /**
     * 日志级别
     * 较少的调试信息请使Level.INFO,
     * 更多使用 Level.ALL
     */
    public final static Level LOG_LEVEL = Level.INFO;

    /**
     * 默认的识别模式，
     * MINI_DEMO_MODE MiniMain 精简版，输入文件流
     * Runner.MODE_FILE_STREAM 完整版本，输入文件流
     * Runner.MODE_REAL_TIME_STREAM 完整版本，输入麦克风流
     * Runner.MODE_SIMULATE_REAL_TIME_STREAM 完整版本，输入文件流模拟实时流
     */
    public final static int DEFAULT_MODE = MINI_DEMO_MODE;
    public static int WIDTH;
    public static int HEIGHT;

    public static final int MESSAGE_WHAT1 = 1;
    public static final int MESSAGE_WHAT2 = 2;

    public static final int MESSAGE_WHAT3 = 3;
}

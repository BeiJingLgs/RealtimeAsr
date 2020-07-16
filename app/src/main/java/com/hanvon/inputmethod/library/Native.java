package com.hanvon.inputmethod.library;

import java.util.ArrayList;

public class Native {
    static {
        try {
            System.loadLibrary( "jni_hanvonime_handwriting" );
        } catch ( UnsatisfiedLinkError e ) {
            e.printStackTrace( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- lib handwriting -----------------------------*/
    //////////////////////////////////////////////////////////////////////////////

    /**
     * 初始化运算空间<p>
     * 注意：1.这个方法的调用，不会重复申请内存，可以重复调用
     * 2.这个方法的调用会释放掉原来的字典，所以重复调用后需要重新设置字典，不然程序会崩溃重启才能正常使用
     */
    public native static void nativeHwInitWorkspace();

    /**
     * 释放运算空间
     */
    public native static void nativeHwReleaseWorkspace();

    /**
     * 设置识别模式
     *
     * @param mode 1:单字；2：中文短句；3：叠写；4：自由写；
     */
    public native static void nativeHwSetMode(int mode);

    /**
     * 设置识别范围
     */
    public native static void nativeHwSetRange(int range);


    /**
     * 设置倾斜识别 0-60
     */
    public native static void nativeHwSetSlantScope(int degree);
    /**
     * 设置笔迹学习字典
     * @param dic
     * @return 成功后会自动卸载旧字典
     */
//    native static boolean nativeHwSetUserDic(String dic);  // 新核心废除 2016年11月29日 14:56:05 By TZ

    /**
     * 进行笔迹学习
     *
     * @param
     */
//    public native static void nativeHwLearnTrace(short[] stroke, int code);

    /**
     * 设置字典
     * @param dic
     * @return 成功后会自动卸载旧字典
     */
//    native static boolean nativeHwSetDic(String dic); // 新核心修改了此方法参数 2016年11月29日 14:56:40 By TZ

    /**
     * 设置字典和识别语言
     *
     * @param dic        字典路径
     * @param languageId 语言类型Id
     * @return 是否成功
     */
    public native static boolean nativeHwSetDicAndLanguage(String dic, int languageId);

    /**
     * 识别笔迹
     *
     * @param stroke 笔记数据数组
     * @return 识别结果数量
     */
    public native static int nativeHwRecognize(short[] stroke);

    /**
     * 获取中文结果
     *
     * @return
     */
    public native static String nativeHwGetResult();

    /**
     * 获取手势索引
     *
     * @return
     */
    public native static int nativeHwGetGestureCode();

    public native static String nativeHwGetVersion();

    public native static String nativeHwGetDictVersion();

    public native static int nativeHwRecoDoc(short[] stroke, ArrayList<String> strings,
                                             ArrayList<Integer> rects);

}

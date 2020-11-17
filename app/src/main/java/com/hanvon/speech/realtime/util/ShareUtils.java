package com.hanvon.speech.realtime.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.hanvon.speech.realtime.bean.Result.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * 图片相关的工具类
 */
public class ShareUtils {


    public static void generateHtml(String path, String txt, ArrayList<String> img) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException io) {

        }

        StringBuilder stringHtml = new StringBuilder();
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(path));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        stringHtml.append("<html><head>");

        stringHtml.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

        stringHtml.append("<title>语音记事分享</title><style>*{margin:10;padding:0;}.div1{margin:10 auto;}</style>");

        stringHtml.append("</head>");

        stringHtml.append("<body  bgcolor=\"#efefef\">");

        stringHtml.append("<p style=\"font-size:50px;border:1px solid black;\">" + txt + "</p>");

        for (int i = 0; i < img.size(); i++) {
            //String p = img.get(i).substring(img.get(i).length() - 5, img.get(i).length());
            stringHtml.append("<img src=\"").append(img.get(i).substring(img.get(i).length() - 5, img.get(i).length())).append("\">");//<center>这是被居中的的文本</center>
            stringHtml.append("<center>(" + (i+1) + "/" + img.size() + ")</center>");
        }

        stringHtml.append("</body></html>");

        try {
            printStream.println(stringHtml.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 将图片转换成Base64编码的字符串
     *
     * @param path 图片本地路径
     * @return base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void saveBase64ToFile(String content, String path) {

        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(path);
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
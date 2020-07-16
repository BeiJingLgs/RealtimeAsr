package com.hanvon.speech.realtime.util;


import android.graphics.Rect;
import android.text.Layout;
import android.util.Log;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.Constants;

public class TextViewUtils {
    /**
     * 获取TextView某一个字符的坐标位置
     *
     * @return 返回的是相对坐标
     * @parms tv
     * @parms index 字符索引
     */
    public static Rect getTextViewSelectionRect(TextView tv, int index) {
        Layout layout = tv.getLayout();
        Rect bound = new Rect();
        int line = layout.getLineForOffset(index);
        layout.getLineBounds(line, bound);
        int yAxisBottom = bound.bottom;//字符底部y坐标
        int yAxisTop = bound.top;//字符顶部y坐标
        int xAxisLeft = (int) layout.getPrimaryHorizontal(index);//字符左边x坐标
        int xAxisRight = (int) layout.getSecondaryHorizontal(index);//字符右边x坐标
        //xAxisRight 位置获取后发现与字符左边x坐标相等，如知道原因请告之。暂时加上字符宽度应对。
        if (xAxisLeft == xAxisRight) {
            String s = tv.getText().toString().substring(index, index + 1);//当前字符
            xAxisRight = xAxisRight + (int) tv.getPaint().measureText(s);//加上字符宽度
        }
        int tvTop = tv.getScrollY();//tv绝对位置
        return new Rect(xAxisLeft, yAxisTop + tvTop, xAxisRight, yAxisBottom + tvTop);

    }

    /**
     * 获取TextView触点坐标下的字符
     *
     * @param tv tv
     * @param x  触点x坐标
     * @param y  触点y坐标
     * @return 当前字符
     */
    public static String getTextViewSelectionByTouch(TextView tv, int x, int y, int page) {
        String s = "";
        //String p = tv.getText().toString();
        String text[] = tv.getText().toString().split("。|？|，|！");
        LogUtils.printErrorLog("append", "text.length: " + text.length);
        String content = tv.getText().toString();
        for (int j = 0; j < text.length; j++) {
            int tempY = y;
            int index = content.indexOf(text[j]);
            //LogUtils.printErrorLog("append", "起始坐标: " + index + "  起始字符: " + content.charAt(index));
            Rect rect = getTextViewSelectionRect(tv, index);

            //LogUtils.printErrorLog("append", "before rect.left: " + rect.left + "  rect.right: " + rect.right
            //       + "  rect.top: " + rect.top + "  rect.bottom: " + rect.bottom);
            int offset = (int) tv.getPaint().measureText("挖") * text[j].length();

            int right = rect.right + offset;
            int lineNum = 0;
            Rect rect2 = new Rect();
            Rect rect3 = new Rect();
            if (right > Constants.WIDTH) {
                lineNum = right / Constants.WIDTH;
                if (lineNum > 1) {
                    rect3.top = rect.bottom;
                    rect3.left = 0;
                    rect3.right = Constants.WIDTH;
                    rect3.bottom = rect.bottom + tv.getLineHeight() * lineNum;
                } else {
                    rect.right = Constants.WIDTH;

                    rect2.left = 0;
                    rect2.right = right - (Constants.WIDTH * lineNum);
                    rect2.bottom = rect.bottom + tv.getLineHeight() * lineNum;
                    rect2.top = rect.bottom - tv.getLineHeight();
                }
            } else {
                rect.right = right;
            }
            if (page != 0) {
                rect.bottom = rect.bottom - 215 * page;
                rect.top = rect.top - 215 * page;

                rect2.bottom = rect2.bottom - 215 * page;
                rect2.top = rect2.top - 215 * page;

                rect3.bottom = rect3.bottom - 215 * page;
                rect3.top = rect3.top - 215 * page;
            }
            tempY += (tv.getLineHeight() * 5 * page);

            LogUtils.printErrorLog("append", "x: " + x + "  y: " + tempY);

            LogUtils.printErrorLog("append", "  起始字符: " + content.charAt(index) + "  after rect.left: " + rect.left + "  rect.right: " + rect.right
                    + "  rect.top: " + rect.top + "  rect.bottom: " + rect.bottom);
            if (right < Constants.WIDTH) {
                if (x < rect.right && x > rect.left && tempY < rect.bottom && tempY > rect.top) {
                    s = text[j];//当前字符
                    break;
                }
            } else if (lineNum > 1) {
                if ((x < rect.right && x > rect.left && tempY < rect.bottom && tempY > rect.top)
                        || (x < rect2.right && x > rect2.left && tempY < rect2.bottom && tempY > rect2.top)
                        || (x < rect3.right && x > rect3.left && tempY < rect3.bottom && tempY > rect3.top)) {
                    s = text[j];//当前字符
                    break;
                }
            } else {
                if ((x < rect.right && x > rect.left && tempY < rect.bottom && tempY > rect.top)
                        || (x < rect2.right && x > rect2.left && tempY < rect2.bottom && tempY > rect2.top)) {
                    s = text[j];//当前字符
                    break;
                }
            }

        }
        return s;
    }
}
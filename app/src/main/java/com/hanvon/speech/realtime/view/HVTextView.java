package com.hanvon.speech.realtime.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.util.LogUtils;

public class HVTextView extends TextView {
    private static final String TAG = "HVTextView";

    int pageCount = 0;
    int pageIdx = -1;
    public static int OFFSET_LINE = 76;//48

    public HVTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.setMovementMethod(ScrollingMovementMethod.getInstance());
        this.setTextColor(Color.BLACK);
        pageCount = 0;
        pageIdx = -1;
        LogUtils.printErrorLog(TAG, "OFFSET_LINE: " + OFFSET_LINE);
    }

    public HVTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setMovementMethod(ScrollingMovementMethod.getInstance());
        this.setTextColor(Color.BLACK);
        pageCount = 0;
        pageIdx = -1;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        //return super.onTouchEvent(event);
        return true;
    }


    @Override
    protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
        // TODO Auto-generated method stub
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.scrollTo(0, 0);
    }

    public boolean isFirstPage() {
        return this.getScrollY() <= 0;
    }

    public boolean isLastPage() {
        int viewHeight = this.getHeight();
        int lineCount = this.getLineCount();
        Rect rtLine = new Rect();
        getLineBounds(lineCount - 1, rtLine);
        if (getPageCount() == 0)
            return true;
        else
            return  (getPageIdx() + 1) == getPageCount();
       // return this.getScrollY() >= (rtLine.bottom - viewHeight);
    }

    public boolean pagerPrev() {
        if (isFirstPage())
            return false;
        int offset = -Math.min(this.getHeight() - OFFSET_LINE, this.getScrollY());
        this.scrollBy(0, offset);
        return true;
    }

    public boolean pagerNext() {
        if (isLastPage())
            return false;

        int lineCount = this.getLineCount();
        Rect rtLine = new Rect();
        getLineBounds(lineCount - 1, rtLine);

        OFFSET_LINE = getResources().getInteger(R.integer.OFFSET_LINE);
        int offset = Math.min(this.getHeight() - OFFSET_LINE, rtLine.bottom - this.getScrollY());
        this.scrollBy(0, offset);
        return true;
    }

    public boolean gotoPage(int nPage) {
        int nCount = getPageCount();
        if (nPage >= nCount) {
            return false;
        }

        int lineCount = this.getLineCount();
        Rect rtLine = new Rect();
        getLineBounds(lineCount - 1, rtLine);
        OFFSET_LINE = getResources().getInteger(R.integer.OFFSET_LINE);
        this.scrollBy(0, (this.getHeight() - OFFSET_LINE) * nPage );
        return true;
    }

    public void gotoLastPage() {
        LogUtils.printErrorLog(TAG, "gotoLastPage isLastPage: " + isLastPage());
        LogUtils.printErrorLog(TAG, "getPageCount(): " + getPageCount());
        if (!isLastPage())
            gotoPage(getPageCount() - 1);
    }

    public int getPageCount() {
        {
            int lineCount = this.getLineCount();
            if (lineCount != 0) {
                Rect rtLine = new Rect();
                getLineBounds(lineCount - 1, rtLine);
                int viewHeight = this.getHeight();
                pageCount = (rtLine.bottom + viewHeight - 6  - OFFSET_LINE - OFFSET_LINE) / (viewHeight - OFFSET_LINE);
            } else {
                return 0;
            }
        }

        return pageCount;
    }

    public int getPageIdx() {
        if (this.getHeight() != 0) {
          /*  if (this.getScrollY() / this.getHeight() == 1)
                return this.getScrollY() / this.getHeight();
            else*/
          OFFSET_LINE = getResources().getInteger(R.integer.OFFSET_LINE);
          LogUtils.printErrorLog(TAG, "this.getScrollY(): " + this.getScrollY() + "  this.getHeight(): " + this.getHeight());
          return (this.getScrollY()) / (this.getHeight() - OFFSET_LINE);
        } else {
            return 0;
        }
    }
}

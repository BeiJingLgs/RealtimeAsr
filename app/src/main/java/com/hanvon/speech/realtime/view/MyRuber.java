package com.hanvon.speech.realtime.view;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

import com.xrz.BasePen;
import com.xrz.FlushInfo;
import com.xrz.PenPoint;

import java.util.LinkedList;

public class MyRuber extends BasePen {

    @Override
    public int getType(){
        return TYPE_RUBBER;
    }

    @Override
    public int getMinStrokeWidth(){
        return 10;
    }

    @Override
    public int getMaxStrokeWidth(){
        return 50;
    }

    @Override
    public void setStrokeWidthRange(int minStrokeWidth,int maxStrokeWidth){

    }

    @Override
    public void drawPath(Canvas[] canvases, Path path) {

    }

    @Override
    public FlushInfo draw(Canvas[] var1, LinkedList<PenPoint> var2) {
        Rect dirtyRect = new Rect();
        return new FlushInfo(dirtyRect);
    }
}

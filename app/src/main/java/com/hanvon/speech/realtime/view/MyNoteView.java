package com.hanvon.speech.realtime.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import com.xrz.NoteView;
import com.xrz.PenPoint;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
public class MyNoteView extends NoteView {

    private static final String TAG = "MyNoteView";
    private Context mContext;
    private PenPoint lastPoint = new PenPoint();
    private Paint paint = new Paint();
    private int mStrokeWidth = 4;
    private Paint rubberPaint = new Paint();
    private int mRubberWidth = 60;
    private boolean moveOutSideFlag = false;
    Bitmap lastPic;

    public MyNoteView(Context context) {
        this(context, null);
    }

    public MyNoteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyNoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyNoteView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    @Override
    public void onCreated() {
        String path = mContext.getFilesDir().getAbsolutePath();
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        lastPic = BitmapFactory.decodeFile(path+"/handwrite.png", bfoOptions);
        super.setForeground(lastPic);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setAntiAlias(true);
        paint.setDither(true);

        //init rubber paint
        rubberPaint.setColor(Color.TRANSPARENT);
        rubberPaint.setStrokeWidth(mRubberWidth);
        rubberPaint.setStyle(Paint.Style.STROKE);
        rubberPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //use round
        rubberPaint.setStrokeCap(Paint.Cap.ROUND);
        rubberPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"saveBitmap handwrite.png");
        saveBitmap("handwrite.png",super.getHandwrittenBitmap());
        if(lastPic!=null){
            lastPic.recycle();
        }
    }

    @Override
    public void onDrawBackground(Canvas bgCanvas, int width, int height, Bitmap bgbitmap) {
        super.onDrawBackground(bgCanvas, width, height, bgbitmap);
    }

    @Override
    public int onInputTouch(int eventType, int x, int y, int pressure, int toolType) {
        return super.onInputTouch(eventType, x, y, pressure, toolType);
    }

    /**
     *
     * @param canvases canvases need to draw
     * @param points points to be drawn this time
     * @param drawMode NoteView.DRAW_PEN_MODE or NoteView.DRAW_RUBBER_MODE
     * @param autoStrokeWidthEnabled whether auto stroke width is enabled
     * @return dirty area drawn this time
     */
    @Override
    public Rect onDraw(Canvas[] canvases, LinkedList<PenPoint> points, int drawMode, boolean autoStrokeWidthEnabled) {
        return super.onDraw(canvases, points,drawMode, autoStrokeWidthEnabled);

        //DEMO CODE:
        /*Rect dirtyRect = new Rect();
        Path path = new Path();
        int border;

        if(lastPoint.isValid()) {
            path.reset();
            path.moveTo(lastPoint.getX(), lastPoint.getY());
        }
        for (PenPoint p : points) {
            //Log.d(TAG,"("+p.getEventType()+","+p.getX()+","+p.getY()+","+p.mPressure+")"+"isOutside:"+p.isOutside());
            if(p.isOutside()){
                moveOutSideFlag = true;
                continue;
            }
            int x = p.getX();
            int y = p.getY();
            switch (p.getEventType()) {
                case NoteView.ACTION_DOWN:
                    path.reset();
                    path.moveTo(x, y);
                    lastPoint = p;
                    break;
                case NoteView.ACTION_MOVE:
                    if(moveOutSideFlag){
                        path.reset();
                        path.moveTo(x, y);
                        lastPoint=p;
                    }else{
                        path.lineTo(x, y);
                    }
                    break;
                case NoteView.ACTION_UP:
                    path.lineTo(x, y);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + p.getEventType());
            }
            moveOutSideFlag = false;
            if (drawMode == NoteView.DRAW_PEN_MODE){
                border = mStrokeWidth;
            }else{
                border = mRubberWidth;
            }
            //Log.d(TAG, "last(" + lastPoint.getX()+","+lastPoint.getY()+")"+"this("+x+","+y+")"+"border="+border);
            dirtyRect.union(
                    Math.min(lastPoint.getX(), x) - border/2,
                    Math.min(lastPoint.getY(), y) - border/2,
                    Math.max(lastPoint.getX(), x) + border/2,
                    Math.max(lastPoint.getY(), y) + border/2);
            //Log.d(TAG, "dirtyRect:" +dirtyRect );
            lastPoint = p;
        }

        try {
            if( drawMode == NoteView.DRAW_PEN_MODE) {
                for (Canvas canvas : canvases) {
                    canvas.drawPath(path, paint);
                }
            }else{
                canvases[0].drawPath(path, rubberPaint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirtyRect;*/
    }

    public void saveBitmap(String path) {
        super.setForeground(lastPic);
        //path = path.trim();
        saveBitmap( "handwrite.png",super.getHandwrittenBitmap());
        saveBitmap( "backgroud.png",super.getBackgroundBitmap());
        saveBitmap("blend.png",super.getBlendBitmap());
    }

    public void saveBitmap(String name, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = mContext.openFileOutput(name, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

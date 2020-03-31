package com.hanvon.speech.realtime.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.hanvon.speech.realtime.model.note.NoteBaseData;
import com.hanvon.speech.realtime.model.note.Record;
import com.hanvon.speech.realtime.model.note.Trace;
import com.hanvon.speech.realtime.ui.IatActivity;
import com.hanvon.speech.realtime.util.LogUtils;
import com.xrz.FlushInfo;
import com.xrz.NoteView;
import com.xrz.PenPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import static com.hanvon.speech.realtime.util.CommonUtils.getScreenWidth;

public class HandWriteNoteView extends NoteView {

    private static final String TAG = "MyNoteView";
    private Context mContext;
    private PenPoint lastPoint = new PenPoint();
    private PenPoint prevPoint = new PenPoint(); // 接收到的前一个点。
    private Paint paint = new Paint();
    private int mStrokeWidth = 5;
    private int penColor = Color.BLACK;
    private boolean bPenDown = false;

    public int penType = TP_PEN; // 0 笔， 1 橡皮
    public static int TP_PEN = 0;
    public static int TP_ERASER = 1;

    private ArrayList<Trace> mTracePage = null;  // 便笺的集合
    private Trace mCurTrace = null;
    private boolean canBeFresh = true;

    /** 便笺内容是否被修改过 */
    private boolean isModified = false;
    private boolean isMemoEmpty = false;

    private boolean m_penErase = false;
    private int m_oldstrokeType = TP_PEN;

    Timer timer = null;
    TimerTask timerTask = null;

    public HandWriteNoteView(Context context) {
        this(context, null);
    }

    public HandWriteNoteView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public HandWriteNoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HandWriteNoteView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    @Override
    public void onCreated() {
        init();
        super.onCreated();
    }

    /**
     * 获取所有笔迹的内容
     *
     * @return 返回笔迹的列表
     */
    public ArrayList<Trace> getTraces() {
        return mTracePage;
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(penColor);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    /**
     *
     * @param canvases canvases need to draw
     * @param points points to be drawn this time
    //     * @param drawMode NoteView.DRAW_PEN_MODE or NoteView.DRAW_RUBBER_MODE
     *
     * @return dirty area drawn this time
     */
    @Override
    public FlushInfo onDraw(Canvas[] canvases, LinkedList<PenPoint> points) {
        Rect dirtyRect = new Rect();
        Path path = new Path();
        int border = mStrokeWidth;


        boolean bUp = false;
        if(lastPoint.isValid()) {
            path.moveTo(lastPoint.getX(), lastPoint.getY());
        }
        for (PenPoint p : points) {
            if (p.getToolType() == 2)
                continue;

            if(p.isOutside()){
                canBeFresh = true;
                continue;
            }

            if (p.getToolType() == 1 && penType == TP_PEN) {
                // 橡皮头的范围
                m_penErase = true;
                penType = TP_ERASER; // 临时改，up的时候再改回去
            }

            int x = p.getX();
            int y = p.getY();
            switch (p.getEventType()) {
                case NoteView.ACTION_DOWN:
                    bUp = false;
                    path.moveTo(x, y);
                    lastPoint = p;
                    bPenDown = true;
                    lastPoint = p;

                    if(penType == TP_PEN && p.getToolType() == 0 && !p.isOutside()) {
                        if (mCurTrace != null) {
                            mCurTrace.clear();
                        }else{
                            mCurTrace = new Trace();
                        }

                        mCurTrace.setWidth(mStrokeWidth);

                        Point pdown = new Point(p.getX(), p.getY());
                        mCurTrace.addPoint(pdown);
                        //Log.i(TAG, "onDraw down, "+ "x: "+p.getX() + ", y: "+ p.getY() );
                        // 判断当前是否正在录音
                        if (((IatActivity)mContext).isRecording()) {
                            long time = ((IatActivity)mContext).getCurrrentRecordTime();
                            mCurTrace.RecInfo = new Record();
                            mCurTrace.RecInfo.nTimeBegin = time;
                            mCurTrace.RecInfo.nTimeEnd = time;
                            mCurTrace.bHasRec = true;
                        }
                    }

                    break;
                case NoteView.ACTION_MOVE:
                    bUp = false;
                    if (!bPenDown){
                        path.moveTo(x, y);
                        lastPoint = p;

                        if(penType == TP_PEN && p.getToolType() == 0 && !p.isOutside()) {
                            if (mCurTrace != null) {
                                mCurTrace.clear();
                            }else{
                                mCurTrace = new Trace();
                            }
                            mCurTrace.setWidth(mStrokeWidth);
                        }
                        bPenDown = true;
                    }else {
                        path.lineTo(x, y);
                    }
                    lastPoint = p;

                    if(mCurTrace != null && penType == TP_PEN && p.getToolType() == 0 && !p.isOutside()) {
                        Point pmove = new Point(p.getX(), p.getY());
                        mCurTrace.addPoint(pmove);
                    }
                    break;
                case NoteView.ACTION_UP:
                    bUp = true;
//                    Log.i(TAG, "onDraw UP, "+ "x: "+p.getX() + ", y: "+ p.getY() );
                    if (bPenDown) {
                        path.lineTo(x, y);
                        bPenDown = false;
                    }
                    lastPoint = p;
                    if(mCurTrace != null && penType == TP_PEN && p.getToolType() == 0 && !p.isOutside()){
                        Point pup = new Point(p.getX(), p.getY());
                        mCurTrace.addPoint(pup);
                        mTracePage.add(mCurTrace);
                        mCurTrace = null;
                        setModified(true);
                        isMemoEmpty = false;
                    }
                    break;
                case NoteView.ACTION_LEAVE:
                case NoteView.ACTION_NEAR:
                    bUp = false;
//                    Log.i(TAG, "onDraw, "+ "x: "+p.getX() + ", y: "+ p.getY() );
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + p.getEventType());
            }

            dirtyRect.union(
                    Math.min(lastPoint.getX(), x) - border,
                    Math.min(lastPoint.getY(), y) - border,
                    Math.max(lastPoint.getX(), x) + border,
                    Math.max(lastPoint.getY(), y) + border);
        }

        try {
            if( penType == TP_PEN  && lastPoint.getToolType() != 1) {
                for (Canvas canvas : canvases) {
                    canvas.drawPath(path, paint);
                }

            }else{
                // 橡皮,线条擦除
                if (prevPoint.isValid()){
                   boolean bErase = eraseTrace(prevPoint.getX(),prevPoint.getY(),
                            lastPoint.getX(), lastPoint.getY());

                    if (bErase){
                        // 重新加载psl批注层
                        updateForeground(this.getHeight(),this.getWidth());
                    }
                }

                if (bPenDown)
                    prevPoint = lastPoint;
                else {
                    prevPoint.reset();
                }

                if(bUp && m_penErase){
                    penType = m_oldstrokeType;
                    m_penErase = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bUp){
            if(timer != null){
                timer.cancel();
                timer = null;
            }
            if(timerTask != null){
                timerTask.cancel();
                timerTask = null;
            }
            Timer timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                    canBeFresh = true;
                }
            };
            timer.schedule(timerTask, 1000);//延时2s
        }

        return new FlushInfo(dirtyRect);
    }

    private Handler handler;
    public void setHandler(Handler mhandler) {
        this.handler = mhandler;
    }

    private boolean eraseTrace(int pt1x, int pt1y, int pt2x, int pt2y){

        if(pt1x == pt2x && pt1y == pt2y){
            return false;
        }

        boolean berase = false;
        // 判断当前的轨迹是否是一点
        ArrayList<Integer> intersect = null;

        // 将当前点和上一个点连接成线并四周扩张成为一个四边形
        Point[] curQuad = new Point[4];
        int lenth1 = Math.abs(pt2x - pt1x);
        int lenth2 = Math.abs(pt2y - pt1y);
        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        if(lenth1 == 0 || lenth2/lenth1 >= 1){ //竖线 或斜率大于45度
            if(pt1y < pt2y){
                x1 = pt1x;
                y1 = pt1y;
                x2 = pt2x;
                y2 = pt2y;
            }
            else{
                x1 = pt2x;
                y1 = pt2y;
                x2 = pt1x;
                y2 = pt1y;
            }
            curQuad[0] = new Point(Math.max((x1 - 5),0), Math.max((y1 - 5),0));
            curQuad[1] = new Point(Math.min((x1 + 5),getHeight()),Math.max((y1 - 5),0));
            curQuad[2] = new Point(Math.min((x2 + 5),getHeight()),Math.min((y2 + 5),getWidth()));
            curQuad[3] = new Point(Math.max((x2 - 5),0),Math.min((y2 + 5),getWidth()));
        }
        else if(lenth2 == 0 || lenth2/lenth1 < 1){ //横线
            if(pt1x < pt2x){
                x1 = pt1x;
                y1 = pt1y;
                x2 = pt2x;
                y2 = pt2y;
            }
            else{
                x1 = pt2x;
                y1 = pt2y;
                x2 = pt1x;
                y2 = pt1y;
            }
            curQuad[0] = new Point(Math.max((x1 - 5),0), Math.max((y1 - 5),0));
            curQuad[1] = new Point(Math.min((x2 + 5),getHeight()),Math.max((y2 - 5),0));
            curQuad[2] = new Point(Math.min((x2 + 5),getHeight()),Math.min((y2 + 5),getWidth()));
            curQuad[3] = new Point(Math.max((x1 - 5),0), Math.min((y1 + 5),getWidth()));
        }

        // 判断四边形和笔迹相交
        intersect = Trace.getIntersect2(mTracePage, curQuad);
        if (intersect != null && intersect.size() > 0) {
            // 删除相交的笔迹
            for (int i = intersect.size() - 1; i >= 0; i--) {
                int delIdx = intersect.get(i);
                if (delIdx < mTracePage.size()) {
                    mTracePage.remove(delIdx);
                    berase = true;
                }
            }
        }
        return berase;
    }

    //Bitmap m_foreBitmap;
    public void updateForeground(int width,int height){
        /*if(mTracePage.size() == 0){
            this.clear(false);
            return;
        }*/

        Bitmap bmp = Bitmap.createBitmap(width,height , Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bmp);

        Path path;
        int nTracecount = mTracePage.size();
        for (int i = 0; i < nTracecount; i++) {
            Trace trace = mTracePage.get(i);
            if (trace == null || trace.getCount() == 0)
                continue;

            path = new Path();
            path.moveTo(trace.getAt(0).x, trace.getAt(0).y);

            for (int j = 0; j < trace.getCount(); j++) {
                path.lineTo(trace.getAt(j).x, trace.getAt(j).y);

            }
            canvas.drawPath(path, paint);
            //MemoOperateJni.getImageInfo(m_pixels, m_height, m_width, curPicPath);
        }
       // m_foreBitmap = Bitmap.createBitmap(bmp);
        setForeground(bmp);
    }


    public void updateImage(int idx) {
        initialize(NoteBaseData.gTraFile.getPage(idx).traces);
        init();
        int width = getWidth();
        int height = getHeight();
        if(width == 0){
            width = NoteBaseData.gTraFile.getWidth();
        }
        if(height == 0){
            height = NoteBaseData.gTraFile.getHeight();
        }
        // TODO Auto-generated method stub
        updateForeground(height, width);
    }


    /**
     * 初始化笔迹的相关数据,mTracePage和traces的数据不相关联
     *
     * @param traces
     *            笔迹的组合
     */
    public void initialize(ArrayList<Trace> traces) {
        if(mTracePage != null)
        {
            for(int i = mTracePage.size() - 1; i >= 0; i--){
                mTracePage.get(i).clear();
                mTracePage.remove(i);
            }
            mTracePage.clear(); // 便笺的集合
        }
        else
        {
            mTracePage = new ArrayList<Trace>();
        }
        if(traces != null){
            for(Trace trace:traces){
                Trace newTrace = trace.deepClone();
                mTracePage.add(newTrace);
            }
        }
    }

    public void saveBitmap(String name,Bitmap bitmap) {
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

    /**
     * 保存当前画布的所有信息到图像
     *
     * @param path
     *            图像路径
     * @return 是否成功
     */
    public boolean saveCanvasInfo(String path) {
        Bitmap bitmap = Bitmap.createBitmap(getHeight(),getWidth(), Bitmap.Config.RGB_565);
        bitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        matrix.postScale((float)0.68, (float)0.68);
        Bitmap bkBitmap = super.getBackgroundBitmap();
        Bitmap bkBitmap1 = super.getHandwrittenBitmap();
        if (null != bkBitmap) {
            canvas.drawBitmap(bkBitmap, 0,0, null);
        }
        if (null != bkBitmap1) {
            canvas.drawBitmap(bkBitmap1, 0,0,  null);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        File file = new File(path);
        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }catch(java.io.IOException e){
            e.printStackTrace();
            return false;
        } finally {
            bitmap.recycle();
        }
        return true;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }


    /**
     * 清除所有笔迹
     */
    public void clearAllMemo() {
        this.clear(false);
        // 删除所有笔迹
        if(mTracePage != null){
            for(int i = mTracePage.size() - 1; i >= 0; i--){
                mTracePage.get(i).clear();
                mTracePage.remove(i);
            }
            mTracePage.clear();
        }
        setModified(true);
        isMemoEmpty = true;
    }

    public boolean canBeFresh()
    {
        return canBeFresh;
    }

}

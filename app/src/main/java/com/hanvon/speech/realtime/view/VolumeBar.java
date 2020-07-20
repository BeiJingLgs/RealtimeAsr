package com.hanvon.speech.realtime.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.asr.ai.speech.realtime.R;

public class VolumeBar extends View implements Runnable
{
	public final static int DEFAULT_VOLUME = 10;
	public final static int MIN_VOLUME = 0;
	public final static int MAX_VOLUME = 15;
	
	private final int SPEAKER_PADDING = 2;
	private int VOLBAR_WIDTH = 10;
	private int VOLBAR_HEIGHT = 28;
	private int VOLBAR_SPACE = 5;
	
	private int mVolume = DEFAULT_VOLUME;
	private Bitmap mBmpSpeaker;
	private Bitmap mBmpHeadset;
	private boolean mShowBar = true;
	private boolean mHeadsetPlug = false;
	
	private Paint mBackPaint = new Paint();
	private Paint mBarPaint = new Paint();
	private Rect mSrcBmpRect = new Rect();
	private Rect mDstBmpRect = new Rect();
	private Rect mBarRect = new Rect();

	private Handler handler = new Handler();
	
	public VolumeBar(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		VOLBAR_WIDTH = (int) getResources().getDimension(R.dimen.volbar_width);	
		VOLBAR_HEIGHT = (int) getResources().getDimension(R.dimen.volbar_height);	
		VOLBAR_SPACE = (int) getResources().getDimension(R.dimen.volbar_space);	
		
		
		mBmpSpeaker = BitmapFactory.decodeResource(getResources(), R.drawable.speaker);
		mBmpHeadset = BitmapFactory.decodeResource(getResources(), R.drawable.headphone);
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		
		if (mShowBar)
		{
			if (mBmpSpeaker != null)
			{
				mSrcBmpRect.left = 0;
				mSrcBmpRect.top = 0;
				mSrcBmpRect.right = mBmpSpeaker.getWidth();
				mSrcBmpRect.bottom = mBmpSpeaker.getHeight();
				
				int hSpeaker = getHeight() - 2 * SPEAKER_PADDING;
				mDstBmpRect.left = SPEAKER_PADDING;
				mDstBmpRect.top = SPEAKER_PADDING;
				mDstBmpRect.bottom = mDstBmpRect.top + hSpeaker;
				mDstBmpRect.right = mDstBmpRect.left + (int)(hSpeaker * (mBmpSpeaker.getWidth()/(float)mBmpSpeaker.getHeight()));
				
				if (mHeadsetPlug)
					canvas.drawBitmap(mBmpHeadset, mSrcBmpRect, mDstBmpRect, null);
				else
					canvas.drawBitmap(mBmpSpeaker, mSrcBmpRect, mDstBmpRect, null);
				
				for (int i = 0; i < MAX_VOLUME; i++)
				{
					mBarRect.left = mDstBmpRect.right + i * (VOLBAR_WIDTH + VOLBAR_SPACE);
					mBarRect.top = (int)(getHeight() - VOLBAR_HEIGHT)/2 + 1;
					mBarRect.right = mBarRect.left + VOLBAR_WIDTH;
					mBarRect.bottom = mBarRect.top + VOLBAR_HEIGHT;
					
					if (i < mVolume)
						mBarPaint.setStyle(Paint.Style.FILL);
					else
						mBarPaint.setStyle(Paint.Style.STROKE);
					
					mBarPaint.setColor(Color.BLACK);
					
					canvas.drawRect(mBarRect, mBarPaint);				
				}
				
				mShowBar = true;
				handler.postDelayed(this, 5 * 1000);				
			}
		}
		else
		{	
			mBarRect.left = 0;
			mBarRect.top = 0;
			mBarRect.right = getWidth();
			mBarRect.bottom = getHeight();
			
			mBackPaint.setStyle(Paint.Style.FILL);
			mBackPaint.setColor(Color.WHITE);
			
			canvas.drawRect(mBarRect, mBackPaint);
		}
	}

	
	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		
		handler.removeCallbacks(this);
	}

	public void headsetPlug(boolean plug)
	{
		mHeadsetPlug = plug;
	}
	
	public void AdjustVolume(int vol, boolean redraw)
	{
		mVolume = vol;
		
		if (redraw)
		{
			mShowBar = true;
			handler.post(this);
		}
	}

	public void run() 
	{
		invalidate();
		
		handler.removeCallbacks(this);
	}
}

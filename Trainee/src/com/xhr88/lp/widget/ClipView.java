package com.xhr88.lp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 * 裁剪头像的工具
 * 
 * @author zou.sq
 * @since 2012-11-1
 * @version 时间，作者，更改内容
 */
public class ClipView extends View {

	private static final int COLOR = 0xAA000000;
	private static final int THREE = 3;
	private static final int SIX = 6;

	private int mOutputX = 640;
	private int mOutputY = 640;
	// 画选取框
	Paint mPaint = new Paint();
	Paint mRectPaint = new Paint();

	/**
	 * 
	 * @param context
	 *            上下文对象
	 */
	public ClipView(Context context) {
		this(context, null);
	}

	/**
	 * 
	 * @param context
	 *            上下文对象
	 * @param attrs
	 *            属性集
	 */
	public ClipView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 
	 * @param context
	 *            上下文对象
	 * @param attrs
	 *            属性集
	 * @param defStyle
	 *            类型
	 */
	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 
	 * @param outputX
	 *            the mOutputX to set
	 */
	public void setmOutputX(int outputX) {
		this.mOutputX = outputX;
	}

	/**
	 * @param outputY
	 *            the mOutputY to set
	 */
	public void setmOutputY(int outputY) {
		this.mOutputY = outputY;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeMiter(SIX);
		mPaint.setStrokeWidth(THREE);
		mPaint.setColor(Color.WHITE);
		mOutputX = width;
		mOutputY = mOutputX;
		canvas.drawRect((width - mOutputX) / 2, (height - mOutputY) / 2, (width + mOutputX) / 2,
				(height + mOutputY) / 2, mPaint);

		mRectPaint.setColor(COLOR);
		canvas.drawRect(0, 0, width, (height - mOutputY) / 2, mRectPaint);
		canvas.drawRect(0, (height - mOutputY) / 2, (width - mOutputX) / 2, (height + mOutputY) / 2, mRectPaint);
		canvas.drawRect((width + mOutputX) / 2, (height - mOutputY) / 2, width, (height + mOutputY) / 2, mRectPaint);
		canvas.drawRect(0, (height + mOutputY) / 2, width, height, mRectPaint);
	}

}

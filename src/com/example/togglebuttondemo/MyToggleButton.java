package com.example.togglebuttondemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MyToggleButton extends View {

	private Bitmap slideBackground;
	private Bitmap slideIcon;
	private int backgroundWidth;
	private int backgroundHeight;
	/** 画滑块的时候，滑块左边的位置 */
	private int slideIconLeft;
	private int slideWidth;
	/** 滑块往右滑时，left的最大值 */
	private int slideIconMostLeft;
	/** 指示开关状态，如果为true则是开的状态 */
	private boolean isOpen;
	/** 如果为true则表示可以去计算开或关的状态了 */
	private boolean canCalcState;
	private OnStateChangedListener onStateChangedListener;

	/**
	 * 在xml代码中使用，由系统调用
	 * @param context
	 * @param attrs
	 */
	public MyToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 在xml代码中使用，由系统调用
	 * @param context
	 * @param attrs
	 */
	public MyToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		String namespace = "http://schemas.android.com/apk/res/com.example.togglebuttondemo";
		// 读取滑块背景和滑块icon属性
		int slideBackgroundResId = attrs.getAttributeResourceValue(namespace, "slide_background", -1);
		int slideIconResId = attrs.getAttributeResourceValue(namespace, "slide_icon", -1);
		
		if (slideBackgroundResId != -1 && slideIconResId != -1) {
			setSwitchImage(slideBackgroundResId, slideIconResId);
		}
		
		// 读取开关状态属性
		isOpen = attrs.getAttributeBooleanValue(namespace, "state", false);
		setState(isOpen);
	}

	/**
	 * 在Java代码中使用，由开发人员调用
	 * @param context
	 */
	public MyToggleButton(Context context) {
		super(context);
	}
	
	/**
	 * 当需要测量View的大小的时候，系统会自动去调用
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(backgroundWidth, backgroundHeight);	// 设置ToggleButton的宽和高和背景图片一样
	}
	
	/***
	 * 当需要得绘的时候系统会自动调用
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// 画背景图片
		Bitmap bitmap = slideBackground;
		float left = 0;		// 指定水平方向从哪里开始画
		float top = 0;		// 指定垂直方向从哪里开始画
		Paint paint = null;	// 指定画笔
		canvas.drawBitmap(bitmap, left, top, paint);
		
		// 画滑块
		drawSlideIcon(canvas);
	}

	/** 画滑块 */
	private void drawSlideIcon(Canvas canvas) {
		// 预防超出边界
		if (slideIconLeft < 0) {
			slideIconLeft = 0;
		} else if (slideIconLeft > slideIconMostLeft) {
			// 滑块向右滑的时候，滑块的left不能大于 (背景宽 - 滑块宽)
			slideIconLeft = slideIconMostLeft;
		}
		
		canvas.drawBitmap(slideIcon, slideIconLeft, 0, null);
		
		if (canCalcState) {
			canCalcState = false;
			// 当手指抬起的时候，或者调用了setState方法的时候才可以计算开或关的状态
			
			// 判断状态是否发生改变，如果发生改变了，则通知监听器
			boolean isOpenTemp = slideIconLeft != 0;
			if (isOpenTemp != isOpen) {
				// 状态发生改变了
				if (onStateChangedListener != null) {
					onStateChangedListener.onStateChanged(isOpenTemp);
				}
				isOpen = isOpenTemp;
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 滑动的时候计算滑块left值：触摸位置 event.getX() - 滑块宽 / 2
			slideIconLeft = (int) (event.getX() - slideWidth / 2);
			break;
		case MotionEvent.ACTION_MOVE:
			slideIconLeft = (int) (event.getX() - slideWidth / 2);
			break;
		case MotionEvent.ACTION_UP:
//			手指松开时，计算滑画应该滑到最左边，还是滑到最右边：
			if (event.getX() < backgroundWidth / 2) {
//				如果手指按下的位置 < 背景 / 2，把滑块滑到最左边
				slideIconLeft = 0;
			} else {
//				把滑块滑到最右边
				slideIconLeft = slideIconMostLeft;
			}
			
			canCalcState = true;
			break;
		}
		
		invalidate();	// 告诉系统需要重新画界面，系统会自动去调用onDraw()方法
		return true;	// 返回true表示我们消费了这个触摸事件
	}

	/***
	 * 设置开关按钮的状态
	 * @param isOpen 是否要打开开关
	 */
	public void setState(boolean isOpen) {
		if (isOpen) {
			// 把这个滑块画成打开状态
			slideIconLeft = slideIconMostLeft;
		} else {
			// 把这个滑块画成关闭状态
			slideIconLeft = 0;
		}
		canCalcState = true;
		invalidate();	// 告诉系统需要重新画界面，系统会自动去调用onDraw()方法
	}

	/**
	 * 设置开关按钮的相关图片
	 * @param slideBackgroundResId 背景图片
	 * @param slideIconResId	滑块图片
	 */
	public void setSwitchImage(int slideBackgroundResId, int slideIconResId) {
		slideBackground = BitmapFactory.decodeResource(getResources(), slideBackgroundResId);
		slideIcon = BitmapFactory.decodeResource(getResources(), slideIconResId);
		
		// 获取背景图片的宽和高
		backgroundWidth = slideBackground.getWidth();
		backgroundHeight = slideBackground.getHeight();
		
		// 获取滑块的宽
		slideWidth = slideIcon.getWidth();
		
		slideIconMostLeft = backgroundWidth - slideWidth;
	}
	
	/**
	 * 状态发生改变的监听器
	 * @author Administrator
	 *
	 */
	public interface OnStateChangedListener {
		/**
		 *  当状态发生改变的时候会调用
		 */
		void onStateChanged(boolean isOpen);
	}

	/**
	 * 设置状态发生改变的监听器
	 * @param onStateChangedListener
	 */
	public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
		this.onStateChangedListener = onStateChangedListener;
	}
}


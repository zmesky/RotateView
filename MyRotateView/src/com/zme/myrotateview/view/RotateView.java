package com.zme.myrotateview.view;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.zme.myrotateview.MainActivity;
import com.zme.myrotateview.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.print.PrintAttributes.Margins;
import android.text.Layout.Alignment;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

@SuppressLint("DrawAllocation")
public class RotateView extends LinearLayout {
	
	public interface RotateViewListener {
		void onCurrentView(int item);
	}
	
	// 用于滑动的类
	public Scroller mScroller;
	// 当前的屏幕视图
	private int mCurScreen;
	private int mPreScreen;
	// 默认的显示视图
	private int mDefaultScreen = 0;
	// 用来处理立体效果的类
	private Camera mCamera;
	private Matrix mMatrix;
	// 旋转的角度
	private float angle = 90;
	private Context context;
	private RotateViewListener listener;

	private boolean isStop = true;
	public boolean isFirst = true;
	private int location;
	private int delta;
	private int speedNum = 15;// 旋转速度
	private long stopTime = 2000;// 停顿时间
	private int textSize = 15 ;//字体大小
	private int textColor = Color.parseColor("#666666") ;//字体颜色
	
	private int screenWidth ;

	public RotateView(Context context) {
		super(context);
		init(context);
	}

	public RotateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressLint("NewApi")
	public RotateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		this.context = context;
		mScroller = new Scroller(context);

		mCurScreen = mDefaultScreen;
		screenWidth = context.getResources().getDisplayMetrics().widthPixels;

		mCamera = new Camera();
		mMatrix = new Matrix();
	}

	public void setRotateViewListener(RotateViewListener listener) {
		this.listener = listener;

	}
	/**
	 * 轮询显示内容集合
	 * setShowContent( )
	 * @param contents 
	 *void
	 * @exception
	 */
	public void setShowContent(List<String> contents ,int height) {
	    for(String title : contents) {
	            this.addView(getInfoView(title,height));
	    }
	        
	}
	
	/**
	 * 设置字体大小
	 * setTextSize( )
	 * @param textSize 
	 *void
	 * @exception
	 */
	public void setTextSize(int textSize) {
	    this.textSize = textSize ;
	}
	/**
	 * 字体颜色
	 * setTextColor( )
	 * @param textColor 
	 *void
	 * @exception
	 */
	public void setTextColor(int textColor) {
	    this.textColor = textColor ;
	}
	/**
	 * 旋转速度
	 * setRotateSpeed( )
	 * @param speed 
	 *void
	 * @exception
	 */
	public void setRotateSpeed(int speed) {
	    this.speedNum = speed ;
	}
	/**
	 * 设置旋转角度大小   
	 * setRoateAngle( )
	 * @param angle 
	 *void
	 * @exception
	 */
	public void setRoateAngle(float angle) {
		this.angle = angle;
	}

	public void rorateTo(int index) {
		if (mScroller.isFinished()) // 如果返回true，表示动画还没有结束
			snapToScreen(index);
		else
			snapToDestination();
	}

	public void rorateToNext() {
		snapToScreen(mCurScreen + 1);
	}

	public int getLocation() {
		return location;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// 创建测量参数
		int cellWidthSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec,
				MeasureSpec.UNSPECIFIED);
		int cellHeightSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec,
				MeasureSpec.UNSPECIFIED);
		// 记录ViewGroup中Child的总个数
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			childView.measure(cellWidthSpec, cellHeightSpec);// 遍历子View 设置宽高
		}

		// 设置容器控件所占区域大小
		setMeasuredDimension(
				resolveSize(widthMeasureSpec * count, widthMeasureSpec),
				resolveSize(heightMeasureSpec * count, heightMeasureSpec));

	}

	private boolean isRefresh;

	public void setRefresh(boolean isRefresh) {
		this.isRefresh = isRefresh;
	}

	public void setIsStop(boolean isStop) {
		this.isStop = isStop;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed || isRefresh) {
			isRefresh = false;
			int childLeft = 0;
			int childTop = 0;
			int childCount = getChildCount();
			setHeadAndFoot();
			childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				setLayoutParams(childView);//设置子控件布局
				if (childView.getVisibility() != View.GONE) {
					childView.measure(r - l, b - t);
					final int childWidth = childView.getMeasuredWidth();
					int childHeight = childView.getMeasuredHeight();
					childHeight = getHeight();
					childView.layout(0, childTop, childWidth, childTop
							+ childHeight);
					childTop += childHeight;

				}
			}
		}
	}

	private void setHeadAndFoot() {
		ImageView img0 = new ImageView(context);
		ImageView img1 = new ImageView(context);
		View view0 = getChildAt(0);
		View view1 = getChildAt(getChildCount() - 1);
		if (view0 == null && view1 == null) {
			return;
		}
		setLayoutParams(view0);
		setLayoutParams(view1);
		img0.setImageBitmap(convertViewToBitmap(view0));
		img1.setImageBitmap(convertViewToBitmap(view1));
		addView(img1, 0);
		addView(img0);

	}

	private Bitmap convertViewToBitmap(View v) {
		v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		v.buildDrawingCache();
		Bitmap bitmap = v.getDrawingCache();
		return bitmap;
	}

	private void setLayoutParams(View view) {
		if (view == null) {
			return;
		}
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = getMeasuredHeight();
		layoutParams.width = getMeasuredWidth();
		view.setLayoutParams(layoutParams);

	}

	private void snapToScreen(int whichScreen) {
		whichScreen = (whichScreen >= getChildCount() - 1) ? getChildCount() - 1
				: whichScreen;
		if (getScrollY() != whichScreen * getHeight()) {
			mCurScreen = whichScreen;
			delta = (int) (whichScreen * getHeight() - getScrollY());
			mScroller.startScroll(0, getScrollY(), 0, delta, Math.abs(delta)
					* 2 * speedNum);

			if (mCurScreen == 0) {
				mScroller.startScroll(0, getHeight() * (getChildCount() - 2)
						- delta, 0, delta, Math.abs(delta) * 2 * speedNum);
				mCurScreen = getChildCount() - 2;
			}
			if (mCurScreen == getChildCount() - 1) {
				mScroller.startScroll(0, getHeight() - delta, 0, delta,
						Math.abs(delta) * 2 * speedNum);
				mCurScreen = 1;
			}
			invalidate(); // 重新布局
		}

		if (this.mPreScreen != this.mCurScreen && listener != null) {
			this.mPreScreen = this.mCurScreen;
			int item = this.mCurScreen - 1;
			this.listener.onCurrentView(item);
			location = item;
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {// 如果返回true，表示动画还没有结束
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			isStop = false;
		} else {
			isStop = true;
		}
	}

	/**
	 * 根据目前的位置滚动到下一个视图位置.
	 */
	public void snapToDestination() {
		int destScreen = 0;
		final int screenHeight = getHeight();
		if (screenHeight != 0)
			// 根据View的宽度以及滑动的值来判断是哪个View
			destScreen = (getScrollY() + screenHeight / 2) / screenHeight;

		snapToScreen(destScreen);
	}

	/*
	 * 当进行View滑动时，会导致当前的View无效，该函数的作用是对View进行重新绘制 调用drawScreen函数
	 */
	protected void dispatchDraw(Canvas canvas) {
		final long drawingTime = getDrawingTime();
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			drawScreen(canvas, i, drawingTime);
		}
	}

	/*
	 * 立体效果的实现函数 ,screen为哪一个子View
	 */
	private void drawScreen(Canvas canvas, int screen, long drawingTime) {
		// 得到当前子View的宽度
		final int height = getHeight();
		final int scrollHeight = screen * height;
		int scrollY = this.getScrollY();
		int faceIndex = screen;
		if (scrollHeight > scrollY + height || scrollHeight + height < scrollY) {
			return;
		}
		final View child = getChildAt(faceIndex);
		final float currentDegree = scrollY * (angle / getMeasuredHeight());
		final float faceDegree = currentDegree - faceIndex * angle;
		if (faceDegree > 90 || faceDegree < -90) {
			return;
		}
		float centerY = (scrollHeight < scrollY) ? scrollHeight + height
				: scrollHeight;
		final float centerX = getWidth() / 2;
		final Camera camera = mCamera;
		final Matrix matrix = mMatrix;
		canvas.save();//画布保存原状态
		camera.save();//camera保存原状态
		camera.rotateX(faceDegree);//设置旋转的角度
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
		canvas.concat(matrix);
		drawChild(canvas, child, drawingTime);
		canvas.restore();

	}

	private Timer timer;

	public void start() {
//		if (Constants.phone_density > 2.0f) {
//
//		} else if (Constants.phone_density == 2.0f) {
//
//		} else if (Constants.phone_density == 1.5f) {
//
//		} else {
//
//		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isStop) {
					if (!isFirst) {
						SystemClock.sleep(stopTime);
					}
					Message message = new Message();
					handler.sendMessage(message);
					isStop = false;
				}
			}
		}, 100, 100);
		
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
		}
		isStop = false;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			isFirst = false;
			// 启动
			rorateToNext();
		}
	};
	
	
	private View getInfoView(String title,int height){
        RelativeLayout infoView = new RelativeLayout(context);
        infoView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        TextView tvTitle = new TextView(context);
        tvTitle.setGravity(Gravity.CENTER_VERTICAL);
        RelativeLayout.LayoutParams lp =new RelativeLayout.LayoutParams(screenWidth*9/11,  height);
        tvTitle.setLayoutParams(lp);
        this.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth*9/11,  height+5));
        tvTitle.setText(title);
        tvTitle.setTextSize(textSize);
        tvTitle.setTextColor(textColor);
        tvTitle.setBackgroundResource(R.drawable.home_page_info_bg);
        infoView.addView(tvTitle);

        return infoView ;
    }

}

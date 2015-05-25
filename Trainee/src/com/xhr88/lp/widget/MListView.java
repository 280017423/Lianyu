package com.xhr88.lp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 
 * Description the class
 * 只不过重写了onMeasure，所以这个listview的全部内容会撑开因为放在scrollview里面的时候，listview跟它有冲突
 * 
 * @author zou.sq
 * @version 2013-8-22 下午2:45:38 zou.sq TODO
 */
public class MListView extends ListView {

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文对象
	 * @param attrs
	 *            属性
	 */
	public MListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				return true;
			default:
				break;
		}
		return super.dispatchTouchEvent(ev);
	}
}

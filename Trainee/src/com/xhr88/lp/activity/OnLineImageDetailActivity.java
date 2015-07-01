package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;

import com.xhr.framework.widget.photoview.HackyViewPager;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.OnLineImageDetailAdapter;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.widget.CircleFlowIndicator;

/**
 * 在线图片浏览界面
 * 
 * @author zou.sq
 */
public class OnLineImageDetailActivity extends TraineeBaseActivity {
	private ArrayList<String> mImgsList;
	private String[] mImgSmallList;
	private int mPosition;
	private ViewPager mViewPager;
	private CircleFlowIndicator mCircleFlowIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_img_detail);
		initVariables();
		initView();
	}

	private void initVariables() {
		Intent intent = getIntent();
		mImgsList = (ArrayList<String>) intent.getSerializableExtra(ConstantSet.ONLINE_IMAGE_DETAIL);
		mImgSmallList = (String[]) intent.getSerializableExtra(ConstantSet.ONLINE_IMAGE_SMALL);
		mPosition = intent.getIntExtra("position", 0);
	}

	private void initView() {
		mViewPager = (HackyViewPager) findViewById(R.id.vp_photo_view);
		mViewPager.setAdapter(new OnLineImageDetailAdapter(
				this, mImgsList, mImageLoader, mImgSmallList == null ? null : Arrays.asList(mImgSmallList)));
		mCircleFlowIndicator = (CircleFlowIndicator) findViewById(R.id.mIndicator);
		mViewPager.setCurrentItem(mPosition, false);
		if (mImgsList.size() <= 1) {
			mCircleFlowIndicator.setVisibility(View.GONE);
		} else {
			mCircleFlowIndicator.setVisibility(View.VISIBLE);
			mCircleFlowIndicator.setCount(mImgsList.size());
			mCircleFlowIndicator.setSeletion(mPosition % mImgsList.size());
		}
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mCircleFlowIndicator.setSeletion(position % mImgsList.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.activity_persistent, R.anim.activity_exit_scale);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

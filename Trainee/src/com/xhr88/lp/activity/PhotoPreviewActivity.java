package com.xhr88.lp.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.xhr.framework.widget.photoview.HackyViewPager;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.PhotoPreviewAdapter;
import com.xhr88.lp.model.viewmodel.ImageItemModel;
import com.xhr88.lp.widget.CircleFlowIndicator;

/**
 * 照片预览界面
 * 
 * @author zou.sq
 */
public class PhotoPreviewActivity extends TraineeBaseActivity {

	private ArrayList<ImageItemModel> mImageList;
	private ArrayList<String> mImgsList;
	private int mPosition;
	private ViewPager mViewPager;
	private CircleFlowIndicator mCircleFlowIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_preview);
		initVariables();
		initView();
	}

	private void initVariables() {
		Intent intent = getIntent();
		if (null != intent) {
			mImageList = (ArrayList<ImageItemModel>) getIntent().getSerializableExtra(ImageItemModel.class.getName());
			mPosition = getIntent().getIntExtra("position", 0);
			if (null == mImageList || mImageList.isEmpty()) {
				finish();
			}
		}
		mImgsList = getImageDetailUrls();
	}

	private ArrayList<String> getImageDetailUrls() {
		ArrayList<String> imgsList = new ArrayList<String>();
		for (int i = 0; i < mImageList.size(); i++) {
			imgsList.add(mImageList.get(i).imagePath);
		}
		return imgsList;
	}

	private void initView() {
		mViewPager = (HackyViewPager) findViewById(R.id.vp_photo_view);
		mViewPager.setAdapter(new PhotoPreviewAdapter(this, mImgsList));
		mViewPager.setCurrentItem(mPosition, false);
		mCircleFlowIndicator = (CircleFlowIndicator) findViewById(R.id.mIndicator);
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
		if (mImgsList.size() <= 1) {
			mCircleFlowIndicator.setVisibility(View.GONE);
		} else {
			mCircleFlowIndicator.setVisibility(View.VISIBLE);
			mCircleFlowIndicator.setCount(mImgsList.size());
			mCircleFlowIndicator.setSeletion(mPosition % mImgsList.size());
		}
	}
}

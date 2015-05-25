package com.xhr88.lp.activity;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.BadgeView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.PhotoGridAdapter;
import com.xhr88.lp.adapter.PhotoGridAdapter.ItemCallback;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.viewmodel.ImageItemModel;
import com.xhr88.lp.util.AlbumHelper;
import com.xhr88.lp.util.BitmapCache;

/**
 * 选择照片界面
 * 
 * @author zou.sq
 */
public class ChoosePhotoActivity extends TraineeBaseActivity implements OnClickListener {
	private static final String TAG = "ChoosePhotoActivity";

	private ArrayList<ImageItemModel> mHasChooseList;
	private ArrayList<ImageItemModel> mAllImageList;
	private GridView mGvPhoto;
	private PhotoGridAdapter mAdapter; // 自定义的适配器
	private AlbumHelper mHelper;
	private BadgeView mBadgeView;
	private Button mBtnChooseOk;
	private int mMaxImgNum;
	private ItemCallback mItemCallback = new ItemCallback() {
		@Override
		public void onChoose(int position, boolean choosed) {
			doChooseOne(mAllImageList.get(position), choosed);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_photo);
		initVariables();
		initView();
		setListener();
	}

	private void initVariables() {
		Intent intent = getIntent();
		mHasChooseList = (ArrayList<ImageItemModel>) intent.getSerializableExtra(ConstantSet.EXTRA_IMAGE_ITEM_MODEL);
		mMaxImgNum = getIntent().getIntExtra(ConstantSet.ADD_IMAGE_MAX_ITEM, 0);
		mHelper = AlbumHelper.getHelper();
		mHelper.init(this);
		mAllImageList = (ArrayList<ImageItemModel>) intent.getSerializableExtra(ConstantSet.EXTRA_IMAGE_LIST);
		mAllImageList = mAllImageList == null ? new ArrayList<ImageItemModel>() : mAllImageList;
		filtImage();
	}

	private void filtImage() {
		if (null == mAllImageList || mAllImageList.isEmpty() || null == mHasChooseList || mHasChooseList.isEmpty()) {
			return;
		}
		int totalSize = mAllImageList.size();
		int choosedSize = mHasChooseList.size();
		for (int i = 0; i < totalSize; i++) {
			ImageItemModel tempModel = mAllImageList.get(i);
			for (int j = 0; j < choosedSize; j++) {
				if (tempModel.imagePath.equals(mHasChooseList.get(j).imagePath)) {
					tempModel.isSelected = true;
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		doChooseConfirm(false);
	}

	private void doChooseOne(final ImageItemModel item, boolean isAdd) {
		if (isAdd && mHasChooseList.size() >= mMaxImgNum) {
			toast("最多" + mMaxImgNum + "张");
			return;
		}
		if (!BitmapCache.isBitmapExist(item.imagePath)) {
			toast("图片不存在，" + item.imagePath);
			EvtLog.e(TAG, "图片不存在，" + item.imagePath);
			return;
		}
		if (isAdd) {
			mHasChooseList.add(item);
			if (1 == mMaxImgNum) {
				doChooseConfirm(true);
			}
		} else {
			for (int i = 0; i < mHasChooseList.size(); i++) {
				ImageItemModel model = mHasChooseList.get(i);
				if (null != model && !StringUtil.isNullOrEmpty(model.imagePath)
						&& model.imagePath.equals(item.imagePath)) {
					mHasChooseList.remove(i);
				}
			}
		}
		refreashBadgeView();
	}

	private void doChooseConfirm(boolean isCompleted) {
		Intent intent = new Intent(this, ImageAlbumActivity.class);
		intent.putExtra(ConstantSet.EXTRA_IMAGE_CHOOSE_LIST, (Serializable) mHasChooseList);
		intent.putExtra(ConstantSet.EXTRA_IMAGE_CHOOSE_IS_COMPLETED, isCompleted);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void setListener() {
		mBtnChooseOk.setOnClickListener(this);
	}

	private void initView() {

		mBtnChooseOk = (Button) findViewById(R.id.btn_choosed_ok);
		mBadgeView = new BadgeView(this, mBtnChooseOk);
		mBadgeView.setGravity(Gravity.CENTER);
		mBadgeView.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
		mBadgeView.setBackgroundResource(R.drawable.trends_pick_count);
		refreashBadgeView();
		// 相册
		mGvPhoto = (GridView) findViewById(R.id.gridview);
		mAdapter = new PhotoGridAdapter(ChoosePhotoActivity.this, mAllImageList, mHasChooseList);
		mAdapter.setItemCallback(mItemCallback);
		mAdapter.setMaxCount(mMaxImgNum);
		mGvPhoto.setAdapter(mAdapter);

		mGvPhoto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mAdapter.notifyDataSetChanged();
			}

		});
	}

	private void refreashBadgeView() {
		if (null == mHasChooseList || mHasChooseList.isEmpty()) {
			mBadgeView.hide();
			mBtnChooseOk.setEnabled(false);
		} else {
			mBtnChooseOk.setEnabled(true);
			int size = mHasChooseList.size();
			mBadgeView.show();
			mBadgeView.setText(size + "");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_choosed_ok:
				doChooseConfirm(true);
				break;
			case R.id.title_with_back_title_btn_left:
				doChooseConfirm(false);
				break;
			case R.id.title_with_back_title_btn_right:
				setResult(RESULT_CANCELED);
				finish();
				break;
			default:
				break;
		}
	}

}

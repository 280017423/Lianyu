package com.xhr88.lp.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xhr88.lp.R;
import com.xhr88.lp.adapter.ImageAlbumAdapter;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.viewmodel.ImageAlbumModel;
import com.xhr88.lp.model.viewmodel.ImageItemModel;
import com.xhr88.lp.util.AlbumHelper;

/**
 * 相册界面
 * 
 * @version 1.0
 * @author zou.sq
 */
public class ImageAlbumActivity extends TraineeBaseActivity implements OnClickListener {

	public static final int REQUEST_CODE_SELECT_IMAGES = 1004; // 自定义相册，可多选
	private List<ImageAlbumModel> mDataList = new ArrayList<ImageAlbumModel>();
	private ListView mGridView;
	private ImageAlbumAdapter mAdapter;
	private AlbumHelper mAlbumHelper;
	private ArrayList<ImageItemModel> mImageItemModels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket);
		initVariables();
		initView();
	}

	private void initVariables() {
		Intent intent = getIntent();
		mImageItemModels = (ArrayList<ImageItemModel>) intent.getSerializableExtra(ConstantSet.EXTRA_IMAGE_ITEM_MODEL);
		if (null == mImageItemModels) {
			mImageItemModels = new ArrayList<ImageItemModel>();
		}
		mAlbumHelper = AlbumHelper.getHelper();
		mAlbumHelper.init(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		List<ImageAlbumModel> tmpList = mAlbumHelper.getImagesBucketList(true);
		if (tmpList != null) {
			mDataList.clear();
			mDataList.addAll(tmpList);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void initView() {
		mGridView = (ListView) findViewById(R.id.lv_photo_album);
		mAdapter = new ImageAlbumAdapter(this, mDataList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ImageAlbumActivity.this, ChoosePhotoActivity.class);
				intent.putExtra(ConstantSet.EXTRA_IMAGE_LIST, (Serializable) mDataList.get(position).imageList);
				intent.putExtra(ConstantSet.EXTRA_IMAGE_ITEM_MODEL, mImageItemModels); // 已选相片
				intent.putExtra(ConstantSet.ADD_IMAGE_MAX_ITEM,
						getIntent().getIntExtra(ConstantSet.ADD_IMAGE_MAX_ITEM, 0));
				startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK || null == data) {
			return;
		}
		if (REQUEST_CODE_SELECT_IMAGES == requestCode) {
			if (data.getBooleanExtra(ConstantSet.EXTRA_IMAGE_CHOOSE_IS_COMPLETED, true)) {
				setResult(Activity.RESULT_OK, data);
				finish();
			} else {
				Object obj = data.getSerializableExtra(ConstantSet.EXTRA_IMAGE_CHOOSE_LIST);
				if (obj == null) {
					return;
				}
				ArrayList<ImageItemModel> models = (ArrayList<ImageItemModel>) obj;
				if (null == models || models.isEmpty()) {
					return;
				}
				for (int i = 0; i < models.size(); i++) {
					ImageItemModel tempModel = models.get(i);
					boolean isContain = false;
					for (int j = 0; j < mImageItemModels.size(); j++) {
						if (tempModel.imagePath.equals(mImageItemModels.get(j).imagePath)) {
							isContain = true;
							break;
						}
					}
					if (!isContain) {
						mImageItemModels.add(tempModel);
					}
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				setResult(RESULT_CANCELED);
				finish();
				break;
			default:
				break;
		}

	}

}

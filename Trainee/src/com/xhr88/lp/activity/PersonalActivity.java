package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.UserPhotoAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.PictureModel;
import com.xhr88.lp.model.datamodel.UserInfoModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.widget.MultiGridView;

public class PersonalActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int REQUEST_CODE_EDIT_USER_INFO = 100;

	private ImageView mIvBackground;
	private TextView mTvUserName;
	private TextView mTvUserSex;
	private TextView mTvUserBirth;
	private LoadingUpView mLoadingUpView;
	private ArrayList<PictureModel> mUserPhoto;
	private MultiGridView mGvUserPhoto;
	private UserPhotoAdapter mUserPhotoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_personal_data);
		initVariables();
		initView();
		getUserbaseinfo();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this);
		mUserPhoto = new ArrayList<PictureModel>();
		mUserPhotoAdapter = new UserPhotoAdapter(this, mUserPhoto, mImageLoader);
	}

	private void initView() {
		mIvBackground = (ImageView) this.findViewById(R.id.iv_personal_bg);
		UIUtil.setViewHeight(mIvBackground, UIUtil.getScreenWidth(this));
		mGvUserPhoto = (MultiGridView) findViewById(R.id.gv_add_new_img);
		mGvUserPhoto.setAdapter(mUserPhotoAdapter);
		mTvUserName = (TextView) this.findViewById(R.id.tv_user_nickname);
		mTvUserSex = (TextView) this.findViewById(R.id.tv_user_sex);
		mTvUserBirth = (TextView) this.findViewById(R.id.tv_user_birth);
		mGvUserPhoto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArrayList<String> imageList = new ArrayList<String>();
				for (int i = 0; i < mUserPhoto.size(); i++) {
					imageList.add(mUserPhoto.get(i).getBigphoto());
				}
				Intent intent = new Intent(mContext, OnLineImageDetailActivity.class);
				intent.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
				intent.putExtra("position", position);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
			}
		});
	}

	private void notifyUserInfo(UserViewModel model) {
		UserInfoModel userInfoModel = model.UserInfo;
		if (null != userInfoModel) {
			Integer sex = userInfoModel.getSex();
			mTvUserName.setText(userInfoModel.getNickname());
			mTvUserBirth.setText(userInfoModel.getBirth());
			int imageBgRes = R.drawable.default_man_bg;
			if (1 == sex) {
				mTvUserSex.setText("男");
				mTvUserSex.setCompoundDrawables(getArrowDrawable(R.drawable.icon_man), null, null, null);
			} else {
				mTvUserSex.setText("女");
				imageBgRes = R.drawable.default_woman_bg;
				mTvUserSex.setCompoundDrawables(getArrowDrawable(R.drawable.icon_woman), null, null, null);
			}
			String background = userInfoModel.getBackground();
			List<PictureModel> pictureModels = userInfoModel.getList();
			mUserPhoto.clear();
			if (null != pictureModels) {
				mUserPhoto.addAll(pictureModels);
			}
			mUserPhotoAdapter.notifyDataSetChanged();
			mImageLoader.displayImage(background, mIvBackground, new DisplayImageOptions.Builder().cacheInMemory()
					.cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageBgRes).build());
		}
	}

	private void getUserbaseinfo() {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null == result) {
					return;
				}
				UserViewModel model = (UserViewModel) result.ResultObject;
				notifyUserInfo(model);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
				UserViewModel userViewModel = UserDao.getLocalUserInfo();
				if (null == userViewModel) {
					return;
				}
				notifyUserInfo(userViewModel);
			}

			@Override
			public ActionResult onAsyncRun() {
				String tempuid = String.valueOf(UserDao.getLocalUserInfo().UserInfo.getUid());
				return UserReq.getUserBaseInfo(tempuid);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_right:
				Intent intent = new Intent(PersonalActivity.this, EditUserInfoActivity.class);
				intent.putExtra(PictureModel.class.getName(), mUserPhoto);
				startActivityForResult(intent, REQUEST_CODE_EDIT_USER_INFO);
				break;
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.iv_personal_bg:
				UserViewModel userViewModel = UserDao.getLocalUserInfo();
				if (null != userViewModel && null != userViewModel.UserInfo
						&& !StringUtil.isNullOrEmpty(userViewModel.UserInfo.getBackground())) {
					ArrayList<String> imageList = new ArrayList<String>();
					imageList.add(userViewModel.UserInfo.getBackground());
					Intent intent1 = new Intent(mContext, OnLineImageDetailActivity.class);
					intent1.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
					startActivity(intent1);
					overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
				}
				break;
		}
	}

	private Drawable getArrowDrawable(int id) {
		Drawable drawable = getResources().getDrawable(id);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK != resultCode) {
			return;
		}
		if (REQUEST_CODE_EDIT_USER_INFO == requestCode) {
			getUserbaseinfo();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
}

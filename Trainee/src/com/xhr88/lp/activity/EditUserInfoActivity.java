package com.xhr88.lp.activity;

import io.rong.imkit.RCloudContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.FileUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.AddImageAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.listener.ImageGridItemListener;
import com.xhr88.lp.model.datamodel.FileuploadModel;
import com.xhr88.lp.model.datamodel.PictureModel;
import com.xhr88.lp.model.datamodel.UserInfoModel;
import com.xhr88.lp.model.viewmodel.ImageItemModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.util.ImageUtil;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.widget.MultiGridView;

public class EditUserInfoActivity extends TraineeBaseActivity implements OnClickListener {

	private static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/DCIM/";
	private static final int REQUEST_CODE_CAPTURE_BG_IMAGE = 1003;
	private static final int REQUEST_CODE_SELECT_BG_IMAGES = 1004;
	private static final int REQUEST_CODE_CAPTURE_PHOTO_IMAGE = 1005;
	private static final int REQUEST_CODE_SELECT_PHOTO_IMAGES = 1006;
	private static final int REQUEST_CODE_EDIT_NICKNAME = 1007;
	private static final int REQUEST_CODE_EDIT_BIRTH = 1008;
	private static final int REQUEST_CODE_CUT = 1009;

	private static final int DIALOG_HAS_CHANGED = 9;
	private static final int UPLOAD_BG_FAIL = 10;
	private static final int UPLOAD_BG_SUCCESS = 11;
	private static final int UPLOAD_PHOTO_FAIL = 12;
	private static final int UPLOAD_PHOTO_SUCCESS = 13;

	private ImageView mIvBackground;
	private TextView mTvUserName;
	private TextView mTvUserSex;
	private TextView mTvUserBirth;
	private MultiGridView mGvUserPhoto;
	private AddImageAdapter mUserPhotoAdapter;
	private ImageItemModel mBgModel;

	private LoadingUpView mLoadingUpView;
	public static final int NICKINTENT = 100;
	public static final int DATEINTENT = 99;
	private ArrayList<ImageItemModel> mUserPhoto;
	private String mCurrentPath;
	private String mToken;
	private List<PictureModel> mPictureModels;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case UPLOAD_BG_FAIL:
					dismissLoadingUpView(mLoadingUpView);
					toast("上传背景失败，请重试");
					break;
				case UPLOAD_BG_SUCCESS:
					dismissLoadingUpView(mLoadingUpView);
					userEdit(DateUtil.getQiniuKey("bg", mBgModel.uuid));
					break;
				case UPLOAD_PHOTO_FAIL:
					dismissLoadingUpView(mLoadingUpView);
					toast("上传照片失败，请重试");
					if (null != mUserPhoto && !mUserPhoto.isEmpty()) {
						mUserPhoto.remove(mUserPhoto.size() - 1);
						mUserPhotoAdapter.notifyDataSetChanged();
					}
					break;
				case UPLOAD_PHOTO_SUCCESS:
					dismissLoadingUpView(mLoadingUpView);
					if (null != mUserPhoto && !mUserPhoto.isEmpty()) {
						photoEdit("1", "", "1",
								DateUtil.getQiniuKey("album", mUserPhoto.get(mUserPhoto.size() - 1).uuid), 0);
					}
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_edit_user_info);
		initVariables();
		initView();
		notifyUserInfo();
		getToken(true, null, null, false);
	}

	private void initVariables() {
		mPictureModels = (List<PictureModel>) getIntent().getSerializableExtra(PictureModel.class.getName());
		mLoadingUpView = new LoadingUpView(this, true);
		mUserPhoto = new ArrayList<ImageItemModel>();
		mBgModel = new ImageItemModel();
		mUserPhotoAdapter = new AddImageAdapter(this, mUserPhoto, new ImageGridItemListener() {

			@Override
			public void onItemDelClick(int position) {
			}

			@Override
			public void onAddPhotoClick() {
				showPickPhotoPop("", -1);
			}
		}, mImageLoader);
		mUserPhotoAdapter.isHiddenDel(true);
		mUserPhotoAdapter.setMaxCount(8);
	}

	protected void showPickPhotoPop(final String imageId, final int position) {
		final View popView = View.inflate(this, R.layout.view_actionsheet, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);

		final Button btnCancel = (Button) popView.findViewById(R.id.btn_cancel);
		final Button btnTakePhoto = (Button) popView.findViewById(R.id.btn_action_2);
		btnTakePhoto.setText("拍照");
		final Button btnPickPhoto = (Button) popView.findViewById(R.id.btn_action_1);
		btnPickPhoto.setText("从手机相册选择");
		if (!StringUtil.isNullOrEmpty(imageId)) {
			final Button btnDelete = (Button) popView.findViewById(R.id.btn_del_photo);
			final Button btnSetHead = (Button) popView.findViewById(R.id.btn_set_head);
			UIUtil.setViewVisible(btnDelete);
			UIUtil.setViewVisible(btnSetHead);
			UIUtil.setViewGone(btnPickPhoto);
			UIUtil.setViewGone(btnTakePhoto);
			btnSetHead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popManager.dismiss(); // 缺陷 #857
					setHead(imageId);
				}
			});
			btnDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popManager.dismiss();
					photoEdit("2", imageId, "1", "", position);
				}
			});
		} else {
			btnTakePhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popManager.dismiss();
					sendCapture(REQUEST_CODE_CAPTURE_PHOTO_IMAGE);
				}
			});
			btnPickPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popManager.dismiss();
					Intent intent = new Intent(EditUserInfoActivity.this, ImageAlbumActivity.class);
					intent.putExtra(ConstantSet.EXTRA_IMAGE_ITEM_MODEL, new ArrayList<ImageItemModel>());
					intent.putExtra(ConstantSet.ADD_IMAGE_MAX_ITEM, 1); // 只单选一张上传，上传成功之后，把地址覆盖到本地，进入相册就不会显示选中状态
					startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO_IMAGES);
				}
			});
		}

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
			}
		});
		popManager.show(Gravity.BOTTOM);

	}

	private void initView() {
		mIvBackground = (ImageView) this.findViewById(R.id.iv_personal_bg);
		UIUtil.setViewHeight(mIvBackground, UIUtil.getScreenWidth(this));
		mTvUserName = (TextView) this.findViewById(R.id.tv_user_nickname);
		mTvUserSex = (TextView) this.findViewById(R.id.tv_user_sex);
		mTvUserBirth = (TextView) this.findViewById(R.id.tv_user_birth);
		mGvUserPhoto = (MultiGridView) findViewById(R.id.gv_add_new_img);
		mGvUserPhoto.setAdapter(mUserPhotoAdapter);
		mGvUserPhoto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (null == mPictureModels) {
					return;
				}
				ImageItemModel model = mUserPhoto.get(position);
				if (null != model && !StringUtil.isNullOrEmpty(model.imageId)) {
					showPickPhotoPop(model.imageId, position);
				}
			}
		});
	}

	private void notifyUserInfo() {
		UserViewModel userViewModel = UserDao.getLocalUserInfo();
		if (null == userViewModel) {
			return;
		}
		UserInfoModel userInfoModel = userViewModel.UserInfo;
		if (null != userInfoModel) {
			Integer sex = userInfoModel.getSex();
			mTvUserName.setText(userInfoModel.getNickname());
			mTvUserBirth.setText(userInfoModel.getBirth());
			int imageBgRes = R.drawable.default_man_bg;
			if (1 == sex) {
				mTvUserSex.setText("男");
				mTvUserSex.setCompoundDrawables(getArrowDrawable(R.drawable.icon_man), null, null, null);
			} else {
				imageBgRes = R.drawable.default_woman_bg;
				mTvUserSex.setText("女");
				mTvUserSex.setCompoundDrawables(getArrowDrawable(R.drawable.icon_woman), null, null, null);
			}
			String background = userInfoModel.getBackground();
			if (null != mPictureModels) {
				for (int i = 0; i < mPictureModels.size(); i++) {
					PictureModel pictureModel = mPictureModels.get(i);
					ImageItemModel imageItemModel = new ImageItemModel();
					imageItemModel.imageId = pictureModel.getPhotoid();
					imageItemModel.thumbnailPath = pictureModel.getPhoto();
					mUserPhoto.add(imageItemModel);
				}
				mUserPhotoAdapter.notifyDataSetChanged();
			}
			mImageLoader.displayImage(background, mIvBackground, new DisplayImageOptions.Builder().cacheInMemory()
					.cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageBgRes).build());
		}
	}

	private void sendCapture(int requestCode) {
		if (ImageUtil.hasSdcard()) {
			FileUtil.prepareFile(PHOTO_PATH); // 准备文件夹
			String fileName = ImageUtil.getPhotoFileName();
			try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统的拍照功能
				// 判断存储卡是否可以用，可用进行存储
				File tempFile = new File(PHOTO_PATH, fileName);
				mCurrentPath = tempFile.getAbsolutePath();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile)); // 指定调用相机拍照后照片的储存路径
				startActivityForResult(intent, requestCode);
			} catch (Exception e) {
				toast(getString(R.string.not_support_take_photo));
			}
		} else {
			toast(getString(R.string.not_have_sd));
		}
	}

	private void showPickBgPop() {
		final View popView = View.inflate(this, R.layout.view_actionsheet, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final Button btnTakePhoto = (Button) popView.findViewById(R.id.btn_action_2);
		btnTakePhoto.setText("拍照");
		final Button btnPickPhoto = (Button) popView.findViewById(R.id.btn_action_1);
		btnPickPhoto.setText("从手机相册选择");
		final Button btnCancel = (Button) popView.findViewById(R.id.btn_cancel);
		btnTakePhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				sendCapture(REQUEST_CODE_CAPTURE_BG_IMAGE);
			}
		});
		btnPickPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				Intent intent = new Intent(EditUserInfoActivity.this, ImageAlbumActivity.class);
				intent.putExtra(ConstantSet.EXTRA_IMAGE_ITEM_MODEL, new ArrayList<ImageItemModel>());
				intent.putExtra(ConstantSet.ADD_IMAGE_MAX_ITEM, 1);
				startActivityForResult(intent, REQUEST_CODE_SELECT_BG_IMAGES);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
			}
		});
		popManager.show(Gravity.BOTTOM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_SELECT_BG_IMAGES) {
			List<ImageItemModel> models = null;
			if (null == data) {
				return;
			}
			Object obj = data.getSerializableExtra(ConstantSet.EXTRA_IMAGE_CHOOSE_LIST);
			if (obj != null) {
				models = (List<ImageItemModel>) obj;
			}
			if (models != null && !models.isEmpty()) {
				startPhotoZoom(Uri.fromFile(new File(models.get(0).imagePath)), true);
			}
		} else if (requestCode == REQUEST_CODE_CAPTURE_BG_IMAGE) {
			if (!StringUtil.isNullOrEmpty(mCurrentPath)) {
				// 拍照之后扫描文件夹
				ImageUtil.scanMedia(this, mCurrentPath);
				startPhotoZoom(Uri.fromFile(new File(mCurrentPath)), true);
			}
		} else if (requestCode == REQUEST_CODE_SELECT_PHOTO_IMAGES) {
			List<ImageItemModel> models = null;
			if (null == data) {
				return;
			}
			Object obj = data.getSerializableExtra(ConstantSet.EXTRA_IMAGE_CHOOSE_LIST);
			if (obj != null) {
				models = (List<ImageItemModel>) obj;
			}
			if (models != null && !models.isEmpty()) {
				startPhotoZoom(Uri.fromFile(new File(models.get(0).imagePath)), false);

			}
		} else if (requestCode == REQUEST_CODE_CAPTURE_PHOTO_IMAGE) {
			if (!StringUtil.isNullOrEmpty(mCurrentPath)) {
				// 拍照之后扫描文件夹
				ImageUtil.scanMedia(this, mCurrentPath);
				startPhotoZoom(Uri.fromFile(new File(mCurrentPath)), false);
			}
		} else if (requestCode == REQUEST_CODE_CUT) {
			Uri uri = data.getData();
			boolean isBackground = data.getBooleanExtra("isBackground", false);
			if (isBackground) {
				mBgModel.imagePath = uri.getPath();
				mBgModel.uuid = UUID.randomUUID().toString();
				displayBg();
			} else {
				ImageItemModel tempModel = new ImageItemModel();
				tempModel.imagePath = uri.getPath();
				tempModel.uuid = UUID.randomUUID().toString();
				mUserPhoto.add(tempModel);
				mUserPhotoAdapter.notifyDataSetChanged();
				upload2Qiniu(tempModel, "album", false);
			}
		} else if (requestCode == REQUEST_CODE_EDIT_NICKNAME) {
			String nickName = data.getStringExtra("nickname");
			mTvUserName.setText(nickName);
		} else if (requestCode == REQUEST_CODE_EDIT_BIRTH) {
			String date = data.getStringExtra("date");
			mTvUserBirth.setText(date);
		}
	}

	/**
	 * 跳转到截屏的界面
	 * 
	 * @param uri
	 *            需要裁剪图片的uri
	 */
	public void startPhotoZoom(Uri uri, boolean isBackground) {
		Intent intent = new Intent(this, ClipPictureActivity.class);
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("isBackground", isBackground);
		startActivityForResult(intent, REQUEST_CODE_CUT);
	}

	private void displayBg() {
		if (null != mBgModel && !StringUtil.isNullOrEmpty(mBgModel.imagePath)) {
			Bitmap bmp = ImageUtil.getImageThumbnail(mBgModel.imagePath, ConstantSet.BG_WIDTH, ConstantSet.BG_HEIFHT);
			if (bmp != null) {
				mIvBackground.setImageBitmap(bmp);
			}
		}
	}

	private void upload2Qiniu(final ImageItemModel model, final String type, final boolean isBgUpload) {
		showLoadingUpView(mLoadingUpView, "正在保存");
		new Thread(new Runnable() {

			@Override
			public void run() {
				UploadManager uploadManager = new UploadManager();
				try {
					String compressImgPath = ImageUtil.compressImage(model.imagePath, new File(PHOTO_PATH), model.uuid);
					final File compressFile = new File(compressImgPath); // 压缩过后的图片
					uploadManager.put(compressFile, DateUtil.getQiniuKey(type, model.uuid), mToken,
							new UpCompletionHandler() {

								@Override
								public void complete(String key, ResponseInfo info, JSONObject response) {
									if (info.isOK()) {
										if (isBgUpload) {
											mHandler.sendEmptyMessage(UPLOAD_BG_SUCCESS);
										} else {
											mHandler.sendEmptyMessage(UPLOAD_PHOTO_SUCCESS);
										}
									} else {
										if (isBgUpload) {
											mHandler.sendEmptyMessage(UPLOAD_BG_FAIL);
										} else {
											mHandler.sendEmptyMessage(UPLOAD_PHOTO_FAIL);
										}
									}
									compressFile.delete();
									EvtLog.d("aaa", "key:" + key + "===" + info.isOK());
									EvtLog.d("aaa", "response:" + response);
									EvtLog.d("aaa", "info:" + info);
								}
							}, null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void getToken(final boolean isBackground, final ImageItemModel imageItemModel, final String type,
			final boolean isBgUpload) {
		if (!isBackground) {
			showLoadingUpView(mLoadingUpView, "正在保存");
		}
		new ActionProcessor().startAction(EditUserInfoActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null != result) {
					FileuploadModel model = (FileuploadModel) result.ResultObject;
					mToken = model.getFiletoken();
					if (!isBackground && !StringUtil.isNullOrEmpty(mToken)) {
						upload2Qiniu(imageItemModel, type, isBgUpload);
					}
				}
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (!isBackground) {
					showErrorMsg(result);
				}
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.getToken();
			}
		});
	}

	// 得到系统当前时间并转化为字符串
	@SuppressLint("SimpleDateFormat")
	public static String getCharacterAndNumber() {
		String rel = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		rel = formatter.format(curDate);
		return rel;
	}

	private void setHead(final String photoid) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				// 清除头像缓存
				RCloudContext.getInstance().getUserInfoCache().remove(UserMgr.getUid());
				dismissLoadingUpView(mLoadingUpView);
				toast("头像设置成功");
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.setUserHead(photoid);
			}
		});
	}

	private void userEdit(final String background) {
		showLoadingUpView(mLoadingUpView, "正在保存");
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				toast("个人资料修改成功");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				String nickName = mTvUserName.getText().toString().trim();
				String birth = mTvUserBirth.getText().toString().trim();
				return UserReq.userEdit(nickName, "", birth, background);
			}
		});
	}

	private void photoEdit(final String opt, final String photoid, final String phototype, final String photo,
			final int position) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if ("1".equals(opt)) {
					if (null != result) {
						PictureModel model = (PictureModel) result.ResultObject;
						mUserPhoto.get(mUserPhoto.size() - 1).thumbnailPath = model.getPhoto();
						mUserPhoto.get(mUserPhoto.size() - 1).imageId = model.getPhotoid();
						mUserPhotoAdapter.notifyDataSetChanged();
					}
					toast("上传照片成功");
				} else if ("2".equals(opt)) {
					mUserPhoto.remove(position);
					mUserPhotoAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onError(ActionResult result) {
				// 上传到七牛成功，但是到业务服务器失败，同样删除本地显示的图片
				if ("1".equals(opt)) {
					if (null != mUserPhoto && !mUserPhoto.isEmpty()) {
						mUserPhoto.remove(mUserPhoto.size() - 1);
						mUserPhotoAdapter.notifyDataSetChanged();
					}
				}
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.photoEdit(opt, photoid, phototype, photo);
			}
		});
	}

	protected void sendMessage(int what, Object obj) {
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_right:
				save();
				break;
			case R.id.title_with_back_title_btn_left:
				if (isChange()) {
					showDialog(DIALOG_HAS_CHANGED);
					return;
				} else {
					setResult(RESULT_OK);
					finish();
				}
				break;
			case R.id.personal_change_btn:
				showPickBgPop();
				break;
			case R.id.rl_user_birth_item:
				Intent intent2 = new Intent(EditUserInfoActivity.this, EditBirthActivity.class);
				intent2.putExtra("date", mTvUserBirth.getText().toString());
				startActivityForResult(intent2, REQUEST_CODE_EDIT_BIRTH);
				break;
			case R.id.rl_user_nickname_item:
				Intent intent1 = new Intent(EditUserInfoActivity.this, EditNickNameActivity.class);
				intent1.putExtra("username", mTvUserName.getText().toString());
				startActivityForResult(intent1, REQUEST_CODE_EDIT_NICKNAME);
				break;
		}
	}

	private void save() {
		String nickName = mTvUserName.getText().toString().trim();
		String birth = mTvUserBirth.getText().toString().trim();
		if (StringUtil.isNullOrEmpty(nickName)) {
			toast("昵称不能为空");
			return;
		}
		if (StringUtil.isNullOrEmpty(birth)) {
			toast("生日不能为空");
			return;
		}
		if (null == mBgModel || StringUtil.isNullOrEmpty(mBgModel.imagePath)) {
			userEdit("");
		} else {
			if (StringUtil.isNullOrEmpty(mToken)) {
				getToken(false, mBgModel, "bg", true); // 如果Token为空，获取Token
			} else {
				upload2Qiniu(mBgModel, "bg", true); // 如果Token不为空,上传文件到七牛
			}
		}
	}

	private Drawable getArrowDrawable(int id) {
		Drawable drawable = getResources().getDrawable(id);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}

	@Override
	public void onBackPressed() {
		if (isChange()) {
			showDialog(DIALOG_HAS_CHANGED);
			return;
		} else {
			setResult(RESULT_OK);
			finish();
		}
		super.onBackPressed();
	}

	private boolean isChange() {
		if (!StringUtil.isNullOrEmpty(mBgModel.imageId) || !StringUtil.isNullOrEmpty(mBgModel.imagePath)) {
			return true;
		}
		UserViewModel userViewModel = UserDao.getLocalUserInfo();
		if (null == userViewModel) {
			return true;
		}
		UserInfoModel userInfoModel = userViewModel.UserInfo;
		if (null != userInfoModel) {
			if (!userInfoModel.getNickname().equals(mTvUserName.getText().toString())
					|| !userInfoModel.getBirth().equals(mTvUserBirth.getText().toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_HAS_CHANGED:
				return createDialogBuilder(this, getString(R.string.button_text_tips), "要保存修改后的个人资料吗？", "放弃", "保存")
						.create(id);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_HAS_CHANGED:
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_HAS_CHANGED:
				save();
				break;
			default:
				break;
		}
	}
}

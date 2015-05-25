package com.xhr88.lp.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.FileUtil;
import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.NetUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.AddImageAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.SystemReq;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.listener.ImageGridItemListener;
import com.xhr88.lp.model.datamodel.FileuploadModel;
import com.xhr88.lp.model.viewmodel.ImageItemModel;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.util.ImageUtil;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.widget.CustomDialog.Builder;
import com.xhr88.lp.widget.MultiGridView;

/**
 * 投诉界面
 * 
 * @author zou.sq
 */
public class ComplainActivity extends TraineeBaseActivity implements OnClickListener {

	private static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/DCIM/";
	private static final int DIALOG_COMPLAIN = 1;
	private static final int DIALOG_COMPLAIN_SUCCESS = 2;
	// 自定义相册，可多选
	private static final int REQUEST_CODE_SELECT_IMAGES = 1004;
	// 从照相机选择
	private static final int REQUEST_CODE_CAPTURE_IMAGE = 1003;
	private static final int UPLOAD_PHOTO_FAIL = 10;
	private static final int UPLOAD_PHOTO_SUCCESS = 11;
	private EditText mEdtFeedbackContent;
	private TextView mTvNickName;
	private boolean mIsUploading;
	private LoadingUpView mLoadingUpView;
	private MultiGridView mGvAddImg;
	private AddImageAdapter mAddImageAdapter;
	// 上传图片的路径集合
	private ArrayList<ImageItemModel> mImageList;
	private String mCurrentPath;
	private String mToken;
	private int mSize;
	private String mUserId;
	private String mNickName;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPLOAD_PHOTO_FAIL:
					dismissLoadingUpView(mLoadingUpView);
					toast("上传文件失败，请重试");
					break;
				case UPLOAD_PHOTO_SUCCESS:
					mSize--;
					if (mSize <= 0) {
						dismissLoadingUpView(mLoadingUpView);
						submit(getmImageUrl());
					}
					break;
				default:
					break;
			}
		}
	};

	public String getmImageUrl() {
		String imageUrl = "";
		if (null == mImageList && mImageList.isEmpty()) {
			return imageUrl;
		}
		for (int i = 0; i < mImageList.size(); i++) {
			imageUrl += DateUtil.getQiniuKey("images", mImageList.get(i).uuid) + ",";
		}
		if (!StringUtil.isNullOrEmpty(imageUrl)) {
			imageUrl = imageUrl.substring(0, imageUrl.length() - 1);
		}
		return imageUrl;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complain);
		initVariable();
		initViews();
		getToken(true);
	}

	private void initVariable() {
		mUserId = getIntent().getStringExtra("targetId");
		mNickName = getIntent().getStringExtra("title");
		if (StringUtil.isNullOrEmpty(mUserId)) {
			finish();
		}
		mLoadingUpView = new LoadingUpView(this);
		mImageList = new ArrayList<ImageItemModel>();
		mAddImageAdapter = new AddImageAdapter(this, mImageList, new ImageGridItemListener() {

			@Override
			public void onItemDelClick(int position) {
				if (position < mImageList.size()) {
					mImageList.remove(position);
					mAddImageAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onAddPhotoClick() {
				showPickPop();
			}
		});
	}

	private void initViews() {
		mEdtFeedbackContent = (EditText) findViewById(R.id.edt_feedback_content);
		mTvNickName = (TextView) findViewById(R.id.tv_nickname);
		mTvNickName.setText(mNickName);
		mGvAddImg = (MultiGridView) findViewById(R.id.gv_add_new_img);
		mGvAddImg.setAdapter(mAddImageAdapter);
		mGvAddImg.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(ComplainActivity.this, PhotoPreviewActivity.class);
				intent.putExtra(ImageItemModel.class.getName(), mImageList);
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.title_with_back_title_btn_right:
				if (!isPrepared()) {
					return;
				}
				showDialog(DIALOG_COMPLAIN);
				break;

			default:
				break;
		}
	}

	private void showPickPop() {
		ImeUtil.hideSoftInput(this);
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
				sendCapture();
			}
		});
		btnPickPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				Intent intent = new Intent(ComplainActivity.this, ImageAlbumActivity.class);
				intent.putExtra(ConstantSet.EXTRA_IMAGE_ITEM_MODEL, mImageList);
				intent.putExtra(ConstantSet.ADD_IMAGE_MAX_ITEM, 4);
				startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES);
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

	private void sendCapture() {
		if (ImageUtil.hasSdcard()) {
			FileUtil.prepareFile(PHOTO_PATH); // 准备文件夹
			String fileName = ImageUtil.getPhotoFileName();
			try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统的拍照功能
				// 判断存储卡是否可以用，可用进行存储
				File tempFile = new File(PHOTO_PATH, fileName);
				mCurrentPath = tempFile.getAbsolutePath();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile)); // 指定调用相机拍照后照片的储存路径
				startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
			} catch (Exception e) {
				toast(getString(R.string.not_support_take_photo));
			}
		} else {
			toast(getString(R.string.not_have_sd));
		}
	}

	private void submit(final String imageUrl) {
		mIsUploading = true;
		showLoadingUpView(mLoadingUpView, "正在提交");
		new ActionProcessor().startAction(ComplainActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				mIsUploading = false;
				showDialog(DIALOG_COMPLAIN_SUCCESS);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				mIsUploading = false;
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				final String content = mEdtFeedbackContent.getText().toString().trim();
				return SystemReq.feedBack("1", mUserId, content, imageUrl);
			}
		});
	}

	private boolean isPrepared() {
		if (mIsUploading) {
			return false;
		}
		ImeUtil.hideSoftInput(this);
		final String content = mEdtFeedbackContent.getText().toString().trim();
		if (StringUtil.isNullOrEmpty(content) || content.length() < 15) {
			toast("至少15个字以上");
			return false;
		}
		if (!NetUtil.isNetworkAvailable()) {
			toast(getString(R.string.network_is_not_available));
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_SELECT_IMAGES) {
			List<ImageItemModel> models = null;
			if (null == data) {
				return;
			}
			Object obj = data.getSerializableExtra(ConstantSet.EXTRA_IMAGE_CHOOSE_LIST);
			if (obj != null) {
				models = (List<ImageItemModel>) obj;
			}
			if (models != null) {
				mImageList.clear();
				mImageList.addAll(models);
				mAddImageAdapter.notifyDataSetChanged();
			}
		}
		if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
			if (!StringUtil.isNullOrEmpty(mCurrentPath)) {
				// 拍照之后扫描文件夹
				ImageUtil.scanMedia(this, mCurrentPath);
				ImageItemModel tempModel = new ImageItemModel();
				tempModel.imagePath = mCurrentPath;
				tempModel.uuid = UUID.randomUUID().toString();
				mImageList.add(tempModel);
				mAddImageAdapter.notifyDataSetChanged();
			}
		}
	}

	private void getToken(final boolean isBackground) {
		if (!isBackground) {
			showLoadingUpView(mLoadingUpView);
		}
		new ActionProcessor().startAction(ComplainActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null != result) {
					FileuploadModel model = (FileuploadModel) result.ResultObject;
					mToken = model.getFiletoken();
					if (!isBackground && !StringUtil.isNullOrEmpty(mToken)) {
						upload2Qiniu();
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

	private void upload2Qiniu() {
		showLoadingUpView(mLoadingUpView, "正在提交");
		new Thread(new Runnable() {

			@Override
			public void run() {
				UploadManager uploadManager = new UploadManager();
				mSize = mImageList.size();
				for (int i = 0; i < mImageList.size(); i++) {
					ImageItemModel model = mImageList.get(i);
					try {
						String compressImgPath = ImageUtil.compressImage(model.imagePath, new File(PHOTO_PATH),
								model.uuid);
						uploadManager.put(new File(compressImgPath), DateUtil.getQiniuKey("images", model.uuid),
								mToken, new UpCompletionHandler() {

									@Override
									public void complete(String key, ResponseInfo info, JSONObject response) {
										if (info.isOK()) {
											mHandler.sendEmptyMessage(UPLOAD_PHOTO_SUCCESS);
										} else {
											mHandler.sendEmptyMessage(UPLOAD_PHOTO_FAIL);
										}
										EvtLog.d("aaa", "key:" + key + "===" + info.isOK());
										EvtLog.d("aaa", "response:" + response);
										EvtLog.d("aaa", "info:" + info);
									}
								}, null);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (DIALOG_COMPLAIN == id) {
			return createDialogBuilder(this, "", "您确定要对本次服务进行投诉吗？", getString(R.string.button_text_no), "确定")
					.create(id);
		}
		if (DIALOG_COMPLAIN_SUCCESS == id) {
			Builder builder = createDialogBuilder(this, "提交成功", "", "", "确定");
			builder.setmDialogView(View.inflate(ComplainActivity.this, R.layout.view_complain_success, null));
			return builder.create(id);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_COMPLAIN:
				if (mImageList.isEmpty()) {
					submit("");
				} else {
					if (StringUtil.isNullOrEmpty(mToken)) {
						getToken(true); // 如果Token为空，获取Token
					} else {
						upload2Qiniu(); // 如果Token不为空,上传文件到七牛
					}
				}
				break;
			case DIALOG_COMPLAIN_SUCCESS:
				dialog.dismiss();
				finish();
				break;
			default:
				break;
		}
	}
}

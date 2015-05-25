package com.xhr88.lp.activity;

import io.rong.imkit.RongIM;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr.framework.widget.pulltozoomview.PullToZoomScrollViewEx;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.EditUserCateAdapter;
import com.xhr88.lp.adapter.UserPhotoAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.OtherUserModel;
import com.xhr88.lp.model.datamodel.PictureModel;
import com.xhr88.lp.model.datamodel.UserCategoryModel;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.widget.MGridView;
import com.xhr88.lp.widget.MultiGridView;

public class OtherHomeActivity extends TraineeBaseActivity implements OnClickListener {

	private final int GET_HOME_INFO_SUCCESS = 1;
	private final int GET_HOME_INFO_FAIL = 2;
	private final int DIALOG_JUBAO = 3;
	private String mTouid;
	private RoundCornerImageView mIvTrendsImg;
	private ImageView mIvBackground;
	private ImageView mIVUserSex;
	private TextView mTvUserAge;
	private TextView mTvUserConstellatory;
	private ImageView mIvUserIsVideo;
	private TextView mTvUserUid;
	private TextView mTvUserTrendsCount;
	private TextView mTvTrendsTime;
	private TextView mTvXiaoDianBrief;
	private TextView mTvTitle;
	private View mViewTrends;
	private View mViewShop;
	private ImageView mIvLevel;
	private MGridView mGvUserCate;
	private TextView mTvTrendsContent;
	private LoadingUpView mLoadingUpView;
	private ArrayList<PictureModel> mUserPhoto;
	private MultiGridView mGvUserPhoto;
	private UserPhotoAdapter mUserPhotoAdapter;
	private OtherUserModel mCurrentModel;
	private int mJubaoContent;
	private List<UserCategoryModel> mCategoryModels;
	private EditUserCateAdapter mAdapter;
	private View mViewChat;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_HOME_INFO_SUCCESS:
					OtherUserModel model = (OtherUserModel) result.ResultObject;
					notifyUserInfo(model);
					break;
				case GET_HOME_INFO_FAIL:
					showErrorMsg(result);
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_home_page);
		initVariables();
		initView();
		getotherhomeinfo();
	}

	private void initVariables() {
		mTouid = getIntent().getStringExtra("touid");
		if (StringUtil.isNullOrEmpty(mTouid)) {
			finish();
		}
		mCategoryModels = new ArrayList<UserCategoryModel>();
		mAdapter = new EditUserCateAdapter(this, mCategoryModels);
		mLoadingUpView = new LoadingUpView(this);
		mUserPhoto = new ArrayList<PictureModel>();
		mUserPhotoAdapter = new UserPhotoAdapter(this, mUserPhoto, mImageLoader);
	}

	private void initView() {
		View headView = View.inflate(this, R.layout.view_profile_head, null);
		mViewChat = findViewById(R.id.other_page_btn_date);
		View contentView = View.inflate(this, R.layout.view_profile_content, null);
		View zoomView = View.inflate(this, R.layout.view_profile_zoom, null);
		mIvBackground = (ImageView) zoomView.findViewById(R.id.iv_personal_bg);

		mIvLevel = (ImageView) contentView.findViewById(R.id.iv_level);
		mGvUserCate = (MGridView) contentView.findViewById(R.id.gv_user_cate);
		mGvUserCate.setAdapter(mAdapter);
		mGvUserPhoto = (MultiGridView) headView.findViewById(R.id.gv_add_new_img);
		mGvUserPhoto.setAdapter(mUserPhotoAdapter);
		mTvUserAge = (TextView) contentView.findViewById(R.id.tv_age);
		mTvTitle = (TextView) findViewById(R.id.tv_title_name);
		mIVUserSex = (ImageView) contentView.findViewById(R.id.iv_sex);
		mTvUserConstellatory = (TextView) contentView.findViewById(R.id.tv_constellatory);
		mIvUserIsVideo = (ImageView) contentView.findViewById(R.id.iv_isvideo);
		mTvUserUid = (TextView) contentView.findViewById(R.id.tv_other_page_uid);
		mTvUserTrendsCount = (TextView) contentView.findViewById(R.id.tv_other_page_trends_count);
		mIvTrendsImg = (RoundCornerImageView) contentView.findViewById(R.id.iv_trends_img);
		mTvTrendsContent = (TextView) contentView.findViewById(R.id.tv_other_page_trends_content);
		mTvTrendsTime = (TextView) contentView.findViewById(R.id.tv_other_page_trends_time);
		mTvXiaoDianBrief = (TextView) contentView.findViewById(R.id.tv_other_page_xiaodian);
		mViewTrends = contentView.findViewById(R.id.rl_other_page_trends);
		mViewShop = contentView.findViewById(R.id.ll_little_shop);
		mGvUserPhoto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArrayList<String> imageList = new ArrayList<String>();
				for (int i = 0; i < mUserPhoto.size(); i++) {
					imageList.add(mUserPhoto.get(i).getBigphoto());
				}
				Intent intent = new Intent(OtherHomeActivity.this, OnLineImageDetailActivity.class);
				intent.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
				intent.putExtra("position", position);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
			}
		});
		PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
		LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(
				UIUtil.getScreenWidth(this), UIUtil.getScreenWidth(this));
		scrollView.setHeaderLayoutParams(localObject);
		scrollView.setHeaderView(headView);
		scrollView.setZoomView(zoomView);
		scrollView.setScrollContentView(contentView);
		// scrollView.setParallax(true);
		scrollView.setHideHeader(false);
		scrollView.setZoomEnabled(false);
	}

	private void getotherhomeinfo() {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null == result) {
					return;
				}
				notifyUserInfo((OtherUserModel) result.ResultObject);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.otherhomeinfo(mTouid);
			}
		});
	}

	private void notifyUserInfo(OtherUserModel model) {
		if (null == model) {
			return;
		}
		mCurrentModel = model;
		Integer sex = model.getSex();
		int imageBgRes = R.drawable.default_man_bg;
		if (1 == sex) {
			mIVUserSex.setImageResource(R.drawable.icon_man);
		} else {
			imageBgRes = R.drawable.default_woman_bg;
			mIVUserSex.setImageResource(R.drawable.icon_woman);
		}
		mIvLevel.setImageResource(ConstantSet.getLevelIcons(model.getLevel()));
		if (0 == model.getCreatetime()) {
			UIUtil.setViewGone(mViewTrends);
		} else {
			UIUtil.setViewVisible(mViewTrends);
			try {
				mTvTrendsTime.setText(DateUtil.getCommentTime(model.getCreatetime()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtil.isNullOrEmpty(model.getResource())) {
				UIUtil.setViewGone(mIvTrendsImg);
			} else {
				UIUtil.setViewVisible(mIvTrendsImg);
				mImageLoader.displayImage(model.getResource(), mIvTrendsImg, new DisplayImageOptions.Builder()
						.cacheInMemory().cacheOnDisc().displayer(new SimpleBitmapDisplayer()).build());
			}
			mTvUserTrendsCount.setText(model.getTrendssum());
			mTvTrendsContent.setText(model.getContent());
		}
		try {
			mTvUserAge.setText(model.getAge());
			mTvUserConstellatory.setText(model.getConstellatory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (1 == model.getIsvideo()) {
			UIUtil.setViewVisible(mIvUserIsVideo);
		} else {
			UIUtil.setViewGone(mIvUserIsVideo);
		}

		mTvUserUid.setText("ID " + model.getTouid());
		mTvTitle.setText(model.getNickname());
		List<PictureModel> pictureModels = model.getPictureModels();
		mUserPhoto.clear();
		if (null != pictureModels) {
			mUserPhoto.addAll(pictureModels);
		}
		mUserPhotoAdapter.notifyDataSetChanged();
		String background = model.getBackground();
		mIvBackground.setTag(background);
		mImageLoader.displayImage(background, mIvBackground, new DisplayImageOptions.Builder().cacheInMemory()
				.cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageBgRes).build());
		String description = model.getStoreinfo();
		int status = model.getChatstatus();
		if (1 == status || StringUtil.isNullOrEmpty(description)) {
			mViewShop.setVisibility(View.GONE);
		} else {
			mViewShop.setVisibility(View.VISIBLE);
			mTvXiaoDianBrief.setText(description);
		}
		String userType = model.getUsertype();
		if (StringUtil.isNullOrEmpty(userType)) {
			UIUtil.setViewGone(mGvUserCate);
		} else {
			UIUtil.setViewVisible(mGvUserCate);
			String[] cateIds = userType.split(",");
			List<UserCategoryModel> allModels = DBMgr.getHistoryData(UserCategoryModel.class);
			if (!allModels.isEmpty() && null != cateIds) {
				for (int i = 0; i < allModels.size(); i++) {
					UserCategoryModel tempModel = allModels.get(i);
					for (int j = 0; j < cateIds.length; j++) {
						if (cateIds[j].equals(tempModel.getCatid() + "")) {
							tempModel.setIsChecked(true);
							mCategoryModels.add(tempModel);
						}
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		}
		if (1 == model.getChatstatus()) {
			mViewChat.setVisibility(View.GONE);// 未签约不显示
		} else {
			mViewChat.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.ibtn_with_back_title_btn_right:
				showPickPop();
				break;
			case R.id.other_page_btn_date:
				if (null == mCurrentModel) {
					return;
				}
				int status = mCurrentModel.getChatstatus();
				if (2 == status) {
					String title = mCurrentModel.getNickname();
					if (!StringUtil.isNullOrEmpty(mTouid) && !StringUtil.isNullOrEmpty(title)) {
						RongIM.getInstance().startPrivateChat(OtherHomeActivity.this, mTouid, title);
					}
				} else if (3 == status) {
					// 签约用户正常接单中显示约可点击进入购买
					Intent intent1 = new Intent(OtherHomeActivity.this, BuyServiceActivity.class);
					intent1.putExtra("touid", mTouid);
					intent1.putExtra("nickname", mCurrentModel.getNickname());
					startActivity(intent1);
				} else if (4 == status) {
					// 签约用户暂不提供服务显示，但不可点击
					toast("当前不接收服务");
				}
				break;
			case R.id.rl_other_page_trends:
				Intent intent = new Intent(OtherHomeActivity.this, OtherTrendsListActivity.class);
				intent.putExtra("touid", mTouid);
				startActivity(intent);
				break;
			case R.id.iv_personal_bg:
				Object object = mIvBackground.getTag();
				if (null == object || StringUtil.isNullOrEmpty((String) object)) {
					return;
				}
				ArrayList<String> imageList = new ArrayList<String>();
				imageList.add((String) object);
				Intent intent1 = new Intent(mContext, OnLineImageDetailActivity.class);
				intent1.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
				startActivity(intent1);
				overridePendingTransition(R.anim.activity_enter_scale, R.anim.activity_persistent);
				break;
			default:
				break;
		}
	}

	private void showPickPop() {
		if (null == mCurrentModel) {
			return;
		}
		final int status = mCurrentModel.getRelation();// 1=已关注,0=未关注
		final View popView = View.inflate(this, R.layout.view_actionsheet, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final Button btn2 = (Button) popView.findViewById(R.id.btn_action_2);
		final Button btn1 = (Button) popView.findViewById(R.id.btn_action_1);
		final TextView title = (TextView) popView.findViewById(R.id.tv_action_sheet_title);
		title.setText("选择操作");
		btn1.setText("举报");
		if (1 == status) {
			btn2.setText("取消关注");
			btn2.setTextColor(Color.RED);
		} else if (0 == status) {
			btn2.setText("+关注");
		}
		final Button btnCancel = (Button) popView.findViewById(R.id.btn_cancel);
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				if (1 == status) {
					follow(2, mCurrentModel.getTouid());
				} else if (0 == status) {
					follow(1, mCurrentModel.getTouid());
				}
			}
		});
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				showJuBaoPop();
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

	private void showJuBaoPop() {
		final View popView = View.inflate(this, R.layout.view_jubao, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final Button btn4 = (Button) popView.findViewById(R.id.btn_action_4);
		final Button btn3 = (Button) popView.findViewById(R.id.btn_action_3);
		final Button btn2 = (Button) popView.findViewById(R.id.btn_action_2);
		final Button btn1 = (Button) popView.findViewById(R.id.btn_action_1);
		btn4.setTag(1);
		btn3.setTag(2);
		btn2.setTag(3);
		btn1.setTag(4);
		OnClickListener mJubaoListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				mJubaoContent = (Integer) v.getTag();
				showDialog(DIALOG_JUBAO);
			}
		};
		btn1.setOnClickListener(mJubaoListener);
		btn2.setOnClickListener(mJubaoListener);
		btn3.setOnClickListener(mJubaoListener);
		btn4.setOnClickListener(mJubaoListener);
		popManager.show(Gravity.CENTER);
	}

	private void jubao(final int content) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(OtherHomeActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				toast("举报成功");
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.report(content + "", mTouid);
			}
		});
	}

	private void follow(final int status, final String followuid) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				if (1 == status) {
					mCurrentModel.setRelation(1);
				} else if (2 == status) {
					mCurrentModel.setRelation(0);
				}
				showErrorMsg(result);
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.userFollow(status + "", followuid);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_JUBAO:
				return createDialogBuilder(this, getString(R.string.button_text_tips), "确定要举报吗？",
						getString(R.string.button_text_no), "确定").create(id);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_JUBAO:
				jubao(mJubaoContent);
				break;
			default:
				break;
		}
	}

}

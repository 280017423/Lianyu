package com.xhr88.lp.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.AttentionTrendsListActivity;
import com.xhr88.lp.activity.EditUserCateActivity;
import com.xhr88.lp.activity.HelpListActivity;
import com.xhr88.lp.activity.HistoryServiceActivity;
import com.xhr88.lp.activity.MainActivity;
import com.xhr88.lp.activity.MyAssetsActivity;
import com.xhr88.lp.activity.MyAttentionActivity;
import com.xhr88.lp.activity.MyFansActivity;
import com.xhr88.lp.activity.MyLittleShopActivity;
import com.xhr88.lp.activity.MyTrendsListActivity;
import com.xhr88.lp.activity.PersonalActivity;
import com.xhr88.lp.activity.ServiceAgreementActivity;
import com.xhr88.lp.activity.SettingActivity;
import com.xhr88.lp.activity.ShopCheckFailActivity;
import com.xhr88.lp.activity.ShopCheckingActivity;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.LittleShopReq;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.UserInfoModel;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.NumberUtil;
import com.xhr88.lp.util.SharedPreferenceUtil;

public class MyCenterFragment extends FragmentBase implements OnClickListener {

	private static final int LOAD_USERINFO_SUCCESS = 1;
	private static final int LOAD_USERINFO_FAILE = 2;
	private static final int REQUEST_CODE_EDIT_INFO = 100;

	private RoundCornerImageView mIvMyHeadIcon;
	private ImageView mIvHasNewFans;
	private ImageView mIvHasVideo;
	private ImageView mIvLevel;
	private TextView mTvNickName;
	private TextView mTvUserId;
	private TextView mTvAttentionNum;
	private TextView mTvTrendsNum;
	private TextView mTvFansNum;
	private TextView mTvAttentionTrendsNum;
	private TextView mTvCoin;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != intent && ConstantSet.ACTION_REFREASH_COIN.equals(intent.getAction())) {
				EvtLog.d("aaa", "shuaxin----");
				if (UserMgr.hasUserInfo()) {
					UserViewModel userInfo = UserDao.getLocalUserInfo();
					mTvCoin.setText(userInfo.UserInfo.getCoin() + "");
				}
			}
		}
	};
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			ActionResult result = (ActionResult) msg.obj;
			if (null == result) {
				return;
			}
			switch (msg.what) {
				case LOAD_USERINFO_SUCCESS:
					UserViewModel model = (UserViewModel) result.ResultObject;
					if (null != model) {
						notifyUserInfo(model);
					}
					break;
				case LOAD_USERINFO_FAILE:
					UserViewModel userViewModel = UserDao.getLocalUserInfo();
					if (null != userViewModel) {
						notifyUserInfo(userViewModel);
					}
					showErrorMsg(result);
					break;
				default:
					break;
			}
		}
	};

	private void notifyUserInfo(UserViewModel model) {
		UserInfoModel userInfoModel = model.UserInfo;
		if (null == userInfoModel) {
			return;
		}
		int imageRes = R.drawable.default_man_head_bg;
		if (1 != userInfoModel.getSex()) {
			imageRes = R.drawable.default_woman_head_bg;
		}
		mImageLoader.displayImage(userInfoModel.getHeadimg(), mIvMyHeadIcon, new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageRes)
				.build());
		mTvNickName.setText(userInfoModel.getNickname());
		mTvUserId.setText("ID : " + userInfoModel.getUid());
		mTvTrendsNum.setText(NumberUtil.format(userInfoModel.getTrendsnum()));
		mTvFansNum.setText(NumberUtil.format(userInfoModel.getFansnum()));
		mTvCoin.setText(userInfoModel.getCoin() + "");
		mIvLevel.setImageResource(ConstantSet.getLevelIcons(userInfoModel.getLevel()));
		int follownum = userInfoModel.getFollownum();
		mTvAttentionNum.setText(NumberUtil.format(follownum));

		int newtrends = userInfoModel.getNewtrends();
		if (0 >= newtrends) {
			UIUtil.setViewGone(mTvAttentionTrendsNum);
		} else {
			UIUtil.setViewVisible(mTvAttentionTrendsNum);
			if (newtrends > 99) {
				mTvAttentionTrendsNum.setText("99+");
			} else {
				mTvAttentionTrendsNum.setText(userInfoModel.getNewtrends() + "");
			}
		}

		if (1 == userInfoModel.getIsnewfans()) {
			UIUtil.setViewVisible(mIvHasNewFans);
		} else {
			UIUtil.setViewGone(mIvHasNewFans);
		}
		if (1 == userInfoModel.getIsvideo()) {
			UIUtil.setViewVisible(mIvHasVideo);
		} else {
			UIUtil.setViewGone(mIvHasVideo);
		}
		setNewTrendsNotify(View.VISIBLE == mIvHasNewFans.getVisibility()
				|| View.VISIBLE == mTvAttentionTrendsNum.getVisibility());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initVariables();
		loadUserInfo();
		super.onCreate(savedInstanceState);
	}

	private void initVariables() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstantSet.ACTION_REFREASH_COIN);
		getActivity().registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View layout = inflater.inflate(R.layout.activity_personal_main, container, false);
		initViews(layout);
		return layout;
	}

	public void loadUserInfo() {
		new ActionProcessor().startAction(getActivity(), new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessage(LOAD_USERINFO_SUCCESS, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendMessage(LOAD_USERINFO_FAILE, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.userMyhomeinfo();
			}
		});
	}

	protected void sendMessage(int what, Object obj) {
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}

	private void initViews(View layout) {
		// 用户资料
		layout.findViewById(R.id.rl_user_item).setOnClickListener(this);
		// 我的动态
		layout.findViewById(R.id.rl_my_trends).setOnClickListener(this);
		// 关注
		layout.findViewById(R.id.rl_attention_item).setOnClickListener(this);
		// 粉丝
		layout.findViewById(R.id.rl_fans_item).setOnClickListener(this);

		layout.findViewById(R.id.rl_attention_trends).setOnClickListener(this);
		layout.findViewById(R.id.rl_little_shop).setOnClickListener(this);
		layout.findViewById(R.id.rl_help_item).setOnClickListener(this);
		layout.findViewById(R.id.rl_history_service_item).setOnClickListener(this);
		layout.findViewById(R.id.rl_setting_item).setOnClickListener(this);
		layout.findViewById(R.id.rl_my_coin_item).setOnClickListener(this);

		mTvCoin = (TextView) layout.findViewById(R.id.tv_my_coin);
		mIvLevel = (ImageView) layout.findViewById(R.id.iv_level);
		mTvFansNum = (TextView) layout.findViewById(R.id.tv_fans_num);
		mTvTrendsNum = (TextView) layout.findViewById(R.id.tv_trends_num);
		mTvAttentionNum = (TextView) layout.findViewById(R.id.tv_attention_num);
		mTvAttentionTrendsNum = (TextView) layout.findViewById(R.id.tv_attention_trends_num);

		mIvMyHeadIcon = (RoundCornerImageView) layout.findViewById(R.id.iv_my_head_icon);
		mIvHasNewFans = (ImageView) layout.findViewById(R.id.iv_new_fans);
		mIvHasVideo = (ImageView) layout.findViewById(R.id.iv_isvideo);
		mTvNickName = (TextView) layout.findViewById(R.id.tv_nickname);
		mTvUserId = (TextView) layout.findViewById(R.id.tv_user_id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_user_item:
				Intent intent = new Intent(getActivity(), PersonalActivity.class);
				startActivityForResult(intent, REQUEST_CODE_EDIT_INFO);
				break;
			case R.id.rl_my_trends:
				Intent intent1 = new Intent(getActivity(), MyTrendsListActivity.class);
				startActivityForResult(intent1, REQUEST_CODE_EDIT_INFO);
				break;
			case R.id.rl_attention_item:
				Intent intent2 = new Intent(getActivity(), MyAttentionActivity.class);
				startActivityForResult(intent2, REQUEST_CODE_EDIT_INFO);
				break;
			case R.id.rl_fans_item:
				Intent intent3 = new Intent(getActivity(), MyFansActivity.class);
				startActivityForResult(intent3, REQUEST_CODE_EDIT_INFO);
				break;
			case R.id.rl_attention_trends:
				Intent intent4 = new Intent(getActivity(), AttentionTrendsListActivity.class);
				startActivityForResult(intent4, REQUEST_CODE_EDIT_INFO);
				break;
			case R.id.rl_little_shop:
				// 请求网络，判断状态，如果小店已经开通就不去请求网络，没有开通才去请求网络，更新本地状态
				String storeStatus = SharedPreferenceUtil.getStringValueByKey(XhrApplicationBase.CONTEXT,
						ServerAPIConstant.KEY_CONFIG_FILENAME, ServerAPIConstant.KEY_STORE_STATUS);
				// 小店状态0:未开通, 1:待审核,2:审核通过，3:审核失败
				if ("2".equals(storeStatus)) {
					String userType = SharedPreferenceUtil.getStringValueByKey(XhrApplicationBase.CONTEXT,
							ServerAPIConstant.KEY_CONFIG_FILENAME, ServerAPIConstant.KEY_USERTYPE);
					jumpToLitterShop(2, "", userType);
				} else {
					getapplystatus();
				}
				break;
			case R.id.rl_my_coin_item:
				startActivity(new Intent(getActivity(), MyAssetsActivity.class));
				break;
			case R.id.rl_history_service_item:
				startActivity(new Intent(getActivity(), HistoryServiceActivity.class));
				break;
			case R.id.rl_setting_item:
				startActivity(new Intent(getActivity(), SettingActivity.class));
				break;
			case R.id.rl_help_item:
				startActivity(new Intent(getActivity(), HelpListActivity.class));
				break;
			default:
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			return;
		}
		if (REQUEST_CODE_EDIT_INFO == requestCode) {
			loadUserInfo();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getapplystatus() {
		new ActionProcessor().startAction(getActivity(), true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				String info = "";
				if (null != result) {
					info = (String) result.ResultObject;
				}
				String storeStatus = SharedPreferenceUtil.getStringValueByKey(XhrApplicationBase.CONTEXT,
						ServerAPIConstant.KEY_CONFIG_FILENAME, ServerAPIConstant.KEY_STORE_STATUS);
				jumpToLitterShop(Integer.parseInt(storeStatus), info, "");
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return LittleShopReq.getapplystatus();
			}
		});
	}

	private void jumpToLitterShop(int status, String info, String userType) {
		switch (status) {
			case 0:
				startActivity(new Intent(getActivity(), ServiceAgreementActivity.class));
				break;
			case 1:// 待审核
				startActivity(new Intent(getActivity(), ShopCheckingActivity.class));
				break;
			case 2:// 审核通过
				if (StringUtil.isNullOrEmpty(userType)) {
					Intent intent = new Intent(getActivity(), EditUserCateActivity.class);
					intent.putExtra("fromMyCenter", true);
					startActivity(intent);
				} else {
					startActivity(new Intent(getActivity(), MyLittleShopActivity.class));
				}
				break;
			case 3:// 审核失败
				Intent intent = new Intent(getActivity(), ShopCheckFailActivity.class);
				intent.putExtra("failInfo", info);
				startActivity(intent);
				break;
			default:
				startActivity(new Intent(getActivity(), ServiceAgreementActivity.class));
				break;
		}
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void setNewTrendsNotify(boolean hasNewTrends) {
		Intent in = new Intent();
		in.setAction(MainActivity.ACTION_RECEIVE_NEW_TRENDS);
		in.putExtra("hasNewTrends", hasNewTrends);
		getActivity().sendBroadcast(in);
	}
}

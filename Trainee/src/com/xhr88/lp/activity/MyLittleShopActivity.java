package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr.framework.widget.SlipButton;
import com.xhr.framework.widget.SlipButton.OnChangedListener;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.ShopServiceAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.request.LittleShopReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.ShopInfoModel;
import com.xhr88.lp.model.datamodel.ShopServiceModel;
import com.xhr88.lp.model.datamodel.UserCategoryModel;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.widget.MListView;
import com.xhr88.lp.widget.wheelview.OnWheelChangedListener;
import com.xhr88.lp.widget.wheelview.WheelView;
import com.xhr88.lp.widget.wheelview.adapters.ArrayWheelAdapter;

/**
 * 我的小店界面
 * 
 * @author zou.sq
 */
public class MyLittleShopActivity extends TraineeBaseActivity implements OnClickListener, OnChangedListener {

	private static final int REQUEST_CODE_ADD_SHOP_DESCRIPTION = 1000;
	private static final int REQUEST_CODE_ADD_USER_CATE = 1001;
	private LoadingUpView mLoadingUpView;
	private EditText mTvDescription;
	private SlipButton mSbtnStartService;
	private TextView mTvServiceInfo;
	private TextView mTvPeopleNum;
	private TextView mTvCateName;
	private MListView mLvService;
	private ShopInfoModel mCurrentShopInfoModel;
	private ShopServiceAdapter mShopServiceAdapter;
	private List<ShopServiceModel> mShopServiceModels;
	private ScrollView mSvNomal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_litter_shop);
		initVariables();
		initViews();
		setListener();
		getShopInfo();
	}

	private void setListener() {
		mSbtnStartService.setOnChangedListener(new OnChangedListener() {

			@Override
			public void onChanged(View view, final boolean checkState) {
				showLoadingUpView(mLoadingUpView);
				new ActionProcessor().startAction(MyLittleShopActivity.this, true, true, new IActionListener() {

					@Override
					public void onSuccess(ActionResult result) {
						dismissLoadingUpView(mLoadingUpView);
						if (null != mCurrentShopInfoModel) {
							mCurrentShopInfoModel.setStorestatus(checkState ? 1 : 0);
							refreashStoreStatus();
						}
					}

					@Override
					public void onError(ActionResult result) {
						refreashStoreStatus();
						showErrorMsg(result);
						dismissLoadingUpView(mLoadingUpView);
					}

					@Override
					public ActionResult onAsyncRun() {
						return LittleShopReq.editShopInfo("", checkState ? "1" : "0", "", "");
					}
				});
			}
		});

	}

	private void getShopInfo() {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				updateUi(result);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return LittleShopReq.getShopInfo();
			}
		});

	}

	protected void updateUi(ActionResult result) {
		if (null == result) {
			return;
		}
		mCurrentShopInfoModel = (ShopInfoModel) result.ResultObject;
		if (null != mCurrentShopInfoModel) {
			mTvDescription.setText(mCurrentShopInfoModel.getDescription());
			mTvPeopleNum.setText(mCurrentShopInfoModel.getServenum() + "");
			refreashService();
			refreashStoreStatus();
			refreashUserCate();
		}
		new CountDownTimer(20, 20) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				mSvNomal.scrollTo(0, 0);
			}
		}.start();
	}

	private void refreashUserCate() {
		String userType = mCurrentShopInfoModel.getUsertype();
		if (StringUtil.isNullOrEmpty(userType)) {
			editUserCate(userType);
		} else {
			String cateNames = "";
			String[] cateIds = userType.split(",");
			List<UserCategoryModel> userCategoryModels = DBMgr.getHistoryData(UserCategoryModel.class);
			for (int i = 0; i < userCategoryModels.size(); i++) {
				UserCategoryModel model = userCategoryModels.get(i);
				for (int j = 0; j < cateIds.length; j++) {
					if (cateIds[j].equals(model.getCatid() + "")) {
						cateNames += model.getCategoryname() + ",";
					}
				}
			}
			if (!StringUtil.isNullOrEmpty(cateNames)) {
				cateNames = cateNames.substring(0, cateNames.lastIndexOf(","));
			}
			mTvCateName.setText(cateNames);
		}
	}

	private void editUserCate(String userType) {
		Intent intent = new Intent(this, EditUserCateActivity.class);
		intent.putExtra(ServerAPIConstant.KEY_USERTYPE, userType);
		startActivityForResult(intent, REQUEST_CODE_ADD_USER_CATE);
	}

	private void refreashService() {
		if (null != mCurrentShopInfoModel.getList()) {
			mShopServiceModels.clear();
			mShopServiceModels.addAll(mCurrentShopInfoModel.getList());
			mShopServiceAdapter.notifyDataSetChanged();
		}
	}

	private void refreashStoreStatus() {
		// 是否提供服务状态1=提供服务, 0=不提供服务
		int storestatus = mCurrentShopInfoModel.getStorestatus();
		if (1 == storestatus) {
			mSbtnStartService.setCheck(true);
			mTvServiceInfo.setText("提供服务");
		} else {
			mSbtnStartService.setCheck(false);
			mTvServiceInfo.setText("暂不提供服务");
		}
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this);
		mShopServiceModels = new ArrayList<ShopServiceModel>();
		mShopServiceAdapter = new ShopServiceAdapter(this, mShopServiceModels, mImageLoader, this);
	}

	private void initViews() {
		mTvDescription = (EditText) findViewById(R.id.edt_introduce_content);
		mSbtnStartService = (SlipButton) findViewById(R.id.sbtn_start_service);
		mTvServiceInfo = (TextView) findViewById(R.id.tv_service_info);
		mTvCateName = (TextView) findViewById(R.id.tv_user_cate_name);
		mTvPeopleNum = (TextView) findViewById(R.id.tv_people_number);
		mLvService = (MListView) findViewById(R.id.lv_service);
		mLvService.setAdapter(mShopServiceAdapter);
		mSvNomal = (ScrollView) findViewById(R.id.sv_litter_shop);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.edt_introduce_content:
			case R.id.ll_litter_shop_introduce:
				Intent intent = new Intent(MyLittleShopActivity.this, AddShopIntroduceActivity.class);
				intent.putExtra(ConstantSet.KEY_INTRODUCE_CONTENT, mTvDescription.getText().toString().trim());
				startActivityForResult(intent, REQUEST_CODE_ADD_SHOP_DESCRIPTION);
				break;
			case R.id.ll_litter_shop_people_number:
				initWheelView();
				break;
			case R.id.ll_litter_shop_user_cate:
				editUserCate(mCurrentShopInfoModel.getUsertype());
				break;

			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK != resultCode) {
			return;
		}
		if (REQUEST_CODE_ADD_SHOP_DESCRIPTION == requestCode && null != data) {
			String description = data.getStringExtra(ConstantSet.KEY_INTRODUCE_CONTENT);
			if (null != description) {
				mTvDescription.setText(description);
			}
		}
		if (REQUEST_CODE_ADD_USER_CATE == requestCode && null != data) {
			String userType = data.getStringExtra(ServerAPIConstant.KEY_USERTYPE);
			mCurrentShopInfoModel.setUsertype(userType);
			refreashUserCate();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onChanged(View view, final boolean checkState) {
		final ShopServiceModel model = (ShopServiceModel) view.getTag();
		if (null != model && !StringUtil.isNullOrEmpty(model.getSid())) {
			final String sid = model.getSid();
			showLoadingUpView(mLoadingUpView);
			new ActionProcessor().startAction(MyLittleShopActivity.this, true, true, new IActionListener() {

				@Override
				public void onSuccess(ActionResult result) {
					dismissLoadingUpView(mLoadingUpView);

					List<ShopServiceModel> models = mCurrentShopInfoModel.getList();
					for (int i = 0; i < models.size(); i++) {
						ShopServiceModel tempModel = models.get(i);
						if (sid.equals(tempModel.getSid())) {
							tempModel.setOpen(checkState ? 1 : 0);
							break;
						}
					}
					refreashService();
				}

				@Override
				public void onError(ActionResult result) {
					refreashService();
					showErrorMsg(result);
					dismissLoadingUpView(mLoadingUpView);
				}

				@Override
				public ActionResult onAsyncRun() {
					return LittleShopReq.editServe(sid, checkState ? "1" : "0");
				}
			});
		}
	}

	private void initWheelView() {
		if (null == mCurrentShopInfoModel) {
			return;
		}
		final String[] displayPeopleNums = mCurrentShopInfoModel.getDisplayPeopleNum();
		final String[] peopleNums = mCurrentShopInfoModel.getPeopleNum();
		final View popView = View.inflate(this, R.layout.view_service_people_picker, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final Button btnSure = (Button) popView.findViewById(R.id.btn_set_ensure);
		final TextView tvPeopleNum = (TextView) popView.findViewById(R.id.tv_people_number);
		final WheelView peopleNumWheelView = (WheelView) popView.findViewById(R.id.wv_hour_picker);
		ArrayWheelAdapter<String> mHourAdapter = new ArrayWheelAdapter<String>(this, displayPeopleNums);
		mHourAdapter.setItemResource(R.layout.view_wheel_text_item);
		mHourAdapter.setItemTextResource(R.id.tv_time_item);
		peopleNumWheelView.setVisibleItems(5);
		peopleNumWheelView.setViewAdapter(mHourAdapter);
		peopleNumWheelView.setCurrentItem(mCurrentShopInfoModel.getPeopleIndex());
		if (peopleNums.length > 0) {
			tvPeopleNum.setText(displayPeopleNums[mCurrentShopInfoModel.getPeopleIndex()]);
		}
		peopleNumWheelView.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (peopleNums.length > newValue) {
					tvPeopleNum.setText(displayPeopleNums[newValue]);
				}
			}
		});
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final int newValue = peopleNumWheelView.getCurrentItem();
				popManager.dismiss();
				if (peopleNums.length > newValue) {
					showLoadingUpView(mLoadingUpView);
					new ActionProcessor().startAction(MyLittleShopActivity.this, true, true, new IActionListener() {

						@Override
						public void onSuccess(ActionResult result) {
							dismissLoadingUpView(mLoadingUpView);
							if (null != mCurrentShopInfoModel) {
								mCurrentShopInfoModel.setServenum(Integer.parseInt(peopleNums[newValue]));
								mTvPeopleNum.setText(mCurrentShopInfoModel.getServenum() + "");
							}
						}

						@Override
						public void onError(ActionResult result) {
							showErrorMsg(result);
							dismissLoadingUpView(mLoadingUpView);
						}

						@Override
						public ActionResult onAsyncRun() {
							return LittleShopReq.editShopInfo("", "", "" + peopleNums[newValue], "");
						}
					});
				}
			}
		});
		popManager.show(Gravity.BOTTOM);
	}
}

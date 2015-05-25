package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.EditUserCateAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.request.LittleShopReq;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.UserCategoryModel;
import com.xhr88.lp.util.SharedPreferenceUtil;

/**
 * 编辑用户类型界面
 * 
 * @author zou.sq
 */
public class EditUserCateActivity extends TraineeBaseActivity implements OnClickListener {

	private GridView mGvUserCate;
	private List<UserCategoryModel> mCategoryModels;
	private EditUserCateAdapter mAdapter;
	private boolean mIsFromMyCenter;
	private LoadingUpView mLoadingUpView;
	private String mUserType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user_cate);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this, true);
		mIsFromMyCenter = getIntent().getBooleanExtra("fromMyCenter", false);
		mCategoryModels = new ArrayList<UserCategoryModel>();
		List<UserCategoryModel> tmpCategoryModels = DBMgr.getHistoryData(UserCategoryModel.class);
		for (UserCategoryModel userCategoryModel : tmpCategoryModels) {
			int sex = UserDao.getSex();
			if (sex == userCategoryModel.getType()) {
				mCategoryModels.add(userCategoryModel);
			}
		}
		if (!mIsFromMyCenter) {
			String userType = getIntent().getStringExtra(ServerAPIConstant.KEY_USERTYPE);
			String[] cateIds = userType.split(",");
			if (!mCategoryModels.isEmpty() && null != cateIds) {
				for (int i = 0; i < mCategoryModels.size(); i++) {
					UserCategoryModel model = mCategoryModels.get(i);
					for (int j = 0; j < cateIds.length; j++) {
						if (cateIds[j].equals(model.getCatid() + "")) {
							model.setIsChecked(true);
						}
					}
				}
			}
		}
		mAdapter = new EditUserCateAdapter(this, mCategoryModels);
	}

	private void initViews() {
		mGvUserCate = (GridView) findViewById(R.id.gv_user_cate);
		mGvUserCate.setAdapter(mAdapter);
		mGvUserCate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UserCategoryModel model = (UserCategoryModel) parent.getAdapter().getItem(position);
				if (null != model) {
					int cateId = model.getCatid();
					for (int i = 0; i < mCategoryModels.size(); i++) {
						UserCategoryModel userCategoryModel = mCategoryModels.get(i);
						if (cateId == userCategoryModel.getCatid()) {
							boolean checked = userCategoryModel.getIsChecked();
							if (checked) {
								userCategoryModel.setIsChecked(!checked);
							} else {
								userCategoryModel.setIsChecked(canChecked() ? !checked : checked);
							}
							mAdapter.notifyDataSetChanged();
							return;
						}
					}
				}
			}
		});
	}

	private boolean canChecked() {
		int count = 0;
		for (int i = 0; i < mCategoryModels.size(); i++) {
			UserCategoryModel userCategoryModel = mCategoryModels.get(i);
			if (userCategoryModel.getIsChecked()) {
				count++;
			}
		}
		return count < 5;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.title_with_back_title_btn_right:
				checkAndUpdate();
				break;
			default:
				break;
		}
	}

	private void checkAndUpdate() {
		mUserType = "";
		for (int i = 0; i < mCategoryModels.size(); i++) {
			UserCategoryModel userCategoryModel = mCategoryModels.get(i);
			if (userCategoryModel.getIsChecked()) {
				mUserType += userCategoryModel.getCatid() + ",";
			}
		}
		if (!StringUtil.isNullOrEmpty(mUserType)) {
			mUserType = mUserType.substring(0, mUserType.lastIndexOf(","));
		} else {
			toast("请选择1-5个类型");
			return;
		}

		new ActionProcessor().startAction(EditUserCateActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				SharedPreferenceUtil.saveValue(XhrApplicationBase.CONTEXT, ServerAPIConstant.KEY_CONFIG_FILENAME,
						ServerAPIConstant.KEY_USERTYPE, mUserType);
				dismissLoadingUpView(mLoadingUpView);
				if (mIsFromMyCenter) {
					startActivity(new Intent(EditUserCateActivity.this, MyLittleShopActivity.class));
				} else {
					Intent intent = new Intent(EditUserCateActivity.this, MyLittleShopActivity.class);
					intent.putExtra(ServerAPIConstant.KEY_USERTYPE, mUserType);
					setResult(RESULT_OK, intent);
				}
				finish();
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
				dismissLoadingUpView(mLoadingUpView);
			}

			@Override
			public ActionResult onAsyncRun() {
				return LittleShopReq.editShopInfo("", "", "", mUserType);
			}
		});
	}
}

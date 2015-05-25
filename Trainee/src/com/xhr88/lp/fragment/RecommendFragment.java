package com.xhr88.lp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshGridView;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.MainActivity;
import com.xhr88.lp.activity.MainActivity.TabHomeIndex;
import com.xhr88.lp.activity.MyTrendsDetailActivity;
import com.xhr88.lp.activity.OtherHomeActivity;
import com.xhr88.lp.activity.TrendsDetailActivity;
import com.xhr88.lp.adapter.RecommendAdapter;
import com.xhr88.lp.adapter.UserCateAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.RecommendReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.common.ServerAPIConstant;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.RecommendListModel;
import com.xhr88.lp.model.datamodel.UserCategoryModel;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.util.SharedPreferenceUtil;

public class RecommendFragment extends FragmentBase implements OnItemClickListener, OnClickListener,
		OnCheckedChangeListener {

	private static final int GET_ALBUM_LIST_FAIL = 0;
	private static final int GET_ALBUM_LIST_SUCCESS = 1;
	private static final int PULL_DOWN = 0;
	private static final int PULL_UP = 1;

	private List<RecommendListModel> mRecommendListModels;
	private PullToRefreshGridView mPullToRefreshGridView;
	private int mCurrentPage;
	private RecommendAdapter mAlbumAdapter;
	private boolean mIsGetRecommend;
	private int mType;// 最终记录的性别
	private int mTemType;// 零时记录的性别，防止用户，点击切换之后不点击确定按钮
	private int mCategoryId;
	private PopWindowUtil mPopWindowUtil;
	private View mFilterView;
	private List<UserCategoryModel> mUserCategoryModels;
	private List<UserCategoryModel> mDisplayUserCategoryModels;
	private RadioButton mRbtnMale;
	private RadioButton mRbtnFemale;
	private RadioGroup mRgSex;
	private GridView mGvUserCate;
	private UserCateAdapter mUserCateAdapter;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_ALBUM_LIST_SUCCESS:
					List<RecommendListModel> models = (List<RecommendListModel>) result.ResultObject;
					mPullToRefreshGridView.onRefreshComplete();
					mCurrentPage++;
					mPullToRefreshGridView.setMode(Mode.BOTH);
					if (PULL_DOWN == msg.arg1) {
						mRecommendListModels.clear();
					}
					if (null != models && models.size() < ConstantSet.PAGE_SIZE) {
						mPullToRefreshGridView.setNoMoreData();
					}
					showData(result, true);
					break;
				case GET_ALBUM_LIST_FAIL:
					mPullToRefreshGridView.setMode(Mode.PULL_DOWN_TO_REFRESH);
					if (mRecommendListModels.isEmpty()) {
						// 当前列表无数据，网络异常并且本里无缓存数据，那么显示错误界面
						if (result.ResultCode != null && ActionResult.RESULT_CODE_NET_ERROR.equals(result.ResultCode)
								&& result.ResultObject != null
								&& !((List<RecommendListModel>) result.ResultObject).isEmpty()) {
							showData(result, false);
						}
					} else {
						showErrorMsg(result);
					}
					mPullToRefreshGridView.onRefreshComplete();
					break;
				default:
					break;
			}
			mIsGetRecommend = false;
		}
	};

	protected void showData(ActionResult result, boolean isSuccess) {
		List<RecommendListModel> models = (List<RecommendListModel>) result.ResultObject;
		if (null != models) {
			mRecommendListModels.addAll(models);
		}
		if (mAlbumAdapter != null) {
			mAlbumAdapter.notifyDataSetChanged();
		}
	}

	public static final RecommendFragment newInstance() {
		RecommendFragment fragment = new RecommendFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initVariables();
		((MainActivity) getActivity()).addOnClicListener(this);
		super.onCreate(savedInstanceState);
	}

	private void getUserCategory() {
		mUserCategoryModels = DBMgr.getHistoryData(UserCategoryModel.class);
		if (null == mUserCategoryModels) {
			mUserCategoryModels = new ArrayList<UserCategoryModel>();
		}
		UserCategoryModel allCategoryModel = new UserCategoryModel();
		allCategoryModel.setCategoryname("全部");
		allCategoryModel.setCatid(0);
		mUserCategoryModels.add(0, allCategoryModel);
	}

	private void initVariables() {
		int type = SharedPreferenceUtil.getIntegerValueByKey(getActivity(), ServerAPIConstant.KEY_CONFIG_FILENAME,
				ServerAPIConstant.KEY_TYPE);
		int categoryId = SharedPreferenceUtil.getIntegerValueByKey(getActivity(),
				ServerAPIConstant.KEY_CONFIG_FILENAME, ServerAPIConstant.KEY_CATEGORY_NAME);
		if (-1 == categoryId) {
			mCategoryId = 0;
		} else {
			mCategoryId = categoryId;
		}
		if (-1 == type) {
			mType = 1 == UserDao.getSex() ? 2 : 1;
		} else {
			mType = type;
		}
		mFilterView = View.inflate(getActivity(), R.layout.view_recommend_filter, null);
		mPopWindowUtil = new PopWindowUtil(mFilterView, null);
		mRecommendListModels = new ArrayList<RecommendListModel>();
		mAlbumAdapter = new RecommendAdapter(getActivity(), mRecommendListModels, mImageLoader, this);
		mDisplayUserCategoryModels = new ArrayList<UserCategoryModel>();
		mUserCateAdapter = new UserCateAdapter(getActivity(), mDisplayUserCategoryModels);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View layout = inflater.inflate(R.layout.activity_recommend, container, false);
		initFilterView();
		initViews(layout);
		return layout;
	}

	private void initFilterView() {
		mRbtnMale = (RadioButton) mFilterView.findViewById(R.id.radio_male);
		mRbtnFemale = (RadioButton) mFilterView.findViewById(R.id.radio_female);
		mRgSex = (RadioGroup) mFilterView.findViewById(R.id.radio_group_sex);
		mGvUserCate = (GridView) mFilterView.findViewById(R.id.gv_recommend_cate);
		mGvUserCate.setAdapter(mUserCateAdapter);
		mRgSex.setOnCheckedChangeListener(this);
		mGvUserCate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UserCategoryModel model = (UserCategoryModel) parent.getAdapter().getItem(position);
				if (null != model) {
					mCategoryId = model.getCatid();
					mType = mTemType;
					mPopWindowUtil.dismiss();
					mPullToRefreshGridView.setHeaderVisible(true);
					SharedPreferenceUtil.saveValue(getActivity(), ServerAPIConstant.KEY_CONFIG_FILENAME,
							ServerAPIConstant.KEY_TYPE, mType);
					SharedPreferenceUtil.saveValue(getActivity(), ServerAPIConstant.KEY_CONFIG_FILENAME,
							ServerAPIConstant.KEY_CATEGORY_NAME, mCategoryId);
				}
			}
		});
	}

	private void initViews(View layout) {
		mPullToRefreshGridView = (PullToRefreshGridView) layout.findViewById(R.id.gv_recommend);
		GridView gridView = mPullToRefreshGridView.getRefreshableView();
		gridView.setCacheColorHint(getResources().getColor(R.color.transparent));
		gridView.setFadingEdgeLength(0);
		gridView.setOnItemClickListener(this);
		mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh() {
				mCurrentPage = 0;
				mPullToRefreshGridView.setMode(Mode.BOTH);
				getRecommendList(PULL_DOWN);
			}

			@Override
			public void onPullUpToRefresh() {
				getRecommendList(PULL_UP);
			}
		});
		mPullToRefreshGridView.setIsShowHeaderFresh(true);
		mPullToRefreshGridView.setHeaderVisible(true);
		mPullToRefreshGridView.setMode(Mode.PULL_DOWN_TO_REFRESH);
		mPullToRefreshGridView.setIsShowHeaderFresh(true);
		gridView.setAdapter(mAlbumAdapter);
	}

	private void getRecommendList(final int status) {
		if (mIsGetRecommend) {
			return;
		}
		mIsGetRecommend = true;
		UIUtil.setViewVisible(mPullToRefreshGridView);
		new ActionProcessor().startAction(getActivity(), new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessageToHandler(GET_ALBUM_LIST_SUCCESS, status, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendMessageToHandler(GET_ALBUM_LIST_FAIL, status, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return RecommendReq.getRecommendList(mType + "", mCategoryId + "", mCurrentPage);
			}
		});
	}

	private void sendMessageToHandler(int what, int status, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.arg1 = status;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		RecommendListModel model = (RecommendListModel) parent.getAdapter().getItem(position);
		if (null != model && !StringUtil.isNullOrEmpty(model.getTid())) {
			Intent intent = new Intent(getActivity(), TrendsDetailActivity.class);
			if (UserMgr.isMineUid(model.getUid())) {
				intent = new Intent(getActivity(), MyTrendsDetailActivity.class);
			}
			intent.putExtra(ConstantSet.KEY_TID, model.getTid());
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_nickname:
				RecommendListModel model = (RecommendListModel) view.getTag();
				if (null == model) {
					return;
				}
				String uid = model.getUid();
				if (UserMgr.isMineUid(uid)) {
					MainActivity.INSTANCE.setTabSelection(TabHomeIndex.HOME_MY_CENTER, false);
				} else {
					Intent intent = new Intent(getActivity(), OtherHomeActivity.class);
					intent.putExtra("touid", uid);
					startActivity(intent);
				}
				break;
			case R.id.btn_filter:
				showFilterView();
				break;
			default:
				break;
		}
	}

	private void showFilterView() {
		getUserCategory();
		mTemType = mType;
		mRbtnMale.setChecked(1 == mType);
		mRbtnFemale.setChecked(2 == mType);
		refreashUserCate(false);
		mPopWindowUtil.show(Gravity.BOTTOM);

	}

	private void refreashUserCate(boolean isFiltAll) {
		mDisplayUserCategoryModels.clear();
		mDisplayUserCategoryModels.add(0, mUserCategoryModels.get(0));
		for (int i = 0; i < mUserCategoryModels.size(); i++) {
			UserCategoryModel model = mUserCategoryModels.get(i);
			model.setIsChecked(!isFiltAll && mCategoryId == model.getCatid());
			if (mTemType == model.getType()) {
				mDisplayUserCategoryModels.add(model);
			}
		}
		if (null != mUserCateAdapter) {
			mUserCateAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.radio_male:
				mTemType = 1;
				break;
			case R.id.radio_female:
				mTemType = 2;
				break;
			default:
				break;
		}
		refreashUserCate(mType != mTemType);
	}
}

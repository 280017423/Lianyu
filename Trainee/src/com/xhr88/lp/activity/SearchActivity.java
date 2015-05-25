package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshListView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.SearchListAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.RecommendReq;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.RecommendListModel;
import com.xhr88.lp.model.datamodel.SearchUserModel;
import com.xhr88.lp.util.PopWindowUtil;

/**
 * 搜索界面
 * 
 * @author zou.sq
 */
public class SearchActivity extends TraineeBaseActivity implements OnClickListener, OnItemClickListener {

	private static final int GET_SEARCH_LIST_FAIL = 0;
	private static final int GET_SEARCH_LIST_SUCCESS = 1;
	private static final int PULL_DOWN = 0;
	private static final int PULL_UP = 1;

	private List<SearchUserModel> mRecommendListModels;
	private SearchListAdapter mSearchListAdapter;
	private PullToRefreshListView mPullToRefreshListView;
	private EditText mEdtSearch;
	private TextView mTvSearchResult;
	private int mCurrentPage;
	private boolean mIsGetSearchList;
	private String mKeyword = "";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_SEARCH_LIST_SUCCESS:
					List<SearchUserModel> models = (List<SearchUserModel>) result.ResultObject;
					mPullToRefreshListView.onRefreshComplete();
					mCurrentPage++;
					if (PULL_DOWN == msg.arg1) {
						mRecommendListModels.clear();
					}
					if (null != models && models.size() < ConstantSet.PAGE_SIZE) {
						mPullToRefreshListView.setNoMoreData();
					}
					UIUtil.setViewVisible(mTvSearchResult);
					mTvSearchResult.setText(getString(R.string.search_result, mKeyword));
					UIUtil.setViewGone(findViewById(R.id.search_title_mid_layout));
					UIUtil.setViewGone(findViewById(R.id.view_search_user));
					UIUtil.setViewGone(findViewById(R.id.title_with_back_title_btn_right));
					showData(result, true);
					break;
				case GET_SEARCH_LIST_FAIL:
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
					mPullToRefreshListView.onRefreshComplete();
					break;
				default:
					break;
			}
			mIsGetSearchList = false;
		}
	};

	protected void showData(ActionResult result, boolean isSuccess) {
		List<SearchUserModel> models = (List<SearchUserModel>) result.ResultObject;
		if (null != models) {
			mRecommendListModels.addAll(models);
		}
		if (mSearchListAdapter != null) {
			mSearchListAdapter.notifyDataSetChanged();
		}
		if (null == mRecommendListModels || mRecommendListModels.isEmpty()) {
			if (isSuccess) {
				toast("无结果");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initVariables();
		initViews();
	}

	private void initViews() {
		mEdtSearch = (EditText) findViewById(R.id.edt_search);
		mTvSearchResult = (TextView) findViewById(R.id.tv_search_result);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_search_user);
		ListView listView = mPullToRefreshListView.getRefreshableView();
		listView.setCacheColorHint(getResources().getColor(R.color.transparent));
		listView.setFadingEdgeLength(0);
		listView.setOnItemClickListener(this);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh() {
				mCurrentPage = 0;
				getSearchList(PULL_DOWN);
			}

			@Override
			public void onPullUpToRefresh() {
				getSearchList(PULL_UP);
			}
		});
		// mPullToRefreshListView.setIsShowHeaderFresh(true);
		// mPullToRefreshListView.setHeaderVisible(true);
		// mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setMode(Mode.BOTH);
		listView.setAdapter(mSearchListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SearchUserModel model = (SearchUserModel) parent.getAdapter().getItem(position);
				if (null == model || StringUtil.isNullOrEmpty(model.getUid())) {
					return;
				}
				Intent intent = new Intent(SearchActivity.this, OtherHomeActivity.class);
				intent.putExtra("touid", model.getUid());
				startActivity(intent);
			}
		});
	}

	private void initVariables() {
		mRecommendListModels = new ArrayList<SearchUserModel>();
		mSearchListAdapter = new SearchListAdapter(this, mRecommendListModels, mImageLoader, this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.btn_search:
				ImeUtil.hideSoftInput(SearchActivity.this, mEdtSearch);
				String keyword = mEdtSearch.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(keyword)) {
					toast("请输入ID或昵称");
					return;
				}
				mKeyword = keyword;
				mCurrentPage = 0;
				getSearchList(PULL_DOWN);
				break;
			case R.id.ibtn_relation_fans:
				SearchUserModel model = (SearchUserModel) view.getTag();
				showPickPop(model.getRelation(), model.getUid());
				break;
			default:
				break;
		}
	}

	private void showPickPop(final int status, final String followuid) {
		final View popView = View.inflate(this, R.layout.view_actionsheet, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final TextView title = (TextView) popView.findViewById(R.id.tv_action_sheet_title);
		title.setText("选择操作");
		final Button btn2 = (Button) popView.findViewById(R.id.btn_action_2);
		UIUtil.setViewGone(btn2);
		final Button btn1 = (Button) popView.findViewById(R.id.btn_action_1);
		if (0 == status) {
			btn1.setText("关注TA");
		} else if (2 == status || 1 == status) {
			btn1.setText("取消关注");
			btn1.setTextColor(Color.RED);
		}
		final Button btnCancel = (Button) popView.findViewById(R.id.btn_cancel);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popManager.dismiss();
				if (0 == status) {
					follow("1", followuid);
				} else if (2 == status || 1 == status) {
					follow("2", followuid);
				}
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	private void getSearchList(final int status) {
		if (mIsGetSearchList) {
			return;
		}
		mIsGetSearchList = true;
		mPullToRefreshListView.setVisibility(View.VISIBLE);
		new ActionProcessor().startAction(SearchActivity.this, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessageToHandler(GET_SEARCH_LIST_SUCCESS, status, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendMessageToHandler(GET_SEARCH_LIST_FAIL, status, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return RecommendReq.searchUser(mKeyword, mCurrentPage);
			}
		});
	}

	private void sendMessageToHandler(int what, int status, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.arg1 = status;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	private void follow(final String type, final String followuid) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				showErrorMsg(result);
				mPullToRefreshListView.setHeaderVisible(true);
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.userFollow(type, followuid);
			}
		});
	}

}

package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.xhr.framework.widget.pulltorefresh.PullToRefreshListView;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.MyTrendsListAdapter;
import com.xhr88.lp.adapter.TrendsListCommentAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.TrendReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.AttentionTrendsModel;
import com.xhr88.lp.model.datamodel.CommentModel;
import com.xhr88.lp.model.datamodel.TrendsListModel;
import com.xhr88.lp.util.ViewUtil;
import com.xhr88.lp.widget.MListView;

/**
 * 我的动态列表
 * 
 * @author zou.sq
 * @version 修改缺陷840，命名不规范导致
 */
public class MyTrendsListActivity extends TraineeBaseActivity implements OnClickListener, OnItemClickListener {

	private static final int REQUEST_CODE_ADD_TRENDS = 100;
	private static final int REQUEST_CODE_DELETE_TRENDS = 101;
	private static final int GET_TRENDS_LIST_FAIL = 0;
	private static final int GET_TRENDS_LIST_SUCCESS = 1;
	private static final int PULL_DOWN = 0;
	private static final int PULL_UP = 1;
	private List<TrendsListModel> mDynamicModels;
	private PullToRefreshListView mPullToRefreshListView;
	private int mCurrentPage;
	private MyTrendsListAdapter mTrendsListAdapter;
	private boolean mIsGetTrends;
	private LoadingUpView mLoadingUpView;
	private View mViewInput;
	private EditText mEdtComment;
	private View mEmptyView;

	public static final String TAG = "MyTrendsListActivity";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ActionResult result = (ActionResult) msg.obj;
			switch (msg.what) {
				case GET_TRENDS_LIST_SUCCESS:
					List<TrendsListModel> models = (List<TrendsListModel>) result.ResultObject;
					mPullToRefreshListView.onRefreshComplete();
					mCurrentPage++;
					mPullToRefreshListView.setMode(Mode.BOTH);
					if (PULL_DOWN == msg.arg1) {
						mDynamicModels.clear();
					}
					if (null != models && models.size() < ConstantSet.PAGE_SIZE) {
						mPullToRefreshListView.setNoMoreData();
					}
					showData(result, true);
					if (null != mDynamicModels && mDynamicModels.isEmpty()) {
						mPullToRefreshListView.setEmptyView(mEmptyView);
					}
					break;
				case GET_TRENDS_LIST_FAIL:
					if (mDynamicModels.isEmpty()) {
						// 当前列表无数据，网络异常并且本里无缓存数据，那么显示错误界面
						if (result.ResultCode != null && ActionResult.RESULT_CODE_NET_ERROR.equals(result.ResultCode)
								&& result.ResultObject != null
								&& !((List<TrendsListModel>) result.ResultObject).isEmpty()) {
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
			mIsGetTrends = false;
		}
	};

	protected void showData(ActionResult result, boolean isSuccess) {
		List<TrendsListModel> models = (List<TrendsListModel>) result.ResultObject;
		if (null != models) {
			mDynamicModels.addAll(models);
		}
		if (mTrendsListAdapter != null) {
			mTrendsListAdapter.notifyDataSetChanged();
		}
	}

	private void initVariables() {
		mDynamicModels = new ArrayList<TrendsListModel>();
		mTrendsListAdapter = new MyTrendsListAdapter(
				this, mDynamicModels, mImageLoader, this, new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						List<CommentModel> models = ((TrendsListCommentAdapter) parent.getAdapter()).getCommentList();
						CommentModel model = models.get(position);
						// CommentModel model = (CommentModel)
						// parent.getAdapter().getItem(position);
						if (null == model) {
							return;
						}
						CommentModel tempModel = new CommentModel();
						tempModel.setTid(model.getTid());
						if (!UserMgr.isMineUid(model.getUid())) {
							tempModel.setComuid(model.getUid());
						}
						mEdtComment.setTag(R.id.tag1, models);
						mEdtComment.setTag(R.id.tag2, parent.getAdapter());
						mEdtComment.setTag(R.id.tag3, model.getTid());
						mEdtComment.setTag(R.id.tag4, model.getUid());
						mEdtComment.setText("");
						mEdtComment.setHint("回复" + model.getNickname() + "：");
						mEdtComment.requestFocus();
						UIUtil.setViewVisible(mViewInput);
						ImeUtil.showSoftInput(MyTrendsListActivity.this);
					}
				});
		mLoadingUpView = new LoadingUpView(this, true);
	}

	private void initViews() {
		mEmptyView = ViewUtil.getEmptyView(this, getString(R.string.empty_info_my_trends_list));
		mViewInput = findViewById(R.id.rl_input_layout);
		mEdtComment = (EditText) findViewById(R.id.edt_add_comment);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_trends_list);
		ListView listView = mPullToRefreshListView.getRefreshableView();
		listView.setCacheColorHint(getResources().getColor(R.color.transparent));
		listView.setFadingEdgeLength(0);
		listView.setOnItemClickListener(this);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh() {
				EvtLog.d(TAG, "onPullDownToRefresh");
				mCurrentPage = 0;
				getTrendsList(PULL_DOWN);
			}

			@Override
			public void onPullUpToRefresh() {
				EvtLog.d(TAG, "onPullUpToRefresh");
				getTrendsList(PULL_UP);
			}
		});
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setHeaderVisible(true);
		mPullToRefreshListView.setIsShowHeaderFresh(true);
		mPullToRefreshListView.setMode(Mode.BOTH);
		listView.setAdapter(mTrendsListAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_trends_list);
		initVariables();
		initViews();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TrendsListModel model = (TrendsListModel) parent.getAdapter().getItem(position);
		if (null != model && !StringUtil.isNullOrEmpty(model.getTid())) {
			Intent intent = new Intent(MyTrendsListActivity.this, MyTrendsDetailActivity.class);
			intent.putExtra(ConstantSet.KEY_TID, model.getTid());
			startActivityForResult(intent, REQUEST_CODE_DELETE_TRENDS);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ibtn_add_trend:
				Intent intent = new Intent(MyTrendsListActivity.this, PublishTrendsActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ADD_TRENDS);
				break;
			case R.id.trend_with_back_title_btn_left:
				finish();
				break;
			case R.id.btn_dynamic_like:
				TrendsListModel model = (TrendsListModel) v.getTag();
				if (null != model && null != model.getTid() && 1 != model.getIsgood()) {
					trendsLike(model);
				}
				break;
			case R.id.btn_dynamic_comment:
				TrendsListModel dynamicModel = (TrendsListModel) v.getTag(R.id.tag1);
				if (null != dynamicModel && null != dynamicModel.getTid()) {
					CommentModel commentModel = new CommentModel();
					commentModel.setTid(dynamicModel.getTid());
					mViewInput.setVisibility(View.VISIBLE);
					// mEdtComment.setTag(commentModel);
					mEdtComment.setTag(R.id.tag1, dynamicModel.getCommentlist());
					mEdtComment.setTag(R.id.tag2, v.getTag(R.id.tag2));
					mEdtComment.setTag(R.id.tag3, dynamicModel.getTid());
					mEdtComment.setTag(R.id.tag4, null);
					mEdtComment.requestFocus();
					ImeUtil.showSoftInput(this);
				}
				break;
			case R.id.btn_send_comment:
				// CommentModel commentModel1 = (CommentModel)
				// mEdtComment.getTag();
				List<CommentModel> commentModels = (List<CommentModel>) mEdtComment.getTag(R.id.tag1);
				BaseAdapter adapter = (BaseAdapter) mEdtComment.getTag(R.id.tag2);
				String tId = (String) mEdtComment.getTag(R.id.tag3);
				String comuId = (String) mEdtComment.getTag(R.id.tag4);

				String comment = mEdtComment.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(comment)) {
					toast("评论不能为空");
					return;
				}
				addComment(tId, comment, comuId, commentModels, adapter);
				break;

			default:
				break;
		}
	}

	private void addComment(final String tid, final String content, final String comuid,
			final List<CommentModel> commentModels, final BaseAdapter adapter) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				toast("评论成功");
				ImeUtil.hideSoftInput(MyTrendsListActivity.this, mEdtComment);
				mEdtComment.setText("");
				mViewInput.setVisibility(View.GONE);
				commentModels.add((CommentModel) result.ResultObject);
				adapter.notifyDataSetChanged();
				// mPullToRefreshListView.setHeaderVisible(true);
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.commentAdd(tid, content, comuid);
			}
		});
	}

	private void trendsLike(final TrendsListModel tempModel) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				toast("喜欢成功");
				for (int i = 0; i < mDynamicModels.size(); i++) {
					TrendsListModel model = mDynamicModels.get(i);
					if (tempModel.getTid().equals(model.getTid())) {
						model.setIsgood(1);
						mTrendsListAdapter.notifyDataSetChanged();
						return;
					}
				}
			}

			@Override
			public void onError(ActionResult result) {
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.trendsLike(tempModel.getTid(), "1");
			}
		});
	}

	private void getTrendsList(final int status) {
		if (mIsGetTrends) {
			return;
		}
		mIsGetTrends = true;
		UIUtil.setViewVisible(mPullToRefreshListView);
		new ActionProcessor().startAction(this, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendMessageToHandler(GET_TRENDS_LIST_SUCCESS, status, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendMessageToHandler(GET_TRENDS_LIST_FAIL, status, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.getMyTrendsList(mCurrentPage);

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_ADD_TRENDS || requestCode == REQUEST_CODE_DELETE_TRENDS) {
			mPullToRefreshListView.setHeaderVisible(true);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		if (View.VISIBLE == mViewInput.getVisibility()) {
			UIUtil.setViewGone(mViewInput);
			return;
		} else {
			finish();
		}
		super.onBackPressed();
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

}

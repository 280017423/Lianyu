package com.xhr88.lp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.ImeUtil;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr.framework.widget.RoundCornerImageView;
import com.xhr.framework.widget.pulltorefresh.XScrollView;
import com.xhr.framework.widget.pulltorefresh.XScrollView.IXScrollViewListener;
import com.xhr88.lp.R;
import com.xhr88.lp.adapter.OtherTrendsListAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.TrendReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.OtherSubTrendsModel;
import com.xhr88.lp.model.datamodel.OtherTrendsModel;
import com.xhr88.lp.widget.MListView;

public class OtherTrendsListActivity extends TraineeBaseActivity implements OnClickListener, OnItemClickListener {

	private String mTouid;
	private int mCurrentPage;
	private LoadingUpView mLoadingUpView;
	private ArrayList<OtherSubTrendsModel> mTrendsModels;
	private OtherTrendsListAdapter mTrendsListAdapter;
	private View mViewInput;
	private ImageView mIvBg;
	private RoundCornerImageView mIvHead;
	private TextView mTvNickname;
	private MListView mLvTrends;
	private EditText mEdtComment;
	private XScrollView mSvNomal;
	private ImageView mIvEmpty;

	public static final String TAG = "TrendsListActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_trends_list);
		initVariables();
		initViews();
		getOtherTrendsList();
	}

	private void initVariables() {
		mTouid = getIntent().getStringExtra("touid");
		if (StringUtil.isNullOrEmpty(mTouid)) {
			finish();
		}
		mTrendsModels = new ArrayList<OtherSubTrendsModel>();
		mTrendsListAdapter = new OtherTrendsListAdapter(this, mTrendsModels, mImageLoader, this);
		mLoadingUpView = new LoadingUpView(this, true);
	}

	private void initViews() {
		initScrollView();
		View header = View.inflate(this, R.layout.activity_other_trends_list_header, null);
		mIvBg = (ImageView) header.findViewById(R.id.iv_personal_bg);
		mIvEmpty = (ImageView) header.findViewById(R.id.iv_empty_view);
		UIUtil.setViewHeight(mIvBg, UIUtil.getScreenWidth(this));
		mIvHead = (RoundCornerImageView) header.findViewById(R.id.iv_personal_head_img);
		mTvNickname = (TextView) header.findViewById(R.id.tv_nickname);
		mLvTrends = (MListView) header.findViewById(R.id.other_trends_list);

		mViewInput = findViewById(R.id.rl_input_layout);
		mEdtComment = (EditText) findViewById(R.id.edt_add_comment);
		mLvTrends.setCacheColorHint(getResources().getColor(R.color.transparent));
		mLvTrends.setFadingEdgeLength(0);
		mLvTrends.setOnItemClickListener(this);
		mLvTrends.setAdapter(mTrendsListAdapter);
		mSvNomal.setView(header);
	}

	private void initScrollView() {
		mSvNomal = (XScrollView) findViewById(R.id.sl_normal_view);
		mSvNomal.setHeaderDivider(getResources().getColor(R.color.transparent));
		mSvNomal.setPullRefreshEnable(false);
		mSvNomal.setPullLoadEnable(true);
		mSvNomal.setAutoLoadEnable(false);
		mSvNomal.setIXScrollViewListener(new IXScrollViewListener() {

			@Override
			public void onRefresh() {
				// 不做任何事情
			}

			@Override
			public void onLoadMore() {
				getOtherTrendsList();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		OtherSubTrendsModel model = (OtherSubTrendsModel) parent.getAdapter().getItem(position);
		if (null == model || StringUtil.isNullOrEmpty(model.getTid())) {
			return;
		}
		Intent intent = new Intent(OtherTrendsListActivity.this, TrendsDetailActivity.class);
		intent.putExtra(ConstantSet.KEY_TID, model.getTid());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_personal_head_img:
			case R.id.trend_with_back_title_btn_left:
				finish();
				break;
			case R.id.btn_dynamic_like:
				OtherSubTrendsModel model = (OtherSubTrendsModel) v.getTag();
				if (null != model && null != model.getTid() && 1 != model.getIsgood()) {
					trendsLike(model);
				}
				break;
			case R.id.btn_dynamic_comment:
				OtherSubTrendsModel dynamicModel = (OtherSubTrendsModel) v.getTag();
				if (null != dynamicModel && null != dynamicModel.getTid()) {
					mViewInput.setVisibility(View.VISIBLE);
					mEdtComment.setTag(dynamicModel);
					mEdtComment.requestFocus();
					ImeUtil.showSoftInput(this);
				}
				break;
			case R.id.btn_send_comment:
				OtherSubTrendsModel commentModel = (OtherSubTrendsModel) mEdtComment.getTag();
				String comment = mEdtComment.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(comment)) {
					toast("评论不能为空");
					return;
				}
				if (null != commentModel && null != commentModel.getTid()) {
					addComment(commentModel.getTid(), comment);
				}
				break;

			default:
				break;
		}
	}

	private void addComment(final String tid, final String content) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				toast("评论成功");
				ImeUtil.hideSoftInput(OtherTrendsListActivity.this, mEdtComment);
				mEdtComment.setText("");
				mViewInput.setVisibility(View.GONE);
				mCurrentPage = 0;
				getOtherTrendsList();
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.commentAdd(tid, content, "");
			}
		});
	}

	private void trendsLike(final OtherSubTrendsModel model) {
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				toast("喜欢成功");
				for (int i = 0; i < mTrendsModels.size(); i++) {
					OtherSubTrendsModel tempModel = mTrendsModels.get(i);
					if (tempModel.getTid().equals(model.getTid())) {
						tempModel.setIsgood(1);
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
				return TrendReq.trendsLike(model.getTid(), "1");
			}
		});
	}

	private void getOtherTrendsList() {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null == result) {
					return;
				}
				showData((OtherTrendsModel) result.ResultObject);
				onFinishLoad();
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return TrendReq.getOtherTrendsList(mTouid, mCurrentPage);
			}
		});
	}

	private void onFinishLoad() {
		new CountDownTimer(20, 200) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				mSvNomal.scrollTo(0, 0);
				EvtLog.d("aaa", "" + mIvBg.getBottom());
				EvtLog.d("aaa", "" + mLvTrends.getBottom());
				EvtLog.d("aaa", "" + mIvEmpty.getTop());
				// 重新计算高度，要是没有填充一屏的就填充满屏幕
				UIUtil.setViewHeight(
						mIvEmpty,
						UIUtil.getScreenHeight(OtherTrendsListActivity.this) - mLvTrends.getBottom()
								- UIUtil.dip2px(OtherTrendsListActivity.this, 45 * 2));
			}
		}.start();
		mSvNomal.stopRefresh();
		mSvNomal.stopLoadMore();
	}

	private void showData(OtherTrendsModel model) {
		if (null == model) {
			return;
		}
		if (0 == mCurrentPage) {
			int imageRes = R.drawable.default_man_head_bg;
			int imageBgRes = R.drawable.default_man_bg;
			if (1 != model.getSex()) {
				imageRes = R.drawable.default_woman_head_bg;
				imageBgRes = R.drawable.default_man_bg;
			}
			mTrendsModels.clear();
			mImageLoader.displayImage(model.getBackground(), mIvBg, new DisplayImageOptions.Builder().cacheInMemory()
					.cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageBgRes).build());
			mImageLoader.displayImage(model.getHeadimg(), mIvHead, new DisplayImageOptions.Builder().cacheInMemory()
					.cacheOnDisc().displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(imageRes).build());
			mTvNickname.setText(model.getNickname());
		}
		mCurrentPage++;
		List<OtherSubTrendsModel> models = model.getList();
		if (null != models) {
			if (models.size() < ConstantSet.PAGE_SIZE) {
				mSvNomal.setNoMoreData(false);
			} else {
				mSvNomal.setNoMoreData(true);
			}
			mTrendsModels.addAll(models);
			mTrendsListAdapter.notifyDataSetChanged();
		}
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

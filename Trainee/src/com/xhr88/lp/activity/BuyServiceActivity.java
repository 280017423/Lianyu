package com.xhr88.lp.activity;

import io.rong.imkit.RongIM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.activity.MainActivity.TabHomeIndex;
import com.xhr88.lp.adapter.BuyServiceAdapter;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.LittleShopReq;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.model.datamodel.BuyServiceModel;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.widget.MListView;

/**
 * 购买服务列表界面
 * 
 * @author zou.sq
 */
public class BuyServiceActivity extends TraineeBaseActivity implements OnClickListener {

	private static final int DIALOG_RECHARGE = 1;
	private List<BuyServiceModel> mBuyListModels;
	private BuyServiceAdapter mBuyListAdapter;
	private MListView mBuyListView;
	private TextView mTvTitle;
	private String mUid;
	private String mNickName;
	private LoadingUpView mLoadingUpView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_service);
		initVariables();
		initViews();
		getBuyList();
	}

	private void initVariables() {
		mUid = getIntent().getStringExtra("touid");
		mNickName = getIntent().getStringExtra("nickname");
		if (StringUtil.isNullOrEmpty(mUid) || StringUtil.isNullOrEmpty(mNickName)) {
			finish();
		}
		mLoadingUpView = new LoadingUpView(this, true);
		mBuyListModels = new ArrayList<BuyServiceModel>();
		mBuyListAdapter = new BuyServiceAdapter(this, mBuyListModels, mImageLoader, this);
	}

	private void initViews() {
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		mBuyListView = (MListView) findViewById(R.id.lv_buy_service_list);
		mBuyListView.setAdapter(mBuyListAdapter);
		mTvTitle.setText("约 " + mNickName);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.title_with_back_title_btn_left:
				finish();
				break;
			case R.id.btn_buy:
				BuyServiceModel model = (BuyServiceModel) view.getTag();
				if (null != model) {
					showBuyPop(model);
				}
				break;
			default:
				break;
		}
	}

	private void showBuyPop(final BuyServiceModel model) {
		final View popView = View.inflate(this, R.layout.view_buy_pop, null);
		final PopWindowUtil popManager = new PopWindowUtil(popView, null);
		final ImageView serviceLogo = (ImageView) popView.findViewById(R.id.iv_experience);
		final TextView serviceCount = (TextView) popView.findViewById(R.id.tv_service_count);
		final TextView servicePrice = (TextView) popView.findViewById(R.id.tv_price);
		final TextView serviceExplain = (TextView) popView.findViewById(R.id.tv_service_explain);
		final TextView buyCount = (TextView) popView.findViewById(R.id.tv_buy_count);
		final Button btnSub = (Button) popView.findViewById(R.id.btn_subtract_service);
		final Button btnAdd = (Button) popView.findViewById(R.id.btn_add_service);
		final Button btnPay = (Button) popView.findViewById(R.id.btn_pay);
		final OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = Integer.parseInt(buyCount.getText().toString());
				switch (v.getId()) {
					case R.id.btn_subtract_service:
						if (1 < count) {
							buyCount.setText((count - 1) + "");
							serviceCount.setText((count - 1) * model.getLength() + model.getUnit());
							servicePrice.setText((count - 1) * model.getCoin() + "");
						}
						break;
					case R.id.btn_add_service:
						buyCount.setText((count + 1) + "");
						serviceCount.setText((count + 1) * model.getLength() + model.getUnit());
						servicePrice.setText((count + 1) * model.getCoin() + "");
						break;
					case R.id.btn_pay:
						buyService(model.getSid(), buyCount.getText().toString(), popManager);
						break;

					default:
						break;
				}
			}
		};
		mImageLoader.displayImage(model.getLogo(), serviceLogo);
		serviceCount.setText(model.getLength() + model.getUnit());
		servicePrice.setText(model.getCoin() + "");
		serviceExplain.setText(model.getExplain());
		buyCount.setText("1");
		btnSub.setOnClickListener(listener);
		btnAdd.setOnClickListener(listener);
		btnPay.setOnClickListener(listener);
		popManager.show(Gravity.BOTTOM);
	}

	private void getBuyList() {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(BuyServiceActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null == result) {
					return;
				}
				List<BuyServiceModel> models = (List<BuyServiceModel>) result.ResultObject;
				if (null != models) {
					mBuyListModels.clear();
					mBuyListModels.addAll(models);
					mBuyListAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return LittleShopReq.getBuyService(mUid);
			}
		});
	}

	private void buyService(final String sid, final String num, final PopWindowUtil popManager) {
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(BuyServiceActivity.this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				if (null != popManager) {
					popManager.dismiss();
				}
				toast("支付成功");
				analytics(num);
				if (!StringUtil.isNullOrEmpty(mUid) && !StringUtil.isNullOrEmpty(mNickName)) {
					MainActivity.INSTANCE.setTabSelection(TabHomeIndex.HOME_MESSAGE, false);
					Intent intent = new Intent(BuyServiceActivity.this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					RongIM.getInstance().startPrivateChat(BuyServiceActivity.this, mUid, mNickName);
				}
			}

			@Override
			public void onError(ActionResult result) {
				dismissLoadingUpView(mLoadingUpView);
				showErrorMsg(result);
				if (null != result && "5".equals(result.ResultCode)) {
					showDialog(DIALOG_RECHARGE);
				}
			}

			@Override
			public ActionResult onAsyncRun() {
				return LittleShopReq.buyService(mUid, sid, num);
			}
		});
	}

	private void analytics(String num) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("购买数量", num);
		MobclickAgent.onEvent(this, "下单统计", map);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (DIALOG_RECHARGE == id) {
			return createDialogBuilder(this, getString(R.string.button_text_tips), "您当前的恋爱币不足，是否充值？",
					getString(R.string.button_text_no), "确定").create(id);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_RECHARGE:
				MainActivity.INSTANCE.setTabSelection(TabHomeIndex.HOME_MY_CENTER, false);
				Intent intent = new Intent(BuyServiceActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				startActivity(new Intent(BuyServiceActivity.this, MyAssetsActivity.class));
				break;
			default:
				break;
		}
	}

}

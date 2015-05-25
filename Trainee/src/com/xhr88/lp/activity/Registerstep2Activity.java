package com.xhr88.lp.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xhr.framework.util.StringUtil;
import com.xhr.framework.widget.LoadingUpView;
import com.xhr88.lp.R;
import com.xhr88.lp.authentication.ActionProcessor;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.request.UserReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.IActionListener;
import com.xhr88.lp.listener.OnPopDismissListener;
import com.xhr88.lp.util.DateUtil;
import com.xhr88.lp.util.PopWindowUtil;
import com.xhr88.lp.widget.wheelview.OnWheelChangedListener;
import com.xhr88.lp.widget.wheelview.WheelView;
import com.xhr88.lp.widget.wheelview.adapters.ArrayWheelAdapter;

public class Registerstep2Activity extends TraineeBaseActivity implements OnClickListener {
	private TextView mTvXingZuo;
	private TextView mTvBirth;
	private TextView mTvAge;
	private LoadingUpView mLoadingUpView;
	public static final int REGISTER_USER_SUCCESS = 1;
	public static final int REGISTER_USERNEME_FAIL = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_step2);
		initVariables();
		initViews();
	}

	private void initVariables() {
		mLoadingUpView = new LoadingUpView(this);
	}

	private void initViews() {
		mTvBirth = (TextView) findViewById(R.id.tv_birth);
		mTvAge = (TextView) findViewById(R.id.tv_age);
		mTvXingZuo = (TextView) findViewById(R.id.tv_xingzuo);
	}

	private void sendHandler(int what, ActionResult result) {
		Message msg = mHandler.obtainMessage(what);
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dismissLoadingUpView(mLoadingUpView);
			super.handleMessage(msg);
			switch (msg.what) {
				case REGISTER_USER_SUCCESS:
					startActivity(new Intent(Registerstep2Activity.this, MainActivity.class));
					sendBroadcast(new Intent(ConstantSet.ACTION_LOGIN_SUCCESS));
					finish();
					break;
				case REGISTER_USERNEME_FAIL:
					ActionResult result = (ActionResult) msg.obj;
					showErrorMsg(result);
					break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.register2_ll_title_with_back_title_btn_left:
				finish();
				break;
			case R.id.register2_ll_title_with_back_title_btn_right:
				final String birth = mTvBirth.getText().toString();
				if (StringUtil.isNullOrEmpty(birth)) {
					toast("请选择生日");
					return;
				}
				showLoadingUpView(mLoadingUpView);
				userEdit();
				break;
			case R.id.ll_birth:
				String[] mYmd = mTvBirth.getText().toString().trim().split("-");
				if (null == mYmd || mYmd.length < 3) {
					initWheelView(1990, 1, 1);
				} else {
					initWheelView(Integer.parseInt(mYmd[0]), Integer.parseInt(mYmd[1]), Integer.parseInt(mYmd[2]));
				}
				break;
			default:
				break;
		}
	}

	private void userEdit() {
		final String nickName = Registerstep2Activity.this.getIntent().getStringExtra("nickName");
		final String sex = Registerstep2Activity.this.getIntent().getStringExtra("sex");
		final String birth = mTvBirth.getText().toString().trim();
		showLoadingUpView(mLoadingUpView);
		new ActionProcessor().startAction(this, true, true, new IActionListener() {

			@Override
			public void onSuccess(ActionResult result) {
				sendHandler(REGISTER_USER_SUCCESS, result);
			}

			@Override
			public void onError(ActionResult result) {
				sendHandler(REGISTER_USERNEME_FAIL, result);
			}

			@Override
			public ActionResult onAsyncRun() {
				return UserReq.userEdit(nickName, sex, birth, "");
			}
		});
	}

	private void initWheelView(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		int endYear = cal.get(Calendar.YEAR) - 1;
		final String[] years = new String[endYear - 1900 + 1];
		for (int i = 1900; i <= endYear; i++) {
			years[i - 1900] = i + "年";
		}
		String[] months = new String[12];
		for (int i = 1; i <= 12; i++) {
			months[i - 1] = i + "月";
		}
		final View popView = View.inflate(this, R.layout.view_birth_picker, null);
		final WheelView wvYear = (WheelView) popView.findViewById(R.id.wv_year_picker);
		final WheelView wvMonth = (WheelView) popView.findViewById(R.id.wv_month_picker);
		final WheelView wvDay = (WheelView) popView.findViewById(R.id.wv_day_picker);
		final PopWindowUtil popManager = new PopWindowUtil(popView, new OnPopDismissListener() {

			@Override
			public void onDismiss() {
				try {
					String date = (wvYear.getCurrentItem() + 1900) + "-" + (wvMonth.getCurrentItem() + 1) + "-"
							+ (wvDay.getCurrentItem() + 1);
					String[] mYmd = date.split("-");
					SimpleDateFormat fromFormatter = new SimpleDateFormat(
							com.xhr.framework.util.DateUtil.DEFAULT_DATE_FORMAT, Locale.getDefault());
					Date birthDay = fromFormatter.parse(date);
					mTvBirth.setText(date);
					mTvAge.setText(DateUtil.getAgeByDate(birthDay) + "岁");
					mTvXingZuo.setText(DateUtil.getAstro(Integer.parseInt(mYmd[1]), Integer.parseInt(mYmd[2])));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		OnWheelChangedListener listener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				switch (wheel.getId()) {
					case R.id.wv_year_picker:
						refreashDayWheelView(wvDay, wvYear.getCurrentItem(), wvMonth.getCurrentItem(),
								wvDay.getCurrentItem());
						break;
					case R.id.wv_month_picker:
						refreashDayWheelView(wvDay, wvYear.getCurrentItem(), wvMonth.getCurrentItem(),
								wvDay.getCurrentItem());
						break;
					case R.id.wv_day_picker:

						break;

					default:
						break;
				}
			}
		};
		wvYear.addChangingListener(listener);
		wvMonth.addChangingListener(listener);
		wvDay.addChangingListener(listener);

		ArrayWheelAdapter<String> mYearAdapter = new ArrayWheelAdapter<String>(this, years);
		mYearAdapter.setItemResource(R.layout.view_wheel_text_item);
		mYearAdapter.setItemTextResource(R.id.tv_time_item);

		ArrayWheelAdapter<String> mMonthAdapter = new ArrayWheelAdapter<String>(this, months);
		mMonthAdapter.setItemResource(R.layout.view_wheel_text_item);
		mMonthAdapter.setItemTextResource(R.id.tv_time_item);

		wvYear.setViewAdapter(mYearAdapter);
		wvMonth.setViewAdapter(mMonthAdapter);
		wvYear.setVisibleItems(5);
		wvMonth.setVisibleItems(5);
		wvYear.setCyclic(true);
		wvMonth.setCyclic(true);
		wvYear.setCurrentItem(year - 1900);
		wvMonth.setCurrentItem(month - 1);

		refreashDayWheelView(wvDay, wvYear.getCurrentItem(), wvMonth.getCurrentItem(), day - 1);

		popManager.show(Gravity.BOTTOM);
	}

	private void refreashDayWheelView(WheelView wvDay, int year, int month, int current) {
		int dayNum = DateUtil.getDayNum(year, month);
		String[] days = new String[dayNum];
		for (int i = 1; i <= dayNum; i++) {
			days[i - 1] = i + "日";
		}
		if (current >= days.length) {
			current = days.length - 1;
		}
		ArrayWheelAdapter<String> mDayAdapter = new ArrayWheelAdapter<String>(this, days);
		mDayAdapter.setItemResource(R.layout.view_wheel_text_item);
		mDayAdapter.setItemTextResource(R.id.tv_time_item);
		wvDay.setVisibleItems(5);
		wvDay.setCyclic(true);
		wvDay.setCurrentItem(current);
		wvDay.setViewAdapter(mDayAdapter);
	}

}

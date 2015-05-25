package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.BuyServiceModel;

/**
 * 购买服务列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class BuyServiceAdapter extends BaseAdapter {
	private List<BuyServiceModel> mServiceListModels;
	private Context mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private OnClickListener mOnClickListener;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param loader
	 *            图片加载器
	 * @param dataList
	 *            数据列表
	 * @param listener
	 *            按钮点击监听
	 */
	public BuyServiceAdapter(Activity activity, List<BuyServiceModel> dataList, ImageLoader loader,
			OnClickListener listener) {
		this.mContext = activity;
		this.mServiceListModels = dataList;
		this.mImageLoader = loader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).build();
		mOnClickListener = listener;
	}

	@Override
	public int getCount() {
		if (mServiceListModels != null && !mServiceListModels.isEmpty()) {
			return mServiceListModels.size();
		}
		return 0;
	}

	@Override
	public BuyServiceModel getItem(int position) {
		if (mServiceListModels != null) {
			return mServiceListModels.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHode holder = new viewHode();
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.view_buy_service_item, null);

			holder.mIvServiceLogo = (ImageView) convertView.findViewById(R.id.iv_experience);
			holder.mTvServiceTitle = (TextView) convertView.findViewById(R.id.tv_experience_title);
			holder.mTvContent = (TextView) convertView.findViewById(R.id.tv_experience_content);
			holder.mTvDetail = (TextView) convertView.findViewById(R.id.tv_detail);
			holder.mTvServicePrice = (TextView) convertView.findViewById(R.id.tv_experience_price);
			holder.mBtnBuy = (Button) convertView.findViewById(R.id.btn_buy);
			holder.mBtnShowAll = (Button) convertView.findViewById(R.id.btn_show_all);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final BuyServiceModel model = getItem(position);
		holder.mBtnBuy.setTag(model);
		holder.mBtnBuy.setOnClickListener(mOnClickListener);
		holder.mTvServiceTitle.setText(model.getTitle());
		holder.mTvContent.setText(model.getExplain());
		holder.mTvDetail.setText(model.getInfo());
		holder.mTvServicePrice.setText(model.getCoin() + " / " + model.getLength() + model.getUnit());
		mImageLoader.displayImage(model.getLogo(), holder.mIvServiceLogo, mOptions);
		if (StringUtil.isNullOrEmpty(model.getInfo())) {
			UIUtil.setViewGone(holder.mBtnShowAll);
		} else {
			UIUtil.setViewVisible(holder.mBtnShowAll);
		}
		if (model.isShow()) {
			UIUtil.setViewVisible(holder.mTvDetail);
			holder.mBtnShowAll.setBackgroundResource(R.drawable.buy_service_show_all_2);
		} else {
			holder.mBtnShowAll.setBackgroundResource(R.drawable.buy_service_show_all_1);
			UIUtil.setViewGone(holder.mTvDetail);
		}
		holder.mBtnShowAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				model.setShow(!model.isShow());
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	class viewHode {
		ImageView mIvServiceLogo;
		TextView mTvServicePrice;
		TextView mTvContent;
		TextView mTvDetail;
		TextView mTvServiceTitle;
		Button mBtnBuy;
		Button mBtnShowAll;
	}
}
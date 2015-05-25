package com.xhr88.lp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.imageloader.core.DisplayImageOptions;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.display.SimpleBitmapDisplayer;
import com.xhr.framework.util.StringUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr.framework.widget.SlipButton;
import com.xhr.framework.widget.SlipButton.OnChangedListener;
import com.xhr88.lp.R;
import com.xhr88.lp.model.datamodel.ShopServiceModel;

/**
 * 服务列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class ShopServiceAdapter extends BaseAdapter {
	private List<ShopServiceModel> mServiceListModels;
	private Context mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private OnChangedListener mOnChangedListener;

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
	public ShopServiceAdapter(Activity activity, List<ShopServiceModel> dataList, ImageLoader loader,
			OnChangedListener onChangedListener) {
		this.mContext = activity;
		this.mServiceListModels = dataList;
		this.mImageLoader = loader;
		mOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer()).showImageForEmptyUri(R.drawable.default_man_head_bg).build();
		mOnChangedListener = onChangedListener;
	}

	@Override
	public int getCount() {
		if (mServiceListModels != null && !mServiceListModels.isEmpty()) {
			return mServiceListModels.size();
		}
		return 0;
	}

	@Override
	public ShopServiceModel getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.view_shop_service_item, null);

			holder.mTvContent = (TextView) convertView.findViewById(R.id.tv_experience_content);
			holder.mIvprice = (ImageView) convertView.findViewById(R.id.iv_experience_price);
			holder.mTvServicePrice = (TextView) convertView.findViewById(R.id.tv_experience_price);
			holder.mTvServiceTitle = (TextView) convertView.findViewById(R.id.tv_experience_title);
			holder.mIvServiceLogo = (ImageView) convertView.findViewById(R.id.iv_experience);
			holder.mTvDetail = (TextView) convertView.findViewById(R.id.tv_detail);
			holder.mBtnShowAll = (Button) convertView.findViewById(R.id.btn_show_all);
			holder.mSbtnStartService = (SlipButton) convertView.findViewById(R.id.sbtn_start_experience);
			convertView.setTag(holder);
		} else {
			holder = (viewHode) convertView.getTag();
		}

		final ShopServiceModel model = getItem(position);
		holder.mTvContent.setText(model.getExplain());
		holder.mTvServiceTitle.setText(model.getTitle());
		holder.mSbtnStartService.setTag(model);
		holder.mSbtnStartService.setOnChangedListener(mOnChangedListener);
		holder.mTvContent.setTextColor(Color.parseColor("#bfbfbf"));
		holder.mTvDetail.setText(model.getInfo());
		if (1 == model.getOpen()) {
			mImageLoader.displayImage(model.getLogo(), holder.mIvServiceLogo, mOptions);
			holder.mIvprice.setImageResource(R.drawable.icon_service_price);
			holder.mSbtnStartService.setCheck(true);
			holder.mTvServicePrice.setTextColor(Color.parseColor("#b596cb"));
			holder.mTvServiceTitle.setTextColor(Color.parseColor("#666666"));
		} else {
			mImageLoader.displayImage(model.getCloselogo(), holder.mIvServiceLogo, mOptions);
			holder.mSbtnStartService.setCheck(false);
			holder.mIvprice.setImageResource(R.drawable.icon_service_price_disable);
			holder.mTvServicePrice.setTextColor(Color.parseColor("#bfbfbf"));
			holder.mTvServiceTitle.setTextColor(Color.parseColor("#bfbfbf"));
		}
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
		holder.mTvServicePrice.setText(model.getCoin() + "（" + model.getUnit() + "）");

		return convertView;
	}

	class viewHode {
		ImageView mIvServiceLogo;
		ImageView mIvprice;
		TextView mTvServicePrice;
		TextView mTvContent;
		TextView mTvServiceTitle;
		SlipButton mSbtnStartService;
		TextView mTvDetail;
		Button mBtnShowAll;
	}
}
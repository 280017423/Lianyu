package com.xhr88.lp.app;

import io.rong.imkit.RongIM;
import io.rong.imlib.AnnotationNotFoundException;

import com.tencent.bugly.crashreport.CrashReport;
import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.xhr.framework.imageloader.core.ImageLoader;
import com.xhr.framework.imageloader.core.ImageLoaderConfiguration;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.listener.RongCloudEvent;
import com.xhr88.lp.model.viewmodel.UserViewModel;
import com.xhr88.lp.util.DBUtil;
import com.xhr88.lp.util.LpMessage;

/**
 * 全局应用程序
 * 
 */
public class TraineeApplication extends XhrApplicationBase {
	public static final int THREAD_POOL_SIZE = 3;
	public static final int MEMORY_CACHE_SIZE = 1500000;

	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = this;
		String appId = "900003418"; // 上Bugly(bugly.qq.com)注册产品获取的AppId
		boolean isDebug = false; // true代表App处于调试阶段，false代表App发布阶段
		CrashReport.initCrashReport(this, appId, isDebug); // 初始化SDK
		initImageLoader();
		// 初始化融云。
		RongIM.init(this);
		/**
		 * 融云SDK事件监听处理
		 */
		RongCloudEvent.init(this);

		try {
			// 注册自定义消息类型
			RongIM.registerMessageType(LpMessage.class);
		} catch (AnnotationNotFoundException e) {
			e.printStackTrace();
		}
		// 打开数据库
		new Thread(new Runnable() {
			@Override
			public void run() {
				DBUtil.getDataManager().firstOpen();

			}
		}).start();
	}

	@Override
	protected void setAppSign() {
		APP_SIGN = ConstantSet.APP_SIGN;
	}

	@Override
	protected void setClientType() {

	}

	/**
	 * This configuration tuning is custom. You can tune every option, you may
	 * tune some of them, or you can create default configuration by
	 * ImageLoaderConfiguration.createDefault(this); method.
	 * 
	 * @Name initImageLoader
	 * @Description 初始化图片加载器
	 * @return void
	 * @Author Administrator
	 * @Date 2014-3-21 上午11:42:06
	 * 
	 */
	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.threadPoolSize(THREAD_POOL_SIZE).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(MEMORY_CACHE_SIZE).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// .enableLogging() // Not necessary in common
				.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public String getToken() {
		if (UserDao.hasUserInfo()) {
			UserViewModel mUserViewModel = UserDao.getLocalUserInfo();
			return mUserViewModel.UserInfo.getToken();
		}
		return "";
	}

	@Override
	public String getUid() {
		if (UserDao.hasUserInfo()) {
			UserViewModel mUserViewModel = UserDao.getLocalUserInfo();
			return String.valueOf(mUserViewModel.UserInfo.getUid());
		}
		return "";
	}

}

package com.xhr88.lp.util;

import android.content.Context;

import com.xhr.framework.app.XhrApplicationBase;
import com.xhr.framework.orm.DataManager;
import com.xhr.framework.orm.DatabaseBuilder;
import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.PackageUtil;
import com.xhr88.lp.business.dao.DBMgr;
import com.xhr88.lp.model.datamodel.AttentionModel;
import com.xhr88.lp.model.datamodel.AttentionTrendsModel;
import com.xhr88.lp.model.datamodel.ConfigModel;
import com.xhr88.lp.model.datamodel.ConversationUserInfoModel;
import com.xhr88.lp.model.datamodel.FansModel;
import com.xhr88.lp.model.datamodel.HelpListModel;
import com.xhr88.lp.model.datamodel.HistoryServiceModel;
import com.xhr88.lp.model.datamodel.OtherTrendsModel;
import com.xhr88.lp.model.datamodel.RecommendListModel;
import com.xhr88.lp.model.datamodel.TrendsListModel;
import com.xhr88.lp.model.datamodel.UserCategoryModel;

// 115.28.28.253:8080/qian-jiang-tec-company/jyt.git

/**
 * 数据库初始化类
 * 
 * @author Wang.xy
 * @since 2013-03-28 zeng.ww 添加SpecialDishModel表初始化操作
 */
public class DBUtil {
	private static final String TAG = "pmr.DBUtil";

	private static DatabaseBuilder DATABASE_BUILDER;
	private static PMRDataManager INSTANCE;

	// 获取数据库实例
	static {
		EvtLog.d(TAG, "DBUtil static, pmr");

		if (DATABASE_BUILDER == null) {
			DATABASE_BUILDER = new DatabaseBuilder(PackageUtil.getConfigString("db_name"));
			DATABASE_BUILDER.addClass(RecommendListModel.class);
			DATABASE_BUILDER.addClass(UserCategoryModel.class);
			DATABASE_BUILDER.addClass(HelpListModel.class);
			DATABASE_BUILDER.addClass(TrendsListModel.class);
			DATABASE_BUILDER.addClass(AttentionModel.class);
			DATABASE_BUILDER.addClass(FansModel.class);
			DATABASE_BUILDER.addClass(HistoryServiceModel.class);
			DATABASE_BUILDER.addClass(AttentionTrendsModel.class);
			DATABASE_BUILDER.addClass(OtherTrendsModel.class);
			DATABASE_BUILDER.addClass(ConfigModel.class);
			DATABASE_BUILDER.addClass(ConversationUserInfoModel.class);
		}
	}

	/**
	 * 清除所有的数据表
	 */
	public static void clearAllTables() {
		if (null != DATABASE_BUILDER) {
			String[] tables = DATABASE_BUILDER.getTables();
			for (int i = 0; i < tables.length; i++) {
				DBMgr.deleteTableFromDb(tables[i]);
			}
		}
	}

	/**
	 * 
	 * @return 数据库管理器
	 */
	public static DataManager getDataManager() {
		EvtLog.d(TAG, "PMRDataManager getDataManager, pmr");
		if (INSTANCE == null) {
			INSTANCE = new PMRDataManager(XhrApplicationBase.CONTEXT, DATABASE_BUILDER);
		}
		return INSTANCE;
	}

	static class PMRDataManager extends DataManager {
		protected PMRDataManager(Context context, DatabaseBuilder databaseBuilder) {
			super(context, PackageUtil.getConfigString("db_name"), PackageUtil.getConfigInt("db_version"), databaseBuilder);
			EvtLog.d(TAG, "PMRDataManager, pmr");
		}
	}
}

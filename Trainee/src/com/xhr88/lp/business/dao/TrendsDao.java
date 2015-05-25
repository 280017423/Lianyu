package com.xhr88.lp.business.dao;

import com.xhr.framework.orm.DataAccessException;
import com.xhr.framework.orm.DataManager;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.model.datamodel.OtherTrendsModel;
import com.xhr88.lp.util.DBUtil;

/**
 * 动态管理类
 * 
 * @author zou.sq
 */
public class TrendsDao {
	private TrendsDao() {
	}

	public static OtherTrendsModel getHistoryData(String uid) {
		if (StringUtil.isNullOrEmpty(uid)) {
			return null;
		}
		OtherTrendsModel model = null;
		DataManager dataManager = DBUtil.getDataManager();
		dataManager.open();
		try {
			model = dataManager.get(OtherTrendsModel.class, "UID = ?", new String[] { uid });
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		dataManager.close();
		return model;
	}

}

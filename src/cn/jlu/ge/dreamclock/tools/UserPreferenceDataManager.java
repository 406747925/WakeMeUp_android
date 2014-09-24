package cn.jlu.ge.dreamclock.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferenceDataManager {
	private static SharedPreferences appInfo = null;
	
	public UserPreferenceDataManager(Context context) {
		if ( appInfo == null )
			appInfo = context.getSharedPreferences(Const.APP_INFO_PREFERENCE, Context.MODE_MULTI_PROCESS);
	}
	
	public boolean clearUserPreferenceData () {
		if ( appInfo == null ) {
			return false;
		}
		
		SharedPreferences.Editor appInfoEditor = (SharedPreferences.Editor)appInfo.edit();
		appInfoEditor.putInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, -1);
		appInfoEditor.putInt(Const.USER_BEEN_JEER_NUM, -1);
		appInfoEditor.putInt(Const.USER_SCORE, -1);
		appInfoEditor.putString(Const.USER_SIGN_IN_TIME, "未更新");
		appInfoEditor.putString(Const.GET_USERS_LIST_LAST_TIME , "");
		appInfoEditor.putInt(Const.SIGN_IN_RANK_NUM, -1);
		appInfoEditor.putBoolean(Const.USER_SIGN_IN_OR_NOT, false);
		appInfoEditor.commit();
		
		appInfoEditor = null;
		
		return true;
	}
}

package jianrt.slidetounlock.constants;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * 应用程序配置
 * @author 
 *
 */
public class AppConfig {
	
	private static String getConfigValue(Context context, String key) {
		if(context == null) return null;
		
		SharedPreferences sp = context.getSharedPreferences(Constant.APP_NAME, 0);
		return sp.getString(key, null);
	}
	
	private static void setConfigValue(Context context, String key, String value) {
		if(context == null) return;
		
		SharedPreferences sp = context.getSharedPreferences(Constant.APP_NAME, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);     
        editor.commit();
	}

	
	
	public static int getRequestTimeoutSecs(Context context) {
		String str = getConfigValue(context, "requestTimeoutSecs");
		if(str != null && str.length() > 0)
			return Integer.valueOf(str).intValue();
		else
			return 30;
	}
	
	public static void setRequestTimeoutSecs(Context context, int requestTimeoutSecs) {
		setConfigValue(context, "requestTimeoutSecs", String.valueOf(requestTimeoutSecs));			
	}
	
	public static int getSoTimeoutSecs(Context context) {
		String str = getConfigValue(context, "soTimeoutSecs");
		if(str != null && str.length() > 0)
			return Integer.valueOf(str).intValue();
		else
			return 60;
	}
	
	public static void setSoTimeoutSecs(Context context, int soTimeoutSecs) {
		setConfigValue(context, "soTimeoutSecs", String.valueOf(soTimeoutSecs));			
	}
	
	public static String getLatestUserNo(Context context) {
		return getConfigValue(context, "latestUserNo");		
	}
	
	public static void setLatestUserNo(Context context, String userNo) {
		setConfigValue(context, "latestUserNo", userNo);		
	}
	
	/**
	 * 设置数据库版本号
	 * @param context
	 * @param dbVersion 数据库版本号
	 */
	public static void setDbVersion(Context context, int dbVersion) {
		setConfigValue(context, "dbVersion", String.valueOf(dbVersion));			
	}
	public static int getDbVersion(Context context) {
		return Integer.parseInt(getConfigValue(context, "dbVersion"));		
	}
}

package cn.jlu.ge.getup.tools;

public class Const {
	public static final String APP_INFO_PREFERENCE = ident("app_info");
	
	// KEY 
	public static final String FIRST_CITY_KEY = ident("city01");
	public static final String SECOUND_CITY_KEY = ident("city02");
	public static final String THIRD_CITY_KEY = ident("city03");
	public static final String FIRST_CITY_URL_KEY = ident("cityUrl01");
	public static final String SECOUND_CITY_URL_KEY = ident("cityUrl02");
	public static final String THIRD_CITY_URL_KEY = ident("cityUrl03");
	public static final String NEXT_ALARM_DESC_KEY = ident("nextAlarmDesc");
	public static final String NEXT_ALARM_TIME_KEY = ident("nextAlarmTime");
	public static final String WELCOME_STR_KEY = ident("welcomeStr");
	public static final String FIRST_NOW_TEMP_KEY = ident("city01Temp");
	public static final String FIRST_DAY_TEMP_KEY = ident("city01DayTemp");
	public static final String FIRST_WEATHER_KEY = ident("city01Weather");
	public static final String FIRST_PTIME_KEY = ident("city01ptime");
	public static final String FIRST_WET_KEY = ident("city01SD");
	public static final String FIRST_WD_KEY = ident("city01WD");
	public static final String FIRST_WS_KEY = ident("city01WS");
	
	// The default value of some KEYs
	public static final String FIRST_CITY_DEFAULT = ident("长春");
	public static final String FIRST_CITY_URL_DEFAULT = ident("101060101");
	public static final String NEXT_ALARM_DESC_DEFAULT = ident("下个闹钟");
	public static final String NEXT_ALARM_TIME_DEFAULT = ident("没有了!");
	public static final String WELCOME_STR_DEFAULT = ident("叫醒你的不是闹钟，而是梦想");
	
	// The Start's State of Service
    public static final String CREATE_STATE = ident("Create_MainActivity");
    public static final String NEW_ALRM_STATE = ident("New_Alarm_SetAlarmActivity");
    public static final String CHANGE_STATE = ident("Change_State");
    public static final String SHOW_NEXT_ALARM = ident("Next_Alarm_State");
	
	
	private static String ident(String s) {
		return s;
	}
	
	private static boolean ident(boolean b) {
		return b;
	}
}

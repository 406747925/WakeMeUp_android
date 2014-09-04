package cn.jlu.ge.dreamclock.tools;

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
	public static final String WEATHER_DATE_KEY = ident("weatherDate");
	public static final String PM2_5_ONE_HOUR_AVERAGE_KEY = ident("pm25OneHourAverage");
	public static final String PM2_5_WHOLE_DAY_AVERAGE_KEY = ident("pm25WholeDayAverage");
	
	// The default value of some KEYs
	public static final String FIRST_CITY_DEFAULT = ident("长春");
	public static final String FIRST_CITY_URL_DEFAULT = ident("101060101");
	public static final String NEXT_ALARM_DESC_DEFAULT = ident("下个闹钟");
	public static final String NEXT_ALARM_TIME_DEFAULT = ident("没有了!");
	public static final String WELCOME_STR_DEFAULT = ident("叫醒你的不是闹钟，而是梦想");
	public static final String WEATHER_KEY_ERROR_DEFAULT = ident("未更新");
	
	// The Start's State of Service
    public static final String CREATE_STATE = ident("Create_MainActivity");
    public static final String NEW_ALRM_STATE = ident("New_Alarm_SetAlarmActivity");
    public static final String CHANGE_STATE = ident("Change_State");
    public static final String SHOW_NEXT_ALARM = ident("Next_Alarm_State");
    public static final String UPDATE_WEATHER = ident("Update_Weather");
	
    // weather like string
	public final static String SNOW_AND_RAIN = ident("雨夹雪");
	public final static String SUNNY = ident("晴");
	public final static String CLOUD_LITTLE_RAIN = ident("多云转小雨");
	public final static String RAIN_TO_LIGHTING_RAIN = ident("小到中雨转雷阵雨");
	public final static String SUNNY_CLOUD = ident("晴转多云");
	public final static String CLOUD = ident("多云");
	public final static String RAIN_WITH_THUNDER = ident("雷阵雨");
	public final static String SOMETIME_RAIN = ident("阵雨");
	public final static String CLOUD_TO_SOMETIME_RAIN = ident("多云转阵雨");
	public final static String THOUNDER_AND_RAIN = ident("雷阵雨转阵雨");
	public final static String RAIN_AND_THOUNDER = ident("阵雨转雷阵雨");
	public final static String RAIN_TO_CLOUDY = ident("阵雨转多云");
	
	// 进程通信中的intent aciton
	public final static String GET_FIVE_DAYS_WEATHER = ident("getFiveDaysWeather");
	
	// 线程通信中的Message
	public final static int MESSAGE_NETWORK_GET_FIVEDAYS_WEATHER = ident(1);
	
//	海里/小时 米/秒 公里/小时 
//	0 无风 <1 0.0-0.2 <1 静,烟直上 平静 0.0 0.0 
//	1 软风 1-3 0.3-1.5 1-5 烟示风向 微波峰无飞沫 0.1 0.1 
//	2 轻风 4-6 1.6-3.3 6-11 感觉有风 小波峰未破碎 0.2 0.3 
//	3 微风 7-10 3.4-5.4 12-19 旌旗展开 小波峰顶破裂 0.6 1.0 
//	4 和风 11-16 5.5-7.9 20-28 吹起尘土 小浪白沫波峰 1.0 1.5 
//	5 劲风 17-21 8.0-10.7 29-38 小树摇摆 中浪折沫峰群 2.0 2.5 
//	6 强风 22-27 10.8-13.8 39-49 电线有声 大浪白沫离峰 3.0 4.0 
//	7 疾风 28-33 13.9-17.1 50-61 步行困难 破峰白沫成条 4.0 5.5 
//	8 大风 34-40 17.2-20.7 62-74 折毁树枝 浪长高有浪花 5.5 7.5 
//	9 烈风 41-47 20.8-24.4 75-88 小损房屋 浪峰倒卷 7.0 10.0 
//	10 狂风 48-55 24.5-28.4 89-102 拔起树木 海浪翻滚咆哮 9.0 12.5 
//	11 暴风 56-63 28.5-32.6 103-117 损毁重大 波峰全呈飞沫 11.5 16.0 
//	12 飓风 64-71 32.7-36.9 118-133 摧毁极大 海浪滔天 14.0 - 
	public final static String [] windLevelByString = {
		"无风","软风","轻风","微风","和风","劲风","强风","疾风","大风","烈风","狂风","暴风","飓风"
	};
	
	private static String ident(String s) {
		return s;
	}
	
	private static int ident(int i) {
		return i;
	}
	
}

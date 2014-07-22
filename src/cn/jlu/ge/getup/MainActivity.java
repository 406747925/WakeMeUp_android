package cn.jlu.ge.getup;

import java.util.Locale;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.tools.BaseActivity;
import cn.jlu.ge.getup.tools.Const;
import cn.jlu.ge.getup.tools.ForegroundService;
import cn.jlu.ge.getup.tools.MenuFragment;
import cn.jlu.ge.getup.tools.MyGlobal;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	
	public MainActivity() {
		super(R.string.app_name);
		// TODO Auto-generated constructor stub
	}

//	private AsyncHttpClient client;
	

	public boolean WEATHER_CITY_CHANGED = false;
	
	public static String nextAlarmTime;
	public static String welcomeStr;
	public static String nextAlarmDiscStr;
	public static String weatherCity;
	public String weatherUrl;
//	public static JSONObject weatherObject;
//	public static JSONObject weatherDetailObject;
	
	final String SNOW_AND_RAIN = "雨夹雪";
	final String SUNNY = "晴";
	final String CLOUD_LITTLE_RAIN = "多云转小雨";
	final String RAIN_TO_LIGHTING_RAIN = "小到中雨转雷阵雨";
	final String SUNNY_CLOUD = "晴转多云";
	final String CLOUD = "多云";
	final String RAIN_WITH_THUNDER = "雷阵雨";
	final String SOMETIME_RAIN = "阵雨";
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        
        // Debugging this App when it is starting.
        Debug.startMethodTracing("startApp");
        
        startService();
        
        // 必须对其进行初始化，否则将会抛出运行时异常，提示未发现控件。
        init();
        
        // End Debug.
        Debug.stopMethodTracing();
        
    }
    
	public void init() {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		viewInit();
		
		// 因为在父类 BaseActivity 的 onCreate 方法执行时， 所需要的 Fragment 控件必须是 子控件，
        // 方法中的 FragmentTransaction 会使用 id 资源 ( R.id.menu_frame2 ) 引用 Fragment 控件，
        // 如果未先将对应的 Fragment 控件设置为子控件进行初始化， FragmentManager 将会找不到这个子控件，
		// 而在绘制界面时才抛出运行时异常
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
	}
	
	void startService () {
		MyGlobal.ALARM_CHANGE = true;
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        foregroundServiceIntent.putExtra("doSth", Const.CREATE_STATE);
        startService(foregroundServiceIntent);
	}
	
	public void viewDataInit () {
		
		SharedPreferences appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
		weatherCity = appInfo.getString(Const.FIRST_CITY_KEY, Const.FIRST_CITY_DEFAULT);
		nextAlarmDiscStr = appInfo.getString(Const.NEXT_ALARM_DESC_KEY, Const.NEXT_ALARM_DESC_DEFAULT);
		nextAlarmTime = appInfo.getString(Const.NEXT_ALARM_TIME_KEY, Const.NEXT_ALARM_TIME_DEFAULT);
		welcomeStr = appInfo.getString(Const.WELCOME_STR_KEY, Const.WELCOME_STR_DEFAULT);
		weatherUrl = appInfo.getString(Const.FIRST_CITY_URL_KEY, Const.FIRST_CITY_URL_DEFAULT);
		
		String ptimeStr = appInfo.getString(Const.FIRST_PTIME_KEY, "");
		String weatherStr = appInfo.getString(Const.FIRST_WEATHER_KEY, "");
		String dayTempStr = appInfo.getString(Const.FIRST_DAY_TEMP_KEY, "");
		
		setWeatherView(ptimeStr, weatherStr, dayTempStr);
		
		String wetStr = appInfo.getString(Const.FIRST_WET_KEY, "");
		String wdStr = appInfo.getString(Const.FIRST_WD_KEY, "");
		String windStr = appInfo.getString(Const.FIRST_WS_KEY, "");
		String tempStr = appInfo.getString(Const.FIRST_NOW_TEMP_KEY, "");
		
		setWeatherDetailView(wetStr, wdStr, windStr, tempStr);
		
		appInfo = null;
		
	}
    

	public void viewInit () {
		
		viewDataInit ();
		
		RelativeLayout weatherLayout = (RelativeLayout) findViewById(R.id.weather);
		RelativeLayout signInLayout = (RelativeLayout) findViewById(R.id.signInLayout);
		RelativeLayout positiveLayout = (RelativeLayout) findViewById(R.id.positiveLayout);
        
        // 声明主界面第一条内容的控件
    	TextView alarmTime;
    	TextView welcomeStrTV;
    	TextView discAlarmTime;
    	TextView signInText;
    	TextView positiveText;
        
        // 主界面第一条：时间、天气和下一次闹钟
        // 时间显示和更新
        alarmTime = (TextView) findViewById(R.id.alarmTime);
        welcomeStrTV = (TextView) findViewById(R.id.welcomeStr);
        
		alarmTime.setText(nextAlarmTime);
		welcomeStrTV.setText(welcomeStr);
		
        discAlarmTime = (TextView) findViewById(R.id.discAlarmTime);
        discAlarmTime.setText(nextAlarmDiscStr);
        
        signInLayout = (RelativeLayout) findViewById(R.id.signInLayout);
        signInLayout.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(getApplicationContext(), SignInActivity.class);
				startActivity(newIntent);
			}
        	
        });
        
        signInText = (TextView) findViewById(R.id.signInText);
        signInText.setText("早起签到");
        
        positiveText = (TextView) findViewById(R.id.positiveText);

        weatherLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(MainActivity.this, WeatherMainActivity.class);
				startActivity(newIntent);
			}
			
		});
        
//        client = new AsyncHttpClient();
//        getWeatherFromNet(weatherUrl);
        
		positiveText.setText("毕业了，还睡呀陪你随行");
		positiveLayout.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(MainActivity.this, WebPageActivity.class);
				newIntent.putExtra("url", "positive_energy");
				startActivity(newIntent);
			}
			
		});
		
		discAlarmTime = null;
		positiveText = null;
		signInText = null;
    	alarmTime = null;
    	welcomeStrTV = null;
		alarmTime = null;
		welcomeStrTV = null;

    	weatherLayout = null;
		signInLayout = null;
		positiveLayout = null;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch(item.getItemId()){ 
        case R.id.alarm_setting:
        	Intent intent = new Intent(MainActivity.this, SetAlarmActivity.class);
        	startActivity(intent);
            break;
            
        }
		return super.onOptionsItemSelected(item);
	}

	
	private void setWeatherView(String ptimeStr, String weatherStr,
			String dayTempStr) {
		// TODO Auto-generated method stub
			
		TextView cityName = (TextView) findViewById(R.id.cityName);
		TextView weatherStrTV = (TextView) findViewById(R.id.weatherStr);
		cityName.setText(weatherCity);
		weatherStrTV.setText(dayTempStr + "\n" + weatherStr);
		
		ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
		
		if (weatherStr == null) {
			weatherIcon.setBackgroundResource(R.drawable.error_weather);
		}
		else if (weatherStr.equals(SNOW_AND_RAIN)) {
			weatherIcon.setBackgroundResource(R.drawable.snow_with_rain);
		}
		else if (weatherStr.equals(SUNNY)) {
			weatherIcon.setBackgroundResource(R.drawable.sunny);
		}
		else if (weatherStr.equals(CLOUD_LITTLE_RAIN)) {
			weatherIcon.setBackgroundResource(R.drawable.cloud_little_rain);
		}
		else if (weatherStr.equals(SUNNY_CLOUD)) {
			weatherIcon.setBackgroundResource(R.drawable.sunny_cloud);
		}
		else if (weatherStr.equals(CLOUD)) {
			weatherIcon.setBackgroundResource(R.drawable.cloudy);
		}
		else if (weatherStr.equals(RAIN_WITH_THUNDER)) {
			weatherIcon.setBackgroundResource(R.drawable.rain_with_thunder);
		}
		else if (weatherStr.equals(SOMETIME_RAIN)) {
			weatherIcon.setBackgroundResource(R.drawable.sometime_rain);
		}
		else {
			weatherIcon.setBackgroundResource(R.drawable.error_weather);
		}
	}
	
	private void setWeatherDetailView(String wetStr, String wdStr,
			String windStr, String tempStr) {
		// TODO Auto-generated method stub
		
		TextView locationText = (TextView) findViewById (R.id.locationText);
		TextView weatherLikeText = (TextView) findViewById (R.id.weatherLikeText);
		TextView windText = (TextView) findViewById (R.id.windText);
		TextView tempText = (TextView) findViewById (R.id.tempText);
		TextView uvValueText = (TextView) findViewById (R.id.uvValueText);
		
	    locationText.setText(weatherCity);
	    weatherLikeText.setText(windStr);
	    windText.setText(wetStr);
	    tempText.setText(tempStr + "℃");
	    uvValueText.setText(wdStr);
	    
	    locationText = null;
	    weatherLikeText = null;
	    windText = null;
	    tempText = null;
	    uvValueText = null;
        
}
	
	
//	void setWeatherView (JSONObject weatherObject) {
//
//		try {
//			String cityStr = weatherObject.getString("city");
//			String temp1Str = weatherObject.getString("temp1");
//			String temp2Str = weatherObject.getString("temp2");
//			String weatherStr = weatherObject.getString("weather");
//			weatherObject.getString("ptime");
//			
//			
//			TextView cityName = (TextView) findViewById(R.id.cityName);
//			TextView weatherStrTV = (TextView) findViewById(R.id.weatherStr);
//			cityName.setText(cityStr);
//			weatherStrTV.setText(temp2Str + "~" + temp1Str);
//			
//			ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
//			
//			if (weatherStr.equals(SNOW_AND_RAIN)) {
//				weatherIcon.setBackgroundResource(R.drawable.snow_with_rain);
//			}
//			else if (weatherStr.equals(SUNNY)) {
//				weatherIcon.setBackgroundResource(R.drawable.sunny);
//			}
//			else if (weatherStr.equals(CLOUD_LITTLE_RAIN)) {
//				weatherIcon.setBackgroundResource(R.drawable.cloud_little_rain);
//			}
//			else if (weatherStr.equals(SUNNY_CLOUD)) {
//				weatherIcon.setBackgroundResource(R.drawable.sunny_cloud);
//			}
//			else if (weatherStr.equals(CLOUD)) {
//				weatherIcon.setBackgroundResource(R.drawable.cloudy);
//			}
//			else if (weatherStr.equals(RAIN_WITH_THUNDER)) {
//				weatherIcon.setBackgroundResource(R.drawable.rain_with_thunder);
//			}
//			else if (weatherStr.equals(SOMETIME_RAIN)) {
//				weatherIcon.setBackgroundResource(R.drawable.sometime_rain);
//			}
//			else {
//				weatherIcon.setBackgroundResource(R.drawable.error_weather);
//			}
//			
//			weatherIcon = null;
//			cityName = null;
//			weatherStrTV = null;
//			cityStr = null;
//			temp1Str = null;
//			temp2Str = null;
//			weatherStr = null;
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	
//	void setWeatherDetailView (JSONObject weatherDetailObject) {
//	
//	try {
//		
//		String cityStr = weatherDetailObject.getString("city");
//		String tempStr = weatherDetailObject.getString("temp");
//		String windStr = weatherDetailObject.getString("WS");
//		String wetStr = weatherDetailObject.getString("SD");
//		String wdStr = weatherDetailObject.getString("WD");
//		
//		TextView locationText = (TextView) findViewById (R.id.locationText);
//		TextView weatherLikeText = (TextView) findViewById (R.id.weatherLikeText);
//		TextView windText = (TextView) findViewById (R.id.windText);
//		TextView tempText = (TextView) findViewById (R.id.tempText);
//		TextView uvValueText = (TextView) findViewById (R.id.uvValueText);
//		
//        locationText.setText(cityStr);
//        weatherLikeText.setText(windStr);
//        windText.setText(wetStr);
//        tempText.setText(tempStr + "℃");
//        uvValueText.setText(wdStr);
//        
//        locationText = null;
//        weatherLikeText = null;
//        windText = null;
//        tempText = null;
//        uvValueText = null;
//        
//		cityStr = null;
//		tempStr = null;
//		windStr = null;
//		wetStr = null;
//		wdStr = null;
//        
//	} catch (JSONException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}
//	
//	
//	void getWeatherFromNet(String weatherUrl) {
//    
//		client.get("http://www.weather.com.cn/data/cityinfo/" + weatherUrl + ".html", new AsyncHttpResponseHandler() {
//            
//			@Override
//            public void onSuccess(String response) {
//            	try {
//            		
//					weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
//					setWeatherView(weatherObject);
//					
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//
//			@Override
//			public void onFailure(Throwable throwable, String failureStr) {
//				// TODO Auto-generated method stub
//				
//				TextView alarmTime = (TextView) findViewById (R.id.alarmTime);
//				alarmTime.setText(nextAlarmTime);
//				
//				if ( weatherObject != null ) {
//					setWeatherView(weatherObject);
//				}
//				
//				alarmTime = null;
//				super.onFailure(throwable, failureStr);
//			}
//        });
//        
//        client.get("http://www.weather.com.cn/data/sk/" + weatherUrl + ".html", new AsyncHttpResponseHandler(){
//
//			@Override
//			public void onFailure(Throwable arg0, String arg1) {
//				// TODO Auto-generated method stub
//				if (weatherDetailObject != null) {
//					setWeatherDetailView(weatherDetailObject);
//				}
//				super.onFailure(arg0, arg1);
//			}
//
//			@Override
//			public void onSuccess(int code, String response) {
//				// TODO Auto-generated method stub
//				try {
//					
//					weatherDetailObject = new JSONObject(response).getJSONObject("weatherinfo");
//					setWeatherDetailView(weatherDetailObject);
//					
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				super.onSuccess(code, response);
//				
//			}
//        	
//        });
//        
//	}
//	
//	
//	@Override
//	protected void onRestart() {
//		// TODO Auto-generated method stub
//		init();
//		Toast.makeText(getApplicationContext(), "MainActivity onRestart()", Toast.LENGTH_SHORT).show();
//		super.onRestart();
//	}


	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		init();
		Toast.makeText(getApplicationContext(), "MainActivity onResume()", Toast.LENGTH_SHORT).show();
		super.onResume();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Log.v("MainActivity", "finish.");
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		am.killBackgroundProcesses(getPackageName());
		super.finish();
	}
}

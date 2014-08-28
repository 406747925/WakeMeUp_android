package cn.jlu.ge.getup.activity;

import java.text.SimpleDateFormat;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jlu.ge.getup.R;
import cn.jlu.ge.getup.service.ForegroundService;
import cn.jlu.ge.getup.tools.BaseActivity;
import cn.jlu.ge.getup.tools.BindDataAndResource;
import cn.jlu.ge.getup.tools.Const;
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
	
	private static final String TAG = "MainActivity";
	
	public boolean WEATHER_CITY_CHANGED = false;
	
	private String weatherCity;
	private String nextAlarmTime;
	private String welcomeStr;
	private String nextAlarmDiscStr;
	private String weatherUrl;
	
	String ptimeStr;
	String weatherStr;
	String dayTempStr;
	String wetStr;
	String wdStr;
	String windStr;
	String tempStr;
	String averageOneHourPM25Str;
	
	String dateStr;
	String sharedDateStr;
	
	boolean updateWeatherOrNot;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        
        // Debugging this App when it is starting.
        Debug.startMethodTracing("startApp");
        
        setDate();
        
        startService();
        
        // 必须对其进行初始化，否则将会抛出运行时异常，提示未发现控件。
        viewDataInit();

        // End Debug.
        Debug.stopMethodTracing();
        
    }
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// TODO Auto-generated method stub
		switch(item.getItemId()){ 
        case R.id.alarm_setting:
        	Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
        	startActivity(intent);
            break;
            
        }
		
		return super.onOptionsItemSelected(item);
	}

    
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.v("MainActivity", "onRestoreInstanceState()");
		weatherCity = savedInstanceState.getString(Const.FIRST_CITY_KEY, Const.FIRST_CITY_DEFAULT);
		
		if ( MyGlobal.defaultWeatherCity != weatherCity ) {
			
			updateWeatherOrNot = true;
			Log.v(TAG, "need to update the weather info. From " + weatherCity + " To " + MyGlobal.defaultWeatherCity);
			viewDataInit();
			
		} else {
		
			nextAlarmDiscStr = savedInstanceState.getString(Const.NEXT_ALARM_DESC_KEY, Const.NEXT_ALARM_DESC_DEFAULT);
			nextAlarmTime = savedInstanceState.getString(Const.NEXT_ALARM_TIME_KEY, Const.NEXT_ALARM_TIME_DEFAULT);
			welcomeStr = savedInstanceState.getString(Const.WELCOME_STR_KEY, Const.WELCOME_STR_DEFAULT);
			weatherUrl = savedInstanceState.getString(Const.FIRST_CITY_URL_KEY, Const.FIRST_CITY_URL_DEFAULT);
			
			ptimeStr = savedInstanceState.getString(Const.FIRST_PTIME_KEY, "");
			weatherStr = savedInstanceState.getString(Const.FIRST_WEATHER_KEY, "");
			dayTempStr = savedInstanceState.getString(Const.FIRST_DAY_TEMP_KEY, "");
			
			averageOneHourPM25Str = savedInstanceState.getString(Const.PM2_5_ONE_HOUR_AVERAGE_KEY, "");
			wetStr = savedInstanceState.getString(Const.FIRST_WET_KEY, "");
			wdStr = savedInstanceState.getString(Const.FIRST_WD_KEY, "");
			windStr = savedInstanceState.getString(Const.FIRST_WS_KEY, "");
			tempStr = savedInstanceState.getString(Const.FIRST_NOW_TEMP_KEY, "");
			
		}
		
	}
	
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub 
//		Toast.makeText(getApplicationContext(), "MainActivity onRestart()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onRestart()");
		super.onRestart();
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub 
//		Toast.makeText(getApplicationContext(), "MainActivity onSaveInstanceState()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		
		outState.putString(Const.FIRST_CITY_KEY, weatherCity);
		outState.putString(Const.NEXT_ALARM_DESC_KEY, nextAlarmDiscStr);
		outState.putString(Const.NEXT_ALARM_TIME_KEY, nextAlarmTime);
		outState.putString(Const.WELCOME_STR_KEY, weatherStr);
		outState.putString(Const.FIRST_CITY_URL_KEY, weatherUrl);
		
		outState.putString(Const.FIRST_PTIME_KEY, ptimeStr);
		outState.putString(Const.FIRST_WEATHER_KEY, weatherStr);
		outState.putString(Const.FIRST_DAY_TEMP_KEY, dayTempStr);
		
		outState.putString(Const.FIRST_WET_KEY, wetStr);
		outState.putString(Const.FIRST_WD_KEY, wdStr);
		outState.putString(Const.FIRST_WS_KEY, windStr);
		outState.putString(Const.FIRST_NOW_TEMP_KEY, tempStr);
		outState.putString(Const.PM2_5_ONE_HOUR_AVERAGE_KEY, averageOneHourPM25Str);
		
	}
	
	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub 
//		Toast.makeText(getApplicationContext(), "MainActivity onPostResume()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onPostResume()");
		super.onPostResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub 
//		Toast.makeText(getApplicationContext(), "MainActivity onPause()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub 
//		Toast.makeText(getApplicationContext(), "MainActivity onStop()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		Toast.makeText(getApplicationContext(), "MainActivity onDestroy()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onResumeFragments() {
		// TODO Auto-generated method stub
//		Toast.makeText(getApplicationContext(), "MainActivity onResumeFragments()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onResumeFragments()");
		super.onResumeFragments();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
//		Toast.makeText(getApplicationContext(), "MainActivity onStart()", Toast.LENGTH_SHORT).show();
		Log.v("MainActivity", "onStart()");
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.v("MainActivity", "onResume()");
		
		// 设置 ActionBar 的背景色
		this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_main));
		
		if ( MyGlobal.defaultWeatherCity != weatherCity ) {
			updateWeatherOrNot = true;
			Log.v(TAG, "need to update the weather info. From " + weatherCity + " To " + MyGlobal.defaultWeatherCity);
			viewDataInit();
		}
		
		init();
//		Toast.makeText(getApplicationContext(), "MainActivity onResume()", Toast.LENGTH_SHORT).show();
		
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
		Log.v("MainActivity", "finish()");
		
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		am.killBackgroundProcesses(getPackageName());
		
		super.finish();
		
	}
	
	void setDate () {
		SimpleDateFormat sdf = new SimpleDateFormat("M月 d日 EEEE");
		dateStr = sdf.format(new java.util.Date());
		sdf = null;
	}
	
	public void init() {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_main2);
		
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
	

	public void viewInit () {
		
		setWeatherView(ptimeStr, weatherStr, dayTempStr);
		setWeatherDetailView(wetStr, averageOneHourPM25Str, windStr, tempStr);
		
		LinearLayout weatherLayout = (LinearLayout) findViewById(R.id.weather);
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
        signInText = (TextView) findViewById(R.id.signInText);
        positiveText = (TextView) findViewById(R.id.positiveText);
        signInText.setText("早起签到");
        
        signInLayout.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(getApplicationContext(), SignInActivity.class);
				startActivity(newIntent);
			}
        	
        });

        
        weatherLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(MainActivity.this, WeatherMainActivity.class);
				
				newIntent.putExtra("weatherCity", 0);
				
				startActivity(newIntent);
			}
			
		});
        
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
	
	public void viewDataInit () {
		
		SharedPreferences appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
		sharedDateStr = appInfo.getString(Const.WEATHER_DATE_KEY, Const.WEATHER_KEY_ERROR_DEFAULT);
		weatherCity = appInfo.getString(Const.FIRST_CITY_KEY, Const.FIRST_CITY_DEFAULT);
		
		nextAlarmDiscStr = appInfo.getString(Const.NEXT_ALARM_DESC_KEY, Const.NEXT_ALARM_DESC_DEFAULT);
		nextAlarmTime = appInfo.getString(Const.NEXT_ALARM_TIME_KEY, Const.NEXT_ALARM_TIME_DEFAULT);
		welcomeStr = appInfo.getString(Const.WELCOME_STR_KEY, Const.WELCOME_STR_DEFAULT);
		weatherUrl = appInfo.getString(Const.FIRST_CITY_URL_KEY, Const.FIRST_CITY_URL_DEFAULT);
		
		ptimeStr = appInfo.getString(Const.FIRST_PTIME_KEY, "");
		weatherStr = appInfo.getString(Const.FIRST_WEATHER_KEY, "");
		dayTempStr = appInfo.getString(Const.FIRST_DAY_TEMP_KEY, "");
		
		wetStr = appInfo.getInt(Const.FIRST_WET_KEY, 0) + "%";
		wdStr = appInfo.getString(Const.FIRST_WD_KEY, "");
		windStr = appInfo.getInt(Const.FIRST_WS_KEY, 0) + "级";
		tempStr = appInfo.getString(Const.FIRST_NOW_TEMP_KEY, "");
		int averageOneHourPM25 = appInfo.getInt(Const.PM2_5_ONE_HOUR_AVERAGE_KEY, 0);
		averageOneHourPM25Str = averageOneHourPM25 + "";
		
		appInfo = null;
		
	}
	
	private void setWeatherView(String ptimeStr, String weatherStr,
			String dayTempStr) {
		// TODO Auto-generated method stub
			
		TextView cityName = (TextView) findViewById(R.id.cityName);
		TextView weatherStrTV = (TextView) findViewById(R.id.weatherStr);
		cityName.setText(weatherCity);
		weatherStrTV.setText(dayTempStr + "\n" + weatherStr);
		
		ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
		int drawableResource = BindDataAndResource.getWeatherIconImageResourceId(weatherStr);
		weatherIcon.setBackgroundResource(drawableResource);
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
	
}

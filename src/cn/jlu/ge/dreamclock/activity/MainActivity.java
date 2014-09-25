package cn.jlu.ge.dreamclock.activity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.service.ForegroundService;
import cn.jlu.ge.dreamclock.tools.BindDataAndResource;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.MenuFragment;
import cn.jlu.ge.dreamclock.tools.MyGlobal;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class MainActivity extends BaseActivity {
	
	private CanvasTransformer mTransformer;
	
	public MainActivity() {
		super(R.string.app_name);
		// TODO Auto-generated constructor stub
		
		mTransformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen*0.35 + 0.65);
				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
			}
		};
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
	int averageOneHourPM25;
	int continuousDaysSum;
	int rankNum;
	
	String dateStr;
	String sharedDateStr;
	
	boolean updateWeatherOrNot;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        
        // Debugging this App when it is starting.
//        Debug.startMethodTracing("startApp");
        
        setDate();
        
        startService();
        
        // 必须对其进行初始化，否则将会抛出运行时异常，提示未发现控件。
        viewDataInit();

        // End Debug.
//        Debug.stopMethodTracing();
        
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
			
			averageOneHourPM25 = savedInstanceState.getInt(Const.PM2_5_ONE_HOUR_AVERAGE_KEY, -1);
			wetStr = savedInstanceState.getString(Const.FIRST_WET_KEY, "");
			wdStr = savedInstanceState.getString(Const.FIRST_WD_KEY, "");
			windStr = savedInstanceState.getString(Const.FIRST_WS_KEY, "");
			tempStr = savedInstanceState.getString(Const.FIRST_NOW_TEMP_KEY, "");
			rankNum = savedInstanceState.getInt(Const.USER_RANK, -1);
			continuousDaysSum = savedInstanceState.getInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, -1);
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
		outState.putInt(Const.PM2_5_ONE_HOUR_AVERAGE_KEY, averageOneHourPM25);
		outState.putInt(Const.USER_RANK, rankNum);
		outState.getInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, continuousDaysSum );		
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
		getSlidingMenu().setBehindCanvasTransformer(mTransformer);
		
	}
	
	void startService () {
		MyGlobal.ALARM_CHANGE = true;
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        foregroundServiceIntent.putExtra("doSth", Const.CREATE_STATE);
        startService(foregroundServiceIntent);
	}
	

	public void viewInit () {
		
		setWeatherView(ptimeStr, weatherStr, dayTempStr, averageOneHourPM25);
		setUserInfoView ( continuousDaysSum, rankNum );
		
		LinearLayout signInLayout = (LinearLayout) findViewById(R.id.signInLayout);
		LinearLayout positiveLayout = (LinearLayout) findViewById(R.id.positiveLayout);
        LinearLayout weatherLayout = (LinearLayout) findViewById(R.id.weatherLayout);
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
        
        signInText = (TextView) findViewById(R.id.signInText);
        positiveText = (TextView) findViewById(R.id.positiveText);
        
        signInLayout.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(getApplicationContext(), SignInActivity.class);
				startActivity(newIntent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
        	
        });

        
        weatherLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(MainActivity.this, WeatherMainActivity.class);
				
				newIntent.putExtra("weatherCity", 0);
				
				startActivity(newIntent);
				
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
			
		});
        
		positiveLayout.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(MainActivity.this, PositiveEnergyActivity.class);
//				newIntent.putExtra("url", "positive_energy");
				startActivity(newIntent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
			
		});
		
		discAlarmTime = null;
		positiveText = null;
		signInText = null;
    	alarmTime = null;
    	welcomeStrTV = null;
		alarmTime = null;
		welcomeStrTV = null;

		signInLayout = null;
		positiveLayout = null;
		
	}
	
	public void viewDataInit () {
		
		SharedPreferences appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
		
		String userName = appInfo.getString(Const.USER_NAME, "Me");
		String UID = appInfo.getString(Const.USER_ID, "123456789");
		boolean signUpOrNot = appInfo.getBoolean(Const.USER_LOG_IN_OR_NOT, false);
		
		continuousDaysSum = appInfo.getInt(Const.USER_CONTINUOUS_SIGN_IN_DAYS, 0);
		rankNum = appInfo.getInt(Const.USER_RANK, 0);

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
		averageOneHourPM25 = appInfo.getInt(Const.PM2_5_ONE_HOUR_AVERAGE_KEY, 0);
		
		appInfo = null;
		
	}
	
	public void setUserInfoView ( int continuousDaysSum, int rankNum ) {
		TextView signInInfoTv = (TextView) findViewById(R.id.signInInfo);
		String text = "上次签到排名 %s \n连续签到天数 %s";
		if ( rankNum == 0 ) {
			text = String.format(text, "未更新", "未更新");
		} else {
			text = String.format(text, rankNum + " 名", continuousDaysSum + " 天");
		}
		signInInfoTv.setText(text);
	}
	
	private void setWeatherView(String ptimeStr, String weatherStr,
			String dayTempStr, int pm25) {
		// TODO Auto-generated method stub

		TextView weatherStrTV = (TextView) findViewById(R.id.weatherStr);
		weatherStrTV.setText(weatherCity + "\n" + dayTempStr);
		
		TextView pm25TV = (TextView) findViewById(R.id.PMStr);
		pm25TV.setText( BindDataAndResource.getPM25Level( pm25 )  + "\n" + pm25);
		
		ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
		int drawableResource = BindDataAndResource.getWeatherIconImageResourceId(weatherStr);
		weatherIcon.setBackgroundResource(drawableResource);
	}
	
}

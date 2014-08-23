package cn.jlu.ge.getup.activity;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.R;
import cn.jlu.ge.getup.service.ForegroundService;
import cn.jlu.ge.getup.tools.BindDataAndResource;
import cn.jlu.ge.getup.tools.Const;
import cn.jlu.ge.getup.tools.GetUpMediaPlayer;
import cn.jlu.ge.getup.tools.GetUpVibrator;
import cn.jlu.ge.getup.tools.MyGlobal;
import cn.jlu.ge.getup.tools.ShakeDetector;
import cn.jlu.ge.getup.tools.ShakeDetector.OnShakeListener;


public class WakeUpActivity extends Activity {

	GetUpVibrator vibrator;
	ShakeDetector shakeDetector;
	GetUpMediaPlayer mediaPlayer;
	
	TextView shakeRemained;
	TextView welcome;
	
	String weatherCity;
	String alarmDiscStr;
	String alarmTimeStr;
	String welcomeStr;
	String weatherUrl;
	String weatherStr;
	String dayTempStr;
	
	int shakeTimes;
	public static String mediaNameStr = "the_train_in_the_spring.mp3";
	
	KeyguardManager mKeyguardManager = null;
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;
	
	final String SNOW_AND_RAIN = "雨夹雪";
	final String SUNNY = "晴";
	final String CLOUD_LITTLE_RAIN = "多云转小雨";
	final String RAIN_TO_LIGHTING_RAIN = "小到中雨转雷阵雨";
	final String SUNNY_CLOUD = "晴转多云";
	final String CLOUD = "多云";
	final String RAIN_WITH_THUNDER = "雷阵雨";
	final String SOMETIME_RAIN = "阵雨";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wakeup);
		

		setScreenBrightAndShowWindow();
		
		shakeTimes = 3;
        long[] pattern = {1000, 500, 1000, 500};
        int type = 0;
        
        viewInit ();
        
        WakeUpActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new GetUpMediaPlayer(getApplicationContext(), mediaNameStr);
        mediaPlayer.startPlay();
        
        vibrator = new GetUpVibrator(getApplicationContext());
		vibrator.playVibrate(pattern, type);
		
		shakeDetector = new ShakeDetector(getApplicationContext());
		shakeDetector.start();
        shakeDetector.registerOnShakeListener(new OnShakeListener(){
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				shakeTimes--;
				if (shakeTimes == 0) {
					shakeDetector.stop();
					vibrator.cancelVibrate();
					mediaPlayer.stopPlay();
				}
				shakeRemained.setText("剩余摇停次数：" + shakeTimes);
			}
        });
        
        MyGlobal.ALARM_CHANGE = true;
        Intent foregroundServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
        foregroundServiceIntent.putExtra("doSth", Const.SHOW_NEXT_ALARM);
        startService(foregroundServiceIntent);
        
	}
	
	void viewInit () {
		
        shakeRemained = (TextView)findViewById(R.id.shakeRemained);
        shakeRemained.setText("剩余摇停次数：" + shakeTimes);
		
		SharedPreferences appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
		weatherCity = appInfo.getString(Const.FIRST_CITY_KEY, Const.FIRST_CITY_DEFAULT);
		alarmDiscStr = appInfo.getString(Const.NEXT_ALARM_DESC_KEY, Const.NEXT_ALARM_DESC_DEFAULT);
		alarmTimeStr = appInfo.getString(Const.NEXT_ALARM_TIME_KEY, Const.NEXT_ALARM_TIME_DEFAULT);
		welcomeStr = appInfo.getString(Const.WELCOME_STR_KEY, Const.WELCOME_STR_DEFAULT);
		
		weatherStr = appInfo.getString(Const.FIRST_WEATHER_KEY, "");
		dayTempStr = appInfo.getString(Const.FIRST_DAY_TEMP_KEY, "");
		
		TextView cityName = (TextView) findViewById (R.id.cityName);
		Toast.makeText(getApplicationContext(), "what:" + welcomeStr, Toast.LENGTH_SHORT).show();
		cityName.setText(weatherCity);
		
		TextView notify = (TextView) findViewById (R.id.notify);
		notify.setText(welcomeStr);
		
		TextView welcome = (TextView) findViewById (R.id.welcome);
		welcome.setText(welcomeStr);
		
		TextView alarmTime = (TextView) findViewById (R.id.alarmTime);
		alarmTime.setText(alarmTimeStr);
		
		TextView dateText = (TextView) findViewById (R.id.dateText);
		SimpleDateFormat sdf = new SimpleDateFormat("M月 d日 EEEE");
		String date = sdf.format(new java.util.Date());
		dateText.setText(" " + date);
		
		TextView weatherStrTV = (TextView) findViewById(R.id.weather);
		weatherStrTV.setText(dayTempStr + "\n" + weatherStr);
		
		ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
		int drawableResource = BindDataAndResource.getWeatherIconImageResourceId(weatherStr);
		weatherIcon.setBackgroundResource(drawableResource);

	}
	
	public void setScreenBrightAndShowWindow() {
		final Window win = (Window) this.getWindow();
		final WindowManager.LayoutParams params = win.getAttributes();
		params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		wakeLock.acquire();
		wakeLock.release();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}

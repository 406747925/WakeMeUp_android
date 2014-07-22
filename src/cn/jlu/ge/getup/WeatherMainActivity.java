package cn.jlu.ge.getup;

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jlu.ge.getup.tools.BaseActivity;
import cn.jlu.ge.getup.tools.Const;
import cn.jlu.ge.getup.tools.MenuFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class WeatherMainActivity extends BaseActivity {

	public WeatherMainActivity() {
		super(MainActivity.weatherCity + "天气");
		// TODO Auto-generated constructor stub
	}

//	private String weatherUrl;
	private SharedPreferences appInfo;
	
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
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		init();
		
	}
	
	
	public void init() {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_weather_main);
		
		viewInit();
		
		// 因为在父类 BaseActivity 的 onCreate 方法执行时， 所需要的 Fragment 控件必须是 子控件，
        // 方法中的 FragmentTransaction 会使用 id 资源( R.id.menu_frame2 ) 引用 Fragment 控件，
        // 如果未先将对应的 Fragment 控件设置为子控件进行初始化， FragmentManager 将会找不到这个子控件，
		// 而在绘制界面时才抛出运行时异常
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
	}
	
	void viewInit () {
		
		
		viewDataInit();
		
		
	}
	
	void viewDataInit () {
		
		appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
		
		String ptimeStr = appInfo.getString(Const.FIRST_PTIME_KEY, null);
		String weatherStr = appInfo.getString(Const.FIRST_WEATHER_KEY, null);
		String nowTempStr = appInfo.getString(Const.FIRST_NOW_TEMP_KEY, null);
		
		setWeatherView(ptimeStr, weatherStr, nowTempStr);
		
		TextView dateText;
		dateText = (TextView) findViewById(R.id.dateText);
		SimpleDateFormat sdf = new SimpleDateFormat("M月 d日 EEEE");
		String date = sdf.format(new java.util.Date());
		dateText.setText(" " + date);
		
		sdf = null;
		date = null;
		dateText = null;
		appInfo = null;

		
	}
	
//	void getWeatherFromNet (String weatherUrl) {
//		
//		client.get("http://www.weather.com.cn/data/cityinfo/" + weatherUrl + ".html", new AsyncHttpResponseHandler() {
//            
//
//			@Override
//			public void onFailure(Throwable throwable, String failureStr) {
//				// TODO Auto-generated method stub
//				String date = sdf.format(new java.util.Date());
//				dateText.setText(" " + date);
//				super.onFailure(throwable, failureStr);
//			}
//
//			@Override
//			public void onSuccess(String response) {
//				// TODO Auto-generated method stub
//
//				try {
//					JSONObject weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
//					
//					setWeatherView (weatherObject);
//					
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				String date = sdf.format(new java.util.Date());
//				dateText.setText(" " + date);
//			}
//			
//        });
//
//	}
	
	private void setWeatherView(String ptimeStr, String weatherStr,
			String nowTempStr) {
		// TODO Auto-generated method stub
		
		TextView updateTimeText = (TextView) findViewById(R.id.updateTimeText);;
		TextView tempText = (TextView) findViewById(R.id.tempText);;
		TextView weatherLikeText = (TextView) findViewById(R.id.weatherLikeText);
		
		updateTimeText.setText(ptimeStr + " 发布");
		tempText.setText(nowTempStr);
		weatherLikeText.setText(weatherStr);
		
		ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
		
		if (weatherStr.equals(SNOW_AND_RAIN)) {
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
		
		weatherIcon = null;
		weatherLikeText = null;
		tempText = null;
		updateTimeText = null;
		
	}
	
	
//	void setWeatherView (JSONObject weatherObject) {
//		
//		try {
//			String cityStr = weatherObject.getString("city");
//			String temp1Str = weatherObject.getString("temp1");
//			String temp2Str = weatherObject.getString("temp2");
//			String weatherStr = weatherObject.getString("weather");
//			String pTime = weatherObject.getString("ptime");
//			updateTimeText.setText(pTime + " 发布");
//			tempText.setText(temp2Str + "~" + temp1Str);
//			weatherLikeText.setText(weatherStr);
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
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.weather_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
        
		case R.id.weather_setting:
        	Intent intent = new Intent(WeatherMainActivity.this, WeatherCitiesSettingActivity.class);
        	startActivity(intent);
            break;
            
        }
		return super.onOptionsItemSelected(item);
	}

}

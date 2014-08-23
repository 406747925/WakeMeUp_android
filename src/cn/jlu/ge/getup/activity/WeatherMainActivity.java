package cn.jlu.ge.getup.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.R;
import cn.jlu.ge.getup.service.INetworkTask;
import cn.jlu.ge.getup.tools.BaseActivity;
import cn.jlu.ge.getup.tools.BindDataAndResource;
import cn.jlu.ge.getup.tools.Const;
import cn.jlu.ge.getup.tools.MenuFragment;
import cn.jlu.ge.getup.tools.UserDataDBAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class WeatherMainActivity extends BaseActivity {

	public WeatherMainActivity() {
		super("天气");
		// TODO Auto-generated constructor stub
	}

//	private String weatherUrl;
	private SharedPreferences appInfo;
	public String weatherCity;
//	private AsyncHttpClient client;
	private UserDataDBAdapter userDataDb;
	private INetworkTask networkTaskService;
	
	final String TAG = "WeatherMainActivity";
	
	private ServiceConnection conn = new ServiceConnection () {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			networkTaskService = INetworkTask.Stub.asInterface(service);
			Log.v(TAG, "networkTaskService: " + networkTaskService.toString());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.v(TAG, "networkTaskService has been disconnected.");
			networkTaskService = null;
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userDataDb = new UserDataDBAdapter(getApplicationContext());
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
		weatherCity = appInfo.getString(Const.FIRST_CITY_KEY, Const.FIRST_CITY_DEFAULT);
		String ptimeStr = appInfo.getString(Const.FIRST_PTIME_KEY, Const.WEATHER_KEY_ERROR_DEFAULT);
		String weatherStr = appInfo.getString(Const.FIRST_WEATHER_KEY, Const.WEATHER_KEY_ERROR_DEFAULT);
		String nowTempStr = appInfo.getString(Const.FIRST_NOW_TEMP_KEY, Const.WEATHER_KEY_ERROR_DEFAULT) + "℃";
		
		setWeatherView(ptimeStr, weatherStr, nowTempStr);
		
		TextView dateText = (TextView) findViewById(R.id.dateText);
		SimpleDateFormat sdf = new SimpleDateFormat("M月 d日 EEEE");
		String date = sdf.format(new java.util.Date());
		dateText.setText(" " + date);
		
		Toast.makeText(getApplicationContext(), "正在更新" + weatherCity + "的五日天气", Toast.LENGTH_SHORT).show();

		
		getFiveDaysWeatherFromNet(weatherCity);
		
		sdf = null;
		date = null;
		dateText = null;
		appInfo = null;

		
	}
	
	void getFiveDaysWeatherFromNet ( final String weatherCity ) {
		Intent intent = new Intent("cn.jlu.ge.getup.service.UserNetworkService");
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
		new Handler().postDelayed(new Runnable(){

		    public void run() {
		    	
		    	// TODO 
				try {
					
					if ( networkTaskService == null ) {
						Log.v(TAG, "when getFromNet : networkTaskService is null.");
					} else {
//						Log.v(TAG, "when getFromNet : networkTaskService is not null.");
						networkTaskService.getFiveDaysWeatherFromNet(weatherCity);
//						setFiveDaysWeatherAndSuggestion (response);
					}

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		    
		 }, 300);
		
	}
	
	void setFiveDaysWeatherView (ArrayList<String> weatherList) {
		
		{
			int index = 0;
			for (String one : weatherList ) {
				Log.v(TAG,index + ": " + one);
				index++;
			}
		}
		
        //准备要添加的数据条目
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>(); 
        for (int i = 1; i < 6; i++) {
        	Map<String, Object> item = new HashMap<String, Object>();
        	String [] dateAndWeather = weatherList.get( i * 5 + 2 ).split(" ");
        	String [] tempMinAndMax = weatherList.get( i * 5 + 3 ).split("/");
        	item.put("date", dateAndWeather[0].replace("月", "-").replace("日", "") );
        	item.put("minTemp", tempMinAndMax[0]);
        	item.put("maxTemp", tempMinAndMax[1]);
        	item.put("weatherLikeImage", BindDataAndResource.getWeatherIconImageResourceId( dateAndWeather[1] ) );
        	item.put("wind", BindDataAndResource.getWindStringByString( weatherList.get( i * 5 + 4) ) );
        	items.add(item);
        }
        
		SimpleAdapter adapter = new SimpleAdapter(this,  
                items,
                R.layout.five_days_weather_item,
                new String[]{"date", "minTemp", "maxTemp", "weatherLikeImage", "wind"},
                new int[]{R.id.date, R.id.minTemp, R.id.maxTemp, R.id.weatherLikeImage, R.id.wind});
		
		GridView fiveDaysWeatherGV = (GridView)findViewById(R.id.fiveDaysWeather);
		
		fiveDaysWeatherGV.setAdapter(adapter);
	}
	
	void setTodayWeatherSuggestionView ( String weatherSuggestionStr ) {
		String [] suggestionArray = weatherSuggestionStr.split("\n");
		
		TextView suggestionTV01 = (TextView) findViewById(R.id.suggestionTV01);
		suggestionTV01.setText( suggestionArray[1].split("。")[0].replace("：", " ") );
		
		TextView suggestionTV02 = (TextView) findViewById(R.id.suggestionTV02);
		suggestionTV02.setText( suggestionArray[3].split("。")[0].replace("：", " ") );

		TextView suggestionTV03 = (TextView) findViewById(R.id.suggestionTV03);
		suggestionTV03.setText( suggestionArray[5].split("。")[0].replace("：", " ") );
		
		TextView suggestionTV04 = (TextView) findViewById(R.id.suggestionTV04);
		suggestionTV04.setText( suggestionArray[8].split("。")[0].replace("：", " ") );
		
		suggestionTV01 = null;
		suggestionTV02 = null;
		suggestionTV03 = null;
		suggestionTV04 = null;
	}
	
	void setSecAndThirdWeatherCitiesView () {
		// TODO 从数据库获取天气城市数据，显示在底部的view
		userDataDb.open();
		Cursor citiesCursor = userDataDb.getAllWeatherCitiesDatas();
		
		if ( citiesCursor.moveToFirst() != false && citiesCursor != null ) {
			citiesCursor.moveToNext();
			String secCityStr = citiesCursor.getString( citiesCursor.getColumnIndex( UserDataDBAdapter.KEY_DATA_CONTENT ) );
			TextView secCityTV = (TextView) findViewById (R.id.secWeatherCityTV);
			secCityTV.setText(secCityStr);
			
			if ( !citiesCursor.isLast() && citiesCursor != null ) {
				citiesCursor.moveToNext();
				String thridCityStr = citiesCursor.getString( citiesCursor.getColumnIndex( UserDataDBAdapter.KEY_DATA_CONTENT ) );
				TextView thridCityTV = (TextView) findViewById (R.id.thirdWeatherCityTV);
				thridCityTV.setText(thridCityStr);
			}
		}
		
		citiesCursor.close();
		userDataDb.close();

	}
	
	void setFiveDaysWeatherDataInDb ( ArrayList<String> weatherList ) {
		
	}
	
	void setFiveDaysWeatherAndSuggestion (String response) {
		
		ArrayList<String> weatherList = new ArrayList<String>();
		
		try {
			XmlPullParser parser = Xml.newPullParser();
			Log.v(TAG, ">>>>" + response);
			InputStream inputStreamWithResponse = new ByteArrayInputStream(response.getBytes("UTF-8"));
			parser.setInput(inputStreamWithResponse, "utf-8");
			int event = parser.getEventType();

			while ( event != XmlPullParser.END_DOCUMENT ) {
				switch ( event ) {
				case XmlPullParser.START_TAG:
					if ( "string".equals( parser.getName() ) ) {
						String item = parser.nextText();
						weatherList.add(item);
					} else if ( "ArrayOfString".equals( parser.getName() ) ) {
					}
				break;
				
				default: break;
				}
				
				event = parser.next();
			}
			
			if ( weatherList.size() > 20 ) {
				setFiveDaysWeatherView(weatherList);
				setFiveDaysWeatherDataInDb(weatherList);
				setTodayWeatherSuggestionView( weatherList.get(6) );
				setSecAndThirdWeatherCitiesView();
			}
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			Log.v(TAG, "XmlPullParserException: " + e.toString());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setWeatherView(String ptimeStr, String weatherStr,
			String nowTempStr) {
		// TODO Auto-generated method stub
		
		TextView updateTimeText = (TextView) findViewById(R.id.updateTimeText);
		TextView tempText = (TextView) findViewById(R.id.tempText);
		TextView weatherLikeText = (TextView) findViewById(R.id.weatherLikeText);
		
		updateTimeText.setText(ptimeStr + " 发布");
		tempText.setText(nowTempStr);
		weatherLikeText.setText(weatherStr);
		
		ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
		int drawableResource = BindDataAndResource.getWeatherIconImageResourceId(weatherStr);
		weatherIcon.setBackgroundResource(drawableResource);
		
		weatherIcon = null;
		weatherLikeText = null;
		tempText = null;
		updateTimeText = null;
		
	}
	
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


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unbindService(conn);
	}

}

package cn.jlu.ge.dreamclock.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.BindDataAndResource;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.MenuFragment;
import cn.jlu.ge.dreamclock.tools.UserDataDBAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class WeatherMainActivity extends BaseActivity {

	public WeatherMainActivity() {
		super("天气");
		// TODO Auto-generated constructor stub
	}

	private SharedPreferences appInfo;
	public String weatherCity;
	private AsyncHttpClient client;
	private UserDataDBAdapter userDataDb;
	ArrayList<String> weatherCities;
	private JSONArray weatherArray;
	private int weatherCityNum;
	
	final String TAG = "WeatherMainActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		weatherCityNum = intent.getExtras().getInt("weatherCity");
		
		userDataDb = new UserDataDBAdapter(getApplicationContext());
		
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Toast.makeText(getApplicationContext(), "onResume() >>> " + weatherCityNum, Toast.LENGTH_SHORT).show();
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
		
		if ( weatherCityNum == 0 ) {
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			
			weatherCity = appInfo.getString(Const.FIRST_CITY_KEY, Const.FIRST_CITY_DEFAULT);
			String ptimeStr = appInfo.getString(Const.FIRST_PTIME_KEY, Const.WEATHER_KEY_ERROR_DEFAULT);
			String weatherStr = appInfo.getString(Const.FIRST_WEATHER_KEY, Const.WEATHER_KEY_ERROR_DEFAULT);
			String nowTempStr = appInfo.getString(Const.FIRST_NOW_TEMP_KEY, Const.WEATHER_KEY_ERROR_DEFAULT) + "℃";
			
			setWeatherView(ptimeStr, weatherStr, nowTempStr, weatherCity);
		} else {
			
			try {
				JSONObject todayWeatherObject = weatherArray.getJSONObject(weatherCityNum).getJSONArray("weather_data").getJSONObject(0);
				String weatherCity = weatherArray.getJSONObject(weatherCityNum).getString("currentCity");
				String weatherStr = todayWeatherObject.getString("weather");
				String nowTempStr = todayWeatherObject.getString("date");
				String ptimeStr = "实时";
				nowTempStr = nowTempStr.split("：")[1].replace(")", "");
				setWeatherView(ptimeStr, weatherStr, nowTempStr, weatherCity);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		TextView dateText = (TextView) findViewById(R.id.dateText);
		SimpleDateFormat sdf = new SimpleDateFormat("M月 d日 EEEE");
		String date = sdf.format(new java.util.Date());
		dateText.setText(" " + date);
		
		if ( weatherArray == null ) {
			Toast.makeText(getApplicationContext(), "正在更新" + weatherCity + "的五日天气", Toast.LENGTH_SHORT).show();
			getFiveDaysWeatherFromNet(weatherCity);
		} else {
			try {
				setFiveDaysWeatherView ( weatherArray.getJSONObject(weatherCityNum).getJSONArray("weather_data") );
				setTodayWeatherSuggestionView ( weatherArray.getJSONObject(weatherCityNum).getJSONArray("index") );
				setSecAndThirdWeatherCitiesView ( weatherCities, weatherArray );
			} catch ( JSONException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sdf = null;
		date = null;
		dateText = null;
		appInfo = null;

		
	}
	
	String getWeatherUrl ( String weatherCity ) {
		ArrayList<String> weatherCities = getWeatherCitiesData();
		
		String weatherUrl = null;
		if ( weatherCities != null ) {
			String weatherCitiesStr = weatherCities.toString().replace(" ", "%7C").replace(",", "").replace("[", "").replace("]", "");
			weatherUrl = "http://api.map.baidu.com/telematics/v3/weather?location=" + weatherCitiesStr 
					+ "&output=json&ak=n3OWXWndI27Lk6GcU9joKMOD";
			
			Log.v(TAG, weatherUrl);
		} else {
			weatherUrl = "http://api.map.baidu.com/telematics/v3/weather?location=" + weatherCity 
			+ "&output=json&ak=n3OWXWndI27Lk6GcU9joKMOD";
		}
		
		return weatherUrl;
	}
	
	void getFiveDaysWeatherFromNet ( final String weatherCity ) {
		
		String weatherUrl = getWeatherUrl(weatherCity);
		
		client = new AsyncHttpClient();
		
		client.get( weatherUrl, new AsyncHttpResponseHandler() {
	        
			@Override
	        public void onSuccess(String response) {
				
				setOtherWeatherView (response);
				
	        }

			@Override
			public void onFailure(Throwable throwable, String failureStr) {
				// TODO Auto-generated method stub
				if ( weatherArray != null ) {
					try {
						setFiveDaysWeatherView ( weatherArray.getJSONObject(0).getJSONArray("weather_data") );
						setTodayWeatherSuggestionView ( weatherArray.getJSONObject(0).getJSONArray("index") );
						setSecAndThirdWeatherCitiesView (weatherCities, weatherArray);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				super.onFailure(throwable, failureStr);
				
			}
	    });
		
		client = null;
	}
	
	void setOtherWeatherView ( String response ) {
    	try {
			weatherArray = new JSONObject(response).getJSONArray("results");
			Log.v(TAG, " weather from baidu : " + weatherArray.toString());
		} catch ( JSONException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			setFiveDaysWeatherView ( weatherArray.getJSONObject(0).getJSONArray("weather_data") );
			setTodayWeatherSuggestionView ( weatherArray.getJSONObject(0).getJSONArray("index") );
			setSecAndThirdWeatherCitiesView ( weatherCities, weatherArray );
		} catch ( JSONException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void setFiveDaysWeatherView (JSONArray weatherDataArray) {
		
        //准备要添加的数据条目
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>(); 
        
        for (int i = 0; i < 4; i++) {
        	
        	Map<String, Object> item = new HashMap<String, Object>();
        	
        	try {
        		JSONObject oneObject = weatherDataArray.getJSONObject(i);
        		String [] tempMinAndMax =oneObject.get("temperature").toString().replace(" ", "").replace("℃", "").split("~");
        		if ( tempMinAndMax.length == 1 ) 
        			tempMinAndMax = new String [] { tempMinAndMax[0], tempMinAndMax[0] };
        		
				item.put("date",  oneObject.get("date").toString().subSequence(0, 2) );
	        	
				Log.v(TAG, ">>>>" + tempMinAndMax.length + ";>>>>" + tempMinAndMax[0] + ";>>>>" + oneObject.get("temperature").toString().replace(" ", "") );
				// TODO 这里会出现问题
				item.put("minTemp", tempMinAndMax[1] + "℃");
	        	item.put("maxTemp", tempMinAndMax[0] + "℃");
	        	item.put("weatherLikeImage", BindDataAndResource.getWeatherIconImageResourceId( oneObject.get("weather").toString() ) );
	        	item.put("wind", oneObject.get("wind").toString() );
	        	items.add(item);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        
		SimpleAdapter adapter = new SimpleAdapter(this,  
                items,
                R.layout.five_days_weather_item,
                new String[]{"date", "minTemp", "maxTemp", "weatherLikeImage", "wind"},
                new int[]{R.id.date, R.id.minTemp, R.id.maxTemp, R.id.weatherLikeImage, R.id.wind});
		
		GridView fiveDaysWeatherGV = (GridView)findViewById(R.id.fiveDaysWeather);
		
		fiveDaysWeatherGV.setAdapter(adapter);
	}
	
	void setTodayWeatherSuggestionView ( JSONArray weatherSuggestionArray ) {
		
		TextView suggestionTV01 = (TextView) findViewById(R.id.suggestionTV01);
		TextView suggestionTV02 = (TextView) findViewById(R.id.suggestionTV02);
		TextView suggestionTV03 = (TextView) findViewById(R.id.suggestionTV03);
		TextView suggestionTV04 = (TextView) findViewById(R.id.suggestionTV04);
		try {
			suggestionTV01.setText( weatherSuggestionArray.getJSONObject(0).getString("tipt") + " " + weatherSuggestionArray.getJSONObject(0).getString("zs") );
			suggestionTV02.setText( weatherSuggestionArray.getJSONObject(3).getString("tipt") + " " + weatherSuggestionArray.getJSONObject(3).getString("zs") );
			suggestionTV03.setText( weatherSuggestionArray.getJSONObject(4).getString("tipt") + " " + weatherSuggestionArray.getJSONObject(4).getString("zs") );
			suggestionTV04.setText( weatherSuggestionArray.getJSONObject(5).getString("tipt").substring(0, 5) + " " + weatherSuggestionArray.getJSONObject(5).getString("zs") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			suggestionTV01.setText("网络通信故障，\n无法获取4日天气预报");
			e.printStackTrace();
		}

		suggestionTV01 = null;
		suggestionTV02 = null;
		suggestionTV03 = null;
		suggestionTV04 = null;
	}
	
	ArrayList<String> getWeatherCitiesData () {
		
		userDataDb.open();
		
		Cursor citiesCursor = userDataDb.getAllWeatherCitiesDatas();

		if ( citiesCursor.moveToFirst() == false || citiesCursor == null ) {
			return null;
		}
		
		weatherCities = new ArrayList<String>();
		
		for ( citiesCursor.moveToFirst() ; !citiesCursor.isLast() ; citiesCursor.moveToNext() ) {
			
			String cityStr = citiesCursor.getString( citiesCursor.getColumnIndex( UserDataDBAdapter.KEY_DATA_CONTENT ) );
			if ( cityStr != null )
				weatherCities.add(cityStr);
			
			if ( citiesCursor.isLast() )
				break;
			
		}

		citiesCursor.close();
		userDataDb.close();
		
		if ( weatherCities.size() == 0 )
			return null;
		else 
			return weatherCities;
	}
	
	void setSecAndThirdWeatherCitiesView (ArrayList<String> weatherCities, JSONArray weatherArray) {
		// TODO 
		int citiesIds [] = { R.id.secWeatherCityTV, R.id.thirdWeatherCityTV };
		int weatherIds [] = { R.id.secWeather, R.id.thirdWeather };
		int imageIds [] = { R.id.secWeatherImage, R.id.thirdWeatherImage };
		int layoutIds [] = { R.id.secWeatherCity, R.id.thirdWeatherCity };
		
		if ( weatherCities != null ) {
			for ( int i = 0, j = 0 ; i < weatherCities.size() && j < citiesIds.length ; i++ ) {
				if ( i == weatherCityNum) {
					continue;
				} else {
					try {
						TextView cityTV = (TextView) findViewById ( citiesIds[j] );
						cityTV.setText( weatherCities.get( i ) );
						
						JSONObject oneObject = weatherArray.getJSONObject( i ).getJSONArray("weather_data").getJSONObject(0);
						
						String tempStr = oneObject.get("temperature").toString();
						String weatherStr = oneObject.get("weather").toString();
						TextView weatherTV = (TextView) findViewById ( weatherIds[j] );
						weatherTV.setText(weatherStr + " " + tempStr);
						ImageView weatherImage = (ImageView) findViewById ( imageIds[j] );
						weatherImage.setBackgroundResource( BindDataAndResource.getWeatherIconImageResourceId( oneObject.get("weather").toString() ) );
						
						LinearLayout layout = (LinearLayout) findViewById (layoutIds[j]);
						final int cityNum = i;
						layout.setOnClickListener(new OnClickListener () {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(WeatherMainActivity.this, WeatherMainActivity.class);
								intent.putExtra("weatherCity", cityNum);
								weatherCityNum = cityNum;
								Toast.makeText(getApplicationContext(), "Clicked " + cityNum, Toast.LENGTH_SHORT).show();
								startActivity(intent);
							}
							
						});
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
					j++;
				}
				
			}
		}
			
		
	}
	
	void setFiveDaysWeatherDataInDb ( ArrayList<String> weatherList ) {
		
	}
	
//	void setFiveDaysWeatherAndSuggestion (String response) {
//		
//		ArrayList<String> weatherList = new ArrayList<String>();
//		
//		try {
//			XmlPullParser parser = Xml.newPullParser();
//			Log.v(TAG, ">>>>" + response);
//			InputStream inputStreamWithResponse = new ByteArrayInputStream(response.getBytes("UTF-8"));
//			parser.setInput(inputStreamWithResponse, "utf-8");
//			int event = parser.getEventType();
//
//			while ( event != XmlPullParser.END_DOCUMENT ) {
//				switch ( event ) {
//				case XmlPullParser.START_TAG:
//					if ( "string".equals( parser.getName() ) ) {
//						String item = parser.nextText();
//						weatherList.add(item);
//					} else if ( "ArrayOfString".equals( parser.getName() ) ) {
//						
//					}
//				break;
//				
//				default: break;
//				}
//				
//				event = parser.next();
//			}
//			
//			if ( weatherList.size() > 20 ) {
////				setFiveDaysWeatherView(weatherList);
////				setFiveDaysWeatherDataInDb(weatherList);
////				setTodayWeatherSuggestionView( weatherList.get(6) );
////				setSecAndThirdWeatherCitiesView();
//			}
//			
//		} catch (XmlPullParserException e) {
//			// TODO Auto-generated catch block
//			Log.v(TAG, "XmlPullParserException: " + e.toString());
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private void setWeatherView(String ptimeStr, String weatherStr,
			String nowTempStr, String weatherCity) {
		// TODO Auto-generated method stub
		
		TextView updateTimeText = (TextView) findViewById(R.id.updateTimeText);
		TextView tempText = (TextView) findViewById(R.id.tempText);
		TextView weatherLikeText = (TextView) findViewById(R.id.weatherLikeText);
		
		updateTimeText.setText(ptimeStr + " 发布");
		tempText.setText(weatherCity + " " + nowTempStr);
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
//		this.unbindService(conn);
//		networkTaskThread.quit();
	}

}

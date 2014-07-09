package cn.jlu.ge.getup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.tools.BaseActivity;
import cn.jlu.ge.getup.tools.ForegroundService;
import cn.jlu.ge.getup.tools.MenuFragment;
import cn.jlu.ge.getup.tools.UserDataDBAdapter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainActivity extends BaseActivity {
	
	public MainActivity() {
		super(R.string.app_name);
		// TODO Auto-generated constructor stub
	}

	private AsyncHttpClient client;
	
	private Calendar calendar;
	private RelativeLayout alarmLayout;
	private RelativeLayout weatherLayout;
	private RelativeLayout signInLayout;
	private RelativeLayout positiveLayout;
	int shakeTimes;
	
	private TextView rowOne;
	private TextView rowTwo;
	private TextView rowThree;
	private ImageView weatherIcon;
	
	private ImageButton locationBtn;
	private ImageButton tempBtn;
	private ImageButton windBtn;
	private ImageButton uvValueBtn;
	private ImageButton weatherLikeBtn;
	private TextView locationText;
	private TextView tempText;
	private TextView windText;
	private TextView uvValueText;
	private TextView weatherLikeText;
	private TextView signInText;
	private TextView positiveText;
	private int DEFAULT_WEATHER_CITY_FLAG = 1;
	
	public static String weatherCity = "长春";
	public static String weatherUrl = "101060101";
	
	public static JSONObject weatherObject;
	
	private UserDataDBAdapter userDatadb;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("hh mm");
	
	final String SNOW_AND_RAIN = "雨夹雪";
	final String SUNNY = "晴";
	final String CLOUD_LITTLE_RAIN = "多云转小雨";
	final String SUNNY_CLOUD = "晴转多云";
	final String CLOUD = "多云";

	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        
        // 必须对其进行初始化，否则将会抛出运行时异常，提示未发现控件。
        init();
        
        userDatadb = new UserDataDBAdapter(getApplicationContext());
        setWeatherCitiesData();
        
        startService();
        
		
    }
    
	public void init() {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		viewInit();
		
		// 因为在父类 BaseActivity 的 onCreate 方法执行时， 所需要的 Fragment 控件必须是 子控件，
        // 方法中的 FragmentTransaction 会使用 id 资源( R.id.menu_frame2 ) 引用 Fragment 控件，
        // 如果未先将对应的 Fragment 控件设置为子控件进行初始化， FragmentManager 将会找不到这个子控件，
		// 而在绘制界面时才抛出运行时异常
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
	}
	
	void startService () {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        foregroundServiceIntent.putExtra("doSth", ForegroundService.CREATE_STATE);
        startService(foregroundServiceIntent);
	}
    
	public void viewInit() {
        alarmLayout = (RelativeLayout) findViewById(R.id.alarm);
        weatherLayout = (RelativeLayout) findViewById(R.id.weather);
        signInLayout = (RelativeLayout) findViewById(R.id.signInLayout);
        positiveLayout = (RelativeLayout) findViewById(R.id.positiveLayout);
        
        calendar = Calendar.getInstance();

        rowOne = (TextView) findViewById(R.id.rowOne);
        
        handler.post(updateThread);
        
        rowTwo = (TextView) findViewById(R.id.rowTwo);
        rowThree = (TextView) findViewById(R.id.rowThree);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        
        locationBtn = (ImageButton) findViewById(R.id.location);
        weatherLikeBtn = (ImageButton) findViewById(R.id.weatherLike);
        windBtn = (ImageButton) findViewById(R.id.wind);
        tempBtn = (ImageButton) findViewById(R.id.temp);
        uvValueBtn = (ImageButton) findViewById(R.id.uvValue);
        
        locationText = (TextView) findViewById(R.id.locationText);
        weatherLikeText = (TextView) findViewById(R.id.weatherLikeText);
        windText = (TextView) findViewById(R.id.windText);
        tempText = (TextView) findViewById(R.id.tempText);
        uvValueText = (TextView) findViewById(R.id.uvValueText);
        
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
        signInText.setText("早期签到");
        
        positiveText = (TextView) findViewById(R.id.positiveText);

        alarmLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(MainActivity.this, WeatherMainActivity.class);
				startActivity(newIntent);
			}
			
		});
        
        client = new AsyncHttpClient();
        getWeatherFromNet(weatherUrl);
        
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
	}
	
    void internetConnect(String weatherCity, String weatherUrl) {
    	try {

			String data = "";
			//baseUrl
			String baseUrl = "http://www.weather.com.cn/data/sk/" + weatherUrl + ".html";

			HttpGet getMethod = new HttpGet(baseUrl);

			HttpClient httpClient = new DefaultHttpClient();

			try {
			    HttpResponse response = httpClient.execute(getMethod);
			    data = EntityUtils.toString(response.getEntity(), "utf-8");
			    Log.i("resCode", "resCode = " + response.getStatusLine().getStatusCode());
			    Log.i("result", "result = " + data);
			} catch (ClientProtocolException e) {
			    // TODO Auto-generated catch block
				Toast.makeText( MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
			    e.printStackTrace();
			} catch (IOException e) {
			    // TODO Auto-generated catch block
				Toast.makeText( MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
			    e.printStackTrace();
			}

			Toast.makeText( MainActivity.this, data, Toast.LENGTH_SHORT).show();

    	} catch (Exception e) {
    		Log.v("Log e", e.toString());
            e.printStackTrace();
    	}
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

	Handler handler = new Handler();
	Runnable updateThread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(updateThread, 10000);
			String time = sdf.format(Calendar.getInstance().getTime());
			rowOne.setText(time);
		}
		
	};
	
	void setWeatherCitiesData() {
        userDatadb.open();
        Cursor cursor = userDatadb.getAllWeatherCitiesDatas();
        
        if ( cursor != null && cursor.getCount() != 0 ) {
        	cursor.moveToFirst();
            if ( cursor.getInt( cursor.getColumnIndex( UserDataDBAdapter.KEY_DATA_COUNT ) ) == DEFAULT_WEATHER_CITY_FLAG ) {
                MainActivity.weatherCity = cursor.getString(cursor.getColumnIndex(UserDataDBAdapter.KEY_DATA_CONTENT));
                MainActivity.weatherUrl = cursor.getString(cursor.getColumnIndex(UserDataDBAdapter.KEY_DATA_UNIT));            	
            }
        }
        
        if (cursor != null) cursor.close();
        
        userDatadb.close();
	}
	
	public void writeFileData(String fileName,String message){ 
		try{ 
			// MODE_PRIVATE
				FileOutputStream fout = openFileOutput(fileName, MODE_APPEND);
		        byte [] bytes = message.getBytes(); 
		        fout.write(bytes); 
		        fout.close();
	
			} catch(Exception e){ 
	        	e.printStackTrace(); 
	        }
	}
	
	void setWeatherView (JSONObject weatherObject) {

		try {
			String cityStr = weatherObject.getString("city");
			String temp1Str = weatherObject.getString("temp1");
			String temp2Str = weatherObject.getString("temp2");
			String weatherStr = weatherObject.getString("weather");
			weatherObject.getString("ptime");
			
			rowTwo.setText(cityStr);
			rowThree.setText(temp2Str + "~" + temp1Str + "\n" + weatherStr);
			
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
			else {
				weatherIcon.setBackgroundResource(R.drawable.error_weather);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void getWeatherFromNet(String weatherUrl) {
    
		client.get("http://www.weather.com.cn/data/cityinfo/" + weatherUrl + ".html", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	try {
            		
					weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
					setWeatherView(weatherObject);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

			@Override
			public void onFailure(Throwable throwable, String failureStr) {
				// TODO Auto-generated method stub
				sdf = new SimpleDateFormat("hh:mm");
				String time = sdf.format(new java.util.Date());
				rowOne.setText(time);
				
				if ( weatherObject != null ) {
					setWeatherView(weatherObject);
				}
				
				super.onFailure(throwable, failureStr);
			}
        });
        
        client.get("http://www.weather.com.cn/data/sk/" + weatherUrl + ".html", new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}

			@Override
			public void onSuccess(int code, String response) {
				// TODO Auto-generated method stub
				try {
					
					JSONObject weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
					String cityStr = weatherObject.getString("city");
					String tempStr = weatherObject.getString("temp");
					String windStr = weatherObject.getString("WS");
					String wetStr = weatherObject.getString("SD");
					String wdStr = weatherObject.getString("WD");
					
			        locationText.setText(weatherCity);
			        weatherLikeText.setText(windStr);
			        windText.setText(wetStr);
			        tempText.setText(tempStr + "C");
			        uvValueText.setText(wdStr);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				super.onSuccess(code, response);
				
			}
        	
        });
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.sign_in, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}

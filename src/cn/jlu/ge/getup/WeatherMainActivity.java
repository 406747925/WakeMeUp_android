package cn.jlu.ge.getup;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class WeatherMainActivity extends SherlockActivity {

	private AsyncHttpClient client;
	private TextView rowOne;
	private TextView rowTwo;
	private TextView rowThree;
	private TextView rowFour;
	private String weatherUrl;
	private ImageView weatherIcon;
	
	final String SNOW_AND_RAIN = "���ѩ";
	final String SUNNY = "��";
	final String CLOUD_LITTLE_RAIN = "����תС��";
	final String SUNNY_CLOUD = "��ת����";
	final String CLOUD = "����";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("hh mm");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_main);
		
		client = new AsyncHttpClient();
		getWeatherFromNet(weatherUrl);
	
	}
	
	void getWeatherFromNet(String weatherUrl) {
		
		client.get("http://www.weather.com.cn/data/cityinfo/" + weatherUrl + ".html", new AsyncHttpResponseHandler() {
            
			@Override
            public void onSuccess(String response) {
            	
				try {
            		
					JSONObject weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
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

			@Override
			public void onFailure(Throwable throwable, String failureStr) {
				// TODO Auto-generated method stub
				sdf = new SimpleDateFormat("hh:mm");
				String time = sdf.format(new java.util.Date());
				rowOne.setText(time);
				super.onFailure(throwable, failureStr);
			}
			
        });
                
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

}

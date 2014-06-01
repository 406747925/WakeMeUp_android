package cn.jlu.ge.getup;

import cn.jlu.ge.getup.tools.WeatherCitiesDBAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class WeatherCitiesSettingActivity extends Activity {

	WeatherCitiesDBAdapter weatherCitiesDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_weahter_cities_setting);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	void getWeatherCitiesFormDb() {
		
		weatherCitiesDb = new WeatherCitiesDBAdapter(this);
		weatherCitiesDb.open();
		Cursor cursor = weatherCitiesDb.getAllRows();
		if (cursor.moveToFirst() == false) {
			cursor.close();
			weatherCitiesDb.close();
			return ;
		}
		
		String cityName;
		String cityPyName;
		String cityUrl;
		for (cursor.moveToFirst(); ; cursor.moveToNext()) {
			
			// get city info : name, pinyin name, url
			cityName = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_CITY));
			cityPyName = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_CITY_PY));
			cityUrl = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_CITY_URL));
			
			// TODO get province info
			
			// TODO put data into map
		}
		
	}

}

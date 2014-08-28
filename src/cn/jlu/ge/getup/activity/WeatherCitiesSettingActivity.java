package cn.jlu.ge.getup.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.R;
import cn.jlu.ge.getup.service.ForegroundService;
import cn.jlu.ge.getup.tools.Const;
import cn.jlu.ge.getup.tools.MyGlobal;
import cn.jlu.ge.getup.tools.UserDataDBAdapter;
import cn.jlu.ge.getup.tools.WeatherCitiesDBAdapter;
import cn.jlu.ge.getup.tools.WeatherCitiesExpandListAdapter;

public class WeatherCitiesSettingActivity extends Activity {

	private WeatherCitiesDBAdapter weatherCitiesDb;
	private UserDataDBAdapter userDataDb;
	private ExpandableListView weatherCitiesList;
	final static String TAG = "WeatherCitiesSettingActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_weather_cities_setting);
		
		weatherCitiesDb = new WeatherCitiesDBAdapter(getApplicationContext());

		userDataDb = new UserDataDBAdapter(getApplicationContext());
		
		weatherCitiesList = (ExpandableListView) findViewById(R.id.cities_list);
		WeatherCitiesExpandListAdapter adapter = new WeatherCitiesExpandListAdapter(this);
		
		weatherCitiesList.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
					long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Group Click " + id, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		
		weatherCitiesList.setOnChildClickListener(new OnChildClickListener () {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPos,
					int childPos, long id) {
				// TODO Auto-generated method stub
				
				Log.v("Child Id", ">>> this child: " + id);
				
				String cityName = null;
				String cityPyName = null;
				String url = null;
				
				weatherCitiesDb.open();
				Cursor cursor = weatherCitiesDb.getRowByRowId(id);
				
				if ( cursor.getCount() != 0 ) {
					
					cityName = cursor.getString( cursor.getColumnIndex( WeatherCitiesDBAdapter.KEY_CITY ) );
					cityPyName = cursor.getString( cursor.getColumnIndex( WeatherCitiesDBAdapter.KEY_CITY_PY ) );
					url = cursor.getString( cursor.getColumnIndex( WeatherCitiesDBAdapter.KEY_CITY_URL ) );
					Log.v(cityName, cityPyName);
				}
				
				cursor.close();
				weatherCitiesDb.close();
				
				if (cityName != null && cityPyName != null && url != null) {
					userDataDb.open();
					Cursor citiesCursor = userDataDb.getAllWeatherCitiesDatas();
					if (citiesCursor != null) {
						if ( citiesCursor.getCount() > 3 ) {
							
							Toast.makeText(getApplicationContext(), "最多4个城市哦～", Toast.LENGTH_SHORT).show();
							
						} else {
							
							userDataDb.insertWeatherCityData(cityName, cityPyName, url, citiesCursor.getCount());
							userDataDb.close();
							
							View cityAddedListItem = setCityAddedView(cityName);
							// TODO Android 4.1 三星无法 addHeaderView
							parent.addHeaderView(cityAddedListItem);
							
						}
						
					}
					
				}
				
				return true;
				
			}
			
		});
		
		setWeatherCitiesHaveBeenAdded();
		weatherCitiesList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onPause");
		Log.v(TAG, "Start Service to update weather.");
		
        Intent foregroundServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
        foregroundServiceIntent.putExtra("doSth", Const.UPDATE_WEATHER);
        startService(foregroundServiceIntent);
		super.onPause();
	}

	public void setWeatherCitiesHaveBeenAdded() {

		int citiesCount = weatherCitiesList.getHeaderViewsCount();
		for (int i = 0; i < citiesCount ; i++) {
			weatherCitiesList.removeHeaderView(weatherCitiesList.getChildAt(i));
		}

		userDataDb.open();
		Cursor cursor = userDataDb.getAllWeatherCitiesDatas();
		if (cursor.moveToFirst() == false) {
			return ;
		}
		
		int cityNameColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_DATA_CONTENT);
		int countColumn = cursor.getColumnIndex(UserDataDBAdapter.KEY_DATA_COUNT);
		
		String cityName = null;
		int cityCount = 0;
		
		for ( cursor.moveToFirst() ; ; cursor.moveToNext() ) {
			
			cityName = cursor.getString(cityNameColumn);
			cityCount = cursor.getInt(countColumn);
			
			
			weatherCitiesList.addHeaderView(setCityAddedView(cityName));
			
			if ( cursor.isLast() ) {
				break;
			}
		}

	}
	
	View setCityAddedView (String cityName) {
		
		View cityAddedListItem = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_weather_city_added, null);
		TextView cityNameTV = (TextView) cityAddedListItem.findViewById(R.id.listText);
		cityNameTV.setText(cityName);
		Button deleteCity = (Button) cityAddedListItem.findViewById(R.id.itemDelete);
		deleteCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				
				View viewParent = (View) view.getParent();
				TextView cityNameTV = (TextView) viewParent.findViewById(R.id.listText);
				userDataDb.open();
				userDataDb.deleteWeatherCityByName(cityNameTV.getText().toString());
				userDataDb.close();
				Log.v("HELLO delete", "delete" + cityNameTV.getText().toString());
				
				setWeatherCitiesHaveBeenAdded();
			}
		});
		
		cityAddedListItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				TextView cityNameTV = (TextView) view.findViewById(R.id.listText);
				
				userDataDb.open();
				String weatherCity = cityNameTV.getText().toString();
				userDataDb.setWeatherCityDefaulted(weatherCity);
				userDataDb.close();
				
				setWeatherCitiesHaveBeenAdded();
				
				MyGlobal.defaultWeatherCity = weatherCity;
				
			}
		});
		
		return cityAddedListItem;
	}

}

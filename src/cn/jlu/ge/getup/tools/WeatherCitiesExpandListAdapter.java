package cn.jlu.ge.getup.tools;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.R;

public class WeatherCitiesExpandListAdapter extends BaseExpandableListAdapter {

	LayoutInflater inflater;
    public Context context;
    
    private WeatherCitiesDBAdapter weatherCitiesDb;
    
	private ArrayList<HashMap<String, Object>> provinceList;
	private ArrayList<HashMap<String, Object>> citiesList;
	
    public WeatherCitiesExpandListAdapter (Context c) {
    	
    	context = c;
    	inflater = LayoutInflater.from(c);
    	provinceList = new ArrayList<HashMap<String,Object>>();
		citiesList = new ArrayList<HashMap<String, Object>>();
    	getWeatherCitiesFormDb();
    	
    }
    
	class ListClickGroup {
		
	    public TextView groupText;
	    public int position;

	}
	
	class ListClickChild {
		
		public TextView childText;
		public int position;
		
	}
    
	@Override
	public Object getChild(int groupPos, int childPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPos, int childPos) {
		// TODO Auto-generated method stub
		if (groupPos < 1) {
			return childPos;
		} else {
			return Integer.parseInt(provinceList.get(groupPos - 1).get("lastChildPos").toString()) + 1 + childPos;
		}
	}

	@Override
	public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		ListClickChild thisView = null;
		if (convertView != null) {
			
			thisView = (ListClickChild) convertView.getTag();

		} else {
			
			thisView = new ListClickChild();
			convertView = inflater.inflate(R.layout.weather_cities_list_child, null);
			thisView.childText = (TextView)convertView.findViewById(R.id.textChild);
			convertView.setTag(thisView);

		}
		
		int childStartPosInList = 0;
		
		if (groupPos > 0)
			childStartPosInList = Integer.parseInt(provinceList.get(groupPos - 1).get("lastChildPos").toString()) + 1;
		else
			childStartPosInList = 0;
			
		thisView.position = childStartPosInList + childPos;
		if ( thisView.position > Integer.parseInt(provinceList.get(groupPos).get("lastChildPos").toString()) ) {
			thisView.childText.setText( citiesList.get( Integer.parseInt(provinceList.get(groupPos).get("lastChildPos").toString()) ).get("cityName").toString() );
		} else {
			thisView.childText.setText(citiesList.get( thisView.position).get("cityName").toString() );
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPos) {
		// TODO Auto-generated method stub
		if (groupPos < 1) {
			int thisProvinceLastChildInList = Integer.parseInt(provinceList.get(groupPos).get("lastChildPos").toString());
			return  thisProvinceLastChildInList + 1;
		} else {
			
			Toast.makeText(context, ">>> " + provinceList.get(groupPos).get("areaPyName").toString(), Toast.LENGTH_SHORT).show();
			int preProvinceLastChildInList = Integer.parseInt(provinceList.get(groupPos - 1).get("lastChildPos").toString());
			int thisProvinceLastChildInList = Integer.parseInt(provinceList.get(groupPos).get("lastChildPos").toString());
			return thisProvinceLastChildInList - preProvinceLastChildInList;
			
		}
		
	}

	@Override
	public Object getGroup(int groupPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return provinceList.size();
	}

	@Override
	public long getGroupId(int groupPos) {
		// TODO Auto-generated method stub
		return groupPos;
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListClickGroup thisView = null;
		if (convertView != null) {
			thisView = (ListClickGroup) convertView.getTag();
			Log.v("tag", "positon " + groupPos + " convertView is not null, "  + thisView);

		} else {
			thisView = new ListClickGroup();
			convertView = inflater.inflate(R.layout.activity_weather_cities_group_title, null);
			thisView.groupText = (TextView)convertView.findViewById(R.id.textGroup);
			convertView.setTag(thisView);
		}
		
		thisView.position = groupPos;
		String areaName = provinceList.get(groupPos).get("areaName").toString();
		thisView.groupText.setText(areaName);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPos, int childPos) {
		
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		// TODO Auto-generated method stub
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO Auto-generated method stub
		super.onGroupExpanded(groupPosition);
	}

	void getWeatherCitiesFormDb () {
		
		weatherCitiesDb = new WeatherCitiesDBAdapter(context);
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
		String areaName;
		String areaPyName;
		HashMap<String, Object> map;
		
		int i = 0;
		
		for (cursor.moveToFirst(); ; cursor.moveToNext(), i++) {
			
			// get city info : name, pinyin name, url
			cityName = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_CITY));
			cityPyName = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_CITY_PY));
			cityUrl = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_CITY_URL));
			
			// TODO get province info
			areaName = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_AREA));
			areaPyName = cursor.getString(cursor.getColumnIndex(WeatherCitiesDBAdapter.KEY_AREA_PY));
			
			// TODO put data into map
			map = new HashMap<String, Object>();
			map.put("cityName", cityName);
			map.put("cityPyName", cityPyName);
			map.put("cityUrl", cityUrl);
			map.put("areaName", areaName);
			map.put("areaPyName", areaPyName);
			
			// TODO add map to citiesListItem
			citiesList.add(map);
			
			// TODO put province data into map
			map = new HashMap<String, Object>();
			map.put("areaName", areaName);
			map.put("areaPyName", areaPyName);
			
			if ( cursor.isLast() ) {
				
				HashMap<String, Object> preMap = new HashMap<String, Object>();
				preMap = provinceList.get(provinceList.size() - 1);
				preMap.put("lastChildPos", i - 1);
				provinceList.remove(provinceList.size() - 1);
				provinceList.add(preMap);
				
				return ;
			}
			else if (provinceList.size() == 0 ) {
				
				// DO NOTHING BUT add map
				provinceList.add(map);
				
			}
			else if (provinceList.size() != 0 && provinceList.get(provinceList.size() - 1 ).get("areaPyName").toString().equals( map.get("areaPyName").toString()) ) {
				
				// TODO Nothing.
				
			} else {
				
				// TODO add province data into provinceListItem
				
				HashMap<String, Object> preMap = new HashMap<String, Object>();
				preMap = provinceList.get(provinceList.size() - 1);
				preMap.put("lastChildPos", i - 1);
				provinceList.remove(provinceList.size() - 1);
				provinceList.add(preMap);
				provinceList.add(map);

			}
			
		}
		
	}

}

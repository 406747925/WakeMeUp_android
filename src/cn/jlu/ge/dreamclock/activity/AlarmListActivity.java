package cn.jlu.ge.dreamclock.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.service.ForegroundService;
import cn.jlu.ge.dreamclock.tools.AlarmDBAdapter;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.MenuFragment;
import cn.jlu.ge.dreamclock.tools.MyGlobal;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class AlarmListActivity extends BaseActivity {
	
	public AlarmListActivity() {
		super(R.string.app_name);
		// TODO Auto-generated constructor stub
	}

	int alarmHourColumn;
	int alarmMinsColumn;
	int kindColumn;
	int activeColumn;
	int welcomeColumn;
	int activeBool;
	String alarmTimeStr;
	String kindStr;
	AlarmDBAdapter db;
	Calendar calendar;
	AlarmManager alarms;
	ListView alarmList;
	int[] time;
	String [] weekStr = { "一", "二", "三", "四", "五", "六", "日"};
	
	private ArrayList<HashMap<String, Object>> listItems;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_list);
		
		// 设置闹钟
		db = new AlarmDBAdapter(this);
		
		setAlarmList();
		
		menuInit();
	}
	
	public void menuInit() {
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_alarm_list);
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setAlarmList();
		super.onResume();
	}

	
	void setDreamClock() {
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.alarm_item);
		
		layout.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AlarmListActivity.this, ChangeAlarmActivity.class);
				intent.putExtra("rowID", listItems.get(0).get("rowID").toString());
				intent.putExtra("alarmTime", listItems.get(0).get("alarmTime").toString());
				intent.putExtra("alarmKind", listItems.get(0).get("alarmKind").toString());
				intent.putExtra("welcome", listItems.get(0).get("welcome").toString());
				startActivity(intent);
			}
		} );

		String welcomeStr = listItems.get(0).get("welcome").toString();
		String weekActiveDaysStr = listItems.get(0).get("alarmKind").toString();
		String alarmTimeStr = listItems.get(0).get("alarmTime").toString();
		String isActiveStr = listItems.get(0).get("activeBool").toString();
		weekActiveDaysStr = getWeekActiveDaysStr( weekActiveDaysStr.split(" ") );
		
		TextView alarmTimeText = (TextView) findViewById(R.id.alarmTime);
		TextView activeDaysText = (TextView) findViewById(R.id.activeDays);
		TextView tips = (TextView) findViewById(R.id.tips);
		Switch switchButton = (Switch) findViewById(R.id.changeActive);
		
		alarmTimeText.setText(alarmTimeStr);
		tips.setText(welcomeStr);
		activeDaysText.setText(Html.fromHtml( weekActiveDaysStr ));
		if ( isActiveStr.equals("1") ) {
			switchButton.setChecked(true);
		} else {
			switchButton.setChecked(false);
		}
		
		OnCheckedChangeListener activeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				db.open();
				
				if ( isChecked ) {
					db.enableRow(Integer.parseInt(listItems.get(0).get("rowID").toString()));
					listItems.get(0).put("activeBool", 1);
					buttonView.setChecked(isChecked);
					setAlarmList();
				} else {
					Log.v("FUCK STR!", listItems.get(0).get("activeBool").toString());
					db.disableRow(Integer.parseInt(listItems.get(0).get("rowID").toString()));
					listItems.get(0).put("activeBool", 0);
					buttonView.setChecked(isChecked);
					setAlarmList();
				}

				db.close();
				
				MyGlobal.ALARM_CHANGE = true;
		        Intent foregroundServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
		        foregroundServiceIntent.putExtra("doSth", Const.CHANGE_STATE);
		        startService(foregroundServiceIntent);
			}
		};
		
		switchButton.setOnCheckedChangeListener(activeListener);
	}
	

	// 设置闹钟
	boolean setAlarmList() {
		
		listItems = new ArrayList<HashMap<String, Object>>();
		
		time = new int[2];
		
		AlarmsData alarmsData = new AlarmsData();
		alarmsData.setAlarmDataList();
		
		// 设置梦想闹钟
		setDreamClock();
		
		calendar = Calendar.getInstance();
        alarmList = (ListView) findViewById(R.id.alarmList);
        MyAdapter listAdapter = new MyAdapter(this);
        alarmList.setAdapter(listAdapter);
        alarmList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
				// TODO Auto-generated method stub
				position = position + 1;
				Intent intent = new Intent(AlarmListActivity.this, ChangeAlarmActivity.class);
				intent.putExtra("rowID", listItems.get(position).get("rowID").toString());
				intent.putExtra("alarmTime", listItems.get(position).get("alarmTime").toString());
				intent.putExtra("alarmKind", listItems.get(position).get("alarmKind").toString());
				intent.putExtra("welcome", listItems.get(position).get("welcome").toString());
				startActivity(intent);
			}
        	
		});
        
        alarmList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adpterView, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Builder delAlarmDialogBuilder = new AlertDialog.Builder(AlarmListActivity.this);
				delAlarmDialogBuilder.create();
				
				String delAlarmStr = "删除闹钟";
				final int pos = position;
				delAlarmDialogBuilder.setPositiveButton(delAlarmStr, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						db.open();
						db.deleteRow(Long.parseLong(listItems.get(pos).get("rowID").toString()));
						db.close();
						
						// 重新排列闹钟列表
						setAlarmList();
						
						MyGlobal.ALARM_CHANGE = true;
						
				        Intent foregroundServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
				        foregroundServiceIntent.putExtra("doSth", Const.NEW_ALRM_STATE);
				        startService(foregroundServiceIntent);
					}
				});
				
				delAlarmDialogBuilder.show();
				
				return true;
			}
        	
		});
		return true;
	}

	// 获取闹钟数据并初始化
	class AlarmsData {

		Cursor cursor;

		int pos;
		HashMap<String, Object> map;
		
		
		// 
		public boolean setAlarmDataList() {
			
			db.open();
			cursor = db.getAllRows();
			if ( cursor.getCount() == 0 ) {
				db.close();
				insertAlarmInDb(6, 0);
				db.open();
				cursor = db.getAllRows();
			}
			
			if (cursor.moveToFirst() == false) {
				return false;
			}
			
			alarmHourColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_HOUR);
			alarmMinsColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_MINS);
			kindColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_KIND);
			activeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ACTIVE);
			welcomeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_WELCOME);
			
			for (cursor.moveToFirst();!cursor.isLast(); cursor.moveToNext()) {
				addHashMap(alarmHourColumn, alarmMinsColumn, kindColumn, activeColumn, welcomeColumn, activeBool, alarmTimeStr, kindStr, map);
			}
			
			addHashMap(alarmHourColumn, alarmMinsColumn, kindColumn, activeColumn, welcomeColumn, activeBool, alarmTimeStr, kindStr, map);
			db.close();
			
			return true;
		}
		
		void addHashMap(int alarmHourColumn, int alarmMinsColumn, int kindColumn, int activeColumn, int welcomeColumn, int activeBool, String alarmTimeStr, String kindStr, HashMap<String, Object> map) {
			int rowID = 0;
			map = new HashMap<String, Object>();
			alarmTimeStr = cursor.getInt(alarmHourColumn) + ":" + cursor.getInt(alarmMinsColumn);
			rowID = cursor.getShort(cursor.getColumnIndex("_id"));
			Log.v("AlarmTime:", alarmTimeStr);
			kindStr = cursor.getString(kindColumn);
			activeBool = cursor.getInt(activeColumn);
			String welcomeStr = cursor.getString(welcomeColumn);
			
			map.put("rowID", rowID);
			map.put("positon", pos++);
			map.put("alarmTime", alarmTimeStr);
			map.put("alarmKind", kindStr);
			map.put("activeBool", activeBool);
			map.put("welcome", welcomeStr);

			listItems.add(map);
		}

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.alarm_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch(item.getItemId()){      
        case R.id.add_alarm:
			Log.d("set Alarm", "click the time button to set time");
			
			calendar.setTimeInMillis(System.currentTimeMillis());

			final TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmListActivity.this,new TimePickerDialog.OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker tp, int hour, int mins) {
					// TODO Auto-generated method stub
					if (calendar.get(Calendar.HOUR_OF_DAY) >= hour || (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) >= mins)) {
						calendar.setTimeInMillis(System.currentTimeMillis() + 86400000);
					} else {
						Log.v("Second", "This is else");
						calendar.setTimeInMillis(System.currentTimeMillis());
					}
					
					time[0] = hour;
					time[1] = mins;
					
					calendar.set(Calendar.HOUR_OF_DAY, hour);
					calendar.set(Calendar.MINUTE, mins);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					
//		            addHashMap(alarmTimeColumn, kindColumn, activeColumn, activeBool, alarmTimeStr, kindStr, map);
				}
			}, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
			
			timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE, "添加闹钟", timePickerDialog);
			timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, "取消闹钟", timePickerDialog);
			
			timePickerDialog.show();
			
			Button positiveBtn = timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE);
			if (positiveBtn != null)
				positiveBtn.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						timePickerDialog.dismiss();
						
						insertAlarmInDb(time[0], time[1]);
			            
						// 因为添加闹钟，所以刷新闹钟列表
						setAlarmList();
						
						MyGlobal.ALARM_CHANGE = true;
						
				        Intent foregroundServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
				        foregroundServiceIntent.putExtra("doSth", Const.NEW_ALRM_STATE);
				        startService(foregroundServiceIntent);
						
					}
				});
            break;
            
        }
		return super.onOptionsItemSelected(item);
	}
	
	int insertAlarmInDb(int hour, int mins) {
		
		int num = alarmSort(hour, mins);
		db.open();
		db.insertRow(hour, mins, "1 2 3 4 5 0 0", num , 0, true, "叫醒你起床的不是闹钟，而是梦想");
		db.close();
		
		return 0;
	}
	
	int alarmSort(int hour, int mins) {
		
		db.open();
		
		Cursor cursor = db.getAllRows();
		
		if (cursor.moveToFirst() == false) {
			return 0;
		}
		
		int alarmHourColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_HOUR);
		int alarmMinsColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_MINS);
		int keyRowIdColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ROWID);
		int numColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_NUM);
		
		int num_id = 0;
		
		int hour_compared = 0;
		int mins_compared = 0;
		
		for (cursor.moveToFirst(); ; cursor.moveToNext()) {
			int numInDb = cursor.getInt(numColumn);
			hour_compared = cursor.getInt(alarmHourColumn);
			mins_compared = cursor.getInt(alarmMinsColumn);
			if (hour_compared > hour) {
				Log.v("sort_num and num: ", numInDb+1 + "," + num_id);
				db.updateNum(cursor.getInt(keyRowIdColumn), numInDb+1);
			}
			else if (hour_compared == hour && mins_compared > mins) {
				Log.v("sort_num and num: ", numInDb+1 + "," + num_id);
				db.updateNum(cursor.getInt(keyRowIdColumn), numInDb+1);
			}
			else
				num_id++;
			if (cursor.isLast()) {
				break;
			}
		}

		db.close();
		
		return num_id;
	}
	
	
	String getWeekActiveDaysStr (String [] weeks) {
		String activeDays = "周";
		for (int i = 1; i <= 7 ;i++) {
			Log.v("the week num: " , "" + i);
			if (weeks[i-1].equals(""+i)) {
				activeDays += weekStr[i - 1] + " "; 
			} else {
				activeDays = activeDays + "<font color=\"#CCCCCC\">" + weekStr[i - 1] + "</font>" + " ";
			}
		}
		return activeDays;
	}
	
	
	public class MyAdapter extends BaseAdapter {
		
		LayoutInflater inflater;
	    public Context context;
	    public MyAdapter (Context c) {
	    	context = c;
	    	inflater = LayoutInflater.from(c);
	    }
	    
		class ListClickGroup {  
		    public Switch changeActive;
		    public TextView alarmTime;
		    public TextView upTimes;
		    public TextView activeDays;
		    public TextView tips;
		    public boolean active;
		    public int position;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size() - 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		String setTimeFormat (int hour, int mins) {
			return (hour < 10 ? "0" + hour : "" + hour) + ":" + (mins < 10 ? "0" + mins : "" + mins);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			position = position + 1;
			ListClickGroup clickViews = null;
			if(convertView != null) {
				clickViews = (ListClickGroup) convertView.getTag();
				Log.v("tag", "positon " + position + " convertView is not null, "  + clickViews);
			} else {
				clickViews = new ListClickGroup();
				convertView = inflater.inflate(R.layout.alarm_item, null);
				Log.v("tag", "positon " + position + " convertView is null, "  + convertView); 
				clickViews.alarmTime = (TextView) convertView.findViewById(R.id.alarmTime);
				clickViews.changeActive = (Switch) convertView.findViewById(R.id.changeActive);
				clickViews.activeDays = (TextView) convertView.findViewById(R.id.activeDays);
				clickViews.tips = (TextView) convertView.findViewById(R.id.tips);
				convertView.setTag(clickViews);
			}
		        
				clickViews.position = position;

				Log.v("tag", "positon " + position + " convertView is not null now, "  + convertView); 
				
				String[] weeks = listItems.get(position).get("alarmKind").toString().split(" ");
				String activeDaysStr = getWeekActiveDaysStr(weeks);
				clickViews.activeDays.setText(Html.fromHtml( activeDaysStr ));
				
				String welcomeStr = listItems.get(position).get("welcome").toString();
				clickViews.tips.setText(welcomeStr);
				
				Log.v("What the Fuck!", listItems.get(position).toString());
				
				String []time = listItems.get(position).get("alarmTime").toString().split(":");
				int hour = Integer.parseInt(time[0]);
				int mins = Integer.parseInt(time[1]);
				clickViews.alarmTime.setText(setTimeFormat(hour, mins));
				
				if (listItems.get(position).get("activeBool").toString().equals("1")) {
					clickViews.changeActive.setChecked(true);
				} else {
					clickViews.changeActive.setChecked(false);
				}
				
				final int listPos = position;
				
				OnCheckedChangeListener activeListener = new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub

						db.open();
						
						if ( isChecked ) {
							Log.v("FUCK STR!", listItems.get(listPos).get("activeBool").toString());
							db.enableRow(Integer.parseInt(listItems.get(listPos).get("rowID").toString()));
							listItems.get(listPos).put("activeBool", 1);
							buttonView.setChecked(isChecked);
							setAlarmList();
						} else {
							Log.v("FUCK STR!", listItems.get(listPos).get("activeBool").toString());
							db.disableRow(Integer.parseInt(listItems.get(listPos).get("rowID").toString()));
							listItems.get(listPos).put("activeBool", 0);
							buttonView.setChecked(isChecked);
							setAlarmList();
						}

						db.close();
						
						MyGlobal.ALARM_CHANGE = true;
						
				        Intent foregroundServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
				        foregroundServiceIntent.putExtra("doSth", Const.CHANGE_STATE);
				        startService(foregroundServiceIntent);
				        
					}
					
				};
				
				clickViews.changeActive.setOnCheckedChangeListener(activeListener);
			
			return convertView;

		}
		
	}
	
}

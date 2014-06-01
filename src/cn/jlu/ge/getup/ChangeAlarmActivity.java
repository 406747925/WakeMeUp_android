package cn.jlu.ge.getup;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import cn.jlu.ge.getup.tools.AlarmDBAdapter;
import cn.jlu.ge.getup.tools.ForegroundService;

public class ChangeAlarmActivity extends Activity {

	private TimePicker timePicker;
	private String alarmTimeStr;
	private String rowID;
	private String alarmKindStr;
	private String welcomeStr;
	private DayBtn[] btnGroup;
	private AlarmDBAdapter db;
	private HashMap<Integer, Integer> weeksMap;
	Calendar calendar;
	String[] time;
	
	EditText editWelcome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_alarm);
		
		// 数据库初始化
		db = new AlarmDBAdapter(this);
		
		setWeeksMap();

		Intent getIntent = getIntent();
        Bundle getBundle = getIntent.getExtras();
        alarmTimeStr = getBundle.getString("alarmTime");
        rowID = getBundle.getString("rowID");
        alarmKindStr = getBundle.getString("alarmKind");
        welcomeStr = getBundle.getString("welcome");
        time = alarmTimeStr.split(":");
        
        setAlarmDay();
        
		timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Integer.parseInt(time[0]));
        timePicker.setCurrentMinute(Integer.parseInt(time[1]));
        
        editWelcome = (EditText) findViewById(R.id.welcomeText);
        editWelcome.setText(welcomeStr);
        
        TextView choiceSong = (TextView) findViewById(R.id.alarmSongText);
        choiceSong.setText("铃声:早上好");
        
        
        
	}
	
	int setTimePicker() {
		
		return 0;
	}
	
	int setDayBtnGroup() {
		
		try {
			btnGroup = new DayBtn[7];
	        btnGroup[0] = new DayBtn(R.id.monday, 1);
	        btnGroup[1] = new DayBtn(R.id.tuesday, 2);
	        btnGroup[2] = new DayBtn(R.id.wednesday, 3);
	        btnGroup[3] = new DayBtn(R.id.thursday, 4);
	        btnGroup[4] = new DayBtn(R.id.friday, 5);
	        btnGroup[5] = new DayBtn(R.id.saturday, 6);
	        btnGroup[6] = new DayBtn(R.id.sunday, 7);
		} catch (Exception e) {
			Log.v("exception", String.valueOf(e));
		}
        
        return 0;
	}
	
	public class DayBtn {
		public Button dayBtn;
		
		public DayBtn (int id, int day) {
			setViewById(id);
			setListenerByDay(day);
		}
		
		int setViewById(int id) {
			dayBtn = (Button) findViewById(id);
			return 0;
		}
		
		int setListenerByDay(final int day) {
			
			dayBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (alarmKindStr.indexOf(String.valueOf(day)) != -1) {
						
						v.setBackgroundResource(R.drawable.disable_button_shape_circle);
						alarmKindStr = alarmKindStr.replace(String.valueOf(day), "0");
						dayBtn.setTextColor(Color.parseColor("#2087FC"));
						Log.v("weekDayButton", "disable a week day button: " + String.valueOf(day) + ";" + alarmKindStr);
					
					} else {
						
						v.setBackgroundResource(R.drawable.enable_button_shape_circle);
						if (day <= 6)
							alarmKindStr = new StringBuilder(alarmKindStr).replace(day*2 - 2, day*2 - 1, String.valueOf(day)).toString();
						else
							alarmKindStr = new StringBuilder(alarmKindStr).replace(alarmKindStr.length() - 1, alarmKindStr.length(), String.valueOf(day)).toString();
						dayBtn.setTextColor(Color.parseColor("#FFFFFF"));
						Log.v("weekDayButton", "enable a week day button： " + String.valueOf(day) + ";" + alarmKindStr);
						
					}
				}
			});
			
			return 0;
		}
	}
	
	int setWeeksMap() {
		
		weeksMap = new HashMap<Integer, Integer>();
		weeksMap.put(R.id.monday, 1);
		weeksMap.put(R.id.tuesday, 2);
		weeksMap.put(R.id.wednesday, 3);
		weeksMap.put(R.id.thursday, 4);
		weeksMap.put(R.id.friday, 5);
		weeksMap.put(R.id.saturday, 6);
		weeksMap.put(R.id.sunday, 7);
		
		return 0;
		
	}
	
	boolean setAlarmDay() {
		
		setDayBtnGroup();
		
		String[] weeks = alarmKindStr.split(" ");
		
		for (int i = 1; i <= 7 ;i++) {
			if (weeks[i-1].equals(String.valueOf(i))) {
				btnGroup[i-1].dayBtn.setBackgroundResource(R.drawable.enable_button_shape_circle);
				btnGroup[i-1].dayBtn.setTextColor(Color.parseColor("#FFFFFF"));
			} else {
				btnGroup[i-1].dayBtn.setBackgroundResource(R.drawable.disable_button_shape_circle);
				btnGroup[i-1].dayBtn.setTextColor(Color.parseColor("#2087FC"));
			}
		}
		
		return true;
	}
	
	boolean saveAlarmSet() {
		
    	db.open();
    	// TODO save the data in db.
    	Log.v("todo", "save the data in db." + alarmKindStr);
    	alarmTimeStr = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
    	welcomeStr = editWelcome.getText().toString();
    	db.updateRowKind(Long.parseLong(rowID), alarmTimeStr, alarmKindStr, welcomeStr);
    	db.close();
		
    	// 闹钟状态修改，反馈程序进行重新定闹钟
		ForegroundService.ALARM_CHANGE_STATE = 0;
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        foregroundServiceIntent.putExtra("doSth",ForegroundService.CHANGE_STATE);
        startService(foregroundServiceIntent);
		
		// 退出
		this.finish();
		
    	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// TODO Auto-generated method stub
        switch(item.getItemId()){ 
        case R.id.change_alarm:
        	saveAlarmSet();
            break;
        }
        
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.change_alarm, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
}

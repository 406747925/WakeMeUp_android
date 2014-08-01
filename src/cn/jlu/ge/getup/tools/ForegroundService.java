package cn.jlu.ge.getup.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import cn.jlu.ge.getup.MainActivity;
import cn.jlu.ge.getup.R;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    
    private static int DEFAULT_WEATHER_CITY_FLAG = 1;
    
    private boolean mReflectFlg = false;
    
    private static final int NOTIFICATION_ID = 1;
    private static final Class<?>[] mSetForegroundSignature = new Class[] {
        boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[] {
        boolean.class};

    private NotificationManager mNM;  
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    private Notification notification;
    
    private AlarmDBAdapter db;
    private UserDataDBAdapter userDatadb;
    private Calendar calendar;
	int weekNum = -1;
	int alarmHourColumn;
	int alarmMinsColumn;
	int alarmKindColumn;
	int activeColumn;
	int upTimesColumn;
	int welcomeColumn;
	
	Date nowDate;
	String weatherUrl;
	String dateStr;
	
	AlarmManager alarmManager;
	private AsyncHttpClient client;
	private SharedPreferences appInfo;
	
	int netCondiction;
	
	boolean weatherNeedUpdateOrNot = true;
	
	String weatherCity;
	String tempStr;
	int windInt;
	int wetInt;
	String wdInt;
	int wse;
	int temp1;
	int temp2;
	String weatherStr;
	String ptimeStr;
	
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        
        db = new AlarmDBAdapter(this);
        
        setDBColumn();
        
        calendar = Calendar.getInstance();
        handler.post(alarmUpdateThread);
        
        //
        mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            mStartForeground = ForegroundService.class.getMethod("startForeground", mStartForegroundSignature);  
            mStopForeground = ForegroundService.class.getMethod("stopForeground", mStopForegroundSignature);  
        } catch (NoSuchMethodException e) {
            mStartForeground = mStopForeground = null;
        }
        
        try {
            mSetForeground = getClass().getMethod("setForeground",
                    mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "OS doesn't have Service.startForeground OR Service.setForeground!");
        }

        setNotificationAndAlarm();
        setAppInfoPreference();
        
    }  
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");
        
        if ( intent == null ) return START_STICKY;

        String doWhatStr = intent.getStringExtra("doSth");
        if (doWhatStr == null) {
        	Log.v("None", "NO STATE Wrong Start.");
        }
        else if (doWhatStr.equals(Const.CREATE_STATE)) {

			Log.v("Create State", "Create State.");
			
			setNotificationAndAlarm();
			MyGlobal.ALARM_CHANGE = false;
			
			setAppInfoPreference();
        } else if (doWhatStr.equals(Const.NEW_ALRM_STATE)) {
        	
			Log.v("New State", "A New Alarm Insert.");
			
			setNotificationAndAlarm();
			MyGlobal.ALARM_CHANGE = false;
			
        } else if (doWhatStr.equals(Const.CHANGE_STATE)) {
        	
			Log.v("Change State", "Having Some Change.");

			setNotificationAndAlarm();
			MyGlobal.ALARM_CHANGE = false;
			
        } else if (doWhatStr.equals(Const.SHOW_NEXT_ALARM)) {
        	
        	Log.v("None State", "Nothing yet.");

			setNotificationAndAlarm();	
			MyGlobal.ALARM_CHANGE = false;

        } else if (doWhatStr.equals(Const.UPDATE_WEATHER)) {
        	Log.v("Weather Update State", "Need Update Weather Information.");
        	setWeatherInfoData();
        }
        
        return START_STICKY;
    }
    
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }

    @Override  
    public void onDestroy() {
    	
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        
        Intent localIntent = new Intent();
        localIntent.setClass(this, ForegroundService.class);
        this.startService(localIntent);
    }
    
    void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            // Should not happen.
            Log.w("ApiDemos", "Unable to invoke method", e);
        } catch (IllegalAccessException e) {
            // Should not happen.
            Log.w("ApiDemos", "Unable to invoke method", e);
        }
    }
    
    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        if (mReflectFlg) {
            // If we have the new startForeground API, then use it.
            if (mStartForeground != null) {
                mStartForegroundArgs[0] = Integer.valueOf(id);
                mStartForegroundArgs[1] = notification;
                invokeMethod(mStartForeground, mStartForegroundArgs);
                return;
            }
    
            // Fall back on the old API.
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNM.notify(id, notification);
        } else {
            
            if(VERSION.SDK_INT >= 5) {
                startForeground(id, notification);
            } else {
                // Fall back on the old API.
                mSetForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
                mNM.notify(id, notification);    
            }
        }
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        if (mReflectFlg) {
            // If we have the new stopForeground API, then use it.
            if (mStopForeground != null) {
                mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mStopForeground, mStopForegroundArgs);
                return;
            }
    
            // Fall back on the old API.  Note to cancel BEFORE changing the
            // foreground state, since we could be killed at that point.
            mNM.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        } else {
            
            if(VERSION.SDK_INT >= 5) {
                stopForeground(true);
            } else {
                // Fall back on the old API.  Note to cancel BEFORE changing the
                // foreground state, since we could be killed at that point.
                mNM.cancel(id);
                mSetForegroundArgs[0] = Boolean.FALSE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
            }
        }
    }
    
    void setAppInfoPreference () {
    	
    	userDatadb = new UserDataDBAdapter(getApplicationContext());
		setWeatherInfoData();

		userDatadb.close();

    }
    
    void setNotificationAndAlarm() {
    	
    	String []stateBarStr = checkRecentAlarmAndSetIt();
    	Log.v("Next Alarm State", Const.SHOW_NEXT_ALARM);
    	
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  
                new Intent(this, MainActivity.class), 0); 
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.clock);
        builder.setTicker("小闹在这里");
        builder.setContentTitle(stateBarStr[0]);
        builder.setContentText(stateBarStr[1]);
    	notification = builder.getNotification();
        
        startForegroundCompat(NOTIFICATION_ID, notification);
        
    }
    
	void setWeatherInfoData() {
		
        userDatadb.open();
        
        Cursor cursor = userDatadb.getAllWeatherCitiesDatas();
        
        if ( cursor != null && cursor.getCount() != 0 ) {
        	
        	cursor.moveToFirst();
        	
        	// 初始化显示天气预报的城市信息
            if ( cursor.getInt( cursor.getColumnIndex( UserDataDBAdapter.KEY_DATA_COUNT ) ) == DEFAULT_WEATHER_CITY_FLAG ) {
                weatherCity = cursor.getString(cursor.getColumnIndex(UserDataDBAdapter.KEY_DATA_CONTENT));
                weatherUrl = cursor.getString(cursor.getColumnIndex(UserDataDBAdapter.KEY_DATA_UNIT));
                
                appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
                SharedPreferences.Editor appInfoEditor = appInfo.edit();
                appInfoEditor.putString(Const.FIRST_CITY_KEY, weatherCity);
                appInfoEditor.putString(Const.FIRST_CITY_URL_KEY, weatherUrl);
                appInfoEditor.commit();
                
                // 存在城市的设置则初始化天气信息
                getWeatherFromNet(weatherUrl);
                
                appInfoEditor = null;
                
            }
        }
        
        if (cursor != null) cursor.close();
        
        userDatadb.close();
        
	}
    
    void setDBColumn() {
    	db.open();
    	Cursor cursor = db.getAllRows();
		alarmHourColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_HOUR);
		alarmMinsColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_MINS);
		alarmKindColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_KIND);
		activeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ACTIVE);
		upTimesColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_UP_TIMES);
		welcomeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_WELCOME);
		cursor.close();
		db.close();
    }
    
	void setWeatherData (JSONObject weatherObject) {

		try {
			String temp1Str = weatherObject.getString("temp1");
			String temp2Str = weatherObject.getString("temp2");
			weatherStr = weatherObject.getString("weather");
			ptimeStr = weatherObject.getString("ptime");
			
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			SharedPreferences.Editor appInfoEditor = appInfo.edit();
			appInfoEditor.putString(Const.WEATHER_DATE_KEY, dateStr);
			appInfoEditor.putString(Const.FIRST_PTIME_KEY, ptimeStr);
			appInfoEditor.putString(Const.FIRST_WEATHER_KEY, weatherStr);
			appInfoEditor.putString(Const.FIRST_DAY_TEMP_KEY, temp1Str + "~" + temp2Str);
			appInfoEditor.commit();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void setWeatherDetailData (JSONObject weatherDetailObject) {
		
		try {
			String pattern = "\\D*";
			tempStr = weatherDetailObject.getString("temp");
			windInt = Integer.parseInt(weatherDetailObject.getString("WS").replaceAll(pattern, ""));
			wetInt = Integer.parseInt(weatherDetailObject.getString("SD").replaceAll(pattern, ""));
			wdInt = weatherDetailObject.getString("WD");
			wse = weatherDetailObject.getInt("WSE");
	        
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			SharedPreferences.Editor appInfoEditor = appInfo.edit();
			appInfoEditor.putInt(Const.FIRST_WET_KEY, wetInt);
			appInfoEditor.putString(Const.FIRST_WD_KEY, wdInt);
			appInfoEditor.putInt(Const.FIRST_WS_KEY, windInt);
			appInfoEditor.putString(Const.FIRST_NOW_TEMP_KEY, tempStr);
			appInfoEditor.commit();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	void getWeatherFromNet(final String weatherUrl) {
		
		client = new AsyncHttpClient();
		
		client.get("http://www.weather.com.cn/data/cityinfo/" + weatherUrl + ".html", new AsyncHttpResponseHandler() {
            
			@Override
            public void onSuccess(String response) {
            	try {
            		
					JSONObject weatherObject = new JSONObject(response).getJSONObject("weatherinfo");
					setWeatherData(weatherObject);
					weatherNeedUpdateOrNot = false;
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

			@Override
			public void onFailure(Throwable throwable, String failureStr) {
				// TODO Auto-generated method stub
				
				super.onFailure(throwable, failureStr);
				weatherNeedUpdateOrNot = true;
			}
        });
        
        client.get("http://www.weather.com.cn/data/sk/" + weatherUrl + ".html", new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				weatherNeedUpdateOrNot = true;
			}

			@Override
			public void onSuccess(int code, String response) {
				// TODO Auto-generated method stub
				try {
					
					JSONObject weatherDetailObject = new JSONObject(response).getJSONObject("weatherinfo");
					setWeatherDetailData(weatherDetailObject);
					weatherNeedUpdateOrNot = false;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				super.onSuccess(code, response);
				
			}
        	
        });
        
        client = null;
        
	}
    
	// 定时更新闹钟,定时查看是否需要更新天气
	Handler handler = new Handler();
	Runnable alarmUpdateThread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(alarmUpdateThread, 9639);
			if ( MyGlobal.ALARM_CHANGE ) {
				setNotificationAndAlarm();
				MyGlobal.ALARM_CHANGE = false;
			}
			Date justNowDate = new Date();
			if ( nowDate != justNowDate ) {
				nowDate = justNowDate;
				justNowDate = null;
				SimpleDateFormat sdf = new SimpleDateFormat("M月 d日 EEEE");
				dateStr = sdf.format(nowDate);
				getWeatherFromNet(weatherUrl);
			}
			Log.v("ForegroundService", "Update the alarm.");
			
		}
		
	};
    

    
    // 
	boolean setAlarmOrNot (int hour, int mins, String kindStr, int upTimes) {
		
		int weekNumAlarm = -1;
		calendar = Calendar.getInstance();
		// 
		if (upTimes == 1) {
			return false;
		}

		// 否则如果是同一天，并且小于等于当前时间的 Alarm 则设置它在第二天触发
		// 方便闹钟测试，这里设置为允许当前时间设置为立即触发的闹钟
		/*************************************************************************
		 * 
		 * 此处为正确触发闹钟的判断条件
		 * calendar.get(Calendar.HOUR_OF_DAY) > hour || (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) >= mins)\
		 * 
		 *************************************************************************/
		else if (calendar.get(Calendar.HOUR_OF_DAY) > hour || (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) >= mins)) {
			if ( weekNum == 7 ) weekNumAlarm = 1;
			else weekNumAlarm = weekNum + 1;

			calendar.setTimeInMillis(System.currentTimeMillis() + 86400000);
		} else {
			Log.v("Second", "This is else");
			weekNumAlarm = weekNum;
			calendar.setTimeInMillis(System.currentTimeMillis());
		}
		
		Log.v("weekNumAlarm", "" + weekNumAlarm);
		Log.v("weekNumAlarm2", "" + kindStr.indexOf("" + weekNumAlarm));
		
		if (kindStr.indexOf("" + weekNumAlarm) != -1) {
			return true;
		} else {
			return false;
		}
		
	}
	

	int reSetAlarm(int hour, int mins, int rowId, String welcomeStr, int dayOffset) {
		
		calendar.setTimeInMillis(System.currentTimeMillis() + dayOffset * 86400 * 1000);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, mins);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Intent intent = new Intent(this,AlarmReceiver.class);
		
//		Log.v("ForegroundService", "" + rowId);
//		Log.v("ForegroundService", "" + welcomeStr);
//		intent.putExtra("rowId", rowId);
//		intent.putExtra("welcomeStr", welcomeStr);
		
		Toast.makeText(getApplicationContext(), ">>>> reSetAlarm:" + hour + ":" + mins, Toast.LENGTH_SHORT).show();
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        return 0;
	}
	
	String[] checkRecentAlarmAndSetIt() {
		
		// 
		db.open();
		Cursor cursor = db.getActiveRow();
		
		if (cursor.moveToFirst() == false) {
			cursor.close();
			db.close();
			String[] returnStr = {"小闹没得闹啦", "小闹提醒,所有的闹钟已闹罄T-T"};
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			SharedPreferences.Editor appInfoEditor = appInfo.edit();
			appInfoEditor.putString(Const.NEXT_ALARM_TIME_KEY, Const.NEXT_ALARM_TIME_DEFAULT);
			appInfoEditor.putString(Const.NEXT_ALARM_DESC_KEY, Const.NEXT_ALARM_DESC_DEFAULT);
			appInfoEditor.commit();
			
			appInfo = null;
			appInfoEditor = null;
			
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			
			return returnStr;
		}
		
		int alarmHourColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_HOUR);
		int alarmMinsColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_MINS);
//		int numColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_NUM);
		int welcomeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_WELCOME);
		int alarmKindColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_KIND);
		
		String welcomeStr = "";
		String alarmKindStr = "";
		
		Calendar calendar = Calendar.getInstance();
		
		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
		int nowMins = calendar.get(Calendar.MINUTE);
		
		int comparedHour = 0;
		int comparedMins = 0;
		
		int subCompareMins = 8400;
		String minAlarmTimeStr = "";
		String minWelcomeStr = "";
		
		int tomorrowAlarmMins = 8400;
		String nextDayAlarmTimeStr = "";
		String nextDayWelcomeStr = "";
		
		int weekNum = setWeekNum();
		boolean todayOrNot = true;
		int minRowId = 0;
		int nextAlarmWeekDay = 0;
		int nextDayRowId = 0;
		
		for (cursor.moveToFirst(); ; cursor.moveToNext()) {
			
			welcomeStr = cursor.getString(welcomeColumn);
			alarmKindStr = cursor.getString(alarmKindColumn);
			
			comparedHour = cursor.getInt(alarmHourColumn);
			comparedMins = cursor.getInt(alarmMinsColumn);
			
			if (alarmKindStr.indexOf("" + weekNum) == -1) {
				
				todayOrNot = false;
				Log.v("Recent Alarm", "alarm time : " + comparedHour + ":" + comparedMins + " ; alarm kind :" + alarmKindStr + " ; today week: " + weekNum);
				
			} else {
				Log.v("Recent Alarm", "alarm time : " + comparedHour + ":" + comparedMins + " ; alarm kind :" + alarmKindStr + " ; today week: " + weekNum);
				todayOrNot = true;
			}
			
			if (todayOrNot) {
				
				if (comparedHour < nowHour || (comparedHour == nowHour && comparedMins < nowMins)) {

				} else {
					
					if ( (((comparedHour - nowHour) * 60 + (comparedMins - nowMins)) < subCompareMins) && 
							(((comparedHour - nowHour) * 60 + (comparedMins - nowMins)) > 0) ) {
						
						subCompareMins = ((comparedHour - nowHour)*60 + (comparedMins - nowMins));
						minAlarmTimeStr = comparedHour + ":" + comparedMins;
						minWelcomeStr = welcomeStr;
						minRowId = cursor.getInt(cursor.getColumnIndex(AlarmDBAdapter.KEY_ROWID));
					}
				}
			}
			
			int compareDay = getAlarmWeekDay(weekNum, alarmKindStr);
			
			if ( compareDay != -1 ) {
				
				Log.v("Day Alarm", "compare day week num: " + compareDay);
				nextAlarmWeekDay = getLaterAlarm(weekNum, nextAlarmWeekDay, compareDay);
				Log.v("Day Alarm", "next day week num: " + nextAlarmWeekDay);
				if (compareDay == nextAlarmWeekDay) {
					if ( ((comparedHour) * 60 + comparedMins) < tomorrowAlarmMins ) {
						Log.v("nextDayAlarm", "next day alarm time: " + comparedHour + ":" + comparedMins);
						tomorrowAlarmMins = (comparedHour)*60 + comparedMins;
						nextDayAlarmTimeStr = comparedHour + ":" + comparedMins;
						nextDayWelcomeStr = welcomeStr;
						nextDayRowId = cursor.getInt(cursor.getColumnIndex(AlarmDBAdapter.KEY_ROWID));
					}
				}
			}
			
			if (cursor.isLast()) {
				
				break;
				
			}
		}

		db.close();

		if ( subCompareMins != 8400 ) {
			
			String time[] = minAlarmTimeStr.split(":");
			int mins = Integer.parseInt(time[1]);
			int hour = Integer.parseInt(time[0]);
			minAlarmTimeStr = setTimeFormat(hour, mins);
			reSetAlarm(hour, mins, minRowId, welcomeStr, 0);
			String[] returnStr = {"下个闹钟：" + minAlarmTimeStr, "小闹提醒," + minWelcomeStr};
			
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			SharedPreferences.Editor appInfoEditor = appInfo.edit();
			appInfoEditor.putString(Const.NEXT_ALARM_DESC_KEY, "下个闹钟");
			appInfoEditor.putString(Const.NEXT_ALARM_TIME_KEY, minAlarmTimeStr);
			appInfoEditor.putString(Const.WELCOME_STR_KEY, minWelcomeStr);
			appInfoEditor.commit();
			
			appInfo = null;
			appInfoEditor = null;
			return returnStr;
			
		} else if ( tomorrowAlarmMins != 8400 ) {
			
			String time[] = nextDayAlarmTimeStr.split(":");
			int mins = Integer.parseInt(time[1]);
			int hour = Integer.parseInt(time[0]);
			nextDayAlarmTimeStr = setTimeFormat(hour, mins);
			String nextAlarmDescStr = NextWeekOrNot(weekNum, nextAlarmWeekDay);
			reSetAlarm(hour, mins, minRowId, nextDayWelcomeStr, getDayOffset(weekNum, nextAlarmWeekDay) );
			String[] returnStr = { nextAlarmDescStr + nextDayAlarmTimeStr, "小闹提醒," + nextDayWelcomeStr};
			
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			SharedPreferences.Editor appInfoEditor = appInfo.edit();
			appInfoEditor.putString(Const.NEXT_ALARM_DESC_KEY, nextAlarmDescStr);
			appInfoEditor.putString(Const.NEXT_ALARM_TIME_KEY, nextDayAlarmTimeStr);
			appInfoEditor.putString(Const.WELCOME_STR_KEY, nextDayWelcomeStr);
			appInfoEditor.commit();
			
			appInfo = null;
			appInfoEditor = null;
			
			return returnStr;
		}
		
		else {
			
			String[] returnStr = {"小闹没得闹啦", "小闹提醒,所有的闹钟已闹罄T-T"};
			appInfo = getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS);
			SharedPreferences.Editor appInfoEditor = appInfo.edit();
			appInfoEditor.putString(Const.NEXT_ALARM_TIME_KEY, Const.NEXT_ALARM_TIME_DEFAULT);
			appInfoEditor.putString(Const.NEXT_ALARM_DESC_KEY, Const.NEXT_ALARM_DESC_DEFAULT);
			appInfoEditor.commit();
			
			appInfo = null;
			appInfoEditor = null;
			
			return returnStr;
			
		}
	}
	
	String setTimeFormat (int hour, int mins) {
		return (hour < 10 ? "0" + hour : "" + hour) + ":" + (mins < 10 ? "0" + mins : "" + mins);
	}
	
	int setWeekNum () {
		calendar = Calendar.getInstance();
		weekNum = calendar.get(Calendar.DAY_OF_WEEK);

		if ( weekNum == 1) {
			weekNum = 7;
		} else {
			weekNum--;
		}
		return weekNum;
	}
	
	int getAlarmWeekDay(int weekNum, String alarmKindStr) {
		for (int i = 0;i < 8; i++ ) {
			if (weekNum != 7) {
				weekNum++;
			} else {
				weekNum = 1;
			}
			if (alarmKindStr.indexOf("" + weekNum) != -1) {
				return weekNum;
			}
		}
		return -1;
	}
	
	int getLaterAlarm (int weekNum, int nextAlarmDay, int compareDay) {
		int subPre = nextAlarmDay - weekNum;
		int subThis = compareDay - weekNum;

		if ( 0 == nextAlarmDay ) {
			return compareDay;
		}
		if ( subPre * subThis >= 0 ) {
			return subPre < subThis ? nextAlarmDay : compareDay;
		} else {
			return subPre < subThis ? compareDay : nextAlarmDay;
		}

	}
	
	int getDayOffset (int weekNum, int alarmWeekDay) {
		return weekNum < alarmWeekDay ? alarmWeekDay - weekNum : alarmWeekDay + 7 - weekNum;
	}
	
	String NextWeekOrNot (int weekNum, int alarmWeekDay) {
		return weekNum < alarmWeekDay ? ("下个闹钟" + ( (alarmWeekDay - weekNum) == 1 ? "在明天:" : (alarmWeekDay - weekNum) + "天后:" ) ) : ("下个闹钟,下周" + alarmWeekDay + ":") ;
	}
	
//	boolean addAllAlarmsToAM() {
//	calendar = Calendar.getInstance();
//	setWeekNum();
//	setAllAlarm();
//	return true;
//}
//
//boolean cancelAlarmsFromAM () {
//	
//	Intent intent = new Intent(this,AlarmReceiver.class);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//    alarmManager.cancel(pendingIntent);
//	return true;
//	
//}
	
//	boolean setAllAlarm() {
//		
//		db.open();
//		Cursor cursor = db.getActiveRow();
//		if (cursor.moveToFirst() == false) {
//			cursor.close();
//			db.close();
//			return false;
//		}
//		
//		for (cursor.moveToFirst(); !cursor.isLast(); cursor.moveToNext()) {
//			doReSetAlarm(cursor, alarmKindColumn, activeColumn, alarmTimeColumn, upTimesColumn, welcomeColumn);
//		}
//
//		doReSetAlarm(cursor, alarmKindColumn, activeColumn, alarmTimeColumn, upTimesColumn, welcomeColumn);
//		
//		db.close();
//		return true;
//	}
	
//	// �������е�Alarm���ж��Ƿ���Ҫ����
//	int doReSetAlarm(Cursor cursor, int kindColumn, int activeColumn, int alarmTimeColumn, int upTimesColumn, int welcomeColumn) {
//		
//		String kindStr = cursor.getString(kindColumn);
//		int activeBool = cursor.getInt(activeColumn);
//		String alarmTimeStr = cursor.getString(alarmTimeColumn);
//		int upTimes = cursor.getInt(upTimesColumn);
//		String welcomeStr = cursor.getString(welcomeColumn);
//		int rowId = cursor.getPosition();
//		
//		Log.v("kindStr: ", kindStr);
//		Log.v("activeBool: ", "" + activeBool);
//		Log.v("alarmTimeStr: ", alarmTimeStr);
//		Log.v("week", "" + weekNum);
//		Log.v("upTimes", "" + upTimes);
//		Log.v("welcome", welcomeStr);
//		
//		String[] time = alarmTimeStr.split(":");
//		int hour = Integer.parseInt(time[0]);
//		int mins = Integer.parseInt(time[1]);
//		
//		// �������û�б�ȡ����������Ҫ�ڽ�������
//		if (activeBool == 1 && setAlarmOrNot(hour, mins, kindStr, upTimes)) {
//			reSetAlarm(hour , mins, rowId, welcomeStr);
//		}
//		// ����������
//		else {
//			Toast.makeText(getApplicationContext(), "Needn't ReSet Alarm: " + alarmTimeStr, Toast.LENGTH_SHORT).show();				
//		}
//		
//		return 0;
//		
//	}
	
	
//	String timeReturn (Cursor cursor) {
//		String timeReturnStr = "";
//		String alarmTimeStr = cursor.getString(alarmTimeColumn);
//		String kindStr = cursor.getString(alarmKindColumn);
//		int activeBool = cursor.getInt(activeColumn);
//		int upTimes = cursor.getInt(upTimesColumn);
//		
//		String[] time = alarmTimeStr.split(":");
//		int hour = Integer.parseInt(time[0]);
//		int mins = Integer.parseInt(time[1]);
//		
//		if (activeBool == 1 && setAlarmOrNot(hour,mins, kindStr, upTimes)) {
//			
//		}
//		return timeReturnStr;
//	}
	

}
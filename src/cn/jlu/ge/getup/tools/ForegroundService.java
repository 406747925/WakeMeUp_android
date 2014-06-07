package cn.jlu.ge.getup.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import cn.jlu.ge.getup.MainActivity;
import cn.jlu.ge.getup.R;
import cn.jlu.ge.getup.WakeUpActivity;

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    
    public static final String CREATE_STATE = "Create_MainActivity";
    public static final String NEW_ALRM_STATE = "New_Alarm_SetAlarmActivity";
    public static final String CHANGE_STATE = "Change_State";
    public static final String SHOW_NEXT_ALARM = "Next_Alarm_State";
    
    public static int ALARM_CHANGE_STATE = 0;
    
    private boolean mReflectFlg = false;
    
    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
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
	int alarmTimeColumn;
	int alarmKindColumn;
	int activeColumn;
	int upTimesColumn;
	int welcomeColumn;

	AlarmManager alarmManager;
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        
        // 数据初始化
        
        db = new AlarmDBAdapter(this);
        
        setDBColumn();
        
        calendar = Calendar.getInstance();
        handler.post(alarmUpdateThread);
        
        // 启动服务
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
    }  
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");
        String doWhatStr = intent.getStringExtra("doSth");
        if (doWhatStr == null) {
        	Log.v("None", "NO STATE， Wrong Start.");
        }
        else if (doWhatStr.equals(CREATE_STATE)) {
			if (ALARM_CHANGE_STATE != 1) {
				
				Log.v("Create State", "Create State.");
//				cancelAlarmsFromAM ();
//				addAllAlarmsToAM ();
				setNotificationAndAlarm();
				
			}
			
			ALARM_CHANGE_STATE = 1;
        } else if (doWhatStr.equals(NEW_ALRM_STATE)) {
			if (ALARM_CHANGE_STATE != 1) {
				
				Log.v("New State", "A New Alarm Insert.");
//				cancelAlarmsFromAM ();
//				addAllAlarmsToAM ();
				setNotificationAndAlarm();
				
			}
			
			ALARM_CHANGE_STATE = 1;
        } else if (doWhatStr.equals(CHANGE_STATE)) {
			if (ALARM_CHANGE_STATE != 1) {
				
				Log.v("Change State", "Having Some Change.");
//				cancelAlarmsFromAM ();
//				addAllAlarmsToAM ();
				setNotificationAndAlarm();
				
			}
			
			ALARM_CHANGE_STATE = 1;
        } else if (doWhatStr.equals(SHOW_NEXT_ALARM)) {
        	
        	setNotificationAndAlarm();
        	
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
        localIntent.setClass(this, ForegroundService.class); // 销毁时重新启动Service
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
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法startForeground设置前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground设置前台运行 */
            
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
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法stopForeground停止前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground停止前台运行 */
            
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
    
    void setNotificationAndAlarm() {
    	
    	String []stateBarStr = checkRecentAlarmAndSetIt();
    	Log.v("Next Alarm State", SHOW_NEXT_ALARM);
    	
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
    
    void setDBColumn() {
    	db.open();
    	Cursor cursor = db.getAllRows();
		alarmTimeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_TIME);
		alarmKindColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_KIND);
		activeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ACTIVE);
		upTimesColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_UP_TIMES);
		welcomeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_WELCOME);
		cursor.close();
		db.close();
    }
    
	// 刷新时间
	Handler handler = new Handler();
	Runnable alarmUpdateThread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(alarmUpdateThread, 9633);
			if (ALARM_CHANGE_STATE != 1) {
//				cancelAlarmsFromAM ();
//				addAllAlarmsToAM ();
				
				setNotificationAndAlarm();

			}
			
			ALARM_CHANGE_STATE = 1;
		}
		
	};
    

    
    // 判断是否应该设置Alarm
	boolean setAlarmOrNot (int hour, int mins, String kindStr, int upTimes) {
		
		int weekNumAlarm = -1;
		calendar = Calendar.getInstance();
		// 如果当前已经响铃过的闹钟，则不进行重新设置
		if (upTimes == 1) {
			Toast.makeText(getApplicationContext(), hour + ":" + mins + " uptimes: " + upTimes, Toast.LENGTH_SHORT).show();
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
			// 一天 86400000 毫秒
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
	
	// 重新设置闹钟，设置广播
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
		
		// 查找离当前时间最近的闹钟
		db.open();
		Cursor cursor = db.getActiveRow();
		
		if (cursor.moveToFirst() == false) {
			cursor.close();
			db.close();
			String[] returnStr = {"小闹没得闹啦", "小闹提醒,今天的闹钟已售罄T-T"};
			return returnStr;
		}
		
		alarmTimeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_ALARM_TIME);
		int numColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_NUM);
		int welcomeColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_WELCOME);
		int alarmKindColumn = cursor.getColumnIndex(AlarmDBAdapter.KEY_KIND);
		
		String alarmTimeStr = "";
		String welcomeStr = "";
		String alarmKindStr = "";
		
		Calendar calendar = Calendar.getInstance();
		
		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
		int nowMins = calendar.get(Calendar.MINUTE);
		
		Toast.makeText(getApplicationContext(), "Now Time: " + nowHour + ":" + nowMins, Toast.LENGTH_SHORT).show();
		
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
			
			alarmTimeStr = cursor.getString(alarmTimeColumn);
			welcomeStr = cursor.getString(welcomeColumn);
			alarmKindStr = cursor.getString(alarmKindColumn);
			
			String[] time = alarmTimeStr.split(":");
			comparedHour = Integer.parseInt(time[0]);
			comparedMins = Integer.parseInt(time[1]);
			
			if (alarmKindStr.indexOf("" + weekNum) == -1) {
				
				todayOrNot = false;
				Log.v("Recent Alarm", "alarm time : " + alarmTimeStr + " ; alarm kind :" + alarmKindStr + " ; today week: " + weekNum);
				
			} else {
				Log.v("Recent Alarm", "alarm time : " + alarmTimeStr + " ; alarm kind :" + alarmKindStr + " ; today week: " + weekNum);
				todayOrNot = true;
			}
			
			if (todayOrNot) {
				
				if (comparedHour < nowHour || (comparedHour == nowHour && comparedMins < nowMins)) {

				} else {
					
					if ( (((comparedHour - nowHour) * 60 + (comparedMins - nowMins)) < subCompareMins) && 
							(((comparedHour - nowHour) * 60 + (comparedMins - nowMins)) > 0) ) {
						
						subCompareMins = ((comparedHour - nowHour)*60 + (comparedMins - nowMins));
						minAlarmTimeStr = alarmTimeStr;
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
						Log.v("nextDayAlarm", "next day alarm time: " + alarmTimeStr);
						tomorrowAlarmMins = (comparedHour)*60 + comparedMins;
						nextDayAlarmTimeStr = alarmTimeStr;
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
		
		if (subCompareMins != 8400 ) {
			String time[] = minAlarmTimeStr.split(":");
			reSetAlarm(Integer.parseInt(time[0]), Integer.parseInt(time[1]), minRowId, welcomeStr, 0);
			String[] returnStr = {"下个闹钟" + minAlarmTimeStr, "小闹提醒," + minWelcomeStr};
			WakeUpActivity.welcomeStr = minWelcomeStr;
			return returnStr;
		} else if (tomorrowAlarmMins != 8400) {
			String time[] = nextDayAlarmTimeStr.split(":");
			reSetAlarm(Integer.parseInt(time[0]), Integer.parseInt(time[1]), minRowId, nextDayWelcomeStr, getDayOffset(weekNum, nextAlarmWeekDay) );
			String[] returnStr = { NextWeekOrNot(weekNum, nextAlarmWeekDay) + nextDayAlarmTimeStr, "小闹提醒," + nextDayWelcomeStr};
			WakeUpActivity.welcomeStr = nextDayWelcomeStr;
			return returnStr;
		}
		else {
			String[] returnStr = {"小闹没得闹啦", "小闹提醒,今天的闹钟已售罄T-T"};
			return returnStr;
		}
	}
	
	int setWeekNum () {
		calendar = Calendar.getInstance();
		weekNum = calendar.get(Calendar.DAY_OF_WEEK);
		// 当星期数为1时，应当是周日
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
		return weekNum < alarmWeekDay ? (alarmWeekDay - weekNum + "天后:") : ("下周" + alarmWeekDay + ":") ;
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
	
//	// 查找所有的Alarm并判断是否需要重置
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
//		// 如果闹钟没有被取消，并且需要在今天提醒
//		if (activeBool == 1 && setAlarmOrNot(hour, mins, kindStr, upTimes)) {
//			reSetAlarm(hour , mins, rowId, welcomeStr);
//		}
//		// 否则不做处理
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
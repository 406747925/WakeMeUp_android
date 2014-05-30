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

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    
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
    
    private DBAdapter db;
    private Calendar calendar;
	int weekNum = -1;
	int alarmTimeColumn;
	int alarmKindColumn;
	int activeColumn;
	int upTimesColumn;
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        
        // 数据初始化
        db = new DBAdapter(this);
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

        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  
                new Intent(this, MainActivity.class), 0); 
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.clock);
        builder.setTicker("同学,别闹了!");
        builder.setContentTitle("同学,别闹了!");
        builder.setContentText("同学,别闹了!快点起来学习啦!");
    	notification = builder.getNotification();
        
        startForegroundCompat(NOTIFICATION_ID, notification);
    }  
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");
        
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
    
	// 刷新时间
	Handler handler = new Handler();
	Runnable alarmUpdateThread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(alarmUpdateThread, 10000);
			if (ALARM_CHANGE_STATE != 1) {
				cancelAlarmsFromAM ();
				addAllAlarmsToAM ();
			}
			
			ALARM_CHANGE_STATE = 1;
		}
		
	};
    
	boolean addAllAlarmsToAM() {
		calendar = Calendar.getInstance();
		setWeekNum();
		setAllAlarm();
		return true;
	}
    
    boolean addOneAlarmToAM (int activeBool, int hour, int mins, String kindStr, int upTimes, int rowId) {
    	
		if (activeBool == 1 && setAlarmOrNot(hour, mins, kindStr, upTimes)) {
			Toast.makeText(getApplicationContext(), "reSetAlarm: " + hour + ":" + mins, Toast.LENGTH_SHORT).show();
			reSetAlarm(hour , mins, rowId);
		} else {
			Toast.makeText(getApplicationContext(), "Needn't ReSet Alarm: " + hour + ":" + mins, Toast.LENGTH_SHORT).show();				
		}
    	return true;
    	
    }
    
    boolean cancelAlarmsFromAM () {
		
		Intent intent = new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    	return true;
    	
    }
    
    
    // 判断是否应该设置Alarm
	boolean setAlarmOrNot (int hour, int mins, String kindStr, int upTimes) {
		
		int weekNumAlarm = -1;
		calendar = Calendar.getInstance();
		// 如果当前已经响铃过的闹钟，则不进行重新设置
		if (upTimes == 1) {
			return false;
		}
		// 否则如果是同一天，并且小于等于当前时间的 Alarm 则设置它在第二天触发
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
	
	boolean setAllAlarm() {
		
		db.open();
		Cursor cursor = db.getActiveRow();
		if (cursor.moveToFirst() == false) {
			cursor.close();
			db.close();
			return false;
		}
		
		alarmTimeColumn = cursor.getColumnIndex(DBAdapter.KEY_ALARM_TIME);
		alarmKindColumn = cursor.getColumnIndex(DBAdapter.KEY_KIND);
		activeColumn = cursor.getColumnIndex(DBAdapter.KEY_ACTIVE);
		upTimesColumn = cursor.getColumnIndex(DBAdapter.KEY_UP_TIMES);
		
		for (cursor.moveToFirst(); !cursor.isLast(); cursor.moveToNext()) {
			doReSetAlarm(cursor, alarmKindColumn, activeColumn, alarmTimeColumn, upTimesColumn);
		}

		doReSetAlarm(cursor, alarmKindColumn, activeColumn, alarmTimeColumn, upTimesColumn);
		
		db.close();
		return true;
	}
	
	// 查找所有的Alarm并判断是否需要重置
	int doReSetAlarm(Cursor cursor, int kindColumn, int activeColumn, int alarmTimeColumn, int upTimesColumn) {
		
		String kindStr = cursor.getString(kindColumn);
		int activeBool = cursor.getInt(activeColumn);
		String alarmTimeStr = cursor.getString(alarmTimeColumn);
		int upTimes = cursor.getInt(upTimesColumn);
		int rowId = cursor.getPosition();
		
		Log.v("kindStr: ", kindStr);
		Log.v("activeBool: ", "" + activeBool);
		Log.v("alarmTimeStr: ", alarmTimeStr);
		Log.v("week", "" + weekNum);
		Log.v("upTimes", "" + upTimes);

		String[] time = alarmTimeStr.split(":");
		int hour = Integer.parseInt(time[0]);
		int mins = Integer.parseInt(time[1]);
		
		// 如果闹钟没有被取消，并且需要在今天提醒
		if (activeBool == 1 && setAlarmOrNot(hour, mins, kindStr, upTimes)) {
			Toast.makeText(getApplicationContext(), "reSetAlarm: " + alarmTimeStr, Toast.LENGTH_SHORT).show();
			reSetAlarm(hour , mins, rowId);
		}
		// 否则不做处理
		else {
			Toast.makeText(getApplicationContext(), "Needn't ReSet Alarm: " + alarmTimeStr, Toast.LENGTH_SHORT).show();				
		}
		
		return 0;
	}
	
	// 重新设置闹钟，设置广播
	int reSetAlarm(int hour, int mins, int rowId) {

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, mins);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Intent intent = new Intent(this,AlarmReceiver.class);
		intent.putExtra("rowId", rowId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        startService(serviceIntent);
        
        return 0;
	}
	
	String checkRecentAlarm() {
		// 查找离当前时间最近的闹钟
		db.open();
		Cursor cursor = db.getActiveRow();
		
		alarmTimeColumn = cursor.getColumnIndex(DBAdapter.KEY_ALARM_TIME);
		alarmKindColumn = cursor.getColumnIndex(DBAdapter.KEY_KIND);
		activeColumn = cursor.getColumnIndex(DBAdapter.KEY_ACTIVE);
		
		for (cursor.moveToFirst(); !cursor.isLast(); cursor.moveToNext()) {
			
		}

		db.close();
		return "";
	}
	
	String timeReturn (Cursor cursor) {
		String timeReturnStr = "";
		String alarmTimeStr = cursor.getString(alarmTimeColumn);
		String kindStr = cursor.getString(alarmKindColumn);
		int activeBool = cursor.getInt(activeColumn);
		int upTimes = cursor.getInt(upTimesColumn);
		
		String[] time = alarmTimeStr.split(":");
		int hour = Integer.parseInt(time[0]);
		int mins = Integer.parseInt(time[1]);
		
		if (activeBool == 1 && setAlarmOrNot(hour,mins, kindStr, upTimes)) {
			
		}
		return timeReturnStr;
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

}
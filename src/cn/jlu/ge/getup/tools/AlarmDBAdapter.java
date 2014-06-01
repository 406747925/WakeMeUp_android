package cn.jlu.ge.getup.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlarmDBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_ALARM_TIME = "time";
	public static final String KEY_KIND = "kind";
	public static final String KEY_UP_TIMES = "up_times";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_NUM = "num";
	public static final String KEY_WELCOME = "welcome";
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "bnl";
	private static final String DATABASE_TABLE = "alarms";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = 
			"create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, " 
			+ "time text not null, kind text not null, num integer not null, " 
			+ "up_times integer not null, active integer not null default 'true', welcome text not null);";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db; 

	public AlarmDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion
			+ " to "
			+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
		
	}
	
	//打开数据库
	
	public AlarmDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	//关闭数据库
	
	public void close() {
		DBHelper.close();
	}
	
	//插入一个数据
	
	public long insertRow(String time, String kind, int num, int up_times, Boolean activeBool, String welcome) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ALARM_TIME, time);
		initialValues.put(KEY_KIND, kind);
		initialValues.put(KEY_NUM, num);
		initialValues.put(KEY_UP_TIMES, up_times);
		initialValues.put(KEY_ACTIVE, activeBool);
		initialValues.put(KEY_WELCOME, welcome);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	//删除一个指定数据
	
	public boolean deleteRow(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	//检索所有数据
	
	public Cursor getAllRows() {
		return db.query(DATABASE_TABLE, new String[] {
		KEY_ROWID,
		KEY_ALARM_TIME,
		KEY_KIND,
		KEY_NUM,
		KEY_UP_TIMES,
		KEY_ACTIVE,
		KEY_WELCOME},
		null,
		null,
		null,
		null,
		null,
		null);
	}

	// 查找激活的闹钟
	public Cursor getActiveRow() {
		Cursor mCursor =
		db.query(true, DATABASE_TABLE, new String[] {
		KEY_ROWID,
		KEY_ALARM_TIME,
		KEY_KIND,
		KEY_NUM,
		KEY_UP_TIMES,
		KEY_ACTIVE,
		KEY_WELCOME},
		KEY_ACTIVE + "=" + 1,
		null,
		null,
		null,
		null,
		null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	//---检索一个指定标题---
	
	public Cursor getRow(long rowId) throws SQLException {
		Cursor mCursor =
		db.query(true, DATABASE_TABLE, new String[] {
		KEY_ROWID,
		KEY_ALARM_TIME,
		KEY_KIND,
		KEY_NUM,
		KEY_UP_TIMES,
		KEY_ACTIVE,
		KEY_WELCOME},
		KEY_ROWID + "=" + rowId,
		null,
		null,
		null,
		null,
		null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}

	//---更新一个标题---
	
	public boolean updateRow(long rowId, String alarmTime, String alarmKind, int num, int upTimes, Boolean activeBool) {
		ContentValues args = new ContentValues();
		args.put(KEY_ALARM_TIME, alarmTime);
		args.put(KEY_KIND, alarmKind);
		args.put(KEY_NUM, num);
		args.put(KEY_UP_TIMES, upTimes);
		args.put(KEY_ACTIVE, activeBool);
		return db.update(DATABASE_TABLE, args,
		KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean updateRowKind(long rowId, String alarmTime, String alarmKind, String welcome) {
		ContentValues args = new ContentValues();
		args.put(KEY_ALARM_TIME, alarmTime);
		args.put(KEY_KIND, alarmKind);
		args.put(KEY_UP_TIMES, 0);
		args.put(KEY_WELCOME, welcome);
		return db.update(DATABASE_TABLE, args,
		KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean updateNum(long rowId, int num) {
		ContentValues args = new ContentValues();
		args.put(KEY_NUM, num);
		Log.v("RowId", "this: " + rowId);
		return db.update(DATABASE_TABLE, args,
				KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean setUpTimes (long rowId, int newUpTimes) {
		ContentValues args = new ContentValues();
		args.put(KEY_UP_TIMES, newUpTimes);
		Log.v("RowId", "this: " + rowId);
		return db.update(DATABASE_TABLE, args,
				KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean enableRow(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ACTIVE, 1);
		Log.v("rowId", "this:" + rowId);
		return db.update(DATABASE_TABLE, args,
				KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean disableRow(long rowId) {
		Log.v("rowId", "this:" + rowId);
		ContentValues args = new ContentValues();
		args.put(KEY_ACTIVE, 0);
		return db.update(DATABASE_TABLE, args,
				KEY_ROWID + "=" + rowId, null) > 0;
	}
	
}

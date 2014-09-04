package cn.jlu.ge.dreamclock.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CITY = "city";
	public static final String KEY_CITYID = "city_id";
	public static final String KEY_TMAX = "tmax";
	public static final String KEY_TMIN = "tmin";
	public static final String KEY_WEATHER = "weather";
	public static final String KEY_UPADATE_TIME = "update_time";
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "bnl_data";
	private static final String DATABASE_TABLE = "weather";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = 
			"create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, " 
			+ "time text not null, kind text not null, num integer not null, " 
			+ "up_times integer not null, active integer not null default 'true', welcome text not null);";
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public WeatherDBAdapter(Context c) {
		
		this.context = c;
		DBHelper = new DatabaseHelper(context);
		
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}
}

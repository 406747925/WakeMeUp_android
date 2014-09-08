package cn.jlu.ge.dreamclock.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDataDBAdapter {

	private static final String TAG = "UserDataDBAdapter";
	private static final String DATABASE_NAME = "bnl_user";
	private static final String DATABASE_USER_DATA_TABLE = "user_data";
	private static final String DATABASE_WEATHER_INFO_TABLE = "weather_data";
	private static final String DATABASE_USERS_SIGN_IN_TABLE = "users_sign_in";
	private static final int DATABASE_VERSION = 1;
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_DATA_TYPE = "data_type";
	public static final String KEY_DATA_CONTENT = "data_content";
	public static final String KEY_DATA_DESC = "data_desc";
	public static final String KEY_DATA_COUNT = "data_count";
	public static final String KEY_DATA_UNIT = "data_unit";
	
	public static final String KEY_CITY = "city";
	public static final String KEY_TEMP1 = "temp1";
	public static final String KEY_TEMP2 = "temp2";
	public static final String KEY_WEATHER = "weather";
	public static final String KEY_PTIME = "ptime";
	public static final String KEY_WD = "wd";
	public static final String KEY_WS = "ws";
	public static final String KEY_WSE = "wse";
	public static final String KEY_SD = "sd";
	public static final String KEY_INSERT_DATE = "insert_date";
	
	public static final String KEY_USER_ID = "_id";
	public static final String KEY_NICKNAME = "nickname";
	public static final String KEY_USER_RANK = "user_rank";
	public static final String KEY_USER_SIGN_IN_TIME = "user_sign_in_time";
	public static final String KEY_USER_JEER_OR_NOT = "jeer_or_not";
	public static final String KEY_USER_INFO = "user_info";
	public static final String KEY_USER_AVATAR_URL = "avatar_url";
	public static final String CREATE_USERS_SIGN_IN_TABLE = 
				"create table " + DATABASE_USERS_SIGN_IN_TABLE + "(_id text primary key, " + 
				KEY_USER_SIGN_IN_TIME + " text";
	
	private static final String CREATE_USER_DATA_TABLE = 
			"create table " + DATABASE_USER_DATA_TABLE + " (_id integer primary key autoincrement, " +
					"data_type text not null, " +
					"data_content text not null, " +
					"data_desc text not null, " +
					"data_count integer not null, " +
					"data_unit text not null);";
	
	private static final String CREATE_WEATHER_DATA_TABLE = 
			"create table " + DATABASE_WEATHER_INFO_TABLE + " (_id integer primary key autoincrement, " +
					"city text not null, " +
					"temp1 integer not null, " +
					"temp2 integer not null, " +
					"weather text not null, " +
					"ptime text not null, " +
					"wd text not null, " +
					"ws integer not null, " +
					"wse integer not null, " + 
					"sd integer not null, " +
					"insert_date DATETIME);";
	
	private Context context;
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase db;
	
	private String WEATHER_CITY_ADDED = "weather_city";
	
	
	public UserDataDBAdapter(Context c) {
		
		context = c;
		databaseHelper = new DatabaseHelper(context);
		
	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		public DatabaseHelper(Context context) {
			
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.v("DB Helper", "Create table.");
			db.execSQL(CREATE_USER_DATA_TABLE);
			db.execSQL(CREATE_WEATHER_DATA_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version " + oldVersion
			+ " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}
	
	
	public UserDataDBAdapter open() throws SQLException {
		db = databaseHelper.getWritableDatabase();
		return this;
	}
	
	
	public void close() {
		databaseHelper.close();
	}
	
	
	public Cursor getAllWeatherCitiesDatas() {
		
		Cursor mCursor = db.query(true, DATABASE_USER_DATA_TABLE, new String[] {
				KEY_ROWID, 
				KEY_DATA_TYPE,
				KEY_DATA_CONTENT,
				KEY_DATA_DESC,
				KEY_DATA_COUNT,
				KEY_DATA_UNIT
		}, 
		KEY_DATA_TYPE + "=" + "'" + WEATHER_CITY_ADDED + "'", 
		null,
		null,
		null,
		KEY_DATA_COUNT + " DESC",
		null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
		
	}
	
	
	public long insertWeatherCityData(String cityName, String cityPyName, String url, int count) {
		ContentValues values = new ContentValues();
		values.put(KEY_DATA_TYPE, WEATHER_CITY_ADDED);
		values.put(KEY_DATA_CONTENT, cityName);
		values.put(KEY_DATA_DESC, cityPyName);
		values.put(KEY_DATA_COUNT, count);
		values.put(KEY_DATA_UNIT, url);
		Log.v(TAG, "City: " + cityName + " ;" + "Url: " + url + " ;");
		return db.insert(DATABASE_USER_DATA_TABLE, null, values);
	}
	
	public boolean deleteWeatherCityByName(String name) {
		
		return db.delete(DATABASE_USER_DATA_TABLE, KEY_DATA_CONTENT + "= '" + name + "'", null) > 0;
		
	}
	
	
	public long setWeatherCityDefaulted ( String name ) {
		long otherCitiesNum = setOtherWeatherCitiesCommon ();
		ContentValues defaultValues = new ContentValues();
		defaultValues.put(KEY_DATA_COUNT, 1);
		Log.v(TAG, "The " + name + " will be the default city. " + otherCitiesNum + " cities had been change.");
		return db.update(DATABASE_USER_DATA_TABLE, defaultValues, KEY_DATA_CONTENT + "='" + name + "'", null);
	}
	
	
	public long setOtherWeatherCitiesCommon () {
		ContentValues othersValues = new ContentValues();
		othersValues.put(KEY_DATA_COUNT, 0);
		return db.update(DATABASE_USER_DATA_TABLE, othersValues, KEY_DATA_TYPE + "='" + WEATHER_CITY_ADDED + "'", null);
	}
	
	
	public Cursor getWeatherByCity ( String cityName ) {
		
		Cursor mCursor = db.query(true, DATABASE_WEATHER_INFO_TABLE, new String[] {
				KEY_ROWID, 
				KEY_TEMP1,
				KEY_TEMP2,
				KEY_WEATHER,
				KEY_WD,
				KEY_WS,
				KEY_WSE,
				KEY_INSERT_DATE
		}, 
		KEY_CITY + "=" + "'" + cityName + "'",
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
	
	
	public long insertTodayWeatherData(String cityName, int temp1, int temp2, String weather, String wd, int ws, int wse, int sd) {
		ContentValues values = new ContentValues();
		values.put(KEY_CITY, cityName);
		values.put(KEY_TEMP1, temp1);
		values.put(KEY_TEMP2, temp2);
		values.put(KEY_WEATHER, weather);
		values.put(KEY_WD, wd);
		values.put(KEY_WS, ws);
		values.put(KEY_WSE, wse);
		
		long id = db.update(DATABASE_WEATHER_INFO_TABLE, values, KEY_CITY + "='" + cityName + "' and insert_date = datetime('now','start of day')", null);
		if ( id < 1 ) {
			Log.v(TAG, "not row and insert a row");
			return db.insert(DATABASE_WEATHER_INFO_TABLE, null, values);
		} else {
			Log.v(TAG, "update row " + id);
			return id;
		}
		
	}
	
	
}
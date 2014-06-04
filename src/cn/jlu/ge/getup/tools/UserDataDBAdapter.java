package cn.jlu.ge.getup.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDataDBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_DATA_TYPE = "data_type";
	public static final String KEY_DATA_CONTENT = "data_content";
	public static final String KEY_DATA_DESC = "data_desc";
	public static final String KEY_DATA_COUNT = "data_count";
	public static final String KEY_DATA_UNIT = "data_unit";
	private static final String TAG = "UserDataDBAdapter";
	private static final String DATABASE_NAME = "bnl_user";
	private static final String DATABASE_TABLE = "user_data";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_USER_DATA_TABLE = 
			"create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, " +
					"data_type text not null, " +
					"data_content text not null, " +
					"data_desc text not null, " +
					"data_count integer not null, " +
					"data_unit text not null);";
	
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
		
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
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
		return db.insert(DATABASE_TABLE, null, values);
	}
	
	public boolean deleteWeatherCityByName(String name) {
		
		return db.delete(DATABASE_TABLE, KEY_DATA_CONTENT + "= '" + name + "'", null) > 0;
		
	}
	
	public long setWeatherCityDefaulted(String name) {
		long otherCitiesNum = setOtherWeatherCitiesCommon ();
		ContentValues defaultValues = new ContentValues();
		defaultValues.put(KEY_DATA_COUNT, 1);
		Log.v(TAG, "The " + name + " will be the default city. " + otherCitiesNum + " cities had been change.");
		return db.update(DATABASE_TABLE, defaultValues, KEY_DATA_CONTENT + "='" + name + "'", null);
	}
	
	public long setOtherWeatherCitiesCommon () {
		ContentValues othersValues = new ContentValues();
		othersValues.put(KEY_DATA_COUNT, 0);
		return db.update(DATABASE_TABLE, othersValues, KEY_DATA_TYPE + "='" + WEATHER_CITY_ADDED + "'", null);
	}
}

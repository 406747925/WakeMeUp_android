package cn.jlu.ge.dreamclock.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherCitiesDBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CITY = "city";
	public static final String KEY_CITY_PY = "city_py";
	public static final String KEY_AREA = "area";
	public static final String KEY_AREA_PY = "area_py";
	public static final String KEY_CITY_URL = "city_url";
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "libBNLDATA.db.so";
	private static final String DATABASE_TABLE = "weather_cities";
	private static final int DATABASE_VERSION = 1;
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public WeatherCitiesDBAdapter (Context c) {
		
		this.context = c;
//		DATABASE_PATH = context.getFilesDir().getPath();
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
	
	//�����ݿ�
	public WeatherCitiesDBAdapter open() throws SQLException {
		String DATABASE_PATH = "/data/data/" + context.getPackageName() + "/lib/";
		db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);    
		return this;
	}
	
	//�ر����ݿ�
	public void close() {
		DBHelper.close();
	}
	
	public Cursor getAllRows() {
		return db.query(DATABASE_TABLE, new String[] {
		KEY_ROWID,
		KEY_CITY,
		KEY_CITY_PY,
		KEY_CITY_URL,
		KEY_AREA,
		KEY_AREA_PY},
		null,
		null,
		null,
		null,
		null);
	}
	
	public Cursor getRowByUrl(String url) {
		Cursor mCursor =
		db.query(true, DATABASE_TABLE, new String[] {
		KEY_ROWID,
		KEY_CITY,
		KEY_CITY_PY,
		KEY_CITY_URL,
		KEY_AREA,
		KEY_AREA_PY},
		KEY_CITY_URL + "=" + url,
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
	
	public Cursor getRowByRowId(long rowId) {
		Cursor mCursor =
		db.query(DATABASE_TABLE, new String[] {
		KEY_ROWID,
		KEY_CITY,
		KEY_CITY_PY,
		KEY_CITY_URL,
		KEY_AREA,
		KEY_AREA_PY},
		KEY_ROWID + "=" + rowId ,
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
	
}

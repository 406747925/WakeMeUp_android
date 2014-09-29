package cn.jlu.ge.dreamclock.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class FriendsDBAdapter {
	public DBHelper mDBHelper;
	public SQLiteDatabase db;
    private Context context; 
    public static final String PHONE_NUMBER="num";
    public static final String NICK_NAME="name";
    public static final String PIC_URL="url";
    public static final String USER_ID="id";
    
    public static final String USER_JEER_CONTENT = "jeer_content";
    
    public static final String STRANGER_USERS_TABLE = "stranger_users";
    public static final String CREATE_STRANGER_USERS_TABLE = 
    			"create table " + STRANGER_USERS_TABLE + "(id text primary key," + 
    			NICK_NAME + " text not null, " + 
    			PHONE_NUMBER + " text not null, " +
    			PIC_URL + " text not null, " +
    			USER_JEER_CONTENT + " text not null);";
    
	public FriendsDBAdapter(Context ctx) {
		this.context = ctx;
		mDBHelper = new DBHelper(context, null, null, 1);
		db=mDBHelper.getWritableDatabase();
	}
	
	public void close() {
		mDBHelper.close();
	}
	
	public FriendsDBAdapter open() throws SQLException {
		db = mDBHelper.getWritableDatabase();
		return this;
	}
	
	class DBHelper extends SQLiteOpenHelper
	{
		public  DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context,"friends", null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {	
			String CREATE_TABLE="create table friendlist"
					+ "(_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT"
					+ ",num text not null unique"
					+ ",name text"
					+ ",url text"
					+ ",id text unique);";
		
			db.execSQL(CREATE_TABLE);
			db.execSQL(CREATE_STRANGER_USERS_TABLE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			Log.w("1", "Upgrading database from version " + arg1
			+ " to " + arg2+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
			
		}}
	
	   public long insertRow(String name,String num,String url,String id)
	   {
		   ContentValues contentValue=new ContentValues();
		   contentValue.put("num", num);
		   contentValue.put("name", name);
		   contentValue.put("url",url );
		   contentValue.put("id", id);
		   return   db.insert("friendlist", null, contentValue);
		   
	   }
	   public void clearAllRows()
	   {
		   String sql="delete from friendlist";
		   db.execSQL(sql);
	   }
	   public Cursor findFromNum(String num)
	   {
		   Cursor cursor=db.query("friendlist",new String[] {
					"num",
					"name",
					"url"},"num="+num, null, null, null, null);		
		   return cursor;
	   }
	   public void removeFromNum(String num)
	   {
           String sql="DELETE FROM friendlist WHERE num="+num;

		 db.execSQL(sql);
	   }
	   public Cursor getAllRows()
	   {
		   Cursor cursor=db.query("friendlist",new String[] {
					"num",
					"name",
					"url",
					"id"},null, null, null, null, null);		
		   return cursor; 
	   }
	   
	   public Cursor getUsersByNotIn (String selectionArgStr) {
		   Cursor cursor = db.query("friendlist", new String[] {
					"num",
					"name",
					"url",
					"id"}, "id not in (?)", new String[] { selectionArgStr }, null, null, null);	
		   return cursor;
	   }
}
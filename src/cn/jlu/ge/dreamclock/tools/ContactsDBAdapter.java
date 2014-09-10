package cn.jlu.ge.dreamclock.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import cn.jlu.ge.dreamclock.tools.FriendsDBAdapter.DBHelper;

public class ContactsDBAdapter {
	
	public DBHelper mDBHelper;
	public SQLiteDatabase db;
    private Context context; 
	public ContactsDBAdapter(Context ctx) {
		this.context = ctx;
		mDBHelper = new DBHelper(context, null, null, 1);
		db=mDBHelper.getWritableDatabase();
	}
	class DBHelper extends SQLiteOpenHelper
	{
		public  DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context,"contacts", null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {	
			String CREATE_TABLE="create table contacts"+ "(phonenumber  text PRIMARY KEY , name text not null);";
			db.execSQL(CREATE_TABLE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			Log.w("1", "Upgrading database from version " + arg1
			+ " to " + arg2+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
			
		}}
	
	   public long insertRow(String num,String name)
	   {
		   ContentValues contentValue=new ContentValues();
		   contentValue.put("phonenumber", num);
		   contentValue.put("name", name);
		   return   db.insert("contacts", null, contentValue);
		   
	   }
	   public void clearAllRows()
	   {
		   String sql="delete from friendlist";
		   db.execSQL(sql);
	   }
	   public Cursor findFromNum(String num)
	   {
		   Cursor cursor=db.query("contacts",new String[] {
					"phonenumber","name"},"phonenumber="+num, null, null, null, null);		
		   return cursor;
	   }
	   public void removeFromNum(String num)
	   {
           String sql="DELETE FROM contacts WHERE phonenumber="+num;

		 db.execSQL(sql);
	   }
	   public Cursor getAllRows()
	   {
		   Cursor cursor=db.query("contacts",new String[] {
					"phonenumber","name"},null, null, null, null, null);		
		   return cursor; 
	   }

}

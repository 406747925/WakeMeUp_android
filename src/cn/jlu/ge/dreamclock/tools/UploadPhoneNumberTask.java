package cn.jlu.ge.dreamclock.tools;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;
import cn.jlu.ge.dreamclock.activity.PositiveEnergyActivity;

public class  UploadPhoneNumberTask extends AsyncTask<Void, Void, JSONObject>
{
	Context mContext;
	public isLoadDataListener loadLisneter;
	public interface isLoadDataListener {
		public void loadComplete();
	}
	public UploadPhoneNumberTask(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	public UploadPhoneNumberTask(Context context,isLoadDataListener dataComplete) {
		// TODO Auto-generated constructor stub
		mContext = context;
		this.loadLisneter = dataComplete;
	}

	@Override
	protected JSONObject doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		SharedPreferences pre=mContext.getSharedPreferences(Const.APP_INFO_PREFERENCE, Context.MODE_MULTI_PROCESS);
		GetContects c=new GetContects(mContext);
		ArrayList<StringBuilder> l=c.getAllContacts();
		StringBuilder num=l.get(0);
		StringBuilder name=l.get(1);
		String path=PositiveEnergyActivity.UrlHead
				+"search.action?claseName=FriendsSrvImpl&invokeMethod=filterFriend"
				+ "&param.user_id="+pre.getString(Const.USER_ID, "")
				+"&param.phones="+num.toString()
				+"&param.names="+name.toString();
		try{
			byte[] data = ReadParseClass.readParse(path);
			String s=new String(data);
			JSONObject json = new JSONObject(s);
			return json;
		}catch(Exception e){}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		// TODO Auto-generated method stub
		FriendsDBAdapter adapter =new FriendsDBAdapter(mContext);
		if(result==null)
			return;
		super.onPostExecute(result);
		try{
			if(result.getInt("statusCode")!=200)
			{
				Toast.makeText(mContext, result.getString("message"), Toast.LENGTH_LONG).show();
				return;
			}
			JSONArray jarray=result.getJSONArray("list");
			for(int i=0;i<jarray.length();i++)
			{
				JSONObject j=(JSONObject)jarray.get(i);
				adapter.removeFromNum(j.getString("phone"));
				adapter.insertRow(j.optString("nickname", null), j.getString("phone"),j.optString("pic_url", null), j.getString("id"));

			}

		}catch(Exception e){
			
		}
		
		String s = new String();
		int i=0;
		Cursor cursor = adapter.getAllRows();

		while( cursor.moveToNext() ) {
			s+=cursor.getString(0);
			i++;
		}
		
		cursor.close();
		
        if (loadLisneter != null) {
            loadLisneter.loadComplete();
        }

	}

}
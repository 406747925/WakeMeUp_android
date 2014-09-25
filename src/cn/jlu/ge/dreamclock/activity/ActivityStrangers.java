package cn.jlu.ge.dreamclock.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.BitmapCache;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.ContactsDBAdapter;
import cn.jlu.ge.dreamclock.tools.FriendsDBAdapter;
import cn.jlu.ge.dreamclock.tools.ReadParseClass;
import cn.jlu.ge.knightView.CircleImageView;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityStrangers extends Activity{
	class Holder 
	{
		CircleImageView imageView;
		TextView textview;
		TextView textview2;
		TextView textview3;
		Button buttonAccept;
		Button buttonReject;
	}
	ListView mListView;
	private SimpleAdapter adapter;
	BitmapCache mBitmapCache;
	private ArrayList<HashMap<String,Object>> list;
	private HashMap<String,Object> map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_strangers);
		mListView=(ListView)findViewById(R.id.listView1);
		mBitmapCache=new BitmapCache(getApplicationContext());
		list=new ArrayList<HashMap<String,Object>>();
		map=new HashMap<String, Object>();
		adapter=new SimpleAdapter(getApplication(), list, R.layout.stranger_item
				,new String[]{"str"}, new int[]{R.id.textView1})
		{
			@Override
			public View getView(int position, View convertView,ViewGroup parent)
			{
				Holder holder;
				//position=position-1;
				if(convertView==null)
				{
					holder=new Holder();
					convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.stranger_item, null);
					CircleImageView imageView=(CircleImageView) convertView.findViewById(R.id.imageView1);
					holder.imageView=imageView;

					//	imageView.setTag(PositiveEnergyActivity.UrlHead+list.get(position).get(FriendsDBAdapter.PIC_URL));

					TextView textview=(TextView) convertView.findViewById(R.id.textViewname);
					holder.textview=textview;
					

					TextView textview2=(TextView) convertView.findViewById(R.id.textViewphone);
					holder.textview2=textview2;
					

					TextView textview3=(TextView) convertView.findViewById(R.id.textViewchaoxiao);
					holder.textview3=textview3;
					

					Button buttonAccept=(Button)convertView.findViewById(R.id.buttona);
					holder.buttonAccept=buttonAccept;
					
					Button buttonReject=(Button)convertView.findViewById(R.id.buttonr);
					holder.buttonReject=buttonReject;
					
					convertView.setTag(holder);
				}
				else
				{
					holder=(Holder)convertView.getTag();
				}
				
				setUserAvatar(list.get(position).get("pic_url").toString(), holder.imageView);
				holder.textview.setText(list.get(position).get("nickname").toString());
				holder.textview2.setText(list.get(position).get("phone").toString());
				holder.textview3.setText(list.get(position).get("content").toString());
				holder.buttonAccept.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

					}
				});
				holder.buttonReject.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

					}
				});
				

				return convertView;
			}

		};
		mListView.setAdapter(adapter);
		new GetDataTask().execute();
		super.onCreate(savedInstanceState);
	}

	void setUserAvatar ( String avatarUrl ,CircleImageView avatarIv) {
		//	CircleImageView avatarIv = (CircleImageView) findViewById(R.id.userAvatar);
		Bitmap avatarBM = mBitmapCache.getBitmapFromCache(avatarUrl);
		if ( avatarBM != null ) {
			try{

				BitmapDrawable avatarDrawable = new BitmapDrawable(getApplicationContext().getResources(), avatarBM);
				avatarIv.setImageDrawable(avatarDrawable);
			}catch(Exception e)
			{
				e.printStackTrace();
			}

		}
		else {
			mBitmapCache.getImageFromNet(avatarUrl, "100-" + avatarUrl, 100, 100, avatarIv);
		}
	}


	private class GetDataTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			String path=PositiveEnergyActivity.UrlHead+"search.action?claseName=FriendsSrvImpl&invokeMethod=loadSignUpFriends&param.flag=0&param.user_id="
					+getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS).getString(Const.USER_ID, null);
			//		+"ff2d2939bc6b4937b4612d851874efa3";
			byte[] data;
			try {
				data = ReadParseClass.readParse(path);
				String s=new String(data);
				JSONObject  json = new JSONObject(s);
				return json;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if(result==null)
				return;
			try {
				if(result.getInt("statusCode")!=200)
				{
					Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_SHORT).show();
					return;
				}	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				JSONArray jarray=result.getJSONArray("list");
				for(int i=0;i<jarray.length();i++)
				{
					JSONObject j=(JSONObject)jarray.get(i);
					map=new HashMap<String, Object>();
					map.put("nickname", j.optString("nickname", ""));
					map.put("pic_url", j.optString("pic_url", ""));
					map.put("content", j.optString("content", ""));
					map.put("phone", j.optString("phone", ""));
					map.put("friend_id", j.optString("friend_id", ""));
					list.add(map);

				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			//			while(cursor.moveToNext())
			//			{
			//				map=new HashMap<String, Object>();
			//				map.put(FriendsDBAdapter.PHONE_NUMBER,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PHONE_NUMBER)));
			//				map.put(FriendsDBAdapter.NICK_NAME,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.NICK_NAME)));
			//				map.put(FriendsDBAdapter.PIC_URL,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PIC_URL)));
			//				map.put(FriendsDBAdapter.USER_ID,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.USER_ID)));
			//				list.add(map);
			//			}
			//			cursor.close();
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

	}

}

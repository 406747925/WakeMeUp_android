package cn.jlu.ge.dreamclock.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.BitmapCache;
import cn.jlu.ge.dreamclock.tools.CavansImageTask;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.ContactsDBAdapter;
import cn.jlu.ge.dreamclock.tools.FriendsDBAdapter;
import cn.jlu.ge.dreamclock.tools.ReadParseClass;
import cn.jlu.ge.dreamclock.tools.UploadPhoneNumberTask;
import cn.jlu.ge.dreamclock.tools.UploadPhoneNumberTask.isLoadDataListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class FriendListActivity extends Activity {

	private PullToRefreshListView mListView;
	private SimpleAdapter adapter;
	private HashMap<String,Object> map;
	private ArrayList<HashMap<String,Object>> list;
	private cn.jlu.ge.dreamclock.tools.FriendsDBAdapter mDBAdapter;
	private final String TAG="1234567890123";
	private String mNewFriendNames;
	private String mNewFriendIDs;
	BitmapCache mBitmapCache;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mNewFriendNames="";
		mNewFriendIDs="";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);
		mBitmapCache=new BitmapCache(getApplicationContext());
		mDBAdapter = new cn.jlu.ge.dreamclock.tools.FriendsDBAdapter(this);	
		mListView=(PullToRefreshListView)findViewById(R.id.listViewfriend);
		list=new ArrayList<HashMap<String,Object>>();
		map=new HashMap<String, Object>();
		View view=findViewById(R.id.textView1);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(FriendListActivity.this, ActivityStrangers.class);
				startActivity(newIntent);		
			}
		});
		adapter=new SimpleAdapter(getApplication(), list, R.layout.friend_item
				,new String[]{"str"}, new int[]{R.id.textViewfriend})
		{

			@Override
			public View getView(int position, View convertView,ViewGroup parent)
			{
				//position=position-1;
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.friend_item, null);
				if(position%2==0)
					//	convertView.setBackgroundResource(R.drawable.onesbackground);
				{	convertView.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd));}
				ImageView imageView=(ImageView) convertView.findViewById(R.id.imageView1);

				imageView.setTag(PositiveEnergyActivity.UrlHead+list.get(position).get(FriendsDBAdapter.PIC_URL));
				new CavansImageTask().execute(imageView);

				//		Bitmap bitmap = mBitmapCache.getBitmapFromCache(list.get(position).get(FriendsDBAdapter.PIC_URL).toString());
				Bitmap bitmap = mBitmapCache.getBitmapFromCache(null);
				if(bitmap==null)
				{
				//		mBitmapCache.getImageFromNet(list.get(position).get(FriendsDBAdapter.PIC_URL).toString(), list.get(position).get(FriendsDBAdapter.PIC_URL).toString(), 45, 45, imageView);
				//	mBitmapCache.getImageFromNet(null,null, 45, 45, imageView);
				}else
				{
					BitmapDrawable drawable=new BitmapDrawable(getApplication().getResources(),bitmap);
					imageView.setBackgroundDrawable(drawable);
				}
				TextView textview=(TextView) convertView.findViewById(R.id.textViewfriend);
				textview.setText(list.get(position).get(FriendsDBAdapter.NICK_NAME).toString());

				return convertView;
			}

		};
		mListView.setAdapter(adapter);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				//		new GetDataTask().execute();

				new UploadPhoneNumberTask(getApplicationContext(),new isLoadDataListener() {

					@Override
					public void loadComplete() {
						// TODO Auto-generated method stub
						list.clear();
						//	mDBAdapter.insertRow("sb", "123", "www.baidu.com", null);
						Cursor cursor=mDBAdapter.getAllRows();
						while(cursor.moveToNext())
						{
							map=new HashMap<String, Object>();
							map.put(FriendsDBAdapter.PHONE_NUMBER,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PHONE_NUMBER)));
							map.put(FriendsDBAdapter.NICK_NAME,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.NICK_NAME)));
							map.put(FriendsDBAdapter.PIC_URL,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PIC_URL)));
							map.put(FriendsDBAdapter.USER_ID,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.USER_ID)));
							list.add(map);
						}
						adapter.notifyDataSetChanged();
						mListView.onRefreshComplete();

					}
				}).execute();

			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView v=(TextView)arg1.findViewById(R.id.textViewfriend);
				Toast toast=Toast.makeText(getApplication(), "click"+v.getText().toString(), Toast.LENGTH_SHORT);
				toast.show();
				Intent intent =new Intent(getApplication(),WebPageActivity.class);
				intent.putExtra("url", "www.baidu.com");
				startActivity(intent);

			}
		});
		Cursor cursor=mDBAdapter.getAllRows();
		while(cursor.moveToNext())
		{
			map=new HashMap<String, Object>();
			map.put(FriendsDBAdapter.PHONE_NUMBER,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PHONE_NUMBER)));
			map.put(FriendsDBAdapter.NICK_NAME,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.NICK_NAME)));
			map.put(FriendsDBAdapter.PIC_URL,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PIC_URL)));
			map.put(FriendsDBAdapter.USER_ID,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.USER_ID)));
			list.add(map);
		}
		adapter.notifyDataSetChanged();
		new GetDataTask().execute();
	}
	private class GetDataTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			String path=PositiveEnergyActivity.UrlHead+"search.action?claseName=FriendsSrvImpl&invokeMethod=strangers&param.user_id="
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
				ContactsDBAdapter dbadapter=new ContactsDBAdapter(getApplicationContext());
				JSONArray jarray=result.getJSONArray("list");
				for(int i=0;i<jarray.length();i++)
				{
					JSONObject j=(JSONObject)jarray.get(i);
					if( dbadapter.findFromNum(j.getString("phone")).getCount()>0)
					{
						mDBAdapter.removeFromNum(j.getString("phone"));
						mDBAdapter.insertRow(j.optString("nickname", null), j.getString("phone"),j.optString("pic_url", null), j.getString("id"));
						Cursor cursor=dbadapter.findFromNum(j.getString("phone"));
						mNewFriendNames+= cursor.getString(cursor.getColumnIndex("name"));
						mNewFriendIDs+=j.getString("id");
					}
				}
				if(mNewFriendIDs.length()==0||mNewFriendIDs.length()==0)
					return;
				mNewFriendNames=mNewFriendNames.substring(0, mNewFriendNames.length()-1);
				mNewFriendIDs=mNewFriendIDs.substring(0, mNewFriendIDs.length()-1);
				final String path=PositiveEnergyActivity.UrlHead+"search.action?claseName=FriendsSrvImpl&invokeMethod=updateName&param.user_id="
						+ getSharedPreferences(Const.APP_INFO_PREFERENCE, MODE_MULTI_PROCESS).getString(Const.USER_ID, null)
						+ "&param.friend_ids="
						+ mNewFriendIDs
						+ "&param.names="
						+ mNewFriendNames;
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							ReadParseClass.readParse(path);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						super.run();
					}
				}.start();

			}catch(JSONException e){
				e.printStackTrace();
			}

			list.clear();
			Cursor cursor=mDBAdapter.getAllRows();
			while(cursor.moveToNext())
			{
				map=new HashMap<String, Object>();
				map.put(FriendsDBAdapter.PHONE_NUMBER,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PHONE_NUMBER)));
				map.put(FriendsDBAdapter.NICK_NAME,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.NICK_NAME)));
				map.put(FriendsDBAdapter.PIC_URL,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.PIC_URL)));
				map.put(FriendsDBAdapter.USER_ID,cursor.getString(cursor.getColumnIndex(FriendsDBAdapter.USER_ID)));
				list.add(map);
			}
			adapter.notifyDataSetChanged();
			mListView.onRefreshComplete();
			super.onPostExecute(result);
		}

	}

}

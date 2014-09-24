package cn.jlu.ge.dreamclock.activity;

import java.util.ArrayList;
import java.util.HashMap;

import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.BitmapCache;
import cn.jlu.ge.dreamclock.tools.FriendsDBAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

public class ActivityStrangers extends Activity{
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
		map.put("str", "123");
		list.add(map);
		
		adapter=new SimpleAdapter(getApplication(), list, R.layout.stranger_item
				,new String[]{"str"}, new int[]{R.id.textView1})
		{
			@Override
			public View getView(int position, View convertView,ViewGroup parent)
			{
				//position=position-1;
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.stranger_item, null);
				if(position%2==0)
					//	convertView.setBackgroundResource(R.drawable.onesbackground);
				{	convertView.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd));}
				ImageView imageView=(ImageView) convertView.findViewById(R.id.imageView1);

				imageView.setTag(PositiveEnergyActivity.UrlHead+list.get(position).get(FriendsDBAdapter.PIC_URL));

				//		Bitmap bitmap = mBitmapCache.getBitmapFromCache(list.get(position).get(FriendsDBAdapter.PIC_URL).toString());
//				Bitmap bitmap = mBitmapCache.getBitmapFromCache(null);
//				if(bitmap==null)
//				{
//					//	mBitmapCache.getImageFromNet(list.get(position).get(FriendsDBAdapter.PIC_URL).toString(), list.get(position).get(FriendsDBAdapter.PIC_URL).toString(), 45, 45, imageView);
//					mBitmapCache.getImageFromNet(null,null, 45, 45, imageView);
//				}else
//				{
//					BitmapDrawable drawable=new BitmapDrawable(getApplication().getResources(),bitmap);
//					imageView.setBackgroundDrawable(drawable);
//				}
				TextView textview=(TextView) convertView.findViewById(R.id.textView1);
				textview.setText(list.get(position).get("str").toString());
				
				Button buttonAccept=(Button)convertView.findViewById(R.id.buttona);
				buttonAccept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				Button buttonReject=(Button)convertView.findViewById(R.id.buttonr);
				buttonReject.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
					}
				});

				return convertView;
			}

		};
		mListView.setAdapter(adapter);

		super.onCreate(savedInstanceState);
	}
	

}

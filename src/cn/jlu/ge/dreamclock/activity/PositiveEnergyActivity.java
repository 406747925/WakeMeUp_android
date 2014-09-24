package cn.jlu.ge.dreamclock.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.CavansImageTask;
import cn.jlu.ge.dreamclock.tools.Const;
import cn.jlu.ge.dreamclock.tools.ReadParseClass;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class PositiveEnergyActivity extends ListActivity{
	public static final String UrlHead = Const.HOST;
	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;

	private LinkedList<String> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private SimpleAdapter adapter;
	private HashMap<String,Object> map;
	private ArrayList<HashMap<String,Object>> list;
	private int page = 1;
    
    //每一个HashMap 对应ListView 中 每一个 item 的数据
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_positive_energy);
	//	getActionBar().hide();
		mPullRefreshListView=(PullToRefreshListView)findViewById(R.id.listView1);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				
			if(	PullToRefreshBase.Mode.PULL_FROM_START == mPullRefreshListView.getCurrentMode())
			{
				page=1;
				new GetDataTask(true).execute();
			}
			else
			{
				new GetDataTask(false).execute();
			}
				
				
			}
		});
		
       list=new ArrayList<HashMap<String,Object>>();
        
        //每一个HashMap 对应ListView 中 每一个 item 的数据
//       map=new HashMap<String, Object>();
//      for(int i=0;i<20;i++)
//       {
//       map=null;
//       map=new HashMap<String, Object>();
//       map.put("title", "吉林大学学报");
//       map.put("date", "lalala");
//       map.put("description", "ddddddd");
//       map.put("texturl", "www.baidu.com");
//       map.put("texturl", "www.baidu.com");
//       list.add(map);
//       }
       
		 adapter=new SimpleAdapter(getApplication(), list, R.layout.pe_item
				,new String[]{}
		        , new int[]{})
		{
			
			@Override
			public View getView(int position, View convertView,ViewGroup parent)
			{
//             final View view=super.getView(position, convertView, parent);
//				convertView=(View)findViewById(R.layout.pe_item);
//				
//				ImageView image = (ImageView) convertView.findViewById(R.id.imageView2);
//				TextView textview=(TextView)convertView.findViewById(R.id.textimageurl);
//				image.setTag(UrlHead+textview.getText().toString());
//	//			 new CavansImageTask().execute(image);
//				return view;
				if(convertView==null)
				{
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pe_item, null);
		        ImageView image = (ImageView) convertView.findViewById(R.id.imageView2);
		 //       TextView textimageurl=(TextView)convertView.findViewById(R.id.textimageurl);
		//        TextView texturl=(TextView)convertView.findViewById(R.id.texturl);
		        TextView texttitle=(TextView)convertView.findViewById(R.id.title);
		        TextView textdate=(TextView)convertView.findViewById(R.id.date);
		        TextView textdescription=(TextView)convertView.findViewById(R.id.description);
		        texttitle.setText(list.get(position).get("title").toString());
		        textdate.setText(list.get(position).get("date").toString());
		        textdescription.setText(list.get(position).get("description").toString());
		        image.setTag(list.get(position).get("imageurl").toString());
		        new CavansImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image);
				}
		        return convertView;
			}
			
		};
		mPullRefreshListView.setAdapter(adapter);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				Toast.makeText(getApplicationContext(), "End of List!", Toast.LENGTH_SHORT).show();		
			}
		});

		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			//	TextView v=(TextView)arg1.findViewById(R.id.viewmore);
		//		Toast toast=Toast.makeText(getApplication(), "click"+v.getText().toString(), Toast.LENGTH_SHORT);
		//		toast.show();
				
				Intent intent =new Intent(getApplication(),WebPageActivity.class);
				intent.putExtra("url", list.get(arg2-1).get("texturl").toString());
				startActivity(intent);
				
			}
		});
		new GetDataTask(true).execute();
		mPullRefreshListView.setRefreshing(false);
		
	}
	private class GetDataTask extends AsyncTask<Void, Void, JSONObject> {
		boolean PullFromStrat;
		public GetDataTask(boolean b) {
			PullFromStrat=b;
			// TODO Auto-generated constructor stub
		}
		
		 
		@Override
		protected JSONObject doInBackground(Void... params) {	
			// Simulates a background job.
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
           try{
        	   int i= list.size();
        	   
        	   int p=1;
        	   if(PullFromStrat==true)
   			{
   				p=1;
   			}else
   			{
         	   if(i%8!=0)
        		   p=i/8+1+1;
        	   else
        		   p=i/8+1;
   			}
        	   JSONObject json;
   			String path=UrlHead+"search.action?claseName=PositiveSrvImpl&invokeMethod=load&pageNum="
				+String.valueOf(p)
				+ "&numPerPage=8&param.school="
				+URLEncoder.encode("吉林大学", "UTF-8");
			byte[] data = ReadParseClass.readParse(path);
			String s=new String(data);
		 json = new JSONObject(s);
		 return json;
           }catch(Exception e)
           {
        	   e.printStackTrace();
           }
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			if(json!=null)
			{
				
			if(PullFromStrat==true)
			{
				list.clear();
			}else
			{
				int i=1;
			}
				try {
					JSONArray jsonArray=json.getJSONArray("list"); 
					for(int i=0;i<jsonArray.length();i++)
					{
						JSONObject jsoni=(JSONObject) jsonArray.get(i); 
						   map=null;
					       map=new HashMap<String, Object>();
					       map.put("title", jsoni.get("title"));
					       map.put("date", jsoni.get("create_time"));
					       map.put("description", jsoni.get("content"));
					       map.put("texturl", UrlHead+"printHtml.action?id="+jsoni.get("id"));
					       map.put("imageurl", UrlHead+jsoni.get("path"));
					       list.add(map); 
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				page++;
			}
			adapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(json);
		}
	}

}

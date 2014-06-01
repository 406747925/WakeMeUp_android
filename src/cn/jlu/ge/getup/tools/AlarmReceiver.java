package cn.jlu.ge.getup.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.jlu.ge.getup.WakeUpActivity;

public class AlarmReceiver extends BroadcastReceiver {

	String mediaNameStr;
	String welcomeStr;
	int rowId;
	
	public void onReceive(Context context, Intent intent) {

		Toast.makeText(context , "小闹提醒！闹钟时间到了！", Toast.LENGTH_SHORT).show();
    	Intent newIntent = new Intent(context, WakeUpActivity.class);
    	newIntent.putExtra("rowId", rowId);
    	newIntent.putExtra("welcomeStr", welcomeStr);
    	newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(newIntent);
    	
    	return ;
    	
    }
	
}
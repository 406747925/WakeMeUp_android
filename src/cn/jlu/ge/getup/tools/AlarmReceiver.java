package cn.jlu.ge.getup.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.jlu.ge.getup.WakeUpActivity;

public class AlarmReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
    	
    	Toast.makeText(context , "闹钟时间到了！", Toast.LENGTH_SHORT).show();
    	Intent newIntent = new Intent(context, WakeUpActivity.class);
    	newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(newIntent);
    	return ;
    	
    }
	
}
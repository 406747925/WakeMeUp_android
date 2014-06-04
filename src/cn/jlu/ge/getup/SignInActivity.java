package cn.jlu.ge.getup;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import cn.jlu.ge.getup.tools.AlarmReceiver;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SignInActivity extends SherlockActivity{

	RelativeLayout signInLayout;
	Calendar calendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
        signInLayout = (RelativeLayout) findViewById(R.id.signIn);
        calendar = Calendar.getInstance();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        
		switch(item.getItemId()){
        
        case R.id.alarm_setting:
			Log.d("set Alarm", "click the time button to set time");
			calendar.setTimeInMillis(System.currentTimeMillis());
			new TimePickerDialog(SignInActivity.this,new TimePickerDialog.OnTimeSetListener() {
	
				@Override
				public void onTimeSet(TimePicker arg0, int hour, int mins) {
					// TODO Auto-generated method stub
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.set(Calendar.HOUR_OF_DAY, hour);
					calendar.set(Calendar.MINUTE, mins);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
		            Intent intent = new Intent(SignInActivity.this,AlarmReceiver.class);
		            PendingIntent pendingIntent = PendingIntent.getBroadcast(SignInActivity.this, 0, intent, 0);
		            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
				}
				
			}, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
			
            break;
            
        }
		
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.sign_in, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
}

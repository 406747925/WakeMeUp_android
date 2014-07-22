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
        
        case R.id.alarm_list:
        	Intent newIntent = new Intent(getApplicationContext(), SetAlarmActivity.class);
        	startActivity(newIntent);
			
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

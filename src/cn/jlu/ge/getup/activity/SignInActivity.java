package cn.jlu.ge.getup.activity;

import java.util.Calendar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import cn.jlu.ge.getup.R;
import cn.jlu.ge.getup.tools.BaseActivity;
import cn.jlu.ge.getup.tools.MenuFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class SignInActivity extends BaseActivity {

	public SignInActivity() {
		super("早起签到");
		// TODO Auto-generated constructor stub
	}


	RelativeLayout signInLayout;
	Calendar calendar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		init();
		
	}
	
	public void init() {
        
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		setContentView(R.layout.activity_sign_in);
		
		viewInit();
		
		// 因为在父类 BaseActivity 的 onCreate 方法执行时， 所需要的 Fragment 控件必须是 子控件，
        // 方法中的 FragmentTransaction 会使用 id 资源 ( R.id.menu_frame2 ) 引用 Fragment 控件，
        // 如果未先将对应的 Fragment 控件设置为子控件进行初始化， FragmentManager 将会找不到这个子控件，
		// 而在绘制界面时才抛出运行时异常
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
	}
	
	public void viewInit () {
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        
		switch(item.getItemId()) {
        
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

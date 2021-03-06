package cn.jlu.ge.dreamclock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.MenuFragment;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

    private int titleRes;
    private String title;
    Fragment frag;  
    
    public BaseActivity (int titleRes) {
        this.titleRes = titleRes;
        this.title = null;
    }
    
    public BaseActivity (String title) {
    	this.titleRes = 0;
    	this.title = title;
    }
    
    @Override  
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);
        
        if (title == null) {
        	this.setTitle(titleRes);
        } else {
        	this.setTitle(title);
        }
        
        
        //设置behind View
        this.setBehindContentView(R.layout.sliding_menu);
        this.setContentView(R.layout.menu_frame);
        
        if(savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
            MenuFragment fragment = new MenuFragment();
            fragmentTransaction.replace(R.id.menu_frame2, fragment);
            fragmentTransaction.commit();
        } else {
            frag = this.getSupportFragmentManager().findFragmentById(R.id.menu_frame2);
        }
        
        
        SlidingMenu menu = this.getSlidingMenu();
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setTouchmodeMarginThreshold(R.dimen.slidingmenu_offset);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

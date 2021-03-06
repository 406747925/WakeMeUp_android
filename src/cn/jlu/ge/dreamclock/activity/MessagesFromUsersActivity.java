package cn.jlu.ge.dreamclock.activity;

import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.MenuFragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MessagesFromUsersActivity extends BaseActivity {

	public MessagesFromUsersActivity(int titleRes) {
		super(titleRes);
		// TODO Auto-generated constructor stub
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
}

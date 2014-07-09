package cn.jlu.ge.getup;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import cn.jlu.ge.getup.tools.BaseActivity;
import cn.jlu.ge.getup.tools.MenuFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class WebPageActivity extends BaseActivity {

	public WebPageActivity() {
		super(R.string.app_name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		init();
		
	}
	
	void init () {
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_webpage);
		
		viewInit();
		
		// 因为在父类 BaseActivity 的 onCreate 方法执行时， 所需要的 Fragment 控件必须是 子控件，
        // 方法中的 FragmentTransaction 会使用 id 资源( R.id.menu_frame2 ) 引用 Fragment 控件，
        // 如果未先将对应的 Fragment 控件设置为子控件进行初始化， FragmentManager 将会找不到这个子控件，
		// 而在绘制界面时才抛出运行时异常
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
	}
	
	void viewInit () {

		bottomBarInit();
		
		Intent urlIntent = getIntent();
		Bundle bundle = urlIntent.getExtras();
		String url = bundle.getString("url");
		
		// Just test
		url = "http://www.baidu.com";
		
		WebView positiveWebView = (WebView) findViewById(R.id.webPage);
		positiveWebView.setWebViewClient(new WebViewClient () {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		
		positiveWebView.loadUrl(url);
		
	}
	
	void bottomBarInit () {
		Button firstBottomBarBtn = (Button) findViewById(R.id.firstBottomBarBtn);
		Button secBottomBarBtn = (Button) findViewById(R.id.secBottomBarBtn);
		Button thirdBottomBarBtn = (Button) findViewById(R.id.thirdBottomBarBtn);
		firstBottomBarBtn.setText(R.string.bottom_bar_first);
		secBottomBarBtn.setText("text");
		thirdBottomBarBtn.setText("third");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	
	

}

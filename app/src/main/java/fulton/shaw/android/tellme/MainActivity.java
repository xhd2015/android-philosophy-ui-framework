
 /**
  * 手机问答系统的基本构成 
 * 
 */
package fulton.shaw.android.tellme;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import fulton.shaw.android.tellme.newdesign.ViewPagerActivity;

public class MainActivity extends FragmentActivity implements OnClickListener
{
	
	FrameLayout mContainer;
	SearchFragment mSearchPage;
	DetailPageFragment mDetailedPage;
	Fragment mAskPage;
	Fragment mPersonalPage;
	
	Button mSearchBtn,mPostQuestion,mDetails,mPersonal;
	
	Fragment mCurrentPage;
	Map<Integer, Fragment> mPages=new HashMap<Integer, Fragment>();;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initFileds();
		
		FragmentManager m=getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		
		/*
		 * 初始化为search 页面
		 */
		t.add(R.id.frameLayout1,mSearchPage);
		mCurrentPage=mSearchPage;
		
		t.commit();
		
		//设置button监听事件
		mSearchBtn.setOnClickListener(this);
		mPostQuestion.setOnClickListener(this);
		mDetails.setOnClickListener(this);
		mPersonal.setOnClickListener(this);
	}
	void initFileds()
	{
		mContainer=(FrameLayout)findViewById(R.id.frameLayout1);
		//Add each page here
		mSearchPage=new SearchFragment();
		mDetailedPage=new DetailPageFragment();
		mPersonalPage=new PersonalFragment();
		mAskPage=new AskFragment();
		
		mPages.put(R.id.button1, mSearchPage);
		mPages.put(R.id.button2, mAskPage);
		mPages.put(R.id.button3, mDetailedPage);
		mPages.put(R.id.button4, mPersonalPage);
		
		mSearchBtn=(Button)findViewById(R.id.button1);
		mPostQuestion=(Button)findViewById(R.id.button2);
		mDetails=(Button)findViewById(R.id.button3);
		mPersonal=(Button)findViewById(R.id.button4);
		
		
	}
	/**
	 * 
	 *处理onclick事件, 切换页面
	 */
	void onChangeButton(Button which)
	{
		final Fragment f=mPages.get(which.getId());
		if(f!=mCurrentPage)
		{
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					changePage(f);
				}
			});
		}
	}
	/*!NOT UI SAFE!*/
	void changePage(Fragment f)
	{
		FragmentManager m=getSupportFragmentManager();
		FragmentTransaction t=m.beginTransaction();
		t.replace(R.id.frameLayout1, f);
		t.commit();
		mCurrentPage=f;
	}
	/**
	 * This is generally for 4 buttons and other activity level 
	 * widgets
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v instanceof Button)
		{
			onChangeButton((Button)v);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}else if(id==R.id.action_exit){
			this.finish();
			return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
	}
	public void changeToDetail(String title,String url)
	{
		
		final String ftitle=title;
		final String fUrl=url;
		changePage(mDetailedPage);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MainActivity.this.getFragmentManager().executePendingTransactions();
				mDetailedPage.openURL(ftitle, fUrl);
			}
		});
	}
}

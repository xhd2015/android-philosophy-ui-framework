package fulton.shaw.android.tellme.newdesign;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import fulton.shaw.android.tellme.DetailPageFragment;
import fulton.shaw.android.tellme.MainActivity;
import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.adapter.CertainFragmentPagerAdapter;

public class ViewPagerActivity extends FragmentActivity{
	FragmentPagerAdapter mAdapterViewPager;
	ViewPager mVPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewpagers);
        mVPager = (ViewPager) findViewById(R.id.vpPager);
        mAdapterViewPager = new CertainFragmentPagerAdapter(getSupportFragmentManager());
        mVPager.setAdapter(mAdapterViewPager);
        mVPager.setCurrentItem(CertainFragmentPagerAdapter.SEARCH);
        
        
        mVPager.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    public void changeToDetail(String title,String url)
    {
		final String ftitle=title;
		final String fUrl=url;
		mVPager.setCurrentItem(CertainFragmentPagerAdapter.DETAIL);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				((DetailPageFragment)mAdapterViewPager.getItem(CertainFragmentPagerAdapter.DETAIL)).openURL(ftitle, fUrl);
			}
		});
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
}

/**Reference
 * https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
 */
package fulton.shaw.android.tellme.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import fulton.shaw.android.tellme.AskFragment;
import fulton.shaw.android.tellme.DetailPageFragment;
import fulton.shaw.android.tellme.PersonalFragment;
import fulton.shaw.android.tellme.SearchFragment;

public class CertainFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Object> mFragments=new ArrayList<Object>();
	private List<CharSequence> mTitles=new ArrayList<CharSequence>();
	public static int ASK=0,SEARCH=1,DETAIL=2,PERSONAL=3;
	
	public CertainFragmentPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
		super(fragmentManager);
		// TODO Auto-generated constructor stub
		mFragments.add(new AskFragment());
		mFragments.add(new SearchFragment());
		mFragments.add(new DetailPageFragment());
		mFragments.add(new PersonalFragment());
		
		
		mTitles.add("Ask");
		mTitles.add("Search");
		mTitles.add("Details");
		mTitles.add("Personal");
		
		for(Object f:mFragments)
		{
			((Fragment)f).setRetainInstance(true);
		}
		
	}
	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return (Fragment)mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragments.size();
	}
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return mTitles.get(position);
	}
	

}

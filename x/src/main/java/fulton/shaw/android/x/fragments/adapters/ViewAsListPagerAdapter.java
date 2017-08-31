package fulton.shaw.android.x.fragments.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import fulton.shaw.android.x.activities.ViewAsListUsingPages;
import fulton.shaw.android.x.fragments.FragmentApplyFilterCondition;
import fulton.shaw.android.x.fragments.FragmentShowDataListGroupDate;
import fulton.shaw.android.x.fragments.FragmentShowConstantItemsList;
import fulton.shaw.android.x.fragments.FragmentShowDataSearchingPage;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/22/2017.
 */

/**
 *    all,constant,search list.
 */
public class ViewAsListPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentShowDataListGroupDate mGroupByDateFragment;
    private FragmentShowConstantItemsList mConstantFragment;
    private FragmentShowDataSearchingPage mSearchFragment;

    private ArrayList<FragmentApplyFilterCondition> mFragments;
    private Bundle mArgs;


    public ViewAsListPagerAdapter(FragmentManager fm,int argFunction,String argTable,long argInnerId) {
        super(fm);
        mFragments=new ArrayList<FragmentApplyFilterCondition>(){{
            for(int i=0;i<getCount();i++)
                add(null);
        }};
        mArgs=new Bundle();
        mArgs.putInt(ViewAsListUsingPages.ARG_FUNCTION,argFunction);
        mArgs.putString(ViewAsListUsingPages.ARG_TABLE_NAME,argTable);
        mArgs.putLong(ViewAsListUsingPages.ARG_INNER_ID,argInnerId);
    }

    @Override
    public Fragment getItem(int position) {
        Util.logi("getting item at:"+position);
        FragmentApplyFilterCondition fragment=mFragments.get(position);
        if(fragment==null)
        {
            if(position==0)
            {
                fragment=mGroupByDateFragment=new FragmentShowDataListGroupDate();
            }else if(position==1){
                fragment=mConstantFragment=new FragmentShowConstantItemsList();
            }else if(position==2){
                fragment=mSearchFragment=new FragmentShowDataSearchingPage();
            }else{
                throw new IndexOutOfBoundsException(position+" is out of bound");
            }
            fragment.setArguments(mArgs);
            mFragments.set(position,fragment);
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return 3;
    }

    public void notifyWhichShouldUpdate(int[] which)
    {
        for(int i:which)
        {
            if(mFragments.get(i)!=null)
                mFragments.get(i).refreshDataSet();
        }
    }


}

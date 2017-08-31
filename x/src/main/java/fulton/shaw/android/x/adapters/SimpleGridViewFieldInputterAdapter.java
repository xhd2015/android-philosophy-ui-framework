package fulton.shaw.android.x.adapters;

import android.app.Activity;

import java.util.ArrayList;

import fulton.util.android.aware.ActivityAware;

/**
 * Created by 13774 on 7/29/2017.
 */

/**
 * fields that are simple based on their types.
 *      int --> edit of number
 *      time --> (simple)
 *              year & month & date + those(expanded)
 *    titles
 */
public class SimpleGridViewFieldInputterAdapter implements ActivityAware<Activity> {
    public static class SimpleFieldsInfo{
        public String mFiledName;
        public boolean mShown;
        public String mFiledShownText;

        public SimpleFieldsInfo(String mFiledName, boolean mShown, String mFiledShownText) {
            this.mFiledName = mFiledName;
            this.mShown = mShown;
            this.mFiledShownText = mFiledShownText;
        }
    }

    private Activity mActivity;
    private ArrayList<SimpleFieldsInfo> mFieldsInfo;



    public SimpleGridViewFieldInputterAdapter(Activity activity)
    {
        this.mActivity=activity;
        mFieldsInfo=new ArrayList<>();
    }
    public ArrayList<SimpleFieldsInfo> add(String field){
        return add(new SimpleFieldsInfo(field,true,field));
    }
    public ArrayList<SimpleFieldsInfo> add(String field,boolean shown,String shownText)
    {
        return add(new SimpleFieldsInfo(field,shown,shownText));
    }
    public ArrayList<SimpleFieldsInfo> add(SimpleFieldsInfo info)
    {
        this.mFieldsInfo.add(info);
        return mFieldsInfo;
    }

    @Override
    public Activity getContextActivity() {
        return mActivity;
    }
}

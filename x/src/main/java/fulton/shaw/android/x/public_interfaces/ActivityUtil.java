package fulton.shaw.android.x.public_interfaces;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import fulton.shaw.android.x.activities.AddGeneralRecordTagBlogActivity;
import fulton.shaw.android.x.activities.AddGeneralRecordTagSaySomethingVeryNewActivity;
import fulton.shaw.android.x.activities.ViewAsListActivity;
import fulton.shaw.android.x.activities.ViewAsListUsingPages;
import fulton.shaw.android.x.activities.ViewSubitemDetailActivity;
import fulton.shaw.android.x.fragments.MainTaggedViewProcessor;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/18/2017.
 */

public class ActivityUtil {

    /**
     *
     * @param argTableName
     *          allowed {@link ViewSubitemDetailActivity}
     * @param argInnerId
     */
    public static void startViewDetailActivity(Activity who,String argTableName, long argInnerId)
    {
        Intent intent=new Intent(who,ViewSubitemDetailActivity.class);
        intent.putExtra(ViewSubitemDetailActivity.ARG_TABLE_NAME,argTableName);
        intent.putExtra(ViewSubitemDetailActivity.ARG_INNER_ID,argInnerId);
        who.startActivity(intent);
    }

    public static void startAcitivty(Activity who,Class<? extends Activity> clz)
    {
        Intent intent=new Intent(who,clz);
        who.startActivity(intent);
    }
    public static void startSelectReferenceActivity(Activity who,String argTableName, long argInnerId)
    {
        who.startActivity(getSelectReferenceActivityIntent(who,argTableName,argInnerId));
    }
    public static Intent getSelectReferenceActivityIntent(Activity who,String argTableName, long argInnerId)
    {

        Intent intent=new Intent(who, ViewAsListUsingPages.class);
        intent.putExtra(ViewAsListUsingPages.ARG_FUNCTION,ViewAsListUsingPages.FUNCTION_SELECT);
        intent.putExtra(ViewAsListUsingPages.ARG_TABLE_NAME,argTableName);
        intent.putExtra(ViewAsListUsingPages.ARG_INNER_ID,argInnerId);
        return intent;
    }

    /**
     *  if id!=-1,then it will be updated.
     */
    public static Intent getAddActivity(Activity who,String table,@Nullable String mainTag,long id)
    {
        Intent intent=null;
        if(SqliteHelper.TABLE_GENERAL_RECORDS.equals(table))
        {
            if(SqlUtil.TAG_SAY_SOMETHING.equals(mainTag))
            {
                intent=new Intent(who, AddGeneralRecordTagSaySomethingVeryNewActivity.class);
                intent.putExtra(AppConfig.ARG_ID,id);
            }else if(SqlUtil.TAG_BLOG.equals(mainTag)){
                intent=new Intent(who, AddGeneralRecordTagBlogActivity.class);
                intent.putExtra(AppConfig.ARG_ID,id);
            }else{
                throw new MainTaggedViewProcessor.UnsupportedMainTagError(mainTag);
            }
        }else{
            throw new MainTaggedViewProcessor.UnsupportedTableError(table);
        }
        return intent;
    }

    public static void abortApp()
    {
        if(true)
            throw new RuntimeException("App called to end itself");
    }


}

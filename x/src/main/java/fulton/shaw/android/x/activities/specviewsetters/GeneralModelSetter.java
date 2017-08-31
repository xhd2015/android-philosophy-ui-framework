package fulton.shaw.android.x.activities.specviewsetters;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.models.SqliteTableModelGetter;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCompletor;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.util.android.interfaces.typetransfers.TransferStringToLongDateTime;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/19/2017.
 */

public class GeneralModelSetter {

    /**
     *
     * @param activity
     * @param cursor
     * @return null
     */
    public static View getSetViewForModel(Activity activity,String table,Cursor cursor)
    {
        if(cursor.getCount()!=1)//有数据错误
        {
            return null;
        }else{
            cursor.moveToFirst();
            if(SqliteHelper.TABLE_GENERAL_RECORDS.equals(table))
            {
                String mainTag=cursor.getString(cursor.getColumnIndex(SqlUtil.COL_MAIN_TAG));
                if(SqlUtil.TAG_SAY_SOMETHING.equals(mainTag))
                {
                    View view=getViewForSaySomething(activity);
                }
            }
            return null;
        }
    }

    //====for saysomething
    public static View getViewForSaySomething(Activity activity)
    {
        return activity.getLayoutInflater().inflate(R.layout.show_list_detail_model_say_something,null,false);
    }
    public static ArrayList<ViewGetter> getViewGettersForSaySomething()
    {
        return ViewGetter.getViewGettersBaseOnKey(
                SqliteTableModelGetter.get(SqliteHelper.TABLE_GENERAL_RECORDS),
                SqlUtil.COL_CREATED_TIME,ViewGetter.IdViewGetter(R.id.createdTimeTextView,TextView.class),
                SqlUtil.COL_DETAIL,ViewGetter.IdViewGetter(R.id.detailTextView,TextView.class)
                );
    }
    public static void setTransfersForSaySomething(SingleViewAutoCompletor completor)
    {
        completor.setTransfersBasedOnKey(
                SqlUtil.COL_CREATED_TIME,new TransferStringToLongDateTime()
        );
    }
}

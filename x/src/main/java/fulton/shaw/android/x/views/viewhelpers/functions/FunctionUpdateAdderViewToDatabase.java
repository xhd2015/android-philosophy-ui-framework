package fulton.shaw.android.x.views.viewhelpers.functions;

import android.content.ContentValues;
import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/18/2017.
 */

/**
 *  the update keys is used to denote which columns should be obtained.If a key is not in it,it will be deleted.
 * @param <V>
 */
public class FunctionUpdateAdderViewToDatabase<V extends AdapterView> extends AdapterViewFunction<V> {
    private ArrayList mClear;
    private String[] mUpdateKeys;
    public FunctionUpdateAdderViewToDatabase(ArrayList clear)
    {
        this(clear,(String[])null);
    }
    public FunctionUpdateAdderViewToDatabase(ArrayList clear,String... updateKeys)
    {
        mClear=clear;
        mUpdateKeys=updateKeys;
    }
    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        ContentValues contentValues=null;
        try {
            contentValues=wrapper.collectAdderValue(ContentValues.class);
            if(mUpdateKeys!=null)
                SqlUtil.removeNonExistKeysForContentValues(contentValues,mUpdateKeys);
            if(wrapper.updateItem(position, contentValues))
            {
                wrapper.setAdderShown(false);
                wrapper.fillAdderValue(mClear);
                wrapper.setArgOpViewIndex(-1);
            }else{
                wrapper.onCommitDatabaseFailed();
            }
        }catch (ShareableSingleViewAutoCollector.ActionAbort e){
            wrapper.onCollectValueFromAdderViewFailed();
        }

    }
}

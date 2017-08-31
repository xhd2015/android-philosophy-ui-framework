package fulton.shaw.android.x.views.viewhelpers.functions;

import android.content.ContentValues;
import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/18/2017.
 */

public class FunctionAddAdderViewToDatabase<V extends AdapterView> extends AdapterViewFunction<V> {
    private ArrayList mClear;
    public FunctionAddAdderViewToDatabase(ArrayList clear)
    {
        mClear=clear;
    }
    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        ContentValues value=null;
        try {
            value=wrapper.collectAdderValue(ContentValues.class);
            if(wrapper.addItem(value))
            {
                wrapper.setAdderShown(false);
                wrapper.fillAdderValue(mClear);
            }else{
                wrapper.onCommitDatabaseFailed();
            }
        }catch (ShareableSingleViewAutoCollector.ActionAbort e)
        {
            wrapper.onCollectValueFromAdderViewFailed();
        }
    }
}

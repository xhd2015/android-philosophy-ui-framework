package fulton.shaw.android.x.activities.specfunctions;

import android.content.ContentValues;
import android.widget.AdapterView;
import android.widget.Toast;

import fulton.shaw.android.x.public_interfaces.ActivityUtil;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;
import fulton.shaw.android.x.views.viewhelpers.functions.AdapterViewFunction;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/20/2017.
 */

public class FunctionViewDetailThroughReference<V extends AdapterView> extends AdapterViewFunction<V> {
    private String mTable;
    private long mInnerId;

    public FunctionViewDetailThroughReference(String table, long id)
    {
        mTable=table;
        mInnerId=id;
    }
    public FunctionViewDetailThroughReference()
    {
        this(null,-1);
    }

    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        ContentValues values=wrapper.collectItemValueFromCursor(position,ContentValues.class);//The value is full.
        String refTable=values.getAsString(SqlUtil.COL_REF_TABLE_NAME);
        long refId=values.getAsLong(SqlUtil.COL_REF_INNER_ID);
        if(refTable.equals(mTable) && mInnerId==refId)
        {
            Toast.makeText(wrapper.getActivity(),"链接指向当前页面",Toast.LENGTH_SHORT).show();
        }else{
            ActivityUtil.startViewDetailActivity(wrapper.getActivity(),refTable,refId);
        }
    }
}

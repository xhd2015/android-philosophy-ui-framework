package fulton.shaw.android.x.activities.specfunctions;

import android.content.Intent;
import android.widget.AdapterView;

import fulton.shaw.android.x.public_interfaces.ActivityUtil;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;
import fulton.shaw.android.x.views.viewhelpers.functions.AdapterViewFunction;

/**
 * Created by 13774 on 8/20/2017.
 */

public class FunctionSelectReferences<V extends AdapterView> extends AdapterViewFunction<V> {
    private String mTable;
    private long mInnerId;

    public FunctionSelectReferences(String table, long id)
    {
        mTable=table;
        mInnerId=id;
    }
    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        Intent intent=ActivityUtil.getSelectReferenceActivityIntent(wrapper.getActivity(),mTable,mInnerId);
        wrapper.getActivity().startActivity(intent);
    }
}

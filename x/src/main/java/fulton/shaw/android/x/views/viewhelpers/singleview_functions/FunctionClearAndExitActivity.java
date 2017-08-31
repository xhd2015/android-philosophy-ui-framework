package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import android.app.Activity;

import java.util.ArrayList;

import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionClearAndExitActivity extends FunctionExitActivity {
    private ArrayList mClear;
    public FunctionClearAndExitActivity(Activity activity, ArrayList clear) {
        super(activity);
        mClear=clear;
    }

    @Override
    public void apply(SingleViewAutoCollector collector) {
        collector.fillValue(mClear);
        super.apply(collector);
    }
}

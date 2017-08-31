package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import java.util.ArrayList;

import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionClear extends DatabaseModelViewFunction {

    private ArrayList mClear;
    public FunctionClear(ArrayList clear){mClear=clear;}

    @Override
    public void apply(SingleViewAutoCollector collector) {
        collector.fillValue(mClear);
    }
}

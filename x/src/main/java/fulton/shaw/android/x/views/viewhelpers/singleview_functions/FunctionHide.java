package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionHide extends DatabaseModelViewFunction {
    @Override
    public void apply(SingleViewAutoCollector collector) {
        ViewUtil.setShown(collector.getRootView(),false);
    }
}

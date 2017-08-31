package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionShownHideSwitcher extends DatabaseModelViewFunction {
    public FunctionShownHideSwitcher(){}
    @Override
    public void apply(SingleViewAutoCollector collector) {
        ViewUtil.switchShown(collector.getRootView());
    }
}

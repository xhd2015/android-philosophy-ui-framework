package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import android.app.Activity;

import fulton.shaw.android.x.public_interfaces.ActivityUtil;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionExitActivity extends DatabaseModelViewFunction {
    private Activity mActivity;

    public FunctionExitActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void apply(SingleViewAutoCollector collector) {
        mActivity.finish();
    }
}

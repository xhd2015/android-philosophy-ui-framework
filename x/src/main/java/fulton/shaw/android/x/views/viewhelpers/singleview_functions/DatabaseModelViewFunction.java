package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import android.view.View;

import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/22/2017.
 */

public abstract class DatabaseModelViewFunction {
    public abstract void apply(SingleViewAutoCollector collector);

    /**
     *  button,as usual,is used to click to perform some functions.
     */
    public static void setButtonFunction(View view, final SingleViewAutoCollector collector, final DatabaseModelViewFunction function)
    {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function.apply(collector);
            }
        });
    }
}

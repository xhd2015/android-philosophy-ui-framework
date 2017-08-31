package fulton.util.android.interfaces.multiview_valuegetter;

import java.util.Calendar;

import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.views.interfaces.ViewTimeAware;

/**
 * Created by 13774 on 8/25/2017.
 */

public final class ViewValueGetterOfTimeAware extends MultiViewValueGetter<Single<ViewTimeAware>,Calendar> {
    private static ViewValueGetterOfTimeAware ourInstance = null;

    public static ViewValueGetterOfTimeAware getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewValueGetterOfTimeAware();
        return ourInstance;
    }

    private ViewValueGetterOfTimeAware() {
    }

    @Override
    public void setValue(Single<ViewTimeAware> viewTimeAwareSingle, Calendar value) {
        viewTimeAwareSingle.first.setTime(value);
    }

    @Override
    public Calendar getValue(Single<ViewTimeAware> viewTimeAwareSingle) {
        return viewTimeAwareSingle.first.getTime();
    }

    @Override
    public Class<Calendar> getValueType() {
        return Calendar.class;
    }
}

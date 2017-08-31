package fulton.util.android.views.interfaces;

import java.util.Calendar;

/**
 * Created by 13774 on 8/9/2017.
 */

public interface ViewTimeAware {
    void setTime(Calendar calendar);
    Calendar getTime();
}

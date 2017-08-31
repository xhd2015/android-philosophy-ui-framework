package fulton.util.android.aware;

import android.content.SharedPreferences;

/**
 * Created by 13774 on 8/5/2017.
 */

public interface PrefSettingsAware {
    SharedPreferences getActivityPref();
    SharedPreferences.Editor getActivityPrefEditor();
}

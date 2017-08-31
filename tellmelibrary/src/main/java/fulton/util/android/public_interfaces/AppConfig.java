package fulton.util.android.public_interfaces;

/**
 * Created by 13774 on 7/28/2017.
 */

import fulton.util.android.aware.FirstRunningAware;

/**
 * static configurations
 */
public interface AppConfig extends FirstRunningAware{

    Object getConfig(String key);
    String getConfigAsString(String key);
    int     getConfigAsInt(String key);
    double getConfigAsDouble(String key);



}

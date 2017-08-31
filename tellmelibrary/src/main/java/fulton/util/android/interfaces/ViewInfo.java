package fulton.util.android.interfaces;

/**
 * Created by 13774 on 8/13/2017.
 */

public class ViewInfo {
    public String key;
    public Class  typeClass;

    public ViewInfo(String key, Class typeClass) {
        this.key = key;
        this.typeClass = typeClass;
    }
}

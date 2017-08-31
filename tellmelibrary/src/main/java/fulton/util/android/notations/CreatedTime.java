package fulton.util.android.notations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by 13774 on 8/24/2017.
 */

@Retention(RetentionPolicy.SOURCE)
public @interface CreatedTime {
    String value();
}

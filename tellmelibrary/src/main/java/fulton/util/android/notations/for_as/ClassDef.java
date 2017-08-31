package fulton.util.android.notations.for_as;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by 13774 on 8/19/2017.
 */

@Retention(SOURCE)
@Target({ANNOTATION_TYPE})
public @interface ClassDef {
    /** Defines the allowed constants for this element */
    Class[] value() default {};

}

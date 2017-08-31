package fulton.util.android.notations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 13774 on 8/18/2017.
 */

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD,ElementType.LOCAL_VARIABLE,ElementType.PACKAGE,ElementType.TYPE})
public @interface TestNote {
   /**
    *   {@link STATE#TESTED_SUCCESS_WEAK} means though tested successfully,but the test cases is not considered enought to ensure that.
    */
   enum STATE{NOT_STARTED,IN_TESTING,TESTED_FAILED,TESTED_SUCCESS_STRONG,TESTED_SUCCESS_WEAK};

   STATE value() default STATE.NOT_STARTED;

}

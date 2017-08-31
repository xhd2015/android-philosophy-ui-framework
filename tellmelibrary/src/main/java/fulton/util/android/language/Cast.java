package fulton.util.android.language;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by 13774 on 7/30/2017.
 */

public class Cast<E> {

    public static Cast<TextView> getTextViewCaster()
    {
        return new Cast<TextView>();
    }

    public static Cast<RadioButton> getRadioButtonCaster()
    {
        return new Cast<RadioButton>();
    }
    public E cast(Object o)
    {
        return (E)o;
    }
}

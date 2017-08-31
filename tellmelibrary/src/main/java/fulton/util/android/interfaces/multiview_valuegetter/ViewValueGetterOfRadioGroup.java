package fulton.util.android.interfaces.multiview_valuegetter;

import android.widget.RadioGroup;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public final class ViewValueGetterOfRadioGroup extends MultiViewValueGetter<Single<RadioGroup>,Integer> {
    private static ViewValueGetterOfRadioGroup ourInstance = null;

    public static ViewValueGetterOfRadioGroup getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewValueGetterOfRadioGroup();
        return ourInstance;
    }

    private ViewValueGetterOfRadioGroup() {

    }

    @Override
    public void setValue(Single<RadioGroup> radioGroupSingle, Integer value) {
        radioGroupSingle.first.check(value);
    }

    @Override
    public Integer getValue(Single<RadioGroup> radioGroupSingle) {
        return radioGroupSingle.first.getCheckedRadioButtonId();
    }

    @Override
    public Class<Integer> getValueType() {
        return Integer.class;
    }
}

package fulton.util.android.interfaces.multiview_valuegetter;

import android.widget.Spinner;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public final class ViewValueGetterOfSpinner extends MultiViewValueGetter<Single<Spinner>,Integer>{
    private static ViewValueGetterOfSpinner ourInstance = null;

    public static ViewValueGetterOfSpinner getInstance() {
        if(ourInstance==null)
            ourInstance=new ViewValueGetterOfSpinner();
        return ourInstance;
    }

    private ViewValueGetterOfSpinner() {
    }

    @Override
    public Integer getValue(Single<Spinner> spinnerSingle) {
        return spinnerSingle.first.getSelectedItemPosition();
    }

    @Override
    public void setValue(Single<Spinner> spinnerSingle, Integer value) {
        spinnerSingle.first.setSelection(value);
    }

    @Override
    public Class<Integer> getValueType() {
        return Integer.class;
    }
}

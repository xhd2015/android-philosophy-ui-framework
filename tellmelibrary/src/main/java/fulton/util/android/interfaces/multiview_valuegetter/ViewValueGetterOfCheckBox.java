package fulton.util.android.interfaces.multiview_valuegetter;

import android.content.Intent;
import android.widget.CheckBox;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

class ViewValueGetterOfCheckBox extends MultiViewValueGetter<Single<CheckBox>,Boolean>{
    private static ViewValueGetterOfCheckBox ourInstance = null;

    static ViewValueGetterOfCheckBox getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewValueGetterOfCheckBox();
        return ourInstance;
    }

    private ViewValueGetterOfCheckBox() {
    }

    @Override
    public void setValue(Single<CheckBox> checkBoxSingle, Boolean value) {
        checkBoxSingle.first.setChecked(value);
    }

    @Override
    public Boolean getValue(Single<CheckBox> checkBoxSingle) {
        return checkBoxSingle.first.isChecked();
    }

    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }
}

package fulton.util.android.interfaces.multiview_valuegetter;

import android.view.View;
import android.widget.CheckBox;

import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */


public class ViewValueGetterWithCheckBox<E> extends MultiViewValueGetter<Single<View>,E> {
    private CheckBox mCheckBox;
    private MultiViewValueGetter<Single<View>,E> mTruelyGetter;

    public ViewValueGetterWithCheckBox(CheckBox checkBox, MultiViewValueGetter<? extends Single<View>, E> truelyGetter) {
        mCheckBox = checkBox;
        mTruelyGetter = (MultiViewValueGetter<Single<View>, E>) truelyGetter;
    }


    @Override
    public void setValue(Single<View> viewSingle, E value) {
        if(value!=null)
        {
            mCheckBox.setChecked(true);
            mTruelyGetter.setValue(viewSingle,value);
        }else{
            mCheckBox.setChecked(false);
        }
    }

    @Override
    public E getValue(Single<View> viewSingle) {
        return mCheckBox.isChecked()?mTruelyGetter.getValue(viewSingle):null;
    }

    @Override
    public Class<E> getValueType() {
        return mTruelyGetter.getValueType();
    }
}

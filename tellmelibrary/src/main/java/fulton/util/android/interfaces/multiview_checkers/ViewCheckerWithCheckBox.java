package fulton.util.android.interfaces.multiview_checkers;

import android.view.View;
import android.widget.CheckBox;

import fulton.util.android.interfaces.multiview_valuegetter.MultiViewValueGetter;
import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ViewCheckerWithCheckBox extends MultiViewChecker<Single<View>> {
    private CheckBox mCheckBox;
    private MultiViewChecker<Single<View>> mTruelyChecker;

    public ViewCheckerWithCheckBox(CheckBox checkBox, MultiViewChecker<Single<View>> truelyChecker) {
        mCheckBox = checkBox;
        mTruelyChecker = truelyChecker;
    }

    @Override
    public boolean check(Single<View> viewSingle) {
        if(mCheckBox.isChecked())
            return mTruelyChecker.check(viewSingle);
        else
            return true;
    }
}

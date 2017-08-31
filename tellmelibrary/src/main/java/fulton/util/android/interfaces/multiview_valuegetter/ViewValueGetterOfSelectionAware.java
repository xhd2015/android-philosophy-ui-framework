package fulton.util.android.interfaces.multiview_valuegetter;

import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.views.interfaces.ViewTitleSelectionAware;

/**
 * Created by 13774 on 8/25/2017.
 */

class ViewValueGetterOfSelectionAware extends MultiViewValueGetter<Single<ViewTitleSelectionAware>,Integer>{
    private static ViewValueGetterOfSelectionAware ourInstance = null;

    public static ViewValueGetterOfSelectionAware getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewValueGetterOfSelectionAware();
        return ourInstance;
    }

    private ViewValueGetterOfSelectionAware() {
    }

    @Override
    public void setValue(Single<ViewTitleSelectionAware> viewTitleSelectionAwareSingle, Integer value) {
        viewTitleSelectionAwareSingle.first.setTitleSelection(value);
    }

    @Override
    public Integer getValue(Single<ViewTitleSelectionAware> viewTitleSelectionAwareSingle) {
        return viewTitleSelectionAwareSingle.first.getTitleSelection();
    }

    @Override
    public Class<Integer> getValueType() {
        return Integer.class;
    }
}

package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import fulton.shaw.android.x.views.ImageIndicator;

/**
 * Created by 13774 on 8/13/2017.
 */

public class ImageIndicatorIntegerAdapter extends ViewDatabaseAdapter<ImageIndicator,Integer> {
    public ImageIndicatorIntegerAdapter(ImageIndicator view) {
        super(view);
    }

    @Override
    public Integer getValue() {
        return mView.getCurrentSelected();
    }

    @Override
    public void setValue(Integer value) {
        if(value!=null)
            mView.setCurrentSelected(value);
    }
}

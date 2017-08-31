package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.widget.RatingBar;

/**
 * Created by 13774 on 8/13/2017.
 */

public class RatingBarFloatAdapter extends ViewDatabaseAdapter<RatingBar,Float> {
    public RatingBarFloatAdapter(RatingBar view) {
        super(view);
    }

    @Override
    public Float getValue() {
        return mView.getRating();
    }

    @Override
    public void setValue(Float value) {
        if(value!=null)
            mView.setRating(value);
    }
}

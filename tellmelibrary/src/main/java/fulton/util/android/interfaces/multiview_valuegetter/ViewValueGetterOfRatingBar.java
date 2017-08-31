package fulton.util.android.interfaces.multiview_valuegetter;

import android.widget.RatingBar;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ViewValueGetterOfRatingBar extends MultiViewValueGetter<Single<RatingBar>,Float> {
    private static ViewValueGetterOfRatingBar ourInstance = null;

    public static ViewValueGetterOfRatingBar getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewValueGetterOfRatingBar();
        return ourInstance;
    }

    private ViewValueGetterOfRatingBar() {
    }

    @Override
    public void setValue(Single<RatingBar> ratingBarSingle, Float value) {
        ratingBarSingle.first.setRating(value);
    }

    @Override
    public Float getValue(Single<RatingBar> ratingBarSingle) {
        return ratingBarSingle.first.getRating();
    }

    @Override
    public Class<Float> getValueType() {
        return Float.class;
    }
}

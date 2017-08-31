package fulton.util.android.interfaces.multiview_valuegetter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.views.interfaces.ViewTimeAware;
import fulton.util.android.views.interfaces.ViewTitleSelectionAware;

/**
 * Created by 13774 on 8/25/2017.
 */

/**
 *
 * @param <I>
 * @param <E>
 * @param <GetterType>  the getter type that is applied.
 */
public class ViewValueGetterOfFixed<I extends Single<? extends View>,E,GetterType extends MultiViewValueGetter<I,E>>
        extends MultiViewValueGetter<I,E> {

    private I mViews;
    private MultiViewValueGetter<I,E> mGetter;

    public ViewValueGetterOfFixed(I views, MultiViewValueGetter<I, E> getter) {
        mViews = views;
        mGetter = getter;
    }
    public ViewValueGetterOfFixed(View v)
    {
        mViews=(I)new Single<View>(v);
        mGetter=(MultiViewValueGetter<I,E>)getInstanceOfSingleView(v.getClass());//use default getter
    }


    public void setValue(E value) {
        mGetter.setValue(mViews,value);
    }


    public E getValue() {
        return mGetter.getValue(mViews);
    }


    @Override
    public void setValue(I i, E value) {
        mGetter.setValue(i,value);
    }

    @Override
    public E getValue(I i) {
        return mGetter.getValue(i);
    }

    @Override
    public Class<E> getValueType() {
        return mGetter.getValueType();
    }

    public I getViews() {
        return mViews;
    }

    public MultiViewValueGetter<I, E> getGetter() {
        return mGetter;
    }


    public static <V extends View,E>  MultiViewValueGetter getInstanceOfSingleView(Class<V> clz)
    {
        if(ViewTitleSelectionAware.class.isAssignableFrom(clz)) {
            return ViewValueGetterOfSelectionAware.getInstance();
        }else if(ViewTimeAware.class.isAssignableFrom(clz)){
            return ViewValueGetterOfTimeAware.getInstance();
        }else if(CheckBox.class.isAssignableFrom(clz)) {
            return ViewValueGetterOfCheckBox.getInstance();
        }else if(TextView.class.isAssignableFrom(clz)) {
            return ViewValueGetterOfTextView.getInstance();
        }else if(RadioGroup.class.isAssignableFrom(clz)){
            return ViewValueGetterOfRadioGroup.getInstance();
        }else if(Spinner.class.isAssignableFrom(clz)) {
            return ViewValueGetterOfSpinner.getInstance();
        }else if(RatingBar.class.isAssignableFrom(clz)) {
            return ViewValueGetterOfRatingBar.getInstance();
        }else{
            throw new UnsupportedClassVersionError(clz+" is not supported");
        }
    }
}

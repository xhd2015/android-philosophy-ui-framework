package fulton.shaw.android.x.views.viewhelpers;

import android.support.annotation.IdRes;
import android.view.View;

import fulton.util.android.notations.HelperStaticMethods;

/**
 * Created by 13774 on 8/11/2017.
 */

public class ViewHelper<E> {
    public View mContainer;
    public E    mListener;

    public ViewHelper(View container)
    {
        mContainer=container;
    }

    /**
     * Returns the visibility status for this view.
     *
     * @return One of {@link View#VISIBLE}, {@link View#INVISIBLE}, or {@link View#GONE}.
     * @attr ref android.R.styleable#View_visibility
     */
    @HelperStaticMethods
    public static int getVisibility(boolean shown)
    {
        return shown?View.VISIBLE:View.GONE;
    }

    public View getContainer()
    {
        return mContainer;
    }
    @SuppressWarnings("ResourceType")
    public void setShown(boolean shown)
    {
        mContainer.setVisibility(getVisibility(shown));
    }
    public void setListener(E listener) {
        mListener = listener;
    }
    public E getListener(){return mListener;}
    public View findViewById(@IdRes int id) {
        return mContainer.findViewById(id);
    }

}

package fulton.shaw.android.tellme.experiment.viewhelpers;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import fulton.util.android.event.GenericNotifier;

/**
 * Created by 13774 on 7/17/2017.
 *
 *
 * ViewHelper should always be responsible for retreiving data from the view,and pass them to those requests them.
 * In anther word, ViewHelper is a proxy between a view and anther Object.All operations Object can do to the view must
 * be proxied by ViewHelper.
 */

public abstract class ViewHelper extends GenericNotifier {
    /**
     * view on which all sub views can be found
     */
    protected View mTargetRootView;

    /**
     * activity context
     */
    protected Activity mActivity;

    /**
     * Fast updater
     */
    protected Handler mFastUpdater;


    /**
     * inflater
     */
    protected LayoutInflater mInflater;


    public ViewHelper(Activity activity,View rootView,int eventCount) {
        super(eventCount);
        mActivity=activity;
        mTargetRootView=rootView;

        mFastUpdater=new Handler(mActivity.getMainLooper());
        mInflater=mActivity.getLayoutInflater();
    }

}

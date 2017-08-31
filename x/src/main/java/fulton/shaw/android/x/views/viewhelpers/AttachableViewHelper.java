package fulton.shaw.android.x.views.viewhelpers;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 13774 on 8/14/2017.
 */

public class AttachableViewHelper {

    protected ViewGroup parentView;
    protected View v;

    public AttachableViewHelper(View view)
    {
        v=view;
    }

    public void attach(ViewGroup parent,int i)
    {
        if(parentView!=null)
            detach();
        parentView=parent;
        parentView.addView(v,i);
    }
    public void attach(ViewGroup parent)
    {
        attach(parent,-1);
    }
    public void detach()
    {
        parentView.removeView(v);
        parentView=null;
    }
    public boolean isAttached()
    {
        return parentView!=null;
    }


}

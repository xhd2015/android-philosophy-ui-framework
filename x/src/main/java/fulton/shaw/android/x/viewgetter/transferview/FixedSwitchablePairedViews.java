package fulton.shaw.android.x.viewgetter.transferview;

import android.view.View;

import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/14/2017.
 */

/**
 *  First is treat as MODE_1,MODE_2
 * @param <ViewType1>
 * @param <ViewType2>
 * @param <ValueType>
 */
public class FixedSwitchablePairedViews<ViewType1 extends View,ViewType2 extends View,ValueType>
        extends ShareableSwitchablePairedViews<ViewType1,ViewType2,ValueType> {
        protected ViewType1 mView1;
        protected ViewType2 mView2;


    public FixedSwitchablePairedViews(ViewType1 view1, ViewType2 view2, Class<ValueType> valueClass,@ViewIndex int mode) {
        super((Class<ViewType1>)view1.getClass(), (Class<ViewType2>) view2.getClass(), valueClass, mode);
        mView1=view1;
        mView2=view2;
        boolean view1Shown=mCurrentView==VIEW_1?true:false;
        ViewUtil.setShown(view1,view1Shown);
        ViewUtil.setShown(view2,!view1Shown);
    }

    public void switchMode()
    {
        switchMode(mView1,mView2);
    }
    public ViewType1 getView1() {
        return mView1;
    }

    public void setView1(ViewType1 view1) {
        mView1 = view1;
    }

    public ViewType2 getView2() {
        return mView2;
    }

    public void setView2(ViewType2 view2) {
        mView2 = view2;
    }
}

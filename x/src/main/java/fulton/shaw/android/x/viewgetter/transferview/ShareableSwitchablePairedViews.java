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
public class ShareableSwitchablePairedViews<ViewType1 extends View,ViewType2 extends View,ValueType>
        extends ShareablePairedViewsValueTransfer<ViewType1,ViewType2,ValueType> {



    public ShareableSwitchablePairedViews(Class<ViewType1> v1Class,Class<ViewType2> v2Class, Class<ValueType> valueClass, @ViewIndex int mode) {
        super(v1Class,v2Class, valueClass, mode);
    }

    public void switchMode(ViewType1 view1,ViewType2 view2)
    {
        ValueType value=getValue(view1,view2);
        mCurrentView=1-mCurrentView;
        boolean view1Shown=mCurrentView==VIEW_1?true:false;
        ViewUtil.setShown(view1,view1Shown);
        ViewUtil.setShown(view2,!view1Shown);
        setValue(view1,view2,value);
    }
}

package fulton.shaw.android.x.viewgetter.transferview;

import android.view.View;

/**
 * Created by 13774 on 8/14/2017.
 */

public class FixedPairedViewsValueTransfer<ViewType1 extends View,ViewType2 extends View,ValueType>
        extends ShareablePairedViewsValueTransfer<ViewType1,ViewType2,ValueType>  {
    protected ViewType1 mView1;
    protected ViewType2 mView2;

    public FixedPairedViewsValueTransfer(ViewType1 view1,ViewType2 view2, Class<ValueType> valueClass, @ViewIndex int initView) {
        super((Class<ViewType1>)view1.getClass(), (Class<ViewType2>)view2.getClass(), valueClass, initView);
        mView1=view1;
        mView2=view2;
    }

    public FixedPairedViewsValueTransfer(ViewType1 view1,ViewType2 view2, Class<ValueType> valueClass) {
        this(view1,view2,valueClass,VIEW_1);
    }

    public ValueType getValue() {
        return super.getValue(mView1,mView2);
    }

    public void setValue(ValueType value) {
        super.setValue(mView1,mView2, value);
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

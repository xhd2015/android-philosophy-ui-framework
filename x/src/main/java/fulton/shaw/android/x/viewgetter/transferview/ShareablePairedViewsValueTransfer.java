package fulton.shaw.android.x.viewgetter.transferview;

import android.support.annotation.IntDef;
import android.view.View;

import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;

/**
 * Created by 13774 on 8/14/2017.
 */


public class ShareablePairedViewsValueTransfer<ViewType1 extends View,ViewType2 extends View,ValueType> {
    public static class NoSuchViewIndexError extends Error{
        public NoSuchViewIndexError(int errIndex)
        {
            super("view index="+errIndex);
        }
    }

    protected ShareableViewValueTransfer<ViewType1,ValueType> mTransfer1;
    protected ShareableViewValueTransfer<ViewType2,ValueType> mTransfer2;

    protected int mCurrentView;

    public static final int VIEW_1=0;
    public static final int VIEW_2=1;

    public ShareablePairedViewsValueTransfer(Class<ViewType1> v1Class,Class<ViewType2> v2Class,Class<ValueType> valueClass,@ViewIndex int initView)
    {
        mTransfer1=new ShareableViewValueTransfer<ViewType1, ValueType>(v1Class,valueClass);
        mTransfer2=new ShareableViewValueTransfer<ViewType2, ValueType>(v2Class,valueClass);
        mCurrentView=initView;
    }
    public ShareablePairedViewsValueTransfer(Class<ViewType1> v1Class,Class<ViewType2> v2Class,Class<ValueType> valueClass)
    {
        this(v1Class,v2Class,valueClass,VIEW_1);
    }



    public void setTypeTransfer(@ViewIndex int viewIndex,ValueTypeTransfer<?, ValueType> valueTypeTransfer)
    {
        if(viewIndex==VIEW_1)
            mTransfer1.setTypeTransfer(valueTypeTransfer);
        else if(viewIndex==VIEW_2)
            mTransfer2.setTypeTransfer(valueTypeTransfer);
    }

    public ValueTypeTransfer<Object,ValueType> getTypeTransfer(@ViewIndex int viewIndex)
    {
        if(viewIndex==VIEW_1)
            return mTransfer1.getTypeTransfer();
        else if(viewIndex==VIEW_2)
            return mTransfer2.getTypeTransfer();
        throw new NoSuchViewIndexError(viewIndex);
    }

    @IntDef({VIEW_1,VIEW_2})
    public @interface ViewIndex{

    }
    /**
     *
     * @param currentView One of {@link #VIEW_1} or {@link #VIEW_2}
     */
    public void setCurrentView(@ViewIndex int currentView)
    {
        mCurrentView=currentView;
    }
    public @ViewIndex  int getCurrentView()
    {
        return mCurrentView;
    }

    public ValueType getValue(ViewType1 v1,ViewType2 v2)
    {
        if(mCurrentView==VIEW_1)
            return mTransfer1.getValue(v1);
        else if(mCurrentView==VIEW_2)
            return mTransfer2.getValue(v2);
        throw new NoSuchViewIndexError(mCurrentView);
    }
    public void setValue(ViewType1 v1,ViewType2 v2,ValueType value)
    {
        if(mCurrentView==VIEW_1)
            mTransfer1.setValue(v1,value);
        else if(mCurrentView==VIEW_2)
            mTransfer2.setValue(v2,value);
    }
}

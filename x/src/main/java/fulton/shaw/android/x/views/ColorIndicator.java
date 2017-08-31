package fulton.shaw.android.x.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.views.interfaces.ViewModifiable;
import fulton.shaw.android.x.views.interfaces.ViewSwitchNext;
import fulton.util.android.utils.Util;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/9/2017.
 */

public class ColorIndicator extends View implements ViewModifiable,ViewSwitchNext{

    protected int[] mColorList;
    /**
     * -1 to indicate invalid
     */
    protected int mCurrentIndex;
    protected boolean mModifiable;

    public ColorIndicator(Context context) {
        super(context);
        init(context,null);
    }

    public ColorIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ColorIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public ColorIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }
    protected void init(Context context,AttributeSet attrs)
    {
        mModifiable=true;
        if(attrs!=null)
        {
            TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomViews,0,0);
            try{
                mModifiable=a.getBoolean(R.styleable.CustomViews_modifiable,true);
                int colorListId=a.getResourceId(R.styleable.CustomViews_colorList,0);
                if(colorListId!=0) {
                    mColorList = ViewUtil.typedArrayToColorList(getResources(),colorListId);
                    this.setBackgroundColor(mColorList[mCurrentIndex]);
                }
                mCurrentIndex=a.getInteger(R.styleable.CustomViews_selectedIndex,mColorList.length>0?0:-1);
            }finally {
                a.recycle();
            }
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchNext();
            }
        });
    }

    @Override
    public void setModifiable(boolean modifiable) {
        mModifiable=modifiable;
    }

    @Override
    public boolean isModifiable() {
        return mModifiable;
    }

    @Override
    public int getCurrentSelected() {
        return mCurrentIndex;
    }
    public void setCurrentSelected(int currentIndex) {
        if(mModifiable) {
            mCurrentIndex = currentIndex;
            this.setBackgroundColor(mColorList[currentIndex]);
        }
    }

    @Override
    public void switchNext() {
        if(mModifiable) {
            mCurrentIndex++;
            if (mCurrentIndex == mColorList.length)
                mCurrentIndex = 0;
            this.setBackgroundColor(mColorList[mCurrentIndex]);
        }
    }

    public int[] getColorList() {
        return mColorList;
    }

    public void setColorList(int[] colorList) {
        mColorList = colorList;
        this.setCurrentSelected(0);
    }
}

package fulton.shaw.android.x.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
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

public class ImageIndicator extends AppCompatImageView implements ViewSwitchNext,ViewModifiable{
    protected int[] mImageRes;
    protected int mCurrentSelected;
    protected boolean mModifiable;

    public ImageIndicator(Context context) {
        super(context);
        init(context,null);
    }

    public ImageIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ImageIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    protected void init(Context context,AttributeSet attrs)
    {
        if(attrs!=null)
        {
            TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomViews,0,0);
            try{
                mModifiable=a.getBoolean(R.styleable.CustomViews_modifiable,false);
                mCurrentSelected=a.getInteger(R.styleable.CustomViews_selectedIndex,0);

                int imageListId=a.getResourceId(R.styleable.CustomViews_imageResList,0);
                if(imageListId!=0) {
                    mImageRes = ViewUtil.typedArrayToResourceIdList(getResources(),imageListId);
                    Util.logi("res[0],true[0]="+mImageRes[0]+","+android.R.drawable.alert_dark_frame);
                    this.setImageResource(mImageRes[mCurrentSelected]);
                }
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
    public int getCurrentSelected() {
        return mCurrentSelected;
    }

    @Override
    public void setCurrentSelected(int index) {
        if(mModifiable)
        {
            this.setImageResource(mImageRes[index]);
        }
    }

    @Override
    public void switchNext() {
        if(mModifiable)
        {
            mCurrentSelected++;
            if(mCurrentSelected==mImageRes.length)
                mCurrentSelected=0;
            this.setImageResource(mImageRes[mCurrentSelected]);
        }
    }

    @Override
    public void setModifiable(boolean modifiable) {
        mModifiable=modifiable;
    }

    @Override
    public boolean isModifiable() {
        return mModifiable;
    }
    public int[] getImageResList() {
        return mImageRes;
    }

    public void setImageResList(int[] imageRes) {
        mImageRes = imageRes;
        this.setCurrentSelected(0);
    }
}

package fulton.shaw.android.x.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import fulton.shaw.android.x.R;
import fulton.util.android.utils.ViewUtil;

public class SwitchReflectedTextView extends TextSwitchableTextView{

    private View mRefView;
    private int mRefId;

    public SwitchReflectedTextView(Context context) {
        super(context);
    }

    public SwitchReflectedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchReflectedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs, int defStyle) {
        super.init(attrs, defStyle);
        checkTitlesAndThrow(getTitles());
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SwitchReflectedTextView, defStyle, 0);
        mRefId=a.getResourceId(R.styleable.SwitchReflectedTextView_refId,0);
        if(mRefId==0)
            throw new NullPointerException("You must specific a valid reference id");

        a.recycle();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup parent= (ViewGroup) getParent();
        if(parent==null)
            throw new NullPointerException("Parent cannot be null and must be ViewGroup.");
        mRefView=parent.findViewById(mRefId);
        if(mRefView==null)
            throw new NullPointerException("Reference view cannot be null.");
        ViewUtil.setShown(mRefView, getTitleSelection()==1);

    }

    @Override
    protected void invalidateState() {
        if(mRefView!=null)
            ViewUtil.setShown(mRefView, getTitleSelection()==1);
        super.invalidateState();
    }

    @Override
    public void setTitles(String[] titles) {
        checkTitlesAndThrow(titles);
        super.setTitles(titles);
    }

    @Override
    public void switchTitle() {
        ViewUtil.switchShown(mRefView);
        super.switchTitle();
    }

    private void checkTitlesAndThrow(String[] titles)
    {
        if(titles.length!=2)
            throw new Error("For switcher,you must specify exactly 2 titles, even if they are the same.");
    }
}
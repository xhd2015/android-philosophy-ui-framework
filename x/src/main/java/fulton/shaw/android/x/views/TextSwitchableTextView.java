package fulton.shaw.android.x.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import fulton.shaw.android.x.R;
import fulton.util.android.views.interfaces.ViewTitleSelectionAware;

/**
 * Created by 13774 on 8/15/2017.
 *
 *  titles can be spilted by ';;;', such as "title1;;;title2"
 */

public class TextSwitchableTextView extends AppCompatTextView implements ViewTitleSelectionAware{
    public static abstract class OnSwitchTitleListener {

         //lastListener call it if you processed.
        public OnSwitchTitleListener lastListener;

        /**
         *
         * @param view
         */
        public  abstract void onSwitchTitle(TextSwitchableTextView view);

        protected void doOnSwitchTitle(TextSwitchableTextView view)
        {
            onSwitchTitle(view);
            if(lastListener!=null)
                lastListener.doOnSwitchTitle(view);
        }
    }
    protected String[] mTitles;
    protected int mSelection;
    protected OnSwitchTitleListener mGivenListener;
    public static final String TITLE_SPLITOR=";;;";

    public TextSwitchableTextView(Context context) {
        super(context);
        init(null,0);
    }

    public TextSwitchableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public TextSwitchableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TextSwitchableTextView, defStyle, 0);
        if(a.getType(R.styleable.TextSwitchableTextView_titles)== TypedValue.TYPE_STRING){
            String s=a.getString(R.styleable.TextSwitchableTextView_titles);
            mTitles=s.split(TITLE_SPLITOR);
        }else {
            mTitles = (String[]) a.getTextArray(R.styleable.TextSwitchableTextView_titles);
        }
        mSelection = a.getInt(R.styleable.TextSwitchableTextView_selection,0);
        if(mTitles==null||mTitles.length==0) {
            mTitles = new String[]{"No Titles Given"};
            mSelection = 0;
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTitle();
            }
        });
        invalidateState();
    }



    public void switchTitle()
    {
        int sel=mSelection+1;
        if(sel==mTitles.length)
            sel=0;
        setTitleSelection(sel);
    }
    public void setTitles(String[] titles)
    {
        if(titles==null||titles.length==0)
            throw new UnsupportedOperationException("title cannot be null and its length cannot be 0");
        mTitles=titles;
        invalidateState();
    }
    protected void invalidateState()
    {
        this.setText(mTitles[mSelection]);
    }
    public void setTitleSelection(int selection)
    {
        if(mSelection!=selection)
        {
            mSelection=selection;
            invalidateState();
            if(mGivenListener!=null)
            {
                mGivenListener.doOnSwitchTitle(TextSwitchableTextView.this);
            }
        }
    }

    public String[] getTitles() {
        return mTitles;
    }

    public int getTitleSelection() {
        return mSelection;
    }

    public void setOnSwitchListener(OnSwitchTitleListener listener) {
        listener.lastListener=mGivenListener;
        mGivenListener = listener;
    }
}

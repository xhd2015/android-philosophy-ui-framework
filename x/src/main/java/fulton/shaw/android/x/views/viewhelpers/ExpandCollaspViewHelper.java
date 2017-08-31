package fulton.shaw.android.x.views.viewhelpers;

import android.view.View;

import fulton.shaw.android.x.R;

/**
 * Created by 13774 on 8/11/2017.
 */

/**
 *   a viewgroup containing 2 ids.
 */
public class ExpandCollaspViewHelper extends ViewHelper<ExpandCollaspViewHelper.OnExpandCollaspListener> {
    public interface OnExpandCollaspListener{
        void onExpand(ExpandCollaspViewHelper helper);
        void onCollasp(ExpandCollaspViewHelper helper);
    }

    protected View mExpandView;
    protected View mCollaspView;
    protected boolean mExpand;
    public static final int COLLASP_ID= R.id.collasp;
    public static final int EXPAND_ID=R.id.expand;




    public ExpandCollaspViewHelper(View container)
    {
        this(container,true);
    }
    @SuppressWarnings("ResourceType")
    public ExpandCollaspViewHelper(View contaier, final boolean expand)
    {
        super(contaier);
        mExpandView=contaier.findViewById(EXPAND_ID);
        mCollaspView=contaier.findViewById(COLLASP_ID);
        mExpandView.setVisibility(ViewHelper.getVisibility(expand));
        mCollaspView.setVisibility(ViewHelper.getVisibility(!expand));

        mContainer.setClickable(true);
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpand(!mExpand);
            }
        });
        mExpand=!expand;
        setExpand(expand);
    }


    /**
     *  behavior is  also triggered
     * @param expand
     */
    @SuppressWarnings("ResourceType")
    public void setExpand(boolean expand)
    {
        if(mExpand==expand)return;
        mExpand=expand;
        mExpandView.setVisibility(ViewHelper.getVisibility(mExpand));
        mCollaspView.setVisibility(ViewHelper.getVisibility(!mExpand));
        if(mListener!=null)
        {
            if(mExpand)
                mListener.onExpand(ExpandCollaspViewHelper.this);
            else
                mListener.onCollasp(ExpandCollaspViewHelper.this);
        }
    }
    public boolean isExpand()
    {
        return mExpand;
    }
}

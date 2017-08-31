package fulton.shaw.android.x.views.viewhelpers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import fulton.shaw.android.x.R;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/11/2017.
 */

/**
 *
 */
public class PositiveNagetiveViewHelper extends ViewHelper<PositiveNagetiveViewHelper.OnClickPositiveNagetiveListener>{
    public interface OnClickPositiveNagetiveListener{
            void onClickPositive(PositiveNagetiveViewHelper helper);
            void onClickNagetive(PositiveNagetiveViewHelper helper);
    }
    public ViewGroup mCustomViewContainer;
    public View mPositive;
    public View mNagetive;

    public PositiveNagetiveViewHelper(View container) {
        super(container);
        mCustomViewContainer = (ViewGroup) mContainer.findViewById(R.id.customViewContainer);
        mPositive=mContainer.findViewById(R.id.positiveButton);
        mNagetive=mContainer.findViewById(R.id.nagetiveButton);

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==mPositive)
                {
                    if(mListener!=null)
                        mListener.onClickPositive(PositiveNagetiveViewHelper.this);
                }else if(v==mNagetive){
                    if(mListener!=null)
                        mListener.onClickNagetive(PositiveNagetiveViewHelper.this);
                }
            }
        };
        mPositive.setClickable(true);
        mNagetive.setClickable(true);
        mPositive.setOnClickListener(listener);
        mNagetive.setOnClickListener(listener);
    }
    public void addCustomView(View customView)
    {
        mCustomViewContainer.addView(customView);
    }


}

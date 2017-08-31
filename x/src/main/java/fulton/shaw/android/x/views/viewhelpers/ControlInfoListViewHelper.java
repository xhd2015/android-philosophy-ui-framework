package fulton.shaw.android.x.views.viewhelpers;

/**
 * Created by 13774 on 8/11/2017.
 */

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import fulton.shaw.android.x.R;

/**
 *   a view container:
 *      the title       --  titleTextView
 *      the icon        --  iconImageView(optional)
 *      a collasp button  -- switchButton(optional)
 *      a list view     -- contentListView, adapter is defined yourself.
 *      an adder view   container --  the adder view is defined yourself.
 *      an add button   -- addButton
 *
 */
public class ControlInfoListViewHelper<AdapterViewType extends AdapterView> extends ViewHelper<ControlInfoListViewHelper.OnEventControlInfoListener>{
    public interface OnEventControlInfoListener {
        public void onClickAdd(ControlInfoListViewHelper helper);
    }
    public TextView mTitle;
    public ImageView mIcon;
    public View mSwitchButton;
    public AdapterViewType mContentList;
    public View mAddButton;

    //set this yourself.
    public PositiveNagetiveViewHelper mAdderViewHelper;
    public ExpandCollaspViewHelper mCollasperHelper;


    @SuppressLint("WrongViewCast")
    public ControlInfoListViewHelper(View container)
    {
        super(container);
        mTitle= (TextView) container.findViewById(R.id.titleTextView);
        mIcon= (ImageView) container.findViewById(R.id.iconImageView);
        mSwitchButton=container.findViewById(R.id.switchButton);
        mContentList= (AdapterViewType) container.findViewById(R.id.contentAdapterView);
        mAdderViewHelper=new PositiveNagetiveViewHelper(container.findViewById(R.id.adderViewContainer));
        mCollasperHelper=new ExpandCollaspViewHelper(container.findViewById(R.id.switchButton));

        mCollasperHelper.setListener(new ExpandCollaspViewHelper.OnExpandCollaspListener() {
            @Override
            public void onExpand(ExpandCollaspViewHelper helper) {
                mContentList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCollasp(ExpandCollaspViewHelper helper) {
                mContentList.setVisibility(View.GONE);
            }
        });
        mAddButton=container.findViewById(R.id.addButton);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null)
                    mListener.onClickAdd(ControlInfoListViewHelper.this);
            }
        });
    }

}

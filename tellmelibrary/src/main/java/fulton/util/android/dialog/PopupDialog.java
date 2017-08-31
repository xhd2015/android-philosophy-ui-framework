package fulton.util.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import fulton.shaw.android.tellmelibrary.R;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 7/28/2017.
 */

/**
 *  title is it self
 *
 *  its view must contain a TextView named as "R.id.dialog_title"
 */
public class PopupDialog extends Dialog {

    TextView mTitleView;
    final LinearLayout mRootView;
    int mTitleIndex,mContentIndex;

    public PopupDialog(@NonNull Context context) {
        super(context);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.popupdialog_layout);

        mTitleIndex=0;
        mContentIndex=1;

        mTitleView= (TextView) super.findViewById(R.id.popup_dialog_title);
        mRootView= (LinearLayout) super.findViewById(R.id.popup_dialog_container);

        Util.logi(" index 0==Title?"+(mTitleView==mRootView.getChildAt(0)));
        Util.logi(" index 1==null?"+(mRootView.getChildAt(1)));
        Util.logi("child count="+mRootView.getChildCount());

        Util.logi("mRootView==null?"+(mRootView==null));
    }

    private static void replace(LinearLayout layout,int index,View v)//v must be view,or id
    {
        View view=layout.getChildAt(index);
        if(view!=null) {
            layout.removeView(layout.getChildAt(index));
//            layout.removeViewAt(index);
        }
        layout.addView(v,index);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        this.setContentView(getLayoutInflater().inflate(layoutResID,mRootView,false));

    }

    @Override
    public void setContentView(@NonNull View view) {
        replace(mRootView,mContentIndex,view);
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        view.setLayoutParams(params);
        this.setContentView(view);
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        mTitleView.setText(titleId);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        mTitleView.setText(title);
    }

    public View getContentView()
    {
        return mRootView.getChildAt(mContentIndex);
    }

    public TextView getTitleView()
    {
        return mTitleView;
    }

    public void setTitleView(TextView v)
    {
        replace(mRootView,mTitleIndex,v);
        mTitleView=v;
    }
    public void setTitleView(int resId)//resId must textview
    {
        setTitleView((TextView) getLayoutInflater().inflate(resId,mRootView,false));
    }

    public LinearLayout getRootView()
    {
        return mRootView;
    }

    //
    public static void HowToUse()
    {
        /**
        PopupDialog pdialog=new PopupDialog(this);
        pdialog.setContentView(R.layout.select_types_layout);

        final GridView container= (GridView) pdialog.getContentView();
        container.setNumColumns(3);
//        pdialog.setTitle("Great");
        pdialog.setTitleView(R.layout.my_popup_dialog_title_layout);
        pdialog.setTitle("Hello");
//        pdialog.getRootView().setBackgroundColor(getResources().getColor(R.color.transparent));

        container.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return container.getNumColumns()*1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null)
                {
                    convertView=getLayoutInflater().inflate(R.layout.select_types_subitem,parent,false);
                }

                return convertView;
            }
        });

        pdialog.show();
         */
    }
}

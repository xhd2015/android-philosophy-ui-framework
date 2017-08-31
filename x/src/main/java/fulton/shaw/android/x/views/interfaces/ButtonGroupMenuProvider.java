package fulton.shaw.android.x.views.interfaces;

import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/17/2017.
 */

public class ButtonGroupMenuProvider extends SingleMenuProvider  {

    private ViewGroup mContainer;
    private ArrayList<View> mButtonList;


    private boolean     mIsAppended;
    private ViewGroup    argAppendView;
    private int         argAppendIndex;



    public ButtonGroupMenuProvider(ViewGroup container, ArrayList<View> buttonList)
    {
        mContainer=container;
        mButtonList = buttonList;
        mIsAppended=false;
    }
    public ButtonGroupMenuProvider()
    {
        this(null,new ArrayList<View>());
    }

    public void addButton(View v)
    {
        if(mButtonList==null)
            mButtonList=new ArrayList<>();
        mButtonList.add(v);
    }
    public ArrayList<View> getButtonList()
    {
        return mButtonList;
    }

    @Override
    public void onRequestMenuShow() {//we need to know show on which  rooter.
        if(mIsAppended) { //from another
            if(argAppendView != mContainer.getParent())
                ViewUtil.removeViewFromItsParent(mContainer);
            else
                return;//already shown
        }
        mIsAppended=true;
        argAppendView.addView(mContainer);
    }

    @Override
    public void onRequestMenuHide() {
        ViewUtil.removeViewFromItsParent(mContainer);
        mIsAppended=false;
    }


    @Override
    public int getActionsNumber() {
        return mButtonList.size();
    }

    @Override
    public boolean isShown() {
        return mIsAppended;
    }

    public ViewGroup getArgAppendView() {
        return argAppendView;
    }

    public void setArgAppendView(ViewGroup argAppendView) {
        this.argAppendView = argAppendView;
    }

    public int getArgAppendIndex() {
        return argAppendIndex;
    }

    public void setArgAppendIndex(int argAppendIndex) {
        this.argAppendIndex = argAppendIndex;
    }

    public ViewGroup getContainerView() {
        return mContainer;
    }
    public void setConainterView(ViewGroup view)
    {
        mContainer=view;
    }
}

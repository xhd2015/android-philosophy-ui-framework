package fulton.util.android.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import fulton.shaw.android.tellmelibrary.R;
import fulton.util.android.notations.HelperStaticMethods;

/**
 * Created by 13774 on 8/8/2017.
 */

public class ViewUtil {

    @HelperStaticMethods
    public static View findParentWithId(View view, int id)
    {
        if(view==null)return null;
        View viewParent=null;
        while( (viewParent=(View) view.getParent())!=null && viewParent.getId()!=id);
        return viewParent;
    }


    public static <E> E findCastView(View rootView,@IdRes int id, Class<E> eClass)
    {
        return (E)rootView.findViewById(id);
    }
    public static RadioGroup getViewAsRadioGroup(View view)
    {
        return  (RadioGroup) view;
    }
    public static TextView getViewAsTextView(View view)
    {
        return  (TextView) view;
    }
    public static EditText getViewAsEditText(View view)
    {
        return (EditText) view;
    }
    public static CheckBox getViewAsCheckBox(View view)
    {
        return (CheckBox) view;
    }
    public static ViewGroup getViewAsViewGroup(View view)
    {
        return (ViewGroup) view;
    }
    public static Spinner getViewAsSpinner(View view)
    {
        return (Spinner) view;
    }

    public static LinearLayout getViewAsLinearLayout(View view)
    {
        return (LinearLayout)view;
    }

    public static int[] typedArrayToResourceIdList(Resources res, int id)
    {
        TypedArray array=res.obtainTypedArray(id);
        int[] result=new int[array.length()];
        for(int i=0;i<result.length;i++)
            result[i]=array.getResourceId(i,0);//array.
        array.recycle();
        return result;
    }
    public static int[] typedArrayToColorList(Resources res, int id)
    {
        TypedArray array=res.obtainTypedArray(id);
        int[] result=new int[array.length()];
        for(int i=0;i<result.length;i++)
            result[i]=array.getColor(i,0);
        array.recycle();
        return result;
    }

    public static View getContentViewOfActivity(Activity activity)
    {
        ViewGroup contentViewContainer= (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        return contentViewContainer.getChildAt(0);
    }

    /**
     * ensure that content view is a view group
     * @param activity
     * @param view
     */
    public static void addViewToContentViewOfActivity(Activity activity,View view)
    {
        ViewGroup container= (ViewGroup) getContentViewOfActivity(activity);
        container.addView(view);
    }

    /**
     *
     * @param viewGroup
     * @param count  max count that will be got.
     * @return
     */
    public static int getChildHeightCount(ViewGroup viewGroup,int count)
    {
        int heigthCount=0;
        for(int i=0;i<count;i++)
        {
            if(i<viewGroup.getChildCount())
            {
                heigthCount+=viewGroup.getChildAt(i).getLayoutParams().height;
            }
        }
        return heigthCount;
    }

    public static void setViewGroupMaxHeight(ViewGroup rootView,int max)
    {
        rootView.getLayoutParams().height = getChildHeightCount(rootView,max==-1?rootView.getChildCount():max);
        rootView.requestLayout();
    }

    /**
     *   after calling this,you must call view's requestlayout.
     *
     *    must be call from UiThread
     * @param view
     * @param unitHeight
     * @param minItems
     * @param maxItems if is it -1,then all will be counted.
     *
     *                 condiiont: getCount <= min -- min used
     *                              getCount > min && getCount<=max
     *                                      use getCount
     *                              getCount>max
     *                                      use max
     */
    public static void setListViewHeightRange(ListView view,int unitHeight,int minItems, int maxItems)
    {
        ViewGroup.LayoutParams lp=view.getLayoutParams();
        int cnt=view.getCount();
        if(maxItems==-1) //correct max
            maxItems=cnt;
        if(minItems<=0 || minItems>maxItems) //correct min to ensure it in range 1~maxItems
            minItems=maxItems;

        int choosedItems=minItems;
        if(cnt > minItems && cnt < maxItems)
            choosedItems=cnt;
        else if(cnt > maxItems)
            choosedItems=maxItems;

        int realHeight=choosedItems*unitHeight;
        if(lp==null)
        {
            lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,realHeight);
            view.setLayoutParams(lp);
        }else{
            lp.height=realHeight;
            view.requestLayout();
        }
    }

    public static int getValidViewTag(int i)
    {
        return (i<<24)+2;
    }

    public static void setViewInScope(View view)
    {
        view.getParent().requestChildFocus(view,view);
    }

    public static void setShown(View view,boolean shown)
    {
        view.setVisibility(shown?View.VISIBLE:View.GONE);
    }
    public static void switchShown(View view)
    {
        setShown(view,!isShown(view));
    }
    public static boolean isShown(View view)
    {
        return view.getVisibility()==View.VISIBLE?true:false;
    }
    public static void removeViewFromItsParent(View view)
    {
        ViewParent parent=view.getParent();
        if(parent!=null && parent instanceof  ViewGroup)
        {
            ViewGroup parentView= (ViewGroup) parent;
            parentView.removeView(view);
        }
    }
    public static void removeViewFromIstParent(View view,int i)
    {
        ViewParent parent=view.getParent();
        if(parent!=null && parent instanceof  ViewGroup)
        {
            ViewGroup parentView= (ViewGroup) parent;
            parentView.removeViewAt(i);
        }
    }

    public static View[] findViews(View root,int[] idList)
    {
        View[] list=new View[idList.length];
        for(int i=0;i<idList.length;i++)
            list[i]=root.findViewById(idList[i]);
        return list;
    }
}

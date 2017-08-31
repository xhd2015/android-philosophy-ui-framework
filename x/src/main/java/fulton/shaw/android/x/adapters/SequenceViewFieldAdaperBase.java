package fulton.shaw.android.x.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.notations.ConvienenceForGrammarUsingArray;

/**
 * Created by 13774 on 8/8/2017.
 *
 * can be used with Listview
 *                  LinearLayout
 */

public abstract class SequenceViewFieldAdaperBase extends BaseAdapter {
    //===inner classes
    public interface ActionAfterEachGetView{
        void performAction(AdapterFieldsInfo info);
    }


    protected Context mContext;
    protected LayoutInflater mInflater;
    protected HashMap<String,Object> mVariables;
    protected ActionAfterEachGetView mActioner;
    protected ArrayList<AdapterFieldsInfo> mFieldsInfo;
    protected ArrayList<Integer> mPositionToFieldsIndex;


    public void setActionAfterEachGetView(ActionAfterEachGetView actioner) {
        this.mActioner = actioner;
    }

    public SequenceViewFieldAdaperBase(Activity activity)
    {
        this(activity,activity.getLayoutInflater());
    }
    public SequenceViewFieldAdaperBase(Context context,LayoutInflater inflater)
    {
        this.mContext=context;
        this.mInflater=inflater;
        mVariables=new HashMap<>();
         mFieldsInfo=new ArrayList<>();
        mPositionToFieldsIndex =new ArrayList<>();
    }
    @Override
    public int getCount() {
//        Util.logi("getCount");
        return mPositionToFieldsIndex.size();
    }

    protected void updatePositionMaps()
    {
        mPositionToFieldsIndex.clear();
        int i=0;
        for(AdapterFieldsInfo info:mFieldsInfo)
        {
            if(info.showType !=info.SHOW_TYPE_NOT_SHOWN) {
                mPositionToFieldsIndex.add(i);
            }
            i++;
        }
    }
    public ArrayList<Integer> getPositionToFieldsIndex() {
        return mPositionToFieldsIndex;
    }
    public ArrayList<AdapterFieldsInfo> getFieldsInfo() {
        return mFieldsInfo;
    }
    /**
     * info = null means the end
     * @param info
     * @return
     */
    public SequenceViewFieldAdaperBase add(AdapterFieldsInfo info)
    {
        if(info==null)
        {
            updatePositionMaps();
        }else {
            mFieldsInfo.add(info);
        }
        return this;
    }

    @ConvienenceForGrammarUsingArray
    public SequenceViewFieldAdaperBase add(Object[] object)
    {
        return add(new AdapterFieldsInfo(object));
    }


    public SequenceViewFieldAdaperBase addAll(AdapterFieldsInfo[] infos)
    {
        for(AdapterFieldsInfo info:infos)
        {
            add(info);
        }
        updatePositionMaps();
        return this;
    }
    public SequenceViewFieldAdaperBase addAll(Object[][] objects)
    {
        return addAll(AdapterFieldsInfo.initFields(objects));
    }

    @Override
    public Object getItem(int position) {
        return mFieldsInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * this should only be called once.
     * it is very simple and proper to be called exactly only once.
     * @param holder
     */
    public void feedToViewGroup(ViewGroup holder)//when refresh,you must remove those viewes again.
    {
        int count=getCount();
        for(int i=0;i<count;i++)
        {
            holder.addView(getView(i,null,holder));
        }
    }
}

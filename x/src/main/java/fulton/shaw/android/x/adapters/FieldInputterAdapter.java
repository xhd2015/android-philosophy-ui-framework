package fulton.shaw.android.x.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.shaw.android.x.R;
import fulton.util.android.utils.Util;
import fulton.util.android.aware.ActivityAware;
import fulton.util.android.notations.DefaultBehaviourFor;
import fulton.util.android.notations.OverrideToCustomBehaviour;

/**
 * Created by 13774 on 7/28/2017.
 */

//targetting on a grid view or something else,to do this,you must let me know when to getByGetter title,and when to getByGetter a content
    //by default,it targets on a gridview,where position%2=0 -->name  position%2=1-->content

/**
 * 处理好surface和model的关系，即要显示的东西，并非其实在。
 *
 * adapter针对一般性的fields进行内容生成。
 */
public class FieldInputterAdapter extends BaseAdapter implements ActivityAware<Activity>{

    public static class FieldsInfo{
        public String mFiledName;
        public boolean mShown;
        public String mFiledShownText;
        public ArrayList<View> mOutputRelatedViews;
        public Class mType;


        public FieldsInfo(String mFiledName, boolean mShown, String mFiledShownText) {
            this.mFiledName = mFiledName;
            this.mShown = mShown;
            this.mFiledShownText = mFiledShownText;
            this.mOutputRelatedViews = new ArrayList<>();
        }
    }
    private Activity mActivity;

    private ArrayList<FieldsInfo> mFieldsInfo;//field-->[shown or not, shown text,view groups]
    private int mLastFieldIndex;//总是指向最新的一个
    private int mShownFieldsCount;


    //生命周期   生成阶段（是否显示，生成提示标题，生成内容输入类型）  用户输入阶段（清空操作）   输出阶段（全部转化为字符串）--->与html相当相似
    //生成阶段
    //      标题
    //      内容
    //  与一个域相关的view可以分散在不同的位置，输入>=输出
    //  因此必须以域为单位进行。
    //基于baseadapter， position决定着类型的对应关系。

    @Deprecated
    private ArrayList<String> mFullFields;//every fields must be different

    //though fields may all be given,but some of them can be ignored.Thus size of mfieds does not denote shown

    private HashMap<String,Integer[]> mCustomViews;
//    private int[] defaultLayoutId,defaultNameId,defaultContentId;
    private Integer[] defaultIds;

    public static final int VIEW_TYPE_TITLE =0;
    public static final int VIEW_TYPE_CONTENT =1;
    public static final int VIEW_TYPE_OTHER =2;

    public FieldInputterAdapter(Activity activity)//it observes,specify a layout,id on it
    {
        this(activity, R.layout.field_inputter_default_name_layout,
                R.id.fieldInputterDefaultNameText,
                R.layout.field_inputter_default_content_layout,
                R.id.fieldInputterDefaultContentText
                );
    }



    public FieldInputterAdapter(Activity activity,
                                    int defaultFieldLayout,int defaultFieldTextId,
                                    int defaultContentLayout,int defaultContentTextId)
    {
        defaultIds=new Integer[]{defaultFieldLayout,defaultFieldTextId,defaultContentLayout,defaultContentTextId};
        mActivity=activity;
        mFullFields =new ArrayList<>();
        mCustomViews=new HashMap<>();
    }

    public ArrayList<String> getFields() {
        return mFullFields;
    }

    public void setFields(ArrayList<String> fields) {
        this.mFullFields = fields;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mShownFieldsCount;
    }

    private void updateShownCount()
    {
        mShownFieldsCount=0;
        for(FieldsInfo info:mFieldsInfo)
        {
            if(info.mShown)
                mShownFieldsCount++;
        }
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
    public View getView(int position, View convertView, ViewGroup parent) {//for position
        final int index=position/2;
        final int viewType=getViewType(position);
        Integer[] ids=Util.mapDefaultGet(mCustomViews, mFieldsInfo.get(index).mFiledName,defaultIds);
        final boolean useDefault=(ids==defaultIds);
        if(viewType==VIEW_TYPE_OTHER)
        {
            return getOtherView(position,convertView,parent);
        }

        if(convertView==null)
        {
            convertView=mActivity.getLayoutInflater().inflate(ids[viewType*2],parent,false);
        }
        TextView nameOrContentView= (TextView) convertView.findViewById(ids[viewType*2+1]);//getByGetter & set content for
        if(useDefault)//two of them are textviews
        {
            if(viewType==VIEW_TYPE_TITLE)//title
            {
                nameOrContentView.setText(mFieldsInfo.get(index).mFiledShownText);
            }else{//content,default is just a spinner or edittext based on the type it was given.

            }
        }else{
            if(viewType==VIEW_TYPE_TITLE)
            {

            }
        }

        return convertView;
    }

    @OverrideToCustomBehaviour
    public View getOtherView(int position,View convertView,ViewGroup parent)
    {
        return convertView;
    }

    @Override
    public Activity getContextActivity() {
        return mActivity;
    }


    @DefaultBehaviourFor("Grid View")
    @OverrideToCustomBehaviour
    public int getViewType(int position)
    {
        return position%2;
    }

    /**
     *  field分为两个部分：显示部分、数据库生成部分。
     * @param position
     * @return
     */
    @OverrideToCustomBehaviour
    public String getFieldShowText(int position)
    {
        return mFullFields.get(position);
    }


    public void clear()
    {

    }

}

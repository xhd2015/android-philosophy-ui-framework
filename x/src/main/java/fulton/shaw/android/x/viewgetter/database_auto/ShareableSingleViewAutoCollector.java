package fulton.shaw.android.x.viewgetter.database_auto;

import android.content.ContentValues;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;

import fulton.shaw.android.x.viewgetter.transferview.ShareableViewValueTransfer;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.interfaces.comparators.Comparator;
import fulton.util.android.interfaces.valueproviders.ValueProvider;
import fulton.util.android.notations.ConvienenceForGrammarUsingArray;
import fulton.util.android.notations.CreatedTime;
import fulton.util.android.notations.HelperStaticMethods;
import fulton.util.android.notations.LazyGetter;
import fulton.util.android.utils.DataUtils;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;
import fulton.shaw.android.x.viewgetter.database_auto.checkers.ShareableSingleViewGetChecker;

/**
 * Created by 13774 on 8/14/2017.
 */

/**
 *  getCheckers influence on collecting value
 *  setCheckers influence on seting value
 *
 *  Update 2017-8-24 19:31:11:
 *          this class should work perfectly with  fulton.util.android.interfaces.valueproviders.ValueProvider(@Deprecated)
 */
public class ShareableSingleViewAutoCollector {

    public class ActionAbort extends Error{
        private String mAbortKey;
        private int   mKeyIndex;
        public ActionAbort(String abortKey,int keyIndex){
            mAbortKey=abortKey;
            mKeyIndex=keyIndex;
        }

        public String getAbortKey() {
            return mAbortKey;
        }

        public int getKeyIndex() {
            return mKeyIndex;
        }
    }
    /**
     * this is saying that "please continue, better if some fatal error happens".
     * this is the default value, indicating that a view is not checked.
     */
    public static final int ACTION_IGNORE = -1;
    /**
     * @see {@link ActionAbort} if this is set,then a runtime error will be thrown if the view gets check failed.
     */
    public static final int ACTION_ABORT = 0;
    /**
     * that value will not be collected.
     * this is different with {@link #ACTION_SET_TO_NULL} in collecting as contentvalues.
     */
    public static final int ACTION_DO_NOT_SET = 1;
    public static final int ACTION_SET_TO_NULL = 2;
    /**
     *  if this action is set,then the {@link #mPresetValues} must be set also.
     */
    public static final int ACTION_SET_TO_PRESET_VALUE=3;

    @IntDef({ACTION_IGNORE,ACTION_ABORT, ACTION_DO_NOT_SET,ACTION_SET_TO_NULL,ACTION_SET_TO_PRESET_VALUE})
    public @interface  FailActions{}




    //===user given
    protected ArrayList<ViewInfo> mViewInfoList;
    protected ArrayList<ViewGetter> mViewGetters;
    private ArrayList<ShareableSingleViewGetChecker>[] mGetCheckers;

    private int[] mActionsOnCheckGetFailed;//fixed size
    private int[] mActionsOnCheckSetFailed;


    /**
     * @see {@link #setPresetValues(ArrayList)}
     */
    private ArrayList<Object> mPresetValues;

    //===generated
    protected ArrayList<ShareableViewValueTransfer> mTransfers;

    public ShareableSingleViewAutoCollector(ArrayList<ViewInfo> infoList, ArrayList<ViewGetter> viewGetters) {
        this(infoList,viewGetters, DataUtils.newList(ArrayList.class,infoList.size(),null));
    }
    public ShareableSingleViewAutoCollector(ArrayList<ViewInfo> infoList, ArrayList<ViewGetter> viewGetters,ArrayList<ShareableViewValueTransfer> transfers) {
        mViewInfoList = infoList;
        mViewGetters = viewGetters;
        mTransfers = new ArrayList<>(infoList.size());
        mGetCheckers = new ArrayList[mViewInfoList.size()];
        mTransfers=transfers;
        mActionsOnCheckGetFailed = new int[mViewInfoList.size()];
        for(int i=0;i<mActionsOnCheckGetFailed.length;i++)
            mActionsOnCheckGetFailed[i]=ACTION_IGNORE;
    }

    public ShareableSingleViewAutoCollector(ArrayList<ViewInfo> infoList) {
        this(infoList,null);
    }
    /**
     * must ensure that i is within the expected bound
     * @param rootView
     * @param i
     * @return
     */
    private boolean checkGet(View rootView,int i)
    {
        return checkGet(i,mViewGetters.get(i).getView(rootView));
    }
    private boolean checkGet(int i,View subView)
    {
        for(int j=0;j<getGetCheckerContainer(i).size();j++){
            if(!getGetCheckerContainer(i).get(j).check(subView)) {
                return false;
            }
        }
        return true;
    }

    @LazyGetter
    protected ArrayList<ShareableSingleViewGetChecker> getGetCheckerContainer(int i)
    {
        if(mGetCheckers[i]==null)
            mGetCheckers[i]=new ArrayList<ShareableSingleViewGetChecker>();
        return mGetCheckers[i];
    }


    public void addGetChecker(int i, ShareableSingleViewGetChecker checker)
    {
        getGetCheckerContainer(i).add(checker);
    }

    public <V extends View> V findViewByKey(View rootView,String key,Class<V> vClz)
    {
        return findViewByIndex(rootView,findKeyIndex(key),vClz);
    }

    public int findKeyIndex(String key)
    {
        return Util.searchArrayList(mViewInfoList,key, Comparator.getViewInfoKeyComparator());
    }
    public <V extends View> V findViewByIndex(View rootView,int index,Class<V> vClz)
    {
        return (V) mViewGetters.get(index).getView(rootView);
    }

    /**
     *
     * @param args has the format: dbKey,Checkers..., dbKey can repeat
     */
    @ConvienenceForGrammarUsingArray
    public void addGetCheckersBasedOnKey(Object...args)
    {
        for(int i=0;i<args.length;i+=2)
        {
            String key=(String)args[i];
            int index=Util.searchArrayList(mViewInfoList, key, Comparator.getViewInfoKeyComparator());
            if(index==-1)
                throw new UnsupportedOperationException(key+" is not a valid key");
            getGetCheckerContainer(index).add((ShareableSingleViewGetChecker)args[i+1]);
        }
    }
    public void setCheckGetFailedAction(int index,@ShareableSingleViewAutoCollector.FailActions int action)
    {
        mActionsOnCheckGetFailed[index]=action;
    }

    /**
     *
     * @param args has the format <dbKey,action_int{@link FailActions}>
     */
    public void setCheckGetFailedActionBasedOnKey(Object...args)
    {
        for(int i=0;i<args.length;i+=2)
        {
            String key=(String)args[i];
            int index=Util.searchArrayList(mViewInfoList, key, Comparator.getViewInfoKeyComparator());
            if(index==-1)
                throw new UnsupportedOperationException(key+" is not a valid key");
            setCheckGetFailedAction(index,(Integer)args[i+1]);
        }
    }


    public void fillPresetValue(View rootView)
    {
        if(getPresetValues()!=null)
        for(int i=0;i<mPresetValues.size();i++)
            if(checkProcessNeeded(i) && mPresetValues.get(i)!=null) {
                fillValue(rootView, i, mPresetValues.get(i));
            }
    }


    /**
     *
     * @param rootView
     * @param i
     * @param value
     * @param <E>
     */
    public <E> void fillValue(View rootView,int i,@ValueGetter.ValueGetterType E value)
    {
        Object gotValue=ValueGetter.getValueFromObjectOrGetter(mViewInfoList.get(i).key,value);
        if(gotValue==null)return;
        ShareableViewValueTransfer transfer= ensureTransferExists(i);
        transfer.setValue(mViewGetters.get(i).getView(rootView),gotValue);
    }

    /**
     *
     * @param rootView
     * @param args has the format:key,value
     * @param <E>
     */
    public <E> void fillValueBasedOnKey(View rootView,Object...args)
    {
        for(int i=0;i<args.length;i+=2)
        {
            int index=Util.searchArrayList(mViewInfoList,(String)args[i],Comparator.getViewInfoKeyComparator());
            if(index!=-1)
                fillValue(rootView,index,args[i+1]);
        }
    }
    private static Util.Comparator<ViewInfo,String> sViewInfoKeyComparator=null;
    public static Util.Comparator<ViewInfo,String> getViewInfoKeyComparator()
    {
        if(sViewInfoKeyComparator==null)
            sViewInfoKeyComparator=new Util.Comparator<ViewInfo, String>() {
                @Override
                public boolean compareEquals(ViewInfo a, String b) {
                    return a.key.equals(b);
                }
            };
        return sViewInfoKeyComparator;
    }

    protected boolean checkProcessNeeded(int i)
    {
        return mViewGetters.get(i)!=null;
    }

    public static boolean getCheckedValue(ArrayList<ViewGetter> getters,int i)
    {
        return getters==null||getters.get(i)!=null;
    }

    protected void checkProcessThrowOnError(int i)
    {
        if(!checkProcessNeeded(i))
            throw new UnsupportedOperationException("operation on index "+i+" is invalid.");
    }

    @CreatedTime("2017-8-24 19:33:29")
    public void fillValue(View rootView, ValueProvider<String,?> provider)
    {
        for(int i=0;i<mViewInfoList.size();i++)
        {
            if(checkProcessNeeded(i))
            {
                fillValue(rootView,i,provider.getValue(mViewInfoList.get(i).key,mViewInfoList.get(i).typeClass));
            }
        }
    }

    public void fillValue(View rootView,Object[] values)
    {
        for(int i=0;i<values.length;i++)
            if(checkProcessNeeded(i))
                fillValue(rootView,i,values[i]);
    }

    public void fillValue(View rootView,ArrayList list)
    {
        for(int i=0;i<list.size();i++)
            if(checkProcessNeeded(i))
                fillValue(rootView,i,list.get(i));
    }

    protected ShareableViewValueTransfer ensureTransferExists(int i)
    {
//        Util.logi("i="+i);
        checkProcessThrowOnError(i);
        ShareableViewValueTransfer transfer=mTransfers.get(i);
        if(transfer==null)
        {
            transfer=new ShareableViewValueTransfer(mViewGetters.get(i).getViewClass(),mViewInfoList.get(i).typeClass);
            mTransfers.set(i,transfer);
        }
        return transfer;
    }

    /**
     *
     * @param i
     * @param transfer  transfer is <ViewType,DatabaseType>
     */
    public void setTransfer(int i, @NonNull ValueTypeTransfer transfer)
    {
        checkProcessThrowOnError(i);
        ShareableViewValueTransfer transferInner= mTransfers.get(i);
        if(transferInner==null)
        {
            Class viewClass=mViewGetters.get(i).getViewClass();
            transferInner=new ShareableViewValueTransfer(transfer);
            mTransfers.set(i,transferInner);
        }else{
            transferInner.setTypeTransfer(transfer);
        }
    }

    /**
     *
     * @param dbKey
     * @param transfer  transfer is <ViewType,DatabaseType>
     */
    public void setTransferBasedOnKey(String dbKey, @NonNull ValueTypeTransfer transfer)
    {
        setTransfer(findKeyIndex(dbKey),transfer);
    }

    /**
     *
     * @param args has the format <key,transfer>, transfer is <ViewType,DatabaseType>
     */
    public void setTransfersBasedOnKey(Object...args)
    {
        for(int i=0;i<args.length;i+=2)
            setTransferBasedOnKey((String)args[i], (ValueTypeTransfer) args[i+1]);
    }

    /**
     *
     * @param rootView
     * @param eClass
     * @param getters  present the wanted value.only those in it who is not null will be collected
     * @param <E>
     * @return
     */
    public <E> E collectValue(View rootView, Class<E> eClass,ArrayList<ViewGetter> getters/*for check*/)
    {
        if(ContentValues.class.equals(eClass))
        {
            return (E) collectAsContentValues(rootView,getters);
        }else if(Object[].class.equals(eClass)){
            return (E) collectAsPrimitiveArray(rootView,getters);
        }else if(ArrayList.class.equals(eClass)){
            return (E) collectAsArrayList(rootView,getters);
        }
        throw new UnsupportedClassVersionError(eClass.getName()+" not supported");
    }

    public <E> E collectValue(View rootView,Class<E> eClass)
    {
        return collectValue(rootView,eClass,null);
    }

    protected Object[] collectAsPrimitiveArray(View rootView,ArrayList<ViewGetter> getters)
    {
        ArrayList<Object> defaultValue=getPresetValues();
        Object[] values=new Object[mViewInfoList.size()];
        for(int i=0;i<mViewInfoList.size();i++)
        {
            if(defaultValue!=null && defaultValue.get(i)!=null)
                values[i]= ValueGetter.getValueFromObjectOrGetter(mViewInfoList.get(i).key,defaultValue.get(i));
            if(checkProcessNeeded(i) && getCheckedValue(getters,i)) {
                ShareableViewValueTransfer transfer = ensureTransferExists(i);
                View subView=mViewGetters.get(i).getView(rootView);
                boolean checked=checkGet(i,subView);
                if(checked || mActionsOnCheckGetFailed[i]==ACTION_IGNORE)
                    values[i] = transfer.getValue(subView);
                else{
                    switch (mActionsOnCheckGetFailed[i])
                    {
                        case ACTION_ABORT:
                            throw new ActionAbort(mViewInfoList.get(i).key,i);
                        case ACTION_DO_NOT_SET:
                            break;
                        case ACTION_SET_TO_NULL:
                            values[i]=null;
                            break;
                        case ACTION_SET_TO_PRESET_VALUE:
                            values[i]=defaultValue.get(i);
                            break;
                    }
                }
            }
        }
        return values;
    }

    protected ArrayList collectAsArrayList(View rootView,ArrayList<ViewGetter> getters)
    {
        ArrayList<Object> defaultValue=getPresetValues();
        ArrayList values=new ArrayList(mViewInfoList.size());
        for(int i=0;i<mViewInfoList.size();i++) {
            if (defaultValue != null && defaultValue.get(i) != null)
                values.set(i, ValueGetter.getValueFromObjectOrGetter(mViewInfoList.get(i).key,defaultValue.get(i)));
            if (checkProcessNeeded(i) && getCheckedValue(getters, i)) {
                ShareableViewValueTransfer transfer = ensureTransferExists(i);
                View subView = mViewGetters.get(i).getView(rootView);
                boolean checked = checkGet(i, subView);
                if (checked || mActionsOnCheckGetFailed[i] == ACTION_IGNORE)
                    values.add(transfer.getValue(subView));
                else {
                    switch (mActionsOnCheckGetFailed[i]) {
                        case ACTION_ABORT:
                            throw new ActionAbort(mViewInfoList.get(i).key,i);
                        case ACTION_DO_NOT_SET:
                            values.add(null);
                        case ACTION_SET_TO_NULL:
                            values.add(null);
                            break;
                        case ACTION_SET_TO_PRESET_VALUE:
                            values.add(defaultValue.get(i));
                            break;
                    }
                }
            }
        }
        return values;
    }
    protected ContentValues collectAsContentValues(View rootView,ArrayList<ViewGetter> getters)
    {
        ArrayList<Object> defaultValue=getPresetValues();
        ContentValues values=new ContentValues();
        for(int i=0;i<mViewInfoList.size();i++)
        {
            if(defaultValue!=null && defaultValue.get(i)!=null)
                SqlUtil.putValue(values,mViewInfoList.get(i).key,
                        ValueGetter.getValueFromObjectOrGetter(mViewInfoList.get(i).key,defaultValue.get(i)));
            if(checkProcessNeeded(i) && getCheckedValue(getters,i)) {
                ShareableViewValueTransfer transfer = ensureTransferExists(i);
                View subView=mViewGetters.get(i).getView(rootView);
                boolean checked=checkGet(i,subView);
                if(checked || mActionsOnCheckGetFailed[i]==ACTION_IGNORE)
                    SqlUtil.putValue(values,mViewInfoList.get(i).key,transfer.getValue(subView));
                else{
                    switch (mActionsOnCheckGetFailed[i])
                    {
                        case ACTION_ABORT:
                            throw new ActionAbort(mViewInfoList.get(i).key,i);
                        case ACTION_DO_NOT_SET:
                            continue;
                        case ACTION_SET_TO_NULL:
                            values.putNull(mViewInfoList.get(i).key);
                            break;
                        case ACTION_SET_TO_PRESET_VALUE:
                            SqlUtil.putValue(values,mViewInfoList.get(i).key,defaultValue.get(i));
                            break;
                    }
                }
            }
        }
        return values;
    }

    public ArrayList<ViewGetter> getViewGetters() {
        return mViewGetters;
    }
    public ArrayList<ViewInfo> getViewInfoList() {
        return mViewInfoList;
    }

    public ArrayList<Object> getPresetValues() {
        return mPresetValues;
    }

    /**
     *
     * remember, this can be null.and any value in it will be overriden by the input values.
     *
     * if it is set to null, it will not be considered as valied.
     *
     *  when it is used to fill value,it behaves exactly as fill value with arraylist
     *  when it is used to collect value, its value -- not checked if it is necessary.Because some column like referenced id will not be in the valid list,
     *      but should always has a value,because it can not be null.
     *      its value is firstly placed into the value set.then if the laterly there were value for the same key,it is overriden.
     *  no matter
     *
     *
     *  UPDATE:
     *      presetValues can be {@link ValueGetter<String,Object>},that means getByGetter value from that getter dynamically.
     *      This is used to solve generate something like created time.
     *  and remember, presetValue is also values that used to 'clear' an ever modified input view.
     * @param presetValues
     */
    public void setPresetValues(ArrayList<Object> presetValues) {
        mPresetValues = presetValues;
    }
    /**
     *  format: dbKey,value
     * @param values do not
     */
    public void setPresetValuesBasedOnKey(final Object...values)
    {
        setPresetValues(genPresetValuesBasedOnKey(mViewInfoList,values));
    }

    /**
     *  format: dbKey,value
     * @param args do not
     */
    @HelperStaticMethods
    public static ArrayList genPresetValuesBasedOnKey(final ArrayList<ViewInfo> viewInfos,final Object...args)
    {
        ArrayList<Object> genValues=new ArrayList<Object>(viewInfos.size()){{
            for(int i=0;i<viewInfos.size();i++)
            {
                boolean added=false;
                for(int j=0;j<args.length;j+=2)
                {
                    String key= (String) args[j];
                    if(key.equals(viewInfos.get(i).key))
                    {
                        add(args[j+1]);
                        added=true;
                    }
                }
                if(!added)
                    add(null);
            }
        }};
        return genValues;
    }

    /**
     *
     view getters must be set before anything
     @see {@link ViewGetter#getViewGettersBaseOnKey(ArrayList, Object...)}
     * @param args  format <key,viewId,viewClass>
     */
    public void setViewGettersBasedOnKey(Object...args)
    {
        this.mViewGetters=ViewGetter.getViewGettersBaseOnKey(mViewInfoList,args);
    }

    public ArrayList<ShareableViewValueTransfer> getTransfers() {
        return mTransfers;
    }

    public void setTransfers(ArrayList<ShareableViewValueTransfer> transfers) {
        mTransfers = transfers;
    }

    /**
     *
     * @param info
     * @param args has the format: <key,ValueTypeTransfer>
     * @return
     */
    public static ArrayList<ShareableViewValueTransfer> genTransfersBasedOnKey(ArrayList<ViewInfo> info,Object...args)
    {
        ArrayList<ShareableViewValueTransfer> list=(ArrayList<ShareableViewValueTransfer>)DataUtils.newList(ArrayList.class,info.size(),null);

        for(int i=0;i<args.length;i+=2)
        {
            int index=Util.searchArrayList(info,(String)args[i],Comparator.getViewInfoKeyComparator());
            if(index!=-1)
            {
                list.set(index,new ShareableViewValueTransfer((ValueTypeTransfer)args[i+1]));
            }
        }
        return list;
    }
}

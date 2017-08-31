package fulton.shaw.android.x.viewgetter.database_auto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.viewgetter.transferview.ShareableViewValueTransfer;
import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.utils.DataUtils;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/13/2017.
 */

public class AdapterViewAutoCompletor<V extends AdapterView> extends ShareableSingleViewAutoCollector{

    protected String mTable;
    protected V mAdapterView;
    protected ViewGenerator mViewGenerator;
    protected ArrayList<ViewInfo> mInfoList;

    /**
     *  @param context
     * @param cursor
     * @param table
     * @param adapterView
     * @param itemViewGenerator  each generated item MUST have the same construct.
     * @param infoList
     */
    public AdapterViewAutoCompletor(Context context, Cursor cursor,
                                    @SqliteHelper.TableName final String table, V adapterView,
                                    ViewGenerator itemViewGenerator,
                                    ArrayList<ViewInfo> infoList,
                                    ArrayList<ViewGetter> viewGetters
                                    )
    {
        this(context,cursor,table,adapterView,itemViewGenerator,infoList,viewGetters, DataUtils.newList(ArrayList.class,infoList.size(),null));
    }

    public AdapterViewAutoCompletor(Context context, Cursor cursor,
                                    @SqliteHelper.TableName final String table, V adapterView,
                                    ViewGenerator itemViewGenerator,
                                    ArrayList<ViewInfo> infoList,
                                    ArrayList<ViewGetter> viewGetters,
                                    ArrayList<ShareableViewValueTransfer> transfers
    )
    {
        super(infoList,viewGetters,transfers);
        mTable=table;
        mInfoList=infoList;
        mAdapterView=  adapterView;
        mViewGenerator=itemViewGenerator;


        CursorAdapter cursorAdapter=new CursorAdapter(context,cursor,false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return mViewGenerator.generateView(parent);
            }

            @Override
            public void bindView(View itemView, Context context, Cursor cursor) {
                for(int i=0;i<mInfoList.size();i++)
                {
                    if(checkProcessNeeded(i)) {
                        ViewInfo info = mInfoList.get(i);
                        ShareableViewValueTransfer transfer = ensureTransferExists(i);
                        View view = mViewGetters.get(i).getView(itemView);
                        transfer.setValue(view, SqlUtil.getCursorValue(cursor, info.key, info.typeClass));
                    }
                }
            }
        };
        mAdapterView.setAdapter(cursorAdapter);
    }



    public AdapterView<CursorAdapter> getAdapterView() {
        return mAdapterView;
    }

    public Cursor getCursor()
    {
        return getAdapter().getCursor();
    }

    public void refresh()
    {
        getAdapter().notifyDataSetChanged();
    }

    @Deprecated
    public void requery()
    {
        Util.logi("before requery: size="+getAdapter().getCursor().getCount());
        getAdapter().getCursor().requery();
        Util.logi("after requery: size="+getAdapter().getCursor().getCount());
    }

    public CursorAdapter getAdapter()
    {
        return (CursorAdapter) mAdapterView.getAdapter();
    }

    public boolean deleteItem(int pos, SqliteHelper helper)
    {
        if(helper.deleteItemInTable(mTable,getAdapter().getItemId(pos))) {
            requery();
            refresh();
            return true;
        }else{
            return false;
        }
    }
    public boolean updateItem(int pos,SqliteHelper helper,ContentValues values)
    {
        if(helper.updateItemInTable(mTable,getAdapter().getItemId(pos),values))
        {
            requery();
            refresh();
            return true;
        }else{
            return false;
        }
    }
    public boolean updateItem(int pos,SqliteHelper helper,String col,String value)
    {
        if(helper.updateItemInTable(mTable,getAdapter().getItemId(pos),col,value)) {
            requery();
            refresh();
            return true;
        }else{
            return false;
        }
    }

    public boolean addItem(SqliteHelper helper,ContentValues values)
    {
        Util.logi(values);
        long id=helper.insert(mTable,null,values);
        Util.logi("id="+id);
        if(id!=-1)
        {
            requery();
            refresh();
            return true;
        }else{
            return false;
        }
    }

    //filler一定需要cursor吗？
    //可以更基础一点：没有refresh功能， 有collect功能
    public ShareableSingleViewAutoCollector getAdditionalCollector(ArrayList<ViewGetter> viewGetters)
    {
        return new ShareableSingleViewAutoCollector(mInfoList,viewGetters);
    }

    public SingleViewAutoCollector getAdditionalCollector(View rootView,ArrayList<ViewGetter> viewGetters)
    {
        return new SingleViewAutoCollector(rootView,mInfoList,viewGetters);
    }

    public SingleViewAutoCollector preparedAdditionalCollector(int pos,View rootView,ArrayList<ViewGetter> viewGetters)
    {
        SingleViewAutoCollector collector=getAdditionalCollector(rootView,viewGetters);//some values not needed,do collect do that for me
        collector.fillValue(this.collectValue(mAdapterView.getChildAt(pos),ArrayList.class,viewGetters));
        return collector;
    }

    @Deprecated/*
        mAdapterView.getChildAt nicely returns null.you must use the views you getByGetter from other place
    */
    public <E> E collectValue(int pos, Class<E> eClass) {
        return super.collectValue(mAdapterView.getChildAt(pos), eClass);//pos may be null
    }

    /**
     *   this should be called only when the cursor will completely reflect what
     *   the view holds for.If not,please use {@link #collectValue(View, Class)} instead.
     * @param pos
     * @param eClz
     * @param filterGetters
     * @param <E>
     * @return
     */
    public <E> E collectValueFromCursor(int pos,Class<E> eClz,ArrayList<ViewGetter> filterGetters)
    {
        //only arraylist supported
        Cursor cursor=getCursor();
        cursor.moveToPosition(pos);
        if(ArrayList.class.isAssignableFrom(eClz)) {
            ArrayList res = new ArrayList();
            ArrayList<ViewInfo> infolist = getViewInfoList();
            int size = infolist.size();
            for (int i = 0; i < size; i++) {
                if (getCheckedValue(filterGetters, i))
                    res.add(SqlUtil.getCursorValue(cursor, infolist.get(i).key, infolist.get(i).typeClass));
                else
                    res.add(null);
            }
            return (E) res;
        }else if(ContentValues.class.isAssignableFrom(eClz)){
            ContentValues values=new ContentValues();
            ArrayList<ViewInfo> infolist = getViewInfoList();
            int size = infolist.size();

            for (int i = 0; i < size; i++) {
                Util.logi("key:"+infolist.get(i).key);
                if (getCheckedValue(filterGetters, i)) {
                    Util.logi("passed");
                    SqlUtil.putValue(values, infolist.get(i).key, SqlUtil.getCursorValue(cursor, infolist.get(i).key, infolist.get(i).typeClass));
                }else{
                    Util.logi("no pass");
                }
            }
            return (E) values;
        }else{
            throw new UnsupportedClassVersionError(eClz+" Unsupported");
        }
    }
    public <E> E collectValueFromCursor(int pos,Class<E> eClz)
    {
        return this.collectValueFromCursor(pos,eClz,null);
    }

    @Deprecated
    public <E> E collectValue(int pos, Class<E> eClass, ArrayList<ViewGetter> getters) {
        return super.collectValue(mAdapterView.getChildAt(pos), eClass, getters);
    }

    public <E> E collectValue(View itemView,Class<E> eClass, ArrayList<ViewGetter> getters)
    {
        return super.collectValue(itemView,eClass,getters);
    }

    public <E> E collectValue(View itemView,Class<E> eClass)
    {
        return super.collectValue(itemView,eClass);
    }
    public void closeCursorIfNeeded()
    {
        Cursor c=getCursor();
        if(c!=null && !c.isClosed())
            c.close();
    }
}

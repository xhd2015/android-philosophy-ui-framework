package fulton.shaw.android.x.viewgetter.database_auto;

import android.database.Cursor;
import android.view.View;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/13/2017.
 */

/**
 *   填充单个
 *   填充列表--> cursor
 *          给定一个AdapterView，自动构造一个CursorAdapter去填充
 *
 *    共通的模式: 一个rootView,一个Cursor，一个<键,ViewGetter,类型>表, 会生成相应的ViewValueTransfer表
 *      对于single，不需要额外的信息
 *              生成SingleViewValueTransfer
 *      对于list, 需要知道是哪个adapter view，以及其item的获取方式。
 *              生成ShareableViewValueTransfer
 *
 */


public class SingleViewAutoCompletor extends SingleViewAutoCollector{
    //given by caller
    protected String mTable;
    protected Cursor mModelCursor;

    public SingleViewAutoCompletor(Cursor cursor, String table, View rootView, ArrayList<ViewInfo> infoList, ArrayList<ViewGetter> viewGetters)
    {
        super(rootView,infoList,viewGetters);
        mModelCursor=cursor;
        mTable=table;
    }
    public SingleViewAutoCompletor(Cursor cursor, String table, View rootView, ArrayList<ViewInfo> infoList)
    {
        super(rootView,infoList);
        mModelCursor=cursor;
        mTable=table;
    }


    public static class CannotRefreshDataError extends Error{}
    public void refresh()throws CannotRefreshDataError
    {
        if(mModelCursor.moveToFirst())
        {
            for(int i=0;i<mViewInfoList.size();i++)
            {
                if(checkProcessNeeded(i))
                    fillValue(i,SqlUtil.getCursorValue(mModelCursor,mViewInfoList.get(i).key,mViewInfoList.get(i).typeClass));
            }
        }else{
            throw new CannotRefreshDataError();
        }
    }
    public Cursor getCursor()
    {
        return mModelCursor;
    }
    @Deprecated
    public void requery()
    {
        mModelCursor.requery();
    }


    public void updateItem(SqliteHelper helper,String col, String value)
    {
        if(helper.updateItemInTable(mTable,SqlUtil.getCursorId(mModelCursor),col,value))
            refresh();
    }

    public void closeCursorIfNeeded()
    {
        if(mModelCursor!=null &&!mModelCursor.isClosed())
            mModelCursor.close();
    }


}

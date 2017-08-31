package fulton.shaw.android.x.viewgetter.database_auto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.interfaces.ViewInfo;

/**
 * Created by 13774 on 8/14/2017.
 */

/**
 *  one model with multiple control infos as adapterview
 */
public class ModelViewAutoCompletor {

    public static class ControlFillerInfo {
        public Context context;
        public Cursor cursor;
        public String table;
        public AdapterView<CursorAdapter> adapterView;
        public ViewGenerator itemViewGenerator;
        public ArrayList<ViewInfo> infoList;
        public ArrayList<ViewGetter> viewGetters;

        public ControlFillerInfo(Context context, Cursor cursor, String table, AdapterView<CursorAdapter> adapterView,
                                 ViewGenerator itemViewGenerator,
                                 ArrayList<ViewInfo> infoList,
                                 ArrayList<ViewGetter> viewGetters) {
            this.context = context;
            this.cursor = cursor;
            this.table=table;
            this.adapterView = adapterView;
            this.itemViewGenerator = itemViewGenerator;
            this.infoList = infoList;
            this.viewGetters = viewGetters;
        }
    }

    public SingleViewAutoCompletor mModelFiller;
    public ArrayList<AdapterViewAutoCompletor> mAdapterViewFillers;

    public ModelViewAutoCompletor(
            Cursor modelCursor, String modelTable, View modelView, ArrayList<ViewInfo> modelInfoList, ArrayList<ViewGetter> modelViewGetters,
            ArrayList<ControlFillerInfo> listInfo)
    {
        mModelFiller=new SingleViewAutoCompletor(modelCursor, modelTable, modelView,modelInfoList,modelViewGetters);
        mAdapterViewFillers=new ArrayList<>(listInfo.size());
        for(int i=0;i<listInfo.size();i++) {
            ControlFillerInfo info=listInfo.get(i);
            mAdapterViewFillers.add(new AdapterViewAutoCompletor(
                    info.context,info.cursor, info.table, info.adapterView,info.itemViewGenerator,info.infoList,info.viewGetters));
        }
    }
    public void refreshModel()
    {
        mModelFiller.refresh();
    }
    public void refreshControl(int i)
    {
        mAdapterViewFillers.get(i).refresh();
    }

    public SingleViewAutoCompletor getModelFiller() {
        return mModelFiller;
    }

    public ArrayList<AdapterViewAutoCompletor> getAdapterViewFillers() {
        return mAdapterViewFillers;
    }

    public AdapterView<CursorAdapter> getAdapterView(int i)
    {
        return mAdapterViewFillers.get(i).getAdapterView();
    }

    public void updateModel(SqliteHelper helper, String col, String value) {
        mModelFiller.updateItem(helper, col, value);
    }
    public void deleteControlItem(int i,int pos, SqliteHelper helper)
    {
        mAdapterViewFillers.get(i).deleteItem(pos,helper);
    }
    public void updateControlItem(int i,int pos,SqliteHelper helper,ContentValues values)
    {
        mAdapterViewFillers.get(i).updateItem(pos,helper,values);
    }

    public void updateControlItem(int i,int pos,SqliteHelper helper,String col,String value)
    {
        mAdapterViewFillers.get(i).updateItem(pos,helper,col,value);
    }

    public void addControlItem(int i,SqliteHelper helper,ContentValues values)
    {
        mAdapterViewFillers.get(i).addItem(helper,values);
    }

    public <E> E collectModelValue(Class<E> eClass) {
        return mModelFiller.collectValue(eClass);
    }
}

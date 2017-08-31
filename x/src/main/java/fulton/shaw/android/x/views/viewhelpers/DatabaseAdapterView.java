package fulton.shaw.android.x.views.viewhelpers;

import android.app.Activity;
import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.viewgetter.database_auto.AdapterViewAutoCompletor;
import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.shaw.android.x.viewgetter.database_auto.checkers.ShareableSingleViewGetChecker;
import fulton.shaw.android.x.viewgetter.transferview.ShareableViewValueTransfer;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.shaw.android.x.views.interfaces.proxiers.ShownSwitchableViewPrxoy;
import fulton.shaw.android.x.views.viewhelpers.functions.AdapterViewFunction;
import fulton.util.android.notations.ConvienenceForGrammarUsingArray;
import fulton.util.android.notations.OverrideToCustomBehaviour;

/**
 * Created by 13774 on 8/17/2017.
 */

public class DatabaseAdapterView<V extends AdapterView> extends AdapterViewWrapper<V> {

    public static abstract class OnErrorConditionHandler<V extends AdapterView>{
        public abstract void onCollectAdderViewValueFailed(DatabaseAdapterView<V> adpertView);
        public abstract void onFillAdderViewValueFailed(DatabaseAdapterView<V> adpertView);
        public abstract void onCommitDatabaseFailed(DatabaseAdapterView<V> adapterView);
    }


    private class ArrayFunctionProvider extends FunctionProvider{
        public ArrayList<AdapterViewFunction<V>> mFuncList;
        public ArrayFunctionProvider()
        {
            mFuncList=new ArrayList<>();
        }
        @Override
        public void applyFunction(int i, int position) {
            mFuncList.get(i).apply(DatabaseAdapterView.this,position);
        }

        @Override
        public int getCount() {
            return mFuncList.size();
        }

        public void add(AdapterViewFunction<V> adapterViewFunction) {
            mFuncList.add(adapterViewFunction);
        }
    }

    private  AdapterViewAutoCompletor<V> mCompletor;

    public void refresh() {
        mCompletor.refresh();
    }

    @Deprecated
    public void requery() {
        mCompletor.requery();
    }

    private ShownSwitchableViewPrxoy<View> mAdderView;
    private SingleViewAutoCollector mAdderViewCollector;
    private OnErrorConditionHandler<V> mErrorHandler;
    private SqliteHelper mSqliteHelper;
    private ArrayFunctionProvider mFunctionProvider;



    //===need an adder view. need to know how to fill it,how to collect value from it.
    //====the adder view,can be shown as pop up
    public DatabaseAdapterView(Activity activity,SqliteHelper helper, V adapterView,
                               AdapterViewAutoCompletor<V> completor,
                               View adderView, ArrayList<ViewGetter> adderViewGetters) {
        super(activity, adapterView);
        mCompletor=completor;
        mSqliteHelper=helper;

        mAdderView=new ShownSwitchableViewPrxoy<View>(adderView, false, null);
        mAdderViewCollector=new SingleViewAutoCollector(adderView,completor.getViewInfoList(),adderViewGetters);
        this.setFunctionProvider(mFunctionProvider=new ArrayFunctionProvider());

    }
    public void setButtonFunction(View v,final int...funcId)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //==no need to set additional information
                doFuncs(funcId);
            }
        });
    }

    public void fillAdderPresetValue() {
        mAdderViewCollector.fillPresetValue();
    }

    public ArrayList<Object> getPresetValues() {
        return mAdderViewCollector.getPresetValues();
    }

    public void setAdderPresetValues(ArrayList<Object> presetValues) {
        mAdderViewCollector.setPresetValues(presetValues);
    }

    /**
     *  format: dbKey,value
     * @param values do not
     */
    public void setAdderPresetValuesBasedOnKey(Object...values)
    {
        mAdderViewCollector.setPresetValuesBasedOnKey(values);
    }


    public void addGetChecker(int i, ShareableSingleViewGetChecker checker) {
        mAdderViewCollector.addGetChecker(i, checker);
    }

    @ConvienenceForGrammarUsingArray
    public void addGetCheckersBasedOnKey(Object... args) {
        mAdderViewCollector.addGetCheckersBasedOnKey(args);
    }

    public void setCheckGetFailedAction(int index,@ShareableSingleViewAutoCollector.FailActions int action) {
        mAdderViewCollector.setCheckGetFailedAction(index, action);
    }


    /**
     * @see {@link fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector#setCheckGetFailedActionBasedOnKey}
     * @param args
     */
    public void setCheckGetFailedActionBaseOnKey(Object... args) {
        mAdderViewCollector.setCheckGetFailedActionBasedOnKey(args);
    }
    @OverrideToCustomBehaviour
    public void onCollectValueFromAdderViewFailed(){
        if(mErrorHandler!=null)
            mErrorHandler.onCollectAdderViewValueFailed(this);
    }

    @OverrideToCustomBehaviour
    public void onFillValueIntoAdderViewFailed(){
        if(mErrorHandler!=null)
            mErrorHandler.onFillAdderViewValueFailed(this);
    }

    public void onCommitDatabaseFailed() {
        if(mErrorHandler!=null)
            mErrorHandler.onCommitDatabaseFailed(this);
    }

    public void setErrorHandler(OnErrorConditionHandler<V> errorHandler)
    {
        this.mErrorHandler=errorHandler;
    }
    public void setAdderTransfer(String dbKey, @NonNull ValueTypeTransfer transfer) {
        mAdderViewCollector.setTransferBasedOnKey(dbKey, transfer);
    }

    public void setAdderTransfers(ArrayList<ShareableViewValueTransfer> transfers) {
        mAdderViewCollector.setTransfers(transfers);
    }

    public void setAdderTransfersBasedOnKey(Object...args)
    {
        mAdderViewCollector.setTransfersBasedOnKey(args);
    }
    public void setItemTransfersBasedOnKey(Object...args)
    {
        mCompletor.setTransfersBasedOnKey(args);
    }

    public void setAdderShown(boolean shown) {
        mAdderView.setShown(shown);
    }

    public SqliteHelper getSqilteHelper()
    {
        return mSqliteHelper;
    }
    public void addFunction(AdapterViewFunction<V> function)
    {
        mFunctionProvider.add(function);
    }
    public void addFunctions(AdapterViewFunction<V>...funcList)
    {
        for(AdapterViewFunction<V> function:funcList)
            mFunctionProvider.add(function);
    }
    public void setFunctions(ArrayList<AdapterViewFunction<V>> funcList)
    {
        mFunctionProvider.mFuncList.clear();
        mFunctionProvider.mFuncList.addAll(funcList);
    }


    public void fillAdderValue(ArrayList values) {
        mAdderViewCollector.fillValue(values);
    }

    public <E> E collectAdderValue(Class<E> eClass) {
        return mAdderViewCollector.collectValue(eClass);
    }

    @Deprecated
    public <E> E collectItemValue(int pos, Class<E> eClass) {
        return mCompletor.collectValue(pos, eClass);
    }

    public <E> E collectItemValueFromCursor(int pos, Class<E> eClz, ArrayList<ViewGetter> getters) {
        return mCompletor.collectValueFromCursor(pos, eClz, getters);
    }
    public <E> E collectItemValueFromCursor(int pos, Class<E> eClz) {
        return mCompletor.collectValueFromCursor(pos, eClz);
    }

    public <E> E collectItemValue(View itemView, Class<E> eClass) {
        return mCompletor.collectValue(itemView, eClass);
    }

    public boolean deleteItem(int pos) {
        return mCompletor.deleteItem(pos,mSqliteHelper);
    }


    public boolean updateItem(int pos, ContentValues values) {
        return mCompletor.updateItem(pos, mSqliteHelper,values);
    }

    public boolean updateItem(int pos, String col, String value) {
        return mCompletor.updateItem(pos, mSqliteHelper, col, value);
    }

    public boolean addItem(ContentValues values) {
        return mCompletor.addItem(mSqliteHelper, values);
    }

    public SingleViewAutoCollector getAdderViewCollector() {
        return mAdderViewCollector;
    }

    public ArrayList<ViewGetter> getAdderViewGetters() {
        return mAdderViewCollector.getViewGetters();
    }
    public void closeCursorIfNeeded() {
        mCompletor.closeCursorIfNeeded();
    }
}

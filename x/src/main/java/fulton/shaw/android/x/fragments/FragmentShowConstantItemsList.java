package fulton.shaw.android.x.fragments;

/**
 * Created by 13774 on 8/22/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionBase;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByShownType;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.Util;
import fulton.util.android.varman.StateVariableManager;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.utils.SqlUtil;

/**
 *  this shows all
 */
public class FragmentShowConstantItemsList extends FragmentApplyFilterCondition{

    private FilterConditionByShownType mCondition;
    private String mGroupDateCol="groupDate";
    private ArrayList<FilterConditionBase> mConditions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCondition=new FilterConditionByShownType(SqlUtil.COL_SHOWN_TYPE,SqlUtil.SHOWN_TYPE_CONSTANT);
        mConditions=new ArrayList<>();
        mConditions.add(mCondition);
        mCondition.updateCachedResult();

        View rootView=super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    protected Pair<String, String[]> getSql() {
        return FilterConditionBase.constructSql(SqliteHelper.TABLE_GENERAL_RECORDS,null,
                new String[]{"*","ifnull("+SqlUtil.COL_SHOWN_DATE+",date("+ SqlUtil.COL_CREATED_TIME+")) as "+mGroupDateCol},
                mGroupDateCol,
                mConditions
                );
    }

    @Override
    protected int getContentRes() {
        return R.layout.show_data_list_constant_all_date;
    }

    @Override
    protected ExpandableListView getShownList(View rootView) {
        return (ExpandableListView) rootView.findViewById(R.id.showList);
    }

    @Override
    protected String getGroupDateCol() {
        return mGroupDateCol;
    }

    @Override
    protected String getPrefFileName() {
        return "constantItems";
    }

}

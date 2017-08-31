package fulton.shaw.android.x.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionBase;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByShownDate;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfViews;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfStateVariableManager;
import fulton.shaw.android.x.views.DatePickerDialogWithTextView;
import fulton.shaw.android.x.views.SwitchReflectedTextView;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/22/2017.
 */

/**
 *  this shows all the items.
 *  only date filters is applied.
 */
public class FragmentShowDataListGroupDate extends FragmentApplyFilterCondition{
    private FilterConditionByShownDate mCondition;
    private String mGroupCol="groupDate";
    private ArrayList<FilterConditionBase> mConditions;

    private SwitchReflectedTextView mSwitcher;
    private Spinner mModelSpinner;
    private DatePickerDialogWithTextView mStart,mEnd;
    private EditText mNumdays;

    private String[] mSavingKeys=new String[]{
           "switcher","modelSpinner","start", "end","numdays"
    };
    private Object[] mDefaultValues=new Object[]{
            0,0, ValueGetter.ValueGetterOfCalendarNow.getIntance(), ValueGetter.ValueGetterOfCalendarNow.getIntance(),"1"
    };
    private ValueProviderOfViews mViewValueProvider;
    private ValueProviderOfStateVariableManager mStateVarProvider;
    private Pair<String, ValueTypeTransfer>[] mSavingTransfers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mStateVarProvider=new ValueProviderOfStateVariableManager(getLoadedVarManagaer());
        for(int i = 0; i< mSavingKeys.length; i++)//ensure default values.
        {
            mStateVarProvider.getOrPutDefaultValue(mSavingKeys[i], mDefaultValues[i], Object.class);
        }

        mConditions=new ArrayList<FilterConditionBase>();
        mCondition=new FilterConditionByShownDate(mGroupCol,
                mStateVarProvider.getValue("modelSpinner",Integer.class),
                mStateVarProvider.getValue("start",Calendar.class),
                mStateVarProvider.getValue("end",Calendar.class),
                Integer.valueOf(mStateVarProvider.getValue("numdays",String.class)));
        mConditions.add(mCondition);


        mCondition.updateCachedResult();
        View rootView=super.onCreateView(inflater, container, savedInstanceState);
        //do some work.

        View conditionView=rootView.findViewById(R.id.dateConditionView);
        mSwitcher= (SwitchReflectedTextView) rootView.findViewById(R.id.conditionSwitcher);
        mModelSpinner= (Spinner) conditionView.findViewById(R.id.dateModelSpinner);
        mStart= (DatePickerDialogWithTextView) conditionView.findViewById(R.id.startTimeTextView);
        mEnd= (DatePickerDialogWithTextView) conditionView.findViewById(R.id.endTimeTextView);
        mNumdays= (EditText) conditionView.findViewById(R.id.numdaysEditText);
        //is also a value provider & setter.

        mSwitcher.setTitleSelection(mStateVarProvider.getValue("switcher",Integer.class));
        mModelSpinner.setSelection(mStateVarProvider.getValue("modelSpinner",Integer.class));
        mStart.setTime(mStateVarProvider.getValue("start",Calendar.class));
        mEnd.setTime(mStateVarProvider.getValue("end",Calendar.class));
        mNumdays.setText(mStateVarProvider.getValue("numdays",String.class));

        //==need some check.
        rootView.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
            @Override

            @SuppressWarnings({ "WrongConstant"})
            public void onClick(View v) {
                mCondition.setDateModel(mModelSpinner.getSelectedItemPosition());
                mCondition.setStartDate(mStart.getTime());
                mCondition.setEndDate(mEnd.getTime());
                switch (mModelSpinner.getSelectedItemPosition())
                {
                    case SqlUtil.DATE_MODEL_JUST_DAYS_FROM_START:
                        case SqlUtil.DATE_MODEL_JUST_DAYS_TO_TODAY:
                            if(mNumdays.getText().length()==0) {
                                Toast.makeText(getContext(), "输入有误", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                mCondition.setNumDays(Integer.valueOf(mNumdays.getText().toString()));
                            }
                }
                mCondition.updateCachedResult();
                refreshDataSet();
            }
        });


        return rootView;
    }

    @Override
    protected Pair<String, String[]> getSql() {
        return FilterConditionBase.constructSql(SqliteHelper.TABLE_GENERAL_RECORDS,null,new String[]{
                "*",
                "ifnull("+ SqlUtil.COL_SHOWN_DATE+",date("+SqlUtil.COL_CREATED_TIME+")) as "+mGroupCol
        }, mGroupCol, mConditions);
    }

    @Override
    protected int getContentRes() {
        return R.layout.show_data_list_group_by_date_page;
    }

    @Override
    protected ExpandableListView getShownList(View rootView) {
        return (ExpandableListView) rootView.findViewById(R.id.showList);
    }

    @Override
    protected String getGroupDateCol() {
        return mGroupCol;
    }

    @Override
    protected String getPrefFileName() {
        return "groupDate";
    }

    @Override
    public void onDetach() {
        //建模:  view-->多个value
        //         多个view-->一个value
        //  <views> -- <values>
        //  at start calls load, views will be set values from loader
        // at end calls save, values will be retrieved from view.
        //  valueprovider
        // getByGetter value of view.
        //  view can also returns Pair,Single,Triple.
        //  we can also save Pair of any thing.

        //=========== the ONE-ONE princple.
        //   we just process one commong condition: the primary value that one holds.
        // and for value,we also just process the primary view it will set.
        getLoadedVarManagaer().set("switcher",mSwitcher.getTitleSelection());
        getLoadedVarManagaer().set("modelSpinner",mModelSpinner.getSelectedItemPosition());
        getLoadedVarManagaer().set("start",mStart.getTime());
        getLoadedVarManagaer().set("end",mEnd.getTime());
        getLoadedVarManagaer().set("numdays",mNumdays.getText().toString());

        super.onDetach();
    }
}

package fulton.shaw.android.x.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionBase;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByShownDate;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByShownType;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByTag;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.multiview_valuegetter.ViewValueGetterOfFixed;
import fulton.util.android.interfaces.multiview_valuegetter.ViewValueGetterOfSimpleAdapterView;
import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.util.android.interfaces.valueproviders.ValueProvider;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfViews;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfStateVariableManager;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FragmentShowDataSearchingPage extends FragmentApplyFilterCondition{
    private FilterConditionByShownType mConditionShownType;
    private FilterConditionByShownDate mConditionShownDate;
    private FilterConditionByTag mConditionTag;

    private ArrayList<FilterConditionBase> mConditions;
    private String mGroupCol="groupDate";

    private GridView mSelectedTags;

    private ArrayList<String> mInputTags;
    private ArrayList<ArrayList<ContentValues>> mTagSets;
    private BaseAdapter mTagSetAdapter;

    private ValueProviderOfViews mViewValueProvider;

    private ValueProviderOfStateVariableManager mStateVarProvider;
    private String[] mSavingKeys;
    private Pair<String, ValueTypeTransfer>[] mSavingTransfers;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSavingKeys =new String[]{
                "switcher",  "modelSpinner", "start","end","numdays",
                "shownTypeSpinner","switcherTagsSet","usedForSpinner","tagsInput","inputTags","filterConditionSpinner"
        };
        mStateVarProvider =new ValueProviderOfStateVariableManager(getLoadedVarManagaer());
        Object[] defaultValues=new Object[]{
                0,0,ValueGetter.ValueGetterOfCalendarNow.getIntance(),ValueGetter.ValueGetterOfCalendarNow.getIntance(),"1",
                0,0,0,"1",new ArrayList<String>(),0
        };
        for(int i = 0; i< mSavingKeys.length; i++)//ensure default values.
        {
            mStateVarProvider.getOrPutDefaultValue(mSavingKeys[i], defaultValues[i], Object.class);
        }

        //checks
        mConditionShownDate=new FilterConditionByShownDate(mGroupCol,
                mStateVarProvider.getValue("modelSpinner",Integer.class),
                mStateVarProvider.getValue("start", Calendar.class),
                mStateVarProvider.getValue("start", Calendar.class),
                Integer.valueOf((String)mStateVarProvider.getValue("numdays", String.class))
        );
        mConditionTag=new FilterConditionByTag(
                mStateVarProvider.getValue("filterConditionSpinner",Integer.class),
                SqliteHelper.TABLE_GENERAL_RECORDS,
                Util.listToArray(mInputTags=mStateVarProvider.getValue("inputTags",ArrayList.class),String.class)
        );
        mConditionShownType=new FilterConditionByShownType(SqlUtil.COL_SHOWN_TYPE,
                mStateVarProvider.getValue("shownTypeSpinner",Integer.class)-1
        );


        mConditions=new ArrayList<FilterConditionBase>(){{
            add(mConditionTag);
            add(mConditionShownDate);
            add(mConditionShownType);
        }};
        for(int i=0;i<mConditions.size();i++)
            mConditions.get(i).updateCachedResult();

        View rootView=super.onCreateView(inflater, container, savedInstanceState);

        View conditionView=rootView.findViewById(R.id.dateConditionView);
        View[] views=new View[]{
                rootView.findViewById(R.id.conditionSwitcher),//0
                conditionView.findViewById(R.id.dateModelSpinner),
                conditionView.findViewById(R.id.startTimeTextView),
                conditionView.findViewById(R.id.endTimeTextView),
                conditionView.findViewById(R.id.numdaysEditText),//4
                rootView.findViewById(R.id.showTypeSpinner),
                rootView.findViewById(R.id.tagSetListSwitcher),
                rootView.findViewById(R.id.usedForSpinner),//7
                rootView.findViewById(R.id.tagsEditText),//8

                rootView.findViewById(R.id.tagSetList),
                rootView.findViewById(R.id.selectedTags),//10
                rootView.findViewById(R.id.filterConditionSpinner),
        };
        final ViewValueGetterOfSimpleAdapterView inputAdapter=new ViewValueGetterOfSimpleAdapterView(getActivity(),
                mSelectedTags=(GridView) views[10],R.layout.show_list_detail_tag_item,R.id.content,mInputTags);

        ViewValueGetterOfFixed[] getters=new ViewValueGetterOfFixed[]{
                new ViewValueGetterOfFixed(views[0]),
                new ViewValueGetterOfFixed(views[1]),
                new ViewValueGetterOfFixed(views[2]),
                new ViewValueGetterOfFixed(views[3]),
                new ViewValueGetterOfFixed(views[4]),

                new ViewValueGetterOfFixed(views[5]),
                new ViewValueGetterOfFixed(views[6]),
                new ViewValueGetterOfFixed(views[7]),
                new ViewValueGetterOfFixed(views[8]),
                inputAdapter,
                new ViewValueGetterOfFixed(views[11]),

        };

        mViewValueProvider = new ValueProviderOfViews(getters,mSavingKeys,null);

        ValueProvider.copyValuePositive(mStateVarProvider,
                mViewValueProvider,mSavingTransfers= (Pair<String, ValueTypeTransfer>[]) Single.zip(mSavingKeys,new Object[0]));


        mSelectedTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mInputTags.remove(position);
                inputAdapter.notifyDatasetChanged();
            }
        });

        mTagSets=getSqliteHelper().getAllTagSet();
        mTagSetAdapter=new BaseAdapter(){

            @Override
            public int getCount() {
                return mTagSets.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return mTagSets.get(position).get(0).getAsLong(SqlUtil.COL_INNER_ID);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout container=null;
                if(convertView==null)
                {
                    container=new LinearLayout(getContext());
                    container.setOrientation(LinearLayout.HORIZONTAL);
                    container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }else{
                    container=(LinearLayout)convertView;
                }
                container.removeViews(0,container.getChildCount());//remove all
                for(int i=0;i<mTagSets.get(position).size();i++)
                {
//                        Util.logi("pos,size:"+position+","+mTagSets.getByGetter(position).size());
                    View v=inflater.inflate(R.layout.show_list_detail_tag_item,parent,false);
                    TextView content= (TextView) v.findViewById(R.id.content);
                    content.setText(mTagSets.get(position).get(i).getAsString(SqlUtil.COL_CONTENT));
                    container.addView(v);
                    LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) v.getLayoutParams();
                    lp.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.tagGap));
                }
                return container;
            }
        };
        final ListView tagSet= (ListView) views[9];
        final Spinner usedForSpinner= (Spinner) views[7];
        final EditText tagsInput= (EditText) views[8];
        final Spinner filterConditionSpinner= (Spinner) views[11];
        tagSet.setAdapter(mTagSetAdapter);
        tagSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mInputTags.clear();
                for(ContentValues values:mTagSets.get(position))
                {
                    mInputTags.add(values.getAsString(SqlUtil.COL_CONTENT));
                }
                inputAdapter.notifyDatasetChanged();
            }
        });
        registerForContextMenu(tagSet);
        final Pair<Boolean,Integer> updateInfo=new Pair<>(false,0);
        tagSet.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE,0,Menu.NONE,"修改");
                menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ListView.AdapterContextMenuInfo info= (ListView.AdapterContextMenuInfo) item.getMenuInfo();
                        usedForSpinner.setSelection(1);
                        tagsInput.setText(Util.join(" ",mTagSets.get(info.position),new Util.ArrayListStringGetter() {
                            @Override
                            public String getString(ArrayList list, int i) {
                                return Util.cast(list.get(i),ContentValues.class).getAsString(SqlUtil.COL_CONTENT);
                            }
                        }));
                        updateInfo.first=true;
                        updateInfo.second=info.position;
                        return true;
                    }
                });
            }
        });
        rootView.findViewById(R.id.tagsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tags=tagsInput.getText().toString().split(" +");
                if(usedForSpinner.getSelectedItemPosition()==0)
                {
                    for(String tag:tags)
                        mInputTags.add(tag);
                    inputAdapter.notifyDatasetChanged();
                }else{
                    if(updateInfo.first)
                    {
                        ContentValues val=mTagSets.get(updateInfo.second).get(0);
                        getSqliteHelper().updateTagSet(val.getAsLong(SqlUtil.COL_INNER_ID),Util.arrayToList(tags),filterConditionSpinner.getSelectedItemPosition());
                        updateInfo.first=false;
                    }else{
                        getSqliteHelper().addTagSet(Util.arrayToList(tags),filterConditionSpinner.getSelectedItemPosition());
                    }
                    mTagSets=getSqliteHelper().getAllTagSet();
                    mTagSetAdapter.notifyDataSetChanged();
                }
                tagsInput.setText("");
            }
        });


        rootView.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateConditions();
                refreshDataSet();
            }
        });

        return rootView;
    }

    @SuppressWarnings({ "WrongConstant"})
    private void updateConditions()
    {
        mConditionShownDate.setDateModel(
                mViewValueProvider.getValue("modelSpinner",Integer.class));
        mConditionShownDate.setStartDate(mViewValueProvider.getValue("start", Calendar.class));
        mConditionShownDate.setEndDate(mViewValueProvider.getValue("start", Calendar.class));
        mConditionShownDate.setNumDays(Integer.valueOf((String)mViewValueProvider.getValue("numdays", String.class)));

        mConditionTag.setFilterCondition(
                mViewValueProvider.getValue("filterConditionSpinner",Integer.class));
        mConditionTag.setTags(Util.listToArray(mInputTags,String.class));

        mConditionShownType.setShownType(mViewValueProvider.getValue("shownTypeSpinner",Integer.class)-1);
        for(FilterConditionBase c:mConditions)
            c.updateCachedResult();
    }

    @Override
    protected Pair<String, String[]> getSql() {
        return  FilterConditionBase.constructSql(SqliteHelper.TABLE_GENERAL_RECORDS,
                null,
                new String[]{
                        "*", "ifnull("+ SqlUtil.COL_SHOWN_DATE+",date("+SqlUtil.COL_CREATED_TIME+")) as "+mGroupCol
                },mGroupCol,mConditions
                );
    }

    @Override
    protected int getContentRes() {
        return R.layout.show_data_list_search_page;
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
        return "searching";
    }

    @Override
    public void onDetach() {
        ValueProvider.copyValueNagetive(mStateVarProvider,mViewValueProvider,mSavingTransfers);
        super.onDetach();
    }
}

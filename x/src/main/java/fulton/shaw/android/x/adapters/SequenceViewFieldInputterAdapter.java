package fulton.shaw.android.x.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import fulton.shaw.android.x.R;
import fulton.util.android.utils.StringFormatters;
import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.language.Cast;
import fulton.util.android.notations.CoreMethodOfThisClass;
import fulton.util.android.notations.HelperMethods;
import fulton.util.android.notations.HelperStaticMethods;
import fulton.util.android.notations.HowToUse;
import fulton.util.android.notations.OverrideToCustomBehaviour;
import fulton.util.android.utils.Util;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 7/29/2017.
 */

/**
 * 提供默认值实现，默认情况下每个域的输入是一个LinearLayout, 其中的第一个是标题，第二个是输入框。
 *  数据库这种并不适合使用ListView这样的AdapterView来实现，因为它物理上要求保存所有的view数据，而adapterView只会保存可视的所有数据，并不适合使用重用这样的技术。
 */
public class SequenceViewFieldInputterAdapter extends SequenceViewFieldAdaperBase{
    protected static final String VAR_DATE_TIME_PICKER_DIALOG_CALENDER="dateTimePickerDialogCalendar";

    public SequenceViewFieldInputterAdapter(Activity activity) {
        super(activity);
    }

    public SequenceViewFieldInputterAdapter(Context context, LayoutInflater inflater) {
        super(context, inflater);
    }


    @OverrideToCustomBehaviour
    protected void processCustomSetViewInfo(int position, AdapterFieldsInfo info)
    {

    }

    @OverrideToCustomBehaviour
    protected void processDefaultSetViewSpinner(AdapterFieldsInfo info,int position)
    {

    }
    @OverrideToCustomBehaviour
    protected void processDefaultSetViewRadioGroup(AdapterFieldsInfo info,int position)
    {

    }

    @OverrideToCustomBehaviour
    protected int processDefaultGetViewLayoutMultiContentSubcontent(AdapterFieldsInfo info)
    {
        return AdapterFieldsInfo.DEFAULT_MULTI_EDITTEXT_SUBCONTENT_LAYOUT;
    }

    /**
     * note:convertview should not be used.Because types maybe different,and data collection reason.
     *
     * getByGetter nextview
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @CoreMethodOfThisClass
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        final AdapterFieldsInfo info = mFieldsInfo.get(mPositionToFieldsIndex.get(position));

        Util.logi("adaper,getDecroView for "+position+",dbKey is "+info.dbKey);
        if(convertView==null) {

            view = mInflater.inflate(info.layoutId, parent, false);//always inflated

            info.generatedLayoutView = view;//this must be a layout,supporting add something
            info.generatedTitleView = view.findViewById(AdapterFieldsInfo.TITLE_ID);
            info.generatedContentView = view.findViewById(AdapterFieldsInfo.CONTENT_ID);

            if (info.showType == AdapterFieldsInfo.SHOW_TYPE_DEFAULT)//has something like default title(textview) and content
            {
                if(info.generatedTitleView!=null)
                    info.getTitleViewAsTextView().setText(info.shownTitle);
                if (info.processType == AdapterFieldsInfo.PROCESS_TYPE_EDITTEXT) {
                    if(info.defaultValue!=null)
                        info.getContentViewAsEditText().setText(String.valueOf(info.defaultValue));
                } else if (info.processType == AdapterFieldsInfo.PROCESS_TYPE_SPINNER)
                    processDefaultSetViewSpinner(info, position);
                else if (info.processType == AdapterFieldsInfo.PROCESS_TYPE_RADIOGROUP)
                    processDefaultSetViewRadioGroup(info, position);
                else if (info.processType == AdapterFieldsInfo.PROCESS_TYPE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_TO_TIME)
                    ((TimePicker) info.generatedContentView.findViewById(R.id.timePicker)).setIs24HourView(true);
                else if (info.processType == AdapterFieldsInfo.PROCESS_TYPE_DATE_TIME_DIALOGS_TIME) {
                    getViewHelperGetTimeDialog(info);
                }else if (info.processType == AdapterFieldsInfo.PROCESS_TYPE_CHECKBOX){
                    info.generatedTitleView=info.generatedContentView;
                    info.getTitleViewAsTextView().setText(info.shownTitle);
                    if(info.defaultValue!=null)
                        info.getContentViewAsCheckBox().setChecked((Boolean) info.defaultValue);
                }

                //content view should set default values.
            } else if (info.showType == AdapterFieldsInfo.SHOW_TYPE_CUSTOM_CONTENT) {
                //set view at that position for yourself.
                info.getTitleViewAsTextView().setText(info.shownTitle);
                processCustomSetViewInfo(position, info);
            } else if (info.showType == AdapterFieldsInfo.SHOW_TYPE_DEFAULT_MULTI_CONTENT) {

                //set title
                info.getTitleViewAsTextView().setText(info.shownTitle);

                //getByGetter the content view
                final ViewGroup contentView = info.getContentViewAsViewGroup();
                final View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == AdapterFieldsInfo.MULTI_COLUMN_ADD_ID) {
                            ViewGroup subContent = (ViewGroup) ViewUtil.findParentWithId(v, AdapterFieldsInfo.MULTI_COLUMN_SUB_CONTENT_ID);
                            View copiedSubContent = mInflater.inflate(processDefaultGetViewLayoutMultiContentSubcontent(info), contentView, false);
                            contentView.addView(copiedSubContent);
                            ImageButton addButton = (ImageButton) copiedSubContent.findViewById(AdapterFieldsInfo.MULTI_COLUMN_ADD_ID);
                            ImageButton removeButton = (ImageButton) copiedSubContent.findViewById(AdapterFieldsInfo.MULTI_COLUMN_REMOVE_ID);
                            addButton.setOnClickListener(this);
                            removeButton.setOnClickListener(this);
                        } else if (v.getId() == AdapterFieldsInfo.MULTI_COLUMN_REMOVE_ID) {
                            ViewGroup subContent = (ViewGroup) ViewUtil.findParentWithId(v, AdapterFieldsInfo.MULTI_COLUMN_SUB_CONTENT_ID);
                            if (contentView.getChildCount() > 1)
                                contentView.removeView(subContent);
                        }
                    }
                };
                for (int i = 0; i < contentView.getChildCount(); i++) {
                    ImageButton addButton = (ImageButton) contentView.getChildAt(i).findViewById(AdapterFieldsInfo.MULTI_COLUMN_ADD_ID);
                    ImageButton removeButton = (ImageButton) contentView.getChildAt(i).findViewById(AdapterFieldsInfo.MULTI_COLUMN_REMOVE_ID);
                    addButton.setOnClickListener(onClickListener);
                    removeButton.setOnClickListener(onClickListener);
                }
            } else if (info.showType == AdapterFieldsInfo.SHOW_TYPE_DEFAULT_DESCRIPTIVE_EDITTEXT) {
                info.getTitleViewAsTextView().setText(info.shownTitle);
                info.getContentViewAsEditText().setLines(3);
            } else if (info.showType == AdapterFieldsInfo.SHOW_TYPE_DEFAULT_DIARY_EDITTEXT) {
                info.getTitleViewAsTextView().setText(info.shownTitle);
                info.getContentViewAsEditText().setLines(10);
            } else {
                processCustomSetViewInfo(position, info);
            }
        }else{
            view=convertView;
        }
            if(mActioner!=null)
                mActioner.performAction(info);
        return view;
    }


    @HelperMethods
    private void getViewHelperGetTimeDialog(AdapterFieldsInfo info)
    {
        final Calendar timeDateCalendar=Calendar.getInstance();
        if(info.defaultValue!=null)
            timeDateCalendar.setTimeInMillis((Long)info.defaultValue);
        mVariables.put(VAR_DATE_TIME_PICKER_DIALOG_CALENDER,timeDateCalendar);
        Util.logi("date equal?"+(timeDateCalendar==mVariables.get(VAR_DATE_TIME_PICKER_DIALOG_CALENDER)));
        final TextView dateView= (TextView) info.generatedContentView.findViewById(R.id.dateTextView);
        final TextView timeView= (TextView) info.generatedContentView.findViewById(R.id.timeTextView);
        dateView.setText(String.format(StringFormatters.DATE,timeDateCalendar.get(Calendar.YEAR),timeDateCalendar.get(Calendar.MONTH)+1,timeDateCalendar.get(Calendar.DAY_OF_MONTH)));
        timeView.setText(String.format(StringFormatters.TIME,timeDateCalendar.get(Calendar.HOUR_OF_DAY),timeDateCalendar.get(Calendar.MINUTE)));
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==dateView.getId())
                {

                    Util.logi("cal date="+timeDateCalendar.getTimeInMillis());
                    Util.logi("var date="+((Calendar)mVariables.get(VAR_DATE_TIME_PICKER_DIALOG_CALENDER)).getTimeInMillis());
                    Util.logi("cal==var?"+(timeDateCalendar==mVariables.get(VAR_DATE_TIME_PICKER_DIALOG_CALENDER)));
                    //first,parse date from the shown view or...
                    final DatePickerDialog dialog=new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            timeDateCalendar.set(year,month,dayOfMonth);
                            dateView.setText(String.format(StringFormatters.DATE,year,month+1,dayOfMonth));
                        }
                    },timeDateCalendar.get(Calendar.YEAR),timeDateCalendar.get(Calendar.MONTH),timeDateCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }else if(v.getId()==timeView.getId()){
                    final TimePickerDialog dialog1=new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            timeDateCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            timeDateCalendar.set(Calendar.MINUTE,minute);
                            timeView.setText(String.format(StringFormatters.TIME,hourOfDay,minute));
                        }
                    },timeDateCalendar.get(Calendar.HOUR_OF_DAY),timeDateCalendar.get(Calendar.MINUTE),true);
                    dialog1.show();
                }
            }
        };
        dateView.setOnClickListener(listener);
        timeView.setOnClickListener(listener);
    }

    /**
     * by process content values, string & int are the same.
     * @return
     *
     */
    @CoreMethodOfThisClass
    public HashMap<String,ArrayList<ContentValues>> collectAsContentValues()
    {
        HashMap<String,ArrayList<ContentValues>> res=new HashMap<>();
        for(AdapterFieldsInfo info:mFieldsInfo)
        {
            ArrayList<ContentValues> thisArrayList=res.get(info.dbTable);
            if(thisArrayList==null)
            {
                thisArrayList=new ArrayList<>();
                res.put(info.dbTable,thisArrayList);
            }
            if(thisArrayList.size()==0)
            {
                thisArrayList.add(new ContentValues());
            }
            ContentValues value=thisArrayList.get(0);
            //process the hiddens using default value.
            switch (info.processType)
            {
                case AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE:
                    switch (info.valueType)
                    {
                        case AdapterFieldsInfo.VALUE_TYPE_INT:
                            value.put(info.dbKey,(Integer)info.defaultValue);
                            break;
                        case AdapterFieldsInfo.VALUE_TYPE_STRING:
                            value.put(info.dbKey,(String)info.defaultValue);
                            break;
                        case AdapterFieldsInfo.VALUE_TYPE_DOUBLE:
                            value.put(info.dbKey,(Double) info.defaultValue);
                            break;
                    }
                    continue;
                case AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT:
                    long current=System.currentTimeMillis();
                    switch (info.valueType)
                    {
                        case AdapterFieldsInfo.VALUE_TYPE_DOUBLE:
                            value.put(info.dbKey,(double)current);break;
                        case AdapterFieldsInfo.VALUE_TYPE_STRING:
                            value.put(info.dbKey,String.valueOf(current));break;
                    }
                    continue;
                case AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_DO_NOT_PROCESS:
                    continue;
            }

            //process the shown using user input value.
            View contentView=info.generatedContentView;

            switch (info.processType)
            {
                case AdapterFieldsInfo.PROCESS_TYPE_EDITTEXT:
                    if(info.valueType==AdapterFieldsInfo.VALUE_TYPE_STRING)
                        value.put(info.dbKey,((TextView)contentView).getText().toString());
                    else if(info.valueType==AdapterFieldsInfo.VALUE_TYPE_INT)
                        try {
//                            info.getContentViewAsEditText().setText("10");
//                            Util.logi("info: dbKey="+info.dbKey+",content=\""+info.getContentViewAsEditText().getText().toString()+"\"");
                            value.put(info.dbKey, Integer.valueOf(((TextView) contentView).getText().toString()));
                        }catch (Exception e){
                            //ignore
                            Util.logi("exception in sequence view adaper");
                            e.printStackTrace();
                        }
                    break;
                case AdapterFieldsInfo.PROCESS_TYPE_SPINNER:
                    if(info.valueType==AdapterFieldsInfo.VALUE_TYPE_STRING)
                        value.put(info.dbKey, (String)processDefaultGetSpinnerType(info,info.getContentViewAsSpinner().getSelectedItemPosition()));
                    else if(info.valueType==AdapterFieldsInfo.VALUE_TYPE_INT)
                        value.put(info.dbKey, (Integer)processDefaultGetSpinnerType(info,info.getContentViewAsSpinner().getSelectedItemPosition()));
                    break;
                case AdapterFieldsInfo.PROCESS_TYPE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_TO_TIME:
                    LinearLayout content= (LinearLayout) info.generatedContentView;
                    TimePicker tp= (TimePicker) content.findViewById(R.id.timePicker);
                    DatePicker dp= (DatePicker) content.findViewById(R.id.datePicker);
                    Calendar calendar=new GregorianCalendar(dp.getYear(),dp.getMonth(),dp.getDayOfMonth(),tp.getCurrentHour(),tp.getCurrentMinute());
                    value.put(info.dbKey,(double)calendar.getTimeInMillis());
                    break;
                case AdapterFieldsInfo.PROCESS_TYPE_DATE_TIME_DIALOGS_TIME:
                    Calendar calendar1= (Calendar) mVariables.get(VAR_DATE_TIME_PICKER_DIALOG_CALENDER);
                    Util.logi("getByGetter time,calendar time:"+calendar1.getTimeInMillis());
                    value.put(info.dbKey,(double)calendar1.getTimeInMillis());
                    break;
                case AdapterFieldsInfo.PROCESS_TYPE_MULTI_EDITTEXT://needs multiple
                    //getByGetter the count of  subContent
                    //copy count times of the original arraylist
                    //for each content value in the arraylist,they will be added such column key and value
                    ViewGroup contentView1= (ViewGroup) info.generatedContentView;
                    int originalSize=thisArrayList.size();
                    for(int i=0;i<contentView1.getChildCount();i++)
                    {
                        for(int j=0;j<originalSize;j++)//(i+1)*originalSize + j
                        {
                            ContentValues cloned=null;
                            if(i!=0) {
                                cloned = cloneContentValues(thisArrayList.get(j));
                                cloned.put(info.dbKey,((EditText)(contentView1.getChildAt(i).findViewById(AdapterFieldsInfo.MULTI_COLUMN_CONTENT_ID))).getText().toString());
                                thisArrayList.add(cloned);
                            }
                            else {
                                cloned = thisArrayList.get(j);
                                cloned.put(info.dbKey,((EditText)(contentView1.getChildAt(i).findViewById(AdapterFieldsInfo.MULTI_COLUMN_CONTENT_ID))).getText().toString());
                            }
                        }
                    }
                    break;
                case AdapterFieldsInfo.PROCESS_TYPE_CHECKBOX:
                    value.put(info.dbKey,info.getContentViewAsCheckBox().isChecked());
                    break;
                case AdapterFieldsInfo.PROCESS_TYPE_CUSTOM:
                    processCustomGetValue(value,contentView,info);break;
            }
        }
        return res;
    }

    @HelperStaticMethods
    public static ContentValues cloneContentValues(ContentValues value)
    {
        ContentValues res=new ContentValues();
        res.putAll(value);
        return res;
    }

    /**
     * by default,just clear those which need multiple input characters.RadioButton,Spinner are not reset.
     */
    @OverrideToCustomBehaviour
    @CoreMethodOfThisClass
    public void clear()
    {
        for(AdapterFieldsInfo info:mFieldsInfo)
        {
//            Util.logi("info="+info);
            if(info.showType==info.SHOW_TYPE_NOT_SHOWN)continue;
            else if(info.processType==info.PROCESS_TYPE_CUSTOM)
            {
                processCutsomClear(info);
            }else if(info.processType==info.PROCESS_TYPE_EDITTEXT){
                if(info.generatedContentView !=null)//may have some bugs.
                    info.getContentViewAsTextView().setText("");
            }else{
                //do nothing
            }
        }
    }

    @OverrideToCustomBehaviour
    protected void processCutsomClear(AdapterFieldsInfo info)
    {

    }
    /**
     *
     *
     * @param valueContainer ContentValues or HashMap or ArrayList
     */
    @OverrideToCustomBehaviour
    protected void processCustomGetValue(Object valueContainer, View contentView, AdapterFieldsInfo info)
    {

    }
    /**
     * when it is a spinner, this set how to getByGetter its value
     *
     * @param info
     * @param position
     * @return  anything that is required to be.
     */
    @OverrideToCustomBehaviour
    protected Object processDefaultGetSpinnerType(AdapterFieldsInfo info, int position)
    {
        return null;
    }

    /**
     *
     * @param info
     * @param radioId
     * @return string , or int or double or anything else that matches info.valueType.
     */
    @OverrideToCustomBehaviour
    protected Object processDefaultGetRadioType(AdapterFieldsInfo info, int radioId)
    {
        return null;
    }


    public HashMap<String,Object> collectAsHashMap()
    {
        return null;
    }

    public ArrayList<Object[]> collectAsArrayList()
    {
        return null;
    }


    @HowToUse
    public static SequenceViewFieldInputterAdapter HowToUse(final Activity activity)
    {
        SequenceViewFieldInputterAdapter adapter=new SequenceViewFieldInputterAdapter(activity){

            @Override
            protected void processCustomSetViewInfo(int position, AdapterFieldsInfo info) {//custom
                if(info.dbKey.equals("level"))
                {
                    ArrayAdapter<String> adpter1=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,new String[]{"1","2","3"});
                    info.getContentViewAsSpinner().setAdapter(adpter1);
                }else if(info.dbKey.equals("repeated")){
                    info.getContentViewAsRadioGroup().check(R.id.radio1);
                    Cast.getRadioButtonCaster().cast(info.getContentViewAsRadioGroup().findViewById(R.id.radio1)).setText("是");
                    Cast.getRadioButtonCaster().cast(info.getContentViewAsRadioGroup().findViewById(R.id.radio2)).setText("否");
                }
            }

            @Override
            protected void processCutsomClear(AdapterFieldsInfo info) {

            }

            @Override
            protected void processCustomGetValue(Object valueContainer, View contentView, AdapterFieldsInfo info) {
                ContentValues contentValues= (ContentValues) valueContainer;
                if(info.dbKey.equals("level"))
                {
                    contentValues.put(info.dbKey,((Spinner)((LinearLayout)info.generatedLayoutView).findViewById(R.id.content)).getSelectedItem().toString());
                }else if(info.dbKey.equals("repeated")){
                    contentValues.put(info.dbKey,info.getContentViewAsRadioGroup().getCheckedRadioButtonId());
                }
            }

            @Override
            protected Object processDefaultGetSpinnerType(AdapterFieldsInfo info, int position) {
                return null;
            }

            @Override
            protected Object processDefaultGetRadioType(AdapterFieldsInfo info, int radioId) {
                return null;
            }
        };
        adapter.add(new AdapterFieldsInfo("_id",AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN))
                .add(new AdapterFieldsInfo(null, "name","姓名"))
                .add(new AdapterFieldsInfo(null, "grade","成绩",AdapterFieldsInfo.VALUE_TYPE_DOUBLE))
                .add(new AdapterFieldsInfo(null, "level","级别",AdapterFieldsInfo.SHOW_TYPE_CUSTOM_CONTENT,AdapterFieldsInfo.VALUE_TYPE_INT, R.layout.listview_field_inputter_item_spinner_layout))
                .add(new AdapterFieldsInfo(null, "repeated","是否复读",
                        AdapterFieldsInfo.SHOW_TYPE_CUSTOM_CONTENT,AdapterFieldsInfo.VALUE_TYPE_BOOLEAN,
                        R.layout.listview_field_inputter_item_radiogroup_layout ))
                .add((AdapterFieldsInfo)null);//the end.
        return adapter;
    }

}

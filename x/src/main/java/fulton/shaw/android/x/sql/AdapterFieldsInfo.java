package fulton.shaw.android.x.sql;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import fulton.shaw.android.x.R;
import fulton.util.android.notations.FixInTheFuture;
import fulton.util.android.notations.HelperMethods;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.notations.ConvienenceForGrammarUsingArray;

/**
 * Created by 13774 on 7/29/2017.
 */

public class AdapterFieldsInfo {
    /**
     * 使用默认的类型处理方法
     *
     * Default has a meaning that using the process_type for design.
     */
    public static final int SHOW_TYPE_DEFAULT=0;

    public static final int SHOW_TYPE_DEFAULT_DESCRIPTIVE_EDITTEXT=1;//line=3


    /**
     * 使用自定义的类型处理方法
     */
    public static final int SHOW_TYPE_CUSTOM_BOTH =2;
    public static final int SHOW_TYPE_CUSTOM_TITLE=3;
    public static final int SHOW_TYPE_CUSTOM_CONTENT =4;

    /**
     * The structure of desired layout is descripted below:
     *
     * <ViewGroup id=content>
     *      <ViewGroup id=subContent>
     *              ...id=multiColumnContent
     *              ...id=multiColumnAdd
     *              ...id=multiColumnRemove
     *          </ViewGroup>
     *          <ViewGroup id=subContent>
     *              ...
     *              </ViewGroup>
     *              ...
     *   </ViewGroup>
     */
    public static final int SHOW_TYPE_DEFAULT_MULTI_CONTENT=5;

    /**
     * title & add,remove button are automatically created.
     */
    public static final int SHOW_TYPE_MULTI_COLUMN_COMBINED_CUTSOM_CONTENT=6;

    public static final int SHOW_TYPE_DEFAULT_DIARY_EDITTEXT=7;//lines=10
    /**
    * 不显示
     */
    public static final int SHOW_TYPE_NOT_SHOWN=10;



    //=======value types
    public static final int VALUE_TYPE_INT=0;
    public static final int VALUE_TYPE_STRING=1;
    public static final int VALUE_TYPE_DOUBLE=2;
    public static final int VALUE_TYPE_BOOLEAN=3;

    //=======process types
    public static final int PROCESS_TYPE_CUSTOM=0;
    public static final int PROCESS_TYPE_EDITTEXT =1;
    public static final int PROCESS_TYPE_SPINNER =2;
    public static final int PROCESS_TYPE_RADIOGROUP=3;
    public static final int PROCESS_TYPE_CHECKBOX=4;

    /**
     *   content is a ViewGroup
     *   each subView of this ViewGroup contains 'subContent' as edittext,'multiColumnAdd','multiColumnRemove' as button
     *
     *   when clicked, add performs adding a subView as that,remove performs removing that subView
     */
    public static final int PROCESS_TYPE_MULTI_EDITTEXT=5;
    /**
     *   show two textviews,each corresponds to a dialog picker.
     *   Two textViews have two ids:dateTextView timeTextView
     */
    public static final int PROCESS_TYPE_DATE_TIME_DIALOGS_TIME=9;
    public static final int PROCESS_TYPE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_TO_TIME=10;//年 月 日 时 分 秒
    public static final int PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE=11;//not shown, should be added to the
    public static final int PROCESS_TYPE_HIDDEN_DO_NOT_PROCESS=12;
    public static final int PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT=13;


    public static final int DEFAULT_EDIT_TEXT_LAYOUT = R.layout.sequence_view_adapter_edittext;
    public static final int DEFAULT_DATE_TIME_PICKER_LAYOUT=R.layout.sequence_view_adapter_time;
    public static final int DEFAULT_DATE_TIME_PICKER_DIALOG_LAYOUT=R.layout.sequence_view_adapter_textview_time_dialog;
    public static final int DEFAULT_SPINNER_LAYOUT=R.layout.sequence_view_adapter_spinner;
    public static final int DEFAULT_RADIOGROUP_LAYOUT=R.layout.sequence_view_adapter_radiogroup;
    public static final int DEFAULT_CHECKBOX_LAYOUT=R.layout.sequence_view_adapter_checkbox;
    public static final int DEFAULT_MULTI_COLUMN_ADD_REMOVE_LAYOUT=R.layout.sequence_view_adapter_multi_add_remove_include_layout;
    public static final int DEFAULT_MULTI_EDITTEXT_LAYOUT=R.layout.sequence_view_adapter_edittext_multi;
    public static final int DEFAULT_MULTI_EDITTEXT_SUBCONTENT_LAYOUT=R.layout.sequence_view_adapter_edittext_multi_subcontent;

    public static final int TITLE_ID=R.id.title;
    public static final int CONTENT_ID=R.id.content;
    public static final int MULTI_COLUMN_SUB_CONTENT_ID=R.id.subContent;

    public static final int MULTI_COLUMN_CONTENT_ID=R.id.multiColumnContent;
    public static final int MULTI_COLUMN_ADD_ID=R.id.multiColumnAdd;
    public static final int MULTI_COLUMN_REMOVE_ID=R.id.multiColumnRemove;

    public String  dbTable;
    public String  dbKey;
    public int     showType;
    public String  shownTitle;
    public int     valueType;
    public int     processType;//默认 EDITTEXT_STRING
    public int     layoutId;//based on process type,when null they'll getByGetter a proper layout.
    public View     generatedLayoutView;
    public View     generatedTitleView;
    public View     generatedContentView;//all needed is here
    public Object   defaultValue;

    //===helper functions



    public AdapterFieldsInfo(String dbKey,int showType)
    {
        this(null, dbKey, showType,null,          null,null, null, null);
    }

    @ConvienenceForGrammarUsingArray
    public static AdapterFieldsInfo[] initFields(Object[][] args)
    {
        AdapterFieldsInfo[] res=new AdapterFieldsInfo[args.length];
        for(int i=0;i<args.length;i++)
        {
            res[i]=new AdapterFieldsInfo(args[i]);
        }
        return res;
    }

    /**
     * you can place all your data into args, and then we init them.
     *
     * @param args contains dbKey...layoutId
     */
    @ConvienenceForGrammarUsingArray
    public AdapterFieldsInfo(Object[] args)
    {
        this((String)args[0], (String)args[1],(Integer) args[2],(String)args[3],(Integer) args[4],(Integer) args[5],(Integer)args[6], args[7]);
    }

    /**
     *  on null, they'll getByGetter a proper value
     * @param dbTable
     * @param dbKey
     * @param showType
     * @param shownTitle
     * @param processType
     * @param valueType
     * @param layoutId
     * @param defaultValue
     */
    @FixInTheFuture("for time type,double should be long")
    public AdapterFieldsInfo(String dbTable, String dbKey, Integer showType, String shownTitle, Integer processType, Integer valueType, Integer layoutId, Object defaultValue) {
        this.dbTable=dbTable;
        this.dbKey = dbKey;

        if(showType==null)
        {
            this.showType=SHOW_TYPE_DEFAULT;
        }
        else this.showType = showType;


        if(shownTitle==null && this.showType!=SHOW_TYPE_NOT_SHOWN)this.shownTitle=this.dbKey;
        else   this.shownTitle = shownTitle;

        if(processType==null)
        {
            if(this.showType==SHOW_TYPE_CUSTOM_BOTH||this.showType==SHOW_TYPE_CUSTOM_CONTENT)
            {
                this.processType=PROCESS_TYPE_CUSTOM;
            }
            else if(this.showType==SHOW_TYPE_DEFAULT_MULTI_CONTENT)
            {
                this.processType=PROCESS_TYPE_MULTI_EDITTEXT;
            }
            else if(this.showType==SHOW_TYPE_NOT_SHOWN) {
                if (this.dbKey == SqlUtil.COL_ID)
                {
                    this.processType = PROCESS_TYPE_HIDDEN_DO_NOT_PROCESS;
                }
                else
                {
                    this.processType = PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE;
                }
            }
            else
                this.processType=PROCESS_TYPE_EDITTEXT;
        }else this.processType = processType;


        if(valueType==null) {
            if (this.processType == PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT || this.processType == PROCESS_TYPE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_TO_TIME||
                    this.processType==PROCESS_TYPE_DATE_TIME_DIALOGS_TIME
                    ) {
                this.valueType = VALUE_TYPE_DOUBLE;//should be long
            }else if(this.processType==PROCESS_TYPE_CHECKBOX){
                this.valueType=VALUE_TYPE_BOOLEAN;
            }
            else
                this.valueType = VALUE_TYPE_STRING;
        }
        else
            this.valueType=valueType;


        if(layoutId==null) {
            if (this.processType == PROCESS_TYPE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_TO_TIME)
                this.layoutId = DEFAULT_DATE_TIME_PICKER_LAYOUT;
            else if(this.processType==PROCESS_TYPE_DATE_TIME_DIALOGS_TIME)
                this.layoutId=DEFAULT_DATE_TIME_PICKER_DIALOG_LAYOUT;
            else if (this.processType == PROCESS_TYPE_EDITTEXT)
                this.layoutId = DEFAULT_EDIT_TEXT_LAYOUT;
            else if (this.processType == PROCESS_TYPE_SPINNER)
                this.layoutId = DEFAULT_SPINNER_LAYOUT;
            else if (this.processType == PROCESS_TYPE_RADIOGROUP)
                this.layoutId = DEFAULT_RADIOGROUP_LAYOUT;
            else if(this.processType==PROCESS_TYPE_MULTI_EDITTEXT)
                this.layoutId= DEFAULT_MULTI_EDITTEXT_LAYOUT;
            else if(this.processType==PROCESS_TYPE_CHECKBOX)
                this.layoutId=DEFAULT_CHECKBOX_LAYOUT;
        }
        else
            this.layoutId = layoutId;

        this.defaultValue=defaultValue;
    }

    public AdapterFieldsInfo(String dbTable, String dbKey, String shownTitle) {
        this(dbTable, dbKey,null,shownTitle, null, null, null, null);
    }
    public AdapterFieldsInfo(String dbTable, String dbKey, int showType, String shownTitle, int valueType) {
        this(dbTable, dbKey,showType,shownTitle,null, valueType, null, null);
    }

    public AdapterFieldsInfo(String dbTable, String dbKey, String shownTitle, int valueType) {
        this(dbTable, dbKey,null,shownTitle,null, valueType, null, null);
    }
    public AdapterFieldsInfo(String dbTable, String dbKey, String shownTitle, int showType, int valueType, Integer layoutId) {
        this(dbTable, dbKey,showType,shownTitle,null, valueType, layoutId, null);
    }

    public AdapterFieldsInfo(String dbTabel, String dbKey) {
        this(dbTabel, dbKey,dbKey);
    }

    public Spinner getContentViewAsSpinner()
    {
        return  (Spinner) generatedContentView;
    }
    public LinearLayout getContentViewAsLinearLayout()
    {
        return  (LinearLayout) generatedContentView;
    }

   public RadioGroup getContentViewAsRadioGroup()
    {
        return  (RadioGroup) generatedContentView;
    }
    public TextView getContentViewAsTextView()
    {
        return  (TextView) generatedContentView;
    }
    public EditText getContentViewAsEditText()
    {
        return (EditText) generatedContentView;
    }
    public CheckBox getContentViewAsCheckBox()
    {
        return (CheckBox) generatedContentView;
    }
    public ViewGroup getContentViewAsViewGroup()
    {
        return (ViewGroup) generatedContentView;
    }

    public LinearLayout getLayoutAsLinearLayout()
    {
        return (LinearLayout)generatedLayoutView;
    }

    public TextView getTitleViewAsTextView()
    {
        return (TextView)generatedTitleView;
    }

    @Override
    public String toString()
    {
        return String.format("{" +
                "dbTable=%s,dbKey=%s,showType=%d,shownTitle=%s,processType=%d,valueType=%d,layoutId=%d}",
                dbTable,dbKey,showType,shownTitle,processType,valueType,layoutId);
    }

    public Object[] asArray()
    {
        return new Object[]{
                dbTable,
                dbKey,
                showType,
                shownTitle,
                processType,
                valueType,
                layoutId
        };
    }



}

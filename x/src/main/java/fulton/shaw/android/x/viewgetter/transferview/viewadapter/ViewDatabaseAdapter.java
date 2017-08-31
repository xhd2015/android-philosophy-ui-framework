package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import fulton.shaw.android.x.views.ColorIndicator;
import fulton.shaw.android.x.views.DatePickerDialogWithTextView;
import fulton.shaw.android.x.views.DateTimePickerDialogWithTextView;
import fulton.shaw.android.x.views.ImageIndicator;
import fulton.shaw.android.x.views.TimePickerDialogWithTextView;
import fulton.util.android.notations.IsAFactory;
import fulton.util.android.utils.Util;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/9/2017.
 */

/**
 * never inflate anything or popup any dailog.
 * leave those as
 * @param <ViewType>
 * @param <ValueType>
 */
public abstract class ViewDatabaseAdapter<ViewType extends View,ValueType> {
    protected ViewType mView;


    public ViewDatabaseAdapter(ViewType view) {
        mView = view;
    }



    public void setView(ViewType view)
    {
        mView=view;
    }
    public ViewType getView()
    {
        return mView;
    }

    public abstract ValueType getValue();
    public abstract void setValue(ValueType value);


    //========static methods

    /**
     *
     * @param <S_ViewType>  must be views as defined in file 'allowedViews'
     * @return ViewAdapter & Its Default Type
     */
    @IsAFactory
    public static <S_ViewType extends View> ViewDatabaseAdapter getDefaultViewAdaper(S_ViewType view)
    {
        Class<S_ViewType> viewClass= (Class<S_ViewType>) view.getClass();
        //让子类在前，先去截获
        if(DatePickerDialogWithTextView.class.isAssignableFrom(viewClass))
        {
           return new TextViewDateCalendarAdaper((DatePickerDialogWithTextView) view);
        }else if(TimePickerDialogWithTextView.class.isAssignableFrom(viewClass)){
            return new TextViewTimeCalendarAdapter((TimePickerDialogWithTextView) view);
        }else if(DateTimePickerDialogWithTextView.class.isAssignableFrom(viewClass)) {
            return new TextViewDateTimeCalendarAdapter((DateTimePickerDialogWithTextView) view);
        }else if(CheckBox.class.isAssignableFrom(viewClass)){
            return new CheckBoxBooleanAdapter((CheckBox) view);
        }else if(EditText.class.isAssignableFrom(viewClass)){
            return new EditTextStringAdapter((EditText) view);
        }else if(TextView.class.isAssignableFrom(viewClass)){
            return new TextViewStringAdapter((TextView) view);
        }else if(RatingBar.class.isAssignableFrom(viewClass)){
            return new RatingBarFloatAdapter((RatingBar) view);
        }else if(Spinner.class.isAssignableFrom(viewClass)){
            return new SpinnerIntegerAdapter((Spinner) view);
        }else if(ColorIndicator.class.isAssignableFrom(viewClass)){
            return new ColorIndicatorIntegerAdapter((ColorIndicator) view);
        }else if(ImageIndicator.class.isAssignableFrom(viewClass)){
            return new ImageIndicatorIntegerAdapter((ImageIndicator) view);
        }
        throw new UnsupportedClassVersionError("Class :"+viewClass+" is not supported currently");

    }


    /**
     *
     * @param viewClass
     * @param <S_ViewType> must be one of the allowed views of its subtype
     * @return  the default value that it will getByGetter.
     */
    @IsAFactory
    public static <S_ViewType extends View> Class getViewAdapterDefaultValueType(Class<S_ViewType> viewClass)
    {
        if(DatePickerDialogWithTextView.class.isAssignableFrom(viewClass)||
                DateTimePickerDialogWithTextView.class.isAssignableFrom(viewClass)||
                TimePickerDialogWithTextView.class.isAssignableFrom(viewClass)
                )
        {
            return Calendar.class;
        }else if(Spinner.class.isAssignableFrom(viewClass)||
                RadioGroup.class.isAssignableFrom(viewClass) ||
                ColorIndicator.class.isAssignableFrom(viewClass)||
                ImageIndicator.class.isAssignableFrom(viewClass)
                ) {
            return Integer.class;
        }else if(CheckBox.class.isAssignableFrom(viewClass)){
            return Boolean.class;
        }else if(TextView.class.isAssignableFrom(viewClass)) {
            /**
             *  textview edittext
             */
            return String.class;
        }else if(RatingBar.class.isAssignableFrom(viewClass)){
            return Float.class;
        }else{
            return null;
        }

    }


    /**
     *
     * @param view
     * @param desiredValueClass can be null, if it is null,you need to cast the value.
     *                             It is only used to indicate the return type.no other means.
     * @param <S_ViewType>
     * @param <S_ValueType>
     * @return
     */
    @IsAFactory
    public static <S_ViewType extends View,S_ValueType> S_ValueType getViewValue(S_ViewType view, Class<S_ValueType> desiredValueClass)
    {
        if(view==null)return null;
        //===TextView
        if(view instanceof CheckBox)
        {
            return (S_ValueType) (Boolean) ViewUtil.getViewAsCheckBox(view).isChecked();
        } else if(view instanceof TimePickerDialogWithTextView) {
            return (S_ValueType) Util.cast(view,TimePickerDialogWithTextView.class).getTime();
        } else if(view instanceof DatePickerDialogWithTextView) {
            return (S_ValueType) Util.cast(view,DatePickerDialogWithTextView.class).getTime();
        } else if(view instanceof DateTimePickerDialogWithTextView) {
            return (S_ValueType) Util.cast(view,DateTimePickerDialogWithTextView.class).getTime();
        } else if(view instanceof EditText) {
            return (S_ValueType) ViewUtil.getViewAsEditText(view).getText().toString();
        }else if(view instanceof TextView){
            return (S_ValueType) ViewUtil.getViewAsTextView(view).getText().toString();
        }else if(view instanceof Spinner){
            return (S_ValueType) (Integer)ViewUtil.getViewAsSpinner(view).getSelectedItemPosition();
            //====ViewGroup
        }else if(view instanceof RadioGroup){
            return (S_ValueType) (Integer)ViewUtil.getViewAsRadioGroup(view).getCheckedRadioButtonId();
        }else if(view instanceof ColorIndicator){
            return (S_ValueType) (Integer)((ColorIndicator)view).getCurrentSelected();
        }else if(view instanceof ImageIndicator){
            return (S_ValueType) (Integer)((ImageIndicator)view).getCurrentSelected();
        }else if(view instanceof RatingBar){
            return (S_ValueType) (Float)Util.cast(view,RatingBar.class).getRating();
        }else{
            throw new UnsupportedClassVersionError("type "+view.getClass()+" is not supported");
        }

    }
    public static <S_ViewType extends View,S_ValueType> S_ValueType getViewValue(S_ViewType view)
    {
        return getViewValue(view,null);
    }

    /**
     *  if value is null, it will not be set.
     * @param view
     * @param value
     * @param <S_ViewType>
     * @param <ValueType>
     */
    public static <S_ViewType extends View,ValueType> void setViewValue(S_ViewType view,ValueType value)
    {
        if(view==null||value==null)return;
        //===TextView
        if(view instanceof CheckBox)
        {
            ViewUtil.getViewAsCheckBox(view).setChecked((Boolean)value);
        } else if(view instanceof TimePickerDialogWithTextView) {
            ((TimePickerDialogWithTextView)view).setTime((Calendar) value);
        } else if(view instanceof DatePickerDialogWithTextView) {
            ((DatePickerDialogWithTextView)view).setTime((Calendar) value);
        } else if(view instanceof DateTimePickerDialogWithTextView) {
            Util.cast(view,DateTimePickerDialogWithTextView.class).setTime((Calendar)value);
        }else if(view instanceof EditText){
            ViewUtil.getViewAsEditText(view).setText((String)value);
        }else if(view instanceof TextView){
            ViewUtil.getViewAsTextView(view).setText((String)value);
        }else if(view instanceof Spinner){
            ViewUtil.getViewAsSpinner(view).setSelection((Integer)value);
            //setTitleSelection(((Integer)value));

            //====ViewGroup
        }else if(view instanceof RadioGroup){
            ViewUtil.getViewAsRadioGroup(view).check((Integer)value);
        }else if(view instanceof ColorIndicator){
            ((ColorIndicator)view).setCurrentSelected((Integer)value);
        }else if(view instanceof ImageIndicator){
            ((ImageIndicator)view).setCurrentSelected((Integer)value);
        }else if(view instanceof RatingBar){
            Util.cast(view,RatingBar.class).setRating((Float)value);
        }else{
            throw new UnsupportedClassVersionError("type "+view.getClass()+" is not supported");
        }
    }
}

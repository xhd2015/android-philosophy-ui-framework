package fulton.shaw.android.x.public_interfaces;


/**
 * Created by 13774 on 7/28/2017.
 */

public class AppConfig implements fulton.util.android.public_interfaces.AppConfig {
    public static final String CONFIG_RUNNING=null;
    public static final String CONFIG_FILE_APPICATION="applicationLevelConfig";
    public static final String CONFIG_KEY_IS_FIRST_TIME_RUNNING="isFirstTimeRunning";
    public static final String CONFIG_KEY_EXIT_AFTER_ADD="exitAfterAdd";
    public static final String CONFIG_KEY_VIEW_DETAIL_AFTER_EXIT="viewDetailAfterExit";
    public static final String CONFIG_KEY_REPLACE_IF_NOT_EMPTY="replaceIfNotEmpty";
    public static final String CONFIG_KEY_SHOW_LIST_DAYS="showListDays";
    public static final String CONFIG_KEY_SECURITY_PASSWORD="securityPassword";
    @Deprecated
    public static final String CONFIG_KEY_START_DATE="startDate";
    @Deprecated
    public static final String CONFIG_KEY_NUM_DAYS="numDays";
    @Deprecated
    public static final String CONFIG_KEY_SHOW_TODAY="showToday";

    public static final String CONFIG_KEY_SHOWN_DAYS="shownDays";
    public static final String CONFIG_KEY_START_CALENDAER="startCalendar";
    public static final String CONFIG_KEY_END_CALENDAER="endCalender";
    public static final String CONFIG_KEY_DATE_MODEL_INDEX="dateModelIndex";

    public static final String CONFIG_KEY_MIN_SHOWN_ITEM_ALARM="minShownItemAlarm";
    public static final String CONFIG_KEY_MAX_SHOWN_ITEM_ALARM="maxShownItemAlarm";
    public static final String CONFIG_KEY_MIN_SHOWN_ITEM_REVIEW="minShownItemReview";
    public static final String CONFIG_KEY_MAX_SHOWN_ITEM_REVIEW="maxShownItemReview";
    public static final String CONFIG_KEY_COMMON_TAGS_LIST="commonTagsList";
    public static final String CONFIG_KEY_FILTER_CONDITION="filterCondition";
    public static final String CONFIG_KEY_CONDITION_TYPE_INDEX="conditionTypeIndex";
    public static final String CONFIG_KEY_SAVED_FILTER_DIALOG_TEXT="savedFilterDialogText";
    public static final String CONFIG_KEY_SAVED_FILTER_DIALOG_SCROLL_Y="savedFilterDialogScrollY";
    public static final String CONFIG_KEY_SAVED_FILTER_DIALOG_SAVED_SHOWNSTATE="savedFilterDialogSavedShownstate";
    public static final String CONFIG_KEY_SAVED_FILTER_DIALOG_SAVED_COMMON_SHOWNSTATE="savedFilterDialogSavedCommonShownstate";


    public static final String ARG_TABLE="argTable";
    public static final String ARG_ID="argId";


    @Override
    public Object getConfig(String key) {
        return null;
    }

    @Override
    public String getConfigAsString(String key) {
        return null;
    }

    @Override
    public int getConfigAsInt(String key) {
        return 0;
    }

    @Override
    public double getConfigAsDouble(String key) {
        return 0;
    }

    @Override
    public void onFirstRunning(Object... args) {//context, we need to create a database,and check other things

    }
}

package fulton.util.android.varman;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.JsonUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/12/2017.
 */

/**
 * before any access, load must be called.
 *
 * remember: you cannot put null to it.
 *
 * current supported:
 *          ArrayList<String>
 *          Calendar
 *
 *  To make the older version work with this,just add extra info&class name.
 */
public class StateVariableManager {
    public interface OnLoadMapAction{
        void onLoadMap(Map<String,Object> map);
    }
    protected SharedPreferences mPref;
    protected SharedPreferences.Editor mPrefEditor;
    protected HashMap<String,Object> mMap;

    public OnLoadMapAction getActionOnLoadMap() {
        return mAction;
    }

    public void setActionOnLoadMap(OnLoadMapAction action) {
        mAction = action;
    }

    protected OnLoadMapAction mAction;

    public static final String EXTRA_SUFFIX="extraInfo";

    public StateVariableManager(SharedPreferences pref)
    {
        mPref=pref;
        mPrefEditor=pref.edit();
    }


    public interface DefaultValueGetter<E>{
        E getValue(String key);
    }
    public static class CalendarDefaultValueGetter implements DefaultValueGetter<Calendar>{
        protected static CalendarDefaultValueGetter sGetter;
        protected CalendarDefaultValueGetter(){}
        public static CalendarDefaultValueGetter getInstance(){
            if(sGetter==null)
                sGetter=new CalendarDefaultValueGetter();
            return sGetter;
        }
        @Override
        public Calendar getValue(String key) {
            return CalendarUtil.getTodayBegin();
        }
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @param <E>
     * @return
     */
    public <E> E get(String key,E defaultValue)
    {
        E value= (E) mMap.get(key);
        if(value==null && defaultValue!=null)
            set(key,defaultValue);
        return value==null?defaultValue:value;
    }

    public <E> E getByGetter(String key, ValueGetter<?,E> getter)
    {
        E value=(E)mMap.get(key);
        ValueGetter<String,E> myGetter= (ValueGetter<String, E>) getter;
        if(value==null && (value=myGetter.getValue(key))!=null)
        {
            set(key,value);
        }
        return value;
    }

    public <E> E get(String key, @ValueGetter.ValueGetterType Object o,Class<E> clz)
    {
        E value=(E)mMap.get(key);
        if(value==null && (value= (E) ValueGetter.getValueFromObjectOrGetter(key,o))!=null)
        {
            set(key,value);
        }
        Util.logi("default value:"+value);
        return value;
    }

    public <E> E get(String key,Class<E> eClass)
    {
        return (E)mMap.get(key);
    }

    public <E> void setDefaultValue(String key,E value)
    {
        if(value!=null && !mMap.containsKey(key)) {
            set(key, value);
        }
    }

    /**
     *
     * @param key
     * @param value
     * @param <E> must be one of primitive type,or arraylist.
     */

    public <E> void set(String key,E value)
    {
        if(value!=null) {
            if(ArrayList.class.isAssignableFrom(value.getClass())||Calendar.class.isAssignableFrom(value.getClass())){
               mMap.put(key+EXTRA_SUFFIX,value.getClass().getName());
            }
            mMap.put(key, value);
        }
    }
    public <E> void setExtrInfo(String key,Class<E> eClass)
    {
        mMap.put(key+EXTRA_SUFFIX,eClass.getName());
    }
    public void load()
    {
        if(mMap!=null)//already loaded
            return;
        mMap= (HashMap<String, Object>) mPref.getAll();
        if(mAction!=null)
            mAction.onLoadMap(mMap);
        for(Map.Entry<String,Object> entry:mMap.entrySet())
        {
            Util.logi("loading:"+entry.getKey());
            if(!entry.getKey().endsWith(EXTRA_SUFFIX)) {
                String extraInfoKey = entry.getKey() + EXTRA_SUFFIX;
                String extraInfoValue = (String) mMap.get(extraInfoKey);

                if (extraInfoValue != null) {
                    try {
                        Class valueClass = Class.forName(extraInfoValue);
                        if (ArrayList.class.isAssignableFrom(valueClass)) {
                            entry.setValue(JsonUtil.jsonArrayToArrayList((String) entry.getValue()));
                        } else if (Calendar.class.isAssignableFrom(valueClass)) {
                            entry.setValue(CalendarUtil.timeInMillisToCalendar((long) entry.getValue()));
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {//do nothing

                }
            }
        }
    }
    public void asyncLoad()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                load();
            }
        }).start();
    }

    public void save()
    {
        for(Map.Entry<String,Object> entry:mMap.entrySet()) {
            Class valueClass = entry.getValue().getClass();
            if (String.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putString(entry.getKey(), (String) entry.getValue());
            } else if (Integer.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putInt(entry.getKey(), (int) entry.getValue());
            } else if (Long.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putLong(entry.getKey(), (long) entry.getValue());
            } else if (Boolean.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putBoolean(entry.getKey(), (boolean) entry.getValue());
            } else if (Float.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putFloat(entry.getKey(), (float) entry.getValue());
            } else if (Set.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putStringSet(entry.getKey(), (Set<String>) entry.getValue());
            } else if (Calendar.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putLong(entry.getKey(), ((Calendar) entry.getValue()).getTimeInMillis());
            } else if (ArrayList.class.isAssignableFrom(valueClass)) {
                mPrefEditor.putString(entry.getKey(), JsonUtil.toJsonArray((ArrayList) entry.getValue()).toString());
            }else{
                throw new UnsupportedClassVersionError(""+valueClass);
            }
        }

        mPrefEditor.commit();
    }

    public void remove(String key)
    {
        Object value=mMap.get(key);
        if(value!=null)
        {
            if(ArrayList.class.isAssignableFrom(value.getClass())||
                    Calendar.class.isAssignableFrom(value.getClass())
                    )
            {
                mMap.remove(key+EXTRA_SUFFIX);
                mPrefEditor.remove(key+EXTRA_SUFFIX);
            }
            mMap.remove(key);
            mPrefEditor.remove(key);
        }
    }

    public boolean containsKey(Object key) {
        return mMap.containsKey(key);
    }

    public Set<String> keySet() {
        return mMap.keySet();
    }

    public String toString()
    {
        return mMap==null?"{}":mMap.toString();
    }

    public static void actionsMustBeDoneWhenUpdateNewExistingInfo(StateVariableManager man)
    {
        man.setExtrInfo("key",ArrayList.class);
        Util.logi(man);//see until it satisfies
        man.save();
        if(true)
            throw new NullPointerException();
    }
}

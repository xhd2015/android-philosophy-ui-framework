package fulton.shaw.android.mytestapplication.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.Observable;

import fulton.shaw.android.mytestapplication.BR;


/**
 * Created by 13774 on 8/8/2017.
 */

public class User extends BaseObservable {
    private String mName;
    private String mSex;

    public User(String name, String sex) {
        mName = name;
        mSex = sex;
    }


    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getSex() {
        return mSex;
    }

    public void setSex(String sex) {
        mSex = sex;
        notifyPropertyChanged(BR.sex);
    }
}

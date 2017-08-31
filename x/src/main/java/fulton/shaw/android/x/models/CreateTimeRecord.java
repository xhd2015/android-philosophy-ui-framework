package fulton.shaw.android.x.models;

import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;

import java.util.Calendar;

import fulton.shaw.android.x.models.awares.CreatedTimeAware;
import fulton.shaw.android.x.models.awares.CursorAwareProxy;
import fulton.shaw.android.x.models.awares.ExternReferenceAware;
import fulton.shaw.android.x.models.awares.RowIdAware;

/**
 * Created by 13774 on 8/8/2017.
 */

public class CreateTimeRecord extends BaseObservable implements RowIdAware,ExternReferenceAware,CreatedTimeAware {
    private CursorAwareProxy mProxy;
    public CreateTimeRecord(Cursor cursor) {
        mProxy=new CursorAwareProxy(cursor);
    }


    @Override
    @Nullable
    public Integer getId() {
        return mProxy.getId();
    }

    @Override
    public String getTableName() {
        return mProxy.getTableName();
    }

    @Override
    public Integer getInnerId() {
        return mProxy.getInnerId();
    }

    @Bindable
    @Override
    public Calendar getCreatedTime() {
        return mProxy.getCreatedTime();
    }
}

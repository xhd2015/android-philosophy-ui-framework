package fulton.shaw.android.x.models;

import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;

import fulton.shaw.android.x.models.awares.ContentAware;
import fulton.shaw.android.x.models.awares.CursorAwareProxy;
import fulton.shaw.android.x.models.awares.ExternReferenceAware;
import fulton.shaw.android.x.models.awares.RowIdAware;

/**
 * Created by 13774 on 8/8/2017.
 */

public class Tag extends BaseObservable implements ContentAware,ExternReferenceAware,RowIdAware{
    private CursorAwareProxy mProxy;


    public Tag(Cursor cursor)
    {
        mProxy = new CursorAwareProxy(cursor);
    }

    @Override
    @Nullable
    public Integer getId() {
        return mProxy.getId();
    }

    @Bindable
    @Override
    public String getContent() {
        return mProxy.getContent();
    }


    @Override
    public String getTableName() {
        return mProxy.getTableName();
    }

    @Override
    public Integer getInnerId() {
        return mProxy.getInnerId();
    }

}

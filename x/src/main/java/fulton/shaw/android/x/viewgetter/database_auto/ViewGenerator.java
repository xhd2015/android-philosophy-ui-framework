package fulton.shaw.android.x.viewgetter.database_auto;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fulton.util.android.notations.IsAFactory;

/**
 * Created by 13774 on 8/13/2017.
 */

public abstract class ViewGenerator {
    public abstract View generateView(ViewGroup parent);
    @IsAFactory
    public static ViewGenerator ViewGeneratorByInflateRes(final LayoutInflater inflater, final int res)
    {
        return new ViewGenerator() {
            @Override
            public View generateView(ViewGroup parent) {
                return inflater.inflate(res,parent,false);
            }
        };
    }
}

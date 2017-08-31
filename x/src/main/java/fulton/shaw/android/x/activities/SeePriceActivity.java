package fulton.shaw.android.x.activities;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.SimpleCursorAdapter;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/4/2017.
 */

public class SeePriceActivity extends ListActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.logi("in see price");

        setContentView(R.layout.see_price_layout);
        SQLiteDatabase db= SqliteHelper.getDatabase(this);
        Cursor c=db.rawQuery("Select t1._id as _id,t1.name,t1.description,t2._id as conId,price,condition " +
                        "From tableThing as t1,tableConditionalPrice as t2 "+
                        "Where t1._id=t2.innerId",null
        );
        Util.logi("price count:"+c.getCount());
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(
                this,
                R.layout.see_price_row_layout,
                c,
                new String[]{"name","description","condition","price"},
                new int[]{R.id.name,R.id.description,R.id.condition,R.id.price}
        );
        setListAdapter(adapter);
    }
}

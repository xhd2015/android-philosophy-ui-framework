package fulton.util.android.interfaces.multiview_valuegetter;

import android.widget.TextView;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ViewValueGetterOfTextView extends MultiViewValueGetter<Single<TextView>,String> {
    private static ViewValueGetterOfTextView ourInstance = null;

    public static ViewValueGetterOfTextView getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewValueGetterOfTextView();
        return ourInstance;
    }

    private ViewValueGetterOfTextView() {
    }



    @Override
    public void setValue(Single<TextView> textViewSingle, String value) {
        textViewSingle.first.setText(value);
    }

    @Override
    public String getValue(Single<TextView> textViewSingle) {
        return textViewSingle.first.getText().toString();
    }

    @Override
    public Class<String> getValueType() {
        return String.class;
    }
}

package fulton.util.android.interfaces.multiview_checkers;

import android.widget.TextView;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ViewCheckerOfTextViewNotEmpty extends ViewCheckerOfSingleView<TextView>{
    private static ViewCheckerOfTextViewNotEmpty ourInstance = null;

    public static ViewCheckerOfTextViewNotEmpty getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewCheckerOfTextViewNotEmpty();
        return ourInstance;
    }

    private ViewCheckerOfTextViewNotEmpty() {
    }

    @Override
    public boolean check(TextView view) {
        return view.length()>0;
    }
}

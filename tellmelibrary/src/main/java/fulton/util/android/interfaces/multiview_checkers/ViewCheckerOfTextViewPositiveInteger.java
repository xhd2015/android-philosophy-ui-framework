package fulton.util.android.interfaces.multiview_checkers;

import android.widget.TextView;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ViewCheckerOfTextViewPositiveInteger extends ViewCheckerOfSingleView<TextView>{
    private static ViewCheckerOfTextViewPositiveInteger ourInstance = null;

    public static ViewCheckerOfTextViewPositiveInteger getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewCheckerOfTextViewPositiveInteger();
        return ourInstance;
    }

    private ViewCheckerOfTextViewPositiveInteger() {
    }

    @Override
    public boolean check(TextView view) {
        if(view.length()>0)
        {
            CharSequence s=view.getText();
            for(int i=0;i<s.length();i++)
                if(!Character.isDigit(s.charAt(i)))
                    return false;
            return true;
        }
        return false;
    }
}

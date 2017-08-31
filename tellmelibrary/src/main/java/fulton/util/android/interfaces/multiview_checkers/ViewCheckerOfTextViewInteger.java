package fulton.util.android.interfaces.multiview_checkers;

import android.widget.TextView;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ViewCheckerOfTextViewInteger extends ViewCheckerOfSingleView<TextView>{
    private static ViewCheckerOfTextViewInteger ourInstance = null;

    public static ViewCheckerOfTextViewInteger getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewCheckerOfTextViewInteger();
        return ourInstance;
    }

    private ViewCheckerOfTextViewInteger() {
    }


    @Override
    public boolean check(TextView view) {
        if(view.length()>0)
        {
            CharSequence s=view.getText();
            int start=0;
            if(s.charAt(0)=='+' || s.charAt(0)=='-')start++;
            for(int i=start;i<s.length();i++)
                if(!Character.isDigit(s.charAt(i)))
                    return false;
            return true;
        }
        return false;
    }
}

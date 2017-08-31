package fulton.shaw.android.tellme.experiment.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.experiment.DateDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class DateRelatedFragment extends Fragment {


    public class DateRelatedListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    public DateRelatedFragment() {
        // Required empty public constructor
    }

    public abstract DateDetailActivity getActivityContext();
    public abstract void setShowArgument(Bundle args);

}

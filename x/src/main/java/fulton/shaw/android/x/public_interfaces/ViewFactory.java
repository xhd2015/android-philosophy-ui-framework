package fulton.shaw.android.x.public_interfaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.fragments.MainTaggedViewProcessor;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.interfaces.valueproviders.ValueProvider;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/23/2017.
 */

public class ViewFactory {

    private static MainTaggedViewProcessor<Triple<LayoutInflater,ViewGroup,View>> sConstructor=null;
    private static MainTaggedViewProcessor<Pair<View,ValueProvider>> sFiller=null;

    public static View newView(String mainTag, LayoutInflater inflater, ViewGroup parent)
    {
        final Triple<LayoutInflater, ViewGroup, View> data = getConstructor().getData();
        data.first=inflater;
        data.second=parent;
        getConstructor().processMainTag(mainTag);
        return data.third;
    }

    public static void fillView(String mainTag, View view, ValueProvider provider)
    {
        final Pair<View, ValueProvider> data = getFiller().getData();
        data.first=view;
        data.second=provider;
        getFiller().processMainTag(mainTag);
    }

    public static MainTaggedViewProcessor<Triple<LayoutInflater,ViewGroup,View>> getConstructor() {
        if(sConstructor==null)
        {
            sConstructor=new MainTaggedViewProcessor<Triple<LayoutInflater,ViewGroup,View>>(){

                @Override
                protected Triple<LayoutInflater, ViewGroup, View> onInitData() {
                    return new Triple<>();
                }
                @Override
                public void prcoessSaySomething() {
                    mData.third=mData.first.inflate(R.layout.show_list_say_something,mData.second,false);
                }

                @Override
                public void prcoessBlog() {
                    mData.third=mData.first.inflate(R.layout.show_list_blog,mData.second,false);
                }
            };
        }
        return sConstructor;
    }

    public static MainTaggedViewProcessor<Pair<View,ValueProvider>> getFiller() {
        if(sFiller==null)
        {
            sFiller=new MainTaggedViewProcessor<Pair<View, ValueProvider>>() {
                @Override
                protected Pair<View, ValueProvider> onInitData() {
                    return new Pair<>();
                }

                @Override
                public void prcoessSaySomething() {
                    TextView detailTextView = (TextView) mData.first.findViewById(R.id.detailTextView);
                    detailTextView.setText((CharSequence) mData.second.getValue(SqlUtil.COL_DETAIL, String.class));
                }
                @Override
                public void prcoessBlog() {
                    TextView briefTextView= (TextView)mData.first.findViewById(R.id.briefTextView);
                    TextView detailTextView= (TextView) mData.first.findViewById(R.id.detailTextView);


                    briefTextView.setText((CharSequence) mData.second.getValue(SqlUtil.COL_BRIEF,String.class));
                    detailTextView.setText((CharSequence) mData.second.getValue(SqlUtil.COL_DETAIL,String.class));
                }
            };
        }
        return sFiller;
    }
}

package fulton.shaw.android.x.viewgetter.database_auto;

import android.view.View;

import java.util.ArrayList;

import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.notations.IsAFactory;

/**
 * Created by 13774 on 8/13/2017.
 */

public abstract class ViewGetter<ViewType extends View> {
    public abstract ViewType getView(View rootView);
    public Class<ViewType> getViewClass(){ throw new UnsupportedOperationException("not yet implmented");}

    @IsAFactory
    public static <S_ViewType extends View>  ViewGetter<S_ViewType> IdViewGetter(final int id, final Class<S_ViewType> viewClass)
    {
        return new ViewGetter() {
            @Override
            public View getView(View rootView) {
                return rootView.findViewById(id);
            }

            @Override
            public Class getViewClass() {
                return viewClass;
            }
        };
    }

    /**
     *
     * @param refInfo
     * @param args  has the format  <key,viewId,viewClass>
     * @return
     */
    public static ArrayList<ViewGetter> getViewGettersBaseOnKey(final ArrayList<ViewInfo> refInfo, final Object...args)
    {
        final ArrayList<ViewGetter> getters=new ArrayList<ViewGetter>(refInfo.size()){{
//            Util.logi("size:"+refInfo.size());
           for(int i=0;i<refInfo.size();i++)
           {
               boolean added=false;
               for(int j=0;j<args.length;j+=3)
               {
                   String key= (String) args[j];
                   if(key.equals(refInfo.get(i).key))
                   {
                       Integer id= (Integer) args[j+1];
                       Class viewClass= (Class) args[j+2];
                       add(ViewGetter.IdViewGetter(id,viewClass));
                       added=true;
                   }
               }
               if(!added)
                   add(null);
           }
        }};
        return getters;
    }


}

package fulton.util.android.utils;


import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fulton.util.android.aware.UiThreadAware;
import fulton.util.android.interfaces.comparators.Comparator;
import fulton.util.android.interfaces.comparators.ListBasedComparator;

public class Util{
	public static final String ITAG="UTIL_ITAG";
	public static final int DEFAULT_TIMES=3;
	public static final int DEFAULT_SECS=1;
	public static final long DEFAULT_TIME_UNIT=1000;
	public static void main(String...args)
	{
		System.out.print(String.format("%s", "123","466"));
	}

	public static void logi(String tag,String msg,int times,int secInter,long timeUnit)
	{
		for(int i=0;i<times;i++)
		{
			Log.i(tag,msg);
//			try {
//				Thread.sleep(timeUnit*secInter);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
	public static void logi(String tag,String msg,int times,int secInter)
	{
		logi(tag,msg,times,secInter,DEFAULT_TIME_UNIT);
	}

	public static void logi(String msg,int times)
	{
		Util.logi(ITAG,msg,times,DEFAULT_SECS);
	}
	public static void logi(String msg)//200 ms
	{
		Util.logi(ITAG,msg,1,1,200);
	}

	public static void logi(Object...args) {
		Util.logi(Arrays.deepToString(args));
	}
	public static void logi(Object o) {
		Util.logi(String.valueOf(o));
	}
	public static <E> void logi(ArrayList<E> list)
	{
		Util.logi(list.toArray());
	}
	public static void logi(Cursor cursor)
    {
        int pos=cursor.getPosition();
        cursor.moveToPosition(-1);
        Util.logi(SqlUtil.dumpCursorValue(cursor));
        cursor.moveToPosition(pos);
    }
	public static void logi(Calendar calendar)
	{
		Util.logi(CalendarUtil.SQLITE_DATETIME_FORMAT.format(calendar.getTime()));
	}
	public static void logv(String tag,String msg,int times,int secInter)
	{
		for(int i=0;i<times;i++)
		{
			Log.v(tag,msg);
			try {
				Thread.sleep(1000*secInter);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void logd(String tag,String msg,int times,int secInter)
	{
		for(int i=0;i<times;i++)
		{
			Log.d(tag,msg);
			try {
				Thread.sleep(1000*secInter);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public static String join(String joiner,String ... strings)
	{
		StringBuilder sb=new StringBuilder();
		int length= (strings==null?0:strings.length);
		for(int i=0;i<length-1;i++)
		{
			sb.append(strings[i]).append(joiner);
		}
		if(length>=1)
			sb.append(strings[length-1]);
		return  sb.toString();
	}
	public static String join(String joiner,ArrayList lists)
	{
		return join(joiner, lists, new ArrayListStringGetter() {
			@Override
			public String getString(ArrayList list, int i) {
				return String.valueOf(list.get(i).toString());
			}
		});
	}
	public interface ArrayListStringGetter{
		String getString(ArrayList list,int i);
	}
	public static String join(String joiner,ArrayList lists,ArrayListStringGetter getter)
	{
		StringBuilder sb=new StringBuilder();
		int length= (lists==null?0:lists.size());
		for(int i=0;i<length-1;i++)
		{
			sb.append(getter.getString(lists,i)).append(joiner);
		}
		if(length>=1)
			sb.append(getter.getString(lists,length-1));
		return  sb.toString();
	}

	public static String joinRepeat(String joiner,String singleString,int times)
	{
		if(times==0)return "";
		else if(times==1)return singleString;
		else{
			String[] makeups=new String[times];
			for(int i=0;i<times;i++)
				makeups[i]=singleString;
			return join(joiner,makeups);
		}
	}


	public static <K,V> V mapDefaultGet(Map<K,V> map,K key,V defaultValue)
	{
		V value=null;
		if(map!=null && (value=map.get(key))!=null)
		{
			return value;
		}
		return defaultValue;
	}

	public static void sleepIgnoreInterruption(long mills)
	{
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static <E> E cast(Object v,Class<E> eClass)
	{
		return (E)v;
	}

	public static void runOnUiThreadDelay(final UiThreadAware aware, final long delay, final Runnable runnable)
	{
		if(Thread.currentThread().equals(aware.getUiThread()))
		{
			new Thread(new Runnable() {
				@Override
				public void run() {
					sleepIgnoreInterruption(delay);
					aware.runOnUiThread(runnable);
				}
			}).start();
		}else{
			sleepIgnoreInterruption(delay);
			aware.runOnUiThread(runnable);
		}
	}

	public static <E> ArrayList<E> arrayToList(final E[] array)
	{
		ArrayList<E> res=new ArrayList<>();
		if(array!=null)
		{
			for(E e:array)
			{
				res.add(e);
			}
		}
		return res;
	}

	public @interface PrimitiveClass{}
	/**
	 *
	 * @param list
	 * @param valueClass
	 * @param <E>
	 * @return
	 */
	public static <E> E[] listToArray(ArrayList<E> list,@PrimitiveClass Class<E> valueClass)
	{
		Object[] res=null;
		if(String.class.equals(valueClass))
		{
			res=new String[list.size()];
		}else if(Integer.class.equals(valueClass)){
			res=new Integer[list.size()];
		}
		else if(Object.class.equals(valueClass)){
			res= new Object[list.size()];
		}else{
			throw new UnsupportedClassVersionError("Class "+valueClass.getName()+" is not supported");
		}
		for (int i = 0; i < res.length; i++) {
			res[i]=list.get(i);
		}
		return (E[]) res;
	}

	@Deprecated
	public interface Comparator<E,V>{
		boolean compareEquals(E a,V b);
	}

	/**
	 *
	 * @param arrayList
	 * @param argument
	 * @param comparator
	 * @param <E>
	 * @return  -1 if fails
	 */
	public static <E,V> int searchArrayList(ArrayList<E> arrayList, V argument, fulton.util.android.interfaces.comparators.Comparator<E,V> comparator)
	{
		for(int i=0;i<arrayList.size();i++)
			if(comparator.equalsWith(arrayList.get(i),argument))
				return i;
		return -1;
	}
	public static <E,V> int searchArray(E[] list, V argument, int start, int stepSize, ListBasedComparator<E[],V> comparator)
	{
		for(int i=start;i<list.length;i+=stepSize) {
			comparator.setCurrentIndex(i);
			if (comparator.equalsWith(list, argument))
				return i;
		}
		return -1;
	}




	public static boolean compareEquals(Object o1,Object o2)
	{
		return o1==null?o2==null:o1.equals(o2);
	}

	public interface KeyGetter<V,X,E>{
		int getSize(V list);
		X getKey(V list);
		boolean next(V list);//iterator
		void resetIterator(V list);
		int	  currentIndex();
		E getElement(V list,int index);
	}
	public interface ListGenerator<K,E>{
		K newList(int size);
		void setElement(K list,int index,E e);
	}


	public static String getSubString(String s,int start,int end)
	{
		if(end<0 || end>s.length())
			end=s.length();
		if(start<0 || start>=end)return "";
		return s.substring(start,end);

	}

	/**
	 *
	 * @param list1
	 * @param list2
	 * @param list1Getter
	 * @param list2Getter
	 * @param generator
	 * @param comparator
	 * @param <E>
	 * @param <V>
	 * @param <EKey>
	 * @param <VKey>
	 * @param <K>
	 * @param <KElement>
	 * @return which has the length of E,with some set to null.
	 */
	public static <E,V,EKey,VKey,K,KElement> K expandByKeysFromArray(E list1,V list2,
															KeyGetter<E,EKey,Object> list1Getter,KeyGetter<V,VKey,KElement> list2Getter,
															ListGenerator<K,KElement> generator,
																	 Comparator<EKey,VKey> comparator
															)
	{
		K newList=generator.newList(list1Getter.getSize(list1));
		while (list2Getter.next(list2))
		{
			VKey vKey=list2Getter.getKey(list2);
			list1Getter.resetIterator(list1);
			int index=-1;
			while (list1Getter.next(list1))
			{
				if(comparator.compareEquals(list1Getter.getKey(list1),vKey)) {
					index = list1Getter.currentIndex();
					break;
				}
			}
			if(index==-1)
				throw new UnsupportedOperationException("cannot find key");
			generator.setElement(newList,index,list2Getter.getElement(list2,list2Getter.currentIndex()));
		}
		return newList;
	}

	public interface ElementGentor<E>{
		E genElement(Object[] list,int index);
	}
	public static <E,V,S> ArrayList<E> expandArrayList(final ArrayList<V> refList, Object[] givenList,
													   int start, int step, int keyIndex, fulton.util.android.interfaces.comparators.Comparator<V,S> comparator,
													   ElementGentor<E> gentor
													   )
	{
		ArrayList<E> res=new ArrayList<E>(refList.size()){{
			for(V v:refList)
				add(null);
		}};
		for(int i=start;i<givenList.length;i+=step)
		{
			int index=Util.searchArrayList(refList,(S)givenList[i+keyIndex],comparator);
			if(index==-1)
				throw new UnsupportedOperationException("key not found");
			res.set(index,gentor.genElement(givenList,i));
		}
		return res;
	}

}
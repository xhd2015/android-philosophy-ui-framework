package fulton.util.android.thread;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 13774 on 7/14/2017.
 */

public class LockedObject<E,D> extends Object{
    private D additionData;

    private E value;
    private ReentrantLock lock=new ReentrantLock();

    public LockedObject()
    {

    }
    public LockedObject(E e)
    {
        this.value=e;
    }
    public void set(E value)
    {
        lock.lock();
        this.value = value;
        lock.unlock();
    }
    public E get()
    {
        E e;
        lock.lock();
        e=value;
        lock.unlock();
        return e;
    }

    public  boolean isNull()
    {
        return get()==null;
    }
    public void set_unsafe(E value)
    {
        this.value = value;
    }
    public E get_unsafe()
    {
        return this.value;
    }
    public boolean isNull_unsafe()
    {
        return this.value == null;
    }


    public ReentrantLock getLock()
    {
        return lock;
    }

    public void lock()
    {
        lock.lock();
    }
    public void unlock()
    {
        lock.unlock();
    }

    public Condition newCondition() {
        return lock.newCondition();
    }

    public D getAdditionalData() {
        return additionData;
    }

    public void setAdditionalData(D additionData) {
        this.additionData = additionData;
    }

}

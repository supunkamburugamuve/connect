package connect.queue;

import connect.Namable;
import connect.lang.ManagedEntity;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public interface LinkQueue<T> extends Namable, ManagedEntity {
    T peek();

    T poll();

    T poll(long timeout, TimeUnit timeUnit) throws InterruptedException;

    /**
     * If no space return false immediately
     *
     * @param t object to add
     * @return weather addition was success or failure
     */
    public boolean offer(T t);

    void add(T t) throws InterruptedException;

    boolean add(T t, long timeout, TimeUnit timeUnit) throws InterruptedException;

    public T take() throws InterruptedException;

    /**
     * Return the capacity of this queue
     *
     * @return capacity
     */
    int capacity();

    int size();

    void setCount(Semaphore semaphore);
}

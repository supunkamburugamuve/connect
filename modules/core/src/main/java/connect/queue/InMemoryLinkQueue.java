package connect.queue;

import connect.QualifiedName;
import connect.State;
import connect.lang.ModuleContext;
import connect.lang.RunningException;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryLinkQueue<T> implements LinkQueue<T> {
    protected ModuleContext moduleContext;

    private State state = State.CREATED;

    private final String name;

    /** The queued items */
    private final Object[] objects;

    /** items index for next take, poll, peek or remove */
    private int head;

    /** items index for next put, offer, or add */
    private int tale;

    /** Number of elements in the queue */
    private int size;

    /** Main lock guarding all access */
    private final ReentrantLock lock;
    /** Condition for waiting takes */
    private final Condition notEmpty;
    /** Condition for waiting puts */
    private final Condition notFull;

    private Semaphore semaphore;

    public InMemoryLinkQueue(String name, int capacity) {
        objects = new Object[capacity];

        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public QualifiedName getQualifiedName() {
        if (state != State.CREATED) {
            return new QualifiedName(name, moduleContext.getModuleName().getModule(),
                    moduleContext.getModuleName().getNamespace());
        } else {
            throw new IllegalStateException("The qualified name is available only after initialization");
        }
    }

    public T peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (size == 0) ? null : itemAt(head);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private T extract() {
        final Object[] items = this.objects;
        T x = this.<T>cast(items[head]);
        items[head] = null;
        head = inc(head);
        --size;
        notFull.signal();
        return x;
    }

    public T poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (size == 0) ? null : extract();
        } finally {
            lock.unlock();
        }
    }

    public T poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        long nanos = timeUnit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (size == 0) {
                if (nanos <= 0) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            return extract();
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(T t) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (size == objects.length)
                return false;
            else {
                insert(t);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (size == 0)
                notEmpty.await();
            return extract();
        } finally {
            lock.unlock();
        }
    }

    public void add(T t) throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (size == objects.length) {
                notFull.await();
            }
            insert(t);
        } finally {
            lock.unlock();
        }
    }

    public boolean add(T t, long timeout, TimeUnit timeUnit) throws InterruptedException {
        long nanos = timeUnit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (size == objects.length) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            insert(t);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int capacity() {
        return objects.length;
    }

    public int size() {
        return size;
    }

    public void setCount(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    /**
     * Returns item at index i.
     */
    @SuppressWarnings("unchecked")
    final T itemAt(int i) {
        return this.<T>cast(objects[i]);
    }

    @SuppressWarnings("unchecked")
    <T> T cast(Object item) {
        return (T) item;
    }

    /**
     * Circularly increment i.
     */
    final int inc(int i) {
        return (++i == objects.length) ? 0 : i;
    }

    /**
     * Circularly decrement i.
     */
    final int dec(int i) {
        return ((i == 0) ? objects.length : i) - 1;
    }

    private void insert(T x) {
        objects[tale] = x;
        tale = inc(tale);
        ++size;
        notEmpty.signal();

        // signal the external parties
        if (semaphore != null) {
            semaphore.release();
        }
    }

    public void init(ModuleContext baseContext) throws RunningException {
        this.moduleContext = baseContext;
        state = State.INIT;
    }

    public void destroy() throws RunningException {
        state = State.SHUTDOWN;
    }
}

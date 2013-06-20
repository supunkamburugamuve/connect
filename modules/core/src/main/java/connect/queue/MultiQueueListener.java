package connect.queue;

import connect.Manageable;
import connect.QualifiedName;
import connect.executor.MessageExecutor;
import connect.lang.ModuleContext;
import connect.lang.ManagedEntity;
import connect.lang.RunningException;
import connect.message.Message;
import connect.message.MessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Listens on a Queue and dispatch the messages to a thread pool
 */
public class MultiQueueListener implements Manageable, ManagedEntity {
    private Log log = LogFactory.getLog(MultiQueueListener.class);

    /** The message listener to be called */
    private final MessageListener listener;

    /** Weather we are running */
    private volatile boolean running = false;

    /** The queues we are listening to */
    private List<LinkQueue> queues = new ArrayList<LinkQueue>();

    /** The queues that are not being listening to at the moment */
    private Queue<LinkQueue> freeQueues = new LinkedBlockingQueue<LinkQueue>();

    /** The executor used to run the messages */
    private MessageExecutor executor = null;

    /** The scheduler threads */
    private List<Thread> schedulers = new ArrayList<Thread>();

    /** Number of scheduling threads */
    private int schedulersCount = 1;

    /** This is the name of the parent using the listener */
    private final QualifiedName name;

    private final Lock freeQueueLock = new ReentrantLock();

    public MultiQueueListener(QualifiedName name, final MessageListener listener) {
        this.listener = listener;
        this.name = name;
    }

    public void setExecutor(MessageExecutor executor) {
        this.executor = executor;
    }

    public MessageExecutor getExecutor() {
        return executor;
    }

    public void addQueue(LinkQueue queue) {
        queues.add(queue);
    }

    public List<LinkQueue> getQueues() {
        return queues;
    }

    public void start() {
        // put the queues to be listened to
        for (LinkQueue queue : queues) {
            freeQueues.offer(queue);
        }

        // starting the schedulers
        if (log.isDebugEnabled()) {
            log.debug("Starting the schedulers for: " + name);
        }

        for (int i = 0; i < schedulersCount; i++) {
            Thread t = schedulers.get(i);
            t.start();
        }
    }

    public void stop() {
        running = false;
    }

    public void init(ModuleContext configuration) throws RunningException {
        // start the scheduler threads
        for (int i = 0; i < schedulersCount; i++) {
            schedulers.add(new Thread(new Scheduler()));
        }
    }

    public void destroy() throws RunningException {
        executor.destroy();
    }

    private class Scheduler implements Runnable {
        public void run() {
            while (running) {
                // get the top of the free queue
                LinkQueue queue;

                freeQueueLock.lock();
                try {
                    queue = freeQueues.peek();
                    // we are only going to look at the queues that have a message
                    if (queue.size() > 0) {
                        queue = freeQueues.poll();
                    } else {
                        continue;
                    }
                } finally {
                    freeQueueLock.unlock();
                }

                // pop a message from the queue and execute it
                executor.execute(new MessageProcessor(queue));
            }
        }
    }

    private class MessageProcessor implements Runnable {
        private final LinkQueue queue;

        private MessageProcessor(final LinkQueue queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                // for safety we are going to do a poll with a timeout
                Message message = (Message) queue.poll(100, TimeUnit.MILLISECONDS);

                // put the queue to the listening queues
                freeQueueLock.lock();
                try {
                    freeQueues.offer(queue);
                } finally {
                    freeQueueLock.unlock();
                }
                // handle the message
                listener.onMessage(message);
            } catch (Throwable t) {
                log.error("Un-caught exception", t);
            }
        }
    }
}

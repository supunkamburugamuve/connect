package connect.queue;

import connect.*;
import connect.executor.DefaultThreadFactory;
import connect.lang.ModuleContext;
import connect.lang.ManagedEntity;
import connect.lang.RunningException;
import connect.message.Message;
import connect.message.MessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Keeps track of multiple queues
 */
public class QueueAggregator implements Namable, Manageable, ManagedEntity {
    private Log log = LogFactory.getLog(QueueAggregator.class);

    /** Name of thq aggregator */
    private String name;

    /** State of the aggregator */
    private State state = State.CREATED;

    /** A queue and listener list */
    private Map<String, QueueAndListener> queueListeners = new HashMap<String, QueueAndListener>();

    /** This is the executor for listening to the threads */
    private ExecutorService executor;

    /** Number of threads */
    private int threads = 0;

    private int queuesPerThread = 2;

    private Semaphore[] semaphores;

    private volatile boolean run = true;

    private List<HashMap<String, QueueAndListener>> threadsQueueListeners = null;

    private ModuleContext moduleContext;

    public QueueAggregator(String name, int queuesPerThread) {
        this.queuesPerThread = queuesPerThread;
        this.name = name;
    }

    public QueueAggregator(String name) {
        this(name, 2);
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

    public void addQueue(LinkQueue<Message> queue, MessageListener listener) {
        if (state == State.STARTED || state == State.INIT) {
            throw new IllegalStateException("Cannot add after the listener is started");
        }

        queueListeners.put(queue.getName(), new QueueAndListener(queue, listener));
    }

    public void start() {
        if (state != State.INIT) {
            throw new IllegalStateException("QueueAggregator should be in INIT state to be started");
        }
        // go through the each thread available and start the workers
        for (int i = 0; i < threads; i++) {
            QueueWorker worker = new QueueWorker(i);

            // let the executor execute
            executor.execute(worker);
        }
        state = State.STARTED;
    }

    public void stop() {
        if (state != State.STARTED) {
            throw new IllegalStateException("QueueAggregator should be in STARTED state to be stopped");
        }
        run = false;
    }

    private void loop(int thread) {
        // we loop until there is nothing left
        Map<String, QueueAndListener>  queueAndListenerMap = threadsQueueListeners.get(thread);
        Semaphore s = semaphores[thread];

        while (run) {
            try {
                for (Map.Entry<String, QueueAndListener> entry : queueAndListenerMap.entrySet()) {
                    LinkQueue<Message> queue = entry.getValue().getQueue();
                    // if we have an entry
                    if (queue.size() > 0) {
                        // acquire a permit before we get a message
                        // here we will block if no messages are available in queue
                        s.acquire();
                        Message m = queue.poll();

                        // execute the message
                        entry.getValue().getMessageListener().onMessage(m);
                    }
                }
            } catch (InterruptedException e) {
                String msg = "Thread dies due to exception in queue listener..";
                log.fatal(msg);
            }
        }
    }

    public void init(ModuleContext configuration) throws RunningException {
        this.moduleContext = configuration;

        if (queuesPerThread <= 0) {
            throw new IllegalArgumentException("number of threads should be greater than 0");
        }

        threads = queueListeners.size() / queuesPerThread;

        executor = Executors.newFixedThreadPool(threads, new DefaultThreadFactory("queue-listener", "queue"));

        semaphores = new Semaphore[threads];
        threadsQueueListeners = new ArrayList<HashMap<String, QueueAndListener>>();
        for (int i = 0; i < threads; i++) {
            // we will allow integer max number of messages in the queues, hopefully this
            // will be sufficient for most of the use cases
            semaphores[i] = new Semaphore(0);
            threadsQueueListeners.add(i, new HashMap<String, QueueAndListener>());
        }


        int count = -1;
        int i = 0;
        for (Map.Entry<String, QueueAndListener> entry : queueListeners.entrySet()) {
            LinkQueue<Message> queue = entry.getValue().getQueue();

            if ((i / threads) == 0) {
                count++;
            }
            queue.setCount(semaphores[count]);

            // add this to the thread's map
            threadsQueueListeners.get(count).put(entry.getKey(), entry.getValue());
            i++;
        }
        state = State.INIT;
    }

    public void destroy() {
        executor.shutdown();
    }

    private class QueueAndListener {
        private final LinkQueue<Message> queue;

        private final MessageListener messageListener;

        private QueueAndListener(LinkQueue<Message> queue, MessageListener messageListener) {
            this.queue = queue;
            this.messageListener = messageListener;
        }

        public LinkQueue<Message> getQueue() {
            return queue;
        }

        public MessageListener getMessageListener() {
            return messageListener;
        }
    }

    private class QueueWorker implements Runnable {
        int thread;

        private QueueWorker(int thread) {
            this.thread = thread;
        }

        public void run() {
            try {
                loop(thread);
            } catch (Throwable e) {
                log.fatal("Unhandled exception....", e);
            }
        }
    }
}

package connect.executor;

import connect.ConnectRunningException;
import connect.QualifiedName;
import connect.State;
import connect.lang.ModuleContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

/**
 * If an entity wishes to do processing on messages using different threads, it
 * should do it using a MessageExecutor.
 */
public class SimpleMessageExecutor implements MessageExecutor {
    private Log log = LogFactory.getLog(SimpleMessageExecutor.class);
    /** Name of the executor */
    private final String name;
    /* Usual thread number */
    private int corePoolSize;
    /* Maximum number of threads */
    private int maximumPoolSize;
    /** Keep alive time */
    private long keepAliveTime;
    /** The queue used for the tasks */
    private BlockingQueue<Runnable> workQueue;
    /** Name of the threads */
    private String threadName;
    /** Thread group name */
    private String groupName;
    /** The actual thread pool */
    private ThreadPoolExecutor executor;
    /** The reject handler */
    private RejectedExecutionHandler rejectedExecutionHandler = null;
    /** shutdown wait */
    private int shutDownWait = 1000;

    private ModuleContext moduleContext;

    private State state = State.CREATED;

    public SimpleMessageExecutor(String name) {
        this(name, 10, 10, 5, new LinkedBlockingDeque<Runnable>(), "worker", "worker");
    }

    public SimpleMessageExecutor(String name, int corePoolSize, int maximumPoolSize,
                                 long keepAliveTime, BlockingQueue<Runnable> workQueue,
                                 String threadName, String groupName) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.workQueue = workQueue;
        this.threadName = threadName;
        this.groupName = groupName;
        this.name = name;
    }

    public void execute(final Runnable r) {
        executor.execute(new Runnable() {
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    t.printStackTrace();
                    log.error("Uncaught exception", t);
                }
            }
        });
    }

    public void setRejectedExecutionHandler(
            RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public void setShutDownWait(int shutDownWait) {
        this.shutDownWait = shutDownWait;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void init(ModuleContext configuration) {
        this.moduleContext = configuration;

        if (rejectedExecutionHandler == null) {
            executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                    keepAliveTime, TimeUnit.MILLISECONDS,
                    workQueue, new DefaultThreadFactory(groupName, threadName));
        } else {
            executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                    keepAliveTime, TimeUnit.MILLISECONDS,
                    workQueue, new DefaultThreadFactory(groupName, threadName));
        }
        state = State.INIT;
    }

    public void destroy() {
        executor.shutdown();
        try {
            executor.awaitTermination(shutDownWait, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new ConnectRunningException("Interrupted while waiting " +
                    "for the executor to shutdown");
        }
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
}

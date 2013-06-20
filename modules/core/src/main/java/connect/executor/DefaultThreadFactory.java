package connect.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {
    final ThreadGroup group;
    final AtomicInteger count;
    final String namePrefix;

    public DefaultThreadFactory(String groupName, String namePrefix) {
        super();
        this.count = new AtomicInteger(1);
        this.group = new ThreadGroup(groupName);
        this.namePrefix = namePrefix;
    }

    public Thread newThread(Runnable r) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(namePrefix);
        buffer.append('-');
        buffer.append(count.getAndIncrement());
        Thread t = new Thread(group, r, buffer.toString());
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY);

        return t;
    }
}

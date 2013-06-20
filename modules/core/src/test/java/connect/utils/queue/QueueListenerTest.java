package connect.utils.queue;

import connect.error.ConnectError;
import connect.error.ErrorTypes;
import connect.lang.RunningException;
import connect.message.ErrorMessage;
import connect.message.Message;
import connect.message.MessageListener;
import connect.queue.InMemoryLinkQueue;
import connect.queue.QueueListener;
import junit.framework.TestCase;

public class QueueListenerTest extends TestCase {
    public void testPoll() throws RunningException {
        InMemoryLinkQueue linkQueue = new InMemoryLinkQueue("testQueue", 100);

        ErrorMessage message = new ErrorMessage(new ConnectError(ErrorTypes.EXCEPTION));

        QueueListener listener = new QueueListener("testListener", linkQueue, new MessageListener() {
            public void onMessage(Message m) {
                System.out.println(m);
            }
        });
        listener.start();

        linkQueue.offer(message);
    }
}

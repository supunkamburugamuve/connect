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

public class InMemoryLinkQueueTest extends TestCase {
    public void testPoll() throws RunningException {
        InMemoryLinkQueue<ErrorMessage> linkQueue = new InMemoryLinkQueue<ErrorMessage>("testQueue", 100);

        ErrorMessage message = new ErrorMessage(new ConnectError(ErrorTypes.EXCEPTION));

        Message out = null;

        linkQueue.offer(message);

        try {
            out = linkQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(out);
    }
}

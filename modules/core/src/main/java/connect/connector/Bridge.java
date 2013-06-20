package connect.connector;

import connect.*;
import connect.lang.ManagedEntity;
import connect.lang.Reference;

/**
 * This is a bridge between two Message Processing entities.
 */
public interface Bridge extends Manageable, ManagedEntity, Namable, Connectable {
    /**
     * Set the input queue
     *
     * @param inputQueueName the name of the queue
     */
    void setInputQueue(Reference inputQueueName);

    /**
     * THe next queue
     *
     * @param nextQueueName name of the next queue
     */
    void setNextQueue(Reference nextQueueName);
}

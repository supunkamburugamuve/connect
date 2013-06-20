package connect.endpoint;

import connect.Namable;
import connect.lang.ManagedEntity;
import connect.lang.Reference;

/**
 * Endpoints define the boundaries of the mediation engine. Every message is injected in to the
 * message mediation using an endpoint and messages are sent out from the mediation engine
 * using endpoints.
 *
 * Endpoints are not doing any executions on the message. They are there to define the boundary
 * conditions of the messages entering in to the system. For example an SOAPEndpoint is a
 * place where you define the gateway properties to the SOAP messages entering in to the
 * system. An entity sending a message in to the system must adhere to the contract offered by
 * the endpoint configuration.
 */
public interface Endpoint extends ManagedEntity, Namable {
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

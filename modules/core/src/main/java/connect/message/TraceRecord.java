package connect.message;

import connect.QualifiedName;

import java.util.ArrayList;
import java.util.List;

public class TraceRecord {
    private final String messageId;

    /** Full name of the entities that put the record */
    private List<QualifiedName> entities = new ArrayList<QualifiedName>();

    /** The type of the entities */
    private List<String> types = new ArrayList<String>();

    public TraceRecord(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void addEntity(String type, QualifiedName name) {
        entities.add(name);
        types.add(type);
    }

    public List<QualifiedName> getPath() {
        return entities;
    }
}

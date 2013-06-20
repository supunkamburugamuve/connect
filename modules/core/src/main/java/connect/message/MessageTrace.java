package connect.message;

import connect.QualifiedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps a single message trace. This class is only accessed by one thread at a time.
 */
public class MessageTrace {
    private final String originatingId;

    private List<TraceRecord> traces = new ArrayList<TraceRecord>();

    private TraceRecord currentRecord = null;

    public MessageTrace(String originatingId) {
        this.originatingId = originatingId;
    }

    public void addRecord(TraceRecord record) {
        traces.add(record);
        currentRecord = record;
    }

    public void addEntity(String type, QualifiedName qualifiedName) {
        currentRecord.addEntity(type, qualifiedName);
    }

    public List<TraceRecord> getTraces() {
        return traces;
    }

    public String getOriginatingId() {
        return originatingId;
    }
}

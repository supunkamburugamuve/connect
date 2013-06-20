package connect.message;

import connect.lang.RunningException;

public interface MessageCloner {
    Message clone(Message m) throws RunningException;
}

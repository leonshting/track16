package track.messenger.messages;

/**
 * Created by leonshting on 22.12.16.
 */
public class QMessage extends Message {
    public QMessage() {
        setType(Type.MSG_QUIT);
    }
}

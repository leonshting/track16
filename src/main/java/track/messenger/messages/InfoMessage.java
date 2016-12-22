package track.messenger.messages;

import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * Created by leonshting on 21.12.16.
 */
public class InfoMessage extends Message {
    public InfoMessage() {
        setRaw("Info message");
        setType(Type.MSG_INFO);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;
}

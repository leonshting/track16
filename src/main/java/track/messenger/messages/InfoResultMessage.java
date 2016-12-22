package track.messenger.messages;

/**
 * Created by leonshting on 21.12.16.
 */
public class InfoResultMessage extends InfoMessage {
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    String info;

    public InfoResultMessage() {
        setRaw("IR Message");
        setType(Type.MSG_INFO_RESULT);
    }

    @Override
    public String toString() {
        return "InfoResultMessage{" +
                "info='" + info + '\'' +
                '}';
    }
}

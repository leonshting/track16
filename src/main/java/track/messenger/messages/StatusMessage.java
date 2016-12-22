package track.messenger.messages;

/**
 * Created by leonshting on 21.12.16.
 */
public class StatusMessage extends Message {

    public StatusMessage(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    private String statusMsg;

    public String getStatusMsg() {
        return statusMsg;
    }

    public StatusMessage() {
        setRaw("Status Message");
        setType(Type.MSG_STATUS);
    }

    @Override
    public String toString() {
        return "StatusMessage{" +

                "statusMsg='" + statusMsg + '\'' +
                '}';
    }
}

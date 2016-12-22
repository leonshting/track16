package track.messenger.messagehandling;

import track.messenger.messages.InfoMessage;
import track.messenger.messages.InfoResultMessage;
import track.messenger.messages.Message;
import track.messenger.messages.StatusMessage;
import track.messenger.net.Session;

import static track.messenger.messages.Type.MSG_TEXT;

/**
 * Created by leonshting on 22.12.16.
 */
public class MessageHandler {

    private Session currentSession;

    public MessageHandler(Session session) {
        currentSession = session;
    }

    public Message executeOrRespond(Message msg) {
        MessageCommand msgCommand = null;
        try {
            switch (msg.getType()) {
                case MSG_TEXT:
                    msgCommand = (MessageCommand) (new TextMessageCommand());
                    break;
                case MSG_LOGIN:
                    msgCommand = (MessageCommand) (new LoginMessageCommand());
                    break;
                case MSG_INFO:
                    msgCommand = (MessageCommand) (new InfoMessageCommand());
                    return msgCommand.execute_with_response(currentSession, msg);
                case MSG_QUIT:
                    msgCommand = (MessageCommand) (new QMessageCommand());
                    break;
                default:
                    throw new CommandException("Wrong message type");
            }
            msgCommand.execute(currentSession, msg);
            return null;
        } catch (CommandException e) {
            e.printStackTrace();
            return new StatusMessage(e.toString());
        }
    }
}

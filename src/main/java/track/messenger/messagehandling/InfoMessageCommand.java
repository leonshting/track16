package track.messenger.messagehandling;

import track.messenger.messages.InfoMessage;
import track.messenger.messages.InfoResultMessage;
import track.messenger.messages.Message;
import track.messenger.messages.StatusMessage;
import track.messenger.net.Session;

import java.sql.SQLException;

/**
 * Created by leonshting on 22.12.16.
 */
public class InfoMessageCommand implements MessageCommand {
    @Override
    public Message execute_with_response(Session session, Message msg) throws CommandException {
        try {
            InfoResultMessage infoResultMessage = new InfoResultMessage();
            InfoMessage infoMessage = (InfoMessage) msg;
            infoResultMessage.setUserId(infoMessage.getUserId());
            infoResultMessage
                    .setInfo(session.getChatStore().getChatsByUser(infoMessage.getUserId()).toString());
            return infoResultMessage;
        } catch (SQLException e) {
            e.printStackTrace();
            return new StatusMessage("bad request");
        }
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        throw new CommandException("Wrong method called");
    }
}

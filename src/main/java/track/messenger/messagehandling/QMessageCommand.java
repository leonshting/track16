package track.messenger.messagehandling;

import track.messenger.messages.Message;
import track.messenger.net.Session;

/**
 * Created by leonshting on 22.12.16.
 */
public class QMessageCommand implements MessageCommand {
    @Override
    public void execute(Session session, Message message) throws CommandException {
        session.close();
    }

    @Override
    public Message execute_with_response(Session session, Message message) throws CommandException {
        return null;
    }
}

package track.messenger.messagehandling;

import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;
import track.messenger.net.Session;

import java.sql.SQLException;

/**
 * Created by leonshting on 22.12.16.
 */

public class TextMessageCommand implements MessageCommand {
    @Override
    public Message execute_with_response(Session session, Message message) throws CommandException {
        return null;
        //TODO: implement this
    }

    @Override
    public void execute(Session session, Message msg) throws CommandException {
        if (session.getUser() != null) {
            try {
                TextMessage textMessage = (TextMessage) msg;
                if (session.getChatStore().chatExists(textMessage.getChatId()) &&
                        session.getChatList().contains(textMessage.getChatId())) {
                    Long messageId = session.getMessageStore().addMessage(textMessage.getChatId(), textMessage);
                    for (Session s : session.getSessions().values()) {
                        synchronized (s.chatsUpLock) {
                            if (session != s) {
                                s.chatsUp.set(true);
                                s.newMessages.put(messageId);
                                s.chatsUpLock.notify();
                            }
                        }
                    }
                } else {
                    throw new CommandException("No such chat");
                }
            } catch (SQLException e) {
                throw new CommandException("SQL error");
            } catch (InterruptedException e) {
                throw new CommandException("Multithreaded stuff");
            }
        }
    }
}

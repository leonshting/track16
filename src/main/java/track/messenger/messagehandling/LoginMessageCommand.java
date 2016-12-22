package track.messenger.messagehandling;

import track.messenger.messages.LoginMessage;
import track.messenger.messages.Message;
import track.messenger.messages.StatusMessage;
import track.messenger.net.Session;

import java.sql.SQLException;

/**
 * Created by leonshting on 22.12.16.
 */
public class LoginMessageCommand implements MessageCommand {
    @Override
    public Message execute_with_response(Session session, Message msg) throws CommandException {
        LoginMessage loginMessage = (LoginMessage) msg;
        session.setUser(session.getUserStore().getUser(loginMessage.getUserName(), loginMessage.getPassWord()));
        if (session.getUser() == null) {
            return new StatusMessage("Unsuccesfull login");
        } else {
            try {
                session.afterLogin();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new StatusMessage("Logged In Succesfully");
    }

    @Override
    public void execute(Session session, Message msg) throws CommandException {
        LoginMessage loginMessage = (LoginMessage) msg;
        session.setUser(session.getUserStore().getUser(loginMessage.getUserName(), loginMessage.getPassWord()));
        if (session.getUser() == null) {
            throw new CommandException("Unsuccesful login");
        } else {
            try {
                session.afterLogin();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

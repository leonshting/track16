package track.messenger.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import track.messenger.User;
import track.messenger.messages.LoginMessage;
import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;
import track.messenger.store.MessageStore;
import track.messenger.store.UserStore;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Сессия связывает бизнес-логику и сетевую часть.
 * Бизнес логика представлена объектом юзера - владельца сессии.
 * Сетевая часть привязывает нас к определнному соединению по сети (от клиента)
 */
public class Session {

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */

    private Socket socket;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;
    private User user;
    private boolean isClosed;
    private Protocol protocol;
    private UserStore userStore;
    private MessageStore messageStore;

    public UserStore getUserStore() {
        return userStore;
    }


    public MessageStore getMessageStore() {
        return messageStore;
    }


    public boolean isClosed() {
        return isClosed;
    }


    public Session() {
        user = null;
        socket = null;
        isClosed = true;
        protocol = new StringProtocol();
    }

    public Session(Socket socket, InputStream in, OutputStream out, UserStore us, MessageStore ms) {
        this.socket = socket;
        this.userStore = us;
        this.messageStore = ms;
        this.in = in;
        this.out = out;
        isClosed = false;
        protocol = new StringProtocol();
    }

    public void sessionRun() {
        try {
            while (!isClosed()) {
                byte[] buf = new byte[32 * 1024];
                int readBytes = 0;
                readBytes = in.read(buf);
                onMessage(protocol.decode(buf));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
            /* TODO: exception handling */
        }

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }


    public void send(Message msg) throws ProtocolException, IOException {
        // TODO: Отправить клиенту сообщение
    }

    public void onMessage(Message msg) {
        switch (msg.getType()) {
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                user = userStore.getUser(loginMessage.getUserName(), loginMessage.getPassWord());
                break;

            case MSG_TEXT:
                TextMessage textMessage = (TextMessage) msg;
                System.out.print(textMessage.getText());
                break;

            default:
        }
        // TODO: Пришло некое сообщение от клиента, его нужно обработать
    }

    public void close() {
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isClosed = true;
        }
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
    }
}
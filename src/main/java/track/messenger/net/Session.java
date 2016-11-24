package track.messenger.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.User;
import track.messenger.messages.LoginMessage;
import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;
import track.messenger.store.ChatStore;
import track.messenger.store.MessageStore;
import track.messenger.store.UserStore;
import track.messenger.teacher.client.MessengerClient;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Сессия связывает бизнес-логику и сетевую часть.
 * Бизнес логика представлена объектом юзера - владельца сессии.
 * Сетевая часть привязывает нас к определнному соединению по сети (от клиента)
 */
public class Session {

    static Logger log = LoggerFactory.getLogger(MessengerClient.class);

    private boolean isClosed;
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

    private Protocol protocol;
    private UserStore userStore;
    private MessageStore messageStore;
    private ChatStore chatStore;
    private Map<User, Session> sessions;
    public Map<Long, Boolean> chatsUp;
    public Map<Long, Message> newMessages;
    //probably queue is better
    public final Object chatsUpLock;

    private List<Long> chatList;


    public UserStore getUserStore() {
        return userStore;
    }


    public MessageStore getMessageStore() {
        return messageStore;
    }


    public boolean isClosed() {
        return isClosed;
    }

    public void afterLogin() {
        sessions.put(user, this);
        chatList = chatStore.getChatsByUser(user.getId());
    }

    public Session() {
        user = null;
        socket = null;
        isClosed = true;

        protocol = new StringProtocol();
        chatsUpLock = new Object();
    }

    public Session(Socket socket, InputStream in, OutputStream out, UserStore us,
                   MessageStore ms, ChatStore cs, Map<User, Session> sessions) {
        this.socket = socket;
        this.userStore = us;
        this.messageStore = ms;
        this.chatStore = cs;
        this.in = in;
        this.out = out;
        this.sessions = sessions;

        chatsUpLock = new Object();
        chatsUp = new ConcurrentHashMap<>();
        newMessages = new ConcurrentHashMap<>();
        isClosed = false;
        protocol = new StringProtocol();
    }

    class MapNotifier implements Runnable {

        public void run() {
            try {
                while (!isClosed()) {
                    synchronized (chatsUpLock) {
                        chatsUpLock.wait();
                        for (Map.Entry<Long, Boolean> entry : chatsUp.entrySet()) {
                            if (entry.getValue()) {
                                entry.setValue(Boolean.FALSE);
                                send(newMessages.get(entry.getKey()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sessionRun() {
        try {
            Thread chatListener = new Thread(new MapNotifier());
            chatListener.start();
            while (!isClosed()) {
                byte[] buf = new byte[32 * 1024];
                int readBytes = 0;
                readBytes = in.read(buf);
                Message message = protocol.decode(buf);
                message.setSenderId((user != null) ? user.getId() : 0);
                onMessage(message);
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


    public synchronized void send(Message msg) throws ProtocolException, IOException {
        log.info(msg.toString());
        out.write(protocol.encode(msg));
        out.flush();
    }

    public synchronized void onMessage(Message msg) {
        switch (msg.getType()) {
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                user = userStore.getUser(loginMessage.getUserName(), loginMessage.getPassWord());
                if (user != null) {
                    afterLogin();
                }
                break;

            case MSG_TEXT:
                if (user != null) {
                    TextMessage textMessage = (TextMessage) msg;
                    try {
                        if (chatStore.chatExists(textMessage.getChatId()) &&
                                chatList.contains(textMessage.getChatId())) {
                            messageStore.addMessage(textMessage.getChatId(), textMessage);
                            for (Session s : sessions.values()) {
                                synchronized (s.chatsUpLock) {
                                    if (this != s) {
                                        s.chatsUp.put(textMessage.getChatId(), Boolean.TRUE);
                                        s.newMessages.put(textMessage.getChatId(), textMessage);
                                        s.chatsUpLock.notify();
                                    }
                                }
                            }
                        } else {
                            TextMessage tmsg = new TextMessage();
                            tmsg.setText("no such chat");
                            //as exception also
                            send(tmsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                //TODO: ^^^
                //      ||| rewrite in terms of exceptions
                // TODO: Handle unsuccesful login
                break;

            default:
        }
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
    }
}
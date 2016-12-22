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
import java.util.concurrent.atomic.AtomicBoolean;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.User;
import track.messenger.messagehandling.MessageHandler;
import track.messenger.messages.*;
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
//@Data lombok getters and setters
public class Session {

    static Logger log = LoggerFactory.getLogger(MessengerClient.class);

    private boolean isClosed;

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */

    private Socket socket;
    private MessageHandler handler;

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
    public BlockingQueue newMessages;
    public AtomicBoolean chatsUp;
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

    public Map<User, Session> getSessions() {
        return sessions;
    }

    public List<Long> getChatList() {

        return chatList;
    }

    public ChatStore getChatStore() {

        return chatStore;
    }

    public void afterLogin() throws SQLException {
        sessions.put(user, this);
        chatList = chatStore.getChatsByUser(user.getId());
    }

    public Session() {
        user = null;
        socket = null;
        isClosed = true;

        chatsUp = new AtomicBoolean(false);
        protocol = new StringProtocol();
        chatsUpLock = new Object();
        newMessages = new ArrayBlockingQueue<Long>(20);

        handler = new MessageHandler(this);
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

        chatsUp = new AtomicBoolean(false);
        chatsUpLock = new Object();
        newMessages = new ArrayBlockingQueue<Long>(20);
        isClosed = false;
        protocol = new StringProtocol();

        handler = new MessageHandler(this);
    }

    class MapNotifier implements Runnable {

        public void run() {
            try {
                while (!isClosed()) {
                    synchronized (chatsUpLock) {
                        chatsUpLock.wait();
                        if (chatsUp.get()) {
                            chatsUp.set(false);
                            while (!newMessages.isEmpty()) {
                                send(messageStore.getMessageById((Long) newMessages.take()));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(Message msg) throws ProtocolException, IOException {
        log.info(msg.toString());
        out.write(protocol.encode(msg));
        out.flush();
    }


    public synchronized void onMessage(Message msg) throws SQLException {

        Message response = handler.executeOrRespond(msg);
        try {
            if (response != null) {
                send(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            log.info("Session closed\n");
        }
    }
}
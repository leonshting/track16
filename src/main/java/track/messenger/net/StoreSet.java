package track.messenger.net;

import org.sqlite.SQLiteDataSource;
import track.messenger.store.*;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

/**
 * Created by leonshting on 21.12.16.
 */
public class StoreSet {

    private static StoreSet instance;
    private static String url;
    private DataSource dsInterface;
    private ChatStore chatStore;
    private MessageStore messageStore;
    private UserStore userStore;

    public static void setUrl(String dburl) {
        url = dburl;
    }

    public ChatStore getChatStore() {
        return chatStore;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public UserStore getUserStore() {
        return userStore;
    }

    private StoreSet(String dbURL) {
        url = dbURL;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        dsInterface = dataSource;
        userStore = new SqLiteUserStore(dsInterface);
        messageStore = new SqLiteMessageStore(dsInterface);
        chatStore = new SqLiteChatStore(dsInterface);
    }

    public static synchronized StoreSet getInstance() {
        if (instance == null) {
            instance = new StoreSet(url);
        }
        return instance;
    }
}

package track.messenger.store;

import track.messenger.net.Protocol;
import track.messenger.net.StringProtocol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonshting on 24.11.16.
 */
public class SqLiteChatStore implements ChatStore {

    private final String url;
    static final Protocol protocol = (Protocol) new StringProtocol();

    public SqLiteChatStore(String url) {
        this.url = url;
    }

    @Override
    public synchronized List<Long> getChatsByUser(Long user) {
        try (Connection connection = DriverManager.getConnection(url)) {
            ArrayList<Long> chatList = new ArrayList<>();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM userchat WHERE user = ?");
            stmt.setLong(1, user);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                chatList.add(rs.getLong(2));
            }
            return chatList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized List<Long> getUsersByChat(Long chat) {
        try (Connection connection = DriverManager.getConnection(url)) {
            ArrayList<Long> userList = new ArrayList<>();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM userchat WHERE chat = ?");
            stmt.setLong(1, chat);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userList.add(rs.getLong(1));
            }
            return userList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean chatExists(Long chat) {
        return (getUsersByChat(chat) != null);
        //TODO: rewrite this
    }
}

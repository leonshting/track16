package track.messenger.store;

import track.messenger.net.Protocol;
import track.messenger.net.StringProtocol;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonshting on 24.11.16.
 */

public class SqLiteChatStore extends AbstractStore implements ChatStore {

    public SqLiteChatStore(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public synchronized List<Long> getChatsByUser(Long user) throws SQLException {

        Connection connection = connFactory.getConnection();
        ArrayList<Long> chatList = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM userchat WHERE user = ?");
        stmt.setLong(1, user);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            chatList.add(rs.getLong(2));
        }
        return chatList;
    }

    @Override
    public synchronized List<Long> getUsersByChat(Long chat) throws SQLException {

        Connection connection = connFactory.getConnection();
        ArrayList<Long> userList = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM userchat WHERE chat = ?");
        stmt.setLong(1, chat);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            userList.add(rs.getLong(1));
        }
        return userList;

    }

    @Override
    public boolean chatExists(Long chat) throws SQLException {
        return (getUsersByChat(chat) != null);
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {
        try (Connection connection = connFactory.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO userchat (user, chat)" + " VALUES (?,?)");
            stmt.setLong(1, userId);
            stmt.setLong(2, chatId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

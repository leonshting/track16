package track.messenger.store;

import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;
import track.messenger.net.Protocol;
import track.messenger.net.StringProtocol;

import java.sql.*;
import java.util.List;

/**
 * Created by leonshting on 22.11.16.
 */

public class SqLiteMessageStore implements MessageStore {

    private final String url;
    static final Protocol protocol = (Protocol) new StringProtocol();

    public SqLiteMessageStore(String url) {
        this.url = url;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        return null;
    }

    @Override
    public Message getMessageById(Long messageId) {
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM messages WHERE MESSAGE_ID = ?");
            stmt.setLong(1, messageId);
            ResultSet rs = stmt.executeQuery();
            return protocol.decode(rs.getString(4).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addMessage(Long chatId, Message message) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO messages (AUTHOR_ID, DATE_WRITTEN, CONTENT)" +
                            " VALUES (?, ?, ?)");
            stmt.setLong(1, message.getSenderId());
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setString(3, message.getRaw());
            stmt.executeUpdate();
        }
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {

    }
}

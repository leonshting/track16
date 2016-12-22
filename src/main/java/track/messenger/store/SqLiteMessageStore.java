package track.messenger.store;

import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;
import track.messenger.net.Protocol;
import track.messenger.net.ProtocolException;
import track.messenger.net.StringProtocol;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leonshting on 22.11.16.
 */

public class SqLiteMessageStore extends AbstractStore implements MessageStore {

    static final Protocol protocol = (Protocol) new StringProtocol();

    public SqLiteMessageStore(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        try (Connection connection = connFactory.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM messages WHERE MESSAGE_ID = ?");
            stmt.setLong(1, chatId);
            ResultSet rs = stmt.executeQuery();
            Long[] resArray = (Long[]) rs.getArray(2).getArray();
            return Arrays.asList(resArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Message getMessageById(Long messageId) {
        try (Connection connection = connFactory.getConnection()) {
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
    public Long addMessage(Long chatId, Message message) {
        try (Connection connection = connFactory.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO messages (AUTHOR_ID, DATE_WRITTEN, CONTENT, chat_id) VALUES (?, ?, ?, ?)");
            stmt.setLong(1, message.getSenderId());
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setString(3, message.getRaw());
            stmt.setLong(4, chatId);
            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            if (stmt.getGeneratedKeys().next()) {
                return rs.getLong(1);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



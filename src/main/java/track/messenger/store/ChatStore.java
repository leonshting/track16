package track.messenger.store;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by leonshting on 24.11.16.
 */
public interface ChatStore {

    List<Long> getChatsByUser(Long user) throws SQLException;

    List<Long> getUsersByChat(Long chat) throws SQLException;

    boolean chatExists(Long chat) throws SQLException;

    void addUserToChat(Long userId, Long chatId);
}

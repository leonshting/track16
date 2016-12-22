package track.messenger.store;

import track.messenger.messages.Message;
import track.messenger.net.ProtocolException;

import java.sql.SQLException;
import java.util.List;

public interface MessageStore {


    /**
     * Список сообщений из чата
     */
    List<Long> getMessagesFromChat(Long chatId);

    /**
     * Получить информацию о сообщении
     */
    Message getMessageById(Long messageId) throws SQLException, ProtocolException;

    /**
     * Добавить сообщение в чат
     */
    Long addMessage(Long chatId, Message message) throws SQLException;


}

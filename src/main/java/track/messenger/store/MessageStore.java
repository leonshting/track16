package track.messenger.store;

import track.messenger.messages.Message;

import java.sql.SQLException;
import java.util.List;

public interface MessageStore {
    /**
     * получаем список ид пользователей заданного чата
     */
    List<Long> getChatsByUserId(Long userId);

    /**
     * получить информацию о чате
     */
    //Chat getChatById(Long chatId);

    /**
     * Список сообщений из чата
     */
    List<Long> getMessagesFromChat(Long chatId);

    /**
     * Получить информацию о сообщении
     */
    Message getMessageById(Long messageId);

    /**
     * Добавить сообщение в чат
     */
    void addMessage(Long chatId, Message message) throws SQLException;

    /**
     * Добавить пользователя к чату
     */
    void addUserToChat(Long userId, Long chatId);

}

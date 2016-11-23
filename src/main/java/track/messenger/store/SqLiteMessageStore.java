package track.messenger.store;

import track.messenger.messages.Message;

import java.util.List;

/**
 * Created by leonshting on 22.11.16.
 */
public class SqLiteMessageStore implements MessageStore {

    private final String url;

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
        return null;
    }

    @Override
    public void addMessage(Long chatId, Message message) {

    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {

    }
}

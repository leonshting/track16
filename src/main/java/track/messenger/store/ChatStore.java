package track.messenger.store;

import java.util.List;

/**
 * Created by leonshting on 24.11.16.
 */
public interface ChatStore {

    List<Long> getChatsByUser(Long user);

    List<Long> getUsersByChat(Long chat);

    boolean chatExists(Long chat);
}

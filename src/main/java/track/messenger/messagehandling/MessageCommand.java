package track.messenger.messagehandling;

import track.messenger.messages.Message;
import track.messenger.net.Session;

/**
 * Created by leonshting on 22.12.16.
 */
public interface MessageCommand {

    /**
     * Реализация паттерна Команда. Метод execute() вызывает соответствующую реализацию,
     * для запуска команды нужна сессия, чтобы можно было сгенерить ответ клиенту и провести валидацию
     * сессии.
     *
     * @param session - текущая сессия
     * @param message - сообщение для обработки
     * @throws CommandException - все исключения перебрасываются как CommandException
     */
    void execute(Session session, Message message) throws CommandException;

    Message execute_with_response(Session session, Message message) throws CommandException;
}

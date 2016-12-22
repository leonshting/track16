package track.messenger.messagehandling;

/**
 * Created by leonshting on 22.12.16.
 */

public class CommandException extends Exception {
    public CommandException(String msg) {
        super(msg);
    }

    public CommandException(Throwable ex) {
        super(ex);
    }
}
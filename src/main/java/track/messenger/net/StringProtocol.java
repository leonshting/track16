package track.messenger.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import track.messenger.messages.LoginMessage;
import track.messenger.messages.Message;
import track.messenger.messages.TextMessage;
import track.messenger.messages.Type;

/**
 * Простейший протокол передачи данных
 */
public class StringProtocol implements Protocol {

    static private Logger log = LoggerFactory.getLogger(StringProtocol.class);

    public static final String DELIMITER = ";";

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        String str = new String(bytes);
        log.info("decoded: {}", str);
        String[] tokens = str.split(DELIMITER);
        Type type = Type.valueOf(tokens[0]);
        switch (type) {
            case MSG_TEXT:
                TextMessage textMsg = new TextMessage();
                textMsg.setSenderId(parseLong(tokens[1]));
                textMsg.setText(tokens[2]);
                textMsg.setType(type);
                return textMsg;
            case MSG_LOGIN:
                LoginMessage loginMsg = new LoginMessage();
                loginMsg.setUserName(tokens[1]);
                loginMsg.setPassWord(tokens[2]);
                loginMsg.setType(type);
                return loginMsg;

            default:
                throw new ProtocolException("Invalid type: " + type);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        StringBuilder builder = new StringBuilder();
        Type type = msg.getType();
        builder.append(type).append(DELIMITER);
        switch (type) {
            case MSG_TEXT:
                TextMessage sendMessage = (TextMessage) msg;
                builder.append(String.valueOf(sendMessage.getSenderId())).append(DELIMITER);
                builder.append(sendMessage.getText()).append(DELIMITER);
                break;
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(loginMessage.getUserName()).append(DELIMITER);
                builder.append(loginMessage.getPassWord()).append(DELIMITER);
                break;

            default:
                throw new ProtocolException("Invalid type: " + type);


        }
        log.info("encoded: {}", builder.toString());
        return builder.toString().getBytes();
    }

    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            // who cares
        }
        return null;
    }
}
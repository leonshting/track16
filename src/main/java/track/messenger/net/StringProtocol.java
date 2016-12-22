package track.messenger.net;

import com.sun.xml.internal.bind.v2.TODO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import track.messenger.messages.*;

/**
 * Простейший протокол передачи данных
 */
public class StringProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(StringProtocol.class);

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
                textMsg.setChatId(parseLong(tokens[1]));
                textMsg.setText(tokens[2]);
                textMsg.setType(type);
                textMsg.setRaw(str);
                return textMsg;
            case MSG_LOGIN:
                LoginMessage loginMsg = new LoginMessage();
                loginMsg.setUserName(tokens[1]);
                loginMsg.setPassWord(tokens[2]);
                loginMsg.setType(type);
                return loginMsg;
            case MSG_INFO:
                InfoMessage infoMessage = new InfoMessage();
                if (tokens.length >= 2) {
                    infoMessage.setUserId(parseLong(tokens[1]));
                } else {
                    infoMessage.setUserId(-1L);
                }
                return infoMessage;
            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = new InfoResultMessage();
                infoResultMessage.setUserId(parseLong(tokens[2]));
                infoResultMessage.setInfo(tokens[1]);
                return infoResultMessage;
            case MSG_STATUS:
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setStatusMsg(tokens[1]);
                return statusMessage;
            case MSG_QUIT:
                QMessage quitMessage = new QMessage();
                return quitMessage;
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
                builder.append(String.valueOf(sendMessage.getChatId())).append(DELIMITER);
                builder.append(sendMessage.getText()).append(DELIMITER);
                break;
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(loginMessage.getUserName()).append(DELIMITER);
                builder.append(loginMessage.getPassWord()).append(DELIMITER);
                break;
            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage) msg;
                builder.append(statusMessage.getStatusMsg()).append(DELIMITER);
                break;
            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = (InfoResultMessage) msg;
                builder.append(infoResultMessage.getInfo()).append(DELIMITER);
                builder.append(infoResultMessage.getUserId()).append(DELIMITER);
                break;
            case MSG_INFO:
                InfoMessage infoMessage = (InfoMessage) msg;
                builder.append(infoMessage.getUserId()).append(DELIMITER);
                break;
            case MSG_QUIT:
                break;
            default:
                throw new ProtocolException("Invalid type: " + type);


        }
        log.info("encoded: {}", builder.toString());
        msg.setRaw(builder.toString());
        return builder.toString().getBytes();
    }

    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
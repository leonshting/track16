package track.messenger.messages;

/**
 * Created by leonshting on 22.11.16.
 */
public class LoginMessage extends Message {

    private String userName;
    private String passWord;

    public LoginMessage() {
        setRaw("login message");
        setType(Type.MSG_LOGIN);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "login='" + userName + '\'' +
                "pass='" + passWord + '\'' +
                '}';
    }
}

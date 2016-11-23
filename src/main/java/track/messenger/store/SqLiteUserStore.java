package track.messenger.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.User;
import track.messenger.teacher.client.MessengerClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by leonshting on 22.11.16.
 */
public class SqLiteUserStore implements UserStore {
    private final String url;
    static Logger log = LoggerFactory.getLogger(MessengerClient.class);

    public SqLiteUserStore(String url) {
        this.url = url;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUser(String login, String pass) {

        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (rs.getString("password_hash").toUpperCase().equals(
                    javax.xml.bind.DatatypeConverter.printHexBinary(md.digest(pass.getBytes())))) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUserName(rs.getString("name"));

                return user;
            } else {
                throw new Exception("wrong password");
            }

        } catch (java.sql.SQLException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to process connection: {}", e);
            e.printStackTrace();
            return null;
            //Probably should stop an execution
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User getUserById(Long id) {
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            stmt.setInt(1, id.intValue());
            ResultSet rs = stmt.executeQuery();
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUserName(rs.getString("name"));
            return user;
        } catch (java.sql.SQLException e) {
            return null;
        }
    }
}

package track.messenger.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import track.messenger.User;
import track.messenger.teacher.client.MessengerClient;

import javax.sql.DataSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by leonshting on 22.11.16.
 */

public class SqLiteUserStore extends AbstractStore implements UserStore {


    public SqLiteUserStore(DataSource dataSource) {
        super(dataSource);
    }


    public User addUser(User user, String pass) {
        try (Connection connection = connFactory.getConnection()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (name, password_hash) VALUES = (?,?)");
            stmt.setString(1, user.getUserName());
            stmt.setString(2, javax.xml.bind.DatatypeConverter.printHexBinary(md.digest(pass.getBytes())));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User updateUser(User user) {
        try (Connection connection = connFactory.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE users SET name = ? WHERE id = ?");
            stmt.setString(1, user.getUserName());
            stmt.setLong(2, user.getId());
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized User getUser(String login, String pass) {
        try (Connection connection = connFactory.getConnection()) {
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
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User getUserById(Long id) {
        try (Connection connection = connFactory.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            stmt.setInt(1, id.intValue());
            ResultSet rs = stmt.executeQuery();
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUserName(rs.getString("name"));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

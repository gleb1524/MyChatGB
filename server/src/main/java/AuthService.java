import java.sql.SQLException;

public interface AuthService {
    public void start();
     String getNickname(String login, String password) throws SQLException;
//     String getNicknameJdbc(String login, String password);

    String rename(String nickname, String login) throws SQLException;

    boolean hasRegistration(String login, String password, String nickname) throws SQLException;

    void creatRegistration(String login, String password, String nickname) throws SQLException;

    //     boolean checkRegistrationJdbc(String login, String password, String nickname);
     public void stop();
    }


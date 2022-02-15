import java.sql.*;
import java.util.logging.Logger;

public class DateBaseAuthService implements AuthService{
    private  Connection connection;
    private  Statement stmt;
    private static final Logger logger = Logger.getLogger(DateBaseAuthService.class.getName());

    public DateBaseAuthService() {
        try {
            connectDb();
//            System.out.println("connect...");
        logger.info("connect");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {

    }
    @Override
    public String getNickname(String login, String password) throws SQLException {
          try(PreparedStatement preparedStatement = connection.prepareStatement
                  ("SELECT nickname FROM clients WHERE login = ? AND password = ?;")) {
              preparedStatement.setString(1,login);
              preparedStatement.setString(2,password);
              ResultSet resultSet = preparedStatement.executeQuery();
             if(resultSet.next()){
                 return resultSet.getString(1);
             }
          }
        return null;
    }
    @Override
    public String rename(String nickname, String login) throws SQLException {
            try(PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT nickname FROM clients WHERE nickname = ?;")){
                preparedStatement.setString(1, nickname);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                   return ServiceMessages.RENAME_NO + nickname;
                }
            }
//            ResultSet rs =stmt.executeQuery("SELECT * FROM clients;");
//            while ((rs.next())){
//                if(rs.getString(2).equals(login)&&rs.getString(3).equals(password)){
//                    PreparedStatement ps = connection.prepareStatement(
//                            "UPDATE clients SET nickname = ? WHERE id = ?;");
//                    ps.setString(1, nickname);
//                    ps.setInt(2,rs.getInt(1));
//                    ps.executeUpdate();
//                    return ServiceMessages.RENAME_OK + " " + nickname;
//                }
//            }
//            rs.close();
//        return ServiceMessages.RENAME_NO + nickname;
        updateNickname(nickname, login);
        return ServiceMessages.RENAME_OK + " " + nickname;
    }

    public void updateNickname(String nickname, String login) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("UPDATE clients SET nickname = ? WHERE login = ?;")){
            preparedStatement.setString(1, nickname);
            preparedStatement.setString(2, login);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean hasRegistration(String login, String password, String nickname) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT login,password,nickname FROM clients WHERE login = ? AND password = ? OR nickname = ?;")){
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickname);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void creatRegistration(String login, String password, String nickname) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("INSERT INTO clients (login, password, nickname) VALUES (?, ?, ?);")){
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickname);
            preparedStatement.executeUpdate();
        }
    }

    public void connectDb() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:clitsDate.db");
        stmt = connection.createStatement();
    }

    @Override
    public void stop() {
        try {
            stmt.close();
        } catch (SQLException e) {
            logger.warning(e.toString());
           // e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.toString());
           // e.printStackTrace();
        }
    }

}

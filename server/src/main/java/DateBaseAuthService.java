import java.sql.*;

public class DateBaseAuthService implements AuthService{
    private  Connection connection;
    private  Statement stmt;

    public DateBaseAuthService() {
        try {
            connectDb();
            System.out.println("connect...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkRegistration(String login, String password, String nickname) {
        return false;
    }

    @Override
    public void start() {

    }
    @Override
    public String getNicknameJdbc(String login, String password) {
        try{
            ResultSet rs =stmt.executeQuery("SELECT * FROM clients;");
            while ((rs.next())){
                if(rs.getString(2).equals(login)&&rs.getString(3).equals(password)){
                    return rs.getString(4);
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public String rename(String login, String password, String nickname) {
        try{
            ResultSet rs =stmt.executeQuery("SELECT * FROM clients;");
            while ((rs.next())){
                if(rs.getString(2).equals(login)&&rs.getString(3).equals(password)){
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE clients SET nickname = ? WHERE id = ?;");
                    ps.setString(1, nickname);
                    ps.setInt(2,rs.getInt(1));
                    ps.executeUpdate();
                    return ServiceMessages.RENAME_OK + " " + nickname;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ServiceMessages.RENAME_NO + nickname;
    }

    @Override
    public boolean checkRegistrationJdbc(String login, String password, String nickname) {
        try {
            ResultSet rs =stmt.executeQuery("SELECT * FROM clients;");
            while (rs.next()){
                if(rs.getString(2).equals(login)||rs.getString(4).equals(nickname)){
                    rs.close();
                    return false;
                }
            }
            rs.close();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO clients (login, password, nickname) VALUES (?, ?, ?);");
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, nickname);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
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
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNickname(String login, String password) {
        return null;
    }
}

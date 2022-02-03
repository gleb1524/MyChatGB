public interface AuthService {
    public void start();
     String getNickname(String login, String password);
     String getNicknameJdbc(String login, String password);
     String rename(String login, String password, String nickname);
     boolean checkRegistration(String login, String password, String nickname);
     boolean checkRegistrationJdbc(String login, String password, String nickname);
     public void stop();
    }


public interface AuthService {
    public void start();
     String getNickname(String login, String password);
     boolean checkRegistration(String login, String password, String nickname);
     public void stop();

    }


import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private  Socket socket;
    private Server server;
    private  DataInputStream in;
    private  DataOutputStream out;
    private boolean authenticated;
    private String login;

    public String getLogin() {
        return login;
    }

    public String getNickname() {
        return nickname;
    }

    public String nickname;
    private List<ClientHandler> clients;

    public ClientHandler(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
        try{
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл аутентификации
                    while (!authenticated) {

                        String str = in.readUTF();
                        if (str.equals(ServiceMessages.END)) {
                            sendMessage("Server down");
                            break;
                        }
                        if(str.startsWith(ServiceMessages.RENAME)){
                            String[] token = str.split(" ", 4);
                            if(token.length<4){
                                continue;
                            }
                            String newNick = server.getAuthService().rename(token[1], token[2], token[3]);
                            if(newNick.startsWith(ServiceMessages.RENAME_OK)){
                            sendMessage(newNick);
                            }else if(newNick.startsWith(ServiceMessages.RENAME_NO)){
                                sendMessage(ServiceMessages.RENAME_NO);
                            }
                        }
                        if(str.startsWith(ServiceMessages.AUTH)){
                            String[] token = str.split(" ",3);
                            if(token.length<3){
                                continue;
                            }
                            String newNick = server.getAuthService().getNicknameJdbc(token[1], token[2]);
                            if (newNick != null){
                                login = token[1];
                                if(!server.checkAuth(login)){
                                authenticated = true;
                                nickname = newNick;
                                sendMessage(ServiceMessages.AUTH_OK + " " + nickname);
                                server.subscribe(this);
                                System.out.println("Client " + nickname +" authenticated");
                                break;
                                }else{
                                    sendMessage("Login online!");
                                }
                            }else{
                                sendMessage("Неверный логин/пароль");
                            }
                        }if(str.startsWith(ServiceMessages.REG)){
                            String[] token = str.split(" ", 4);
                            if(token.length<4){
                                continue;
                            }
                            if(server.getAuthService().
                                    checkRegistrationJdbc(token[1], token[2], token[3])){
                                sendMessage(ServiceMessages.REG_OK);
                            }else{
                                sendMessage(ServiceMessages.REG_NO);
                            }
                        }
                    }
                //цикл работы
                while (authenticated) {
                    String str = in.readUTF();
                        System.out.println("Client: " + str);
                        if(str.startsWith("/")){
                            if (str.equals(ServiceMessages.END)) {
                                sendMessage(ServiceMessages.END);
                                break;
                            }
                            if(str.startsWith(ServiceMessages.PRIVATE_MSG)){
                                String[] token = str.split(" ",3);
                                if(token.length<3){
                                    continue;
                                }
                                server.broadcastPrivateMessage(this, token[1], token[2] + "\n");
                            }
                        }else{
                            server.broadcastMessage(   this,str + "\n");
                        }
                }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Client disconnected");
                        try {
                            socket.close();
                            server.unsubscribe(this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
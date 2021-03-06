import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private  ServerSocket server;
    private  Socket socket;
    private  final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        authService = new SimpleAuthService();
        authService.start();
        clients = new CopyOnWriteArrayList<>();
        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started!");

            while (true){
                socket = server.accept();
                System.out.println("Client connected:"+socket.getRemoteSocketAddress());
                new ClientHandler(this, socket);

            }





        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("Server down");
            try{
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void subscribe(ClientHandler clientHandler){

        clients.add(clientHandler);
        clientList();
    }

    public void unsubscribe(ClientHandler clientHandler){

        clients.remove(clientHandler);
        clientList();
    }

    public boolean checkAuth(String login){
        for (ClientHandler client : clients) {
            if(client.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }
    public void clientList(){
        StringBuilder stringBuilder = new StringBuilder(ServiceMessages.CLIENT_LIST);
        for (ClientHandler client : clients) {
            stringBuilder.append(" ").append(client.getNickname());
        }
        String massage = stringBuilder.toString();
        for (ClientHandler client : clients) {
            client.sendMessage(massage);
        }
    }
    public void broadcastMessage(ClientHandler sender,String msg){
        String message = String.format("[ %s ]: %s", sender.getNickname(),msg);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }

    }public void broadcastPrivateMessage(ClientHandler sender,String recipient,String msg){
        String message = String.format("[ %s ] to [ %s ]: %s", sender.getNickname(),recipient ,msg);

        for (ClientHandler client : clients) {
            if(sender.getNickname().equals(recipient)){
                sender.sendMessage(message);
                break;
            }
            if(client.getNickname().equals(recipient)){
                client.sendMessage(message);
                sender.sendMessage(message);
                break;
            }else {
                sender.sendMessage(String.format("%s not found", recipient));
            }
        }

    }

}

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Client implements Initializable {
    private static Socket socket;
    private static final int PORT = 8180;
    private static final String HOST = "localhost";
    private static DataInputStream in;
    private static DataOutputStream out;
    private static BufferedWriter writeHistory;
    public HBox sendMessageBar;
    public ListView clientList;
    private boolean authenticated;
    private String nickname;
    private Stage stage;
    private int authTime = 0;
    private String history;

    @FXML
    public javafx.scene.control.TextArea textArea;
    @FXML
    public javafx.scene.control.TextField textField;
    @FXML
    public TextField textFieldLogin;
    @FXML
    public PasswordField textFieldPassword;
    @FXML
    public Button sigIn;
    @FXML
    public HBox authBar;
    @FXML
    private Stage regStage;
    @FXML
    private RegController regController;
    public boolean isAuthenticated() {
        return authenticated;
    }

    @FXML
    public void pressToClose(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                authTime = 130;
                if(socket!=null&&!socket.isClosed()){
                out.writeUTF(ServiceMessages.END);
                setAuthenticated(false);
                textArea.clear();
                }else{
                    stage.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    if(socket != null && !socket.isClosed()) {
                        try {
                            out.writeUTF(ServiceMessages.END);
                            setAuthenticated(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        authenticated=true;
                        stage.close();
                    }
                }
            });
        });

    }

    @FXML
    protected void enterBtn(ActionEvent actionEvent) throws IOException {
        if (textField.getText().length() > 0) {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        }
    }

    @FXML
    public void pressToAuth(ActionEvent actionEvent) {
        if(socket == null || socket.isClosed()){
            connect();
        }
        authTime();
        try{
            String str = String.format("%s %s %s",ServiceMessages.AUTH,
                    textFieldLogin.getText().trim(), textFieldPassword.getText().trim());
           out.writeUTF(str);
           textFieldPassword.clear();
//показать пользователи последние 100 сообщений истории после входа
           Platform.runLater(() -> {
               try {
                   textArea.appendText(historyMessage());
               } catch (IOException e) {
                   e.printStackTrace();
               }
           });

        } catch (IOException e) {
            System.out.println(e+"ошибка в отправке пары пароль/логин");
        }
    }

    private String historyMessage() throws IOException {
        List<String> historyList = Files.readAllLines(Paths.get(history));
        List<String> historyListFormat;
        StringBuilder stringBuilder = new StringBuilder("******HISTORY OF RECENT MESSAGE******\n");
        if(historyList.size()>=100){
            historyListFormat = historyList.subList(historyList.size()-100, historyList.size());
            for (String s : historyListFormat) {
                stringBuilder.append(s);
                stringBuilder.append("\n");
            }
        }else {
            for (String s : historyList) {
                stringBuilder.append(s);
                stringBuilder.append("\n");
            }
        }

//        int count = 0;
//        for (int i = historyList.size()-1; i >=0 ; i--) {
//            count++;
//            if(count>100){
//                break;
//            }
//            historyListFormat.add(historyList.get(i));
//        }
//        for (int i = historyListFormat.size()-1; i>=0 ; i--) {
//            stringBuilder.append(historyListFormat.get(i));
//            stringBuilder.append("\n");
//        }
            stringBuilder.append("***********END HISTORY*********** \n");
        return stringBuilder.toString();
    }
    public void connect(){
        try {

            socket = new Socket(HOST, PORT);
            history = "client/src/main/java/history/history_"+textFieldLogin.getText()+".txt";
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
//создание txt файла с историей сообщений
            File historyFile = new File(history);
            if(!historyFile.exists()){
                historyFile.createNewFile();
            }
            writeHistory = new BufferedWriter(new FileWriter(historyFile, true));


            /* Thread inputStream =*/
            new Thread(() -> {
                try {
                    while (!authenticated) {
                    //цикл аутентификации
                        String str = in.readUTF();
                            if (str.equals(ServiceMessages.END)) {
                            break;
                            }
                            if(str.startsWith(ServiceMessages.REG)){
                                regController.registrationResult(str);
                                break;
                            }

                            if(str.startsWith(ServiceMessages.AUTH_OK)){
                                nickname = str.split(" ")[1];
                                setAuthenticated(true);
                                textArea.clear();
                                //File file = new File("client/history.txt");


                                break;
                            }
                        else {
                            textArea.appendText((str + "\n"));
                            break;
                        }
                    }
                    //цикл работы
                    while (authenticated) {
                        String str = in.readUTF();
                        if(str.startsWith("/")){
                            if(str.startsWith(ServiceMessages.RENAME)){
                                if(str.startsWith(ServiceMessages.RENAME_OK)){
                                    regController.registrationResult(str);
                                }else if(str.startsWith(ServiceMessages.RENAME_NO)){
                                    regController.registrationResult(str);
                                }
                                //break;
                            }
                            if(str.startsWith(ServiceMessages.CLIENT_LIST)){
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    String[] token = str.split(" ");
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                           }
                        }else{
                            textArea.appendText(str+"\n");
                            writeHistory.append(str);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.writeUTF(ServiceMessages.END);
                        writeHistory.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authBar.setVisible(!authenticated);
        authBar.setManaged(!authenticated);
        sendMessageBar.setVisible(authenticated);
        sendMessageBar.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);

        if(!authenticated){
            nickname="";
        }
        setTitle(nickname);
    }
    private void setTitle(String nickname){
        String title;
        if(nickname.equals("")){
            title = "MyChat";
        }else {
            title = String.format("Magic chat - %s", nickname);
        }
        Platform.runLater(() -> {
            stage.setTitle(title);
        });
    }
    private void authTime(){
        authTime=0;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!authenticated) {
                        try {
                            Thread.sleep(1000);
                            authTime++;
                            System.out.println(authTime);
                            if (authTime >= 120) {
                                if (socket != null && !socket.isClosed()) {
                                    out.writeUTF(ServiceMessages.END);
                                    socket.close();
                                }
                                return;
                            }

                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
    }


    public void privateMsg(MouseEvent mouseEvent) {
       String receiver = clientList.getSelectionModel().getSelectedItem().toString();
       textField.setText(ServiceMessages.PRIVATE_MSG + " " + receiver + " ");
    }
    public void registration(){
        if(socket == null || socket.isClosed()){
            connect();
        }
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 300, 390 );
            regStage = new Stage();
            regStage.setTitle("registration");
            regStage.setScene(scene);
            regController = fxmlLoader.getController();
            regController.setClient(this);
            regStage.initModality(Modality.APPLICATION_MODAL);
            regStage.initStyle(StageStyle.UTILITY);

            regStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void pressToReg(ActionEvent actionEvent) {
        registration();
    }

    public void checkRegMsg(String result){
        if(socket == null || socket.isClosed()){
            connect();
        }
        try {
            out.writeUTF(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void renameMsg(String result){
        if(socket == null || socket.isClosed()){
            connect();
        }
        try {
            out.writeUTF(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rename(ActionEvent actionEvent) {
        registration();
        regStage.setTitle("Rename");

    }
}

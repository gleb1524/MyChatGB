import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class RegController {
    @FXML
    public TextField textFieldLogin;
    @FXML
    public PasswordField textFieldPassword;
    @FXML
    public TextField textFieldNick;
    @FXML
    public TextArea textArea;
    @FXML
    public Button registration;
    public HBox buttonsPanels;
    public Button rename;
    public Button enter;
    public Label labelLogin;
    public Label labelPassword;
    public Label labelNickname;
    private Client client;

    public void setClient(Client client) {
        this.client = client;
        if(client.isAuthenticated()){
            registration.setVisible(false);
            registration.setManaged(false);
            rename.setVisible(true);
            rename.setManaged(true);
            textFieldLogin.setVisible(false);
            textFieldLogin.setManaged(false);
            textFieldPassword.setVisible(false);
            textFieldPassword.setManaged(false);
            labelLogin.setVisible(false);
            labelPassword.setVisible(false);
            labelLogin.setManaged(false);
            labelPassword.setManaged(false);
            labelNickname.setText("Pleas enter a new nickname.");
        }
    }

    @FXML
    public void pressToReg(ActionEvent actionEvent) {
        String login = textFieldLogin.getText().trim();
        String password = textFieldPassword.getText().trim();
        String nickname = textFieldNick.getText().trim();
        String massage = String.format("%s %s %s %s", ServiceMessages.REG, login, password, nickname);
        client.checkRegMsg(massage);
    }

    public void registrationResult(String result) {
        if (result.equals(ServiceMessages.REG_OK)) {
            textArea.appendText("Registration satisfied!\n");
        }
        if (result.equals(ServiceMessages.REG_NO)) {
            textArea.appendText("Registration failed!\n");
        }
        if(result.startsWith(ServiceMessages.RENAME_OK)){
           String nick = result.split(" ")[1];
            textArea.appendText("Nickname changed to: " + nick + " successfully!");
        }else if(result.startsWith(ServiceMessages.RENAME_NO)){
            textArea.appendText("Rename failed! Check password or login.");
        }
    }

    public void pressToRename(ActionEvent actionEvent) {
//        String login = textFieldLogin.getText().trim();
//        String password = textFieldPassword.getText().trim();
        String nickname = textFieldNick.getText().trim();
        String massage = String.format("%s %s", ServiceMessages.RENAME, nickname);
        client.renameMsg(massage);
    }

    public void saveRename(ActionEvent actionEvent) {




    }
}

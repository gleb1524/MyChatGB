import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private Client client;

    public void setClient(Client client) {
        this.client = client;
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
        registration.setVisible(false);
        registration.setManaged(false);
        rename.setVisible(false);
        rename.setManaged(false);
        enter.setVisible(true);
        enter.setManaged(true);
    }

    public void saveRename(ActionEvent actionEvent) {
        registration.setVisible(true);
        registration.setManaged(true);
        rename.setVisible(true);
        rename.setManaged(true);
        enter.setManaged(false);
        enter.setVisible(false);

        String login = textFieldLogin.getText().trim();
        String password = textFieldPassword.getText().trim();
        String nickname = textFieldNick.getText().trim();
        String massage = String.format("%s %s %s %s", ServiceMessages.RENAME, login, password, nickname);
        client.renameMsg(massage);

    }
}

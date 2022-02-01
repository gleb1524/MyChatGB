import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    public void pressToReg(ActionEvent actionEvent) {
        String login = textFieldLogin.getText().trim();
        String password = textFieldPassword.getText().trim();
        String nickname = textFieldNick.getText().trim();
        String massage = String.format("%s %s %s %s",ServiceMessages.REG, login, password, nickname);
        client.checkRegMsg(massage);
    }
    public void registrationResult(String result){
        if(result.equals(ServiceMessages.REG_OK)){
            textArea.appendText("Registration satisfied!\n");
        }
        if(result.equals(ServiceMessages.REG_NO)){
            textArea.appendText("Registration failed!\n");
        }
    }
}

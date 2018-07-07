package passwordkeeper.controller;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import passwordkeeper.PasswordKeeper;
import passwordkeeper.storage.KeeperOfStorage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

public class LoginFormWindowController implements Initializable {

    @FXML
    private Button btn_enter;

    @FXML
    private Label lb_message;

    @FXML
    private Button btn_back;

    @FXML
    private PasswordField passwordField;

    private KeeperOfStorage keeperOfStorage = null;
    private Window storageSelectionWindow = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PseudoClass pseudoClassEmpty = PseudoClass.getPseudoClass("empty");
        passwordField.pseudoClassStateChanged(pseudoClassEmpty, true);
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                passwordField.pseudoClassStateChanged(pseudoClassEmpty, true);
            } else {
                passwordField.pseudoClassStateChanged(pseudoClassEmpty, false);
            }
        });

        btn_enter.disableProperty().bind(passwordField.textProperty().isEmpty());
        btn_enter.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && keeperOfStorage != null) {
                loginToStorage(((Node) event.getSource()).getScene().getWindow());
            }
        });
        passwordField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER && keeperOfStorage != null && !passwordField.getText().isEmpty()) loginToStorage(((Node) event.getSource()).getScene().getWindow());
        });
    }

    @FXML
    private void closeWindow(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
    }

    private void loginToStorage(Window window) {
        if (passwordField.getText().equals(keeperOfStorage.getStorage().getPasswordOfStorage())) {
            try {
                FXMLLoader loader = new FXMLLoader(PasswordKeeper.class.getResource("/fxml/StorageManagerWindow.fxml"));

                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();

                StorageManagerWindowController controller = loader.getController();
                controller.setKeeperOfStorage(keeperOfStorage);
                controller.setStorageSelectionWindow(storageSelectionWindow);
                controller.setThisWindow(stage);

                stage.setMinHeight(535);
                stage.setMinWidth(800);

                stage.setTitle(NAME_PROGRAM + " - " + keeperOfStorage.getName());

                stage.setOnCloseRequest(e -> System.exit(0));

                stage.setScene(scene);
                stage.show();

                lb_message.setText("");
                window.hide();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            passwordField.setText("");
            lb_message.setText("Неправильный пароль!");
        }
    }

    public void setKeeperOfStorage(KeeperOfStorage keeperOfStorage) {
        this.keeperOfStorage = keeperOfStorage;
    }

    public void setStorageSelectionWindow(Window storageSelectionWindow) {
        this.storageSelectionWindow = storageSelectionWindow;
    }
}

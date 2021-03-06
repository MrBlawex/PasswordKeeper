package passwordkeeper.controller;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import passwordkeeper.storage.KeeperOfStorage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

/**
 * Контроллер окна для создания новых хранилищ
 *
 * @author Eduard
 */
public class StorageCreatorWindowController implements Initializable {


    @FXML
    private AnchorPane ap_settings;

    @FXML
    private Button btn_accept;

    @FXML
    private PasswordField pf_password;

    @FXML
    private CheckBox cb_useDefault;

    @FXML
    private Button btn_generateRandomName;

    @FXML
    private TextField tf_pathOfFile;

    @FXML
    private Button btn_back;

    @FXML
    private Button btn_choosePath;

    @FXML
    private Label lb_wrongMessage;

    @FXML
    private TextField tf_nameOfFile;

    private StorageSelectionWindowController controller;
    private File tempPath;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PseudoClass pseudoClassEmpty = PseudoClass.getPseudoClass("empty");
        cb_useDefault.selectedProperty().addListener((observable, oldValue, newValue) -> {
            tf_nameOfFile.pseudoClassStateChanged(pseudoClassEmpty, !newValue);
            tf_pathOfFile.pseudoClassStateChanged(pseudoClassEmpty, !newValue);
        });

        pf_password.pseudoClassStateChanged(pseudoClassEmpty, true);
        pf_password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                pf_password.pseudoClassStateChanged(pseudoClassEmpty, true);
            } else {
                pf_password.pseudoClassStateChanged(pseudoClassEmpty, false);
            }
        });

        tf_nameOfFile.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                tf_nameOfFile.pseudoClassStateChanged(pseudoClassEmpty, true);
            } else {
                tf_nameOfFile.pseudoClassStateChanged(pseudoClassEmpty, false);
            }
        });

        tf_pathOfFile.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                tf_pathOfFile.pseudoClassStateChanged(pseudoClassEmpty, true);
            } else {
                tf_pathOfFile.pseudoClassStateChanged(pseudoClassEmpty, false);
            }
        });

        ap_settings.disableProperty().bind(cb_useDefault.selectedProperty());
        btn_accept.disableProperty().bind(tf_pathOfFile.textProperty().isNotEmpty().and(tf_pathOfFile.textProperty().isNotEmpty()).or(cb_useDefault.selectedProperty()).and(pf_password.textProperty().isNotEmpty()).not());
    }

    @FXML
    public void createStorage(MouseEvent event) {
        try {
            if (cb_useDefault.isSelected()) {
                File defaultDir = new File("src/main/resources/storages");
                controller.addToListOfKeepers(KeeperOfStorage.newStorage(defaultDir, generateRandomName(), pf_password.getText()));
            } else {
                if (tempPath != null) {
                    String nameOfFile = tf_nameOfFile.getText();
                    controller.addToListOfKeepers(KeeperOfStorage.newStorage(tempPath, nameOfFile, pf_password.getText()));
                }
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText("Look, an Exception Dialog");
            alert.setContentText("Could not find file blabla.txt!");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();
        }
        close(event);
    }

    @FXML
    public void close(MouseEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void choosePath(MouseEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(NAME_PROGRAM + " - " + "Выберите путь");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        tempPath = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());
        if (tempPath != null) {
            tf_pathOfFile.setText(tempPath.getAbsolutePath());
        } else {
            tf_pathOfFile.setText("");
        }
    }

    public void setGenerateRandomName() {
        tf_nameOfFile.setText(generateRandomName());
    }

    private String generateRandomName() {
        Random random = new Random();
        return "Storage" + (random.nextInt(999) + 1);
    }

    void setStorageSelectionWindowController(StorageSelectionWindowController controller) {
        this.controller = controller;
    }
}

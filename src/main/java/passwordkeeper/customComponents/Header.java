package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

public class Header extends FieldPane implements SampleField, KryoSerializable, Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private HBox hBox;

    @FXML
    private Label label;

    private StringProperty textProperty;

    public Header() {
        this.textProperty = new SimpleStringProperty("");

        Platform.runLater(this::makeDraggable);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Header(String text) {
        this.textProperty = new SimpleStringProperty(text);

        Platform.runLater(this::makeDraggable);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Header(Header header) {
        this.textProperty = new SimpleStringProperty(header.textProperty.get());

        Platform.runLater(this::makeDraggable);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePane();
        initializeControl();
    }

    private void initializePane() {
        label.textProperty().bindBidirectional(textProperty);
    }

    private void initializeControl() {
        MenuItem itemRenameLB = new MenuItem("Переименовать");
        itemRenameLB.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(label.getText());
            dialog.setTitle(NAME_PROGRAM + " - " + "Переименование заглавия");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите текст: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> label.setText(s));
        });

        ContextMenu menu = new ContextMenu(itemRenameLB);

        hBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(textProperty.getValue());
    }

    @Override
    public void read(Kryo kryo, Input input) {
        textProperty.setValue(input.readString());
    }

    @Override
    public Node getNode() {
        return this;
    }
}

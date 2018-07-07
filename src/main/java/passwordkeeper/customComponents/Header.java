package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
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
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Observer;
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

    @FXML
    private MaterialDesignIconView btn_remove;

    private HeaderDataObserver headerDataObserver;

    public Header() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        makeDraggable();
        makeRemovable();
    }

    public Header(String text, Boolean difficultVersion) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeDataObserver();
        label.setText(text);
        if (difficultVersion) {
            makeDraggable();
            makeRemovable();
        }
    }

    public Header(Header header) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.headerDataObserver = header.headerDataObserver;
        this.headerDataObserver.bindText(label.textProperty());
        makeDraggable();
        makeRemovable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeControl();
    }

    private void initializeDataObserver() {
        headerDataObserver = new HeaderDataObserver(label.textProperty());
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

    private void makeRemovable() {
        btn_remove.setOnMouseClicked(event -> ((VBox) this.getParent()).getChildren().remove(this));
    }

    @Override
    public void setObserver(Observer observer) {
        headerDataObserver.addObserver(observer);
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeClassAndObject(output, headerDataObserver);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        headerDataObserver = (HeaderDataObserver) kryo.readClassAndObject(input);
        headerDataObserver.bindText(label.textProperty());
    }
}

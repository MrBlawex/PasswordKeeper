package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import passwordkeeper.storage.Snapshot;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

public class Field extends FieldPane implements SampleField, KryoSerializable, Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane fields_box;

    @FXML
    private Label label;

    @FXML
    private TextField textField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private HBox btn_box;

    @FXML
    private ToggleButton visibleText;

    @FXML
    private Button copyToBuff;

    private MaterialDesignIconView iconEYE;
    private MaterialDesignIconView iconEYE_OFF;
    private MaterialDesignIconView iconCopy;

    private StringProperty nameProperty;
    private StringProperty textProperty;
    private BooleanProperty isShowing;

    private ArrayList<Snapshot> history;
    private Snapshot currentSnapshot;

    public Field() {
        nameProperty = new SimpleStringProperty("");
        textProperty = new SimpleStringProperty("");
        isShowing = new SimpleBooleanProperty(false);

        Platform.runLater(() -> {
            initializeHistory();
            makeDraggable();
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Field(String name, String text, Boolean difficultVersion) {
        nameProperty = new SimpleStringProperty(name);
        textProperty = new SimpleStringProperty(text);
        isShowing = new SimpleBooleanProperty(false);

        if (difficultVersion) {
            Platform.runLater(() -> {
                initializeHistory();
                makeDraggable();
            });
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Field(Field field) {
        nameProperty = new SimpleStringProperty(field.nameProperty.get());
        textProperty = new SimpleStringProperty(field.textProperty.get());
        isShowing = new SimpleBooleanProperty(field.isShowing.get());

        Platform.runLater(() -> {
            initializeHistory();
            makeDraggable();
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Field.fxml"));
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
        tuningGraphic();
        initializePane();
        initializeGraphics();
        initializeControl();
    }

    private void tuningGraphic() {
        iconEYE = new MaterialDesignIconView(MaterialDesignIcon.EYE);
        iconEYE_OFF = new MaterialDesignIconView(MaterialDesignIcon.EYE_OFF);
        iconCopy = new MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY);
    }

    private void initializePane() {
        label.textProperty().bindBidirectional(nameProperty);
        textField.textProperty().bindBidirectional(textProperty);
        passwordField.textProperty().bindBidirectional(textProperty);
        visibleText.selectedProperty().bindBidirectional(isShowing);

        textField.setManaged(false);
        textField.setVisible(false);

        textField.managedProperty().bind(isShowing.not());
        textField.visibleProperty().bind(isShowing.not());

        passwordField.managedProperty().bind(isShowing);
        passwordField.visibleProperty().bind(isShowing);

        copyToBuff.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && !textField.getText().isEmpty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(passwordField.getText());
                clipboard.setContents(stringSelection, null);
            }
        });
    }

    private void initializeGraphics() {
        copyToBuff.setGraphic(iconCopy);
        visibleText.setGraphic(iconEYE);

        if (!isShowing.getValue()) {
            showMask();
        } else {
            hideMask();
        }

        label.widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setRightAnchor(fields_box, btn_box.getPrefWidth() + 14);
            AnchorPane.setLeftAnchor(fields_box, newValue.doubleValue() + 14);
        });
    }

    private void initializeControl() {
        isShowing.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                showMask();
            } else {
                hideMask();
            }
        });

        MenuItem itemRenameLB = new MenuItem("Переименовать");
        itemRenameLB.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(label.getText());
            dialog.setTitle(NAME_PROGRAM + " - " + "Переименование поля");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите текст: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> label.setText(s));
        });

        ContextMenu menu = new ContextMenu(itemRenameLB);

        label.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void initializeHistory() {
        history = new ArrayList<>();
        currentSnapshot = makeSnapshot();

        PseudoClass pseudoClassNotSafe = PseudoClass.getPseudoClass("not-safe");
        PseudoClass pseudoClassSafe = PseudoClass.getPseudoClass("safe");
        Tooltip tooltipSafe = new Tooltip("Изменения сохранены");
        Tooltip tooltipNotSafe = new Tooltip("Изменения не сохранены");

        tooltipSafe.setContentDisplay(ContentDisplay.CENTER);
        tooltipNotSafe.setContentDisplay(ContentDisplay.CENTER);

        textField.pseudoClassStateChanged(pseudoClassSafe, historyContainsText());
        passwordField.pseudoClassStateChanged(pseudoClassSafe, historyContainsText());


        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                currentSnapshot = makeSnapshot();
                textField.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField.pseudoClassStateChanged(pseudoClassSafe, true);
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                currentSnapshot = makeSnapshot();
                textField.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField.pseudoClassStateChanged(pseudoClassSafe, true);
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            }
        });

        textProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(history.get(history.size() - 1).getText())) {
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            } else {
                textField.setTooltip(tooltipNotSafe);
                passwordField.setTooltip(tooltipNotSafe);
            }
            textField.pseudoClassStateChanged(pseudoClassSafe, historyContainsText());
            passwordField.pseudoClassStateChanged(pseudoClassSafe, historyContainsText());

            textField.pseudoClassStateChanged(pseudoClassNotSafe, !historyContainsText());
            passwordField.pseudoClassStateChanged(pseudoClassNotSafe, !historyContainsText());
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuHistory = new MenuItem("История", new MaterialDesignIconView(MaterialDesignIcon.HISTORY));
        menuHistory.setOnAction(event -> {
            Dialog<Snapshot> dialog = new Dialog<>();

            dialog.setWidth(400);
            dialog.setTitle(NAME_PROGRAM);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            ListView<Snapshot> listView = new ListView<>();
            listView.getItems().addAll(history);

            listView.getSelectionModel().select(currentSnapshot);

            dialog.getDialogPane().setPadding(new Insets(5, 5, 5, 5));
            dialog.getDialogPane().setContent(listView);
            Platform.runLater(listView::requestFocus);
            dialog.setResultConverter(param -> listView.getSelectionModel().getSelectedItem());

            Optional<Snapshot> result = dialog.showAndWait();

            result.ifPresent(snapshot -> {
                currentSnapshot = snapshot;
                restoreSnapshot(snapshot);
            });
        });
        contextMenu.getItems().setAll(menuHistory);

        textField.setContextMenu(contextMenu);
        passwordField.setContextMenu(contextMenu);
    }

    private void showMask() {
        visibleText.setGraphic(iconEYE);
    }

    private void hideMask() {
        visibleText.setGraphic(iconEYE_OFF);
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(nameProperty.getValue());
        output.writeBoolean(isShowing.getValue());
        kryo.writeClassAndObject(output, currentSnapshot);
        kryo.writeClassAndObject(output, history);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        nameProperty.setValue(input.readString());
        isShowing.set(input.readBoolean());
        currentSnapshot = (Snapshot) kryo.readClassAndObject(input);
        history = (ArrayList<Snapshot>) kryo.readClassAndObject(input);
        restoreCurrentSnapshot();
    }

    private void restoreLastSnapshot() {
        textProperty.set(history.get(history.size() - 1).getText());
    }

    private void restoreCurrentSnapshot() {
        textProperty.set(currentSnapshot.getText());
    }

    private void restoreSnapshot(Snapshot snapshot) {
        textProperty.set(snapshot.getText());
    }

    private Snapshot makeSnapshot() {
        Snapshot temp = new Snapshot(textProperty.get());
        history.add(temp);
        return temp;
    }

    private boolean historyContainsText() {
        AtomicBoolean contains = new AtomicBoolean(false);

        history.forEach(snapshot -> {
            if (snapshot.getText().equals(textProperty.getValue())) contains.set(true);
        });

        return contains.get();
    }

    @Override
    public Node getNode() {
        return this;
    }
}

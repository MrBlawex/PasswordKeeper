package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import passwordkeeper.storage.Snapshot;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

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

    @FXML
    private Button btn_history;

    @FXML
    private MaterialDesignIconView btn_remove;

    private MaterialDesignIconView iconEYE;
    private MaterialDesignIconView iconEYE_OFF;

    private StringProperty nameProperty;
    private StringProperty textProperty;
    private BooleanProperty isShowing;

    private FieldDataObserver fieldDataObserver;

    public Field() {
        nameProperty = new SimpleStringProperty("");
        textProperty = new SimpleStringProperty("");
        isShowing = new SimpleBooleanProperty(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeFieldObserver();
        makeDraggable();
        makeRemovable();
    }

    public Field(String name, String text, Boolean difficultVersion) {
        nameProperty = new SimpleStringProperty(name);
        textProperty = new SimpleStringProperty(text);
        isShowing = new SimpleBooleanProperty(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (difficultVersion) {
            initializeFieldObserver();
            makeDraggable();
            makeRemovable();
        }
    }

    public Field(Field field) {
        nameProperty = new SimpleStringProperty(field.nameProperty.get());
        textProperty = new SimpleStringProperty(field.textProperty.get());
        isShowing = new SimpleBooleanProperty(field.isShowing.get());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeFieldObserver();
        makeDraggable();
        makeRemovable();
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
        visibleText.setGraphic(iconEYE);

        if (!isShowing.getValue()) {
            showMask();
        } else {
            hideMask();
        }

        label.widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setRightAnchor(fields_box, btn_box.getPrefWidth() + 22);
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

    private void initializeFieldObserver() {
        fieldDataObserver = new FieldDataObserver(nameProperty, textProperty, isShowing);

        initializeHistory();
    }

    private void initializeHistory() {
        btn_history.setOnMouseClicked(event -> openHistory());

        PseudoClass pseudoClassNotSafe = PseudoClass.getPseudoClass("not-safe");
        PseudoClass pseudoClassSafe = PseudoClass.getPseudoClass("safe");
        Tooltip tooltipSafe = new Tooltip("Изменения сохранены");
        Tooltip tooltipNotSafe = new Tooltip("Изменения не сохранены");

        tooltipSafe.setContentDisplay(ContentDisplay.CENTER);
        tooltipNotSafe.setContentDisplay(ContentDisplay.CENTER);

        textField.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver.getHistory().containsOnHistory());
        passwordField.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver.getHistory().containsOnHistory());

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                fieldDataObserver.getHistory().makeSnapshot();
                fieldDataObserver.getHistory().restoreLastSnapshot();
                textField.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField.pseudoClassStateChanged(pseudoClassSafe, true);
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                fieldDataObserver.getHistory().makeSnapshot();
                fieldDataObserver.getHistory().restoreLastSnapshot();
                textField.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField.pseudoClassStateChanged(pseudoClassSafe, true);
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            }
        });

        textProperty.addListener((observable, oldValue, newValue) -> {
            if (fieldDataObserver.getHistory().containsOnHistory(newValue)) {
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            } else {
                textField.setTooltip(tooltipNotSafe);
                passwordField.setTooltip(tooltipNotSafe);
            }
            textField.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver.getHistory().containsOnHistory(newValue));
            passwordField.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver.getHistory().containsOnHistory(newValue));

            textField.pseudoClassStateChanged(pseudoClassNotSafe, !fieldDataObserver.getHistory().containsOnHistory(newValue));
            passwordField.pseudoClassStateChanged(pseudoClassNotSafe, !fieldDataObserver.getHistory().containsOnHistory(newValue));
        });

        textField.setContextMenu(new ContextMenu());
        passwordField.setContextMenu(new ContextMenu());
    }

    private void makeRemovable() {
        btn_remove.setOnMouseClicked(event -> ((VBox) this.getParent()).getChildren().remove(this));
    }

    private void showMask() {
        visibleText.setGraphic(iconEYE);
    }

    private void hideMask() {
        visibleText.setGraphic(iconEYE_OFF);
    }

    private void openHistory() {
        Dialog<Snapshot> dialog = new Dialog<>();

        dialog.setTitle(NAME_PROGRAM);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        GridOfHistory gridOfHistory = new GridOfHistory(fieldDataObserver.getHistory());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(480, 400);
        scrollPane.setContent(gridOfHistory);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.setResultConverter(param -> gridOfHistory.getSelectedSnapshot());

        Optional<Snapshot> result = dialog.showAndWait();

        result.ifPresent(snapshot -> fieldDataObserver.getHistory().setCurrent(snapshot));
    }

    @Override
    public void setObserver(Observer observer) {
        fieldDataObserver.addObserver(observer);
        fieldDataObserver.getHistory().addObserver(observer);
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeClassAndObject(output, fieldDataObserver);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        this.fieldDataObserver = (FieldDataObserver) kryo.readClassAndObject(input);
        fieldDataObserver.bindAll(nameProperty, textProperty, isShowing);
    }
}

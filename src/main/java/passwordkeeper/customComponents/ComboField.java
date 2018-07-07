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

public class ComboField extends FieldPane implements SampleField, KryoSerializable, Initializable {

    @FXML
    private AnchorPane ach_home;

    @FXML
    private ToggleButton visibleText2;

    @FXML
    private ToggleButton visibleText1;

    @FXML
    private AnchorPane ach_leftBar;

    @FXML
    private Label label1;

    @FXML
    private HBox btn_box1;

    @FXML
    private Label label2;

    @FXML
    private HBox btn_box2;

    @FXML
    private AnchorPane ach_rightBar;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private TextField textField2;

    @FXML
    private TextField textField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    private Button copyToBuff1;

    @FXML
    private Button copyToBuff2;

    @FXML
    private Button btn_history1;

    @FXML
    private Button btn_history2;

    @FXML
    private AnchorPane fields_box1;

    @FXML
    private AnchorPane fields_box2;

    @FXML
    private MaterialDesignIconView btn_remove;

    private MaterialDesignIconView iconEYE1;
    private MaterialDesignIconView iconEYE_OFF1;

    private MaterialDesignIconView iconEYE2;
    private MaterialDesignIconView iconEYE_OFF2;

    private StringProperty nameProperty1;
    private StringProperty textProperty1;
    private BooleanProperty isShowing1;
    private StringProperty nameProperty2;
    private StringProperty textProperty2;
    private BooleanProperty isShowing2;

    private FieldDataObserver fieldDataObserver1;
    private FieldDataObserver fieldDataObserver2;

    public ComboField() {
        nameProperty1 = new SimpleStringProperty("");
        textProperty1 = new SimpleStringProperty("");
        isShowing1 = new SimpleBooleanProperty(false);

        nameProperty2 = new SimpleStringProperty("");
        textProperty2 = new SimpleStringProperty("");
        isShowing2 = new SimpleBooleanProperty(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComboField.fxml"));
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

    public ComboField(String name1, String text1, String name2, String text2, Boolean difficultVersion) {
        nameProperty1 = new SimpleStringProperty(name1);
        textProperty1 = new SimpleStringProperty(text1);
        isShowing1 = new SimpleBooleanProperty(false);

        nameProperty2 = new SimpleStringProperty(name2);
        textProperty2 = new SimpleStringProperty(text2);
        isShowing2 = new SimpleBooleanProperty(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComboField.fxml"));
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

    public ComboField(ComboField comboField) {
        nameProperty1 = new SimpleStringProperty(comboField.nameProperty1.get());
        textProperty1 = new SimpleStringProperty(comboField.textProperty1.get());
        isShowing1 = new SimpleBooleanProperty(comboField.isShowing1.get());

        nameProperty2 = new SimpleStringProperty(comboField.nameProperty2.get());
        textProperty2 = new SimpleStringProperty(comboField.textProperty2.get());
        isShowing2 = new SimpleBooleanProperty(comboField.isShowing2.get());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComboField.fxml"));
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
        tuningImageView();
        initializePane();
        initializeGraphics();
        initializeControl();
    }

    private void tuningImageView() {
        iconEYE1 = new MaterialDesignIconView(MaterialDesignIcon.EYE);
        iconEYE_OFF1 = new MaterialDesignIconView(MaterialDesignIcon.EYE_OFF);

        iconEYE2 = new MaterialDesignIconView(MaterialDesignIcon.EYE);
        iconEYE_OFF2 = new MaterialDesignIconView(MaterialDesignIcon.EYE_OFF);
    }

    private void initializePane() {
        label1.textProperty().bindBidirectional(nameProperty1);
        textField1.textProperty().bindBidirectional(textProperty1);
        passwordField1.textProperty().bindBidirectional(textProperty1);
        visibleText1.selectedProperty().bindBidirectional(isShowing1);

        copyToBuff1.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && !textField1.getText().isEmpty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(passwordField1.getText());
                clipboard.setContents(stringSelection, null);
            }
        });

        textField1.setManaged(false);
        textField1.setVisible(false);

        textField1.managedProperty().bind(isShowing1.not());
        textField1.visibleProperty().bind(isShowing1.not());

        passwordField1.managedProperty().bind(isShowing1);
        passwordField1.visibleProperty().bind(isShowing1);

        textField1.textProperty().bindBidirectional(passwordField1.textProperty());

        label2.textProperty().bindBidirectional(nameProperty2);
        textField2.textProperty().bindBidirectional(textProperty2);
        passwordField2.textProperty().bindBidirectional(textProperty2);
        visibleText2.selectedProperty().bindBidirectional(isShowing2);


        copyToBuff2.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && !textField2.getText().isEmpty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(passwordField2.getText());
                clipboard.setContents(stringSelection, null);
            }
        });

        textField2.setManaged(false);
        textField2.setVisible(false);

        textField2.managedProperty().bind(isShowing2.not());
        textField2.visibleProperty().bind(isShowing2.not());

        passwordField2.managedProperty().bind(isShowing2);
        passwordField2.visibleProperty().bind(isShowing2);

        textField2.textProperty().bindBidirectional(passwordField2.textProperty());

    }

    private void initializeGraphics() {
        if (!isShowing1.getValue()) {
            showMask1();
        } else {
            hideMask1();
        }

        if (!isShowing2.getValue()) {
            showMask2();
        } else {
            hideMask2();
        }

        ach_home.widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setRightAnchor(ach_leftBar, newValue.doubleValue() / 2);
            AnchorPane.setLeftAnchor(ach_rightBar, newValue.doubleValue() / 2);
        });
    }

    private void initializeControl() {
        isShowing1.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                showMask1();
            } else {
                hideMask1();
            }
        });

        isShowing2.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                showMask2();
            } else {
                hideMask2();
            }
        });

        MenuItem itemRenameLB1 = new MenuItem("Переименовать");
        itemRenameLB1.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(label1.getText());
            dialog.setTitle(NAME_PROGRAM + " - " + "Переименование поля");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите текст: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> label1.setText(s));
        });
        MenuItem itemRenameLB2 = new MenuItem("Переименовать");
        itemRenameLB2.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(label2.getText());
            dialog.setTitle(NAME_PROGRAM + " - " + "Переименование поля");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите текст: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> label2.setText(s));
        });

        ContextMenu menu1 = new ContextMenu(itemRenameLB1);
        ContextMenu menu2 = new ContextMenu(itemRenameLB2);

        label1.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                menu1.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        });
        label2.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                menu2.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void initializeHistory() {
        btn_history1.setOnMouseClicked(event -> openHistory1());
        btn_history2.setOnMouseClicked(event -> openHistory2());

        PseudoClass pseudoClassNotSafe = PseudoClass.getPseudoClass("not-safe");
        PseudoClass pseudoClassSafe = PseudoClass.getPseudoClass("safe");
        Tooltip tooltipSafe = new Tooltip("Изменения сохранены");
        Tooltip tooltipNotSafe = new Tooltip("Изменения не сохранены");

        tooltipSafe.setContentDisplay(ContentDisplay.CENTER);
        tooltipNotSafe.setContentDisplay(ContentDisplay.CENTER);

        textField1.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver1.getHistory().containsOnHistory());
        passwordField1.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver2.getHistory().containsOnHistory());


        textField1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !fieldDataObserver1.getHistory().containsOnHistory()) {
                fieldDataObserver1.getHistory().makeSnapshot();
                fieldDataObserver1.getHistory().restoreLastSnapshot();
                textField1.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField1.pseudoClassStateChanged(pseudoClassSafe, true);
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            }
        });

        passwordField1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !fieldDataObserver1.getHistory().containsOnHistory()) {
                fieldDataObserver1.getHistory().makeSnapshot();
                fieldDataObserver1.getHistory().restoreLastSnapshot();
                textField1.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField1.pseudoClassStateChanged(pseudoClassSafe, true);
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            }
        });

        textProperty1.addListener((observable, oldValue, newValue) -> {
            if (fieldDataObserver1.getHistory().containsOnHistory()) {
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            } else {
                textField1.setTooltip(tooltipNotSafe);
                passwordField1.setTooltip(tooltipNotSafe);
            }
            textField1.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver1.getHistory().containsOnHistory());
            passwordField1.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver1.getHistory().containsOnHistory());

            textField1.pseudoClassStateChanged(pseudoClassNotSafe, !fieldDataObserver1.getHistory().containsOnHistory());
            passwordField1.pseudoClassStateChanged(pseudoClassNotSafe, !fieldDataObserver1.getHistory().containsOnHistory());
        });

        textField2.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver2.getHistory().containsOnHistory());
        passwordField2.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver2.getHistory().containsOnHistory());


        textField2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !fieldDataObserver2.getHistory().containsOnHistory()) {
                fieldDataObserver2.getHistory().makeSnapshot();
                fieldDataObserver2.getHistory().restoreLastSnapshot();
                textField2.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField2.pseudoClassStateChanged(pseudoClassSafe, true);
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            }
        });

        passwordField2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !fieldDataObserver2.getHistory().containsOnHistory()) {
                fieldDataObserver2.getHistory().makeSnapshot();
                fieldDataObserver2.getHistory().restoreLastSnapshot();
                textField2.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField2.pseudoClassStateChanged(pseudoClassSafe, true);
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            }
        });

        textProperty2.addListener((observable, oldValue, newValue) -> {
            if (fieldDataObserver2.getHistory().containsOnHistory(newValue)) {
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            } else {
                textField2.setTooltip(tooltipNotSafe);
                passwordField2.setTooltip(tooltipNotSafe);
            }
            textField2.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver2.getHistory().containsOnHistory(newValue));
            passwordField2.pseudoClassStateChanged(pseudoClassSafe, fieldDataObserver2.getHistory().containsOnHistory(newValue));

            textField2.pseudoClassStateChanged(pseudoClassNotSafe, !fieldDataObserver2.getHistory().containsOnHistory(newValue));
            passwordField2.pseudoClassStateChanged(pseudoClassNotSafe, !fieldDataObserver2.getHistory().containsOnHistory(newValue));
        });

        textField1.setContextMenu(new ContextMenu());
        passwordField1.setContextMenu(new ContextMenu());

        textField2.setContextMenu(new ContextMenu());
        passwordField2.setContextMenu(new ContextMenu());
    }

    private void initializeFieldObserver() {
        fieldDataObserver1 = new FieldDataObserver(nameProperty1, textProperty1, isShowing1);
        fieldDataObserver2 = new FieldDataObserver(nameProperty2, textProperty2, isShowing2);

        initializeHistory();
    }

    private void showMask1() {
        visibleText1.setGraphic(iconEYE1);
    }

    private void hideMask1() {
        visibleText1.setGraphic(iconEYE_OFF1);
    }

    private void showMask2() {
        visibleText2.setGraphic(iconEYE2);
    }

    private void hideMask2() {
        visibleText2.setGraphic(iconEYE_OFF2);
    }

    private void openHistory1() {
        Dialog<Snapshot> dialog = new Dialog<>();

        dialog.setTitle(NAME_PROGRAM);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        GridOfHistory gridOfHistory = new GridOfHistory(fieldDataObserver1.getHistory());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(480, 400);
        scrollPane.setContent(gridOfHistory);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.setResultConverter(param -> gridOfHistory.getSelectedSnapshot());

        Optional<Snapshot> result = dialog.showAndWait();

        result.ifPresent(snapshot -> fieldDataObserver1.getHistory().setCurrent(snapshot));
    }

    private void openHistory2() {
        Dialog<Snapshot> dialog = new Dialog<>();

        dialog.setTitle(NAME_PROGRAM);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        GridOfHistory gridOfHistory = new GridOfHistory(fieldDataObserver2.getHistory());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(480, 400);
        scrollPane.setContent(gridOfHistory);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.setResultConverter(param -> gridOfHistory.getSelectedSnapshot());

        Optional<Snapshot> result = dialog.showAndWait();

        result.ifPresent(snapshot -> fieldDataObserver2.getHistory().setCurrent(snapshot));
    }

    private void makeRemovable() {
        btn_remove.setOnMouseClicked(event -> ((VBox) this.getParent()).getChildren().remove(this));
    }

    @Override
    public void setObserver(Observer observer) {
        fieldDataObserver1.addObserver(observer);
        fieldDataObserver1.getHistory().addObserver(observer);

        fieldDataObserver1.addObserver(observer);
        fieldDataObserver2.addObserver(observer);
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeClassAndObject(output, fieldDataObserver1);
        kryo.writeClassAndObject(output, fieldDataObserver2);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        fieldDataObserver1 = (FieldDataObserver) kryo.readClassAndObject(input);
        fieldDataObserver2 = (FieldDataObserver) kryo.readClassAndObject(input);

        fieldDataObserver1.bindAll(nameProperty1, textProperty1, isShowing1);
        fieldDataObserver2.bindAll(nameProperty2, textProperty2, isShowing2);
    }
}

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
    private AnchorPane fields_box1;

    @FXML
    private AnchorPane fields_box2;


    private MaterialDesignIconView iconEYE1;
    private MaterialDesignIconView iconEYE_OFF1;
    private MaterialDesignIconView iconCopy1;

    private MaterialDesignIconView iconEYE2;
    private MaterialDesignIconView iconEYE_OFF2;
    private MaterialDesignIconView iconCopy2;

    private StringProperty nameProperty1;
    private StringProperty textProperty1;
    private BooleanProperty isShowing1;
    private StringProperty nameProperty2;
    private StringProperty textProperty2;
    private BooleanProperty isShowing2;

    private ArrayList<Snapshot> history1;
    private ArrayList<Snapshot> history2;

    private Snapshot currentSnapshot1;
    private Snapshot currentSnapshot2;

    public ComboField() {
        nameProperty1 = new SimpleStringProperty("");
        textProperty1 = new SimpleStringProperty("");
        isShowing1 = new SimpleBooleanProperty(false);

        nameProperty2 = new SimpleStringProperty("");
        textProperty2 = new SimpleStringProperty("");
        isShowing2 = new SimpleBooleanProperty(false);

        Platform.runLater(() -> {
            initializeHistory();
            makeDraggable();
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ComboField.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ComboField(String name1, String text1, String name2, String text2, Boolean difficultVersion) {
        nameProperty1 = new SimpleStringProperty(name1);
        textProperty1 = new SimpleStringProperty(text1);
        isShowing1 = new SimpleBooleanProperty(false);

        nameProperty2 = new SimpleStringProperty(name2);
        textProperty2 = new SimpleStringProperty(text2);
        isShowing2 = new SimpleBooleanProperty(false);

        if (difficultVersion) {
            Platform.runLater(() -> {
                initializeHistory();
                makeDraggable();
            });
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ComboField.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ComboField(ComboField comboField) {
        nameProperty1 = new SimpleStringProperty(comboField.nameProperty1.get());
        textProperty1 = new SimpleStringProperty(comboField.textProperty1.get());
        isShowing1 = new SimpleBooleanProperty(comboField.isShowing1.get());

        nameProperty2 = new SimpleStringProperty(comboField.nameProperty2.get());
        textProperty2 = new SimpleStringProperty(comboField.textProperty2.get());
        isShowing2 = new SimpleBooleanProperty(comboField.isShowing2.get());

        Platform.runLater(() -> {
            initializeHistory();
            makeDraggable();
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ComboField.fxml"));
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
        tuningImageView();
        initializePane();
        initializeGraphics();
        initializeControl();
    }

    private void tuningImageView() {
        iconCopy1 = new MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY);
        iconEYE1 = new MaterialDesignIconView(MaterialDesignIcon.EYE);
        iconEYE_OFF1 = new MaterialDesignIconView(MaterialDesignIcon.EYE_OFF);

        iconCopy2 = new MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY);
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
        copyToBuff1.setGraphic(iconCopy1);
        visibleText1.setGraphic(iconEYE1);

        copyToBuff2.setGraphic(iconCopy2);
        visibleText2.setGraphic(iconEYE2);

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
        history1 = new ArrayList<>();
        currentSnapshot1 = makeSnapshot1();
        history2 = new ArrayList<>();
        currentSnapshot2 = makeSnapshot2();

        PseudoClass pseudoClassNotSafe = PseudoClass.getPseudoClass("not-safe");
        PseudoClass pseudoClassSafe = PseudoClass.getPseudoClass("safe");
        Tooltip tooltipSafe = new Tooltip("Изменения сохранены");
        Tooltip tooltipNotSafe = new Tooltip("Изменения не сохранены");

        tooltipSafe.setContentDisplay(ContentDisplay.CENTER);
        tooltipNotSafe.setContentDisplay(ContentDisplay.CENTER);

        textField1.pseudoClassStateChanged(pseudoClassSafe, historyContainsText1());
        passwordField1.pseudoClassStateChanged(pseudoClassSafe, historyContainsText1());


        textField1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !historyContainsText1()) {
                currentSnapshot1 = makeSnapshot1();
                textField1.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField1.pseudoClassStateChanged(pseudoClassSafe, true);
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            }
        });

        passwordField1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !historyContainsText1()) {
                currentSnapshot1 = makeSnapshot1();
                textField1.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField1.pseudoClassStateChanged(pseudoClassSafe, true);
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            }
        });

        textProperty1.addListener((observable, oldValue, newValue) -> {
            if (historyContainsText1()) {
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            } else {
                textField1.setTooltip(tooltipNotSafe);
                passwordField1.setTooltip(tooltipNotSafe);
            }
            textField1.pseudoClassStateChanged(pseudoClassSafe, historyContainsText1());
            passwordField1.pseudoClassStateChanged(pseudoClassSafe, historyContainsText1());

            textField1.pseudoClassStateChanged(pseudoClassNotSafe, !historyContainsText1());
            passwordField1.pseudoClassStateChanged(pseudoClassNotSafe, !historyContainsText1());
        });

        textField2.pseudoClassStateChanged(pseudoClassSafe, historyContainsText2());
        passwordField2.pseudoClassStateChanged(pseudoClassSafe, historyContainsText2());


        textField2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !historyContainsText2()) {
                currentSnapshot2 = makeSnapshot2();
                textField2.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField2.pseudoClassStateChanged(pseudoClassSafe, true);
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            }
        });

        passwordField2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !historyContainsText2()) {
                currentSnapshot2 = makeSnapshot2();
                textField2.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField2.pseudoClassStateChanged(pseudoClassSafe, true);
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            }
        });

        textProperty2.addListener((observable, oldValue, newValue) -> {
            if (historyContainsText2()) {
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            } else {
                textField2.setTooltip(tooltipNotSafe);
                passwordField2.setTooltip(tooltipNotSafe);
            }
            textField2.pseudoClassStateChanged(pseudoClassSafe, historyContainsText2());
            passwordField2.pseudoClassStateChanged(pseudoClassSafe, historyContainsText2());

            textField2.pseudoClassStateChanged(pseudoClassNotSafe, !historyContainsText2());
            passwordField2.pseudoClassStateChanged(pseudoClassNotSafe, !historyContainsText2());
        });

        ContextMenu contextMenu1 = new ContextMenu();
        MenuItem menuHistory1 = new MenuItem("История", new MaterialDesignIconView(MaterialDesignIcon.HISTORY));
        menuHistory1.setOnAction(event -> {
            Dialog<Snapshot> dialog = new Dialog<>();

            dialog.setWidth(400);
            dialog.setTitle(NAME_PROGRAM);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            ListView<Snapshot> listView = new ListView<>();
            listView.getItems().addAll(history1);

            listView.getSelectionModel().select(currentSnapshot1);

            dialog.getDialogPane().setPadding(new Insets(5, 5, 5, 5));
            dialog.getDialogPane().setContent(listView);
            Platform.runLater(listView::requestFocus);
            dialog.setResultConverter(param -> listView.getSelectionModel().getSelectedItem());

            Optional<Snapshot> result = dialog.showAndWait();

            result.ifPresent(snapshot -> {
                currentSnapshot1 = snapshot;
                restoreSnapshot1(snapshot);
            });
        });
        contextMenu1.getItems().addAll(menuHistory1);

        ContextMenu contextMenu2 = new ContextMenu();
        MenuItem menuHistory2 = new MenuItem("История", new MaterialDesignIconView(MaterialDesignIcon.HISTORY));
        menuHistory2.setOnAction(event -> {
            Dialog<Snapshot> dialog = new Dialog<>();

            dialog.setWidth(400);
            dialog.setTitle(NAME_PROGRAM);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            ListView<Snapshot> listView = new ListView<>();
            listView.getItems().addAll(history2);

            listView.getSelectionModel().select(currentSnapshot2);

            dialog.getDialogPane().setPadding(new Insets(5, 5, 5, 5));
            dialog.getDialogPane().setContent(listView);
            Platform.runLater(listView::requestFocus);
            dialog.setResultConverter(param -> listView.getSelectionModel().getSelectedItem());

            Optional<Snapshot> result = dialog.showAndWait();

            result.ifPresent(snapshot -> {
                currentSnapshot2 = snapshot;
                restoreSnapshot2(snapshot);
            });
        });
        contextMenu2.getItems().addAll(menuHistory2);

        textField1.setContextMenu(contextMenu1);
        passwordField1.setContextMenu(contextMenu1);

        textField2.setContextMenu(contextMenu2);
        passwordField2.setContextMenu(contextMenu2);
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

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(nameProperty1.getValue());
        output.writeBoolean(isShowing1.get());
        kryo.writeClassAndObject(output, currentSnapshot1);
        kryo.writeClassAndObject(output, history1);

        output.writeString(nameProperty2.getValue());
        output.writeBoolean(isShowing2.get());
        kryo.writeClassAndObject(output, currentSnapshot2);
        kryo.writeClassAndObject(output, history2);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        nameProperty1.setValue(input.readString());
        isShowing1.set(input.readBoolean());
        currentSnapshot1 = (Snapshot) kryo.readClassAndObject(input);
        history1 = (ArrayList<Snapshot>) kryo.readClassAndObject(input);

        nameProperty2.setValue(input.readString());
        isShowing2.set(input.readBoolean());
        currentSnapshot2 = (Snapshot) kryo.readClassAndObject(input);
        history2 = (ArrayList<Snapshot>) kryo.readClassAndObject(input);

        restoreCurrentSnapshot1();
        restoreCurrentSnapshot2();
    }

    private void restoreLastSnapshot1() {
        textProperty1.set(history1.get(history1.size() - 1).getText());
    }

    private void restoreCurrentSnapshot1() {
        textProperty1.set(currentSnapshot1.getText());
    }

    private void restoreSnapshot1(Snapshot snapshot) {
        textProperty1.set(snapshot.getText());
    }

    private Snapshot makeSnapshot1() {
        Snapshot temp = new Snapshot(textProperty1.get());
        history1.add(temp);
        return temp;
    }

    private void restoreLastSnapshot2() {
        textProperty2.set(history2.get(history2.size() - 1).getText());
    }

    private void restoreCurrentSnapshot2() {
        textProperty2.set(currentSnapshot2.getText());
    }

    private void restoreSnapshot2(Snapshot snapshot) {
        textProperty2.set(snapshot.getText());
    }

    private Snapshot makeSnapshot2() {
        Snapshot temp = new Snapshot(textProperty2.get());
        history2.add(temp);
        return temp;
    }

    private boolean historyContainsText1() {
        AtomicBoolean contains = new AtomicBoolean(false);

        history1.forEach(snapshot -> {
            if (snapshot.getText().equals(textProperty1.getValue())) contains.set(true);
        });

        return contains.get();
    }

    private boolean historyContainsText2() {
        AtomicBoolean contains = new AtomicBoolean(false);

        history2.forEach(snapshot -> {
            if (snapshot.getText().equals(textProperty2.getValue())) contains.set(true);
        });

        return contains.get();
    }

    @Override
    public Node getNode() {
        return this;
    }
}

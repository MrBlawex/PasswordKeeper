package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import passwordkeeper.PasswordKeeper;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

@SuppressWarnings("ALL")
public class ComboField extends AnchorPane implements SampleField, KryoSerializable, Initializable {

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


    private ImageView imageViewSee1;
    private ImageView imageViewCopy1;
    private ImageView imageViewSee2;
    private ImageView imageViewCopy2;
    private Image imageEye;
    private Image imageEyeClosed;
    private Image imageCopy;

    private StringProperty nameProperty1;
    private StringProperty textProperty1;
    private BooleanProperty isShowing1;
    private StringProperty nameProperty2;
    private StringProperty textProperty2;
    private BooleanProperty isShowing2;

    private ArrayList<Snapshot> history1;
    private ArrayList<Snapshot> history2;

    public ComboField() {
        nameProperty1 = new SimpleStringProperty("");
        textProperty1 = new SimpleStringProperty("");
        isShowing1 = new SimpleBooleanProperty(false);

        nameProperty2 = new SimpleStringProperty("");
        textProperty2 = new SimpleStringProperty("");
        isShowing2 = new SimpleBooleanProperty(false);

        Platform.runLater(() -> initializeHistory());

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
            Platform.runLater(() -> initializeHistory());
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

        Platform.runLater(() -> initializeHistory());

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
        importImage();
        tuningImageView();
        initializePane();
        initializeGraphics();
        initializeControl();
    }

    private void importImage() {
        InputStream inputStreamCopy = PasswordKeeper.class.getResourceAsStream("fxml/icons/copy.png");
        InputStream inputStreamEye = PasswordKeeper.class.getResourceAsStream("fxml/icons/eye.png");
        InputStream inputStreamEyeClosed = PasswordKeeper.class.getResourceAsStream("fxml/icons/eye-closed.png");

        imageCopy = new Image(inputStreamCopy);
        imageEye = new Image(inputStreamEye);
        imageEyeClosed = new Image(inputStreamEyeClosed);
    }

    private void tuningImageView() {
        imageViewCopy1 = new ImageView();
        imageViewSee1 = new ImageView();

        imageViewCopy2 = new ImageView();
        imageViewSee2 = new ImageView();

        imageViewCopy1.setFitHeight(24);
        imageViewCopy1.setFitWidth(24);

        imageViewCopy2.setFitHeight(24);
        imageViewCopy2.setFitWidth(24);

        imageViewSee1.setFitHeight(24);
        imageViewSee1.setFitWidth(24);

        imageViewSee2.setFitHeight(24);
        imageViewSee2.setFitWidth(24);

        imageViewCopy1.setImage(imageCopy);
        imageViewSee1.setImage(imageEye);

        imageViewCopy2.setImage(imageCopy);
        imageViewSee2.setImage(imageEye);
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
        copyToBuff1.setGraphic(imageViewCopy1);
        visibleText1.setGraphic(imageViewSee1);

        visibleText2.setGraphic(imageViewSee2);
        copyToBuff2.setGraphic(imageViewCopy2);

        if (isShowing1.getValue()) {
            showMask1();
        } else {
            hideMask1();
        }

        if (isShowing2.getValue()) {
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
            if (newValue) {
                showMask1();
            } else {
                hideMask1();
            }
        });

        isShowing2.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showMask2();
            } else {
                hideMask2();
            }
        });

        textField1.setContextMenu(new ContextMenu());
        passwordField1.setContextMenu(new ContextMenu());

        textField2.setContextMenu(new ContextMenu());
        passwordField2.setContextMenu(new ContextMenu());

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
        makeSnapshot1();
        history2 = new ArrayList<>();
        makeSnapshot2();

        PseudoClass pseudoClassNotSafe = PseudoClass.getPseudoClass("not-safe");
        PseudoClass pseudoClassSafe = PseudoClass.getPseudoClass("safe");
        Tooltip tooltipSafe = new Tooltip("Изменения сохранены");
        Tooltip tooltipNotSafe = new Tooltip("Изменения не сохранены");

        tooltipSafe.setContentDisplay(ContentDisplay.CENTER);
        tooltipNotSafe.setContentDisplay(ContentDisplay.CENTER);

        textField1.pseudoClassStateChanged(pseudoClassSafe, textProperty1.get().equals(history1.get(history1.size() - 1).text));
        passwordField1.pseudoClassStateChanged(pseudoClassSafe, textProperty1.get().equals(history1.get(history1.size() - 1).text));


        textField1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                makeSnapshot1();
                textField1.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField1.pseudoClassStateChanged(pseudoClassSafe, true);
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            }
        });

        passwordField1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                makeSnapshot1();
                textField1.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField1.pseudoClassStateChanged(pseudoClassSafe, true);
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            }
        });

        textProperty1.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(history1.get(history1.size() - 1).text)) {
                textField1.setTooltip(tooltipSafe);
                passwordField1.setTooltip(tooltipSafe);
            } else {
                textField1.setTooltip(tooltipNotSafe);
                passwordField1.setTooltip(tooltipNotSafe);
            }
            textField1.pseudoClassStateChanged(pseudoClassSafe, newValue.equals(history1.get(history1.size() - 1).text));
            passwordField1.pseudoClassStateChanged(pseudoClassSafe, newValue.equals(history1.get(history1.size() - 1).text));

            textField1.pseudoClassStateChanged(pseudoClassNotSafe, !newValue.equals(history1.get(history1.size() - 1).text));
            passwordField1.pseudoClassStateChanged(pseudoClassNotSafe, !newValue.equals(history1.get(history1.size() - 1).text));
        });

        textField2.pseudoClassStateChanged(pseudoClassSafe, textProperty2.get().equals(history2.get(history2.size() - 1).text));
        passwordField2.pseudoClassStateChanged(pseudoClassSafe, textProperty2.get().equals(history2.get(history2.size() - 1).text));


        textField2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                makeSnapshot2();
                textField2.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField2.pseudoClassStateChanged(pseudoClassSafe, true);
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            }
        });

        passwordField2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                makeSnapshot2();
                textField2.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField2.pseudoClassStateChanged(pseudoClassSafe, true);
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            }
        });

        textProperty2.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(history2.get(history2.size() - 1).text)) {
                textField2.setTooltip(tooltipSafe);
                passwordField2.setTooltip(tooltipSafe);
            } else {
                textField2.setTooltip(tooltipNotSafe);
                passwordField2.setTooltip(tooltipNotSafe);
            }
            textField2.pseudoClassStateChanged(pseudoClassSafe, newValue.equals(history2.get(history2.size() - 1).text));
            passwordField2.pseudoClassStateChanged(pseudoClassSafe, newValue.equals(history2.get(history2.size() - 1).text));

            textField2.pseudoClassStateChanged(pseudoClassNotSafe, !newValue.equals(history2.get(history2.size() - 1).text));
            passwordField2.pseudoClassStateChanged(pseudoClassNotSafe, !newValue.equals(history2.get(history2.size() - 1).text));
        });
    }

    private void hideMask1() {
        imageViewSee1.setImage(imageEye);
    }

    private void showMask1() {
        imageViewSee1.setImage(imageEyeClosed);
    }

    private void hideMask2() {
        imageViewSee2.setImage(imageEye);
    }

    private void showMask2() {
        imageViewSee2.setImage(imageEyeClosed);
    }


    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(nameProperty1.getValue());
        output.writeBoolean(isShowing1.get());
        kryo.writeClassAndObject(output, history1);

        output.writeString(nameProperty2.getValue());
        output.writeBoolean(isShowing2.get());
        kryo.writeClassAndObject(output, history2);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        nameProperty1.setValue(input.readString());
        isShowing1.set(input.readBoolean());
        history1 = (ArrayList<Snapshot>) kryo.readClassAndObject(input);

        nameProperty2.setValue(input.readString());
        isShowing2.set(input.readBoolean());
        history2 = (ArrayList<Snapshot>) kryo.readClassAndObject(input);

        restoreLastSnapshot1();
        restoreLastSnapshot2();
    }

    private void restoreLastSnapshot1() {
        textProperty1.set(history1.get(history1.size() - 1).text);
    }

    private void restoreSnapshot1(Snapshot snapshot) {
        textProperty1.set(snapshot.text);
    }

    private void makeSnapshot1() {
        history1.add(new Snapshot(textProperty1.get()));
    }

    private void restoreLastSnapshot2() {
        textProperty2.set(history2.get(history2.size() - 1).text);
    }

    private void restoreSnapshot2(Snapshot snapshot) {
        textProperty2.set(snapshot.text);
    }

    private void makeSnapshot2() {
        history2.add(new Snapshot(textProperty2.get()));
    }

    private static class Snapshot implements KryoSerializable {

        private String text;
        private Date date;

        Snapshot() {
        }

        Snapshot(String text) {
            this.text = text;
            this.date = new Date();
        }

        public String getText() {
            return text;
        }

        public String getDate() {
            return new SimpleDateFormat().format(date);
        }

        @Override
        public void write(Kryo kryo, Output output) {
            output.writeString(text);
            kryo.writeClassAndObject(output, date);
        }

        @Override
        public void read(Kryo kryo, Input input) {
            text = input.readString();
            date = (Date) kryo.readClassAndObject(input);
        }
    }
}

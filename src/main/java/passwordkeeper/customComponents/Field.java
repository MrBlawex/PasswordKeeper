package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

public class Field extends AnchorPane implements SampleField, KryoSerializable, Initializable {

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

    private ImageView imageViewSee;
    private ImageView imageViewCopy;
    private Image imageEye;
    private Image imageEyeClosed;
    private Image imageCopy;

    private StringProperty nameProperty;
    private StringProperty textProperty;
    private BooleanProperty isShowing;

    private ArrayList<Snapshot> history;

    public Field() {
        nameProperty = new SimpleStringProperty("");
        textProperty = new SimpleStringProperty("");
        isShowing = new SimpleBooleanProperty();

        history = new ArrayList<>();
        makeSnapshot();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Field.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Field(String name) {
        nameProperty = new SimpleStringProperty(name);
        textProperty = new SimpleStringProperty("");
        isShowing = new SimpleBooleanProperty(false);

        history = new ArrayList<>();
        makeSnapshot();

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

        history = new ArrayList<>();
        makeSnapshot();

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
        importImage();
        tuningImageView();
        initializePane();
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
        imageViewCopy = new ImageView();
        imageViewSee = new ImageView();

        imageViewCopy.setFitHeight(22);
        imageViewCopy.setFitWidth(22);

        imageViewSee.setFitHeight(22);
        imageViewSee.setFitWidth(22);

        imageViewCopy.setImage(imageCopy);
        imageViewSee.setImage(imageEye);
    }

    private void initializePane() {
        label.textProperty().bindBidirectional(nameProperty);
        textField.textProperty().bindBidirectional(textProperty);
        visibleText.selectedProperty().bindBidirectional(isShowing);

        textField.setManaged(false);
        textField.setVisible(false);

        textField.managedProperty().bind(isShowing.not());
        textField.visibleProperty().bind(isShowing.not());

        passwordField.managedProperty().bind(isShowing);
        passwordField.visibleProperty().bind(isShowing);

        textField.textProperty().bindBidirectional(passwordField.textProperty());

        copyToBuff.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && !textField.getText().isEmpty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(passwordField.getText());
                clipboard.setContents(stringSelection, null);
            }
        });

        initializeGraphics();
        initializeControl();
    }

    private void initializeGraphics() {
        PseudoClass pseudoClassNotSafe = PseudoClass.getPseudoClass("not-safe");
        PseudoClass pseudoClassSafe = PseudoClass.getPseudoClass("safe");
        Tooltip tooltipSafe = new Tooltip("Изменения сохранены");
        Tooltip tooltipNotSafe = new Tooltip("Изменения не сохранены");

        tooltipSafe.setContentDisplay(ContentDisplay.CENTER);
        tooltipNotSafe.setContentDisplay(ContentDisplay.CENTER);

        textField.pseudoClassStateChanged(pseudoClassSafe, textProperty.get().equals(history.get(history.size() - 1).text));
        passwordField.pseudoClassStateChanged(pseudoClassSafe, textProperty.get().equals(history.get(history.size() - 1).text));



        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                makeSnapshot();
                textField.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField.pseudoClassStateChanged(pseudoClassSafe, true);
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                makeSnapshot();
                textField.pseudoClassStateChanged(pseudoClassSafe, true);
                passwordField.pseudoClassStateChanged(pseudoClassSafe, true);
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            }
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(history.get(history.size() - 1).text)) {
                textField.setTooltip(tooltipSafe);
                passwordField.setTooltip(tooltipSafe);
            }else {
                textField.setTooltip(tooltipNotSafe);
                passwordField.setTooltip(tooltipNotSafe);
            }
            textField.pseudoClassStateChanged(pseudoClassSafe, newValue.equals(history.get(history.size() - 1).text));
            passwordField.pseudoClassStateChanged(pseudoClassSafe, newValue.equals(history.get(history.size() - 1).text));

            textField.pseudoClassStateChanged(pseudoClassNotSafe, !newValue.equals(history.get(history.size() - 1).text));
            passwordField.pseudoClassStateChanged(pseudoClassNotSafe, !newValue.equals(history.get(history.size() - 1).text));
        });

        copyToBuff.setGraphic(imageViewCopy);

        label.widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setRightAnchor(fields_box, btn_box.getPrefWidth() + 36);
            AnchorPane.setLeftAnchor(fields_box, newValue.doubleValue() + 14);
        });
    }

    private void initializeControl() {
        isShowing.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showMask();
            } else {
                hideMask();
            }
        });

        textField.setContextMenu(new ContextMenu());
        passwordField.setContextMenu(new ContextMenu());

        MenuItem itemRenameLB = new MenuItem("Переименовать");
        itemRenameLB.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(label.getText());
            dialog.setTitle(NAME_PROGRAM + " - " + "Переименование поля");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите текст: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> label.setText(s));
            makeSnapshot();
        });

        ContextMenu menu = new ContextMenu(itemRenameLB);

        label.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void hideMask() {
        imageViewSee.setImage(imageEye);
    }

    private void showMask() {
        imageViewSee.setImage(imageEyeClosed);
    }


    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(nameProperty.getValue());
        output.writeBoolean(isShowing.get());
        kryo.writeClassAndObject(output, history);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        nameProperty.setValue(input.readString());
        isShowing.set(input.readBoolean());
        history = new ArrayList<>((ArrayList<Snapshot>) kryo.readClassAndObject(input));
        restoreLastSnapshot();
    }

    private void restoreLastSnapshot() {
        textProperty.set(history.get(history.size() - 1).text);
    }

    private void restoreSnapshot(Snapshot snapshot) {
        textProperty.set(snapshot.text);
    }

    private void makeSnapshot() {
        history.add(new Snapshot(textProperty.get()));
    }

    private class Snapshot {

        private String text;

        Snapshot(String text) {
            this.text = text;
        }
    }
}

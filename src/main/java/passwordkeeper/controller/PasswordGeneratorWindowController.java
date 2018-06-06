package passwordkeeper.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import passwordkeeper.PasswordKeeper;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class PasswordGeneratorWindowController implements Initializable {

    BooleanProperty mode = new SimpleBooleanProperty(false);
    DoubleProperty maxDoubleProperty = new SimpleDoubleProperty(0);
    @FXML
    private CheckBox cb_upperCaseChar;

    @FXML
    private CheckBox cb_avoidRepetition;

    @FXML
    private CheckBox cb_symbols;

    @FXML
    private Button btn_generate;

    @FXML
    private VBox showPasswords;

    @FXML
    private Slider slider_characters;

    @FXML
    private Label lb_minSl;

    @FXML
    private CheckBox cb_digits;

    @FXML
    private AnchorPane showGenerated;

    @FXML
    private VBox passwords;

    @FXML
    private CheckBox cb_lowerCaseChar;

    @FXML
    private TextField tx_characters;

    @FXML
    private Label lb_maxSl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePane();
    }

    @FXML
    public void generate(ActionEvent event) {
        passwords.getChildren().clear();
        ArrayList<Character> tempCharacters = new ArrayList<>();

        if (cb_digits.isSelected()) tempCharacters.addAll(getDigit());
        if (cb_lowerCaseChar.isSelected()) tempCharacters.addAll(getlowerCase());
        if (cb_upperCaseChar.isSelected()) tempCharacters.addAll(getUpperCase());
        if (cb_symbols.isSelected()) tempCharacters.addAll(getSymbols());

        for (int i = 0; i < 8; i++) {
            SmallField tempSmallField = new SmallField(generatePassword(tempCharacters, (int) slider_characters.getValue(), cb_avoidRepetition.isSelected()));
            passwords.getChildren().add(tempSmallField.getAnchorPane());
        }

    }

    private String generatePassword(ArrayList<Character> characters, int capacity, boolean repatition) {
        Random random = new Random();
        ArrayList<Character> tempArrayList = (ArrayList<Character>) characters.clone();
        StringBuilder builder = new StringBuilder();

        if (!repatition) {
            for (int i = 0; i < capacity; i++) {
                builder.append(tempArrayList.get(random.nextInt(tempArrayList.size())));
            }
        }
        if (repatition) {
            for (int i = 0; i < capacity; i++) {
                int temp = random.nextInt(tempArrayList.size());
                builder.append(tempArrayList.get(temp));
                tempArrayList.remove(temp);
            }
        }

        return builder.toString();
    }

    private void initializePane() {
        tx_characters.setText(String.valueOf((int) slider_characters.getValue()));
        lb_minSl.setText(String.valueOf((int) slider_characters.getMin()));
        lb_maxSl.setText(String.valueOf((int) slider_characters.getMax()));


        cb_symbols.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                maxDoubleProperty.setValue(maxDoubleProperty.get() + getSymbols().size());
                slider_characters.setMin(1);
                if (slider_characters.getValue() == 1) slider_characters.setValue(8);
            } else {
                maxDoubleProperty.setValue(maxDoubleProperty.get() - getSymbols().size());
            }
        });
        cb_upperCaseChar.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                maxDoubleProperty.setValue(maxDoubleProperty.get() + getUpperCase().size());
                slider_characters.setMin(1);
                if (slider_characters.getValue() == 1) slider_characters.setValue(8);
            } else {
                maxDoubleProperty.setValue(maxDoubleProperty.get() - getUpperCase().size());
            }
        });
        cb_lowerCaseChar.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                maxDoubleProperty.setValue(maxDoubleProperty.get() + getlowerCase().size());
                slider_characters.setMin(1);
                if (slider_characters.getValue() == 1) slider_characters.setValue(8);
            } else {
                maxDoubleProperty.setValue(maxDoubleProperty.get() - getlowerCase().size());
            }
        });
        cb_digits.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                maxDoubleProperty.setValue(maxDoubleProperty.get() + getDigit().size());
                slider_characters.setMin(1);
                if (slider_characters.getValue() == 1) slider_characters.setValue(8);
            } else {
                maxDoubleProperty.setValue(maxDoubleProperty.get() - getDigit().size());
            }
        });

        cb_avoidRepetition.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                slider_characters.maxProperty().bind(maxDoubleProperty);
            }
            if (!newValue) {
                slider_characters.maxProperty().unbind();
                slider_characters.setMax(70);
                slider_characters.setMin(1);
                if (slider_characters.getValue() == 1) slider_characters.setValue(8);
            }
        });

        slider_characters.maxProperty().addListener((observable, oldValue, newValue) -> lb_maxSl.setText(String.valueOf(newValue.intValue())));
        slider_characters.minProperty().addListener((observable, oldValue, newValue) -> lb_minSl.setText(String.valueOf(newValue.intValue())));

        btn_generate.disableProperty().bind(cb_digits.selectedProperty().or(cb_upperCaseChar.selectedProperty().or(cb_lowerCaseChar.selectedProperty())).not());

        tx_characters.editableProperty().bind(mode);
        tx_characters.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                mode.set(true);
            }
        });

        tx_characters.textProperty().addListener((observable, oldValue, newValue) -> {
            int value = Integer.valueOf(newValue);
            if (slider_characters.getMax() >= value && slider_characters.getMin() <= value) {
                tx_characters.setStyle("-fx-background-color: white");
            } else {
                tx_characters.setStyle("-fx-background-color: red");
            }
            if (!newValue.matches("[0-9]*")) tx_characters.setText(oldValue);
        });

        tx_characters.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER && !tx_characters.getText().isEmpty()) {
                int value = Integer.valueOf(tx_characters.getText());
                if (slider_characters.getMax() >= value && slider_characters.getMin() <= value) {
                    mode.set(false);
                    slider_characters.setValue(Double.valueOf(tx_characters.getText()));
                }
            }
        });

        slider_characters.valueProperty().addListener((observable, oldValue, newValue) -> {
            tx_characters.setText(String.valueOf(newValue.intValue()));
            slider_characters.setValue((int) Math.ceil(newValue.doubleValue()));
        });
    }

    private ArrayList<Character> getDigit() {
        ArrayList<Character> temp = new ArrayList<>();

        for (int i = 48; i < 58; i++) temp.add((char) i);

        return temp;
    }

    private ArrayList<Character> getUpperCase() {
        ArrayList<Character> temp = new ArrayList<>();

        for (int i = 65; i < 91; i++) temp.add((char) i);

        return temp;
    }

    private ArrayList<Character> getlowerCase() {
        ArrayList<Character> temp = new ArrayList<>();

        for (int i = 97; i < 123; i++) temp.add((char) i);

        return temp;
    }

    private ArrayList<Character> getSymbols() {
        ArrayList<Character> temp = new ArrayList<>();

        temp.add((char) 37);
        temp.add((char) 42);
        temp.add((char) 63);
        temp.add((char) 64);
        temp.add((char) 35);
        temp.add((char) 36);
        temp.add((char) 126);
        temp.add((char) 95);

        return temp;
    }

    private class SmallField {

        private AnchorPane anchorPane;
        private TextField textField;
        private ImageView imageViewCopy;
        private Button copyToBuff;

        private SmallField(String text) {
            this.textField = new TextField(text);
            this.imageViewCopy = new ImageView();
            this.imageViewCopy.setFitHeight(24);
            this.imageViewCopy.setFitWidth(24);
            this.imageViewCopy.setImage(new Image(PasswordKeeper.class.getResourceAsStream("fxml/icons/copy.png")));
            this.copyToBuff = new Button();
            this.copyToBuff.setGraphic(this.imageViewCopy);
            this.copyToBuff.setOnMouseClicked((event) -> {
                if (event.getButton() == MouseButton.PRIMARY && !this.textField.getText().isEmpty()) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection stringSelection = new StringSelection(this.textField.getText());
                    clipboard.setContents(stringSelection, null);
                }
            });

            this.anchorPane = new AnchorPane(textField, copyToBuff);

            AnchorPane.setLeftAnchor(this.textField, 5.0);
            AnchorPane.setTopAnchor(this.textField, 5.0);
            AnchorPane.setBottomAnchor(this.textField, 5.0);
            AnchorPane.setRightAnchor(this.textField, 60.0);

            AnchorPane.setTopAnchor(this.copyToBuff, 5.0);
            AnchorPane.setBottomAnchor(this.copyToBuff, 5.0);
            AnchorPane.setRightAnchor(this.copyToBuff, 5.0);

            this.anchorPane.setPrefWidth(300);
        }

        private AnchorPane getAnchorPane() {
            return this.anchorPane;
        }
    }
}

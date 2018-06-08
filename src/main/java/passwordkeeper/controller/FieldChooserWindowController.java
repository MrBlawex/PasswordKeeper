package passwordkeeper.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import passwordkeeper.customComponents.ComboField;
import passwordkeeper.customComponents.Field;
import passwordkeeper.customComponents.SampleField;
import passwordkeeper.storage.FileOfStorage;

import java.net.URL;
import java.util.ResourceBundle;

public class FieldChooserWindowController implements Initializable {

    @FXML
    private CheckBox cb_saveText;

    @FXML
    private ListView<FieldWrapper> lv_fields;

    @FXML
    private Button btn_add;

    @FXML
    private VBox vbox_previewer;

    @FXML
    private HBox hbox_controls;

    private ObservableList<FieldWrapper> list;
    private FileOfStorage fileOfStorage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list = lv_fields.getItems();

        list.add(new FieldWrapper("Простое поле", new Field("Test", "", false)));
        list.add(new FieldWrapper("Комбинированное поле", new ComboField("Test1", "", "Test2", "", false)));

        initializePane();
    }

    private void initializePane() {
        hbox_controls.setDisable(true);

        lv_fields.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            vbox_previewer.getChildren().setAll(newValue.getFieldNode());
            hbox_controls.setDisable(false);
        });
    }

    @FXML
    public void addField(MouseEvent mouseEvent) {
        if (cb_saveText.isSelected()) {
            Node tempNode = vbox_previewer.getChildren().get(0);

            if (tempNode instanceof Field) {
                fileOfStorage.addField(new Field((Field) tempNode));
            }
            if (tempNode instanceof ComboField) {
                fileOfStorage.addField(new ComboField((ComboField) tempNode));
            }
        }
        if (!cb_saveText.isSelected()) {
            Node tempNode = vbox_previewer.getChildren().get(0);

            if (tempNode instanceof Field) {
                fileOfStorage.addField(new Field("newField", "", true));
            }
            if (tempNode instanceof ComboField) {
                fileOfStorage.addField(new ComboField());
            }
        }

        ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
    }

    void setFileOfStorage(FileOfStorage fileOfStorage) {
        this.fileOfStorage = fileOfStorage;
    }

    private class FieldWrapper {

        private String nameField;
        private SampleField sampleField;

        FieldWrapper(String nameField, SampleField sampleField) {
            this.nameField = nameField;
            this.sampleField = sampleField;
        }

        public SampleField getSampleField() {
            return sampleField;
        }

        Node getFieldNode() {
            if (sampleField instanceof Field) return (Field) sampleField;
            if (sampleField instanceof ComboField) return (ComboField) sampleField;
            return null;
        }

        @Override
        public String toString() {
            return nameField;
        }
    }
}

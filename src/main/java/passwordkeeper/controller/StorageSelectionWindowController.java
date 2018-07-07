package passwordkeeper.controller;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import passwordkeeper.PasswordKeeper;
import passwordkeeper.storage.KeeperOfStorage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

public class StorageSelectionWindowController implements Initializable {

    @FXML
    public Button btn_createStorage;

    @FXML
    public Button btn_importStorage;

    @FXML
    private ListView<KeeperOfStorage> lv_storages;

    private ObservableList<KeeperOfStorage> list = FXCollections.observableArrayList();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        lv_storages.setItems(list);
        initialize();
    }

    @FXML
    public void createStorage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(PasswordKeeper.class.getResource("/fxml/StorageCreatorWindow.fxml"));

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            StorageCreatorWindowController creatorWindowController = loader.getController();
            creatorWindowController.setStorageSelectionWindowController(this);

            stage.setResizable(false);

            stage.setTitle(NAME_PROGRAM + " - " + "Создать хранилище");

            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public boolean importStorage(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle(NAME_PROGRAM + " - " + "Выбор файла хранилища");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PasswordKeeperStorage(*.pks)", "*.pks");
        fc.getExtensionFilters().add(filter);

        File selectedFile = fc.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                KeeperOfStorage temp = KeeperOfStorage.getKeeperFromFile(selectedFile);
                if (!list.contains(temp)) {
                    return list.add(temp);
                }
                return false;
            } catch (IOException ex) {
                return false;
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не выбран или ошибка чтения файла");
            alert.showAndWait();
            return false;
        }
    }

    void addToListOfKeepers(KeeperOfStorage keeper) {
        list.add(keeper);
    }

    private void initialize() {
        ContextMenu cm = new ContextMenu();
        MenuItem remove = new MenuItem("Удалить");
        remove.setOnAction((event) -> list.remove(lv_storages.getSelectionModel().getSelectedItem()));
        cm.getItems().add(remove);
        lv_storages.setContextMenu(cm);

        lv_storages.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !lv_storages.getSelectionModel().isEmpty()) {
                try {
                    FXMLLoader loader = new FXMLLoader(PasswordKeeper.class.getResource("/fxml/LoginFormWindow.fxml"));

                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();

                    LoginFormWindowController lfc = loader.getController();
                    lfc.setKeeperOfStorage(lv_storages.getSelectionModel().getSelectedItem());
                    lfc.setStorageSelectionWindow(((Node) event.getSource()).getScene().getWindow());

                    stage.setResizable(false);

                    stage.initOwner(lv_storages.getScene().getWindow());
                    stage.initModality(Modality.WINDOW_MODAL);

                    stage.setTitle(NAME_PROGRAM);

                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            getList();
        } catch (FileNotFoundException e) {
            saveList();
        }

        list.addListener((ListChangeListener<? super KeeperOfStorage>) c -> saveList());

        showDialogForDelete(getNotFoundFiles());
    }

    private void saveList() {
        try {
            Kryo kryo = new Kryo();
            Output output = new Output(new FileOutputStream(new File("src/main/resources/list.kryo")));
            kryo.writeClassAndObject(output, new ArrayList<>(list));
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getList() throws FileNotFoundException {
        Kryo kryo = new Kryo();
        Input input = new Input(new FileInputStream(new File("src/main/resources/list.kryo")));
        list.setAll((ArrayList<KeeperOfStorage>) kryo.readClassAndObject(input));
        input.close();
    }

    private ArrayList<KeeperOfStorage> getNotFoundFiles() {
        ArrayList<KeeperOfStorage> listForDelete = new ArrayList<>();

        list.forEach(keeper -> {
            if (!keeper.checkExistsFile()) listForDelete.add(keeper);
        });

        return listForDelete;
    }

    private void showDialogForDelete(ArrayList<KeeperOfStorage> listForRemove) {
        if (!listForRemove.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Удаление");
            alert.setHeaderText("");
            alert.setContentText("Есть несуществующие файлы по ссылкам в списке, удалить?");

            ButtonType buttonTypeOne = new ButtonType("Удалить");
            ButtonType buttonTypeTwo = new ButtonType("Оставить");

            alert.getButtonTypes().addAll(buttonTypeOne, buttonTypeTwo);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == buttonTypeOne) {
                System.out.println("twewa");
            }
        }
    }
}

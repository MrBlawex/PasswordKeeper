package passwordkeeper.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import passwordkeeper.PasswordKeeper;
import passwordkeeper.customComponents.ItemTreeCell;
import passwordkeeper.storage.FileOfStorage;
import passwordkeeper.storage.FolderOfStorage;
import passwordkeeper.storage.Item;
import passwordkeeper.storage.KeeperOfStorage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static passwordkeeper.PasswordKeeper.NAME_PROGRAM;

public class StorageManagerWindowController implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem exit;

    @FXML
    private TextField tf_search;

    @FXML
    private TreeView<Item> treeView;

    @FXML
    private AnchorPane ach_leftBar;

    @FXML
    private AnchorPane ach_window;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button search_btn;

    private KeeperOfStorage keeperOfStorage;
    private Window storageSelectionWindow = null;
    private Window thisWindow = null;
    private VBox mainView = null;

    private ObjectProperty<FileOfStorage> tempFile = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            if (storageSelectionWindow != null) {
                storageSelectionWindow.hide();
            }
            if (keeperOfStorage.getStorage() != null) {
                setToTreeView(keeperOfStorage.getStorage().buildTreeItem());
            }
        });
        initializeHomeWindow();
    }

    @FXML
    public void changeStorageOrExit() {
        ((Stage) storageSelectionWindow).show();
        thisWindow.hide();
    }

    /**
     * Сохраняет хранилище в файл
     */
    @FXML
    public void saveChanges() {
        try {
            keeperOfStorage.saveStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setToTreeView(TreeItem<Item> root) {
        treeView.setRoot(root);
    }

    /**
     * Используется для передачи хранилища из другого окна
     *
     * @param keeperOfStorage
     */
    void setKeeperOfStorage(KeeperOfStorage keeperOfStorage) {
        this.keeperOfStorage = keeperOfStorage;
    }

    /**
     * Используется для передачи ссылки на окно выбора хранилища
     *
     * @param storageSelectionWindow
     */
    void setStorageSelectionWindow(Window storageSelectionWindow) {
        this.storageSelectionWindow = storageSelectionWindow;
    }

    /**
     * Используется для передачи ссылки на главное окно
     *
     * @param thisWindow
     */
    void setThisWindow(Window thisWindow) {
        this.thisWindow = thisWindow;
    }

    /**
     *
     */
    private void initializeHomeWindow() {
        scrollPane.prefWidthProperty().bind(ach_window.widthProperty().multiply(2.0).divide(3.0));
        ach_leftBar.prefWidthProperty().bind(ach_window.widthProperty().subtract(scrollPane.widthProperty()).subtract(5.0));

        treeView.setCellFactory(param -> {
            ItemTreeCell itemTreeCell = new ItemTreeCell() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        setText(item.toString());
                        setGraphic(getTreeItem().getGraphic());
                    } else {
                        setText(null);
                        setGraphic(null);
                    }

                    if (item instanceof FolderOfStorage && getTreeItem().getParent() == null) {
                        setContextMenu(makeMenuForHeadFolder());
                    }
                    if (item instanceof FolderOfStorage && getTreeItem().getParent() != null) {
                        setContextMenu(makeMenuForFolder());
                    }
                    if (item instanceof FileOfStorage) {
                        setContextMenu(makeMenuForFile());
                    }
                }
            };

            itemTreeCell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    try {
                        Item temp = itemTreeCell.getTreeItem().getValue();
                        if (temp instanceof FolderOfStorage) {
                            itemTreeCell.getTreeItem().setExpanded(!itemTreeCell.getTreeItem().isExpanded());
                        }
                        if (temp instanceof FileOfStorage) {
                            tempFile.setValue((FileOfStorage) temp);
                        }
                    } catch (NullPointerException ex) {
                        itemTreeCell.getTreeView().getSelectionModel().clearSelection();
                        event.consume();
                    }
                }
            });

            return itemTreeCell;
        });

        tempFile.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) Bindings.unbindContentBidirectional(oldValue.getFields(), mainView.getChildren());
            mainView = new VBox();
            mainView.getStyleClass().add("homeVBox");
            mainView.getChildren().setAll(newValue.getFields());
            Bindings.bindContentBidirectional(newValue.getFields(), mainView.getChildren());
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(mainView);
        });
    }

    private ContextMenu makeMenuForHeadFolder() {
        MenuItem makeFolder = new MenuItem("Создать папку");
        makeFolder.setOnAction((event) -> {
            TextInputDialog dialog = new TextInputDialog("newFolder");
            dialog.setTitle(NAME_PROGRAM + " - " + "Создание папки");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите название: ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                Item temp = treeView.getSelectionModel().getSelectedItem().getValue();
                FolderOfStorage tempFolderOfStorage = (FolderOfStorage) temp;
                tempFolderOfStorage.addNewFolder(result.get());
            }
        });
        MenuItem makeFile = new MenuItem("Создать файл");
        makeFile.setOnAction((event) -> {
            TextInputDialog dialog = new TextInputDialog("newFile");
            dialog.setTitle(NAME_PROGRAM + " - " + "Создание файла");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите название: ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                Item temp = treeView.getSelectionModel().getSelectedItem().getValue();
                FolderOfStorage tempFolderOfStorage = (FolderOfStorage) temp;
                tempFolderOfStorage.addNewFile(result.get());
            }
        });
        return new ContextMenu(makeFolder, makeFile);
    }

    private ContextMenu makeMenuForFolder() {
        MenuItem makeFolder = new MenuItem("Создать папку");
        makeFolder.setOnAction((event) -> {
            TextInputDialog dialog = new TextInputDialog("newFolder");
            dialog.setTitle(NAME_PROGRAM + " - " + "Создание папки");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите название: ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                Item temp = treeView.getSelectionModel().getSelectedItem().getValue();
                FolderOfStorage tempFolderOfStorage = (FolderOfStorage) temp;
                tempFolderOfStorage.addNewFolder(result.get());
            }
        });

        MenuItem makeFile = new MenuItem("Создать файл");
        makeFile.setOnAction((event) -> {
            TextInputDialog dialog = new TextInputDialog("newFile");
            dialog.setTitle(NAME_PROGRAM + " - " + "Создание файла");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите название: ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                Item temp = treeView.getSelectionModel().getSelectedItem().getValue();
                FolderOfStorage tempFolderOfStorage = (FolderOfStorage) temp;
                tempFolderOfStorage.addNewFile(result.get());
            }
        });

        MenuItem rename = new MenuItem("Переименовать");
        rename.setOnAction((event) -> {
            TextInputDialog dialog = new TextInputDialog(treeView.getSelectionModel().getSelectedItem().getValue().getName());
            dialog.setTitle(NAME_PROGRAM + " - " + "Переименование папки");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите название: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                treeView.getSelectionModel().getSelectedItem().getValue().renameItem(s);
                treeView.refresh();
            });
        });

        MenuItem copy = new MenuItem("Копировать");
        MenuItem insert = new MenuItem("Вставить");
        MenuItem delete = new MenuItem("Удалить");
        delete.setOnAction((event) -> {
            FolderOfStorage parentFolderOfStorage = (FolderOfStorage) treeView.getSelectionModel().getSelectedItem().getParent().getValue();
            try {
                parentFolderOfStorage.removeChild(treeView.getSelectionModel().getSelectedItem().getValue(), keeperOfStorage.getStorage());
            } catch (Error ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(NAME_PROGRAM + " - " + "Ошибка");
                alert.setHeaderText("Папку нельзя удалить!");
                alert.setContentText("Включён режим безопасности, его можно отключить в настройках");
                alert.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        return new ContextMenu(makeFolder, makeFile, rename, copy, insert, delete);
    }

    private ContextMenu makeMenuForFile() {
        MenuItem insertField = new MenuItem("Добавить поле");
        insertField.setOnAction((event) -> {
            FileOfStorage tempFileOfStorage = (FileOfStorage) treeView.getSelectionModel().getSelectedItem().getValue();

            try {
                FXMLLoader loader = new FXMLLoader(PasswordKeeper.class.getResource("/fxml/FieldChooserWindow.fxml"));

                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();

                FieldChooserWindowController fieldChooser = loader.getController();
                fieldChooser.setFileOfStorage(tempFileOfStorage);

                stage.setMinHeight(300);
                stage.setMinWidth(660);

                stage.setMaxHeight(500);
                stage.setMaxWidth(800);

                stage.initOwner(thisWindow);
                stage.initModality(Modality.WINDOW_MODAL);

                stage.setTitle(NAME_PROGRAM + " - " + "Выбор нового поля");

                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        MenuItem rename = new MenuItem("Переименовать");
        rename.setOnAction((event) -> {
            TextInputDialog dialog = new TextInputDialog(treeView.getSelectionModel().getSelectedItem().getValue().getName());
            dialog.setTitle(NAME_PROGRAM + " - " + "Переименование файла");
            dialog.setHeaderText("");
            dialog.setGraphic(null);
            dialog.setContentText("Введите название: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                treeView.getSelectionModel().getSelectedItem().getValue().renameItem(s);
                treeView.refresh();
            });
        });

        MenuItem copy = new MenuItem("Копировать");
        MenuItem delete = new MenuItem("Удалить");
        delete.setOnAction((event) -> {
            FolderOfStorage parentFolderOfStorage = (FolderOfStorage) treeView.getSelectionModel().getSelectedItem().getParent().getValue();
            try {
                parentFolderOfStorage.removeChild(treeView.getSelectionModel().getSelectedItem().getValue(), keeperOfStorage.getStorage());
            } catch (Error ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(NAME_PROGRAM + " - " + "Ошибка");
                alert.setHeaderText("Файл нельзя удалить!");
                alert.setContentText("Включён режим безопасности, его можно отключить в настройках");
                alert.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        return new ContextMenu(insertField, rename, copy, delete);
    }

    @FXML
    public void showGenerator(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(PasswordKeeper.class.getResource("/fxml/PasswordGeneratorWindow.fxml"));

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            stage.setResizable(false);

            stage.initOwner(thisWindow);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle(NAME_PROGRAM + " - " + "Генератор паролей");

            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

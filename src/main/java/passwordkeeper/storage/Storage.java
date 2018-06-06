package passwordkeeper.storage;

import javafx.scene.control.TreeItem;

import java.util.Objects;

public class Storage {

    private String passwordOfStorage;
    private FolderOfStorage rootFolder;

    public Storage() {
    }

    public Storage(String passwordOfStorage) {
        this.rootFolder = new FolderOfStorage("Хранилище", true);
        this.passwordOfStorage = passwordOfStorage;
    }

    public String getPasswordOfStorage() {
        return passwordOfStorage;
    }

    /**
     * Создает дерево элементов
     *
     * @return TreeItem<>
     */
    public TreeItem<Item> buildTreeItem() {
        return rootFolder.getTreeItem();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Storage)) return false;
        Storage storage = (Storage) o;
        return Objects.equals(passwordOfStorage, storage.passwordOfStorage) &&
                Objects.equals(rootFolder, storage.rootFolder);
    }

    @Override
    public int hashCode() {

        return Objects.hash(passwordOfStorage, rootFolder);
    }
}

package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.scene.control.TreeItem;

import java.util.Objects;

public class Storage implements KryoSerializable {

    private String passwordOfStorage;
    private FolderOfStorage rootFolder;
    private Boolean safeMode = false;

    public Storage() {
    }

    Storage(String passwordOfStorage) {
        this.rootFolder = new FolderOfStorage("Storage");
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

    Boolean getSafeMode() {
        return safeMode;
    }

    public void setSafeMode(Boolean safeMode) {
        this.safeMode = safeMode;
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

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(passwordOfStorage);
        output.writeBoolean(safeMode);
        kryo.writeClassAndObject(output, rootFolder);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        passwordOfStorage = input.readString();
        safeMode = input.readBoolean();
        rootFolder = (FolderOfStorage) kryo.readClassAndObject(input);
    }
}

package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class Storage extends Observable implements KryoSerializable {

    private StringProperty passwordOfStorage;
    private FolderOfStorage rootFolder;
    private BooleanProperty safeMode;

    public Storage() {
    }

    Storage(String passwordOfStorage) {
        this.rootFolder = new FolderOfStorage("Storage");
        this.passwordOfStorage = new SimpleStringProperty(passwordOfStorage);
        this.safeMode = new SimpleBooleanProperty(false);
    }

    void setObserver(Observer observer) {
        this.addObserver(observer);
        rootFolder.setObserver(observer);
        initializeObserveData();
    }

    private void initializeObserveData() {
        safeMode.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });
        passwordOfStorage.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });
    }

    public String getPasswordOfStorage() {
        return passwordOfStorage.getValue();
    }

    public TreeItem<Item> buildTreeItem() {
        return rootFolder.getTreeItem();
    }

    boolean getSafeMode() {
        return safeMode.getValue();
    }

    void setSafeMode(Boolean safeMode) {
        this.safeMode.setValue(safeMode);
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
        output.writeString(passwordOfStorage.getValue());
        output.writeBoolean(safeMode.getValue());
        kryo.writeClassAndObject(output, rootFolder);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        passwordOfStorage = new SimpleStringProperty(input.readString());
        safeMode = new SimpleBooleanProperty(input.readBoolean());
        rootFolder = (FolderOfStorage) kryo.readClassAndObject(input);

        initializeObserveData();
    }
}

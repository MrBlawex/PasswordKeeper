package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.Observer;

public class FolderOfStorage extends Item implements KryoSerializable {

    private ObservableList<Item> items;
    private BooleanProperty isExpanded;
    private TreeItem<Item> temp;
    private Observer manager;

    public FolderOfStorage() {
    }

    FolderOfStorage(String name) {
        super(name);
        items = FXCollections.observableArrayList();
        isExpanded = new SimpleBooleanProperty(true);
        initializeTreeItem();
        initializeObserveData();
    }

    public void addNewFolder(String name) {
        FolderOfStorage temp = new FolderOfStorage(name);
        temp.setObserver(manager);

        items.add(temp);
    }

    public void addNewFile(String name) {
        FileOfStorage temp = new FileOfStorage(name);
        temp.setObserver(manager);

        items.add(temp);
    }

    public void addFolder(FolderOfStorage folder, int pos) {
        items.add(pos, folder);
    }

    public void addFile(FileOfStorage file, int pos) {
        items.add(pos, file);
    }

    public Item removeChild(Item item, Storage storage) {
        if (!storage.getSafeMode()) {
            items.remove(item);
            return item;
        } else {
            throw new Error();
        }
    }

    @Override
    public TreeItem<Item> getTreeItem() {
        return temp;
    }

    @Override
    public void setObserver(Observer observer) {
        manager = observer;
        this.addObserver(observer);
        items.forEach(item -> item.setObserver(observer));

        initializeObserveData();
    }

    private void initializeObserveData() {
        isExpanded.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });
        items.addListener((ListChangeListener<? super Item>) c -> {
            setChanged();
            notifyObservers();
        });
        name.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });
    }

    private void initializeTreeItem() {
        temp = new TreeItem<>(this, getIconFolder());
        temp.expandedProperty().bindBidirectional(this.isExpanded);

        items.forEach(item -> temp.getChildren().add(item.getTreeItem()));

        items.addListener((ListChangeListener<? super Item>) c -> {
            temp.getChildren().clear();
            items.forEach(item -> temp.getChildren().add(item.getTreeItem()));
        });
    }

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeClassAndObject(output, new ArrayList<>(items));
        output.writeString(name.getValue());
        output.writeBoolean(isExpanded.get());
    }

    @Override
    public void read(Kryo kryo, Input input) {
        items = FXCollections.observableArrayList((ArrayList<Item>) kryo.readClassAndObject(input));
        name = new SimpleStringProperty(input.readString());
        isExpanded = new SimpleBooleanProperty(input.readBoolean());

        initializeTreeItem();
        initializeObserveData();
    }
}

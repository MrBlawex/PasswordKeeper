package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import passwordkeeper.customComponents.SampleField;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observer;

public class FileOfStorage extends Item implements KryoSerializable {

    private ObservableList<SampleField> fields;
    private ObservableList<Node> fieldsOfNodes;
    private TreeItem<Item> temp;
    private Observer manager;

    public FileOfStorage() {
    }

    FileOfStorage(String name) {
        super(name);
        fields = FXCollections.observableArrayList();
        fieldsOfNodes = FXCollections.observableArrayList();
        temp = new TreeItem<>(this, getIconFile());

        initializeObserveData();
    }

    public void addField(SampleField field) {
        fieldsOfNodes.add(field.getNode());
        fields.add(field);
        if (manager != null) Platform.runLater(() -> field.setObserver(manager));
    }

    @Override
    public TreeItem<Item> getTreeItem() {
        return temp;
    }

    public ObservableList<Node> getFields() {
        return fieldsOfNodes;
    }

    @Override
    public void setObserver(Observer observer) {
        manager = observer;
        this.addObserver(observer);
        fields.forEach(sampleField -> sampleField.setObserver(observer));
    }

    private void initializeObserveData() {
        fields.addListener((ListChangeListener<? super SampleField>) c -> {
            setChanged();
            notifyObservers();
        });
        name.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileOfStorage)) return false;
        if (!super.equals(o)) return false;
        FileOfStorage that = (FileOfStorage) o;
        return Objects.equals(fields, that.fields) && Objects.equals(fieldsOfNodes, that.fieldsOfNodes) && Objects.equals(temp, that.temp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, fieldsOfNodes, temp);
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(name.getValue());
        kryo.writeClassAndObject(output, new ArrayList<>(fields));
    }

    @Override
    public void read(Kryo kryo, Input input) {
        name = new SimpleStringProperty(input.readString());
        fields = FXCollections.observableArrayList((ArrayList<SampleField>) kryo.readClassAndObject(input));
        fieldsOfNodes = FXCollections.observableArrayList();
        fields.forEach(sampleField -> fieldsOfNodes.add(sampleField.getNode()));
        temp = new TreeItem<>(this, getIconFile());

        initializeObserveData();
    }
}
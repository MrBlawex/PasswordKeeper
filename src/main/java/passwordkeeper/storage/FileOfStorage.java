package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import passwordkeeper.customComponents.SampleField;

import java.util.ArrayList;
import java.util.Objects;

public class FileOfStorage extends Item implements KryoSerializable {

    private ObservableList<SampleField> fields;
    private ObservableList<Node> fieldsOfNodes;

    public FileOfStorage() {
    }

    FileOfStorage(String name) {
        super(name);
        fields = FXCollections.observableArrayList();
        fieldsOfNodes = FXCollections.observableArrayList();
    }

    public Boolean addField(SampleField field) {
        fieldsOfNodes.add(field.getNode());
        return fields.add(field);
    }

    @Override
    public TreeItem<Item> getTreeItem() {
        return new TreeItem<>(this, getIconFile());
    }

    public ObservableList<Node> getFields() {
        return fieldsOfNodes;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(name);
        kryo.writeClassAndObject(output, new ArrayList<>(fields));
    }

    @Override
    public void read(Kryo kryo, Input input) {
        name = input.readString();
        fields = FXCollections.observableArrayList((ArrayList<SampleField>) kryo.readClassAndObject(input));
        fieldsOfNodes = FXCollections.observableArrayList();
        fields.forEach(sampleField -> fieldsOfNodes.add(sampleField.getNode()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileOfStorage)) return false;
        if (!super.equals(o)) return false;
        FileOfStorage that = (FileOfStorage) o;
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
}
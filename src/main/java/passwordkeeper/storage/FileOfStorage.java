package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import passwordkeeper.customComponents.ComboField;
import passwordkeeper.customComponents.Field;
import passwordkeeper.customComponents.SampleField;

import java.util.ArrayList;

public class FileOfStorage extends Item implements KryoSerializable {

    ArrayList<SampleField> fields;

    public FileOfStorage() {
    }

    public FileOfStorage(String name, Boolean safeMode) {
        super(name);
        fields = new ArrayList<>();
    }

    public Boolean addField(SampleField field) {
        return fields.add(field);
    }

    @Override
    public TreeItem<Item> getTreeItem() {
        return new TreeItem<>(this, getIconFile());
    }

    public ObservableList<Node> getFields() {
        ObservableList<Node> temp = FXCollections.observableArrayList();

        fields.forEach(sampleField -> {
            if (sampleField instanceof Field) temp.add((Field) sampleField);
            if (sampleField instanceof ComboField) temp.add((ComboField) sampleField);
        });

        return temp;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(name);
        kryo.writeClassAndObject(output, fields);
        output.flush();
    }

    @Override
    public void read(Kryo kryo, Input input) {
        name = input.readString();
        fields = (ArrayList<SampleField>) kryo.readClassAndObject(input);
    }
}
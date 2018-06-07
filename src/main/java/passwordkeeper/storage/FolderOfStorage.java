package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;


public class FolderOfStorage extends Item implements KryoSerializable {

    private ArrayList<Item> items;
    private BooleanProperty isExpanded;

    public FolderOfStorage() {
    }

    FolderOfStorage(String name) {
        super(name);
        items = new ArrayList<>();
        isExpanded = new SimpleBooleanProperty(true);
    }

    public Boolean addNewFolder(String name) {
        return items.add(new FolderOfStorage(name));
    }

    public Boolean addNewFile(String name, Boolean safeMode) {
        return items.add(new FileOfStorage(name, safeMode));
    }

    public Boolean removeChild(Item item, Storage storage) {
        if (!storage.getSafeMode()) {
            return items.remove(item);
        } else {
            throw new Error("Включён режим безопасности");
        }
    }

    @Override
    public TreeItem<Item> getTreeItem() {
        TreeItem<Item> temp = new TreeItem<>(this, getIconFolder());
        temp.expandedProperty().bindBidirectional(this.isExpanded);

        items.forEach(item -> temp.getChildren().add(item.getTreeItem()));

        return temp;
    }


    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeClassAndObject(output, items);
        output.writeString(name);
        output.writeBoolean(isExpanded.get());
        output.flush();
    }

    @Override
    public void read(Kryo kryo, Input input) {
        items = (ArrayList<Item>) kryo.readClassAndObject(input);
        name = input.readString();
        isExpanded = new SimpleBooleanProperty(input.readBoolean());
    }
}

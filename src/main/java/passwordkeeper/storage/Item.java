package passwordkeeper.storage;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

/**
 * Абстрактный класс который реализует стандартные методы работы с файлами
 *
 * @author Eduard
 */
public abstract class Item extends Observable {

    protected StringProperty name;

    Item() {
    }

    Item(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getName() {
        return name.getValue();
    }

    public void renameItem(String name) {
        this.name.setValue(name);
        setChanged();
        notifyObservers();
    }

    public abstract TreeItem<Item> getTreeItem();

    protected MaterialDesignIconView getIconFile() {
        return new MaterialDesignIconView(MaterialDesignIcon.FILE_OUTLINE);
    }

    protected MaterialDesignIconView getIconFolder() {
        return new MaterialDesignIconView(MaterialDesignIcon.FOLDER_OUTLINE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public abstract void setObserver(Observer observer);

    @Override
    public String toString() {
        return name.getValue();
    }
}

package passwordkeeper.storage;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.TreeItem;

import java.util.Objects;

/**
 * Абстрактный класс который реализует стандартные методы работы с файлами
 *
 * @author Eduard
 */
public abstract class Item {

    String name;

    Item() {
    }

    Item(String name) {
        this.name = name;
    }

    public void renameItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract TreeItem<Item> getTreeItem();

    protected MaterialDesignIconView getIconFile() {
        return new MaterialDesignIconView(MaterialDesignIcon.FILE_OUTLINE);
    }

    protected MaterialDesignIconView getIconFolder() {
        return new MaterialDesignIconView(MaterialDesignIcon.FOLDER_OUTLINE);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name);
    }
}

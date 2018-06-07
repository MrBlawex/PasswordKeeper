package passwordkeeper.storage;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import passwordkeeper.PasswordKeeper;

import java.io.InputStream;
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

    ImageView getIconFile() {
        InputStream inputStreamFile = PasswordKeeper.class.getResourceAsStream("fxml/icons/file.png");
        Image imageFile = new Image(inputStreamFile);
        ImageView t = new ImageView(imageFile);
        t.fitHeightProperty().setValue(24);
        t.fitWidthProperty().setValue(24);
        return t;
    }

    ImageView getIconFolder() {
        InputStream inputStreamFolder = PasswordKeeper.class.getResourceAsStream("fxml/icons/folder.png");
        Image imageFolder = new Image(inputStreamFolder);
        ImageView t = new ImageView(imageFolder);
        t.fitHeightProperty().setValue(24);
        t.fitWidthProperty().setValue(24);
        return t;
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

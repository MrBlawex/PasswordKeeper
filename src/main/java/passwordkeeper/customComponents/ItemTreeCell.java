package passwordkeeper.customComponents;

import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import passwordkeeper.storage.FolderOfStorage;
import passwordkeeper.storage.Item;

public class ItemTreeCell extends TreeCell<Item> {

    public ItemTreeCell() {
        setOnDragEntered(event -> {
            System.out.println(" Entered ");
            event.consume();
        });
        setOnDragDetected(event -> {
            System.out.println(" Detected ");
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("yeah");
            db.setContent(content);
            event.consume();
        });
        setOnDragDone(event -> {
            System.out.println(" Done ");
            event.consume();
        });
        setOnDragDropped(event -> {
            System.out.println(" Dropped ");
            event.setDropCompleted(true);
            event.consume();
        });
        setOnDragExited(event -> {
            System.out.println(" Exited ");
            event.consume();
        });
        setOnDragOver(event -> {
            System.out.println(" Over ");
            System.out.println("target: " + event.getGestureSource());
            if (acceptable(event)) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            setText(item.toString());
            setGraphic(getTreeItem().getGraphic());
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    private boolean acceptable(DragEvent event) {
        if (event.getDragboard().hasString()) {
            if (event.getGestureSource() instanceof FolderOfStorage) {
                return true;
            }
        }
        return false;
    }
}

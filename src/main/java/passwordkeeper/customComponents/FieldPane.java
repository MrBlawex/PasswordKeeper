package passwordkeeper.customComponents;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public abstract class FieldPane extends AnchorPane {

    protected void makeDraggable() {
        this.setOnDragDetected((event) -> {
            Dragboard db = this.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString("field-drop");
            db.setContent(content);

            db.setDragView(this.snapshot(null, null));
            db.setDragViewOffsetX(event.getX());
            db.setDragViewOffsetY(event.getY());
            event.consume();
        });

        //this.setOnDragEntered(event -> this.setStyle("-fx-background-color: red;"));

        //this.setOnDragExited(event -> this.setStyle(""));

        this.setOnDragOver(event -> {
            if (event.getDragboard().getString().equals("field-drop")) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        /*this.setOnDragDone(event -> {
            System.out.println("AnchorPane " + event.getDragboard().getString() + " moved");

            event.consume();
        });*/

        this.setOnDragDropped(event -> {
            if (!this.equals(event.getGestureSource())) {
                double height = getHeight();
                if (height / 2 > event.getY()) {
                    moveToUp((AnchorPane) event.getGestureSource(), this);
                }
                if (height / 2 < event.getY()) {
                    moveToDown((AnchorPane) event.getGestureSource(), this);
                }
            }
            if (event.getTransferMode() == TransferMode.MOVE) {
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
        });
    }

    private void moveToUp(Node nodeForMove, Node targetToMove) {
        ObservableList<Node> list = ((VBox) nodeForMove.getParent()).getChildren();

        if (list.contains(targetToMove)) {
            list.remove(nodeForMove);
            list.add(list.indexOf(targetToMove), nodeForMove);
        }
    }

    private void moveToDown(Node nodeForMove, Node targetToMove) {
        ObservableList<Node> list = ((VBox) nodeForMove.getParent()).getChildren();

        if (list.contains(targetToMove)) {
            list.remove(nodeForMove);
            list.add(list.indexOf(targetToMove) + 1, nodeForMove);
        }
    }
}

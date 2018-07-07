package passwordkeeper.customComponents;

import javafx.scene.Node;

import java.util.Observer;

public interface SampleField {
    void setObserver(Observer observer);

    Node getNode();
}

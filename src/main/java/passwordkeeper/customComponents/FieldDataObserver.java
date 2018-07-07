package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import passwordkeeper.storage.History;

import java.util.Observable;

public class FieldDataObserver extends Observable implements KryoSerializable, DataObserver {

    private StringProperty nameProperty;
    private StringProperty textProperty;
    private BooleanProperty isShowing;

    private History history;

    public FieldDataObserver() {
    }

    public FieldDataObserver(StringProperty nameProperty, StringProperty textProperty, BooleanProperty isShowing) {
        this.nameProperty = new SimpleStringProperty();
        this.textProperty = new SimpleStringProperty();
        this.isShowing = new SimpleBooleanProperty();

        history = new History(textProperty);
        bindAll(nameProperty, textProperty, isShowing);
    }

    public void bindAll(StringProperty nameProperty, StringProperty textProperty, BooleanProperty isShowing) {
        nameProperty.bindBidirectional(this.nameProperty);
        textProperty.bindBidirectional(this.textProperty);
        isShowing.bindBidirectional(this.isShowing);
        history.bindTextProperty(textProperty);

        initializeObserver();
    }

    public History getHistory() {
        return history;
    }

    @Override
    public void initializeObserver() {
        nameProperty.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });

        isShowing.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(nameProperty.getValue());
        output.writeString(textProperty.getValue());
        output.writeBoolean(isShowing.getValue());
        kryo.writeClassAndObject(output, history);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        nameProperty = new SimpleStringProperty(input.readString());
        textProperty = new SimpleStringProperty(input.readString());
        isShowing = new SimpleBooleanProperty(input.readBoolean());

        history = (History) kryo.readClassAndObject(input);
    }
}

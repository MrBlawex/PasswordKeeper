package passwordkeeper.customComponents;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Observable;

public class HeaderDataObserver extends Observable implements KryoSerializable, DataObserver {

    private StringProperty text;

    public HeaderDataObserver() {
    }

    public HeaderDataObserver(StringProperty property) {
        this.text = new SimpleStringProperty("");
        bindText(property);
    }

    void bindText(StringProperty property) {
        property.setValue(text.getValue());
        this.text.bindBidirectional(property);
        initializeObserver();
    }

    @Override
    public void initializeObserver() {
        text.addListener((observable, oldValue, newValue) -> {
            setChanged();
            notifyObservers();
        });
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(text.getValue());
    }

    @Override
    public void read(Kryo kryo, Input input) {
        text = new SimpleStringProperty(input.readString());
    }
}

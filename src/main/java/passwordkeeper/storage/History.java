package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Observable;

public class History extends Observable implements KryoSerializable {

    private StringProperty text;
    private ObservableList<Snapshot> list;
    private ObjectProperty<Snapshot> current;

    public History() {
    }

    public History(StringProperty property) {
        this.text = new SimpleStringProperty();
        this.list = FXCollections.observableArrayList();
        this.current = new SimpleObjectProperty<>();

        text.bindBidirectional(property);

        makeSnapshot();
        initialize();
    }

    private void initialize() {
        current.addListener((observable, oldValue, newValue) -> {
            text.setValue(newValue.getText());
            setChanged();
            notifyObservers();
        });

        list.addListener((ListChangeListener<? super Snapshot>) c -> {
            setChanged();
            notifyObservers();
        });
    }

    public ObservableList<Snapshot> getList() {
        return list;
    }

    public void bindTextProperty(StringProperty textProperty) {
        textProperty.setValue(this.text.getValue());
        this.text.bindBidirectional(textProperty);
    }

    public void makeSnapshot() {
        Snapshot temp = new Snapshot(text.getValue());
        list.add(temp);
        current.setValue(temp);
    }

    public boolean containsOnHistory() {
        String temp = text.getValue();
        for (Snapshot snapshot : list) {
            if (snapshot.getText().equals(temp)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsOnHistory(String text) {
        for (Snapshot snapshot : list) {
            if (snapshot.getText().equals(text)) {
                return true;
            }
        }
        return false;
    }

    public Snapshot getCurrent() {
        return current.getValue();
    }

    public void setCurrent(Snapshot current) {
        if (list.contains(current)) {
            this.current.setValue(current);
        }
    }

    public void restoreLastSnapshot() {
        Snapshot temp = list.get(list.size() - 1);
        current.setValue(temp);
    }

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeClassAndObject(output, new ArrayList<>(list));
        kryo.writeClassAndObject(output, current.getValue());
    }

    @Override
    public void read(Kryo kryo, Input input) {
        this.list = FXCollections.observableArrayList((ArrayList) kryo.readClassAndObject(input));
        this.current = new SimpleObjectProperty<>((Snapshot) kryo.readClassAndObject(input));

        this.text = new SimpleStringProperty(current.getValue().getText());

        initialize();
    }
}
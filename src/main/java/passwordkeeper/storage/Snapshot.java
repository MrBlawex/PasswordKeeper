package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Snapshot implements KryoSerializable {

    private String text;
    private Date date;

    Snapshot() {
    }

    public Snapshot(String text) {
        this.text = text;
        this.date = new Date();
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return new SimpleDateFormat().format(date);
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(text);
        kryo.writeClassAndObject(output, date);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        text = input.readString();
        date = (Date) kryo.readClassAndObject(input);
    }

    @Override
    public String toString() {
        return text + ", " + getDate();
    }
}

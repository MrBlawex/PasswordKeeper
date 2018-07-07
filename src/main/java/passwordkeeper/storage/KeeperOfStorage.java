package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class KeeperOfStorage implements KryoSerializable, Observer {

    private String name;
    private File pathToFile;
    private Storage storage;

    public KeeperOfStorage() {
    }

    private KeeperOfStorage(Storage storage, File pathToFile, String name) {
        this.name = name;
        this.pathToFile = pathToFile;
        this.storage = storage;

        setObserver();
    }

    public static KeeperOfStorage newStorage(File pathToDir, String name, String psw) throws IOException {
        Storage storage = new Storage(psw);
        Kryo kryo = new Kryo();
        File pathToFile = new File(pathToDir.getCanonicalPath() + File.separatorChar + name + ".pks");
        Output output = new Output(new FileOutputStream(pathToFile));
        kryo.register(Storage.class);
        kryo.writeObject(output, storage);
        output.close();
        return new KeeperOfStorage(storage, pathToFile, name);
    }

    public static KeeperOfStorage getKeeperFromFile(File pathToFile) throws IOException {
        Kryo kryo = new Kryo();
        kryo.register(Storage.class);
        Input input = new Input(new FileInputStream(pathToFile));
        return new KeeperOfStorage(kryo.readObject(input, Storage.class), pathToFile, pathToFile.getName().replace(".pks", ""));
    }

    public void saveStorage() throws IOException {
        Kryo kryo = new Kryo();
        Output output = new Output(new FileOutputStream(this.getPathToFile()));
        kryo.register(Storage.class);
        kryo.writeObject(output, this.getStorage());
        output.close();
    }


    public String getName() {
        return name;
    }

    private File getPathToFile() {
        return pathToFile;
    }

    public Storage getStorage() {
        return storage;
    }

    public boolean checkExistsFile() {
        try {
            return this.pathToFile.exists();
        } catch (NullPointerException ex) {
            return false;
        }
    }

    private void setObserver() {
        storage.setObserver(this);
    }

    @Override
    public String toString() {
        return name;
    }


    @Override
    public void update(Observable o, Object arg) {
        try {
            this.saveStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeeperOfStorage)) return false;
        KeeperOfStorage that = (KeeperOfStorage) o;
        if (this.hashCode() == that.hashCode()) return true;
        return Objects.equals(name, that.name) && Objects.equals(pathToFile, that.pathToFile) && Objects.equals(storage, that.storage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pathToFile, storage);
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(pathToFile.getPath());
    }

    @Override
    public void read(Kryo kryo, Input input) {
        try {
            KeeperOfStorage temp = getKeeperFromFile(new File(input.readString()));
            this.name = temp.getName();
            this.pathToFile = temp.getPathToFile();
            this.storage = temp.getStorage();

            temp.setObserver();
        } catch (IOException e) {

        }
    }
}

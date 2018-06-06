package passwordkeeper.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;
import java.util.Objects;

public class KeeperOfStorage implements KryoSerializable {

    private String name;
    private File pathToFile;
    private Storage storage;

    public KeeperOfStorage() {
    }

    public KeeperOfStorage(Storage storage, File pathToFile, String name) {
        this.name = name;
        this.pathToFile = pathToFile;
        this.storage = storage;
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

    /**
     * Сохраняет хранилище в файл
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveStorage(KeeperOfStorage keeper) throws FileNotFoundException, IOException {
        Kryo kryo = new Kryo();
        Output output = new Output(new FileOutputStream(keeper.getPathToFile()));
        kryo.register(Storage.class);
        kryo.writeObject(output, keeper.getStorage());
        output.close();
    }

    /**
     * Получает из файла хранилище
     *
     * @param pathToFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static KeeperOfStorage getKeeperFromFile(File pathToFile) throws FileNotFoundException, IOException {
        Kryo kryo = new Kryo();
        kryo.register(Storage.class);
        Input input = new Input(new FileInputStream(pathToFile));
        return new KeeperOfStorage(kryo.readObject(input, Storage.class), pathToFile, pathToFile.getName().replace(".pks", ""));
    }

    public String getName() {
        return name;
    }

    public File getPathToFile() {
        return pathToFile;
    }

    public Storage getStorage() {
        return storage;
    }

    public boolean checkExistsFile() {
        return this.pathToFile.exists();
    }

    @Override
    public String toString() {
        return name;
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
        } catch (IOException e) {

        }
    }
}

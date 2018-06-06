package passwordkeeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PasswordKeeper extends Application {

    public static String NAME_PROGRAM = "PasswordKeeper";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(PasswordKeeper.class.getResource("fxml/StorageSelectionWindow.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setHeight(400 + 50);
        stage.setWidth(320);

        stage.setResizable(false);

        stage.setTitle(NAME_PROGRAM);

        stage.setOnCloseRequest(event -> System.exit(0));

        stage.setScene(scene);
        stage.show();
    }
}

package passwordkeeper.customComponents;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import passwordkeeper.storage.History;
import passwordkeeper.storage.Snapshot;

import java.io.IOException;
import java.util.Optional;

public class GridOfHistory extends GridPane {

    private static ToggleGroup group = new ToggleGroup();
    @FXML
    private GridPane table;
    private ObservableList<SpecificRow> rows;
    private History tempHistory;

    GridOfHistory(History history) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GridOfHistory.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tempHistory = history;
        rows = FXCollections.observableArrayList();
        createListRows(history);
        makeGrid();
    }

    public Snapshot getSelectedSnapshot() {
        Optional<SpecificRow> result = rows.stream().parallel().filter(SpecificRow::isSelected).findFirst();
        return result.get().snapshot;
    }

    private void createListRows(History history) {
        history.getList().forEach(snapshot -> rows.add(new SpecificRow(snapshot, snapshot.equals(history.getCurrent()))));
    }

    private void makeGrid() {
        for (int i = 1; i <= rows.size(); i++) {
            addRowToGrid(rows.get(i - 1), i);
        }
    }

    private void addRowToGrid(SpecificRow row, int rowIndex) {
        table.getRowConstraints().add(makeRow());
        table.add(row.getRadio(), 0, rowIndex);
        table.add(row.getValue(), 1, rowIndex);
        table.add(row.getDate(), 2, rowIndex);
        //table.add(row.getBtn_remove(), 3, rowIndex);
    }

    private RowConstraints makeRow() {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPrefHeight(40);
        rowConstraints.setMinHeight(Region.USE_PREF_SIZE);
        rowConstraints.setMaxHeight(Region.USE_PREF_SIZE);

        return rowConstraints;
    }

    private class SpecificRow {

        private Snapshot snapshot;
        private RadioButton radioButton;
        private HBox hBox;
        private AnchorPane anchorPane;
        private TextField textField;
        private PasswordField passwordField;
        private ToggleButton toggleButton;
        private Label date;
        private MaterialDesignIconView btn_remove;

        SpecificRow(Snapshot snapshot, boolean isSelect) {
            this.snapshot = snapshot;

            makeRadioButton(isSelect);
            makeValue();
            makeDate();
            makeRemovable();
        }

        boolean isSelected() {
            return radioButton.isSelected();
        }

        RadioButton getRadio() {
            return radioButton;
        }

        HBox getValue() {
            return hBox;
        }

        Label getDate() {
            return date;
        }

        public MaterialDesignIconView getBtn_remove() {
            return btn_remove;
        }

        private void makeValue() {
            this.hBox = new HBox();
            this.hBox.setPadding(new Insets(0, 4, 0, 4));
            this.hBox.setAlignment(Pos.CENTER);
            this.hBox.setSpacing(4);

            this.anchorPane = new AnchorPane();
            this.anchorPane.setPrefHeight(32);
            this.anchorPane.setPrefWidth(150);
            this.anchorPane.setMinHeight(Region.USE_PREF_SIZE);
            this.anchorPane.setMaxHeight(Region.USE_PREF_SIZE);

            makeToggleButton();
            makeFields();
            doAnchor(textField);
            doAnchor(passwordField);

            this.hBox.getChildren().addAll(this.anchorPane, this.toggleButton);
        }

        private void makeFields() {
            this.textField = new TextField();
            this.passwordField = new PasswordField();

            this.textField.setEditable(false);
            this.passwordField.setEditable(false);

            this.textField.textProperty().bindBidirectional(passwordField.textProperty());

            this.textField.setText(snapshot.getText());

            this.textField.setManaged(true);
            this.textField.setVisible(true);

            this.textField.managedProperty().bind(toggleButton.selectedProperty());
            this.textField.visibleProperty().bind(toggleButton.selectedProperty());

            this.passwordField.managedProperty().bind(toggleButton.selectedProperty().not());
            this.passwordField.visibleProperty().bind(toggleButton.selectedProperty().not());
        }

        private void makeToggleButton() {
            this.toggleButton = new ToggleButton();
            this.toggleButton.setPrefSize(32, 32);

            this.toggleButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.EYE_OFF));

            this.toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    toggleButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.EYE));
                } else {
                    toggleButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.EYE_OFF));
                }
            });
        }

        private void makeRadioButton(boolean select) {
            this.radioButton = new RadioButton();
            this.radioButton.setSelected(select);
            this.radioButton.setPrefHeight(32);
            this.radioButton.setPrefWidth(27);
            this.radioButton.setToggleGroup(group);
        }

        private void makeDate() {
            this.date = new Label(snapshot.getDate());
            this.date.setFont(new Font(17));
        }

        private void makeRemovable() {
            btn_remove = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_CLOSE);
            btn_remove.setId("buttonRemove");
            btn_remove.setOnMouseClicked(event -> {
                tempHistory.getList().remove(this.snapshot);
                rows.remove(this);
            });
        }

        private void doAnchor(Node node) {
            this.anchorPane.getChildren().addAll(node);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
        }
    }
}

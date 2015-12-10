package ru.killer666.Apteka.workspaces;

import com.google.common.base.Preconditions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.NonNull;
import ru.killer666.Apteka.ResourceWorkspaceInterface;
import ru.killer666.Apteka.domains.Drug;
import ru.killer666.Apteka.domains.Recipe;

@SuppressWarnings("unchecked")
public class TraderCreateOrder extends ResourceWorkspaceInterface {

    private int calcPrice(@NonNull Drug drug, int quantity) {
        return drug.getPrice() * quantity;
    }

    private void calcPriceAndCheckQuantity(@NonNull ComboBox<Drug> fieldDrug, TextField fieldQuantity, Text fieldPrice) {
        Drug drug = fieldDrug.getSelectionModel().getSelectedItem();
        int quantity = 0;

        try {
            if (fieldQuantity.getLength() != 0) {
                quantity = Integer.parseInt(fieldQuantity.getText());

                if (quantity < 0 || quantity > drug.getStorageQuantity()) {
                    quantity = 0;
                    fieldQuantity.setText("0");
                }
            }
        } catch (IllegalArgumentException e) {
            fieldPrice.setText("0");
        }

        fieldPrice.setText(String.valueOf(drug.getPrice() * quantity));
    }

    @Override
    public Pane getPane() {
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(15, 15, 15, 15));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Text textInfo = new Text("Создание заказа");
        textInfo.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        gridPane.add(textInfo, 0, 0);

        gridPane.add(new Label("Лекарство:"), 0, 1);
        gridPane.add(new Label("Рецепт:"), 0, 2);
        gridPane.add(new Label("Количество:"), 0, 3);
        gridPane.add(new Label("Итоговая цена:"), 0, 4);

        ComboBox<Drug> fieldDrug = new ComboBox<>();
        ComboBox<Recipe> fieldRecipe = new ComboBox<>();
        CheckBox withoutRecipe = new CheckBox("Отпустить без рецепта");
        TextField fieldQuantity = new TextField();
        Text fieldPrice = new Text("0");

        fieldDrug.setOnAction(event -> this.calcPriceAndCheckQuantity(fieldDrug, fieldQuantity, fieldPrice));
        fieldQuantity.textProperty().addListener((observable, oldValue, newValue) -> this.calcPriceAndCheckQuantity(fieldDrug, fieldQuantity, fieldPrice));
        withoutRecipe.setOnAction(event -> fieldRecipe.setDisable(withoutRecipe.isSelected()));

        ObservableList<Drug> drugs = FXCollections.observableArrayList();
        drugs.addAll(this.getSession().createCriteria(Drug.class).list());

        fieldDrug.setItems(drugs);

        if (drugs.size() != 0) {
            fieldDrug.getSelectionModel().select(0);
        }

        ObservableList<Recipe> recipes = FXCollections.observableArrayList();
        recipes.addAll(this.getSession().createCriteria(Recipe.class).list());

        fieldRecipe.setItems(recipes);

        if (recipes.size() != 0) {
            fieldRecipe.getSelectionModel().select(0);
        }

        GridPane gridPane2 = new GridPane();
        gridPane2.setHgap(10);
        gridPane2.setVgap(10);

        gridPane2.add(fieldRecipe, 0, 0);
        gridPane2.add(withoutRecipe, 0, 1);

        gridPane.add(fieldDrug, 1, 1);
        gridPane.add(gridPane2, 1, 2);
        gridPane.add(fieldQuantity, 1, 3);
        gridPane.add(fieldPrice, 1, 4);

        borderPane.setCenter(gridPane);

        Button createButton = new Button("Создать");
        createButton.setOnAction(event -> {
            Drug drug = fieldDrug.getSelectionModel().getSelectedItem();
            int quantity;

            try {
                Preconditions.checkArgument(fieldQuantity.getLength() != 0, "Заполните количество");
                quantity = Integer.parseInt(fieldQuantity.getText());

                Preconditions.checkArgument(drug.getStorageQuantity() >= quantity, "Недопустимое количество! Осталось: " + drug.getStorageQuantity() + " единиц товара");
            } catch (IllegalArgumentException e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Ошибка");
                alert.setContentText(e.getMessage());
                alert.showAndWait();

                return;
            }
        });

        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(15, 15, 15, 15));
        pane.getChildren().add(createButton);

        borderPane.setBottom(pane);
        return borderPane;
    }
}

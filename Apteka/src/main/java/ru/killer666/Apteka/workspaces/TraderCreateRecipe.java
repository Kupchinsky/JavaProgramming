package ru.killer666.Apteka.workspaces;

import com.google.common.base.Preconditions;
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
import org.hibernate.Transaction;
import ru.killer666.Apteka.ResourceWorkspaceInterface;
import ru.killer666.Apteka.domains.Recipe;

@SuppressWarnings("unchecked")
public class TraderCreateRecipe extends ResourceWorkspaceInterface {
    @Override
    public Pane getPane() {
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(15, 15, 15, 15));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Text textInfo = new Text("Добавление рецепта");
        textInfo.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        gridPane.add(textInfo, 0, 0);

        gridPane.add(new Label("Фамилия:"), 0, 1);
        gridPane.add(new Label("Имя:"), 0, 2);
        gridPane.add(new Label("Отчество:"), 0, 3);
        gridPane.add(new Label("Возраст:"), 0, 4);
        gridPane.add(new Label("Диагноз:"), 0, 5);
        gridPane.add(new Label("Адрес:"), 0, 6);
        gridPane.add(new Label("Телефон:"), 0, 7);

        TextField fieldFirstName = new TextField();
        TextField fieldLastName = new TextField();
        TextField fieldSecondName = new TextField();
        TextField fieldAge = new TextField();
        TextField fieldDiagnosis = new TextField();
        TextField fieldAddress = new TextField();
        TextField fieldPhone = new TextField();

        gridPane.add(fieldFirstName, 1, 1);
        gridPane.add(fieldLastName, 1, 2);
        gridPane.add(fieldSecondName, 1, 3);
        gridPane.add(fieldAge, 1, 4);
        gridPane.add(fieldDiagnosis, 1, 5);
        gridPane.add(fieldAddress, 1, 6);
        gridPane.add(fieldPhone, 1, 7);

        borderPane.setCenter(gridPane);

        Button deleteButton = new Button("Добавить");
        deleteButton.setOnAction(event -> {

            int age;

            try {
                Preconditions.checkArgument(fieldFirstName.getLength() != 0, "Заполните фамилию");
                Preconditions.checkArgument(fieldLastName.getLength() != 0, "Заполните имя");
                Preconditions.checkArgument(fieldSecondName.getLength() != 0, "Заполните отчество");
                Preconditions.checkArgument(fieldAge.getLength() != 0, "Заполните возраст");
                Preconditions.checkArgument(fieldDiagnosis.getLength() != 0, "Заполните диагноз");
                Preconditions.checkArgument(fieldAddress.getLength() != 0, "Заполните адрес");
                Preconditions.checkArgument(fieldPhone.getLength() != 0, "Заполните телефон");

                age = Integer.parseInt(fieldAge.getText());
            } catch (IllegalArgumentException e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Ошибка");
                alert.setContentText(e.getMessage());
                alert.showAndWait();

                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Подтверждение добавления");
            alert.setContentText("Вы уверены?");

            if (alert.showAndWait().get() == ButtonType.CANCEL) {
                return;
            }

            Transaction tx = this.getSession().beginTransaction();

            Recipe recipe = new Recipe();
            recipe.setFirstName(fieldFirstName.getText());
            recipe.setLastName(fieldLastName.getText());
            recipe.setSecondName(fieldSecondName.getText());
            recipe.setDiagnosis(fieldDiagnosis.getText());
            recipe.setAddress(fieldAddress.getText());
            recipe.setPhone(fieldPhone.getText());
            recipe.setAge(age);

            this.getSession().save(recipe);
            tx.commit();

            fieldFirstName.clear();
            fieldLastName.clear();
            fieldSecondName.clear();
            fieldAge.clear();
            fieldDiagnosis.clear();
            fieldAddress.clear();
            fieldPhone.clear();
        });

        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(15, 15, 15, 15));
        pane.getChildren().add(deleteButton);

        borderPane.setBottom(pane);
        return borderPane;
    }
}

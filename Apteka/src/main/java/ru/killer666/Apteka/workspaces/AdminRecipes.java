package ru.killer666.Apteka.workspaces;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;
import ru.killer666.Apteka.ResourceWorkspaceInterface;
import ru.killer666.Apteka.domains.Recipe;
import ru.killer666.Apteka.domains.Role;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class AdminRecipes extends ResourceWorkspaceInterface {
    private static final Logger logger = LogManager.getLogger(AdminRecipes.class);

    @Override
    public Pane getPane() {
        BorderPane borderPane = new BorderPane();

        Text textInfo = new Text("Управление рецептами");
        textInfo.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        borderPane.setTop(textInfo);

        TableView table = new TableView();
        table.setPlaceholder(new Label("Рецепты отсутствуют. Их может добавить продавец"));
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.addColumn(table, "firstName", "Фамилия");
        this.addColumn(table, "lastName", "Имя");
        this.addColumn(table, "secondName", "Отчество");
        this.addColumn(table, "age", "Возраст");
        this.addColumn(table, "diagnosis", "Диагноз");
        this.addColumn(table, "address", "Адрес");
        this.addColumn(table, "phone", "Телефон");

        ObservableList<Recipe> data = FXCollections.observableArrayList();
        data.addAll(this.getSession().createCriteria(Recipe.class).list());

        table.setItems(data);
        borderPane.setCenter(table);

        if (this.getRole() == Role.ADMIN) {
            Button deleteButton = new Button("Удалить");
            deleteButton.setOnAction(event -> {
                ObservableList<Recipe> selectedItems = table.getSelectionModel().getSelectedItems();

                if (selectedItems.size() == 0) {
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Подтверждение удаления");
                alert.setContentText("Вы уверены?");

                if (alert.showAndWait().get() == ButtonType.CANCEL) {
                    return;
                }

                Transaction tx = this.getSession().beginTransaction();

                for (Recipe recipe : selectedItems) {
                    this.getSession().delete(recipe);
                    data.remove(recipe);
                }

                tx.commit();
            });

            FlowPane pane = new FlowPane();
            pane.setPadding(new Insets(15, 15, 15, 15));
            pane.getChildren().add(deleteButton);

            borderPane.setBottom(pane);
        }

        return borderPane;
    }

    private void addColumn(TableView view, final String fieldName, String columnDescription) {
        Field field;

        try {
            field = Recipe.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.error(e);
            return;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        TableColumn<Recipe, String> tableColumn = new TableColumn<>(columnDescription);

        if (this.getRole() == Role.ADMIN) {
            tableColumn.setEditable(true);
            tableColumn.setOnEditCommit(event -> {
                try {
                    if (field.getType() == String.class) {
                        field.set(event.getRowValue(), event.getNewValue());
                    } else if (field.getType() == Integer.class || field.getGenericType().getTypeName().equals("int")) {
                        field.set(event.getRowValue(), Integer.valueOf(event.getNewValue()));
                    } else {
                        logger.warn("Unknown field type: " + field.getType().getName());
                    }

                    Transaction tx = this.getSession().beginTransaction();
                    this.getSession().saveOrUpdate(event.getRowValue());
                    tx.commit();
                } catch (IllegalAccessException e) {
                    logger.error(e);
                }
            });
            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        }

        tableColumn.setCellValueFactory(param -> {
            try {
                return new SimpleStringProperty(String.valueOf(field.get(param.getValue())));
            } catch (IllegalAccessException e) {
                logger.error(e);
            }

            return new SimpleStringProperty("Exception");
        });

        view.getColumns().add(tableColumn);
    }
}

package ru.killer666.Apteka.workspaces;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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
import ru.killer666.Apteka.domains.Drug;
import ru.killer666.Apteka.domains.Order;
import ru.killer666.Apteka.domains.Recipe;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unchecked")
public class AdminSells extends ResourceWorkspaceInterface {
    private static final Logger logger = LogManager.getLogger(AdminSells.class);
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @Override
    public Pane getPane() {
        BorderPane borderPane = new BorderPane();

        Text textInfo = new Text("Продажи");
        textInfo.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        borderPane.setTop(textInfo);

        TableView table = new TableView();
        table.setPlaceholder(new Label("Продажи отсутствуют"));
        table.setEditable(false);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.addColumn(table, "drug", "Лекарство");
        this.addColumn(table, "totalPrice", "Общая цена");
        this.addColumn(table, "quantity", "Количество");
        this.addColumn(table, "dateSell", "Дата продажи");
        this.addColumn(table, "recipe", "Рецепт");

        ObservableList<Order> data = FXCollections.observableArrayList();
        data.addAll(this.getSession().createCriteria(Order.class).list());

        table.setItems(data);
        borderPane.setCenter(table);

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(event -> {
            ObservableList<Order> selectedItems = table.getSelectionModel().getSelectedItems();

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

            for (Order order : selectedItems) {
                this.getSession().delete(order);
                data.remove(order);
            }

            tx.commit();
        });

        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(15, 15, 15, 15));
        pane.getChildren().add(deleteButton);

        borderPane.setBottom(pane);
        return borderPane;
    }

    private void addColumn(TableView view, final String fieldName, String columnDescription) {
        Field field;

        try {
            field = Order.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.error(e);
            return;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        TableColumn<Order, String> tableColumn = new TableColumn<>(columnDescription);

        tableColumn.setCellValueFactory(param -> {
            try {
                Object obj = field.get(param.getValue());

                if (field.getType() == Drug.class) {
                    return new SimpleStringProperty(((Drug) obj).getName());
                } else if (field.getType() == Recipe.class) {
                    Recipe recipe = (Recipe) obj;
                    String result = "< без рецепта >";

                    if (recipe != null) {
                        result = recipe.getFirstName() + " " + recipe.getLastName() + " " + recipe.getSecondName() + " (" + recipe.getDiagnosis() + ")";
                    }

                    return new SimpleStringProperty(result);
                } else if (field.getType() == Date.class) {
                    return new SimpleStringProperty(this.dateTimeFormatter.format((Date) obj));
                } else {
                    return new SimpleStringProperty(String.valueOf(obj));
                }
            } catch (IllegalAccessException e) {
                logger.error(e);
            }

            return new SimpleStringProperty("Exception");
        });

        view.getColumns().add(tableColumn);
    }
}

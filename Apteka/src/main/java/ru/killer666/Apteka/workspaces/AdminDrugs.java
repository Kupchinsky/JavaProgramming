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
import ru.killer666.Apteka.domains.Drug;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AdminDrugs extends ResourceWorkspaceInterface {
    private static final Logger logger = LogManager.getLogger(AdminDrugs.class);

    private final List<Drug> newItems = new ArrayList<>();

    @Override
    public Pane getPane() {
        BorderPane borderPane = new BorderPane();

        Text textInfo = new Text("Управление продуктами");
        textInfo.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        borderPane.setTop(textInfo);

        TableView table = new TableView();
        table.setPlaceholder(new Label("Продукты отсутствуют"));
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.addColumn(table, "name", "Наименование");
        this.addColumn(table, "type", "Тип лекарства");
        this.addColumn(table, "secondName", "Отчество");
        this.addColumn(table, "applyType", "Способ применения");
        this.addColumn(table, "price", "Цена");
        this.addColumn(table, "storageQuantity", "Количество на складе");

        ObservableList<Drug> data = FXCollections.observableArrayList();
        data.addAll(this.getSession().createCriteria(Drug.class).list());

        table.setItems(data);
        borderPane.setCenter(table);

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(event -> {
            ObservableList<Drug> selectedItems = table.getSelectionModel().getSelectedItems();

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

            for (Drug recipe : selectedItems) {
                this.getSession().delete(recipe);
                data.remove(recipe);
            }

            tx.commit();
        });

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> {
            Drug drug = new Drug();

            this.newItems.add(drug);
            data.add(drug);
        });

        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(event -> {
            ObservableList<Drug> selectedItems = table.getSelectionModel().getSelectedItems();

            if (selectedItems.size() == 0) {
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Подтверждение сохранения");
            alert.setContentText("Вы уверены?");

            if (alert.showAndWait().get() == ButtonType.CANCEL) {
                return;
            }

            Transaction tx = this.getSession().beginTransaction();
            int saved = 0;

            for (Drug drug : selectedItems) {

                if (!this.newItems.contains(drug)) {
                    continue;
                }

                this.getSession().save(drug);
                this.newItems.remove(drug);
                saved++;
            }

            tx.commit();

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Информация");
            alert.setContentText("Сохранено " + saved + " новых записей");
            alert.show();
        });

        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(15, 15, 15, 15));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.getChildren().add(deleteButton);
        pane.getChildren().add(addButton);
        pane.getChildren().add(saveButton);

        borderPane.setBottom(pane);
        return borderPane;
    }

    private void addColumn(TableView view, final String fieldName, String columnDescription) {
        Field field;

        try {
            field = Drug.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.error(e);
            return;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        TableColumn<Drug, String> tableColumn = new TableColumn<>(columnDescription);

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

                if (!this.newItems.contains(event.getRowValue())) {
                    Transaction tx = this.getSession().beginTransaction();
                    this.getSession().saveOrUpdate(event.getRowValue());
                    tx.commit();
                }
            } catch (IllegalAccessException e) {
                logger.error(e);
            }
        });
        tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
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

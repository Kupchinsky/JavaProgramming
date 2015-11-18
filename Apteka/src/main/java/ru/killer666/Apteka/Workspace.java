package ru.killer666.Apteka;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ru.killer666.trpo.aaa.UserController;
import ru.killer666.trpo.aaa.domains.Resource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class Workspace {

    static {
        //Workspace.workspaceClassMap.put("admin", EmptyWorkspace.class);
    }

    private static Map<String, Class<? extends ResourceWorkspaceInterface>> workspaceClassMap = new HashMap<>();

    private ImmutableMap<Resource, Class<? extends ResourceWorkspaceInterface>> resourceClassMap;

    private final Stage stage;
    private final Scene previousScene;
    private final UserController userController;

    Workspace(Stage stage, UserController userController) {
        this.stage = stage;
        this.previousScene = stage.getScene();
        this.userController = userController;

        Map<Resource, Class<? extends ResourceWorkspaceInterface>> resourceClassMap = new HashMap<>();

        try {
            for (Resource resource : userController.getAllResources()) {
                Class<? extends ResourceWorkspaceInterface> resourceClass = Workspace.workspaceClassMap.get(resource.getName());

                if (resourceClass == null) {
                    resourceClass = EmptyWorkspace.class;
                }

                resourceClassMap.put(resource, resourceClass);
            }

            this.resourceClassMap = new ImmutableMap.Builder<Resource, Class<? extends ResourceWorkspaceInterface>>()
                    .putAll(resourceClassMap).build();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    void init() {

        BorderPane borderPane = new BorderPane();

        HBox authBox = new HBox(10);
        authBox.setAlignment(Pos.CENTER);
        authBox.setPadding(new Insets(10, 0, 10, 0));
        authBox.setStyle("-fx-background-color: #C5B0B0;");
        authBox.getChildren().add(new Label("Добро пожаловать, " + this.userController.getLogOnUser().getPersonName() + "!"));

        Button logoutButton = new Button("Выход");

        logoutButton.setOnAction((e) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(this.stage.getTitle());
            alert.setHeaderText("Подтверждение выхода");
            alert.setContentText("Вы уверены?");

            if (alert.showAndWait().get() == ButtonType.OK) {

                // TODO: Save accounting

                this.userController.clearAll();

                this.stage.close();
                this.stage.setScene(this.previousScene);
                this.stage.show();
            }
        });

        authBox.getChildren().add(logoutButton);

        borderPane.setTop(authBox);

        GridPane resourcesPane = new GridPane();
        resourcesPane.setAlignment(Pos.CENTER_LEFT);
        resourcesPane.setHgap(10);
        resourcesPane.setVgap(10);
        resourcesPane.setPadding(new Insets(25, 25, 25, 25));
        resourcesPane.setStyle("-fx-background-color: #336699;");

        int rowIndex = 0;

        Label infoLabel2 = new Label("- Ресурсы -");
        infoLabel2.setStyle("-fx-text-fill: white;");
        resourcesPane.add(infoLabel2, 0, rowIndex++);

        for (Resource resource : this.resourceClassMap.keySet()) {
            Button buttonResource = new Button(resource.getName());

            buttonResource.setOnAction((e) -> {

                ResourceWorkspaceInterface workspaceInterface;

                try {
                    workspaceInterface = this.resourceClassMap.get(resource).newInstance();
                } catch (InstantiationException | IllegalAccessException e1) {
                    e1.printStackTrace();
                    return;
                }

                workspaceInterface.setResource(resource);
                borderPane.setCenter(workspaceInterface.getPane());
            });

            resourcesPane.add(buttonResource, 0, rowIndex++);
        }

        borderPane.setRight(resourcesPane);

        Scene scene = new Scene(borderPane, 800, 600);

        this.stage.setScene(scene);
        this.stage.show();
    }

    static class EmptyWorkspace extends ResourceWorkspaceInterface {

        @Override
        Pane getPane() {
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(new Label("Ресурс \"" + this.getResource().getName() + "\" не имеет обработчика"));

            return borderPane;
        }
    }
}

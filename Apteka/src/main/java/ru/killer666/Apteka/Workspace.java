package ru.killer666.Apteka;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import ru.killer666.Apteka.domains.Role;
import ru.killer666.trpo.aaa.UserController;
import ru.killer666.trpo.aaa.domains.Resource;
import ru.killer666.trpo.aaa.exceptions.ResourceDeniedException;
import ru.killer666.trpo.aaa.exceptions.ResourceNotFoundException;

import java.sql.SQLException;
import java.util.*;

class Workspace {

    private static Map<String, Class<? extends ResourceWorkspaceInterface>> workspaceClassMap = new HashMap<>();

    static {
        Workspace.workspaceClassMap.put("admin", SelectSubInterfaceWorkspace.class);
        Workspace.workspaceClassMap.put("trader", SelectSubInterfaceWorkspace.class);
    }

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
            List<Resource> resourceList = userController.getAllResources();
            Collections.sort(resourceList);

            for (Resource resource : resourceList) {
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

                try {
                    this.userController.saveAccounting();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

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

        Label infoLabelRole = new Label("");
        infoLabelRole.setStyle("-fx-text-fill: white;");
        resourcesPane.add(infoLabelRole, 0, rowIndex++);

        for (Resource resource : this.resourceClassMap.keySet()) {
            Button buttonResource = new Button(resource.getName());

            buttonResource.setOnAction((e) -> {

                ResourceWorkspaceInterface workspaceInterface;
                Role role = null;

                try {
                    List<Integer> roles = this.userController.getGrantedRoles(resource);

                    if (roles.size() == 0) {
                        throw new RolesNotFoundException();
                    }

                    if (roles.size() > 1) {
                        List<String> choices = new ArrayList<>();

                        for (Integer intRole : roles) {

                            for (Role _role : Role.values()) {
                                if (_role.getValue() == intRole) {
                                    choices.add(_role.name());
                                    break;
                                }
                            }
                        }

                        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.iterator().next(), choices);

                        dialog.setTitle(this.stage.getTitle());
                        dialog.setHeaderText("Выбор роли для доступа");
                        dialog.setContentText("Выберите роль:");

                        Optional<String> result = dialog.showAndWait();

                        if (!result.isPresent()) {
                            return;
                        }

                        role = Role.valueOf(result.get());
                    } else {
                        int intRole = roles.iterator().next();

                        for (Role _role : Role.values()) {
                            if (_role.getValue() == intRole) {
                                role = _role;
                                break;
                            }
                        }
                    }

                    if (role == null) {
                        return;
                    }

                    this.userController.authResource(resource, role);
                    workspaceInterface = this.resourceClassMap.get(resource).newInstance();
                    buttonResource.setStyle("-fx-background-color: green;");

                    infoLabelRole.setText("Роль: " + role.name());
                } catch (SQLException | InstantiationException | IllegalAccessException | ResourceNotFoundException e1) {
                    e1.printStackTrace();
                    return;
                } catch (RolesNotFoundException | ResourceDeniedException e1) {
                    workspaceInterface = new AccessDeniedWorkspace();
                    ((AccessDeniedWorkspace) workspaceInterface).setRole(role);
                    buttonResource.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    infoLabelRole.setText("");
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

    static class AccessDeniedWorkspace extends ResourceWorkspaceInterface {

        @Getter
        @Setter
        private Role role = null;

        @Override
        Pane getPane() {
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(new Label("Доступ запрещён в \"" + this.getResource().getName() + "\"" + (this.role != null ? " с ролью " + this.role.name() + "!" : "")));

            return borderPane;
        }
    }

    static class SelectSubInterfaceWorkspace extends ResourceWorkspaceInterface {

        @Override
        Pane getPane() {
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(new Label("Выберите дочерний ресурс!"));

            return borderPane;
        }
    }

    static class RolesNotFoundException extends Exception {

    }
}
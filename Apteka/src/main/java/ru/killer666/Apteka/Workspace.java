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
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.Session;
import ru.killer666.Apteka.domains.Role;
import ru.killer666.Apteka.workspaces.*;
import ru.killer666.trpo.aaa.RoleInterface;
import ru.killer666.trpo.aaa.domains.Resource;
import ru.killer666.trpo.aaa.exceptions.ResourceDeniedException;
import ru.killer666.trpo.aaa.exceptions.ResourceNotFoundException;
import ru.killer666.trpo.aaa.services.AuthService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

class Workspace {

    private static Map<String, Class<? extends ResourceWorkspaceInterface>> workspaceClassMap = new HashMap<>();

    static {
        Workspace.workspaceClassMap.put("admin", SelectSubInterfaceWorkspace.class);
        Workspace.workspaceClassMap.put("admin/recipes", AdminRecipes.class);
        Workspace.workspaceClassMap.put("admin/products", AdminDrugs.class);
        Workspace.workspaceClassMap.put("admin/sells", AdminSells.class);

        Workspace.workspaceClassMap.put("trader", SelectSubInterfaceWorkspace.class);
        Workspace.workspaceClassMap.put("trader/create_recipe", TraderCreateRecipe.class);
        Workspace.workspaceClassMap.put("trader/sell", TraderCreateOrder.class);
    }

    private ImmutableMap<Resource, Class<? extends ResourceWorkspaceInterface>> resourceClassMap;

    private final Stage stage;
    private final Scene previousScene;
    private final AuthService authService;
    private final Session session;

    Workspace(Stage stage, AuthService authService, Session session) {
        this.stage = stage;
        this.previousScene = stage.getScene();
        this.authService = authService;
        this.session = session;

        Map<Resource, Class<? extends ResourceWorkspaceInterface>> resourceClassMap = new HashMap<>();

        List<Resource> resourceList = authService.getAllResources(this.session);
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
    }

    void init() {

        BorderPane borderPane = new BorderPane();

        HBox authBox = new HBox(10);
        authBox.setAlignment(Pos.CENTER);
        authBox.setPadding(new Insets(10, 0, 10, 0));
        authBox.setStyle("-fx-background-color: #C5B0B0;");
        authBox.getChildren().add(new Label("Добро пожаловать, " + this.authService.getLogOnUser().getPersonName() + "!"));

        Button logoutButton = new Button("Выход");

        logoutButton.setOnAction((e) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(this.stage.getTitle());
            alert.setHeaderText("Подтверждение выхода");
            alert.setContentText("Вы уверены?");

            if (alert.showAndWait().get() == ButtonType.OK) {

                try {
                    this.authService.saveAccounting(this.session);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                this.authService.clearAll();

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
            Button buttonResource = new Button(resourceToName(resource));

            buttonResource.setOnAction((e) -> {

                ResourceWorkspaceInterface workspaceInterface;
                Role role = null;

                try {
                    List<RoleInterface> roles = this.authService.getGrantedRoles(this.session, resource);

                    if (roles.size() == 0) {
                        throw new RolesNotFoundException();
                    }

                    if (roles.size() > 1) {
                        List<String> choices = new ArrayList<>();

                        for (RoleInterface _role : roles) {
                            choices.add(_role.name());
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
                        role = (Role) roles.iterator().next();
                    }

                    if (role == null) {
                        return;
                    }

                    this.authService.authResource(this.session, resource, role);
                    workspaceInterface = this.resourceClassMap.get(resource).newInstance();
                    buttonResource.setStyle("-fx-background-color: green;");

                    infoLabelRole.setText("Роль: " + role.name());
                } catch (InstantiationException | IllegalAccessException | ResourceNotFoundException | InvocationTargetException | NoSuchMethodException e1) {
                    e1.printStackTrace();
                    return;
                } catch (RolesNotFoundException | ResourceDeniedException e1) {
                    workspaceInterface = new AccessDeniedWorkspace();
                    ((AccessDeniedWorkspace) workspaceInterface).setRole(role);
                    buttonResource.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    infoLabelRole.setText("");
                }

                workspaceInterface.setResource(resource);
                workspaceInterface.setSession(this.session);

                borderPane.setCenter(workspaceInterface.getPane());
            });

            resourcesPane.add(buttonResource, 0, rowIndex++);
        }

        borderPane.setRight(resourcesPane);

        Scene scene = new Scene(borderPane, 800, 600);

        this.stage.setScene(scene);
        this.stage.show();
    }

    private static String resourceToName(@NonNull Resource resource) {
        switch (resource.getName()) {
            case "admin":
                return "Администрирование";
            case "admin/recipes":
                return "Администрирование - Рецепты";
            case "admin/products":
                return "Администрирование - Продукты";
            case "admin/sells":
                return "Администрирование - Продажи";
            case "trader":
                return "Продавец";
            case "trader/sell":
                return "Продавец - Создать заказ";
            case "trader/create_recipe":
                return "Продавец - Создать рецепт";
            default:
                return resource.getName();
        }
    }

    static class EmptyWorkspace extends ResourceWorkspaceInterface {

        @Override
        public Pane getPane() {
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(new Label("Ресурс " + resourceToName(this.getResource()) + " (" + this.getResource().getName() + ") не имеет обработчика"));

            return borderPane;
        }
    }

    static class AccessDeniedWorkspace extends ResourceWorkspaceInterface {

        @Getter
        @Setter
        private Role role = null;

        @Override
        public Pane getPane() {
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(new Label("Доступ запрещён в \"" + this.getResource().getName() + "\"" + (this.role != null ? " с ролью " + this.role.name() + "!" : "")));

            return borderPane;
        }
    }

    static class SelectSubInterfaceWorkspace extends ResourceWorkspaceInterface {

        @Override
        public Pane getPane() {
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(new Label("Выберите дочерний ресурс!"));

            return borderPane;
        }
    }

    static class RolesNotFoundException extends Exception {

    }
}
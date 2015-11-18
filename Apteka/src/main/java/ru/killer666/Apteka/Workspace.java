package ru.killer666.Apteka;

import com.google.common.collect.ImmutableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.killer666.trpo.aaa.UserController;
import ru.killer666.trpo.aaa.domains.Resource;

import java.sql.SQLException;

class Workspace {

    private ImmutableList<Resource> resourceList;
    private final Stage stage;
    private final UserController userController;

    Workspace(Stage stage, UserController userController) {
        this.stage = stage;
        this.userController = userController;

        try {
            this.resourceList = new ImmutableList.Builder<Resource>().addAll(userController.getAllResources()).build();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    void init() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        int rowIndex = 1;

        for (Resource resource : this.resourceList) {

            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.CENTER_LEFT);
            hbBtn.getChildren().add(new Button(resource.getName()));

            grid.add(hbBtn, 1, rowIndex++);
        }

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER_LEFT);
        hbBtn.getChildren().add(new Label("- Ресурсы -"));

        grid.add(hbBtn, 1, 0);
        grid.add(new Label("Добро пожаловать, " + this.userController.getLogOnUser().getPersonName()), 0, 0);

        Scene scene = new Scene(grid, 800, 600);

        this.stage.setScene(scene);
        this.stage.show();
    }
}

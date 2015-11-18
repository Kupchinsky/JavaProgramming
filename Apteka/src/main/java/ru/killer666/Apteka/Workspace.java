package ru.killer666.Apteka;

import com.google.common.collect.ImmutableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.killer666.trpo.aaa.UserController;
import ru.killer666.trpo.aaa.domains.Resource;

import java.sql.SQLException;

class Workspace {

    private ImmutableList<Resource> resourceList;
    private final Stage stage;

    Workspace(Stage stage, UserController userController) {
        this.stage = stage;

        try {
            this.resourceList = new ImmutableList.Builder<Resource>().addAll(userController.getAllResources()).build();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return;
        }
    }

    void init() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn1.getChildren().add(new Button("Privet"));
        grid.add(hbBtn1, 1, 4);

        Scene scene = new Scene(grid, 800, 600);

        this.stage.setScene(scene);
        this.stage.show();
    }
}

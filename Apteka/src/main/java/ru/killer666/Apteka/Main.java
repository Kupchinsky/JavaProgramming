package ru.killer666.Apteka;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import ru.killer666.trpo.aaa.UserController;
import ru.killer666.trpo.aaa.exceptions.IncorrectPasswordException;
import ru.killer666.trpo.aaa.exceptions.UserNotFoundException;

import java.util.concurrent.ExecutionException;

public class Main extends Application {

    private final TextField userField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Text status = new Text();
    private final Button authButton = new Button("Войти");
    private final UserController userController = new UserController();

    @AllArgsConstructor
    private final class UserAuthTask extends Task<Exception> {

        String userName;
        String password;

        @Override
        public Exception call() {

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }

            try {
                Main.this.userController.authUser(this.userName, this.password);
            } catch (Exception e) {
                return e;
            }

            return null;
        }
    }

    private void createFields(GridPane pane) {
        pane.add(new Label("Пользователь:"), 0, 1);
        pane.add(this.userField, 1, 1);
        pane.add(new Label("Пароль:"), 0, 2);
        pane.add(this.passwordField, 1, 2);
        pane.add(this.status, 1, 6);

        this.userField.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                this.authButton.fire();
            }
        });

        this.passwordField.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                this.authButton.fire();
            }
        });
    }

    private void createLogo(GridPane pane, Stage stage) {
        final ImageView imv = new ImageView();
        final Image image2 = new Image(Main.class.getResourceAsStream("logo.jpg"));
        imv.setImage(image2);

        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().add(imv);
        pane.add(pictureRegion, 1, 0);

        Text scenetitle = new Text(stage.getTitle());
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        pane.add(scenetitle, 0, 0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Аптека \"Рашнаптек груп\"");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        this.createLogo(grid, primaryStage);
        this.createFields(grid);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(this.authButton);
        grid.add(hbBtn, 1, 4);

        this.authButton.setOnAction(e -> {

            UserAuthTask userAuthTask = new UserAuthTask(this.userField.getText(), this.passwordField.getText());

            userAuthTask.setOnRunning((workerStateEvent) -> {
                this.status.setFill(Color.FORESTGREEN);
                this.status.setText("Выполняется вход...");
            });

            userAuthTask.setOnSucceeded((workerStateEvent) -> {
                try {
                    Exception authResult = userAuthTask.get();

                    if (authResult == null) {
                        this.status.setText("Привет, " + this.userController.getLogOnUser().getPersonName() + "!");

                        // TODO: Auth succeed
                        return;
                    }

                    this.status.setFill(Color.RED);

                    if (authResult instanceof UserNotFoundException) {
                        this.status.setText("Пользователь не найден!");
                    } else if (authResult instanceof IncorrectPasswordException) {
                        this.status.setText("Неверный пароль!");
                    } else {
                        this.status.setText("Exception: " + authResult);
                    }
                } catch (InterruptedException | ExecutionException e1) {
                    e1.printStackTrace();
                }
            });

            new Thread(userAuthTask).start();
        });

        Scene scene = new Scene(grid, 550, 500);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

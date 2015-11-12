package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private final TextField userField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Text status = new Text();

    private void createFields(GridPane pane)
    {
        pane.add(new Label("Пользователь:"), 0, 1);
        pane.add(this.userField, 1, 1);
        pane.add(new Label("Пароль:"), 0, 2);
        pane.add(this.passwordField, 1, 2);
        pane.add(this.status, 1, 6);
    }

    private void createLogo(GridPane pane, Stage stage)
    {
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

        Button btn = new Button("Войти");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        btn.setOnAction(e -> {
            this.status.setFill(Color.FORESTGREEN);
            this.status.setText("Sign in button pressed");
        });

        Scene scene = new Scene(grid, 550, 500);
        primaryStage.setScene(scene);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

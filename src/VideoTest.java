import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();

        MediaPlayer player = new MediaPlayer( new Media(getClass().getResource("/Achievements/acid/Кислота Breaking Bad.mp4").toExternalForm()));
        MediaView mediaView = new MediaView(player);

        root.getChildren().add( mediaView);

        Scene scene = new Scene(root, 1024, 768);

        primaryStage.setScene(scene);
        primaryStage.show();


        player.play();

    }

}

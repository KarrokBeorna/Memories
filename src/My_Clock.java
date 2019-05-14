import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class My_Clock extends Label {
    public long time;

    My_Clock() {
        time = 0;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), ae -> {
                    time++;
                    setText("" + time);
                }),new KeyFrame(Duration.millis(1000)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }
}
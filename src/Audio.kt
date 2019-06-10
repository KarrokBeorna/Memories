import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import tornadofx.Controller

class Audio: Controller() {
    private val md = Media(Audio::class.java.getResource("/Achievements/Audio.mp3").toExternalForm())
    val mdp = MediaPlayer(md)
    val repeat = mdp.setOnEndOfMedia { mdp.seek(Duration.ZERO) }
}
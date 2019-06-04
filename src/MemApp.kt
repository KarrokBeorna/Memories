import javafx.scene.layout.Pane
import javafx.stage.Stage
import tornadofx.*
import java.io.File


class Memories : View("Memories") {

    override val root = Pane(GameFunctionality().root)


    private val timeFile = File("Time.txt").readText()
    private val numT = Integer.parseInt(timeFile)
    private val numTT = numT / 1000

    val bgi = if (numTT < 44) root.style = "-fx-background-image: url(Icons/Fon$numTT.jpg)"
    else root.style = "-fx-background-image: url(Icons/Fon43.jpg)"

}

class MemoriesApp : App(Memories::class) {
        override fun start(stage: Stage) {
            importStylesheet("/style.css")
            super.start(stage)
            stage.isFullScreen = true
        }
}
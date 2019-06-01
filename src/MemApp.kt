import javafx.scene.layout.Pane
import javafx.stage.Stage
import tornadofx.*


class Memories : View("Memories") {

    override val root = Pane(GameFunctionality().root)

}

class MemoriesApp : App(Memories::class) {
        override fun start(stage: Stage) {
            importStylesheet("/style.css")
            super.start(stage)
            stage.isFullScreen = true
        }
}
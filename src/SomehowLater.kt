import javafx.scene.image.Image
import javafx.scene.layout.Pane
import tornadofx.*

class SomehowLater {

    fun randomImage(num: Int): Image {
        return when (num) {
            1 -> Image("/Icons/Harry Potter.jpg")
            2 -> Image("/Icons/GOT.jpg")
            3 -> Image("/Icons/HTTYD.jpg")
            4 -> Image("/Icons/Star Wars.png")
            5 -> Image("/Icons/The Lord of The Rings.png")
            else -> Image("/Icons/WoW.png")
        }
    }
}
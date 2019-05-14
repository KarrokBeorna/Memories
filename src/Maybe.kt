import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import tornadofx.*
import java.util.*
import kotlin.system.exitProcess


class HelloWorld : View("Memories") {
    override val root = Pane()

    val timeLabel = label(KTimer().sspTime.get()) {
        translateY = 100.0
        translateX = 1000.0
        prefWidth = 150.0
        prefHeight = 50.0
        alignment = Pos.CENTER
        style {
            backgroundColor += Color.AZURE
            fontSize = 18.px
        }
        isVisible = false
    }
    private fun kt() {
        KTimer().sspTime.addListener(InvalidationListener {
            @Override
            fun invalidated(observable: Observable) {
                timeLabel.text = KTimer().sspTime.get()
            }
        })
    }

    val run = button("Начало игры") {
            prefWidth = 250.0
            translateX = 558.0
            translateY = 384.0
            style {
                backgroundColor += Color.AQUAMARINE
                fontSize = 30.px
            }
            setOnMouseClicked {
                field64()
                rIF()
                skills()
                lblPoints().bind(numPoints)
                isVisible = false
                progress.isVisible = false
                newExit()
                timeLabel.isVisible = true
                KTimer().startTimer(KTimer().time)
                newProgress.isVisible = true
            }
        }


    private val progress = button("Достижения") {
        prefWidth = 250.0
        translateX = 558.0
        translateY = 460.0
        style {
            backgroundColor += Color.GREEN
            fontSize = 30.px
        }
        setOnMouseClicked {
            sceneProgress()
        }
    }
    private val exit = button("Выход") {
        prefWidth = 250.0
        translateX = 558.0
        translateY = 536.0
        style {
            backgroundColor += Color.RED
            fontSize = 30.px
        }
        action { exitProcess(1) }
    }
    private val newProgress = button("", imageview("/Icons/VidIc.png")) {
        translateY = 640.0
        translateX = 1272.0
        setOnMouseClicked {
            sceneProgress()
        }
        isVisible = false
    }
    private fun newExit():Button {
        exit.translateY = 700.0
        exit.translateX = 1250.0
        exit.prefWidth = 100.0
        exit.style {
            backgroundColor += Color.RED
            fontSize = 20.px
        }
        return exit
    }

    private fun sceneProgress(): Group {
        return group {
            rectangle {
                fill = Color.GRAY
                width = 1366.0
                height = 768.0
            }
            for (i in 1..9)
                rectangle {
                    translateX = ((i - 1) % 3) * 422.0 + 100.0 //100 522 944
                    width = 322.0
                    height = 200.0
                    translateY = ((i - 1) / 3) * 234.0 + 50.0 //50 284 518
                    fill = Color.BLACK
                }
            button("Выход") {
                prefWidth = 70.0
                prefHeight = 40.0
                translateX = 1280.0
                translateY = 715.0
                style {
                    backgroundColor += Color.RED
                    fontSize = 15.px
                }
                action { exitProcess(1) }
            }
        }
    }


    private val list = mutableListOf<Int>()
    private var count = 0
    private var numPoints = SimpleIntegerProperty()
    private var numPF = SimpleIntegerProperty() + numPoints


    private fun random(): Image {
        var random = 0
        var image = Image("/Icons/Acid.jpg")

        while (random == 0) {
            random = (1..6).random()
            when {
                count in 0..1 || count in 8..9 -> {
                    list += random
                    image = randomImage(random)
                }
                count % 8 in 0..1 -> if (random == list[count - 16] && random == list[count - 8])
                    random = 0
                else {
                    list += random
                    image = randomImage(random)
                }
                count in 2..7 || count in 10..15 -> if (random == list[count - 2] && random == list[count - 1])
                    random = 0
                else {
                    list += random
                    image = randomImage(random)
                }
                else -> if ((random == list[count - 16] && random == list[count - 8])
                    || (random == list[count - 2] && random == list[count - 1]))
                    random = 0
                else {
                    list += random
                    image = randomImage(random)
                }
            }
        }
        return image
        }


    private fun randomImage(num: Int): Image {
        return when (num) {
            1 -> Image("/Icons/Harry Potter.jpg")
            2 -> Image("/Icons/GOT.jpg")
            3 -> Image("/Icons/HTTYD.jpg")
            4 -> Image("/Icons/Star Wars.png")
            5 -> Image("/Icons/The Lord of The Rings.png")
            else -> Image("/Icons/WoW.png")
        }
    }

    private fun rIF() {
        for (i in 0..63) {
            button("", imageview(random())) {
                translateX = 92.0 + (count % 8) * 75.0
                translateY = 80.0 + count / 8 * 75.0
                count++
            }
        }
    }

    private fun field64(): ImageView {
        return imageview("/Icons/Field.jpg") {
            translateX = 100.0
            translateY = 84.0
        }
    }

    private fun skills(): Pane {
        return pane {
            button("", imageview("/Icons/Aquaman.png")) {
                translateX = 800.0
                translateY = 100.0
                setOnMouseClicked {
                    count = 0
                    list.clear()
                    field64()
                    rIF()
                }
            }
            val lblFlames = label {
                alignment = Pos.CENTER
                prefWidth = 35.0
                translateX = 828.0
                translateY = 334.0
                style {
                backgroundColor += Color.WHITE
                }
            }
            button("", imageview("/Icons/Flames.jpg")) {
                translateX = 800.0
                translateY = 250.0
                setOnMouseClicked {if (numPF / 100 >= 1) {
                    points(16)
                    numPF -= 100
                    lblFlames.bind(numPF / 100)
                }
                }

            }
            button("", imageview("/Icons/Bomb.png")) {
                translateX = 800.0
                translateY = 400.0
                setOnMouseClicked {
                    count = 0
                    list.clear()
                    field64()
                    rIF()
                    points(64)
                }

            }
            label("x") {
                alignment = Pos.CENTER
                prefWidth = 35.0
                translateX = 828.0
                translateY = 484.0
                style {
                    backgroundColor += Color.WHITE
                }
            }
            button("", imageview("/Icons/Acid.jpg")) {
                translateX = 800.0
                translateY = 550.0
                setOnMouseClicked {

                }
            }
            label("x") {
                alignment = Pos.CENTER
                prefWidth = 35.0
                translateX = 828.0
                translateY = 634.0
                style {
                    backgroundColor += Color.WHITE
                }
            }
        }
    }

    private fun points(num: Int) {
        numPoints.value += num
    }

    private fun lblPoints(): Label = label {
        alignment = Pos.CENTER
        prefWidth = 150.0
        prefHeight = 75.0
        translateX = 1000.0
        translateY = 200.0
        style {
            backgroundColor += Color.AZURE
            fontSize = 18.px
        }
    }

    /**
     * Проверяем левую на 1 клетку
     * Проверяем правую на 1 клетку
     * Проверяем левую на 2 клетки (в случае если предыдущая совпала с передвинутой)
     * Проверяем правую на 2 клетки (аналогично второй левой)
     * Если не набралось хотя бы 2-1-0 или 1-0-1 или 0-1-2, то сбрасываем кол-во повторений (может их получилось 1) и:
     * Проверяем верхнюю на 1 клетку
     * Проверяем нижнюю на 1 клетку
     * Проверяем верхнюю на 2 клетки (аналогично второй левой)
     * Проверяем нижнюю на 2 клетки (аналогично второй левой)
     * Если и тут не набралось 2-1-0 или 1-0-1 или 0-1-2, то сбрасываем кол-во повторений и клетка возращается обратно.
     */
    private fun checkLengthDubIc(){

    }
}



class HelloWorldApp : App(HelloWorld::class) {
        override fun start(stage: Stage) {
            importStylesheet("/style.css")
            super.start(stage)
            stage.isFullScreen =  true
        }
}



/** private fun timer(): SimpleStringProperty {

}

private fun secundomer(): Label {
return label(timer()) {
alignment = Pos.CENTER
translateX = 1000.0
translateY = 100.0
prefWidth = 150.0
prefHeight = 75.0
style {
backgroundColor += Color.AZURE
fontSize = 18.px
}
isVisible = false
}
}

private fun startTimer(time: Long) {
timer.
}*/
/**private val back = button("Главное меню"){
translateX = 1100.0
translateY = 575.0
prefWidth = 250.0
style {
backgroundColor += Color.INDIGO
fontSize = 25.px
}
setOnMouseClicked {
imageview("/Icons/LOTR.jpg")
runVis()
progress.isVisible = true
exit.translateX = 558.0
exit.translateY = 536.0
isVisible = false
}
isVisible = false
}

fun runVis(): Button {
run.isVisible = true
return run
} */
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import tornadofx.*
import kotlin.system.exitProcess


class HelloWorld : View("Memories") {
    override val root = Pane()

    init {
        with(root) {
            val a = button("Достижения") {
            prefWidth = 250.0
            translateX = 558.0
            translateY = 460.0
            style {
                backgroundColor += Color.GREEN
                fontSize = 30.px
            }
            }
            val b = button("Выход") {
                prefWidth = 250.0
                translateX = 558.0
                translateY = 536.0
                style {
                    backgroundColor += Color.RED
                    fontSize = 30.px
                }
                action { exitProcess(1) }
            }
            button("Начало игры") {
                prefWidth = 250.0
                translateX = 558.0
                translateY = 384.0
                style {
                    backgroundColor += Color.AQUAMARINE
                    fontSize = 30.px
                }
                setOnMouseClicked {
                    pane {
                        screen()
                        while (count != 64) {
                            buttonImage()
                            count++
                        }
                        skills()
                        points()
                    }
                    isVisible = false
                    a.isVisible = false
                    b.translateX = 1000.0
                    b.translateY = 600.0
                }
            }

        }
    }


    private val list = mutableListOf<Int>()
    private var count = 0


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

    private fun screen(): ImageView {
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

                }
            }
            label("x") {
                prefWidth = 35.0
                translateX = 828.0
                translateY = 184.0
                style {
                    backgroundColor += Color.WHITE
                }
            }
            button("", imageview("/Icons/Flames.jpg")) {
                translateX = 800.0
                translateY = 250.0
                setOnMouseClicked {

                }
            }
            label("x") {
                prefWidth = 35.0
                translateX = 828.0
                translateY = 334.0
                style {
                    backgroundColor += Color.WHITE
                }
            }
            button("", imageview("/Icons/Bomb.png")) {
                translateX = 800.0
                translateY = 400.0
                setOnMouseClicked {

                }
            }
            label("x") {
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
                prefWidth = 35.0
                translateX = 828.0
                translateY = 634.0
                style {
                    backgroundColor += Color.WHITE
                }
            }
        }
    }

    private fun buttonImage(): Button {
        return button("", imageview(random())) {
            translateX = 92.0 + (count % 8) * 75.0
            translateY = 80.0 + count / 8 * 75.0
        }
    }

    private fun startTimer() {
        val timeline = Timeline(KeyFrame(Duration.seconds(0.0), EventHandler { advanceDuration() }), KeyFrame(Duration.seconds(1.0)))
        timeline.cycleCount = Animation.INDEFINITE
        timeline.play()
    }
    private var seconds = 0
    private var minutes = 0
    private var hours = 0

    private fun advanceDuration() {
        if (seconds < 59) {
            seconds++
        } else {
            seconds = 0
            if (minutes < 59) {
                minutes++
            }else{
                minutes = 0
                hours++
            }
        }
    }

    private fun points(): Label = label("КРЯЯЯЯ") {
        tornadofx.insets(0,0, 0, 15)
        prefWidth = 150.0
        prefHeight = 75.0
        translateX = 1000.0
        translateY = 200.0
        style {
            backgroundColor += Color.AZURE
            fontSize = 18.px
        }
    }
}



class HelloWorldApp : App(HelloWorld::class) {
        override fun start(stage: Stage) {
            importStylesheet("/style.css")
            super.start(stage)
            stage.isFullScreen =  true
        }
}

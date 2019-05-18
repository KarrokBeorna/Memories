import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
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
import kotlin.system.exitProcess


class Memories : View("Memories") {
    override val root = Pane()


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
                timeLabel.isVisible = true
                timeline()
                timeLabel.bind(time)
                progress.isVisible = false
                newExit()
                newProgress.isVisible = true
            }
    }

    private val time = SimpleStringProperty()

    private var seconds = 0
    private var minutes = 0
    private var hours = 0

    private fun timeline() {
        val timeline = Timeline(
            KeyFrame(Duration.seconds(0.0), EventHandler {
                if (seconds < 59) {
                    seconds++
                } else {
                    seconds = 0
                    if (minutes < 59) {
                        minutes++
                    } else {
                        minutes = 0
                        hours++
                    }
                }
                time.set(String.format("%02d:%02d:%02d", hours, minutes, seconds))
                timeInSeconds.value += 1
            }),
            KeyFrame(Duration.seconds(1.0))
        )
        timeline.cycleCount = Animation.INDEFINITE
        timeline.play()
    }

    private val timeLabel = label {
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
    private val listButtons = mutableListOf<Button>()
    private var count = 0
    private var numPoints = SimpleIntegerProperty()
    private var numPF = SimpleIntegerProperty() + numPoints
    private var timeInSeconds = SimpleIntegerProperty()
    private var tISN = SimpleIntegerProperty() + timeInSeconds


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
            val btn = button("", imageview(random())) {
                val runx = 92.0 + (count % 8) * 75.0
                val runy = 80 + (count / 8) * 75.0
                translateX = 92.0 + (count % 8) * 75.0
                translateY = 80.0 + count / 8 * 75.0
                setOnMouseDragged { event ->
                    val x = event.screenX
                    val y = event.screenY
                    if (x < runx) {translateX = runx - 75.0; translateY = runy; listButtons[i - 1].translateX = runx} else {
                        if (y < runy) {
                            translateY = runy - 75.0; translateX = runx
                        } else {
                            if (x > runx + 75.0) {
                                translateX = runx + 75.0; translateY = runy
                            } else {
                                if (y > runy + 75.0) {
                                    translateY = runy + 75.0; translateX = runx
                                } else {
                                    translateY = runy; translateX = runx
                                }
                            }
                        }
                    }
                }
            }
            count++
            listButtons.add(btn)
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
                bind(numPF / 100)
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
            val lblBomb = label {
                alignment = Pos.CENTER
                prefWidth = 35.0
                translateX = 828.0
                translateY = 484.0
                style {
                    backgroundColor += Color.WHITE
                }
                bind(tISN / 10)
            }
            button("", imageview("/Icons/Bomb.png")) {
                translateX = 800.0
                translateY = 400.0
                setOnMouseClicked {
                    if (tISN / 10 >= 1) {
                        count = 0
                        list.clear()
                        field64()
                        rIF()
                        points(64)
                        tISN -= 10
                        lblBomb.bind(tISN / 10)
                    }
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

    /** Значит, мы смотрим индекс элемента % 8, если он равен 0, то проверяем [index + 1] и [index + 2]
     *                                                  равен 1 , то проверяем [index - 1], [index + 1] и [index + 2]
     *                                                  равен 6, то проверяем [index - 1], [index + 1] и [index - 2]
     *                                                  равен 7, то проверяем [index - 1] и [index - 2]
     *                                    иначе проверяем 2 левые и 2 правые
     * Если верхняя проверка не собрала 3 элементов, то дальше мы смотрим индекс / 8 (предварительно сбросив счётчик),
     *                                          если он равен 0, то проверяем [index + 8] и [index + 16]
     *                                                  равен 1, то проверяем [index - 8], [index + 8] и [index + 16]
     *                                                  равен 6, то проверяем [index - 8], [index + 8] и [index - 16]
     *                                                  равен 7, то проверяем [index - 8] и [index - 16]
     *                                    иначе проверяем 2 верхние и 2 нижние
     * То же самое проделываем с клеткой, которую мы поменяли местами с нашей. По идее мы просто должны поменять
     * их индексы на некоторое время, чтобы облегчить сравнение.
     *
     * Если ни одна из клеток не собрала 3+ в ряд (столбец),
     * то индексы клеток и сами клетки приходят в исходные состояния.
     */
    private fun killingIcons(){

    }

    /** Восстановление кнопок будет происходить также, как и появление начальных элементов, то есть они не образуют
     * сразу 3 одинаковых клетки подряд.
     * Итак, предварительно мы должны узнать индекс первого уничтоженного элемента из прошлой функции, если он
     *            равен 0, то из random() убираем элементы, которые находятся на [index + 1] и [index + 8]
     *            равен 7, то из random() убираем элементы, которые находятся на [index - 1] и [index + 8]
     *            равен 56, то из random() убираем элементы, которые находятся на [index - 8] и [index + 1]
     *            равен 63, то из random() убираем элементы, которые находятся на [index - 8] и [index - 1]
     *            in 1..6, то из random() убираем элементы, которые находятся на [index - 1], [index + 1] и [index + 8]
     *            % 8 = 0, то из random() убираем элементы, которые находятся на [index - 8], [index + 1] и [index + 8]
     *            % 8 = 7, то из random() убираем элементы, которые находятся на [index - 8], [index - 1] и [index + 8]
     *          in 57..62, то из random() убираем элементы, которые находятся на [index - 8], [index - 1] и [index + 1]
     *     иначе из random() убираем элементы, которые находятся на [index - 8], [index - 1], [index + 8] и [index + 1]
     */
    private fun deadIcons() {

    }
}



class MemoriesApp : App(Memories::class) {
        override fun start(stage: Stage) {
            importStylesheet("/style.css")
            super.start(stage)
            stage.isFullScreen =  true
        }
}
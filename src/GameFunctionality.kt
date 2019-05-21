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
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*
import kotlin.system.exitProcess

class GameFunctionality : View()  {
    override val root = Pane()

    private val time = SimpleStringProperty()
    private var timeInSeconds = SimpleIntegerProperty()
    private var tISN = SimpleIntegerProperty() + timeInSeconds

    private var seconds = 0
    private var minutes = 0
    private var hours = 0

    private var numPoints = SimpleIntegerProperty()
    private var numPF = SimpleIntegerProperty() + numPoints

    var count = 0
    val list = mutableListOf<Int>()
    val listButtons = mutableListOf<Button>()


    fun startGame() {
        field64()
        randomField(63)
        skills()
        lblPoints().bind(numPoints)
        timeLabel.isVisible = true
        timeline()
        timeLabel.bind(time)
        progress.isVisible = false
        newExit()
        newProgress.isVisible = true
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
            startGame()
            isVisible = false
        }
    }

    private val progress = button("Достижения") {
        prefHeight = 60.0
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


    private fun newExit(): Button {
        exit.translateY = 700.0
        exit.translateX = 1250.0
        exit.prefWidth = 100.0
        exit.style {
            backgroundColor += Color.RED
            fontSize = 20.px
        }
        return exit
    }


    private val newProgress = button("", imageview("/Icons/VidIc.png")) {
        translateY = 640.0
        translateX = 1272.0
        setOnMouseClicked {
            sceneProgress()
        }
        isVisible = false
    }


    fun field64(): ImageView {
        return imageview("/Icons/Field.jpg") {
            translateX = 100.0
            translateY = 84.0
        }
    }


    fun random(): Int {
        var random = 0
        while (random == 0) {
            random = (1..6).random()
            when {
                count in 0..1 || count in 8..9 -> list += random

                count % 8 in 0..1 -> if (random == list[count - 16] && random == list[count - 8]) random = 0
                else list += random

                count in 2..7 || count in 10..15 -> if (random == list[count - 2] && random == list[count - 1]) random = 0
                else list += random

                else -> if ((random == list[count - 16] && random == list[count - 8])
                    || (random == list[count - 2] && random == list[count - 1]))
                    random = 0 else list += random
            }
        }
        return random
    }

    fun moving(index1: Int, index2: Int, she: Int, other: Int) {
        listButtons[index1].isVisible = false                                    //делаю 2 кнопки невидимыми
        listButtons[index2].isVisible = false
        listButtons.removeAt(index1)                                          //удаляю из листа кнопок нажатый элемент
        listButtons.removeAt(index2)                                          //и тот, с которым нужно поменяться местами
        list.removeAt(index1)                                                 //удаляю из листа 2 числа для иконок
        list.removeAt(index2)
        list.add(index2, she)                                                 //добавляю их обратно, меняя местами
        list.add(index1, other)
        listButtons.add(index2, icBtn(index2, she))                           //добавляю в лист кнопки с
        listButtons.add(index1, icBtn(index1, other))                         //новыми координатами, которые
                                                                              //могу продолжать двигать
    }

    fun icBtn(index: Int, num: Int): Button = button("", imageview(SomehowLater().randomImage(num))) {
        val runx = 92.0 + index % 8 * 75.0
        val runy = 80.0 + index / 8 * 75.0
        translateX = 92.0 + index % 8 * 75.0
        translateY = 80.0 + index / 8 * 75.0
        setOnMouseDragged { event ->
            val x = event.screenX
            val y = event.screenY
            val she = list[index]
            val left = list[index - 1]
            val right = list[index + 1]
            val top = list[index - 8]
            val bot = list[index + 8]
            when {
                x < runx -> if (index % 8 != 0) moving(index, index - 1, she, left)
                y < runy -> if (index in 8..63) moving(index, index - 8, she, top)
                x > runx + 75.0 -> if (index % 8 != 7) moving(index + 1, index, right, she) //чуть-чуть пришлось
                y > runy + 75.0 -> if (index in 0..55) moving(index + 8, index, bot, she)   //пошаманить...
            }
        }
    }


    fun randomField(num: Int) {
        for (i in 0..num) {
            val a = icBtn(i, random())
            count++
            listButtons.add(a)
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
                    listButtons.clear()
                    field64()
                    randomField(63)
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
                        field64()
                        randomField(63)
                        count = 0
                        list.clear()
                        listButtons.clear()
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


    fun points(num: Int) {
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
}
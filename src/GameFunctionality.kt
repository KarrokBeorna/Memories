import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*
import kotlin.system.exitProcess

class GameFunctionality : View()  {
    override val root = Pane(run())


    private val time = SimpleStringProperty()
    private var timeInSeconds = SimpleIntegerProperty()
    private var tISBomb = SimpleIntegerProperty() + timeInSeconds
    private var tISAcid = SimpleIntegerProperty() + timeInSeconds

    private var seconds = 0
    private var minutes = 0
    private var hours = 0

    private var numPoints = SimpleIntegerProperty()
    private var numPF = SimpleIntegerProperty() + numPoints
    private var doubling = SimpleIntegerProperty()

    private val listButtons = mutableListOf<Button>()
    private val list = arrayListOf<Int>()


    private fun startGame() {
        field64()
        lblPoints().bind(numPoints)
        timeLabel.isVisible = true
        timeline()
        timeLabel.bind(time)
        progress.isVisible = false
        newExit()
        newProgress.isVisible = true
        skills()
        randomField(63)
    }


    private fun run():Button = button("Начало игры") {
        prefWidth = 250.0
        translateX = 558.0
        translateY = 384.0
        style {
            backgroundColor += Color.AQUAMARINE
            fontSize = 30.px
        }
        setOnMouseClicked {
            isVisible = false
            startGame()
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
            openInternalWindow(MyFragment())
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
            openInternalWindow(MyFragment())
        }
        isVisible = false
    }


    private fun field64(): ImageView {
        return imageview("/Icons/Field.jpg") {
            translateX = 100.0
            translateY = 84.0
        }
    }


    private fun random(index: Int): Int {
        var random = 0
        while (random == 0) {
            random = (1..6).random()
            when {
                index in 0..1 || index in 8..9 -> return random

                index % 8 in 0..1 -> if (random == list[index - 16] && random == list[index - 8]) random = 0

                index in 2..7 || index in 10..15 -> if (random == list[index - 2] && random == list[index - 1]) random = 0

                else -> if ((random == list[index - 16] && random == list[index - 8])
                    || (random == list[index - 2] && random == list[index - 1])
                ) random = 0
            }
        }
        return random
    }


    private fun moving(index1: Int, index2: Int) {
        killCount2.clear()
        killingIcons(index1, index2)                                          //добавляю в список повторяющиеся
        killingIcons(index2, index1)                                          //элементы для перемещенных кнопок
        val she = list[index1]
        val other = list[index2]
        list.removeAt(index1)                                                 //удаляю из листа 2 числа для иконок
        list.removeAt(index2)
        list.add(index2, she)                                                 //добавляю их обратно, меняя местами
        list.add(index1, other)
        listButtons[index1].isVisible = false                                 //делаю 2 кнопки невидимыми
        listButtons[index2].isVisible = false
        listButtons.removeAt(index1)                                          //удаляю из листа кнопок нажатый элемент
        listButtons.removeAt(index2)                                          //и тот, с которым нужно поменяться местами
        listButtons.add(index2, icBtn(index2, she))                           //добавляю в лист кнопки с
        listButtons.add(index1, icBtn(index1, other))                         //новыми координатами, которые
                                                                              //могу продолжать двигать
        for (ind in killCount2.sortedDescending()) {
            listButtons[ind].isVisible = false                                //убираю кнопки с поля и с листов
            list.removeAt(ind)
            list.add(ind, 0)                                          //вставляю на место номеров картинок - нули
            listButtons.removeAt(ind)
        }

        for (ind in killCount2.sorted())                                      //создаю новые кнопки на месте уничтоженных
            renewal(ind)
        if (doubling.value > 0) {                                             //добавляю баллы
            points(killCount2.size * 2)
            doubling.value--
        } else points(killCount2.size)
    }


    private fun icBtn(index: Int, num: Int): Button = button("", imageview(SomehowLater().randomImage(num))) {
        val runx = 92.0 + index % 8 * 75.0
        val runy = 80.0 + index / 8 * 75.0
        translateX = runx
        translateY = runy
        setOnMouseDragged { event ->
            val x = event.screenX
            val y = event.screenY                           //действие с левой кнопкой
            if (x < runx && index % 8 != 0 && (killingIcons(index, index - 1) || (killingIcons(index - 1, index)))) {
                moving(index, index - 1)
            } else {                                            //действие с верхней кнопкой
                if (y < runy && index in 8..63 && (killingIcons(index, index - 8) || (killingIcons(index - 8, index)))) {
                    moving(index, index - 8)
                } else {                                            //действие с правой кнопкой
                    if (x > runx + 75.0 && index % 8 != 7 && (killingIcons(index, index + 1) || (killingIcons(index + 1, index)))) {
                        moving(index + 1, index)
                    } else {                                            //действие с нижней кнопкой
                        if (y > runy + 75.0 && index in 0..55 && (killingIcons(index, index + 8) || (killingIcons(index + 8, index)))) {
                            moving(index + 8, index)
                        }
                    }
                }
            }
        }
    }


    private fun randomField(num: Int) {
        for (i in 0..num) {
            val rnd = random(i)
            val a = icBtn(i, rnd)
            list.add(rnd)
            listButtons.add(a)
        }
    }


    private fun skills(): Pane {
        return pane {
            button("", imageview("/Icons/Aquaman.png")) {
                translateX = 800.0
                translateY = 100.0
                setOnMouseClicked {
                    listButtons.clear()
                    list.clear()
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
                setOnMouseDragged {
                    if (numPF / 100 >= 1) {
                        setOnMouseReleased { event ->
                            val indexY = ((event.screenY - 80) / 75).toInt()
                            val indexX = ((event.screenX - 92) / 75).toInt()
                            val index = indexY * 8 + indexX
                            if (indexX > 7 || indexY > 7) burning(1000) else burning(index)
                            points(16)
                            numPF -= 100
                            lblFlames.bind(numPF / 100)
                        }
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
                bind(tISBomb / 10)
            }
            button("", imageview("/Icons/Bomb.png")) {
                translateX = 800.0
                translateY = 400.0
                setOnMouseClicked {
                    if (tISBomb / 10 >= 1) {
                        list.clear()
                        listButtons.clear()
                        field64()
                        randomField(63)
                        points(64)
                        tISBomb -= 10
                        lblBomb.bind(tISBomb / 10)
                    }
                }

            }
            val lblAcid = label {
                alignment = Pos.CENTER
                prefWidth = 35.0
                translateX = 828.0
                translateY = 634.0
                style {
                    backgroundColor += Color.WHITE
                }
                bind(tISAcid / 10)
            }
            button("", imageview("/Icons/Acid.jpg")) {
                translateX = 800.0
                translateY = 550.0
                setOnMouseClicked {
                    if (tISAcid / 10 >= 1) {
                        doubling.value += 10
                        tISAcid -= 10
                        lblAcid.bind(tISAcid / 10)
                    }
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



    /**   1) Для разных видов перемещения сравниваемся с разными индексами. Сначала по горизонтали, затем по вертикали.
     *    2) Добавляем в killCount индексы кнопок, которые необходимо уничтожить.
     * То же самое проделываем с кнопкой, которую мы поменяли местами с нашей.
     *    3) Далее уничтожаем их. Удаляем из листа кнопок эти кнопки. Удаляем из листа картинок для кнопок эти картинки
     * и вставляем на их индексы временные нули для упрощения дальнейшего появления новых клеток.
     *    4) Создаем новые клетки, которые не создадут случай мгновенного образования трёх одинаковых клеток.
     * При этом, конечно, до создания сначала перезаписываем нули в листе на нормальные значения картинок.
     */

    private val killCount = mutableSetOf<Int>()
    private val killCount2 = mutableSetOf<Int>()

    private fun killLength(index: Int, index2: Int){
        val minus = index - index2
        killCount.clear()
        killCount.add(index2)
        checkLeft(index, minus)
        checkRight(index, minus)
        if (killCount.size < 3) {
            killCount.clear()
            killCount.add(index2)
            checkTop(index, minus)
            checkBottom(index, minus)
        }
    }

    private fun killingIcons(index: Int, index2: Int):Boolean {
        killLength(index, index2)
        if (killCount.size >= 3) {
            killCount.forEach { killCount2.add(it) }
            return true
        }
        return false
    }


    private fun checkRight(index: Int, minus: Int) {
        when (minus) {
            -1 -> when {
                index % 8 == 5 -> if (list[index] == list[index + 2]) killCount.add(index + 2)
                index % 8 in 0..4 -> if (list[index] == list[index + 3] && list[index] == list[index + 2]) {
                    killCount.add(index + 3)
                    killCount.add(index + 2)
                } else if (list[index] == list[index + 2]) killCount.add(index + 2)
            }
            8 -> when {
                index % 8 == 6 -> if (list[index] == list[index - 7]) killCount.add(index - 7)
                index % 8 in 0..5 -> if (list[index] == list[index - 7] && list[index] == list[index - 6]) {
                    killCount.add(index - 7)
                    killCount.add(index - 6)
                } else if (list[index] == list[index - 7]) killCount.add(index - 7)
            }
            -8 -> when {
                index % 8 == 6 -> if (list[index] == list[index + 9]) killCount.add(index + 9)
                index % 8 in 0..5 -> if (list[index] == list[index + 9] && list[index] == list[index + 10]) {
                    killCount.add(index + 9)
                    killCount.add(index + 10)
                } else if (list[index] == list[index + 9]) killCount.add(index + 9)
            }
        }
    }
    private fun checkLeft(index: Int, minus: Int){
        when (minus) {
            1 -> when {
                index % 8 == 2 -> if (list[index] == list[index - 2]) killCount.add(index - 2)
                index % 8 in 3..7 -> if (list[index] == list[index - 2] && list[index] == list[index - 3]) {
                    killCount.add(index - 2)
                    killCount.add(index - 3)
                } else if (list[index] == list[index - 2]) killCount.add(index - 2)
            }
            8 -> when {
                index % 8 == 1 -> if (list[index] == list[index - 9]) killCount.add(index - 9)
                index % 8 in 2..7 -> if (list[index] == list[index - 9] && list[index] == list[index - 10]) {
                    killCount.add(index - 9)
                    killCount.add(index - 10)
                } else if (list[index] == list[index - 9]) killCount.add(index - 9)
            }
            -8 -> when {
                index % 8 == 1 -> if (list[index] == list[index + 7]) killCount.add(index + 7)
                index % 8 in 2..7 -> if (list[index] == list[index + 7] && list[index] == list[index + 6]) {
                    killCount.add(index + 7)
                    killCount.add(index + 6)
                } else if (list[index] == list[index + 7]) killCount.add(index + 7)
            }
        }
    }
    private fun checkTop(index: Int, minus: Int){
        when (minus) {
            -1 -> when {
                index / 8 == 1 -> if (list[index] == list[index - 7]) killCount.add(index - 7)
                index / 8 in 2..7 -> if (list[index] == list[index - 7] && list[index] == list[index - 15]) {
                    killCount.add(index - 7)
                    killCount.add(index - 15)
                } else if (list[index] == list[index - 7]) killCount.add(index - 7)
            }
            8 -> when {
                index / 8 == 2 -> if (list[index] == list[index - 16]) killCount.add(index - 16)
                index / 8 in 3..7 -> if (list[index] == list[index - 16] && list[index] == list[index - 24]) {
                    killCount.add(index - 16)
                    killCount.add(index - 24)
                } else if (list[index] == list[index - 16]) killCount.add(index - 16)
            }
            1 -> when {
                index / 8 == 1 -> if (list[index] == list[index - 9]) killCount.add(index - 9)
                index / 8 in 2..7 -> if (list[index] == list[index - 9] && list[index] == list[index - 17]) {
                    killCount.add(index - 9)
                    killCount.add(index - 17)
                } else if (list[index] == list[index - 9]) killCount.add(index - 9)
            }
        }
    }
    private fun checkBottom(index: Int, minus: Int){
        when (minus) {
            -1 -> when {
                index / 8 == 6 -> if (list[index] == list[index + 9]) killCount.add(index + 9)
                index / 8 in 0..5 -> if (list[index] == list[index + 9] && list[index] == list[index + 17]) {
                    killCount.add(index + 9)
                    killCount.add(index + 17)
                } else if (list[index] == list[index + 9]) killCount.add(index + 9)
            }
            1 -> when {
                index / 8 == 6 -> if (list[index] == list[index + 7]) killCount.add(index + 7)
                index / 8 in 0..5 -> if (list[index] == list[index + 7] && list[index] == list[index + 15]) {
                    killCount.add(index + 7)
                    killCount.add(index + 15)
                } else if (list[index] == list[index + 7]) killCount.add(index + 7)
            }
            -8 -> when {
                index / 8 == 5 -> if (list[index] == list[index + 16]) killCount.add(index + 16)
                index / 8 in 0..4 -> if (list[index] == list[index + 16] && list[index] == list[index + 24]) {
                    killCount.add(index + 16)
                    killCount.add(index + 24)
                } else if (list[index] == list[index + 16]) killCount.add(index + 16)
            }
        }
    }


    private fun randomForRenewal(index: Int): Int {
        var random = 0
        while (random == 0) {
            random = (1..6).random()
            if (index / 8 in 0..5) if (random == list[index + 8] && random == list[index + 16]) random = 0
            if (index / 8 in 2..7) if (random == list[index - 8] && random == list[index - 16]) random = 0
            if (index % 8 in 0..5) if (random == list[index + 1] && random == list[index + 2]) random = 0
            if (index % 8 in 2..7) if (random == list[index - 1] && random == list[index - 2]) random = 0
            if (index / 8 in 1..6) if (random == list[index - 8] && random == list[index + 8]) random = 0
            if (index % 8 in 1..6) if (random == list[index - 1] && random == list[index + 1]) random = 0
        }
        list.removeAt(index)
        list.add(index, random)
        return random
    }


    private fun renewal(index: Int) {
        listButtons.add(index, icBtn(index, randomForRenewal(index)))
    }


    private fun burning(index: Int) {
        killCount2.clear()
        when {
            index % 8 in 0..4 && index / 8 in 0..4 ->
                for (i in 0..3) killCount2 += listOf(index + i*8, index + 1 + i*8, index + 2 + i*8, index + 3 + i*8)
            index % 8 in 5..7 && index / 8 in 0..4 ->
                for (i in 0..3) killCount2 += listOf(index + i*8, index - 1 + i*8, index - 2 + i*8, index - 3 + i*8)
            index % 8 in 0..4 && index / 8 in 5..7 ->
                for (i in 0..3) killCount2 += listOf(index - i*8, index + 1 - i*8, index + 2 - i*8, index + 3 - i*8)
            index % 8 in 5..7 && index / 8 in 5..7 ->
                for (i in 0..3) killCount2 += listOf(index - i*8, index - 1 - i*8, index - 2 - i*8, index - 3 - i*8)
            else -> killCount2 += listOf(18, 19, 20, 21, 26, 27, 28, 29, 34, 35, 36, 37, 42, 43, 44, 45)
        }
        for (ind in killCount2.sortedDescending()) {
            listButtons[ind].isVisible = false
            list.removeAt(ind)
            list.add(ind, 0)
            listButtons.removeAt(ind)
        }
        for (ind in killCount2.sorted())
            renewal(ind)
    }
}

class MyFragment: Fragment() {
    override val root = pane {
        translateY = -33.0
        translateX = -5.0
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
            button("Назад") {
                prefWidth = 70.0
                prefHeight = 40.0
                translateX = 1280.0
                translateY = 670.0
                style {
                    backgroundColor += Color.GREENYELLOW
                    fontSize = 15.px
                }
                setOnMouseClicked {
                    close()
                }
            }
        }
}
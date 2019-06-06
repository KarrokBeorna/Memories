import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*
import java.io.File
import kotlin.system.exitProcess

class GameFunctionality : Fragment()  {
    override val root = Pane()


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

    private val listButtons = mutableListOf<ImageView>()
    private val list = mutableListOf<Int>()


    private fun startGame() {                       //действие при нажатии кнопки "Начало игры"
        lblPoints().bind(numPoints)
        timeLabel.isVisible = true
        timeline()
        timeLabel.bind(time)
        progress.isVisible = false
        newExit()
        newProgress.isVisible = true
        skills()
        field64()
        randomField()
    }


    private val run = button("Начало игры") {
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
            find<Achievements>().openModal(StageStyle.UNDECORATED)
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
        setOnMouseClicked {
            val timeFile = File("Time.txt")
            val timePoints = timeFile.readText()
            val newTime = Integer.parseInt(timePoints) + timeInSeconds.value
            val pointsFile = File("Points.txt")
            val points = pointsFile.readText()
            val newPoints = Integer.parseInt(points) + numPoints.value
            timeFile.bufferedWriter().use {out -> out.write("$newTime")}
            pointsFile.bufferedWriter().use {out -> out.write("$newPoints")}
            exitProcess(1)
        }
    }


    private fun timeline() {                                                      //секундомер
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


    private val timeLabel = label {                     //панель для времени
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
        tooltip("Прошедшее время. При выходе\nдобавляется ко времени из прошлых игр") {
            font = Font.font(13.0)
        }
    }


    private fun newExit(): Button {                                 //перемещение "Выхода" при запуске игры
        exit.translateY = 700.0
        exit.translateX = 1250.0
        exit.prefWidth = 100.0
        exit.style {
            backgroundColor += Color.RED
            fontSize = 20.px
        }
        return exit
    }


    private val newProgress = button("", imageview("/Icons/VidIc.png")) {       //новая иконка достижений
        translateY = 640.0                                                               //при запуске игры
        translateX = 1272.0
        setOnMouseClicked {
            find<Achievements>().openModal(StageStyle.UNDECORATED)
        }
        isVisible = false
    }


    private fun field64(): ImageView {                                      //игровое поле
        return imageview("/Icons/Field.jpg") {
            translateX = 100.0
            translateY = 84.0
        }
    }


    private fun random(index: Int): Int {                            //рандомные картинки для обновлений игрового поля
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
            list.add(ind, 0)                                                  //вставляю на место номеров картинок - нули
            listButtons.removeAt(ind)
        }

        for (ind in killCount2.sorted())                                      //создаю новые кнопки на месте уничтоженных
            renewal(ind)

        if (doubling.value > 0) {                                             //добавляю баллы
            points(killCount2.size * 2)
            doubling.value--
        } else points(killCount2.size)
    }


    private fun icBtn(index: Int, num: Int): ImageView {
        return imageview(randomImage(num)) {
        val runx = 100.0 + index % 8 * 75.0
        val runy = 84.0 + index / 8 * 75.0
        translateX = runx                                                   //главный игровой элемент - кнопка
        translateY = runy
        setOnMouseReleased { event ->
            val x = event.screenX
            val y = event.screenY                                      //действие с левой кнопкой
            if (x < runx && index % 8 != 0 && (killingIcons(index, index - 1) || (killingIcons(index - 1, index)))) {
                moving(index, index - 1)
            }                                                          //действие с верхней кнопкой
            else if (y < runy && index in 8..63 && (killingIcons(index, index - 8) || (killingIcons(index - 8, index)))) {
                moving(index, index - 8)
            }                                                          //действие с правой кнопкой
            else if (x > runx + 75.0 && index % 8 != 7 && (killingIcons(index, index + 1) || (killingIcons(index + 1, index)))) {
                moving(index + 1, index)
            }                                                          //действие с нижней кнопкой
            else if (y > runy + 75.0 && index in 0..55 && (killingIcons(index, index + 8) || (killingIcons(index + 8, index)))) {
                moving(index + 8, index)
            }
        }
    }
    }


    private fun randomField() {             //запускает появление 64 кнопок на игровом поле
        for (i in 0..63) {
            val rnd = random(i)
            val a = icBtn(i, rnd)
            list.add(rnd)
            listButtons.add(a)
        }
    }


    private fun skills(): Pane {                //способности и их панельки с количеством
        return pane {
            button("", imageview("/Icons/Aquaman.png")) {
                translateX = 800.0
                translateY = 100.0
                setOnMouseClicked {
                    listButtons.clear()
                    list.clear()
                    field64()
                    randomField()
                }
                tooltip("Обновляет поле при нажатии") {
                    font = Font.font(13.0)
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
                tooltip("Уничтожает 16 элементов, в\nзависимости, куда наведен курсор.\nДобавляется каждые 100 баллов") {
                    font = Font.font(13.0)
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
                bind(tISBomb / 300)
            }
            button("", imageview("/Icons/Bomb.png")) {
                translateX = 800.0
                translateY = 400.0
                setOnMouseClicked {
                    if (tISBomb / 300 >= 1) {
                        list.clear()
                        listButtons.clear()
                        field64()
                        randomField()
                        points(64)
                        tISBomb -= 300
                        lblBomb.bind(tISBomb / 300)
                    }
                }
                tooltip("Уничтожает всё поле.\nДобавляется каждые 5 минут") {
                    font = Font.font(13.0)
                }
            }
            val lblAcid = label {
                alignment = Pos.CENTER
                prefWidth = 30.0
                translateX = 853.0
                translateY = 634.0
                style {
                    backgroundColor += Color.WHITE
                }
                bind(tISAcid / 180)
            }
            label {
                alignment = Pos.CENTER
                prefWidth = 30.0
                translateX = 808.0
                translateY = 634.0
                style {
                    backgroundColor += Color.WHITE
                }
                bind(doubling)
                tooltip("Количество оставшихся удвоений") {
                    font = Font.font(13.0)
                }
            }
            button("", imageview("/Icons/Acid.jpg")) {
                translateX = 800.0
                translateY = 550.0
                setOnMouseClicked {
                    if (tISAcid / 180 >= 1) {
                        doubling.value += 10
                        tISAcid -= 180
                        lblAcid.bind(tISAcid / 180)
                    }
                }
                tooltip("При нажатии удваивает баллы\nна 10 уничтожений. Добавляется\nкаждые 3 минуты") {
                    font = Font.font(13.0)
                }
            }
        }
    }


    private fun points(num: Int) {                  //добавление баллов в счётчик
        numPoints.value += num
    }


    private fun lblPoints(): Label = label {                //панель для баллов
        alignment = Pos.CENTER
        prefWidth = 150.0
        prefHeight = 50.0
        translateX = 1000.0
        translateY = 200.0
        style {
            backgroundColor += Color.AZURE
            fontSize = 18.px
        }
        tooltip("Количество баллов. При выходе\nдобавляется к баллам из прошлых игр") {
            font = Font.font(13.0)
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

    private fun killLength(index1: Int, index2: Int){                  //проверяем кнопку на одинаковые элементы по
        val minus = index1 - index2                                    //горизонтали и по вертикали (если необходимо)
        killCount.clear()
        killCount.add(index2)
        checkLeft(index1, minus)
        checkRight(index1, minus)
        if (killCount.size < 3) {
            killCount.clear()
            killCount.add(index2)
            checkTop(index1, minus)
            checkBottom(index1, minus)
        }
    }

    private fun killingIcons(index1: Int, index2: Int):Boolean {        //добавление кнопок для уничтожения
        killLength(index1, index2)
        if (killCount.size >= 3) {
            killCount.forEach { killCount2.add(it) }
            return true
        }
        return false
    }


    private fun checkRight(index1: Int, minus: Int) {
        when (minus) {
            -1 -> when {
                index1 % 8 == 5 -> if (list[index1] == list[index1 + 2]) killCount.add(index1 + 2)
                index1 % 8 in 0..4 -> if (list[index1] == list[index1 + 3] && list[index1] == list[index1 + 2]) {
                    killCount.add(index1 + 3)
                    killCount.add(index1 + 2)
                } else if (list[index1] == list[index1 + 2]) killCount.add(index1 + 2)
            }
            8 -> when {
                index1 % 8 == 6 -> if (list[index1] == list[index1 - 7]) killCount.add(index1 - 7)
                index1 % 8 in 0..5 -> if (list[index1] == list[index1 - 7] && list[index1] == list[index1 - 6]) {
                    killCount.add(index1 - 7)
                    killCount.add(index1 - 6)
                } else if (list[index1] == list[index1 - 7]) killCount.add(index1 - 7)
            }
            -8 -> when {
                index1 % 8 == 6 -> if (list[index1] == list[index1 + 9]) killCount.add(index1 + 9)
                index1 % 8 in 0..5 -> if (list[index1] == list[index1 + 9] && list[index1] == list[index1 + 10]) {
                    killCount.add(index1 + 9)
                    killCount.add(index1 + 10)
                } else if (list[index1] == list[index1 + 9]) killCount.add(index1 + 9)
            }
        }
    }
    private fun checkLeft(index1: Int, minus: Int){
        when (minus) {
            1 -> when {
                index1 % 8 == 2 -> if (list[index1] == list[index1 - 2]) killCount.add(index1 - 2)
                index1 % 8 in 3..7 -> if (list[index1] == list[index1 - 2] && list[index1] == list[index1 - 3]) {
                    killCount.add(index1 - 2)
                    killCount.add(index1 - 3)
                } else if (list[index1] == list[index1 - 2]) killCount.add(index1 - 2)
            }
            8 -> when {
                index1 % 8 == 1 -> if (list[index1] == list[index1 - 9]) killCount.add(index1 - 9)
                index1 % 8 in 2..7 -> if (list[index1] == list[index1 - 9] && list[index1] == list[index1 - 10]) {
                    killCount.add(index1 - 9)
                    killCount.add(index1 - 10)
                } else if (list[index1] == list[index1 - 9]) killCount.add(index1 - 9)
            }
            -8 -> when {
                index1 % 8 == 1 -> if (list[index1] == list[index1 + 7]) killCount.add(index1 + 7)
                index1 % 8 in 2..7 -> if (list[index1] == list[index1 + 7] && list[index1] == list[index1 + 6]) {
                    killCount.add(index1 + 7)
                    killCount.add(index1 + 6)
                } else if (list[index1] == list[index1 + 7]) killCount.add(index1 + 7)
            }
        }
    }
    private fun checkTop(index1: Int, minus: Int){
        when (minus) {
            -1 -> when {
                index1 / 8 == 1 -> if (list[index1] == list[index1 - 7]) killCount.add(index1 - 7)
                index1 / 8 in 2..7 -> if (list[index1] == list[index1 - 7] && list[index1] == list[index1 - 15]) {
                    killCount.add(index1 - 7)
                    killCount.add(index1 - 15)
                } else if (list[index1] == list[index1 - 7]) killCount.add(index1 - 7)
            }
            8 -> when {
                index1 / 8 == 2 -> if (list[index1] == list[index1 - 16]) killCount.add(index1 - 16)
                index1 / 8 in 3..7 -> if (list[index1] == list[index1 - 16] && list[index1] == list[index1 - 24]) {
                    killCount.add(index1 - 16)
                    killCount.add(index1 - 24)
                } else if (list[index1] == list[index1 - 16]) killCount.add(index1 - 16)
            }
            1 -> when {
                index1 / 8 == 1 -> if (list[index1] == list[index1 - 9]) killCount.add(index1 - 9)
                index1 / 8 in 2..7 -> if (list[index1] == list[index1 - 9] && list[index1] == list[index1 - 17]) {
                    killCount.add(index1 - 9)
                    killCount.add(index1 - 17)
                } else if (list[index1] == list[index1 - 9]) killCount.add(index1 - 9)
            }
        }
    }
    private fun checkBottom(index1: Int, minus: Int){
        when (minus) {
            -1 -> when {
                index1 / 8 == 6 -> if (list[index1] == list[index1 + 9]) killCount.add(index1 + 9)
                index1 / 8 in 0..5 -> if (list[index1] == list[index1 + 9] && list[index1] == list[index1 + 17]) {
                    killCount.add(index1 + 9)
                    killCount.add(index1 + 17)
                } else if (list[index1] == list[index1 + 9]) killCount.add(index1 + 9)
            }
            1 -> when {
                index1 / 8 == 6 -> if (list[index1] == list[index1 + 7]) killCount.add(index1 + 7)
                index1 / 8 in 0..5 -> if (list[index1] == list[index1 + 7] && list[index1] == list[index1 + 15]) {
                    killCount.add(index1 + 7)
                    killCount.add(index1 + 15)
                } else if (list[index1] == list[index1 + 7]) killCount.add(index1 + 7)
            }
            -8 -> when {
                index1 / 8 == 5 -> if (list[index1] == list[index1 + 16]) killCount.add(index1 + 16)
                index1 / 8 in 0..4 -> if (list[index1] == list[index1 + 16] && list[index1] == list[index1 + 24]) {
                    killCount.add(index1 + 16)
                    killCount.add(index1 + 24)
                } else if (list[index1] == list[index1 + 16]) killCount.add(index1 + 16)
            }
        }
    }


    private fun randomForRenewal(index: Int): Int {             //рандомные элементы после уничтожения кнопок при
        var random = 0                                          //перемещении и при сгорании от Flames
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


    private fun renewal(index: Int) {                                       //возрождение кнопки
        listButtons.add(index, icBtn(index, randomForRenewal(index)))
    }


    private fun burning(index: Int) {                     //сгорание 16 клеток в зависимости от местоположения курсора
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
        for (ind in killCount2.sorted())               //возрождение 16 клеток
            renewal(ind)
    }
}
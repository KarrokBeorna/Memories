import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.Duration
import tornadofx.Controller

class Logics : Controller() {

    val list = mutableListOf<Int>()
    val listButtons = mutableListOf<ImageView>()

    var doubling = SimpleIntegerProperty()
    var numPoints = SimpleIntegerProperty()

    val time = SimpleStringProperty()
    var timeInSeconds = SimpleIntegerProperty()
    private var seconds = 0
    private var minutes = 0
    private var hours = 0


    //секундомер
    fun timeline() {
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


    //иконки для кнопок на поле
    fun random(index: Int): Int {
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


    //подсчёт баллов
    fun points(num: Int) {
        numPoints.value += num
    }


    //перемещение кнопок
    fun swap(index1: Int, index2: Int){
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
        listButtons.removeAt(index2)                                          //и элемент, с которым хочу поменяться
    }
    fun reincarnation() {
        for (ind in killCount2.sortedDescending()) {
            listButtons[ind].isVisible = false                                //убираю кнопки с поля и с листов
            list.removeAt(ind)
            list.add(ind, 0)                                                  //вставляю на место номеров картинок - нули
            listButtons.removeAt(ind)
        }

        if (doubling.value > 0) {                                             //добавляю баллы
            points(killCount2.size * 2)
            doubling.value--
        } else points(killCount2.size)
    }


    /**   1) Для разных видов перемещения сравниваемся с разными индексами. Сначала по горизонтали, затем по вертикали.
     *    2) Добавляем в killCount индексы кнопок, которые необходимо уничтожить.
     * То же самое проделываем с кнопкой, которую мы поменяли местами с нашей.
     *    3) Далее уничтожаем их. Удаляем из листа кнопок эти кнопки. Удаляем из листа картинок для кнопок эти картинки
     * и вставляем на их индексы временные нули для упрощения дальнейшего появления новых клеток.
     *    4) Создаем новые клетки, которые не создадут случай мгновенного образования трёх одинаковых клеток.
     * При этом, конечно, до создания сначала перезаписываем нули в листе на нормальные значения картинок.
     */


    //проверка и уничтожение кнопок
    private val killCount = mutableSetOf<Int>()
    val killCount2 = mutableSetOf<Int>()

    fun killingIcons(index1: Int, index2: Int):Boolean {
        killLength(index1, index2)
        if (killCount.size >= 3) {
            killCount.forEach { killCount2.add(it) }
            return true
        }
        return false
    }
    private fun killLength(index1: Int, index2: Int){
        val minus = index1 - index2
        killCount.clear()
        killCount.add(index2)
        checkLeft(index1, index2, minus)
        checkRight(index1, index2, minus)
        if (killCount.size < 3) {
            killCount.clear()
            killCount.add(index2)
            checkTop(index1, index2, minus)
            checkBottom(index1, index2, minus)
        }
    }

    private fun checkRight(index1: Int, index2: Int, minus: Int) {
        val central = list[index1]
        if (minus != 1) when (index2 % 8) {
            6 -> if (central == list[index2 + 1]) killCount.add(index2 + 1)
            in 0..5 -> if (central == list[index2 + 1] && central == list[index2 + 2]) {
                killCount.add(index2 + 1)
                killCount.add(index2 + 2)
            } else if (central == list[index2 + 1]) killCount.add(index2 + 1)
        }
    }
    private fun checkLeft(index1: Int, index2: Int, minus: Int) {
        val central = list[index1]
        if (minus != -1) when (index2 % 8) {
            1 -> if (central == list[index2 - 1]) killCount.add(index2 - 1)
            in 2..7 -> if (central == list[index2 - 1] && central == list[index2 - 2]) {
                killCount.add(index2 - 1)
                killCount.add(index2 - 2)
            } else if (central == list[index2 - 1]) killCount.add(index2 - 1)
        }
    }
    private fun checkTop(index1: Int, index2: Int, minus: Int) {
        val central = list[index1]
        if (minus != -8) when (index2 / 8) {
            1 -> if (central == list[index2 - 8]) killCount.add(index2 - 8)
            in 2..7 -> if (central == list[index2 - 8] && central == list[index2 - 16]) {
                killCount.add(index2 - 8)
                killCount.add(index2 - 16)
            } else if (central == list[index2 - 8]) killCount.add(index2 - 8)
        }
    }
    private fun checkBottom(index1: Int, index2: Int, minus: Int) {
        val central = list[index1]
        if (minus != 8) when (index2 / 8) {
            6 -> if (central == list[index2 + 8]) killCount.add(index2 + 8)
            in 0..5 -> if (central == list[index2 + 8] && central == list[index2 + 16]) {
                killCount.add(index2 + 8)
                killCount.add(index2 + 16)
            } else if (central == list[index2 + 8]) killCount.add(index2 + 8)
        }
    }

    //иконка для появления после уничтожения клетки
    fun randomForRenewal(index: Int): Int {
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


    //действия для способности Flames
    fun burning(index: Int) {                     //сгорание 16 клеток в зависимости от местоположения курсора
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
    }


}


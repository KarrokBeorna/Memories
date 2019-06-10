import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.StageStyle
import tornadofx.*
import java.io.File
import kotlin.system.exitProcess

class Screen : Fragment()  {
    override val root = Pane()

    private val controller: Logics by inject()
    private val audio: Audio by inject()

    private var tISBomb = SimpleIntegerProperty() + controller.timeInSeconds
    private var tISAcid = SimpleIntegerProperty() + controller.timeInSeconds
    private var numPF = SimpleIntegerProperty() + controller.numPoints


    //"начало игры" в главном меню
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
            lblPoints.bind(controller.numPoints)
            timeLabel.isVisible = true
            controller.timeline()
            timeLabel.bind(controller.time)
            progress.isVisible = false
            newExit()
            newProgress.isVisible = true
            skills()
            field64()
            randomField()
        }
    }


    //"достижения" в главное меню
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
            audio.mdp.pause()
            find<Achievements>().openModal(StageStyle.UNDECORATED, escapeClosesWindow = false)
        }
    }


    //"выход" в главном меню
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
            val newTime = Integer.parseInt(timePoints) + controller.timeInSeconds.value
            timeFile.bufferedWriter().use {out -> out.write("$newTime")}

            val pointsFile = File("Points.txt")
            val points = pointsFile.readText()
            val newPoints = Integer.parseInt(points) + controller.numPoints.value
            pointsFile.bufferedWriter().use {out -> out.write("$newPoints")}

            exitProcess(1)
        }
    }


    //панелька для секундомера
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
        tooltip("Прошедшее время. При выходе\nдобавляется ко времени из прошлых игр") {
            font = Font.font(13.0)
        }
    }


    //новый "выход" после начала игры
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


    //новая кнопка достижений после начала игры
    private val newProgress = button("", imageview("/Icons/VidIc.png")) {
        translateY = 640.0
        translateX = 1272.0
        setOnMouseClicked {
            find<Achievements>().openModal(StageStyle.UNDECORATED, escapeClosesWindow = false)
            audio.mdp.pause()
        }
        isVisible = false
    }


    //задний план поля 8х8
    private fun field64(): ImageView {
        return imageview("/Icons/Field.jpg") {
            translateX = 100.0
            translateY = 84.0
        }
    }


    //перемещение, связанное с Logics
    private fun moving(index1: Int, index2: Int) {
        controller.swap(index1, index2)
        controller.listButtons.add(index2, icBtn(index2, controller.list[index2]))
        controller.listButtons.add(index1, icBtn(index1, controller.list[index1]))
        controller.reincarnation()
        for (ind in controller.killCount2.sorted())
            renewal(ind)
    }


    //кнопка на поле
    private fun icBtn(index: Int, num: Int): ImageView {
        return imageview(controller.randomImage(num)) {
        val runx = 100.0 + index % 8 * 75.0
        val runy = 84.0 + index / 8 * 75.0
        translateX = runx                                                   //главный игровой элемент - кнопка
        translateY = runy
        setOnMouseReleased { event ->
            val x = event.screenX
            val y = event.screenY                                      //действие с левой кнопкой
            if (x < runx && index % 8 != 0 &&
                (controller.killingIcons(index, index - 1) || (controller.killingIcons(index - 1, index)))) {
                moving(index, index - 1)
            }                                                          //действие с верхней кнопкой
            else if (y < runy && index in 8..63 &&
                (controller.killingIcons(index, index - 8) || (controller.killingIcons(index - 8, index)))) {
                moving(index, index - 8)
            }                                                          //действие с правой кнопкой
            else if (x > runx + 75.0 && index % 8 != 7 &&
                (controller.killingIcons(index, index + 1) || (controller.killingIcons(index + 1, index)))) {
                moving(index + 1, index)
            }                                                          //действие с нижней кнопкой
            else if (y > runy + 75.0 && index in 0..55 &&
                (controller.killingIcons(index, index + 8) || (controller.killingIcons(index + 8, index)))) {
                moving(index + 8, index)
            }
        }
    }
    }


    //появление 64 кнопок, необходимое для начала игры и 2 способностей
    private fun randomField() {
        for (i in 0..63) {
            val rnd = controller.random(i)
            val a = icBtn(i, rnd)
            controller.list.add(rnd)
            controller.listButtons.add(a)
        }
    }


    //способности и их панельки для кол-ва
    private fun skills(): Pane {
        return pane {
            button("", imageview("/Icons/Aquaman.png")) {
                translateX = 800.0
                translateY = 100.0
                setOnMouseClicked {
                    controller.listButtons.clear()
                    controller.list.clear()
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
                setOnMouseReleased { event ->
                    if (numPF / 100 >= 1) {
                        val indexY = ((event.screenY - 80) / 75).toInt()
                        val indexX = ((event.screenX - 92) / 75).toInt()
                        val index = indexY * 8 + indexX
                        if (indexX > 7 || indexY > 7) controller.burning(1000) else controller.burning(index)
                        controller.points(16)
                        for (ind in controller.killCount2.sorted())
                            renewal(ind)
                        numPF -= 100
                        lblFlames.bind(numPF / 100)
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
                        controller.list.clear()
                        controller.listButtons.clear()
                        field64()
                        randomField()
                        controller.points(64)
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
                bind(controller.doubling)
                tooltip("Количество оставшихся удвоений") {
                    font = Font.font(13.0)
                }
            }
            button("", imageview("/Icons/Acid.jpg")) {
                translateX = 800.0
                translateY = 550.0
                setOnMouseClicked {
                    if (tISAcid / 180 >= 1) {
                        controller.doubling.value += 10
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


    //возрождение кнопки
    private fun renewal(index: Int) {
        controller.listButtons.add(index, icBtn(index, controller.randomForRenewal(index)))
    }


    //панелька для очков
    private val lblPoints = label {                //панель для баллов
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
}
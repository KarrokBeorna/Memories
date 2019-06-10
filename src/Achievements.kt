import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File

class Achievements: Fragment() {
    override val root = scrollpane {
        setPrefSize(1366.0, 768.0)
        vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    }

    private val audio: Audio by inject()

    private val pointsFile = File("Points.txt").readText()
    private val numP = Integer.parseInt(pointsFile)
    private val timeFile = File("Time.txt").readText()
    private val numT = Integer.parseInt(timeFile)

    private val listAchi = listOf(
        "acid/1.mp4", "explosions/2.mp4",
        "death/Смерть Густаво Фринга Breaking Bad.mp4", "death/Смерть Хайзенберга Breaking Bad.mp4",
        "flood/Спасение отца Артура.mp4", "flood/Битва Артура и Орма.mp4",
        "flood/Путешествие к Трезубцу Атланна.mp4", "flames/ВоВка Катаклизм.mp4",
        "flames/ВоВка Дренор.mp4", "death/Смерть Боромира.mp4",
        "explosions/Хельмова Падь.mp4", "flames/Потеря Эребора.mp4",
        "flames/Гномы против Смауга.mp4", "death/Смерть Смауга.mp4",
        "death/Смерть Торина, Фили и Кили.mp4", "flames/Огонь из Фантастических тварей 2.mp4",
        "death/Смерть Седрика Диггори.mp4", "death/Смерть Сириуса и Дуэль Дамблдора и Волан-Де-Морта.mp4",
        "flames/Огонь на острове с Крестражем.mp4", "death/Смерть Дамблдора.mp4",
        "death/Смерть Добби.mp4", "explosions/Подрыв моста.mp4",
        "flames/Выручай-комната.mp4", "death/Смерть Снегга.mp4",
        "explosions/Уничтожение планет (7 эпизод).mp4", "death/Смерть Хана Соло.mp4",
        "explosions/Взрыв Стар-Киллера (7 эпизод).mp4", "explosions/Самопожертвование (8 эпизод).mp4",
        "death/Смерть Люка СкайУокера (8 эпизод).mp4", "death/Смерть Визериса Таргариена.mp4",
        "death/Смерть Эддарда Старка.mp4", "flames/Битва при Черноводной.mp4",
        "death/Красная свадьба.mp4", "death/Смерть Джоффри Баратеона.mp4",
        "death/Поединок Горы и Оберина Мартелла.mp4", "death/Сжигание Ширен Баратеон.mp4",
        "death/Смерть Мерина Транта.mp4", "death/Смерть Рамси Болтона.mp4",
        "explosions/Взрыв Септы Бэйлора.mp4", "death/Смерть Томмена.mp4",
        "death/Убийство Уолдера Фрея.mp4", "death/Смерть Дома Фреев.mp4",
        "flames/Игра престолов. Дейенерис атакует войско Джейме..mp4", "flames/Сражение на острове.mp4",
        "death/Смерть Петира Бэйлиша.mp4", "flames/Падение стены.mp4",
        "flames/Сражение Иккинга и Красной Смерти.mp4", "death/Как приручить дракона 2 - Смерть Стоика.mp4"
        )

    private val listLogo = listOf(
        1,1,1,1,
        2,2,2,
        3,3,
        4,4,
        5,5,5,5,
        6,6,6,6,6,6,6,6,6,
        7,7,7,7,7,
        8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
        9,9
    )

    private fun logo(num:Int): Image {
        return when (num){
            1 -> Image("/Icons/LogoBB.jpeg")
            2 -> Image("/Icons/LogoAq.jpg")
            3 -> Image("/Icons/LogoWoW.jpg")
            4 -> Image("/Icons/LogoTLoTR.png")
            5 -> Image("/Icons/LogoHobbit.jpeg")
            6 -> Image("/Icons/LogoHP.png")
            7 -> Image("/Icons/LogoSW.jpg")
            8 -> Image("/Icons/LogoGoT.jpg")
            else -> Image("/Icons/LogoHTTYD.png")
        }
    }

    val pane = pane {
        rectangle {
            width = 1351.0
            height = 3849.0
            fill = Color.LIGHTGRAY
        }
        button("Назад") {
            prefWidth = 70.0
            translateX = 1280.0
            translateY = 20.0
            style {
                backgroundColor += Color.GREENYELLOW
                fontSize = 15.px
            }
            setOnMouseClicked {
                close()
                audio.mdp.play()
            }
        }

        fun video(name: String) {
            val md = Media(Achievements::class.java.getResource(name).toExternalForm())
            val mdp = MediaPlayer(md)
            val mdv = MediaView(mdp)
            mdv.isPreserveRatio = false
            mdv.fitHeight = 768.0
            mdv.fitWidth = 1366.0
            root.vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            root.add(mdv)
            mdp.setOnEndOfMedia { close(); audio.mdp.play() }
            mdp.play()
        }

        fun btnVideo(x: Double, y: Double, name: String, index:Int, num:Int, rqrd: Int): ImageView {
            return if (num < rqrd) imageview("/Icons/Close.png") {
                translateX = x
                translateY = y
            } else
                imageview(logo(index)) {
                    translateX = x
                    translateY = y
                    setOnMouseClicked {
                        video("/Achievements/$name")
                    }
                }
        }


        //открытие видео по баллам
        for (i in 0..47 step 2) {
            val x = (i % 3) * 422.0 + 100.0
            val y = (i / 3) * 237.0 + 57.0
            btnVideo(x, y, listAchi[i], listLogo[i], numP, (i + 1) * 1000)
        }


        //открытие видео по времени
        for (i in 1..47 step 2) {
            val x = (i % 3) * 422.0 + 100.0
            val y = (i / 3) * 237.0 + 57.0
            btnVideo(x, y, listAchi[i], listLogo[i], numT, (i + 1) * 900)
        }
    }
}
fun main (args: Array<String>) {
    val list = mutableListOf<Int>()
    for (i in randomIcon()) {
        list += i
        if (list.size == 8) {
            println(list)
            list.clear()
            println()
            }
        }
}

fun randomIcon(): List<Int> {
    val list = mutableListOf<Int>()
    var index = 0
    while (list.size != 64) {
        val random = (1..6).random()
        when (index) {
            in 0..1, in 8..9 -> {
                list += random
                index++
            }
            in 2..7, in 10..15 -> {
                if (random != list[index - 1] && random != list[index - 2]) {
                    list += random
                    index++
                }
            }
            else -> {
                if (random != list[index - 1] && random != list[index - 2] &&
                    random != list[index - 16] && random != list[index - 8]) {
                    list += random
                    index++
                }
            }
        }
    }
    return list
}
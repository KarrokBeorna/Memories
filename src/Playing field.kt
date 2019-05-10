fun main (args: Array<String>) {
    println(ranic())
}

fun ranic(): List<Int> {
    val list = mutableListOf<Int>()
    for (i in 0..63) {
        val random = (0..10).random()
        list += random
    }
    return list
}
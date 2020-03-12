package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int,Int>>.groupByBounds(bounds: List<Pair<Int,Int>>): List<List<Pair<Int, Int>>> {
    val outList: List<List<Pair<Int,Int>>> = emptyList()
    val a1 = this.map { (lb, hb) -> {
        val bound = bounds.find { (lbound,hbound) -> lbound <= lb && hbound >= hb }
    }}


    return emptyList()
    // TODO
}
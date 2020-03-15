package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int,Int>>.groupByBounds(bounds: List<Pair<Int,Int>>): List<List<Pair<Int, Int>>> {
    val outList  = mutableListOf<List<Pair<Int,Int>>>()
    bounds.forEach { (lb,hb) ->
        run {
           val insideBounds =
               this.filter { (lbound, hbound) -> lbound >= lb && hbound <= hb }
           outList.add(insideBounds)
        }
    }
    return  outList
}



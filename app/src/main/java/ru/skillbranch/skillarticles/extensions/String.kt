package ru.skillbranch.skillarticles.extensions

fun String.indexesOf(queryString: String):List<Int> {
    val resultList = mutableListOf<Int>()
    var fromIndex = 0
    while(indexOf(queryString, fromIndex) > -1){
        fromIndex = indexOf(queryString, fromIndex)
        resultList.add(fromIndex)
        fromIndex++
    }
    return resultList
}

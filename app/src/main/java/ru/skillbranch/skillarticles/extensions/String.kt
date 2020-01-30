package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true):List<Int> {
    val resultList = mutableListOf<Int>()
    var fromIndex = 0
    if (substr.isEmpty())
        return resultList

    if (this.isNullOrEmpty())
        return resultList

    while(indexOf(substr, fromIndex,ignoreCase) > -1){
        fromIndex = indexOf(substr, fromIndex, ignoreCase)
        resultList.add(fromIndex)
        fromIndex++
    }
    return resultList
}

package ru.skillbranch.skillarticles

import junit.framework.Assert.assertEquals
import org.junit.Test
import ru.skillbranch.skillarticles.markdown.Element
import ru.skillbranch.skillarticles.markdown.MarkdownParser

class MarkdownParserTest {

    @Test
    fun parse_list_item() {
        val result = MarkdownParser.parse(unorderedListString)
        val actual = prepare<Element.UnorderedListItem>(result.elements)
        assertEquals(expectedUnorderedList, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    private fun printResults(list: List<String>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            println("find >> ${iterator.next()}")
        }
    }

    private fun printElements(list: List<Element>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            println("element >> ${iterator.next()}")
        }
    }


    private fun Element.spread():List<Element> {
        val elements = mutableListOf<Element>()
        elements.add(this)
        elements.addAll(this.elements.spread())
        return elements
    }

    private fun List<Element>.spread():List<Element>{
        val elements = mutableListOf<Element>()

        if (this.isNotEmpty()) elements.addAll(
            this.fold(mutableListOf()) { acc, el -> acc.also { it.addAll(el.spread())}}
        )
        return elements
    }

    private inline fun <reified T:Element> prepare(list: List<Element>):List<String> {
        return list
            .fold(mutableListOf<Element>()) { acc, el ->
                // spread inner elements
                acc.also { it.addAll(el.spread()) }
            }
            .filterIsInstance<T>() // filter only expected instance
            .map { it.text.toString()} // transform to element text
    }
}
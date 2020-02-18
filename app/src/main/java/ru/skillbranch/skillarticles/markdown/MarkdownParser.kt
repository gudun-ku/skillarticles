package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    // group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+)"

    // result regex
    const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP"

    private val elementsPattern by lazy {
        Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE)
    }

    /**
     * parse markdown text to elements
     */

    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    /**
     * clear markdown text to string without markdown characters
     */

    fun clear(string: String) : String? {
        // TODO - Homework part 1
        return string.replace("//#", "")
    }

    /**
     * find markdown elements in markdown text
     */
    private fun findElements(string: CharSequence):List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            // if something is found then everything before - TEXT
            if (lastStartIndex < startIndex) {
                parents.add (Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }

            // found text
            var text: CharSequence

            // groups range info
            val groups = 1..1
            var group = -1
            for (currGroup in groups) {
                if(matcher.group(currGroup) != null) {
                    group = currGroup
                    break
                }
            }

            when(group) {
                // NOT FOUND -> BREAK WHILE
                -1 -> break@loop
                // UNORDERED LIST
                1 -> {
                    // text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    // find inner
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)
                    // next search starts from position "endIndex (last regex character)
                    lastStartIndex = endIndex
                }
            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }

        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element() {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()
}
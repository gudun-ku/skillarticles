package ru.skillbranch.skillarticles.markdown

import android.util.Log
import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    // group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP = "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"


    // result regex
    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
                                        "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP" +
                                        "|$INLINE_GROUP"

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

            val groups = 1..8

            // every group
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
                    val subelements = findElements(text)
                    val element = Element.UnorderedListItem(text, subelements)
                    parents.add(element)
                    // next search starts from position "endIndex (last regex character)
                    lastStartIndex = endIndex
                }

                // HEADER
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    // text without "{#} "
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // QUOTE
                3 -> {
                    // text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelements = findElements(text)
                    val element = Element.Quote(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // ITALIC
                4 -> {
                    // text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)
                    val element = Element.Italic(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // BOLD
                5 -> {
                    // text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subelements = findElements(text)
                    val element = Element.Bold(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // STRIKE
                6 -> {
                    // text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subelements = findElements(text)
                    val element = Element.Strike(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // RULE
                7 -> {
                    // text without "***" insert empty character
                    text = " " + string.subSequence(startIndex.plus(3), endIndex)
                    val element = Element.Rule(text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // INLINE
                8 -> {
                    // text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)
                    val element = Element.InlineCode(text, subelements)
                    parents.add(element)
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

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Rule(
        override val text: CharSequence = " ", // for insert span
        override val elements: List<Element> = emptyList()
    ): Element()

    data class InlineCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()
}
package ru.skillbranch.skillarticles.markdown

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
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^[*?]\\(.*?\\))"
    private const val ORDERED_LIST_ITEM_GROUP = "(^\\d+\\. .+\$)"
    private const val MULTILINE_CODE_GROUP = "((?<!`)`{3}[^`\\s](.|\\n|\\r\\n)*?[^`\\s]?`{3}(?!`))"


    // result regex
    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
                                        "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP" +
                                        "|$INLINE_GROUP|$LINK_GROUP|$ORDERED_LIST_ITEM_GROUP" +
                                        "|$MULTILINE_CODE_GROUP"

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

    fun clear(string: String?) : String? {
        if (string.isNullOrEmpty()) return null

        val mdText = parse(string)
        if (string.isNullOrEmpty()) return null
        var resultString = ""
        val textList = mdText.elements.fold(mutableListOf<Element>()){ acc, el -> //spread inner elements
            acc.also { it.addAll(el.spread()) }
        }
            .map { it.text.toString() }

        textList.forEach {
               resultString += it
            }

        return resultString
    }

    private fun Element.spread():List<Element>{
        val elements = mutableListOf<Element>()
        if (this.elements.isEmpty()) {
            elements.add(this)
        } else {
            elements.addAll(this.elements.spread())
        }
        return elements
    }

    private fun List<Element>.spread():List<Element>{
        val elements = mutableListOf<Element>()
        if(this.isNotEmpty()) elements.addAll(
            this.fold(mutableListOf()){acc, el -> acc.also { it.addAll(el.spread()) }}
        )
        return elements
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

            val groups = 1..11

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

                // LINK
                9 -> {
                    // full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    val element = Element.Link(link, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // ORDERED LIST
                10 -> {
                    val reg = "^(^\\d+\\.)".toRegex().find(string.subSequence(startIndex, endIndex))
                    val order = reg!!.value
                    // text without "X. "
                    text = string.subSequence(startIndex.plus(order.length + 1), endIndex)
                    val element = Element.OrderedListItem(order, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // MULTILINE CODE BLOCK
                11 -> {

                    // line by line
                    val fullText = string.subSequence(startIndex, endIndex)
                    val blockStrings = fullText.split("\n")
                    for (str in blockStrings) {
                        val rStart = "(^`{3})".toRegex().find(str)
                        val rEnd =   "(`{3}\$)".toRegex().find(str)
                        if (rStart != null && rEnd != null) {
                            // SINGLE
                            val elText = str.subSequence(3,str.length -3)
                            parents.add(Element.BlockCode(Element.BlockCode.Type.SINGLE, elText))
                        } else if (rStart != null && rEnd == null) {
                            // START
                            val elText = str.subSequence(3,str.length)
                            parents.add(Element.BlockCode(Element.BlockCode.Type.START, "$elText\n"))
                        } else if (rStart == null && rEnd != null) {
                            // START
                            val elText = str.subSequence(0,str.length -3)
                            parents.add(Element.BlockCode(Element.BlockCode.Type.END, elText))
                        } else {
                            // MIDDLE
                            parents.add(Element.BlockCode(text = "$str\n"))
                        }
                    }

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

sealed class Element {
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

    data class Link(
        val link: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }

}
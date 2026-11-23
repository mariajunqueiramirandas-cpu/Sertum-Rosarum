package com.sertumrosarum.feature.catechist

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.lumen.webUriOrNull
import com.sertumrosarum.ui.components.SacredDivider
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import org.commonmark.ext.gfm.alerts.Alert
import org.commonmark.ext.gfm.alerts.AlertTitle
import org.commonmark.ext.gfm.alerts.AlertsExtension
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.BlockQuote
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListBlock
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser

private val alertTitles = mapOf(
    "ORACAO" to "Oração", "ESCRITURA" to "Escritura", "REFLEXAO" to "Reflexão",
    "NOTE" to "Nota", "TIP" to "Sugestão", "IMPORTANT" to "Importante", "WARNING" to "Atenção", "CAUTION" to "Cuidado",
)

private val markdownParser = Parser.builder()
    .extensions(listOf(
        AlertsExtension.builder().setAllowedTypes(alertTitles).allowCustomTitles(true).build(),
        StrikethroughExtension.create(),
        TablesExtension.create(),
    ))
    .maxOpenBlockParsers(16)
    .maxInlineNesting(32)
    .build()

@Composable
internal fun CatechistMarkdown(
    markdown: String,
    modifier: Modifier = Modifier,
    style: TextStyle = SertumTheme.typography.whisper,
) {
    val document = remember(markdown) { markdownParser.parse(markdown) }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        document.children().forEach { MarkdownBlock(it, style) }
    }
}

@Composable
private fun MarkdownBlock(node: Node, style: TextStyle) {
    val type = SertumTheme.typography
    val colors = SertumTheme.colors
    when (node) {
        is Paragraph -> MarkdownText(node, style)
        is Heading -> MarkdownText(
            node,
            when (node.level) {
                1 -> type.heading
                2 -> type.section
                else -> style.copy(fontWeight = FontWeight.Bold)
            },
            Modifier.semantics { heading() },
        )
        is ListBlock -> Column(verticalArrangement = Arrangement.spacedBy(if (node.isTight) 5.dp else 10.dp)) {
            node.children().forEachIndexed { index, item ->
                val marker = if (node is OrderedList) {
                    "${(node.markerStartNumber ?: 1) + index}${node.markerDelimiter ?: "."}"
                } else "•"
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SacredText(marker, style = style, color = colors.accentInk)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        item.children().forEach { MarkdownBlock(it, style) }
                    }
                }
            }
        }
        is BlockQuote -> MarkdownQuote(node, style)
        is Alert -> MarkdownQuote(node, style, node.type)
        is ThematicBreak -> SacredDivider()
        is FencedCodeBlock -> MarkdownCode(node.literal)
        is IndentedCodeBlock -> MarkdownCode(node.literal)
        is HtmlBlock -> SacredText(node.literal.trimEnd(), style = style)
        is TableBlock -> MarkdownTable(node)
    }
}

@Composable
private fun MarkdownQuote(node: Node, style: TextStyle, alertType: String? = null) {
    val colors = SertumTheme.colors
    val accent = when (alertType) {
        "ORACAO", "ESCRITURA", "IMPORTANT" -> colors.gold
        "WARNING", "CAUTION" -> colors.ruby
        else -> colors.marian
    }
    val container = Modifier.fillMaxWidth().then(
        if (alertType != null) Modifier.background(colors.surface, RoundedCornerShape(12.dp)) else Modifier,
    )
    Column(
        container.drawBehind {
            val stripe = 2.dp.toPx()
            val x = if (layoutDirection == LayoutDirection.Ltr) 0f else size.width - stripe
            drawRect(accent, Offset(x, 0f), Size(stripe, size.height))
        }.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (alertType != null) {
            val title = node.firstChild as? AlertTitle
            val heading = Modifier.semantics { heading() }
            val titleStyle = SertumTheme.typography.label
            if (title == null) SacredText(alertTitles[alertType].orEmpty(), heading, titleStyle, colors.accentInk)
            else MarkdownText(title, titleStyle, heading)
        }
        node.children().filterNot { it is AlertTitle }.forEach { MarkdownBlock(it, style) }
    }
}

@Composable
private fun MarkdownCode(code: String) {
    val colors = SertumTheme.colors
    SacredText(
        code.removeSuffix("\n"),
        Modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(12.dp)).padding(12.dp),
        SertumTheme.typography.body.copy(fontFamily = FontFamily.Monospace),
    )
}

@Composable
private fun MarkdownTable(table: TableBlock) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(Modifier.horizontalScroll(rememberScrollState())) {
        table.children().flatMap { it.children() }.forEach { row ->
            val cells = row.children().filterIsInstance<TableCell>().toList()
            Row(Modifier.background(if (cells.firstOrNull()?.isHeader == true) colors.elevated else colors.surface)) {
                cells.forEach { cell ->
                    val align = when (cell.alignment) {
                        TableCell.Alignment.CENTER -> TextAlign.Center
                        TableCell.Alignment.RIGHT -> TextAlign.End
                        else -> TextAlign.Start
                    }
                    MarkdownText(
                        cell,
                        type.body.copy(fontWeight = if (cell.isHeader) FontWeight.Bold else FontWeight.Normal, textAlign = align),
                        Modifier.width(144.dp).padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }
            SacredDivider(Modifier.width(144.dp * cells.size))
        }
    }
}

@Composable
private fun MarkdownText(node: Node, style: TextStyle, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val annotated = remember(node, colors.accentInk, colors.elevated) {
        buildAnnotatedString { appendInline(node, colors.accentInk, colors.elevated) }
    }
    BasicText(annotated, modifier, style.copy(color = colors.ink))
}

private fun AnnotatedString.Builder.appendInline(parent: Node, linkColor: Color, codeBackground: Color) {
    fun appendChildren(node: Node) = appendInline(node, linkColor, codeBackground)
    parent.children().forEach { node ->
        when (node) {
            is Text -> append(node.literal)
            is SoftLineBreak, is HardLineBreak -> append('\n')
            is StrongEmphasis -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { appendChildren(node) }
            is Emphasis -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { appendChildren(node) }
            is Strikethrough -> withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { appendChildren(node) }
            is Code -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = codeBackground)) { append(node.literal) }
            is HtmlInline -> append(node.literal)
            is Link -> {
                val uri = webUriOrNull(node.destination)
                if (uri == null) appendChildren(node)
                else withLink(LinkAnnotation.Url(
                    uri.toString(),
                    TextLinkStyles(style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)),
                )) { appendChildren(node) }
            }
            else -> appendChildren(node)
        }
    }
}

private fun Node.children(): Sequence<Node> = generateSequence(firstChild) { it.next }

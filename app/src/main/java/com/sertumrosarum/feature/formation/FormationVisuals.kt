package com.sertumrosarum.feature.formation

import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sertumrosarum.R
import com.sertumrosarum.ui.canvas.FineArt
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

internal data class ConceptNode(val title: String, val detail: String)
internal data class LessonConceptMap(val title: String, val root: String, val sequence: Boolean, val nodes: List<ConceptNode>, val note: String)
private fun node(title: String, detail: String) = ConceptNode(title, detail)

/** Relationships are text and layout, never baked into generated pixels. */
internal fun lessonConceptMap(id: String): LessonConceptMap = when (id) {
    "god-loves" -> LessonConceptMap("Deus Se revela, nós respondemos", "Uma história de encontro", true, listOf(node("Iniciativa de Deus", "Criação, aliança e promessa: Deus chama por amor."), node("Plenitude em Jesus", "O Filho revela o Pai e realiza Sua obra de salvação."), node("Transmissão apostólica", "Escritura e Tradição; o Magistério serve à Palavra."), node("Resposta de fé", "Acolher livremente, conhecer, rezar e viver.")), "A revelação não começa em nosso desempenho. A resposta é livre e sustentada pela graça.")
    "trinity" -> LessonConceptMap("Um só Deus, três Pessoas", "Uma única natureza divina, indivisível", false, listOf(node("Pai", "Plenamente Deus. Não é o Filho nem o Espírito Santo."), node("Filho", "Plenamente Deus. Não é o Pai nem o Espírito Santo."), node("Espírito Santo", "Plenamente Deus. Não é o Pai nem o Filho.")), "Os ramos distinguem Pessoas, não partes, graus ou etapas. Não representam três deuses nem uma pessoa com três disfarces.")
    "jesus" -> LessonConceptMap("Quem é Jesus?", "Uma única Pessoa: o Filho eterno", false, listOf(node("Natureza divina", "Verdadeiro Deus, eterno e consubstancial ao Pai."), node("Natureza humana", "Verdadeiro homem: corpo, alma, inteligência e vontade humanos.")), "Duas naturezas completas, sem mistura nem separação. Jesus não é duas pessoas nem metade Deus e metade homem.")
    "church-mary" -> LessonConceptMap("Uma comunhão em Cristo", "Cristo é a cabeça da Igreja", false, listOf(node("Fiéis na terra", "Dons e ministérios diferentes, uma missão comum."), node("Os que se purificam", "Rezamos pelos falecidos, confiando na misericórdia de Deus."), node("Santos na glória", "Intercedem por nós; Maria recebe honra singular.")), "Toda intercessão depende de Cristo. Adoração pertence somente a Deus; veneração reconhece Sua graça nas criaturas.")
    "sacraments" -> LessonConceptMap("Sete sacramentos, uma vida em Cristo", "Cristo age na Igreja", false, listOf(node("Iniciação · 3", "Batismo · Confirmação · Eucaristia"), node("Cura · 2", "Penitência · Unção dos Enfermos"), node("Comunhão e missão · 2", "Ordem · Matrimônio")), "Os grupos ajudam a compreender o serviço de cada sacramento. Não são etapas obrigatórias para todos receberem os sete.")
    "mass" -> LessonConceptMap("O caminho da Missa", "Uma única celebração", true, listOf(node("Reunir", "Ritos iniciais: acolher o chamado e preparar o coração."), node("Escutar", "Palavra: leituras, homilia, resposta de fé e preces."), node("Dar graças e oferecer", "Liturgia eucarística: dons, oração eucarística e Comunhão."), node("Ser enviado", "Bênção e despedida: viver na caridade o que celebramos.")), "Palavra e Eucaristia estão unidas. O único sacrifício de Cristo se torna presente sacramentalmente.")
    "eucharist" -> LessonConceptMap("O que muda na consagração?", "Cristo inteiro está realmente presente", false, listOf(node("O que se converte", "Toda a substância do pão e do vinho no Corpo e no Sangue de Cristo."), node("O que permanece", "As aparências sensíveis do pão e do vinho, como cor e sabor.")), "A presença não depende da emoção. Este esquema distingue afirmações da fé; não desenha uma transformação química.")
    "reconciliation" -> LessonConceptMap("Um caminho de retorno", "A misericórdia de Cristo nos chama", true, listOf(node("Examinar", "Olhar escolhas concretas à luz da Palavra."), node("Arrepender-se", "Rejeitar o pecado e querer mudar."), node("Confessar e receber", "Dizer a verdade e acolher a absolvição."), node("Reparar e recomeçar", "Cumprir a penitência e reparar o possível.")), "O perdão é dom, não prêmio por um relato perfeito. Esquecimento involuntário e ocultação deliberada são situações distintas.")
    "dignity" -> LessonConceptMap("Da dignidade à escolha concreta", "Toda pessoa é imagem de Deus", true, listOf(node("Reconhecer", "O valor da pessoa não depende de produtividade ou aprovação."), node("Discernir", "Formar a consciência com a verdade e a Palavra."), node("Escolher", "Praticar justiça, respeito à vida e cuidado com o próximo."), node("Cultivar", "Repetir o bem, formar virtudes e servir ao bem comum.")), "A dignidade é recebida, não conquistada ao final do percurso. As escolhas devem respeitá-la em todos.")
    "grace-freedom" -> LessonConceptMap("Dom e resposta livre", "Deus toma a iniciativa da graça", true, listOf(node("Receber", "O amor e o auxílio de Deus não são comprados."), node("Responder", "A liberdade coopera: escuta, decisão e ação."), node("Dar fruto", "As boas obras crescem da vida em Cristo.")), "Para pecado mortal, matéria grave, plena consciência e consentimento deliberado devem estar juntos. Tentação não equivale a consentimento.")
    "mercy" -> LessonConceptMap("O amor cuida da pessoa inteira", "Uma caridade com justiça e respeito", false, listOf(node("Obras corporais", "Alimento, água, roupa, acolhida, visita a enfermos e presos, sepultura."), node("Obras espirituais", "Ensino, conselho, correção caridosa, consolo, perdão, paciência e oração.")), "Escute a pessoa e preserve sua dignidade. A ajuda imediata pode caminhar com respostas às causas da injustiça.")
    "forgiveness" -> LessonConceptMap("Três distinções para cuidar da relação", "Misericórdia, verdade e proteção", false, listOf(node("Perdão", "Renunciar à vingança; a dor pode permanecer."), node("Reconciliação", "Restaurar a relação com verdade, mudança e condições seguras."), node("Confiança", "Reconstruir pela consistência dos atos ao longo do tempo.")), "Não são etapas automáticas. Perdoar não exige contato, impunidade ou retorno a uma situação de abuso.")
    "prayer" -> LessonConceptMap("O Pai-Nosso forma nosso desejo", "Rezamos como filhos, com os irmãos", false, listOf(node("Voltados ao Pai · 3 petições", "Seu nome · Seu Reino · Sua vontade"), node("Nossa vida entregue · 4 petições", "Pão · Perdão · Auxílio na tentação · Libertação do mal")), "As petições formam uma única oração de confiança. O pão e o perdão nos comprometem também com as necessidades do próximo.")
    "rosary" -> LessonConceptMap("Uma dezena, passo a passo", "Contemplar Cristo com Maria", true, listOf(node("Anunciar e escutar", "Mistério, Palavra e breve silêncio."), node("Rezar", "Pai-Nosso · 10 Ave-Marias · Glória."), node("Retomar a atenção", "Voltar a Jesus quando a mente se dispersar."), node("Levar um fruto", "Escolher um gesto de fé, esperança ou caridade.")), "A oração de Fátima pode ser acrescentada conforme o costume. A contagem serve à contemplação, sem obrigar Deus a conceder um pedido.")
    "year" -> LessonConceptMap("O ano da Igreja", "A Páscoa é o centro; o domingo marca a semana", true, listOf(node("Advento → Natal", "Esperar e celebrar a Encarnação."), node("Tempo Comum · primeira parte", "Seguir Jesus no cotidiano depois do tempo natalino."), node("Quaresma → Tríduo Pascal", "Converter-se e celebrar a morte e a ressurreição."), node("Tempo Pascal → Pentecostes", "Cinquenta dias de alegria e o dom do Espírito."), node("Tempo Comum · segunda parte", "Continuar a missão até o próximo Advento.")), "O percurso recomeça no Advento. Cores e datas específicas seguem o calendário litúrgico, com celebrações próprias de cada lugar.")
    "daily" -> LessonConceptMap("Um ritmo que pode recomeçar", "A presença de Deus no dia inteiro", true, listOf(node("Oferecer", "No início da jornada: entregar pessoas, trabalhos e escolhas."), node("Recordar", "Numa pausa: agradecer, interceder ou pedir luz."), node("Rever", "Ao terminar: agradecer, reconhecer, pedir perdão e preparar um passo.")), "Adapte os horários à vida real. A regularidade serve ao encontro; não exige negligenciar descanso, saúde ou deveres.")
    else -> error("Missing catechetical map for $id")
}

@Composable
internal fun ConceptMap(id: String) {
    val map = remember(id) { lessonConceptMap(id) }
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredCard(Modifier.fillMaxWidth().padding(top = 24.dp).testTag("lesson-concept-map")) {
        Column(Modifier.padding(18.dp)) {
            SacredEyebrow("Ver as relações")
            SacredText(map.title, Modifier.padding(top = 8.dp).semantics { heading() }, type.section)
            SacredText(map.root, Modifier.fillMaxWidth().padding(top = 16.dp).background(colors.marian.copy(alpha = .13f), RoundedCornerShape(14.dp)).padding(14.dp), type.label, colors.accentInk, TextAlign.Center)
            val line = colors.marian.copy(alpha = .45f)
            map.nodes.forEachIndexed { index, n ->
                Row(Modifier.fillMaxWidth().drawBehind {
                    val x = 10.dp.toPx()
                    drawLine(line, Offset(x, 0f), Offset(x, if (index == map.nodes.lastIndex) 28.dp.toPx() else size.height), 1.5.dp.toPx())
                    drawLine(line, Offset(x, 28.dp.toPx()), Offset(22.dp.toPx(), 28.dp.toPx()), 1.5.dp.toPx())
                }.padding(start = 22.dp, top = 10.dp)) {
                    Column(Modifier.weight(1f).border(1.dp, colors.border, RoundedCornerShape(12.dp)).padding(12.dp)) {
                        SacredText(if (map.sequence) "${index + 1} · ${n.title}" else n.title, style = type.label, color = colors.accentInk)
                        SacredText(n.detail, Modifier.padding(top = 5.dp), type.body)
                    }
                }
            }
            SacredText(map.note, Modifier.padding(top = 16.dp), type.body, colors.secondary)
        }
    }
}

internal data class TeachingImage(@DrawableRes val resource: Int, val title: String, val description: String, val question: String, val explanation: String)
internal fun teachingImage(id: String): TeachingImage? = when (id) {
    "god-loves" -> TeachingImage(R.drawable.catechism_emmaus, "Escutar no caminho de Emaús", "Jesus caminha entre dois discípulos; um segura um rolo aberto, o outro escuta. Uma estrada conduz à aldeia.", "O que os gestos de escuta e de conversa ajudam a perceber sobre a iniciativa de Jesus?", "O Ressuscitado Se aproxima de discípulos desanimados e ilumina sua história pelas Escrituras. A cena representa Lucas 24; os detalhes de paisagem e roupas são escolhas ilustrativas, não documentação histórica.")
    "sacraments" -> TeachingImage(R.drawable.catechism_baptism, "Sinais visíveis do Batismo", "Um sacerdote derrama água sobre a cabeça de uma criança envolta em branco. Pais e madrinha a acompanham junto da fonte; há uma vela acesa.", "Localize a água, a veste branca e a luz. O que esses sinais anunciam?", "A água acompanha o novo nascimento em Cristo; a veste branca recorda a vida nova; a luz remete a Cristo e à fé recebida. O desenho não reproduz todo o rito: água e fórmula trinitária são essenciais, e os demais sinais ajudam a expressar seu sentido.")
    "mercy" -> TeachingImage(R.drawable.catechism_mercy, "Alimento e presença", "Uma mulher visita um homem idoso e o escuta à altura dos olhos. Sobre a mesa há alimento e água; ao lado, uma sacola de compras.", "Que obras corporais e espirituais aparecem juntas nesta visita?", "O alimento e a água cuidam do corpo; a escuta e a companhia podem consolar. A pessoa visitada participa da conversa. A misericórdia respeita sua voz, em vez de reduzi-la a alguém que apenas recebe objetos.")
    "daily" -> TeachingImage(R.drawable.catechism_daily, "Uma vida atravessada pela oração", "Três cenas da mesma mulher: leitura de manhã, partilha de pão e escuta durante o dia, reflexão num caderno ao anoitecer.", "Que ligação você percebe entre a leitura, a partilha e a reflexão?", "Oração e serviço podem atravessar a mesma jornada. A cena central mostra que o encontro com Deus produz atenção ao próximo. Os três momentos são uma sugestão adaptável; não uma obrigação de ter uma rotina ou uma casa como a da imagem.")
    else -> null
}

@Composable
private fun teachingBitmap(@DrawableRes resource: Int) = run {
    val resources = LocalContext.current.resources
    remember(resource, resources) { BitmapFactory.decodeResource(resources, resource).asImageBitmap() }
}

@Composable
internal fun TeachingIllustration(id: String) {
    val art = teachingImage(id) ?: return
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var open by rememberSaveable(id) { mutableStateOf(false) }
    val bitmap = teachingBitmap(art.resource)
    Column(Modifier.fillMaxWidth().padding(top = 24.dp).testTag("lesson-teaching-image")) {
        SacredEyebrow("Observar para compreender")
        Image(bitmap, art.description, Modifier.padding(top = 12.dp).fillMaxWidth().aspectRatio(1.5f).clip(RoundedCornerShape(18.dp)).clickable(role = Role.Button, onClickLabel = "Ampliar a ilustração") { open = true }.testTag("lesson-image-open"), contentScale = ContentScale.Fit)
        SacredText(art.title, Modifier.padding(top = 10.dp).semantics { heading() }, type.section.copy(fontSize = 20.sp))
        SacredText("Ilustração original gerada com IA · toque para ampliar", Modifier.padding(top = 5.dp), type.micro, colors.muted)
        SacredText(art.question, Modifier.padding(top = 12.dp), type.label)
        RevealAnswer("Uma leitura da imagem", art.explanation, "image-$id")
    }
    if (open) Dialog(onDismissRequest = { open = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        var zoom by rememberSaveable(id) { mutableStateOf(1f) }
        var pan by remember(id) { mutableStateOf(Offset.Zero) }
        val transform = rememberTransformableState { scale, offset, _ ->
            zoom = (zoom * scale).coerceIn(1f, 3f)
            pan = if (zoom == 1f) Offset.Zero else pan + offset
        }
        Column(Modifier.fillMaxSize().background(colors.background).systemBarsPadding().testTag("lesson-image-viewer")) {
            // Close and zoom remain reachable even at enlarged system text sizes.
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SacredSecondaryButton("Fechar", { open = false }, Modifier.weight(1f), "lesson-image-close")
                SacredSecondaryButton(if (zoom > 1f) "Restaurar" else "Ampliar 2×", { zoom = if (zoom > 1f) 1f else 2f; pan = Offset.Zero }, Modifier.weight(1f), "lesson-image-zoom")
            }
            Box(Modifier.weight(1f).fillMaxWidth().clipToBounds().transformable(transform), contentAlignment = Alignment.Center) {
                Image(bitmap, art.description, Modifier.fillMaxSize().graphicsLayer { scaleX = zoom; scaleY = zoom; translationX = pan.x; translationY = pan.y }.testTag("lesson-image-detail"), contentScale = ContentScale.Fit)
            }
            Column(Modifier.fillMaxWidth().heightIn(max = 180.dp).verticalScroll(rememberScrollState()).padding(16.dp)) {
                SacredText(art.title, Modifier.semantics { heading() }, type.section)
                SacredText("Ilustração original gerada com IA · amplie e arraste para observar", Modifier.padding(top = 8.dp), type.micro, colors.muted)
                SacredText(art.description, Modifier.padding(top = 8.dp), type.body)
            }
        }
    }
}

internal fun lessonArtStudy(id: String): Pair<FineArt, String>? = when (id) {
    "jesus" -> FineArt.SALVATOR_MUNDI to "Observe como a figura de Cristo concentra a composição. A pintura propõe uma contemplação de Jesus Salvador; não demonstra visualmente a união das duas naturezas. Que diferença há entre reconhecer uma imagem e compreender a afirmação da fé?"
    "church-mary" -> FineArt.ENCLOSED_GARDEN to "Observe Maria junto do Menino e dos santos. A reunião figura uma comunhão de pessoas voltadas a Cristo. A imagem não torna Maria divina nem os santos fontes independentes de graça. Como a composição pode ajudar a recordar a intercessão?"
    "reconciliation" -> FineArt.PRODIGAL_SON to "Observe a aproximação entre o pai e o filho e releia Lucas 15. A pintura permite imaginar o acolhimento, mas não retrata o rito da confissão. Como a atitude do pai ilumina a coragem de voltar e reconhecer a verdade?"
    "forgiveness" -> FineArt.ADULTERESS to "Observe a mulher e as pessoas ao redor de Jesus, à luz de João 8, 1–11. A misericórdia recusa a condenação violenta e chama à mudança. A cena não permite exigir que uma vítima volte a uma situação de abuso. Que atitudes precisam ser distinguidas?"
    "prayer" -> FineArt.MAGDALEN to "Observe a quietude e a luz na cena de oração. Elas são escolhas do artista, não requisitos para rezar. Que espaço de atenção a Deus é possível em sua vida, mesmo sem silêncio perfeito ou uma emoção especial?"
    "rosary" -> FineArt.ROSARY to "Percorra as pequenas cenas em torno da figura central. Esta obra histórica apresenta quinze mistérios, anteriores à inclusão dos luminosos em 2002. Compare com os vinte contemplados hoje. Como uma imagem ajuda a manter o Evangelho no centro da repetição?"
    else -> null
}

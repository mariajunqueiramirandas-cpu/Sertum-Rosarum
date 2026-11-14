package com.sertumrosarum.ui.canvas

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** UI-only editorial mapping: the teaching catalog remains independent of resources and Compose. */
fun lessonIllustration(id: String): DevotionalIllustration = when (id) {
    "god-loves", "jesus" -> DevotionalIllustration.CATECHESIS
    "trinity" -> DevotionalIllustration.FAITH
    "church-mary" -> DevotionalIllustration.TREASURY_MARIAN
    "sacraments", "mass", "eucharist" -> DevotionalIllustration.SACRAMENTS
    "reconciliation", "forgiveness" -> DevotionalIllustration.RECONCILIATION
    "dignity", "mercy" -> DevotionalIllustration.CHARITY
    "grace-freedom" -> DevotionalIllustration.INTENTIONS
    "prayer", "rosary" -> DevotionalIllustration.CONTEMPLATIVE_PRAYER
    "year" -> DevotionalIllustration.LITURGICAL_YEAR
    "daily" -> DevotionalIllustration.JOURNAL
    else -> DevotionalIllustration.FAITH
}

fun pillarIllustration(index: Int): DevotionalIllustration = when (index) {
    1 -> DevotionalIllustration.SACRAMENTS
    2 -> DevotionalIllustration.CHARITY
    3 -> DevotionalIllustration.CONTEMPLATIVE_PRAYER
    else -> DevotionalIllustration.FAITH
}

fun journeyIllustration(id: String): DevotionalIllustration = when (id) {
    "mass" -> DevotionalIllustration.SACRAMENTS
    "confession" -> DevotionalIllustration.RECONCILIATION
    "mercy" -> DevotionalIllustration.CHARITY
    else -> DevotionalIllustration.CONTEMPLATIVE_PRAYER
}

fun questionIllustration(id: String): DevotionalIllustration = when (id) {
    "mary", "distraction", "start", "repeat" -> DevotionalIllustration.CONTEMPLATIVE_PRAYER
    "confess", "mortal", "forgive" -> DevotionalIllustration.RECONCILIATION
    "eucharist", "communion", "baptism" -> DevotionalIllustration.SACRAMENTS
    "suffering" -> DevotionalIllustration.TREASURY_CONSOLATION
    else -> DevotionalIllustration.QUESTIONS
}

/** A fixed, uncropped frame shared by the new centers and detail pages. */
@Composable
fun FormationPainting(illustration: DevotionalIllustration, tag: String, compact: Boolean = false) {
    Box(Modifier.fillMaxWidth().height(if (compact) 112.dp else 144.dp).testTag(tag), contentAlignment = Alignment.Center) {
        DevotionalArtwork(illustration, Modifier.widthIn(max = 256.dp).fillMaxWidth().fillMaxHeight())
    }
}

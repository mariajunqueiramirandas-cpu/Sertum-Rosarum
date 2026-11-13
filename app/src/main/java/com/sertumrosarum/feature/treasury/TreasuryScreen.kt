package com.sertumrosarum.feature.treasury

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.prayer.MysteryContent
import com.sertumrosarum.feature.prayer.BeadByBeadList
import com.sertumrosarum.feature.prayer.MysteryDimensions
import com.sertumrosarum.feature.prayer.ordinal
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

/**
 * Tesouro: the prayer library, the reading of one prayer, and the study of the twenty mysteries.
 * Its sheets are raised by [TreasurySheets], which the app places over the tabs.
 */
@Composable
fun TreasuryScreen(state: TreasuryUiState, actions: TreasuryActions) {
    // The library keeps its place while a prayer is read.
    val libraryScroll = rememberLazyListState()
    var listedQuery by rememberSaveable { mutableStateOf(state.query) }
    var listedCategory by rememberSaveable { mutableStateOf(state.category?.name) }
    LaunchedEffect(state.query, state.category) {
        if (listedQuery != state.query || listedCategory != state.category?.name) {
            listedQuery = state.query
            listedCategory = state.category?.name
            libraryScroll.scrollToItem(0)
        }
    }
    val reading = rememberLast(state.reading)
    // This page joins the back stack after the sheets do, so it steps aside while one is raised.
    BackHandler(state.place != TreasuryPlace.LIBRARY && state.term == null && state.studyMystery == null) { actions.backToLibrary() }
    Crossfade(state.place, animationSpec = tween(if (SertumTheme.reducedMotion) 0 else 320), label = "treasury-place") { place ->
        when (place) {
            TreasuryPlace.LIBRARY -> PrayerLibrary(state, actions, libraryScroll)
            TreasuryPlace.READING -> if (reading != null) key(reading.id) { PrayerReadingPage(reading, state.language, state.openVerse, actions) }
            TreasuryPlace.MYSTERIES -> MysteriesStudyPage(state.studySet, actions)
        }
    }
}

/** The explanation of an expression and the study of a mystery, each on its own sheet. */
@Composable
fun TreasurySheets(state: TreasuryUiState, actions: TreasuryActions, visible: Boolean) {
    val type = SertumTheme.typography
    val colors = SertumTheme.colors
    // A sheet keeps its words while it slides away.
    val term = rememberLast(state.term)
    val studied = rememberLast(state.studyMystery?.let { state.studySet to it })
    SacredBottomSheet(visible && state.term != null, actions::closeTerm, "Toque para entender", tag = "term-sheet") {
        if (term != null) TermSheetContent(term)
    }
    SacredBottomSheet(visible && state.studyMystery != null, actions::closeStudyMystery, "Estudar o mistério", tag = "study-sheet") {
        if (studied != null) {
            val (set, index) = studied
            val mystery = set.mysteries[index]
            val contemplation = MysteryContent.contemplationFor(set, index)
            SacredText("${ordinal(index)} mistério ${set.singular.lowercase()}", Modifier.fillMaxWidth(), type.micro, colors.marian, TextAlign.Center)
            SacredText(mystery.title, Modifier.fillMaxWidth().padding(top = 6.dp).testTag("study-sheet-title").semantics { heading() }, type.heading, align = TextAlign.Center)
            SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(top = 14.dp, bottom = 18.dp))
            MysteryDimensions(mystery, contemplation, meditated = false, onToggleMeditated = {}, unfolded = true, showMeditatedToggle = false)
            SacredText("Conta a conta", Modifier.padding(start = 6.dp, top = 24.dp, bottom = 8.dp).semantics { heading() }, type.section)
            BeadByBeadList(contemplation.beads, current = null)
        }
    }
}

/** The last value that was not null, so content can outlive the state that showed it. */
@Composable
internal fun <T : Any> rememberLast(value: T?): T? {
    val last = remember { arrayOfNulls<Any>(1) }
    if (value != null) last[0] = value
    @Suppress("UNCHECKED_CAST")
    return last[0] as T?
}

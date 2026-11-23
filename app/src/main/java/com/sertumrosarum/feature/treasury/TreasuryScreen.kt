package com.sertumrosarum.feature.treasury

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.theme.SertumTheme

/**
 * Tesouro: the prayer library, the reading of one prayer, and the deep study of the twenty mysteries.
 * Its sheets are raised by [TreasurySheets], which the app places over the tabs.
 */
@Composable
fun TreasuryScreen(
    state: TreasuryUiState,
    actions: TreasuryActions,
    formation: @Composable () -> Unit = {},
    bookmark: @Composable () -> Unit = {},
    bookmarkedMysteries: Set<String> = emptySet(),
    onPraySet: ((MysterySet) -> Unit)? = null,
) {
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
            TreasuryPlace.LIBRARY -> PrayerLibrary(state, actions, libraryScroll, formation)
            TreasuryPlace.READING -> if (reading != null) key(reading.id) { PrayerReadingPage(reading, state.language, state.openVerse, actions, bookmark) }
            TreasuryPlace.MYSTERIES -> MysteriesStudyPage(state.studySet, actions, bookmarkedMysteries, onPraySet)
        }
    }
}

/** The explanation of an expression and the deep study of a mystery, each on its own sheet. */
@Composable
fun TreasurySheets(
    state: TreasuryUiState,
    actions: TreasuryActions,
    visible: Boolean,
    bookmarkedMysteries: Set<String> = emptySet(),
    onToggleMysteryBookmark: ((String) -> Unit)? = null,
    onPraySingleDecade: ((MysterySet, Int) -> Unit)? = null,
    onPraySet: ((MysterySet) -> Unit)? = null,
) {
    // A sheet keeps its words while it slides away.
    val term = rememberLast(state.term)
    val studied = rememberLast(state.studyMystery?.let { state.studySet to it })
    SacredBottomSheet(visible && state.term != null, actions::closeTerm, "Toque para entender", tag = "term-sheet") {
        if (term != null) TermSheetContent(term)
    }
    SacredBottomSheet(visible && state.studyMystery != null, actions::closeStudyMystery, "Estudar o mistério", tag = "study-sheet") {
        if (studied != null) {
            val (set, index) = studied
            val key = mysteryBookmarkId(set, index)
            MysteryStudyContent(
                set = set,
                index = index,
                bookmarked = key in bookmarkedMysteries,
                onToggleBookmark = onToggleMysteryBookmark?.let { toggle -> { toggle(key) } },
                onPraySingleDecade = onPraySingleDecade?.let { pray -> { actions.closeStudyMystery(); pray(set, index) } },
                onPraySet = onPraySet?.let { pray -> { actions.closeStudyMystery(); pray(set) } },
            )
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

package com.sertumrosarum

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.formation.FormationScreen
import com.sertumrosarum.feature.garden.GardenState
import com.sertumrosarum.feature.prayer.ThemeMode
import com.sertumrosarum.feature.treasury.TreasuryScreen
import com.sertumrosarum.feature.treasury.TreasurySheets
import com.sertumrosarum.feature.treasury.TreasuryViewModel
import com.sertumrosarum.ui.canvas.FineArt
import com.sertumrosarum.ui.theme.SertumTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w392dp-h860dp-xxhdpi")
class FineArtTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `every mystery and every lesson has its own painting, with a full credit`() {
        val mysteries = MysterySet.entries.flatMap { set -> set.mysteries.indices.map { FineArt.forMystery(set, it) } }
        assertEquals("one painting for each of the twenty mysteries", 20, mysteries.distinct().size)
        FormationCatalog.lessons.forEach { assertNotNull("painting for lesson ${it.id}", FineArt.forLesson(it.id)) }
        val resources = ApplicationProvider.getApplicationContext<SertumApplication>().resources
        FineArt.entries.forEach { art ->
            assertTrue("credit for $art", art.title.isNotBlank() && art.artist.isNotBlank() && art.date.isNotBlank() && art.collection.isNotBlank())
            assertTrue("source for $art", art.source.startsWith("https://"))
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(resources, art.resourceId, bounds)
            assertTrue("image for $art is large enough to read", maxOf(bounds.outWidth, bounds.outHeight) >= 1000)
        }
    }

    @Test fun `a studied mystery and a lesson each show their painting, which opens large`() {
        val treasury = TreasuryViewModel(SavedStateHandle())
        val lesson = androidx.compose.runtime.mutableStateOf(false)
        compose.setContent {
            SertumTheme(ThemeMode.DAY, MysterySet.JOYFUL) {
                Box {
                    if (lesson.value) FormationScreen(null, GardenState(), "reconciliation") {}
                    else { val state by treasury.state.collectAsState(); TreasuryScreen(state, treasury); TreasurySheets(state, treasury, true) }
                }
            }
        }
        compose.runOnIdle { treasury.openMysteries(); treasury.selectStudySet(MysterySet.GLORIOUS); treasury.openStudyMystery(2) }
        compose.onNodeWithTag("study-painting", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        compose.runOnIdle { treasury.closeStudyMystery(); lesson.value = true }
        compose.onNodeWithTag("lesson-painting", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.OnClick)
        compose.onNodeWithTag("fine-art-viewer").assertIsDisplayed()
        compose.onNodeWithTag("fine-art-close", useUnmergedTree = true).performSemanticsAction(SemanticsActions.OnClick)
        compose.onNodeWithTag("formation-scroll").assertIsDisplayed()
    }
}

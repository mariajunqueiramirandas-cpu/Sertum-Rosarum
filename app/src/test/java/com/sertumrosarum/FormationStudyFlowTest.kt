package com.sertumrosarum

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.formation.*
import com.sertumrosarum.feature.garden.GardenState
import com.sertumrosarum.feature.prayer.ThemeMode
import com.sertumrosarum.ui.theme.SertumTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w392dp-h860dp-xxhdpi")
class FormationStudyFlowTest {
    @get:Rule val compose = createComposeRule()
    private fun tap(tag: String) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true)
        if (tag !in setOf("lesson-image-close", "lesson-image-zoom")) node.performScrollTo()
        node.performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }
    private fun shot(name: String) { compose.onRoot().captureRoboImage("build/outputs/roborazzi/catechism-$name.png") }

    @Test fun `a long chapter supports navigation image study and recall without losing the learning loop`() {
        compose.setContent {
            SertumTheme(ThemeMode.DAY, MysterySet.JOYFUL) {
                Box(Modifier.fillMaxSize().background(SertumTheme.colors.background)) { FormationScreen(null, GardenState(), "god-loves") {} }
            }
        }
        shot("chapter-opening")
        compose.onNodeWithTag("explanation-case-god-loves").assertDoesNotExist()
        tap("lesson-jump-0")
        compose.onNodeWithTag("lesson-concept-map").performScrollTo().assertIsDisplayed()
        shot("revelation-map")
        tap("lesson-image-open")
        compose.onNodeWithTag("lesson-image-viewer").assertIsDisplayed()
        compose.onNodeWithTag("lesson-image-detail").assertIsDisplayed().assertContentDescriptionEquals(teachingImage("god-loves")!!.description)
        tap("lesson-image-zoom")
        compose.onNodeWithText("Restaurar").assertIsDisplayed()
        tap("lesson-image-zoom")
        tap("lesson-image-close")
        tap("reveal-image-god-loves")
        compose.onNodeWithTag("explanation-image-god-loves").assertExists()
        compose.onNodeWithTag("lesson-teaching-image").performScrollTo()
        shot("emmaus-study")
        tap("lesson-jump-4")
        compose.onNodeWithTag("lesson-scripture-heading").assertIsDisplayed()
        tap("lesson-jump-5")
        tap("reveal-case-god-loves")
        compose.onNodeWithTag("explanation-case-god-loves").assertExists()
        tap("lesson-jump-6")
        tap("reveal-recall-god-loves-0")
        compose.onNodeWithTag("explanation-recall-god-loves-0").assertExists()
        tap("reveal-recall-god-loves-0")
        compose.onNodeWithTag("explanation-recall-god-loves-0").assertDoesNotExist()
        compose.onNodeWithTag("complete-lesson").assertDoesNotExist()
        tap("lesson-answer-0")
        compose.onNodeWithTag("complete-lesson").performScrollTo().assertIsDisplayed()
    }

    @Test fun `every lesson has an explicit teaching map and original images decode offline`() {
        val resources = ApplicationProvider.getApplicationContext<SertumApplication>().resources
        FormationCatalog.lessons.forEach { lesson ->
            val map = lessonConceptMap(lesson.id)
            assertTrue(map.nodes.size >= 2 && map.note.isNotBlank())
        }
        listOf("god-loves", "sacraments", "mercy", "daily").forEach { id ->
            val art = teachingImage(id)!!
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(resources, art.resource, bounds)
            assertEquals(id, 960, bounds.outWidth)
            assertEquals(id, 640, bounds.outHeight)
            assertTrue(art.description.isNotBlank() && art.question.isNotBlank() && art.explanation.isNotBlank())
        }
    }

    @Test
    @Config(qualifiers = "w320dp-h760dp-xxhdpi")
    fun `maps images and study text remain readable on a narrow phone with enlarged text in Vigil`() {
        val lesson = mutableStateOf("trinity")
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                SertumTheme(ThemeMode.VIGIL, MysterySet.JOYFUL) {
                    Box(Modifier.fillMaxSize().background(SertumTheme.colors.background)) { FormationScreen(null, GardenState(), lesson.value) {} }
                }
            }
        }
        compose.onNodeWithTag("lesson-concept-map").performScrollTo().assertIsDisplayed()
        shot("trinity-vigil-large-text")
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true).fetchSemanticsNodes().forEach { n ->
            val results = mutableListOf<TextLayoutResult>()
            n.config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
            results.forEach { text ->
                assertFalse("Text height: ${text.layoutInput.text}", text.didOverflowHeight)
                val widest = (0 until text.lineCount).maxOfOrNull { text.getLineRight(it) - text.getLineLeft(it) } ?: 0f
                assertTrue("Text width: ${text.layoutInput.text}", widest <= text.size.width + 1f)
                assertTrue((0 until text.lineCount).none { text.isLineEllipsized(it) })
            }
        }
        compose.runOnIdle { lesson.value = "sacraments" }
        compose.onNodeWithTag("lesson-teaching-image").performScrollTo().assertIsDisplayed()
        shot("baptism-vigil-large-text")
    }
}

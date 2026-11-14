package com.sertumrosarum

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.domain.formation.FormationCatalog
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class FormationArtworkFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Before fun ready() { compose.awaitSertumReady() }
    private fun tap(tag: String, scroll: Boolean = false) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true)
        if (scroll) node.performScrollTo()
        node.performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }
    private fun shot(name: String) { compose.onRoot().captureRoboImage("build/outputs/roborazzi/artwork-$name.png") }
    private fun back() { compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }; compose.waitForIdle() }

    @Test fun everyPillarAndPracticalJourneyOpensAtItsPainting() {
        tap("tab-CATECHISM")
        compose.onNodeWithTag("formation-hero").assertIsDisplayed()
        shot("catechesis-day")
        FormationCatalog.pillars.forEachIndexed { index, pillar ->
            // Every pillar is a chapter on the course page; its first lesson opens straight from there.
            val lesson = FormationCatalog.lessons.first { it.pillar == pillar }
            tap("lesson-${lesson.id}", true)
            compose.onNodeWithTag("lesson-artwork").assertIsDisplayed()
            shot("pillar-$index")
            back()
        }
        tap("formation-Na vida", true)
        shot("journeys-day")
        FormationCatalog.journeys.forEach { journey ->
            tap("journey-${journey.id}", true)
            compose.onNodeWithTag("journey-artwork").assertIsDisplayed()
            shot("journey-${journey.id}")
            back()
        }
    }

    @Test fun personalSectionsKeepPaintingsAndReadableActionsInDayAndVigil() {
        for (mode in listOf("DAY", "VIGIL")) {
            tap("tab-HOME"); tap("theme-menu"); tap("theme-$mode")
            tap("tab-GARDEN")
            for (section in listOf("Intenções", "Coroas", "Diário", "Guardados")) {
                tap("garden-$section", true)
                compose.onNodeWithTag("garden-hero").performScrollTo().assertIsDisplayed()
                shot("garden-$mode-${listOf("Intenções", "Coroas", "Diário", "Guardados").indexOf(section)}")
            }
        }
    }
}

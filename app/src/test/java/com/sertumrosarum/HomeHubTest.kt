package com.sertumrosarum

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class HomeHubTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Before fun ready() { compose.awaitSertumReady() }
    private fun tap(tag: String, scroll: Boolean = false) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true)
        if (scroll) node.performScrollTo()
        node.performSemanticsAction(SemanticsActions.OnClick)
    }
    private fun shot(name: String) { compose.waitForIdle(); compose.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png") }
    private fun assertOrder() {
        val positions = listOf("CATECHISM", "GUIDES", "HOME", "TREASURY", "GARDEN").map { compose.onNodeWithTag("tab-$it").getUnclippedBoundsInRoot().let { (it.left.value + it.right.value) / 2f } }
        assertEquals(positions.sorted(), positions)
        val bar = compose.onNodeWithTag("bottom-bar").getUnclippedBoundsInRoot()
        assertEquals((bar.left.value + bar.right.value) / 2f, positions[2], 1f)
    }
    @Test fun centralHomeConnectsAllFourDestinations() {
        assertOrder(); compose.onNodeWithTag("mystery-selector").assertDoesNotExist()
        shot("hub-home-day")
        tap("home-catechism", true); compose.onNodeWithTag("formation").assertIsDisplayed(); compose.onNodeWithTag("tab-CATECHISM").assertIsSelected(); shot("hub-catechism")
        tap("tab-HOME"); tap("other-guides", true); compose.onNodeWithTag("guides").assertIsDisplayed(); shot("hub-roteiros")
        tap("tab-HOME"); tap("home-treasury", true); compose.onNodeWithTag("treasury").assertIsDisplayed()
        tap("tab-HOME"); tap("home-garden", true); compose.onNodeWithTag("garden").assertIsDisplayed()
        tap("tab-HOME"); tap("home-pray", true); compose.onNodeWithTag("rosary-setup").assertIsDisplayed(); compose.onNodeWithTag("tab-GUIDES").assertIsSelected()
        tap("tab-HOME"); compose.onNodeWithTag("sanctuary").assertIsDisplayed(); assertOrder()
        tap("theme-menu"); tap("theme-VIGIL"); shot("hub-home-vigil")
    }
    @Test @Config(qualifiers = "w320dp-h640dp-xxhdpi") fun fiveDestinationsKeepTheirOrderOnNarrowPhones() {
        assertOrder()
        listOf("CATECHISM", "GUIDES", "HOME", "TREASURY", "GARDEN").forEach { compose.onNodeWithTag("tab-$it").assertIsDisplayed() }
        shot("hub-home-narrow")
    }
}

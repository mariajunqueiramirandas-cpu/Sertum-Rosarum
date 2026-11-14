package com.sertumrosarum

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasTestTag

/** Database hydration is asynchronous; idle composition alone does not imply local records are loaded. */
fun ComposeContentTestRule.awaitSertumReady() {
    waitUntil(15_000) {
        onAllNodes(hasTestTag("bottom-bar") or hasTestTag("prayer-screen") or hasTestTag("crown-delivered"), useUnmergedTree = true)
            .fetchSemanticsNodes().isNotEmpty()
    }
}

fun ComposeContentTestRule.openRosarySetup() {
    onNodeWithTag("tab-GUIDES").performSemanticsAction(SemanticsActions.OnClick)
    onNodeWithTag("rosary-setup-open").performSemanticsAction(SemanticsActions.OnClick)
    waitForIdle()
}
fun ComposeContentTestRule.ensurePrayerTarget(tag: String) {
    if ((tag == "start-rosary" || tag.startsWith("mystery-") || tag.startsWith("prayer-mode-")) && onAllNodes(hasTestTag(tag), useUnmergedTree = true).fetchSemanticsNodes().isEmpty()) openRosarySetup()
}

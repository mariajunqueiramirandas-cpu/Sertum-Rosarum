package com.sertumrosarum

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class GardenFormationDeviceTest {
    @get:Rule(order = 0) val freshGarden = FreshGardenRule()
    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()
    private val repo get() = (compose.activity.application as SertumApplication).garden
    private fun ready() { compose.waitUntil(15000) { compose.onAllNodesWithTag("sanctuary").fetchSemanticsNodes().isNotEmpty() } }
    private fun tap(tag: String, scroll: Boolean = true) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true)
        if (scroll && !tag.startsWith("tab-") && tag !in setOf("start-rosary", "return-sanctuary")) node.performScrollTo()
        node.performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }
    private fun waitFor(tag: String) { compose.waitUntil(10000) { compose.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() } }
    private fun shot(name: String) { compose.waitForIdle() }

    @Test fun intentionOfferingHistoryAndJournal() {
        ready(); tap("tab-GARDEN"); tap("new-intention")
        compose.onNodeWithTag("intention-title").performTextInput("Pela recuperação da minha mãe")
        compose.onNodeWithTag("intention-description").performTextInput("Pelos cuidados e pela nossa família.")
        tap("intention-thanksgiving"); tap("save-intention")
        runBlocking { repo.flush() }
        val intention = runBlocking { repo.dao.intentions().first().single() }
        waitFor("intention-${intention.id}"); shot("garden-intentions")
        tap("tab-GUIDES"); tap("rosary-setup-open"); tap("choose-intentions"); tap("pick-${intention.id}"); tap("picker-done")
        tap("start-rosary")
        val vm = ViewModelProvider(compose.activity)[PrayerSessionViewModel::class.java]
        compose.runOnIdle {
            assertEquals(intention.id, vm.state.value.offeredIntentions.single().id)
            repeat(vm.state.value.sequence.size) { vm.next() }
        }
        waitFor("crown-delivered"); runBlocking { repo.flush() }; shot("garden-offering")
        compose.onNodeWithText("Em ação de graças: Pela recuperação da minha mãe").assertExists()
        tap("return-sanctuary"); tap("tab-GARDEN"); tap("garden-Coroas"); shot("garden-month")
        val session = runBlocking { repo.dao.sessions().first().single { it.status == "COMPLETED" } }
        waitFor("crown-${session.id}"); tap("crown-${session.id}"); shot("garden-crown-detail")
        tap("crown-reflect")
        compose.onNodeWithTag("journal-text").performTextInput("Hoje aprendi a confiar um pouco mais.")
        tap("save-reflection"); runBlocking { repo.flush() }
        assertEquals(session.id, runBlocking { repo.dao.journal().first().single().sessionId })
        shot("garden-journal")
    }

    @Test fun chapletHistoryUsesItsOwnStructure() {
        ready(); tap("tab-GUIDES"); tap("guide-DIVINE_MERCY")
        compose.onNodeWithTag("guide-start").performSemanticsAction(SemanticsActions.OnClick)
        val vm = ViewModelProvider(compose.activity)[PrayerSessionViewModel::class.java]
        compose.runOnIdle { repeat(vm.state.value.sequence.size) { vm.next() } }
        waitFor("crown-delivered"); runBlocking { repo.flush() }
        tap("return-sanctuary"); tap("tab-GARDEN"); tap("garden-Coroas")
        val session = runBlocking { repo.dao.sessions().first().single { it.status == "COMPLETED" } }
        waitFor("crown-${session.id}"); tap("crown-${session.id}")
        val label = com.sertumrosarum.feature.guides.PrayerGuide.DIVINE_MERCY.shape.badge
        compose.onNodeWithText("$label · A sós").assertExists()
    }

    @Test fun learningQuestionFeedbackAndBookmark() {
        ready(); tap("tab-TREASURY"); tap("open-formation")
        shot("formation-path")
        tap("lesson-god-loves")
        tap("bookmark-lesson")
        tap("lesson-answer-1"); compose.onNodeWithTag("lesson-answer-explanation").assertExists()
        tap("lesson-answer-0")
        compose.onNodeWithTag("lesson-reflection").performScrollTo().performTextInput("Deus me ama primeiro.")
        tap("complete-lesson"); runBlocking { repo.flush() }
        assertNotNull(runBlocking { repo.dao.lesson("god-loves")?.completedAt })
        assertEquals("Deus me ama primeiro.", runBlocking { repo.dao.lesson("god-loves")?.reflection })
        compose.onNodeWithTag("formation-back").performSemanticsAction(SemanticsActions.OnClick)
        tap("formation-Perguntas")
        compose.onNodeWithTag("formation-search").performTextInput("Por que Deus permite o sofrimento?")
        tap("question-suffering"); shot("formation-question")
        tap("feedback-question-suffering")
        compose.onNodeWithTag("feedback-text").performScrollTo().performTextInput("Gostaria de sugerir uma explicação adicional.")
        tap("save-feedback"); runBlocking { repo.flush() }
        assertEquals(1, runBlocking { repo.dao.feedbackCount() })
        tap("tab-GARDEN"); tap("garden-Guardados")
        compose.onNodeWithText("Deus vem ao nosso encontro").assertExists()
    }
}

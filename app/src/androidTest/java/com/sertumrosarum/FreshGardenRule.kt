package com.sertumrosarum

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import com.sertumrosarum.data.garden.PersonalPreferences
import org.junit.rules.ExternalResource

/** Reset local records before launching each device test, independently of test order. */
class FreshGardenRule : ExternalResource() {
    override fun before() = runBlocking(Dispatchers.IO) {
        val app = ApplicationProvider.getApplicationContext<SertumApplication>()
        app.garden.flush()
        app.garden.database.clearAllTables()
        app.garden.savePreferences(PersonalPreferences())
    }
}

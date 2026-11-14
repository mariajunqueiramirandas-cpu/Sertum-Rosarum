package com.sertumrosarum

import android.app.Application
import com.sertumrosarum.data.garden.*

class SertumApplication : Application() {
    val garden: GardenRepository by lazy { GardenRepository.create(this) }
}

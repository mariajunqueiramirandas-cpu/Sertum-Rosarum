package com.sertumrosarum.feature.guides

import com.sertumrosarum.ui.canvas.DevotionalIllustration

/** One coherent family of paintings for the ten prayer guides. */
internal val PrayerGuide.illustration: DevotionalIllustration get() = when (this) {
    PrayerGuide.TRADITIONAL, PrayerGuide.CATECHETICAL -> DevotionalIllustration.PRAYER_BOOK
    PrayerGuide.COMPLETE -> DevotionalIllustration.SEVEN_JOYS
    PrayerGuide.FAMILY -> DevotionalIllustration.HOLY_FAMILY
    PrayerGuide.SICK -> DevotionalIllustration.CONSOLATION
    PrayerGuide.PEACE -> DevotionalIllustration.PEACE
    PrayerGuide.DIVINE_MERCY -> DevotionalIllustration.DIVINE_MERCY
    PrayerGuide.SAINT_JOSEPH -> DevotionalIllustration.SAINT_JOSEPH
    PrayerGuide.TEARS -> DevotionalIllustration.OUR_LADY_OF_TEARS
    PrayerGuide.FRANCISCAN -> DevotionalIllustration.SEVEN_JOYS
}

/** Ordinary rosaries retain the offered rose; the other guides carry their own intention. */
internal val PrayerGuide.offeringIllustration: DevotionalIllustration? get() = when (this) {
    PrayerGuide.TRADITIONAL, PrayerGuide.CATECHETICAL, PrayerGuide.COMPLETE -> null
    else -> illustration
}

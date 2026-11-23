package com.sertumrosarum.feature.guides

import com.sertumrosarum.ui.canvas.DevotionalIllustration

/** One coherent family of paintings for all prayer guides and Brazilian devotions. */
internal val PrayerGuide.illustration: DevotionalIllustration get() = when (this) {
    PrayerGuide.TRADITIONAL, PrayerGuide.CATECHETICAL -> DevotionalIllustration.PRAYER_BOOK
    PrayerGuide.SINGLE_DECADE -> DevotionalIllustration.CONTEMPLATIVE_PRAYER
    PrayerGuide.COMPLETE, PrayerGuide.FULL_ROSARY -> DevotionalIllustration.SEVEN_JOYS
    PrayerGuide.FAMILY -> DevotionalIllustration.HOLY_FAMILY
    PrayerGuide.SICK -> DevotionalIllustration.CONSOLATION
    PrayerGuide.PEACE -> DevotionalIllustration.PEACE
    PrayerGuide.DECEASED -> DevotionalIllustration.TREASURY_CONSOLATION
    PrayerGuide.ANXIETY -> DevotionalIllustration.TREASURY_NIGHT
    PrayerGuide.VOCATIONS -> DevotionalIllustration.FAITH
    PrayerGuide.DIVINE_MERCY -> DevotionalIllustration.DIVINE_MERCY
    PrayerGuide.SAINT_JOSEPH -> DevotionalIllustration.SAINT_JOSEPH
    PrayerGuide.TEARS, PrayerGuide.SEVEN_SORROWS -> DevotionalIllustration.OUR_LADY_OF_TEARS
    PrayerGuide.FRANCISCAN -> DevotionalIllustration.SEVEN_JOYS
    PrayerGuide.HOLY_WOUNDS -> DevotionalIllustration.DIVINE_MERCY
    PrayerGuide.SAINT_MICHAEL -> DevotionalIllustration.TREASURY_MORNING
    PrayerGuide.VIA_SACRA -> DevotionalIllustration.RECONCILIATION
}

/** Ordinary rosaries retain the offered rose; the other guides carry their own intention. */
internal val PrayerGuide.offeringIllustration: DevotionalIllustration? get() = when (this) {
    PrayerGuide.TRADITIONAL, PrayerGuide.CATECHETICAL, PrayerGuide.COMPLETE, PrayerGuide.SINGLE_DECADE, PrayerGuide.FULL_ROSARY -> null
    else -> illustration
}

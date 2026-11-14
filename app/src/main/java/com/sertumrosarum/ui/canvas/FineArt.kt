package com.sertumrosarum.ui.canvas

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.sertumrosarum.R
import com.sertumrosarum.domain.prayer.MysterySet

private const val MET = "The Metropolitan Museum of Art, Nova York"
private const val NGA = "National Gallery of Art, Washington"
private const val AIC = "The Art Institute of Chicago"
private const val CMA = "The Cleveland Museum of Art"

/**
 * Works of sacred art in the public domain, each from a museum that releases its image under CC0.
 * Every work keeps its credit and its source; docs/artwork/fine-art/README.md lists them for review.
 */
enum class FineArt(
    internal val resourceId: Int,
    val title: String,
    val artist: String,
    val date: String,
    val collection: String,
    val source: String,
) {
    ANNUNCIATION(R.drawable.art_mystery_joyful_1, "A Anunciação", "Masolino da Panicale", "c. 1423–1424", NGA, "https://www.nga.gov/collection/art-object-page.18.html"),
    VISITATION(R.drawable.art_mystery_joyful_2, "A Visitação, com São Nicolau e Santo Antão", "Piero di Cosimo", "c. 1489–1490", NGA, "https://www.nga.gov/collection/art-object-page.505.html"),
    NATIVITY(R.drawable.art_mystery_joyful_3, "A Natividade", "Fra Bartolomeo", "1504–1507", AIC, "https://www.artic.edu/artworks/184371"),
    PRESENTATION(R.drawable.art_mystery_joyful_4, "A Apresentação no Templo", "Álvaro Pires de Évora", "c. 1405–1415", MET, "https://www.metmuseum.org/art/collection/search/437298"),
    FINDING(R.drawable.art_mystery_joyful_5, "Cristo entre os doutores", "Mestre dos Reis Católicos", "c. 1495–1497", NGA, "https://www.nga.gov/collection/art-object-page.41656.html"),
    BAPTISM(R.drawable.art_mystery_luminous_1, "O Batismo de Cristo", "Perugino", "1500–1505", AIC, "https://www.artic.edu/artworks/16197"),
    CANA(R.drawable.art_mystery_luminous_2, "As Bodas de Caná", "Juan de Flandes", "c. 1497", MET, "https://www.metmuseum.org/art/collection/search/436801"),
    KINGDOM(R.drawable.art_mystery_luminous_3, "O chamado de Pedro e André", "Duccio di Buoninsegna", "1308–1311", NGA, "https://www.nga.gov/collection/art-object-page.282.html"),
    TRANSFIGURATION(R.drawable.art_mystery_luminous_4, "A Transfiguração, de um evangeliário armênio", "T'oros, o Diácono", "1311", MET, "https://www.metmuseum.org/art/collection/search/751462"),
    LAST_SUPPER(R.drawable.art_mystery_luminous_5, "A Última Ceia", "Ugolino da Siena", "c. 1325", MET, "https://www.metmuseum.org/art/collection/search/459131"),
    AGONY(R.drawable.art_mystery_sorrowful_1, "A Agonia no Horto", "Rafael", "c. 1504", MET, "https://www.metmuseum.org/art/collection/search/437371"),
    FLAGELLATION(R.drawable.art_mystery_sorrowful_2, "A Flagelação de Cristo", "Bachiacca", "c. 1512–1515", NGA, "https://www.nga.gov/collection/art-object-page.41696.html"),
    THORNS(R.drawable.art_mystery_sorrowful_3, "A Virgem dolorosa e Cristo coroado de espinhos", "Oficina, segundo Dieric Bouts", "c. 1520–1530", MET, "https://www.metmuseum.org/art/collection/search/435763"),
    CROSS(R.drawable.art_mystery_sorrowful_4, "Cristo carregando a cruz", "El Greco", "c. 1577–1587", MET, "https://www.metmuseum.org/art/collection/search/459087"),
    CRUCIFIXION(R.drawable.art_mystery_sorrowful_5, "A Crucifixão", "Fra Angelico", "c. 1440", MET, "https://www.metmuseum.org/art/collection/search/435577"),
    RESURRECTION(R.drawable.art_mystery_glorious_1, "A Ressurreição", "Benvenuto di Giovanni", "1491", NGA, "https://www.nga.gov/collection/art-object-page.41670.html"),
    ASCENSION(R.drawable.art_mystery_glorious_2, "A Ascensão", "Johann Koerbecke", "1456–1457", NGA, "https://www.nga.gov/collection/art-object-page.45890.html"),
    PENTECOST(R.drawable.art_mystery_glorious_3, "Pentecostes", "Oficina de Bernard van Orley", "c. 1520", AIC, "https://www.artic.edu/artworks/41217"),
    ASSUMPTION(R.drawable.art_mystery_glorious_4, "A Assunção da Virgem", "Nicolas Poussin", "c. 1630–1632", NGA, "https://www.nga.gov/collection/art-object-page.46470.html"),
    CORONATION(R.drawable.art_mystery_glorious_5, "A Coroação da Virgem", "Annibale Carracci", "depois de 1595", MET, "https://www.metmuseum.org/art/collection/search/435853"),

    CHILDREN(R.drawable.art_lesson_god_loves, "Cristo acolhendo as crianças", "Sébastien Bourdon", "c. 1655", AIC, "https://www.artic.edu/artworks/9672"),
    TRINITY(R.drawable.art_lesson_trinity, "A Trindade", "Laurent Girardin", "c. 1460", CMA, "https://clevelandart.org/art/1960.79"),
    SALVATOR_MUNDI(R.drawable.art_lesson_jesus, "Salvator Mundi", "Hans Memling", "1480–1485", MET, "https://www.metmuseum.org/art/collection/search/437061"),
    ENCLOSED_GARDEN(R.drawable.art_lesson_church_mary, "A Virgem com o Menino e santos no jardim fechado", "Seguidor de Robert Campin", "c. 1440–1460", NGA, "https://www.nga.gov/collection/art-object-page.45888.html"),
    BAPTISM_POUSSIN(R.drawable.art_lesson_sacraments, "O Batismo de Cristo", "Nicolas Poussin", "1641–1642", NGA, "https://www.nga.gov/collection/art-object-page.32692.html"),
    MASS_OF_GREGORY(R.drawable.art_lesson_mass, "A Missa de São Gregório", "Pintor espanhol", "c. 1490–1500", MET, "https://www.metmuseum.org/art/collection/search/437722"),
    EMMAUS(R.drawable.art_lesson_eucharist, "A Ceia em Emaús", "Diego Velázquez", "1622–1623", MET, "https://www.metmuseum.org/art/collection/search/437871"),
    PRODIGAL_SON(R.drawable.art_lesson_reconciliation, "O regresso do filho pródigo", "Bartolomé Esteban Murillo", "1667–1670", NGA, "https://www.nga.gov/collection/art-object-page.34956.html"),
    CLOTHING_THE_NAKED(R.drawable.art_lesson_dignity, "Vestir os nus", "Michiel Sweerts", "c. 1661", MET, "https://www.metmuseum.org/art/collection/search/437769"),
    TEARS_OF_PETER(R.drawable.art_lesson_grace_freedom, "As lágrimas de São Pedro", "Jusepe de Ribera", "c. 1612–1613", MET, "https://www.metmuseum.org/art/collection/search/441971"),
    GOOD_SAMARITAN(R.drawable.art_lesson_mercy, "O Bom Samaritano", "Domenico Fetti", "c. 1618–1622", MET, "https://www.metmuseum.org/art/collection/search/436302"),
    ADULTERESS(R.drawable.art_lesson_forgiveness, "Cristo e a mulher adúltera", "Lucas Cranach, o Jovem, e oficina", "c. 1545–1550", MET, "https://www.metmuseum.org/art/collection/search/436034"),
    MAGDALEN(R.drawable.art_lesson_prayer, "Madalena penitente", "Georges de La Tour", "c. 1640", MET, "https://www.metmuseum.org/art/collection/search/436839"),
    ROSARY(R.drawable.art_lesson_rosary, "Os quinze mistérios e a Virgem do Rosário", "Pintor de Bruxelas", "c. 1515–1520", MET, "https://www.metmuseum.org/art/collection/search/437216"),
    PROPHETS(R.drawable.art_lesson_year, "A Natividade com os profetas Isaías e Ezequiel", "Duccio di Buoninsegna", "1308–1311", NGA, "https://www.nga.gov/collection/art-object-page.10.html"),
    AMONG_THE_HUMBLE(R.drawable.art_lesson_daily, "Entre os humildes", "Léon-Augustin Lhermitte", "1905", MET, "https://www.metmuseum.org/art/collection/search/436883"),
    ;

    /** Artist and date, as a caption reads them. */
    val byline: String get() = "$artist, $date"

    companion object {
        const val LICENSE = "Obra em domínio público. Imagem cedida pelo museu sob CC0."

        fun forMystery(set: MysterySet, index: Int): FineArt = entries[set.ordinal * 5 + index]

        fun forLesson(id: String): FineArt? = when (id) {
            "god-loves" -> CHILDREN
            "trinity" -> TRINITY
            "jesus" -> SALVATOR_MUNDI
            "church-mary" -> ENCLOSED_GARDEN
            "sacraments" -> BAPTISM_POUSSIN
            "mass" -> MASS_OF_GREGORY
            "eucharist" -> EMMAUS
            "reconciliation" -> PRODIGAL_SON
            "dignity" -> CLOTHING_THE_NAKED
            "grace-freedom" -> TEARS_OF_PETER
            "mercy" -> GOOD_SAMARITAN
            "forgiveness" -> ADULTERESS
            "prayer" -> MAGDALEN
            "rosary" -> ROSARY
            "year" -> PROPHETS
            "daily" -> AMONG_THE_HUMBLE
            else -> null
        }
    }
}

/**
 * The painting decoded no larger than it will be shown, so a row of small thumbnails
 * never holds full-size bitmaps in memory.
 */
@Composable
fun rememberFineArt(art: FineArt, maxPx: Int): ImageBitmap {
    val resources = LocalContext.current.resources
    return remember(art, maxPx, resources) {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, art.resourceId, bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxPx) sample *= 2
        BitmapFactory.decodeResource(resources, art.resourceId, BitmapFactory.Options().apply { inSampleSize = sample }).asImageBitmap()
    }
}

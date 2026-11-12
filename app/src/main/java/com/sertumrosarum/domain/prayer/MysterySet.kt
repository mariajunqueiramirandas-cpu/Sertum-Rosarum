package com.sertumrosarum.domain.prayer

import java.time.DayOfWeek

data class Mystery(val title: String, val scripture: String, val fruit: String)

enum class MysterySet(
    val label: String,
    val singular: String,
    val contemplation: String,
    val invitation: String,
    val days: String,
    val mysteries: List<Mystery>,
) {
    JOYFUL(
        "Gozosos", "Gozoso", "A alegria de acolher o amor.",
        "Com Maria, contemple a alegria de Deus que vem habitar entre nós.", "SEGUNDA E SÁBADO",
        listOf(
            Mystery("A Anunciação", "Lc 1, 26–38", "Humildade"),
            Mystery("A Visitação", "Lc 1, 39–56", "Amor ao próximo"),
            Mystery("O Nascimento de Jesus", "Lc 2, 1–20", "Desprendimento"),
            Mystery("A Apresentação no Templo", "Lc 2, 22–38", "Obediência"),
            Mystery("O Encontro de Jesus no Templo", "Lc 2, 41–52", "Busca de Deus"),
        ),
    ),
    LUMINOUS(
        "Luminosos", "Luminoso", "Deixe a luz encontrar você.",
        "Com Maria, contemple a luz de Cristo que se revela ao mundo.", "QUINTA-FEIRA",
        listOf(
            Mystery("O Batismo de Jesus", "Mt 3, 13–17", "Abertura ao Espírito"),
            Mystery("As Bodas de Caná", "Jo 2, 1–12", "Confiança em Maria"),
            Mystery("O Anúncio do Reino", "Mc 1, 14–15", "Conversão"),
            Mystery("A Transfiguração", "Mt 17, 1–9", "Escuta de Cristo"),
            Mystery("A Instituição da Eucaristia", "Lc 22, 14–20", "Amor à Eucaristia"),
        ),
    ),
    SORROWFUL(
        "Dolorosos", "Doloroso", "O amor permanece na entrega.",
        "Com Maria, permaneça junto a Jesus no caminho do amor que se entrega.", "TERÇA E SEXTA",
        listOf(
            Mystery("A Agonia no Horto", "Mt 26, 36–46", "Confiança em Deus"),
            Mystery("A Flagelação de Jesus", "Jo 19, 1", "Pureza de coração"),
            Mystery("A Coroação de Espinhos", "Mt 27, 27–31", "Mansidão"),
            Mystery("Jesus Carrega a Cruz", "Jo 19, 16–17", "Perseverança"),
            Mystery("A Crucificação e Morte de Jesus", "Jo 19, 25–30", "Amor e perdão"),
        ),
    ),
    GLORIOUS(
        "Gloriosos", "Glorioso", "A esperança floresce de novo.",
        "Com Maria, contemple a vida que vence a morte e renova a esperança.", "QUARTA E DOMINGO",
        listOf(
            Mystery("A Ressurreição de Jesus", "Jo 20, 1–18", "Fé"),
            Mystery("A Ascensão do Senhor", "At 1, 6–11", "Esperança"),
            Mystery("A Vinda do Espírito Santo", "At 2, 1–13", "Caridade"),
            Mystery("A Assunção de Maria", "Lc 1, 46–55", "União com Deus"),
            Mystery("A Coroação de Maria", "Ap 12, 1", "Confiança em Maria"),
        ),
    );

    companion object {
        fun forDay(day: DayOfWeek): MysterySet = when (day) {
            DayOfWeek.MONDAY, DayOfWeek.SATURDAY -> JOYFUL
            DayOfWeek.THURSDAY -> LUMINOUS
            DayOfWeek.TUESDAY, DayOfWeek.FRIDAY -> SORROWFUL
            DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY -> GLORIOUS
        }
    }
}

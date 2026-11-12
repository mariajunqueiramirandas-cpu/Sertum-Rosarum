package com.sertumrosarum.domain.prayer

/** One intentional contemplation for each mystery in each of the three themed scripts. */
object ThemedMeditations {
    private val family = mapOf(
        MysterySet.JOYFUL to listOf(
            "Com o sim de Maria, peçamos que nossa família acolha a vida e os chamados de Deus.",
            "Com Maria que visita Isabel, rezemos pelos parentes distantes e por quem precisa de companhia.",
            "Diante de Jesus na manjedoura, peçamos um lar em que cada pessoa seja acolhida com dignidade.",
            "Com Maria e José no Templo, confiemos a Deus os filhos e as responsabilidades de nossa casa.",
            "Com os pais que procuram Jesus, rezemos pelas famílias separadas e pelo diálogo entre gerações.",
        ),
        MysterySet.LUMINOUS to listOf(
            "No Batismo de Jesus, recordemos que somos filhos amados; que essa certeza cure as relações de nossa casa.",
            "Nas bodas de Caná, confiemos a Jesus os casais e as famílias que enfrentam dificuldades.",
            "Ao ouvir o anúncio do Reino, peçamos conversão para reconhecer nossos erros e recomeçar em família.",
            "No monte da Transfiguração, peçamos a graça de escutar Jesus e de escutar uns aos outros.",
            "À mesa da Eucaristia, rezemos para que a partilha e o serviço deem forma à vida de nossa casa.",
        ),
        MysterySet.SORROWFUL to listOf(
            "No Getsêmani, entreguemos a angústia dos pais, dos filhos e de quem vive sozinho.",
            "Diante de Jesus flagelado, rezemos pelas vítimas de violência doméstica, por proteção e por ajuda concreta.",
            "Diante da coroa de espinhos, peçamos que a palavra em nossa casa respeite e nunca humilhe.",
            "No caminho da cruz, rezemos por quem sustenta a família em meio ao cansaço e à falta de recursos.",
            "Junto à cruz, confiemos os familiares falecidos e peçamos consolo para quem vive o luto.",
        ),
        MysterySet.GLORIOUS to listOf(
            "Com Cristo ressuscitado, peçamos esperança para as famílias que precisam recomeçar.",
            "Na Ascensão do Senhor, rezemos pelos parentes que partiram para longe e por seus novos caminhos.",
            "Em Pentecostes, peçamos ao Espírito Santo comunhão entre as gerações e respeito às diferenças.",
            "Na Assunção de Maria, confiemos os idosos de nossa família e o cuidado de sua dignidade.",
            "Com Maria na glória, entreguemos nossa família à sua intercessão e aprendamos a servir com amor.",
        ),
    )
    private val sick = mapOf(
        MysterySet.JOYFUL to listOf(
            "Com Maria na Anunciação, confiemos ao Senhor o medo de um diagnóstico e o próximo passo do tratamento.",
            "Na Visitação, rezemos por quem acompanha os enfermos e por visitas que levem cuidado e alívio.",
            "Diante do Menino frágil, rezemos pelas crianças doentes e por suas famílias.",
            "Na Apresentação, entreguemos a Deus os profissionais de saúde e suas decisões diárias.",
            "Com Maria e José à procura de Jesus, rezemos por quem espera exames ou busca um tratamento.",
        ),
        MysterySet.LUMINOUS to listOf(
            "No Jordão, peçamos que cada enfermo se saiba amado por Deus, mesmo na incerteza.",
            "Em Caná, apresentemos a Jesus as necessidades que os doentes nem sempre conseguem expressar.",
            "No anúncio do Reino, peçamos acesso ao cuidado, justiça e atenção às pessoas esquecidas.",
            "Na Transfiguração, peçamos uma luz de esperança para quem atravessa dias difíceis.",
            "Na Eucaristia, rezemos pelos enfermos que desejam a comunhão e por quem os visita.",
        ),
        MysterySet.SORROWFUL to listOf(
            "Na agonia de Jesus, rezemos por quem enfrenta ansiedade, depressão ou solidão, e por ajuda adequada.",
            "Diante de Jesus ferido, confiemos as dores do corpo e peçamos alívio e cuidado digno.",
            "Na coroação de espinhos, rezemos por quem sofre preconceito por causa de sua doença.",
            "No caminho da cruz, peçamos descanso e apoio para cuidadores e familiares exaustos.",
            "Junto à cruz, acompanhemos em oração os que estão morrendo e os que permanecem ao seu lado.",
        ),
        MysterySet.GLORIOUS to listOf(
            "Na Ressurreição, peçamos que a esperança de Cristo sustente quem vive uma doença prolongada.",
            "Na Ascensão, entreguemos os limites que não podemos resolver e assumamos o cuidado que podemos oferecer.",
            "Em Pentecostes, peçamos sabedoria para as equipes de saúde e uma escuta atenta para os familiares.",
            "Na Assunção, recordemos a dignidade do corpo e rezemos por quem depende de cuidados diários.",
            "Com Maria glorificada, confiemos os enfermos à sua intercessão e ofereçamos nossa presença.",
        ),
    )
    private val peace = mapOf(
        MysterySet.JOYFUL to listOf(
            "Na Anunciação, peçamos um coração capaz de responder a Deus sem impor sua vontade aos outros.",
            "Na Visitação, rezemos para que o encontro vença a desconfiança entre pessoas e povos.",
            "Em Belém, peçamos proteção para crianças e famílias atingidas pela guerra.",
            "Na Apresentação, rezemos para que as novas gerações cresçam na justiça e no respeito.",
            "No encontro de Jesus no Templo, peçamos diálogo para quem perdeu o caminho da reconciliação.",
        ),
        MysterySet.LUMINOUS to listOf(
            "No Batismo de Jesus, recordemos a dignidade que nos une e rejeitemos toda discriminação.",
            "Nas bodas de Caná, peçamos atenção às necessidades que estão na origem de tantos conflitos.",
            "No anúncio do Reino, rezemos pela conversão dos violentos e por decisões públicas justas.",
            "Na Transfiguração, peçamos a graça de escutar antes de julgar ou responder com agressividade.",
            "Na Eucaristia, rezemos por uma partilha que combata a fome e crie fraternidade.",
        ),
        MysterySet.SORROWFUL to listOf(
            "No Getsêmani, unamos nossa oração à angústia de quem vive sob bombardeios ou ameaças.",
            "Na Flagelação, rezemos pelas vítimas de tortura e pelo fim da violência contra pessoas indefesas.",
            "Na coroação de espinhos, peçamos que nenhum povo ou pessoa seja desumanizado por palavras de ódio.",
            "No caminho da cruz, acompanhemos os refugiados e os que foram obrigados a deixar sua casa.",
            "Junto à cruz, rezemos pelos mortos nos conflitos e por um perdão unido à verdade e à justiça.",
        ),
        MysterySet.GLORIOUS to listOf(
            "Na Ressurreição, peçamos coragem para reconstruir relações e lugares marcados pela violência.",
            "Na Ascensão, assumamos nossa responsabilidade de testemunhar a paz nas decisões de cada dia.",
            "Em Pentecostes, rezemos por povos que se compreendam sem perder sua língua e sua cultura.",
            "Na Assunção de Maria, peçamos esperança para quem já não consegue imaginar um futuro de paz.",
            "Com a Rainha da paz, confiemos o mundo a Cristo e escolhamos um gesto de reconciliação.",
        ),
    )

    fun forMystery(script: PrayerScript, set: MysterySet, index: Int): String? = when (script) {
        PrayerScript.FAMILY -> family
        PrayerScript.SICK -> sick
        PrayerScript.PEACE -> peace
        else -> null
    }?.getValue(set)?.get(index)
}

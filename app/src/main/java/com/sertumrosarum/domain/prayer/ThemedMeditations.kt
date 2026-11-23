package com.sertumrosarum.domain.prayer

/** Rich themed contemplation layered over a Rosary mystery for thematic scripts. */
data class ThemedMysteryDetail(
    val intention: String,
    val reflection: String,
    val petition: String,
)

/**
 * Complete themed contemplations for all six thematic Rosaries across all twenty mysteries,
 * plus dedicated closing prayers (solo & group) for each theme.
 */
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

    private val deceased = mapOf(
        MysterySet.JOYFUL to listOf(
            "Na Anunciação, agradeçamos pela vida dos nossos entes queridos que Deus chamou à existência por amor.",
            "Na Visitação, rezemos para que quem vive o luto receba visitas, escuta e companhia verdadeira.",
            "Em Belém, recordemos com ternura os começos simples e a história de quem partiu antes de nós.",
            "Na Apresentação, entreguemos ao Pai a vida inteira dos falecidos, com suas alegrias e imperfeições.",
            "No encontro no Templo, confiemos nossa saudade à certeza de que em Deus nenhum amor verdadeiro se perde.",
        ),
        MysterySet.LUMINOUS to listOf(
            "No Batismo de Jesus, recordemos que no Batismo nossos falecidos foram enxertados na vida eterna de Cristo.",
            "Em Caná, peçamos que o Senhor acolha os que partiram na festa eterna que nunca se acaba.",
            "No anúncio do Reino, confiemos à misericórdia de Jesus todas as falhas e pedidos de perdão de quem já partiu.",
            "Na Transfiguração, peçamos que brilhe para os fiéis defuntos a luz perpétua da face de Cristo.",
            "Na Eucaristia, unamo-nos na comunhão dos santos a todos os que comungaram conosco neste altar da terra.",
        ),
        MysterySet.SORROWFUL to listOf(
            "No Getsêmani, apresentemos a Jesus as lágrimas do luto e o medo diante da morte.",
            "Na Flagelação, confiemos à ternura do Senhor todos os que sofreram dores físicas antes de partir.",
            "Na coroação de espinhos, rezemos pelos falecidos esquecidos ou que partiram na solidão.",
            "No caminho da cruz, peçamos forças para as famílias que aprendem a caminhar carregando a saudade.",
            "Junto à cruz, escutemos a promessa de Jesus ao bom ladrão: «Hoje estarás comigo no paraíso».",
        ),
        MysterySet.GLORIOUS to listOf(
            "Na Ressurreição, proclamemos nossa esperança: Cristo venceu a morte e chamará pelo nome os que amamos.",
            "Na Ascensão, confiemos os falecidos ao Senhor que foi preparar um lugar na casa do Pai.",
            "Em Pentecostes, peçamos ao Espírito Consolador paz serena para os corações enlutados.",
            "Na Assunção de Maria, olhemos para a glória de Maria como promessa do nosso reencontro em Cristo.",
            "Na Coroação de Maria, peçamos à Mãe de Misericórdia que acolha os que partiram na paz eterna.",
        ),
    )

    private val anxiety = mapOf(
        MysterySet.JOYFUL to listOf(
            "Na Anunciação, diante do que nos perturba, aprendamos com Maria a respirar, perguntar e confiar em Deus.",
            "Na Visitação, peçamos coragem para sair do círculo fechado dos pensamentos ansiosos e buscar um encontro amigo.",
            "Em Belém, lembremos que Deus cuida de nós no pequeno e no simples — um passo de cada vez.",
            "Na Apresentação, entreguemos ao Pai o futuro que não conseguimos controlar e peçamos discernimento.",
            "No encontro no Templo, peçamos paz quando as respostas demoram e lucidez para procurar ajuda.",
        ),
        MysterySet.LUMINOUS to listOf(
            "No Batismo de Jesus, descansemos na certeza de que somos amados pelo Pai antes de qualquer desempenho.",
            "Em Caná, apresentemos a Jesus exatamente o que nos falta hoje, sem precisar fingir força.",
            "No anúncio do Reino, acolhamos a palavra que liberta da culpa obsessiva e abre um recomeço sereno.",
            "Na Transfiguração, escutemos Jesus dizer ao nosso medo: «Levantai-vos e não tenhais medo».",
            "Na Eucaristia, encontremos no silêncio diante de Cristo o ritmo calmo de quem confia no pão de cada dia.",
        ),
        MysterySet.SORROWFUL to listOf(
            "No Getsêmani, saibamos que a angústia não é falta de fé: Jesus mesmo atravessou o medo rezando ao Pai.",
            "Na Flagelação, peçamos compaixão por nossos limites e liberdade para cuidar da saúde da mente e do corpo.",
            "Na coroação de espinhos, soltemos a pressão sufocante pelo julgamento e aprovação dos outros.",
            "No caminho da cruz, aceitemos a ajuda dos «cireneus» — amigos, família e profissionais — sem isolamento.",
            "Junto à cruz, entreguemos nas mãos do Pai aquilo que hoje está além das nossas forças resolver.",
        ),
        MysterySet.GLORIOUS to listOf(
            "Na Ressurreição, confiemos que mesmo as situações que parecem sem saída podem receber luz nova.",
            "Na Ascensão, peçamos discernimento para distinguir o que depende de nós do que pertence a Deus.",
            "Em Pentecostes, invoquemos o Espírito Santo: dom de conselho, paz interior e clareza nas decisões.",
            "Na Assunção de Maria, elevemos o olhar além da pressa imediata para o horizonte amplo de Deus.",
            "Com Maria na glória, descansemos sob seu manto materno e escolhamos um único próximo passo possível.",
        ),
    )

    private val vocations = mapOf(
        MysterySet.JOYFUL to listOf(
            "Na Anunciação, rezemos pelos jovens em discernimento, para que escutem com liberdade o chamado de Deus.",
            "Na Visitação, peçamos uma Igreja em saída, atenta às periferias e pronta para servir com alegria.",
            "Em Belém, rezemos pelas famílias cristãs, primeiras escolas da fé e berço de todas as vocações.",
            "Na Apresentação, agradeçamos pelos consagrados, sacerdotes e leigos idosos que perseveraram na fidelidade.",
            "No encontro no Templo, peçamos amor apaixonado pela Palavra de Deus nos seminários e comunidades.",
        ),
        MysterySet.LUMINOUS to listOf(
            "No Batismo de Jesus, renovemos a consciência da vocação batismal de todo cristão à santidade.",
            "Em Caná, rezemos pelos noivos e casais chamados à vocação do Matrimônio, para que o vinho do amor não falte.",
            "No anúncio do Reino, peçamos missionários corajosos que anunciem o Evangelho com palavras e coerência.",
            "Na Transfiguração, rezemos pela vida contemplativa que sustenta a Igreja pela escuta silenciosa de Cristo.",
            "Na Eucaristia, rezemos pelos bispos, presbíteros e diáconos, para que sirvam com humildade e caridade.",
        ),
        MysterySet.SORROWFUL to listOf(
            "No Getsêmani, sustentemos com nossa oração os padres, religiosos e leigos que atravessam cansaço ou solidão.",
            "Na Flagelação, rezemos pela purificação da Igreja, por justiça, verdade e proteção aos mais vulneráveis.",
            "Na coroação de espinhos, peçamos pastores com coração de servo, livres de vaidade e apego ao poder.",
            "No caminho da cruz, acompanhemos os cristãos perseguidos e os missionários em lugares difíceis.",
            "Junto à cruz, peçamos fidelidade até o fim em toda vocação assumida por amor a Cristo e aos irmãos.",
        ),
        MysterySet.GLORIOUS to listOf(
            "Na Ressurreição, peçamos testemunhas alegres que irradiem a esperança pascal no mundo de hoje.",
            "Na Ascensão, rezemos pelos leigos que testemunham o Evangelho no trabalho, na ciência, na arte e na política.",
            "Em Pentecostes, invoquemos o Espírito para que desperte carismas novos e unidade em toda a Igreja.",
            "Na Assunção de Maria, confiemos à Mãe da Igreja o discernimento vocacional de toda uma geração.",
            "Na Coroação de Maria, entreguemos o Papa, os bispos e nossa paróquia à intercessão da Rainha dos Apóstolos.",
        ),
    )

    private fun reflectionFor(script: PrayerScript, mystery: Mystery): String = when (script) {
        PrayerScript.FAMILY -> "Contemplando ${mystery.titleInSentence}, trazemos diante de Deus os rostos, as alegrias e as feridas da nossa casa. Que a virtude de ${mystery.fruitInSentence} transforme o modo como nos escutamos e cuidamos uns dos outros."
        PrayerScript.SICK -> "Unimos a ${mystery.titleInSentence} todos os que atravessam a fragilidade da doença, os que aguardam respostas médicas e os que cuidam no silêncio. Que ${mystery.fruitInSentence} sustente cada passo com esperança."
        PrayerScript.PEACE -> "À luz de ${mystery.titleInSentence}, apresentamos ao Senhor os conflitos do mundo e as divisões mais próximas de nós. Que a graça de ${mystery.fruitInSentence} desarme a violência e abra caminhos de justiça."
        PrayerScript.DECEASED -> "Diante de ${mystery.titleInSentence}, recordamos com gratidão quem já partiu desta vida e confiamos nossa saudade ao amor eterno de Deus. Que ${mystery.fruitInSentence} console os enlutados na esperança da Ressurreição."
        PrayerScript.ANXIETY -> "Em ${mystery.titleInSentence}, colocamos diante de Jesus a pressa, o medo e o peso das decisões. Que a graça de ${mystery.fruitInSentence} traga lucidez, serenidade e liberdade interior."
        PrayerScript.VOCATIONS -> "Contemplando ${mystery.titleInSentence}, rezamos por toda a Igreja e por cada pessoa que busca descobrir como servir ao Senhor. Que o fruto de ${mystery.fruitInSentence} desperte respostas generosas."
        else -> ""
    }

    private fun petitionFor(script: PrayerScript, mystery: Mystery): String = when (script) {
        PrayerScript.FAMILY -> "Senhor, abençoai nossa família com o dom de ${mystery.fruitInSentence}."
        PrayerScript.SICK -> "Jesus, concedei alívio aos enfermos e fortalecei seus cuidadores com ${mystery.fruitInSentence}."
        PrayerScript.PEACE -> "Príncipe da Paz, fazei de nós instrumentos de paz através de ${mystery.fruitInSentence}."
        PrayerScript.DECEASED -> "Dai-lhes, Senhor, o descanso eterno, e sustentai nossa esperança com ${mystery.fruitInSentence}."
        PrayerScript.ANXIETY -> "Pai, acalmai o meu coração e guiai minhas decisões com ${mystery.fruitInSentence}."
        PrayerScript.VOCATIONS -> "Senhor da messe, enviai operários santos e cultivai em nós ${mystery.fruitInSentence}."
        else -> ""
    }

    fun forMystery(script: PrayerScript, set: MysterySet, index: Int): String? = when (script) {
        PrayerScript.FAMILY -> family
        PrayerScript.SICK -> sick
        PrayerScript.PEACE -> peace
        PrayerScript.DECEASED -> deceased
        PrayerScript.ANXIETY -> anxiety
        PrayerScript.VOCATIONS -> vocations
        else -> null
    }?.getValue(set)?.getOrNull(index)

    fun detailFor(script: PrayerScript, set: MysterySet, index: Int): ThemedMysteryDetail? {
        val intention = forMystery(script, set, index) ?: return null
        val mystery = set.mysteries.getOrNull(index) ?: return null
        return ThemedMysteryDetail(
            intention = intention,
            reflection = reflectionFor(script, mystery),
            petition = petitionFor(script, mystery),
        )
    }

    fun closingFor(script: PrayerScript): PrayerReading? = when (script) {
        PrayerScript.FAMILY -> PrayerReading(
            "Bênção final pela família",
            "Sagrada Família de Nazaré, Jesus, Maria e José, confiamos às vossas mãos as nossas casas. Guardai a paz nas conversas, fortalecei o perdão nos dias difíceis e ensinai-nos a amar com fidelidade cotidiana. Amém.",
            soloText = "Sagrada Família de Nazaré, Jesus, Maria e José, confio às vossas mãos a minha casa. Guardai a paz nas conversas, fortalecei o perdão nos dias difíceis e ensinai-me a amar com fidelidade cotidiana. Amém.",
        )
        PrayerScript.SICK -> PrayerReading(
            "Oração final pelos enfermos",
            "Senhor Jesus, médico dos corpos e das almas, permanecei junto de cada enfermo que hoje apresentamos. Dai descanso aos cuidadores, sabedoria às equipes de saúde e esperança firme a todos os que sofrem. Nossa Senhora, Saúde dos Enfermos, rogai por nós. Amém.",
            soloText = "Senhor Jesus, médico dos corpos e das almas, permanecei junto de cada enfermo que hoje apresento. Dai descanso aos cuidadores, sabedoria às equipes de saúde e esperança firme a todos os que sofrem. Nossa Senhora, Saúde dos Enfermos, rogai por nós. Amém.",
        )
        PrayerScript.PEACE -> PrayerReading(
            "Oração final pela paz",
            "Senhor, fazei de nós instrumentos da Vossa paz. Que onde houver violência, cresça a justiça; onde houver ódio, nasça o diálogo; e que Maria, Rainha da Paz, interceda por todos os povos feridos pela guerra. Amém.",
            soloText = "Senhor, fazei de mim instrumento da Vossa paz. Que onde houver violência, cresça a justiça; onde houver ódio, nasça o diálogo; e que Maria, Rainha da Paz, interceda por todos os povos feridos pela guerra. Amém.",
        )
        PrayerScript.DECEASED -> PrayerReading(
            "Oração final pelos falecidos",
            "Dai-lhes, Senhor, o descanso eterno, e brilhe para eles a Vossa luz. Consolai os corações marcados pela saudade e renovai em nós a certeza da Ressurreição em Cristo Jesus. Amém.",
        )
        PrayerScript.ANXIETY -> PrayerReading(
            "Entrega final na paz de Cristo",
            "Senhor Jesus, que dissestes «Deixo-vos a paz, a minha paz vos dou», recebei minhas preocupações e decisões. Concedei-me um coração tranquilo para viver este dia com confiança, um passo de cada vez. Amém.",
        )
        PrayerScript.VOCATIONS -> PrayerReading(
            "Oração final pelas vocações",
            "Senhor da messe, despertai na Vossa Igreja corações disponíveis para o sacerdócio, a vida consagrada, o matrimônio santo e a missão leiga. Maria, Mãe das Vocações, ensinai-nos a responder com generosidade. Amém.",
        )
        else -> null
    }
}

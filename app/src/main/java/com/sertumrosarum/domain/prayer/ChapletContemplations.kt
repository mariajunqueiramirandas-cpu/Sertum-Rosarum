package com.sertumrosarum.domain.prayer

/**
 * Contemplations for every non-Rosary devotional crown (Divine Mercy, St. Joseph,
 * Crown of Tears, Franciscan Crown, Seven Sorrows, Holy Wounds, St. Michael, and Via-Sacra),
 * so every chaplet offers the same calm depth of Scripture, meaning, and daily fruit.
 */
data class ChapletGroupContemplation(
    val title: String,
    val scripture: String,
    val fruit: String,
    val scriptureQuote: String,
    val scene: String,
    val meaning: String,
    val practice: String,
) {
    fun toMystery(): Mystery = Mystery(title, scripture, fruit)
    fun toContemplation(): MysteryContemplation = MysteryContemplation(
        scene = scene,
        meaning = meaning,
        practice = practice,
        beads = List(10) { "$title — $fruit." },
        scriptureQuote = scriptureQuote,
    )
}

object ChapletContemplations {

    val divineMercy: List<ChapletGroupContemplation> = listOf(
        ChapletGroupContemplation(
            "1ª Dezena · A Agonia no Getsêmani e a Misericórdia",
            "Lc 22, 41–44",
            "Confiança na provação",
            "Pai, não se faça a minha vontade, mas a Tua.",
            "No Horto das Oliveiras, Jesus assume o peso da angústia humana e oferece ao Pai sua obediência amorosa em favor de todos os que atravessam medo, solidão e tristeza.",
            "A misericórdia de Deus não é distante: Cristo entra em nossas noites mais difíceis e transforma a entrega confiante em fonte de paz para o mundo inteiro.",
            "Entregue hoje a Jesus uma preocupação que o angustia, repetindo com serenidade: «Jesus, eu confio em Vós».",
        ),
        ChapletGroupContemplation(
            "2ª Dezena · As Chagas que Curam",
            "Is 53, 4–5; Jo 19, 1",
            "Compaixão pelos feridos",
            "Pelas suas chagas fomos curados.",
            "Entregue à flagelação e aos espinhos, Jesus responde à violência sem ódio. Seu corpo ferido acolhe a dor dos injustiçados e chama os corações endurecidos à conversão.",
            "Oferecer ao Pai a Paixão do Filho é unir-se ao amor que vence o mal pelo perdão e nunca desiste da dignidade de ninguém.",
            "Pratique hoje um gesto de delicadeza com alguém que esteja cansado, ferido ou esquecido.",
        ),
        ChapletGroupContemplation(
            "3ª Dezena · O Perdão Oferecido na Cruz",
            "Lc 23, 33–43",
            "Perdão sem rancor",
            "Pai, perdoa-lhes, porque não sabem o que fazem.",
            "Da cruz, Jesus intercede por quem o fere e acolhe o ladrão arrependido com a promessa do paraíso. Nenhum passado fechado impede o encontro com a misericórdia.",
            "A Divina Misericórdia é maior que qualquer culpa: onde o ser humano reconhece sua fragilidade com verdade, o amor de Cristo abre um recomeço.",
            "Reze sinceramente por alguém com quem você teve atrito e renuncie a um pensamento de vingança.",
        ),
        ChapletGroupContemplation(
            "4ª Dezena · Sangue e Água do Lado Aberto",
            "Jo 19, 34",
            "Amor aos sacramentos",
            "Do seu lado aberto jorrou sangue e água.",
            "Do coração transpassado de Jesus na cruz brotam sangue e água — sinais vivos do Batismo que purifica e da Eucaristia que alimenta a Igreja.",
            "Às três da tarde, hora da entrega de Jesus, contemplamos o coração aberto de Deus: nada se reserva quando se ama até o fim.",
            "Prepare com carinho sua próxima participação na Missa ou na Confissão.",
        ),
        ChapletGroupContemplation(
            "5ª Dezena · Misericórdia pelo Mundo Inteiro",
            "Jo 20, 19–23",
            "Aposta na esperança",
            "A paz esteja convosco!",
            "Ressuscitado, Jesus entra no meio dos discípulos amedrontados, mostra suas chagas gloriosas e sopra o Espírito Santo para o perdão dos pecados e a paz do mundo.",
            "O Terço da Divina Misericórdia amplia o coração além de nós mesmos: rezamos «por nós e pelo mundo inteiro», confiando toda a humanidade ao amor do Pai.",
            "Escolha uma intenção pelo mundo — pelos doentes, pelos aflitos ou pela paz — e una a ela uma obra de misericórdia.",
        ),
    )

    val saintJoseph: List<ChapletGroupContemplation> = listOf(
        ChapletGroupContemplation(
            "O sonho de São José",
            "Mt 1, 18–24",
            "Confiança em Deus",
            "José, filho de Davi, não temas receber Maria, tua esposa.",
            "Diante de um mistério que ultrapassava sua compreensão, José decide agir com justiça e ternura para não expor Maria. Em sonho, o anjo revela a obra do Espírito Santo, e ao despertar José faz exatamente o que o Senhor ordenou.",
            "A santidade de São José se revela no silêncio que discerne antes de julgar e na coragem de acolher os planos de Deus mesmo quando alteram os nossos projetos.",
            "Antes de reagir a uma situação difícil hoje, faça silêncio, reze e escolha a resposta mais justa e misericordiosa.",
        ),
        ChapletGroupContemplation(
            "O nascimento de Jesus em Belém",
            "Lc 2, 1–20",
            "Acolhimento",
            "Encontraram Maria, José e o recém-nascido deitado na manjedoura.",
            "Longe de casa por causa do recenseamento, José protege Maria no parto e prepara o pouco que existe — um abrigo simples e uma manjedoura — para acolher o Filho de Deus que nasce na pobreza.",
            "José ensina que cuidar de uma família não depende de grandezas exteriores, mas de presença fiel, proteção discreta e ternura concreta.",
            "Transforme um pequeno gesto em casa em sinal de acolhimento e cuidado com quem vive com você.",
        ),
        ChapletGroupContemplation(
            "A apresentação de Jesus no Templo",
            "Lc 2, 22–38",
            "Fidelidade",
            "Levaram-No a Jerusalém para apresentá-Lo ao Senhor.",
            "José acompanha Maria ao Templo com a oferta dos pobres, escuta com admiração as profecias de Simeão e Ana e assume a missão de educar o Menino na fé de Israel.",
            "Ser fiel nas pequenas obrigações da fé prepara o coração para reconhecer a ação de Deus nos momentos decisivos da vida.",
            "Cumpre hoje com capricho e gratidão uma responsabilidade simples do seu trabalho ou da sua fé.",
        ),
        ChapletGroupContemplation(
            "A fuga para o Egito",
            "Mt 2, 13–15",
            "Proteção da vida",
            "Levanta-te, toma o Menino e sua mãe, e foge para o Egito.",
            "Avisado de noite sobre a ameaça de Herodes, José não adia: parte imediatamente com Maria e Jesus como migrante para uma terra estrangeira, sustentando a família com seu trabalho até poder voltar.",
            "São José é guardião dos vulneráveis, dos migrantes e de toda vida ameaçada. Sua fé une confiança em Deus e ação prudente e responsável.",
            "Reze pelas famílias migrantes ou ameaçadas e procure uma forma concreta de proteger quem está mais frágil.",
        ),
        ChapletGroupContemplation(
            "O encontro de Jesus no Templo",
            "Lc 2, 41–52",
            "Busca de Deus",
            "Depois de três dias, encontraram-No no Templo.",
            "Depois de três dias de procura angustiada por Jerusalém, José e Maria encontram Jesus entre os mestres. De volta a Nazaré, José ensina ao Filho o ofício de carpinteiro e acompanha seu crescimento.",
            "No trabalho honesto da oficina de Nazaré, São José mostra que as mãos que trabalham com amor cooperam silenciosamente com a obra de Deus.",
            "Ofereça o seu trabalho ou estudo de hoje a Deus, cuidando também do tempo de descanso e de diálogo em família.",
        ),
    )

    val sevenSorrows: List<ChapletGroupContemplation> = listOf(
        ChapletGroupContemplation(
            "1ª Dor · A profecia de Simeão",
            "Lc 2, 34–35",
            "Fidelidade no anúncio da cruz",
            "Uma espada traspassará a tua própria alma.",
            "No Templo de Jerusalém, enquanto celebra a alegria do Menino, Maria escuta de Simeão que Jesus será sinal de contradição e que a dor atravessará sua própria alma materna.",
            "Maria não recua diante do custo do amor: aceita acompanhar o Filho sabendo que a redenção passará pela cruz.",
            "Acolha hoje uma dificuldade inevitável sem perder a ternura nem a confiança em Deus.",
        ),
        ChapletGroupContemplation(
            "2ª Dor · A fuga para o Egito",
            "Mt 2, 13–15",
            "Confiança na Providência",
            "Partiu de noite e retirou-se para o Egito.",
            "Na pressa da madrugada, Maria e José deixam tudo para trás para salvar o Menino da violência de Herodes, vivendo o desamparo de estrangeiros em terra desconhecida.",
            "A Sagrada Família conhece na pele a dor dos exilados e dos que fogem da injustiça. Deus caminha com eles mesmo no deserto.",
            "Lembre-se em oração dos refugiados e das famílias que hoje precisam recomeçar longe de casa.",
        ),
        ChapletGroupContemplation(
            "3ª Dor · A perda de Jesus no Templo",
            "Lc 2, 43–48",
            "Perseverança na busca",
            "Teu pai e eu, aflitos, estávamos à tua procura.",
            "Por três dias longos e silenciosos, Maria e José percorrem as ruas de Jerusalém procurando o Filho de doze anos, experimentando o peso da ausência e da incerteza.",
            "Mesmo quem ama a Deus fielmente atravessa noites em que parece não O encontrar. A fé persevera procurando até reencontrar a Sua Palavra.",
            "Se hoje você sente aridez na oração, permaneça por alguns minutos diante do Evangelho sem desistir.",
        ),
        ChapletGroupContemplation(
            "4ª Dor · O encontro com Jesus no caminho do Calvário",
            "Lc 23, 27–31",
            "Presença compassiva",
            "Seguia-O uma grande multidão e mulheres que se lamentavam.",
            "No caminho íngreme até o Gólgota, o olhar de Maria encontra o rosto ferido de Jesus sob o peso da cruz. Sem poder impedir o suplício, ela permanece ao lado do Filho.",
            "Há dores que não conseguimos remover com palavras; a presença fiel e silenciosa é, muitas vezes, a forma mais alta da caridade.",
            "Esteja hoje ao lado de alguém que sofre, oferecendo escuta calma em vez de conselhos apressados.",
        ),
        ChapletGroupContemplation(
            "5ª Dor · Maria de pé junto à Cruz",
            "Jo 19, 25–27",
            "Fortaleza no amor",
            "Junto à cruz de Jesus estava de pé sua mãe.",
            "Enquanto muitos fogem pelo medo, Maria permanece de pé junto à cruz, unindo sua dor à oferta de Jesus e recebendo o discípulo amado como filho.",
            "«De pé» revela a fortaleza serena da fé: Maria não se entrega ao ódio nem ao desespero, e aos pés da cruz torna-se Mãe de toda a Igreja.",
            "Apresente a Deus uma dor que hoje lhe parece sem solução e peça fortaleza para continuar amando.",
        ),
        ChapletGroupContemplation(
            "6ª Dor · Jesus descido da Cruz nos braços de Maria",
            "Mc 15, 42–46; Jo 19, 38–40",
            "Consolo no luto",
            "José de Arimateia desceu o corpo de Jesus e envolveu-o num lençol.",
            "Os mesmos braços que embalaram o Menino em Belém acolhem agora o corpo sem vida do Filho descido da cruz, na contemplação silenciosa da Pietà.",
            "A fé cristã não nega o pranto: Maria chora verdadeiramente a morte do Filho, santificando as lágrimas de todas as mães e famílias enlutadas.",
            "Reze por quem atravessa o luto hoje e confie à misericórdia de Deus os entes queridos já falecidos.",
        ),
        ChapletGroupContemplation(
            "7ª Dor · O sepultamento de Jesus",
            "Jo 19, 41–42",
            "Esperança no silêncio",
            "No lugar onde fora crucificado havia um jardim e um sepulcro novo.",
            "Fechada a pedra do sepulcro, Maria atravessa o silêncio do Sábado Santo guardando no coração a chama da fé na promessa da Ressurreição.",
            "Entre a Sexta-feira da dor e o Domingo da vida nova existe a espera silenciosa: a esperança nasce justamente quando confiamos em Deus na escuridão.",
            "Termine o dia entregando a Deus aquilo que ainda espera resposta, confiando que a última palavra é da Vida.",
        ),
    )

    val franciscanExtra: Map<Int, ChapletGroupContemplation> = mapOf(
        3 to ChapletGroupContemplation(
            "A adoração dos Magos",
            "Mt 2, 1–12",
            "Adoração",
            "Entrando na casa, viram o Menino com Maria, sua mãe, e prostrando-se O adoraram.",
            "Guiados por uma estrela, sábios vindos do Oriente chegam a Belém, encontram Jesus nos braços de Maria e oferecem ouro, incenso e mirra em adoração.",
            "A alegria de Maria cresce ao ver que o Filho não veio apenas para um povo: todas as nações, culturas e buscas sinceras da verdade são chamadas a encontrar Cristo.",
            "Dedique alguns minutos hoje à adoração silenciosa, agradecendo a Deus por se deixar encontrar por quem O busca.",
        ),
        6 to ChapletGroupContemplation(
            "A Assunção e Coroação de Maria",
            "Lc 1, 46–55; Ap 12, 1",
            "Esperança na vida eterna",
            "O Todo-Poderoso fez em mim maravilhas.",
            "Terminada sua peregrinação na terra, Maria é elevada em corpo e alma à glória do Céu e coroada junto ao seu Filho, como sinal luminoso de esperança para toda a Igreja.",
            "As sete alegrias de Maria culminam na alegria sem fim da comunhão com a Trindade, recordando-nos a vocação eterna para a qual fomos criados.",
            "Renove hoje a sua esperança na vida eterna e confie à Rainha da Paz as alegrias e cansaços da sua semana.",
        ),
    )

    val holyWounds: List<ChapletGroupContemplation> = listOf(
        ChapletGroupContemplation(
            "1ª Chaga · A mão direita de Jesus",
            "Jo 20, 27; Sl 22, 17",
            "Retidão nas obras",
            "Vê as minhas mãos... não sejas incrédulo, mas crê.",
            "Contemplamos a mão direita de Jesus, tantas vezes estendida para abençoar crianças, levantar doentes e partir o pão, agora transpassada pelo cravo na cruz.",
            "Pela chaga da mão direita de Cristo, apresentamos nossas ações, omissões e trabalhos, pedindo que nossas mãos sejam instrumentos de cura e nunca de ferida.",
            "Use suas mãos hoje em um serviço discreto e generoso por alguém próximo.",
        ),
        ChapletGroupContemplation(
            "2ª Chaga · A mão esquerda de Jesus",
            "Is 53, 5; Mt 11, 28",
            "Desprendimento e partilha",
            "Vinde a mim todos os que estais cansados e sobrecarregados.",
            "Contemplamos a mão esquerda de Jesus ferida pelo amor, aberta sobre o madeiro para abraçar a humanidade inteira sem excluir ninguém.",
            "As mãos abertas de Cristo na cruz curam a nossa ganância e o medo de partilhar, ensinando-nos a liberdade de quem confia no Pai.",
            "Partilhe tempo, atenção ou recursos materiais com alguém que atravesse uma necessidade concreta.",
        ),
        ChapletGroupContemplation(
            "3ª Chaga · O pé direito de Jesus",
            "Lc 4, 43; Jo 14, 6",
            "Fidelidade no caminho",
            "Eu sou o Caminho, a Verdade e a Vida.",
            "Contemplamos o pé direito de Jesus, que percorreu estradas poeirentas da Galileia e da Judeia anunciando a Boa Nova e indo ao encontro da ovelha perdida.",
            "Pela chaga do pé direito, entregamos nossos passos, escolhas e desvios, pedindo clareza para caminhar na verdade do Evangelho.",
            "Reexamine hoje uma decisão importante à luz do Evangelho, dando um passo concreto na direção certa.",
        ),
        ChapletGroupContemplation(
            "4ª Chaga · O pé esquerdo de Jesus",
            "Mc 10, 45; Lc 15, 4",
            "Perseverança no bem",
            "O Filho do Homem veio para servir e dar a sua vida.",
            "Contemplamos o pé esquerdo de Jesus, que não recuou ao subir para Jerusalém nem parou diante do cansaço no caminho do Calvário.",
            "Quando o desânimo nos convida a parar de fazer o bem, as chagas dos pés de Cristo sustentam nossa perseverança cotidiana.",
            "Retome com paz uma prática boa — de oração, cuidado ou reconciliação — que você havia deixado de lado.",
        ),
        ChapletGroupContemplation(
            "5ª Chaga · O lado aberto e o Coração de Jesus",
            "Jo 19, 34; Jo 7, 37–38",
            "Amor misericordioso",
            "Um dos soldados abriu-lhe o lado com uma lança, e imediatamente saiu sangue e água.",
            "Contemplamos o lado transpassado de Jesus, fonte viva de misericórdia de onde nascem os sacramentos da Igreja e o refúgio para os corações aflitos.",
            "O Coração aberto de Cristo revela um amor sem reservas. Nele descansamos nossas feridas interiores e aprendemos a amar com mansidão.",
            "Permaneça um minuto em silêncio confiando suas feridas ao Coração de Jesus e pedindo um coração misericordioso.",
        ),
    )

    val saintMichaelChoirs: List<ChapletGroupContemplation> = listOf(
        ChapletGroupContemplation("1ª Saudação · Coro dos Serafins", "Is 6, 2–3", "Caridade perfeita", "Santo, Santo, Santo é o Senhor dos Exércitos!", "Com São Miguel e o coro celeste dos Serafins, pedimos ao Senhor que nos torne dignos de arder na chama da perfeita caridade.", "Os Serafins simbolizam o amor ardente a Deus. Pedir sua intercessão é desejar um coração livre da tibieza e inflamado de caridade verdadeira.", "Escolha um gesto de amor gratuito hoje, sem esperar reconhecimento."),
        ChapletGroupContemplation("2ª Saudação · Coro dos Querubins", "Ez 10, 1–4", "Abandono do pecado", "A glória do Senhor encheu o templo.", "Com São Miguel e o coro celeste dos Querubins, pedimos a graça de abandonar o caminho do pecado e correr pelo caminho da perfeição cristã.", "Os Querubins evocam a sabedoria que contempla a santidade de Deus e ilumina a consciência para escolher o bem.", "Reconheça uma ocasião de pecado que precisa evitar hoje e dê um passo firme de conversão."),
        ChapletGroupContemplation("3ª Saudação · Coro dos Tronos", "Cl 1, 16", "Humildade sincera", "Nele foram criadas todas as coisas no céu e na terra.", "Com São Miguel e o coro celeste dos Tronos, pedimos que Deus infunda em nossos corações o espírito de verdadeira e sincera humildade.", "Os Tronos lembram que somente Deus reina com justiça; a humildade devolve ao Senhor o primeiro lugar em nossa vida.", "Escute hoje com humildade alguém de quem você costuma discordar."),
        ChapletGroupContemplation("4ª Saudação · Coro das Dominações", "Ef 1, 21", "Domínio das paixões", "Cristo está acima de todo principado, potestade e dominação.", "Com São Miguel e o coro celeste das Dominações, pedimos a graça de dominar nossos sentidos e ordenar nossas inclinações desordenadas.", "O domínio próprio é fruto do Espírito Santo: ser livre por dentro para amar sem se tornar escravo dos impulsos.", "Pratique hoje uma pequena temperança nas palavras, nas telas ou nos impulsos."),
        ChapletGroupContemplation("5ª Saudação · Coro das Potestades", "Ef 6, 10–12", "Proteção nas tentações", "Revesti-vos da armadura de Deus.", "Com São Miguel e o coro celeste das Potestades, pedimos que o Senhor proteja nossa alma contra as ciladas e tentações do mal.", "A luta espiritual não é motivo de medo supersticioso, mas de vigilância serena apoiada na graça de Cristo vencedor.", "Diante de uma tentação hoje, invoque brevemente o nome de Jesus e mude de ambiente ou foco."),
        ChapletGroupContemplation("6ª Saudação · Coro das Virtudes", "Sl 103, 20", "Fortaleza no bem", "Bendizei o Senhor, vós todos os seus anjos, valorosos executores de suas ordens.", "Com São Miguel e o coro admirável das Virtudes, pedimos ao Senhor que não nos deixe cair em tentação e nos livre de todo mal.", "A força cristã nasce da união com Cristo: ela sustenta a fidelidade quando o bem exige coragem.", "Mantenha-se firme hoje em um compromisso ético ou familiar que exige coragem."),
        ChapletGroupContemplation("7ª Saudação · Coro dos Principados", "Sl 51, 12", "Obediência verdadeira", "Cria em mim, ó Deus, um coração puro.", "Com São Miguel e o coro celeste dos Principados, pedimos que Deus encha nossa alma do verdadeiro espírito de obediência.", "Obedecer a Deus e às responsabilidades justas é caminho de paz, não de servilismo: Jesus mesmo foi obediente ao Pai.", "Acolha hoje com serenidade uma obrigação que você costumava cumprir reclamando."),
        ChapletGroupContemplation("8ª Saudação · Coro dos Arcanjos", "Tb 12, 15; Dn 12, 1", "Perseverança na fé", "Miguel, o grande príncipe, vigia pelos filhos do teu povo.", "Com São Miguel e o coro celeste dos Arcanjos, pedimos ao Senhor o dom da perseverança na fé e nas boas obras, para chegarmos à glória do paraíso.", "Miguel («Quem como Deus?»), Gabriel e Rafael recordam que Deus guarda, anuncia e cura o seu povo ao longo da história.", "Reze pelos que estão desanimados na fé e ofereça uma palavra de encorajamento."),
        ChapletGroupContemplation("9ª Saudação · Coro dos Anjos", "Sl 91, 11; Mt 18, 10", "Guarda cotidiana", "Ele dará ordem aos seus anjos a teu respeito, para que te guardem em todos os teus caminhos.", "Com São Miguel e o coro celeste de todos os Anjos, pedimos que sejamos guardados por eles durante esta vida e conduzidos à glória eterna.", "Cada pessoa é acompanhada pelo cuidado providente de Deus. A gratidão ao Anjo da Guarda educa o olhar para a presença discreta do Céu no cotidiano.", "Agradeça hoje pela proteção recebida ao longo do dia e reze por alguém em viagem ou perigo."),
    )

    val viaSacraStations: List<ChapletGroupContemplation> = listOf(
        ChapletGroupContemplation("1ª Estação · Jesus é condenado à morte", "Mt 27, 22–26", "Justiça e coragem", "Pilatos entregou Jesus para ser crucificado.", "Diante do tribunal de Pilatos, o Justo é condenado por medo político e pressão da multidão. Jesus acolhe a sentença em silêncio soberano, sem responder com ódio.", "Quantas vezes lavamos as mãos diante de injustiças pequenas ou grandes para evitar incômodo? Cristo nos chama à coragem da verdade.", "Recuse hoje qualquer omissão covarde diante de uma injustiça que você pode corrigir."),
        ChapletGroupContemplation("2ª Estação · Jesus carrega a cruz", "Jo 19, 17", "Entrega fiel", "Levando a própria cruz, Jesus saiu para o lugar chamado Calvário.", "Jesus recebe nos ombros o peso áspero da cruz e inicia a caminhada para fora da cidade. Ele não abraça a dor por si mesma, mas o amor fiel que atravessa a provação.", "Seguir Jesus é assumir com amor as responsabilidades reais da nossa vocação, confiando na graça que sustenta cada passo.", "Abrace hoje uma responsabilidade difícil sem murmuração, oferecendo-a por quem você ama."),
        ChapletGroupContemplation("3ª Estação · Jesus cai pela primeira vez", "Is 53, 4–6", "Humildade para recomeçar", "Ele carregava as nossas enfermidades.", "Exausto pela noite sem sono e pela flagelação, o Filho de Deus cai sob o peso do madeiro. O chão duro mostra a verdade completa da sua humanidade.", "Cair não é o fim do caminho: Jesus se levanta e prossegue. A humildade verdadeira aceita a própria fragilidade sem desespero.", "Se você tropeçou recentemente, levante-se com mansidão hoje e recomece diante de Deus."),
        ChapletGroupContemplation("4ª Estação · Jesus encontra Sua Mãe", "Lc 2, 34–35", "Presença que consola", "Uma espada traspassará a tua própria alma.", "No meio do barulho da multidão, o olhar de Jesus cruza o olhar silencioso de Maria. Mãe e Filho se fortalecem na comunhão silenciosa de quem confia no Pai.", "Mesmo quando não podemos tirar a cruz de quem amamos, nossa presença fiel e serena transforma a solidão em companhia.", "Procure hoje uma pessoa que atravessa dor e ofereça sua presença atenta."),
        ChapletGroupContemplation("5ª Estação · Simão de Cirene ajuda Jesus", "Mc 15, 21", "Solidariedade concreta", "Obrigaram a carregar a cruz de Jesus um homem que passava, Simão de Cirene.", "Voltando do campo, um desconhecido é colocado sob a cruz de Jesus. O que começa como peso imposto transforma-se no gesto eterno da caridade que divide o fardo.", "Todo serviço prestado a quem sofre é prestado ao próprio Cristo. Ninguém foi feito para carregar sua cruz sozinho.", "Divida hoje o peso de alguém — em casa, no trabalho ou na comunidade — sem esperar que peçam."),
        ChapletGroupContemplation("6ª Estação · Verônica enxuga o rosto de Jesus", "Sl 27, 8; Mt 25, 40", "Ternura e coragem", "É a Vossa face, Senhor, que eu procuro.", "Segundo a piedosa tradição cristã, uma mulher rompe o medo da multidão para oferecer um gesto simples de alívio: enxugar o suor e o sangue do rosto de Jesus.", "Pequenos gestos de ternura têm valor imenso diante de Deus quando feitos com amor e coragem onde reina a indiferença.", "Ofereça hoje um gesto delicado de cuidado que devolva dignidade a alguém."),
        ChapletGroupContemplation("7ª Estação · Jesus cai pela segunda vez", "Lm 3, 22–26", "Perseverança na recaída", "As misericórdias do Senhor não se esgotaram.", "O peso cresce, as forças diminuem e Jesus cai novamente no chão pedregoso. Ainda assim, seu amor não recua e Ele se ergue outra vez.", "As recaídas nos mesmos erros cansam e tentam ao desânimo; contemplar Jesus levantando-se renova a confiança de que a graça é mais forte que o hábito.", "Não desista de uma mudança boa por causa de um tropeço repetido: recomece hoje com humildade."),
        ChapletGroupContemplation("8ª Estação · Jesus consola as mulheres de Jerusalém", "Lc 23, 27–31", "Compaixão que converte", "Filhas de Jerusalém, não choreis por mim; chorai por vós mesmas e por vossos filhos.", "Mesmo exausto, Jesus esquece a própria dor para acolher o pranto das mulheres e chamá-las a uma conversão verdadeira que vá além da emoção passageira.", "A compaixão autêntica não termina nas lágrimas: ela se transforma em cuidado responsável pelas futuras gerações e em conversão de vida.", "Transforme uma comoção ou preocupação de hoje em uma ação concreta de bem."),
        ChapletGroupContemplation("9ª Estação · Jesus cai pela terceira vez", "Sl 38, 18; Fl 2, 8", "Confiança no limite", "Humilhou-se a si mesmo, fazendo-se obediente até a morte.", "Já perto do cume do Gólgota, esgotado até o limite, Jesus cai pela terceira vez. O amor chega ao extremo da fragilidade humana sem abandonar a missão.", "Nos momentos em que sentimos não ter mais forças, Cristo conhece exatamente o chão onde estamos e nos dá mão para o último passo.", "Confie hoje a Deus o seu cansaço mais fundo, pedindo forças apenas para o passo de hoje."),
        ChapletGroupContemplation("10ª Estação · Jesus é despojado de suas vestes", "Jo 19, 23–24", "Respeito à dignidade", "Repartiram entre si as minhas vestes.", "No alto do Calvário, os soldados arrancam as vestes de Jesus e lançam sortes sobre sua túnica, expondo-O à humilhação pública.", "Cristo despojado se identifica com todos os que tiveram sua dignidade, privacidade ou direitos violados. Ele nos ensina o desprendimento e o respeito sagrado por cada pessoa.", "Rejeite qualquer fofoca ou exposição que fira a honra de outra pessoa."),
        ChapletGroupContemplation("11ª Estação · Jesus é pregado na cruz", "Lc 23, 33–34", "Perdão que liberta", "Pai, perdoa-lhes, porque não sabem o que fazem.", "Pregado no madeiro entre dois condenados, Jesus não responde com maldições: sua primeira palavra da cruz é uma prece de perdão pelos que o ferem.", "A cruz revela a medida sem medida do amor de Deus. Perdoar, unido a Cristo, rompe a corrente do ódio sem negar a verdade.", "Apresente a Jesus na cruz o nome de alguém que o feriu e peça a graça de não cultivar rancor."),
        ChapletGroupContemplation("12ª Estação · Jesus morre na cruz", "Jo 19, 25–30", "Amor até o fim", "Tudo está consumado! E, inclinando a cabeça, entregou o espírito.", "Depois de confiar Maria ao discípulo amado e entregar seu espírito nas mãos do Pai, Jesus morre na cruz para reconciliar o mundo inteiro com Deus.", "Diante deste mistério, as palavras cedem lugar ao silêncio adorador: ninguém tem maior amor do que dar a vida por seus amigos.", "Faça alguns segundos de silêncio reverente agradecendo a Jesus por sua entrega por você."),
        ChapletGroupContemplation("13ª Estação · Jesus é descido da cruz e entregue a Sua Mãe", "Jo 19, 38–40", "Acolher a dor com fé", "Tomaram o corpo de Jesus e envolveram-no em panos de linho.", "José de Arimateia e Nicodemos vencem o medo para cuidar do corpo de Jesus com reverência, colocando-O nos braços silenciosos de Maria.", "Na dor do adeus, a ternura e o respeito permanecem sinais de fé. Maria acolhe a dor inteira sem perder a confiança em Deus.", "Reze pelas famílias que hoje velam ou recordam alguém querido com saudade."),
        ChapletGroupContemplation("14ª Estação · Jesus é colocado no sepulcro", "Jo 19, 41–42; Jo 12, 24", "Esperança na Ressurreição", "Se o grão de trigo que cai na terra não morrer, fica só; mas se morrer, produz muito fruto.", "Num jardim próximo, o corpo de Jesus repousa num sepulcro novo. A pedra parece encerrar a história, mas no silêncio da terra já germina a aurora do terceiro dia.", "A Via-Sacra nunca termina no desespero: guardamos a vigília da esperança, certos de que em Cristo a morte não tem a última palavra.", "Entregue hoje a Deus aquilo que parece encerrado e confie na promessa da vida nova."),
    )

    fun forStep(script: PrayerScript, set: MysterySet, decadeIndex: Int?): MysteryContemplation? {
        if (decadeIndex == null) return null
        return when (script) {
            PrayerScript.TRADITIONAL, PrayerScript.CATECHETICAL, PrayerScript.COMPLETE,
            PrayerScript.FAMILY, PrayerScript.SICK, PrayerScript.PEACE,
            PrayerScript.DECEASED, PrayerScript.ANXIETY, PrayerScript.VOCATIONS,
            PrayerScript.SINGLE_DECADE -> MysteryContent.contemplationFor(set, decadeIndex)
            PrayerScript.FULL_ROSARY -> {
                val actualSet = MysterySet.entries.getOrNull(decadeIndex / 5) ?: return null
                MysteryContent.contemplationFor(actualSet, decadeIndex % 5)
            }
            PrayerScript.DIVINE_MERCY -> divineMercy.getOrNull(decadeIndex)?.toContemplation()
            PrayerScript.SAINT_JOSEPH -> saintJoseph.getOrNull(decadeIndex)?.toContemplation()
            PrayerScript.TEARS -> sevenSorrows.getOrNull(decadeIndex)?.toContemplation()
            PrayerScript.SEVEN_SORROWS -> sevenSorrows.getOrNull(decadeIndex)?.toContemplation()
            PrayerScript.FRANCISCAN -> when (decadeIndex) {
                0 -> MysteryContent.contemplationFor(MysterySet.JOYFUL, 0)
                1 -> MysteryContent.contemplationFor(MysterySet.JOYFUL, 1)
                2 -> MysteryContent.contemplationFor(MysterySet.JOYFUL, 2)
                3 -> franciscanExtra.getValue(3).toContemplation()
                4 -> MysteryContent.contemplationFor(MysterySet.JOYFUL, 4)
                5 -> MysteryContent.contemplationFor(MysterySet.GLORIOUS, 0)
                6 -> franciscanExtra.getValue(6).toContemplation()
                else -> null
            }
            PrayerScript.HOLY_WOUNDS -> holyWounds.getOrNull(decadeIndex)?.toContemplation()
            PrayerScript.SAINT_MICHAEL -> saintMichaelChoirs.getOrNull(decadeIndex)?.toContemplation()
            PrayerScript.VIA_SACRA -> viaSacraStations.getOrNull(decadeIndex)?.toContemplation()
        }
    }

    /** Mystery metadata for chaplet steps whose PrayerStep.mystery is null (Divine Mercy, Tears). */
    fun mysteryFor(script: PrayerScript, decadeIndex: Int?): Mystery? {
        if (decadeIndex == null) return null
        return when (script) {
            PrayerScript.DIVINE_MERCY -> divineMercy.getOrNull(decadeIndex)?.toMystery()
            PrayerScript.TEARS -> sevenSorrows.getOrNull(decadeIndex)?.toMystery()
            else -> null
        }
    }
}

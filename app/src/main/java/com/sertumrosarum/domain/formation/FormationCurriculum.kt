package com.sertumrosarum.domain.formation

data class FormationSection(val title: String, val body: String, val reference: String)
data class FormationScripture(val reference: String, val context: String, val reading: String, val notice: String)
data class FormationExample(val situation: String, val response: String)
data class FormationRecall(val question: String, val answer: String)
data class FormationTerm(val name: String, val meaning: String)
data class FormationStudy(
    val sections: List<FormationSection>, val scripture: FormationScripture,
    val example: FormationExample, val takeaways: List<String>,
    val terms: List<FormationTerm>, val recall: List<FormationRecall>,
) {
    val searchableText: String get() = buildString {
        sections.forEach { append(" ${it.title} ${it.body} ${it.reference}") }
        append(" ${scripture.reference} ${scripture.context} ${scripture.reading} ${scripture.notice}")
        append(" ${example.situation} ${example.response} ${takeaways.joinToString(" ")}")
        terms.forEach { append(" ${it.name} ${it.meaning}") }
        recall.forEach { append(" ${it.question} ${it.answer}") }
    }
}

/** Original Portuguese teaching, not quotations. Each section names its doctrinal support. */
object FormationCurriculum {
    internal fun s(title: String, reference: String, body: String) = FormationSection(title, body.trimIndent(), reference)
    internal fun bible(reference: String, context: String, reading: String, notice: String) = FormationScripture(reference, context, reading, notice)
    internal fun example(situation: String, response: String) = FormationExample(situation, response)
    internal fun term(name: String, meaning: String) = FormationTerm(name, meaning)
    internal fun recall(question: String, answer: String) = FormationRecall(question, answer)

    val studies: Map<String, FormationStudy> = mapOf(
        "god-loves" to FormationStudy(
            sections = listOf(
                s("1 · Um desejo e uma iniciativa", "CIC 27–35; 50–53", """
                    Desejamos conhecer a verdade, amar e ser amados. Essas buscas podem abrir uma pergunta sobre Deus, mas não nos permitem controlar Seu mistério. A criação e a consciência oferecem caminhos para reconhecer o Criador. A revelação vai além: Deus toma a iniciativa de compartilhar Sua vida e de nos convidar à amizade com Ele.

                    Isso muda o ponto de partida da catequese. Antes de aprender regras, recebemos uma boa notícia: nossa existência é querida por Deus. Uma experiência de abandono, um fracasso ou a ausência de sentimentos religiosos não provam que Ele deixou de amar. A fé aprende a apoiar-se em Sua fidelidade, sem desvalorizar as perguntas que a vida desperta.
                """),
                s("2 · Uma história que chega a Jesus", "CIC 54–67; 101–104", """
                    Deus educa Seu povo ao longo da história: a aliança com Noé, a vocação de Abraão, a libertação do Egito, a Lei e a palavra dos profetas preparam a vinda de Cristo. Essa história não é uma coleção de heróis perfeitos. Mostra um Deus fiel que chama pessoas reais, corrige a injustiça e sustenta a esperança.

                    Jesus é a plenitude da revelação, o Filho que nos dá a conhecer o Pai. Não esperamos uma nova revelação pública que substitua Seu Evangelho. Aparições e experiências particulares, mesmo reconhecidas pela Igreja, não completam nem corrigem Cristo; podem ajudar a viver a fé recebida. O centro do aprendizado permanece a pessoa de Jesus.
                """),
                s("3 · Bíblia, Tradição e a escuta da Igreja", "CIC 74–100; 109–119", """
                    A Palavra de Deus é transmitida na Sagrada Escritura e na Tradição apostólica, que pertencem ao mesmo depósito da fé. Tradição, aqui, não significa qualquer costume antigo. É a transmissão viva do Evangelho confiado aos apóstolos. O Magistério, serviço de ensino dos bispos em comunhão com o papa, está a serviço dessa Palavra, não acima dela.

                    Para ler a Bíblia, considere o gênero literário, o contexto e a unidade de toda a Escritura. Um salmo expressa oração poética; uma parábola ensina por uma história; um Evangelho testemunha Jesus. Retirar uma frase de seu contexto pode produzir uma conclusão contrária ao conjunto. A leitura pessoal cresce com a liturgia, o estudo e a comunidade.
                """),
                s("4 · Crer com inteligência e liberdade", "CIC 142–165", """
                    A fé tem duas dimensões inseparáveis: confiar em Deus e acolher como verdadeiro o que Ele revela. Não é somente sentir-se bem, nem apenas decorar afirmações. A graça desperta a resposta; a pessoa responde livremente, com a inteligência e a vontade. Ninguém pode ser forçado a crer de modo autêntico.

                    Uma dúvida pode pedir estudo, uma conversa honesta ou tempo de oração. Diferencie não entender uma doutrina de rejeitá-la deliberadamente. Dizer «ainda não compreendo» pode iniciar uma aprendizagem séria. Um primeiro passo possível é ler um trecho do Evangelho, identificar o que Jesus faz e dirigir-Lhe uma pergunta concreta. A confiança amadurece quando a verdade ilumina a vida.
                """),
            ),
            scripture = bible("Lucas 24, 13–35", "Dois discípulos deixam Jerusalém desanimados depois da morte de Jesus. O Ressuscitado aproxima-se e caminha com eles.", "Jesus escuta sua tristeza e explica as Escrituras à luz do que aconteceu com Ele. Na mesa, os discípulos O reconhecem ao partir o pão e voltam à comunidade. Este é um resumo da passagem; leia o texto completo na sua Bíblia.", "Observe a ordem: aproximação, escuta, Palavra, reconhecimento e retorno. Como essa sequência ajuda alguém que tem perguntas sobre a fé? A compreensão cresce dentro de um encontro, não pela humilhação de quem duvida."),
            example = example("Ana pensa que precisa resolver todas as dúvidas antes de ir à paróquia. Ela tem medo de fazer perguntas básicas.", "Ela pode participar da Missa, procurar um catequista e começar pelo Evangelho de Marcos. Participar não exige fingir uma certeza que ainda não tem. Ao mesmo tempo, aprender pede disposição para ouvir respostas e rever ideias: a pergunta honesta abre um caminho de confiança e estudo."),
            takeaways = listOf("Deus toma a iniciativa; a fé é resposta livre sustentada pela graça.", "Jesus é a plenitude da revelação e o centro da leitura cristã da Bíblia.", "Escritura, Tradição e Magistério se relacionam no serviço à mesma Palavra."),
            terms = listOf(term("Revelação", "Deus se dá a conhecer e comunica Seu desígnio de salvação."), term("Aliança", "Relação de comunhão que Deus estabelece com Seu povo."), term("Magistério", "Serviço de interpretação autêntica da Palavra confiado ao papa e aos bispos em comunhão com ele.")),
            recall = listOf(recall("Por que Tradição não é sinônimo de qualquer costume antigo?", "A Tradição apostólica transmite o Evangelho recebido dos apóstolos; costumes locais podem mudar e precisam ser avaliados à luz dessa fé."), recall("Uma dúvida sincera significa que a pessoa já perdeu a fé?", "Não necessariamente. Pode revelar algo que precisa de compreensão e acompanhamento. Fé, razão, oração e busca da verdade podem caminhar juntas.")),
        ),
        "trinity" to FormationStudy(
            sections = listOf(
                s("1 · Jesus nos apresenta o Pai e o Espírito", "CIC 238–248", """
                    A Trindade não foi inventada para resolver um quebra-cabeça. A Igreja recebeu de Jesus uma relação concreta com o Pai e a promessa do Espírito Santo. Jesus reza ao Pai, revela Seu amor e envia os discípulos a batizar em um único nome: do Pai, do Filho e do Espírito Santo. A linguagem da fé procura guardar fielmente esse testemunho.

                    No Batismo de Jesus, o Filho está no Jordão, o Espírito desce e a voz do Pai se faz ouvir. A cena ajuda a perceber uma distinção real, sem apresentar três deuses. O Novo Testamento anuncia ao mesmo tempo a unidade de Deus e a divindade do Filho e do Espírito. A doutrina trinitária mantém juntas essas afirmações.
                """),
                s("2 · Uma natureza, três Pessoas", "CIC 251–255", """
                    A palavra natureza responde aqui a «o que é?»: há uma só natureza divina, inteira e indivisível. A palavra Pessoa procura dizer «quem?»: o Pai, o Filho e o Espírito Santo são realmente distintos em Suas relações. Cada Pessoa é plenamente Deus, não um terço de Deus. Nenhuma é mais antiga, menos poderosa ou menos divina.

                    O Pai não é o Filho; o Filho não é o Espírito; o Espírito não é o Pai. Mas não existem três vontades divinas rivais, três criadores independentes ou três centros de poder concorrentes. A unidade divina não apaga as relações pessoais, e as relações pessoais não repartem Deus. Essas palavras delimitam o erro sem esgotar o mistério.
                """),
                s("3 · O que as comparações não conseguem dizer", "CIC 42–43; 256", """
                    Água, gelo e vapor podem sugerir que uma única pessoa apenas muda de aparência: isso não expressa a Trindade. Três folhas de um trevo podem sugerir que cada Pessoa é somente uma parte de Deus: também não. Três amigos unidos podem sugerir três seres divinos separados. Nenhuma dessas imagens deve funcionar como definição da fé.

                    Um diagrama com nomes e relações tem uma função mais modesta: recordar o que afirmamos e o que distinguimos. Mesmo uma pintura de três figuras é linguagem simbólica, não um retrato da essência divina. O cuidado com as imagens não impede a contemplação; ensina a reconhecer o limite de toda representação de Deus.
                """),
                s("4 · A Trindade na oração e na vida", "CIC 257–267; 683–686", """
                    Toda a obra da salvação é obra do único Deus. Reconhecemos as missões do Filho e do Espírito sem separar Sua ação da do Pai. Na liturgia, rezamos ao Pai, por Cristo, no Espírito Santo. Também podemos dirigir-nos a cada Pessoa divina: pedir a Jesus misericórdia ou ao Espírito luz é oração ao único Deus.

                    O sinal da cruz reúne a profissão trinitária e a memória da entrega de Jesus. Faça-o com atenção às palavras, sem tratá-lo como amuleto. O Batismo introduz na comunhão com a Trindade; a vida cristã desenvolve esse dom na caridade. Não copiamos a vida divina como uma técnica, mas aprendemos a receber e a compartilhar o amor que nos alcança.
                """),
            ),
            scripture = bible("Mateus 3, 13–17; 28, 16–20", "O Evangelho aproxima o início da missão pública de Jesus e o envio dos discípulos pelo Ressuscitado.", "No Jordão, Jesus recebe o Batismo, o Espírito desce e o Pai manifesta Seu agrado. Ao enviar os discípulos, Jesus manda batizar no nome do Pai, do Filho e do Espírito Santo. Resumo para acompanhar a leitura bíblica.", "Quais Pessoas são nomeadas? Por que a fórmula batismal fala em um só nome? As duas passagens sustentam distinção pessoal e unidade, sem explicar Deus por uma transformação de matéria."),
            example = example("Uma criança pergunta se Pai, Filho e Espírito são três pedaços que, reunidos, formam Deus.", "Comece pelo que a Igreja afirma: cada Pessoa é plenamente Deus; Deus é um só. Use os três nomes para distinguir quem é quem, mas não divida um círculo em fatias. É melhor reconhecer o limite da comparação do que oferecer uma resposta fácil que ensine outra coisa."),
            takeaways = listOf("Há um só Deus, não uma associação de três deuses.", "Pai, Filho e Espírito Santo são Pessoas distintas, cada uma plenamente Deus.", "A oração cristã participa da comunhão trinitária recebida no Batismo."),
            terms = listOf(term("Natureza divina", "O único ser divino, que não se divide entre as Pessoas."), term("Pessoa", "Nome usado para o Pai, o Filho e o Espírito Santo em Sua distinção real."), term("Consubstancial", "Da mesma e única substância; o Filho não é uma criatura inferior ao Pai.")),
            recall = listOf(recall("Por que a comparação com água, gelo e vapor pode confundir?", "Ela pode fazer pensar em uma só pessoa que alterna três modos de aparecer, quando a fé confessa três Pessoas realmente distintas."), recall("Quando invocamos Jesus, estamos rezando a outro Deus?", "Não. O Filho é plenamente Deus na única natureza divina, inseparável do Pai e do Espírito Santo.")),
        ),
        "jesus" to FormationStudy(
            sections = listOf(
                s("1 · O Filho eterno entra em nossa história", "CIC 456–469", """
                    Encarnação significa que o Filho eterno de Deus assumiu uma humanidade verdadeira. Ele não começou a existir em Belém: nasceu de Maria segundo a humanidade, permanecendo o Filho eterno. Maria é chamada Mãe de Deus porque Aquele que ela concebeu é uma Pessoa divina, não porque tenha criado a divindade ou existido antes de Deus.

                    Jesus aprende, trabalha, sente fome, alegra-se e sofre. Sua humanidade não é uma roupa usada por fora nem uma encenação. Ele possui corpo e alma humanos, inteligência e vontade humanas. Por isso Sua proximidade alcança a vida concreta: a infância, a amizade, o cansaço e a experiência da dor podem ser apresentados a Ele na oração.
                """),
                s("2 · Uma Pessoa, duas naturezas", "CIC 464–483", """
                    Ao perguntar quem é Jesus, respondemos: o Filho de Deus. Ao perguntar como Ele existe e age, confessamos Sua natureza divina e Sua natureza humana. As duas estão unidas na única Pessoa do Filho, sem mistura, mudança, divisão ou separação. Jesus não é metade Deus e metade homem; é plenamente Deus e plenamente homem.

                    Essa união não apaga Sua liberdade humana. No Getsêmani, Jesus enfrenta uma angústia real e entrega Sua vontade humana ao Pai. Não há pecado nEle, mas há sofrimento verdadeiro. O nome «Cristo» significa Ungido: reconhecemos em Jesus o Messias esperado, que realiza Sua missão pelo serviço e pela entrega, não pela imposição de poder político.
                """),
                s("3 · O Reino anunciado com palavras e gestos", "CIC 541–560", """
                    Jesus anuncia a proximidade do Reino de Deus e convida à conversão. Cura enfermos, acolhe pecadores, perdoa e chama discípulos. Seus milagres são sinais do Reino e de Sua identidade, não espetáculos para satisfazer curiosidade. Nem todos os doentes de Seu tempo foram curados; a fé não promete que toda doença desaparecerá agora.

                    As parábolas revelam a misericórdia e também pedem uma resposta. O bom samaritano mostra como tornar-se próximo; o filho pródigo revela o acolhimento do Pai. Leia cada gesto em relação à pessoa e à missão de Jesus. Uma catequese centrada nEle une anúncio, seguimento e cuidado real com quem sofre.
                """),
                s("4 · Da cruz à ressurreição e à esperança", "CIC 599–618; 638–658; 988–1004", """
                    A morte de Jesus não foi um acidente sem sentido nem uma derrota definitiva. Ele Se entrega livremente por amor para nos reconciliar com Deus. Não atribuamos coletivamente Sua morte a um povo: a Igreja rejeita a culpabilização de todos os judeus. A cruz revela a gravidade do pecado e a força de um amor que alcança os pecadores.

                    A ressurreição é a vitória real de Cristo sobre a morte, não apenas a sobrevivência de uma lembrança nos discípulos. O Ressuscitado é o mesmo Jesus crucificado, agora em vida glorificada. Sua vitória funda a esperança da ressurreição dos mortos. Segui-Lo inclui tomar a própria cruz, mas nunca exige procurar sofrimento ou deixar de combater uma injustiça evitável.
                """),
            ),
            scripture = bible("João 1, 1–18; 20, 24–29", "O início do Evangelho apresenta a identidade do Verbo; o encontro com Tomé mostra o Ressuscitado reconhecido pelos discípulos.", "O Verbo que estava junto de Deus assume nossa carne. Mais adiante, Jesus ressuscitado apresenta a Tomé as marcas de Sua paixão, e Tomé O reconhece como seu Senhor e seu Deus. Resumo editorial; confira os detalhes na Bíblia.", "Que palavras sustentam a divindade de Jesus? Que elementos mostram Sua humanidade real? As marcas do Ressuscitado unem a entrega da cruz e a vida nova, em vez de apagar Sua história."),
            example = example("Paulo diz: «Se Jesus é Deus, então Ele só fingiu sentir medo e dor; não pode compreender meu sofrimento».", "A divindade não torna a humanidade de Jesus uma aparência. Sua angústia no Getsêmani e Sua morte são reais. Paulo pode buscar em Cristo companhia e esperança, e também procurar tratamento e apoio humano. A fé na ressurreição não exige negar o sofrimento presente."),
            takeaways = listOf("Jesus é uma Pessoa divina em duas naturezas completas, divina e humana.", "Sua vida e Seus sinais anunciam o Reino e convidam ao seguimento.", "Morte e ressurreição formam o centro do mistério pascal e da nossa esperança."),
            terms = listOf(term("Encarnação", "O Filho eterno assume verdadeiramente a natureza humana."), term("Mistério pascal", "Paixão, morte, ressurreição e glorificação de Jesus, centro de nossa salvação."), term("Ressurreição", "Vitória de Cristo sobre a morte em vida glorificada, fundamento de nossa futura ressurreição.")),
            recall = listOf(recall("Por que chamar Maria de Mãe de Deus não significa que ela criou Deus?", "O título se refere a quem ela gerou segundo a humanidade: a Pessoa do Filho eterno. Ela não é a origem da natureza divina."), recall("A ressurreição é apenas uma lembrança viva de Jesus?", "Não. A Igreja anuncia que Jesus realmente venceu a morte; os discípulos encontram o próprio Senhor vivo e glorificado.")),
        ),
        "church-mary" to FormationStudy(
            sections = listOf(
                s("1 · Um povo reunido por Cristo", "CIC 751–776; 781–801", """
                    A Igreja nasce da iniciativa de Deus e da missão de Jesus, manifestando-se publicamente em Pentecostes pelo dom do Espírito. É povo de Deus, Corpo de Cristo e templo do Espírito Santo. Essas imagens iluminam uma mesma realidade: a salvação nos reúne numa comunhão, e não numa coleção de experiências religiosas isoladas.

                    A Igreja é visível e espiritual. Tem comunidades, ministros e sacramentos, mas sua vida vem de Cristo. É santa por sua origem e por Seus dons, embora seus membros precisem de purificação e conversão. Reconhecer pecados e crimes cometidos por cristãos não é trair a Igreja. A verdade, a justiça e a proteção das vítimas pertencem à responsabilidade de seus membros.
                """),
                s("2 · Dons diferentes, uma missão comum", "CIC 811–870; 871–913", """
                    Professamos a Igreja una, santa, católica e apostólica. Ela é una pela comunhão em Cristo; santa pelos dons de Deus; católica porque recebe e anuncia a plenitude da fé a todos os povos; apostólica porque permanece fundada no testemunho e na missão dos apóstolos. Essas notas são dons e tarefas que a comunidade deve tornar visíveis.

                    Bispos, presbíteros e diáconos exercem ministérios ordenados distintos; leigos e pessoas consagradas também participam da missão. O papa serve à unidade em comunhão com os bispos. Nem toda opinião pessoal de um ministro é uma definição da fé. O discernimento dos dons procura o bem de todos, sem transformar autoridade em domínio ou participação em competição.
                """),
                s("3 · A comunhão dos santos", "CIC 946–962", """
                    «Santos» pode nomear os que pertencem a Cristo e também aqueles cuja vida a Igreja propõe como exemplo. A comunhão dos santos inclui a partilha dos bens espirituais e a união dos fiéis que peregrinam na terra, dos que se purificam e dos que vivem na glória de Deus. A morte não rompe a comunhão em Cristo.

                    Pedimos orações uns aos outros e aos santos, e rezamos pelos falecidos. Isso não é procurar mensagens dos mortos nem atribuir a uma criatura poder autônomo. Toda graça vem de Deus. Uma devoção autêntica conduz à vida sacramental, à conversão e à caridade; não transforma santos em fornecedores especializados de favores que possamos controlar.
                """),
                s("4 · Maria, discípula e mãe", "CIC 484–511; 963–975; 2096–2097", """
                    Maria acolhe a Palavra e coopera livremente, pela fé, com a obra do Filho. A Imaculada Conceição significa que foi preservada do pecado original desde sua concepção, pela graça de Cristo; não é o mesmo que a concepção virginal de Jesus. A Assunção anuncia que ela foi elevada em corpo e alma à glória celeste por dom de Deus.

                    A honra singular dada a Maria difere da adoração, devida somente à Trindade. Sua maternidade espiritual e sua intercessão dependem inteiramente da única mediação de Cristo. Nas bodas de Caná, ela dirige os serventes a Jesus. Esse é um critério para nossas práticas marianas: ajudam a escutar o Filho, a servi-Lo nos irmãos e a participar de Sua Igreja?
                """),
            ),
            scripture = bible("1 Coríntios 12, 12–27; João 2, 1–11", "Paulo ensina uma comunidade dividida a reconhecer a unidade do corpo. Em Caná, Maria percebe uma necessidade e aponta para Jesus.", "No corpo, a diversidade dos membros não destrói a unidade; cada um precisa dos outros. Na festa de casamento, a atenção de Maria conduz os serventes a fazer o que Jesus disser. Resumo das passagens, sem transcrição literal.", "Quem parece dispensável na sua comunidade? Que atitude de Maria pode ser imitada por um discípulo? Nem a imagem do corpo nem a intercessão substituem a centralidade de Cristo."),
            example = example("Uma amiga diz que pedir uma oração a Maria coloca outra salvadora entre nós e Jesus.", "Explique que a Igreja não atribui a Maria uma salvação independente. Como pedimos a um amigo que reze conosco, pedimos sua intercessão na comunhão de Cristo. A comparação ajuda a compreender a oração, mas Maria recebe uma honra singular como Mãe do Senhor. A adoração continua pertencendo somente a Deus."),
            takeaways = listOf("A Igreja é povo de Deus, Corpo de Cristo e templo do Espírito.", "A comunhão dos santos une os fiéis em Cristo, também para além da morte.", "Maria recebe veneração singular; somente Deus recebe adoração."),
            terms = listOf(term("Católica", "Universal: enviada a todos e portadora da plenitude dos meios de salvação recebidos de Cristo."), term("Intercessão", "Oração feita por outra pessoa, sempre dependente da ação de Deus."), term("Assunção", "Maria elevada por Deus, em corpo e alma, à glória celeste.")),
            recall = listOf(recall("Como a Igreja pode ser santa e ter membros pecadores?", "Sua santidade vem de Cristo, do Espírito e dos dons que recebe. Seus membros continuam precisando de conversão, justiça e purificação."), recall("Imaculada Conceição e concepção virginal de Jesus significam a mesma coisa?", "Não. A primeira se refere à preservação de Maria do pecado original; a segunda, a Jesus concebido em Maria por obra do Espírito Santo.")),
        ),
    ) + FormationCelebration.studies + FormationLife.studies + FormationPrayer.studies
}

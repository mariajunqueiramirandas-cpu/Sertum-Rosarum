package com.sertumrosarum.domain.formation

import com.sertumrosarum.domain.formation.FormationCurriculum.bible
import com.sertumrosarum.domain.formation.FormationCurriculum.example
import com.sertumrosarum.domain.formation.FormationCurriculum.recall
import com.sertumrosarum.domain.formation.FormationCurriculum.s
import com.sertumrosarum.domain.formation.FormationCurriculum.term

internal object FormationPrayer {
    val studies = mapOf(
        "prayer" to FormationStudy(
            sections = listOf(
                s("1 · Deus nos chama antes de nossa resposta", "CIC 2558–2565; 2598–2616", """
                    A oração cristã nasce de uma relação: Deus procura a pessoa e torna possível que ela responda. Jesus reza em lugares retirados, antes de decisões, na alegria e na angústia. Sua oração não é fuga da missão; alimenta a entrega ao Pai. Aprender a rezar é aproximar-se dessa relação filial, com o auxílio do Espírito Santo.

                    Podemos começar de modo simples: escolher um momento, reconhecer a presença de Deus e dizer uma verdade da vida. Não é necessário produzir uma linguagem solene. Uma oração tradicional permite receber palavras da Igreja; uma fala espontânea apresenta a situação de hoje. O silêncio pode ser escuta amorosa, mesmo quando não surge uma ideia nova ou uma emoção agradável.
                """),
                s("2 · Muitas formas de uma mesma relação", "CIC 2626–2649; 2700–2724", """
                    Bendizer e adorar reconhecem Deus como fonte da vida. Pedir apresenta necessidades; interceder leva a Ele outras pessoas; agradecer reconhece dons; louvar volta-se para quem Deus é. Essas formas se entrelaçam. Uma oração que começa em sofrimento pode incluir gratidão; uma ação de graças pode despertar o desejo de ajudar alguém.

                    A oração vocal usa palavras; a meditação procura compreender e responder ao mistério de Cristo; a contemplação é atenção amorosa a Deus, dom acolhido na fé. Não são níveis de prestígio. A pessoa pode rezar um salmo, meditar uma passagem e permanecer em silêncio no mesmo encontro. O corpo participa por uma postura possível, pela respiração tranquila e pelos gestos, sem técnicas de controle sobre Deus.
                """),
                s("3 · O Pai-Nosso educa o desejo", "CIC 2759–2865", """
                    Jesus nos ensina a dizer «Pai nosso»: rezamos como filhos e com os irmãos, não como indivíduos que excluem o mundo. Nas três primeiras petições, pedimos que Seu nome seja santificado, Seu Reino venha e Sua vontade se realize. Nas quatro seguintes, apresentamos o pão cotidiano, o perdão, a ajuda na tentação e a libertação do mal.

                    Pedir que a vontade de Deus se faça não significa atribuir-Lhe todo ato de violência ou desistir de agir. Pedimos força para viver o amor que Jesus revelou. O pão inclui nossas necessidades reais e nos lembra de quem passa fome. O perdão recebido abre à misericórdia. Rezar devagar uma petição pode mostrar onde nossa vida precisa mudar e qual necessidade devemos apresentar com confiança.
                """),
                s("4 · Distração, aridez e perseverança", "CIC 2725–2745; 2734–2741", """
                    Distrações podem ser acolhidas como sinais do que ocupa o coração e depois deixadas de lado com mansidão. Aridez é a ausência de consolação sensível; não prova que a oração fracassou. A fidelidade não exige sessões cada vez mais longas. Um tempo pequeno e possível pode ser mais fecundo que uma promessa que nos esgota e gera culpa.

                    Também podemos perguntar por que um pedido não foi atendido como esperávamos. A confiança cristã não promete controle dos acontecimentos nem mede a fé pelo resultado de uma cura. Apresente a dor, procure ajuda e continue a agir pelo bem possível. O Espírito intercede em nossa fraqueza. Se a oração estiver ligada a compulsões ou medo constante, procure acompanhamento pastoral e profissional adequado.
                """),
            ),
            scripture = bible("Mateus 6, 5–13; Romanos 8, 26–27", "No Sermão da Montanha, Jesus ensina uma oração sem exibição. Paulo fala do Espírito que socorre nossa fraqueza.", "Jesus orienta a rezar ao Pai sem ostentação e sem tratar o acúmulo de palavras como garantia. Ensina o Pai-Nosso. Paulo recorda que o Espírito vem em auxílio quando não sabemos como orar. Resumo para acompanhar a Bíblia.", "Jesus condena toda repetição ou a pretensão de obrigar Deus pelo número de palavras? Que petição do Pai-Nosso você pode rezar hoje com atenção? A confiança não depende de encontrar uma frase perfeita."),
            example = example("Marina tenta rezar por vinte minutos, se distrai e conclui que Deus só escuta quem consegue esvaziar completamente a mente.", "Ela pode começar com cinco minutos, ler uma passagem curta e retomar a atenção quando perceber a distração. A oração é relação com Deus, não uma prova de concentração absoluta. Uma frase como «Pai, aqui estou» pode ajudá-la a voltar. Aos poucos, ela aprende a reconhecer o que a distrai sem se punir por cada pensamento."),
            takeaways = listOf("Rezar é responder ao Deus que nos chama, por Cristo e no Espírito.", "Louvor, pedido, intercessão e gratidão podem usar palavras, meditação e silêncio.", "O Pai-Nosso forma o desejo; a perseverança não se mede por emoção ou concentração perfeita."),
            terms = listOf(term("Intercessão", "Oração que apresenta a Deus as necessidades de outras pessoas."), term("Meditação", "Busca orante que envolve pensamento, imaginação, afetos e desejo para responder à Palavra."), term("Aridez", "Experiência de oração sem consolação sensível, que pede discernimento e perseverança.")),
            recall = listOf(recall("O silêncio sem sentimentos intensos pode ser oração?", "Sim. A atenção amorosa e a confiança podem permanecer sem uma emoção forte. O valor da oração não depende de sensações."), recall("Por que dizer «Pai nosso» muda a maneira de pedir?", "Rezamos como filhos em comunhão com os irmãos. Nossas necessidades não devem nos tornar indiferentes ao pão, ao perdão e ao bem dos outros.")),
        ),
        "rosary" to FormationStudy(
            sections = listOf(
                s("1 · Uma oração que guarda o Evangelho", "CIC 971; 2678; 2708", """
                    O rosário é uma oração de contemplação dos mistérios de Cristo com Maria. Na Ave-Maria, a saudação bíblica e a bênção se concentram no fruto de seu ventre, Jesus; depois pedimos sua intercessão. A repetição oferece um ritmo para permanecer na cena evangélica, como uma frase musical que ajuda a atenção a voltar ao essencial.

                    Essa oração é uma devoção, não um sacramento, e não substitui a Missa nem a escuta da Palavra. Seu fruto se reconhece no seguimento de Jesus, na humildade e na caridade. Rezar muitas contas não compra uma resposta de Deus. Uma dezena rezada com o desejo de escutar pode ser um começo fecundo para quem ainda não consegue percorrer cinco mistérios.
                """),
                s("2 · Quatro conjuntos para contemplar a vida de Cristo", "CIC 512–560; 599–655; 966; Rosarium Virginis Mariae 19–23", """
                    Os gozosos contemplam Anunciação, Visitação, Nascimento de Jesus, Apresentação e encontro no Templo. Os luminosos contemplam Batismo no Jordão, bodas de Caná, anúncio do Reino, Transfiguração e instituição da Eucaristia. Os dolorosos percorrem agonia, flagelação, coroação de espinhos, caminho da cruz e crucifixão. Os gloriosos contemplam Ressurreição, Ascensão, Pentecostes, Assunção e coroação de Maria.

                    Cada conjunto oferece uma entrada no Evangelho e na esperança da Igreja. A Assunção e a coroação de Maria exprimem a fé e a tradição da Igreja, sem um relato bíblico direto igual ao da Natividade. A distribuição por dias ajuda a organizar a oração, mas não impede escolher um mistério adequado a uma ocasião, conforme o costume e a orientação da comunidade.
                """),
                s("3 · Como atravessar uma dezena", "CIC 2705–2708; Rosarium Virginis Mariae 29–35", """
                    Anuncie o mistério, leia uma frase bíblica e deixe um breve silêncio. Reze o Pai-Nosso, dez Ave-Marias e o Glória; a oração de Fátima pode ser acrescentada conforme o costume. O nome do mistério e uma imagem ajudam a recordar a cena. Não é preciso imaginar cada detalhe ou manter uma visualização contínua para que a oração seja verdadeira.

                    Na Anunciação, você pode prestar atenção à escuta de Maria e pedir disponibilidade à Palavra. Na agonia, pode permanecer junto de Jesus e apresentar alguém que sofre. Quando a atenção se perde, volte ao nome de Jesus ou à intenção do mistério. As contas servem para acompanhar a oração, liberando a mente da necessidade de vigiar a contagem.
                """),
                s("4 · Da contemplação ao fruto", "CIC 2673–2679; 2682; Rosarium Virginis Mariae 40–43", """
                    Um fruto é uma disposição para levar à vida: humildade, caridade, paciência, esperança ou paz. Não funciona como prêmio automático por terminar uma sequência. Depois de contemplar a Visitação, por exemplo, pergunte quem precisa de uma visita ou de uma ajuda. A oração forma o olhar e pede uma resposta livre, sem transformar cada mistério numa tarefa pesada.

                    Ao rezar em grupo, respeite o ritmo comum e ofereça espaço para a escuta. Com crianças ou pessoas enfermas, uma forma breve pode ser mais adequada. Se o terço foi interrompido, retome quando possível sem pensar que as orações anteriores perderam valor. A devoção mariana autêntica aproxima de Cristo e de Seus irmãos; não cria uma competição entre quem reza mais.
                """),
            ),
            scripture = bible("Lucas 1, 26–38; 2, 19", "A Anunciação apresenta o chamado de Deus e a resposta de Maria. Lucas também a descreve guardando e meditando os acontecimentos no coração.", "Maria escuta, pergunta e acolhe a missão que lhe é anunciada. Depois, guarda o que acontece com Jesus e procura seu sentido diante de Deus. Resumo editorial; leia a passagem para acompanhar a resposta inteira.", "Como a pergunta de Maria e sua disponibilidade aparecem juntas? Escolha um detalhe da cena para acompanhar uma dezena. Contemplar não é apenas contar o episódio: é deixar a Palavra formar uma resposta na própria vida."),
            example = example("André começa o terço e é interrompido para cuidar do filho. Pensa que precisa recomeçar tudo para a oração ter valor.", "O cuidado necessário também pertence à vocação ao amor. Ele pode retomar de onde parou ou rezar outra vez em outro momento. O rosário não é uma fórmula anulada por interrupções. Escolher um ritmo possível ajuda a perseverar e permite que a contemplação se expresse no serviço concreto à família."),
            takeaways = listOf("O centro do rosário é Cristo, contemplado com Maria.", "Mistério, Palavra, silêncio e repetição ajudam a atenção amorosa.", "A contemplação pede frutos na vida; não substitui sacramentos nem compra graças."),
            terms = listOf(term("Mistério", "Acontecimento da vida de Cristo e da obra de Deus contemplado na fé; não apenas um enigma."), term("Dezena", "Unidade de oração com Pai-Nosso, dez Ave-Marias e Glória, acompanhando um mistério."), term("Fruto espiritual", "Disposição para acolher e viver a graça contemplada, como caridade, paciência ou esperança.")),
            recall = listOf(recall("A repetição das Ave-Marias é o objetivo final do rosário?", "Não. Ela oferece um ritmo para contemplar Cristo, escutar a Palavra e responder com fé e amor."), recall("Toda cena dos vinte mistérios tem um relato bíblico direto?", "Não. A Assunção e a coroação de Maria exprimem a fé e a tradição da Igreja; não têm uma narração bíblica direta como a Anunciação.")),
        ),
        "year" to FormationStudy(
            sections = listOf(
                s("1 · O domingo e o centro pascal", "CIC 1163–1173; 2174–2179", """
                    O ano litúrgico organiza a celebração do mistério de Cristo. Não é um retorno a acontecimentos distantes como se Jesus precisasse nascer ou ressuscitar de novo. Na liturgia, participamos da obra de salvação que Ele realizou uma vez por todas. A Páscoa é o centro anual; o domingo, dia da ressurreição, é o centro da semana cristã.

                    O calendário ajuda a comunidade a percorrer toda a vida do Senhor, e não apenas seus temas preferidos. Também recorda Maria e os santos como testemunhas da graça. O mistério de Cristo permanece o fundamento de cada celebração. Festas de padroeiros e transferências locais precisam ser consultadas no calendário da diocese ou da paróquia, sem deduções apenas pela data civil.
                """),
                s("2 · Esperar e acolher: Advento e Natal", "CIC 522–526; 524; 1171", """
                    O Advento prepara a celebração do Natal e desperta a esperança na vinda gloriosa de Cristo. Sua espera não é passividade: pede vigilância, conversão e caridade. O Natal celebra a Encarnação, a proximidade do Filho que assumiu nossa humanidade. O tempo natalino continua depois de 25 de dezembro, segundo o calendário litúrgico, até a festa do Batismo do Senhor.

                    A coroa do Advento e o presépio podem educar a oração da família, mas não são amuletos nem condições para receber a graça. Uma vela acesa pede cuidado com a segurança; uma pequena leitura pode bastar onde não há espaço para objetos. A preparação material faz sentido quando serve à escuta da Palavra, à celebração e à atenção a quem precisa.
                """),
                s("3 · Converter-se e celebrar: Quaresma e Páscoa", "CIC 540; 571–573; 638–655; 1438", """
                    A Quaresma prepara a Páscoa com oração, penitência e caridade, em memória do Batismo e em solidariedade com quem se prepara para recebê-lo. Jejum e abstinência seguem a disciplina da Igreja, com condições de idade, saúde e outras exceções. Seu sentido é abrir espaço para Deus e para o próximo, não punir o corpo ou provar resistência.

                    O Tríduo Pascal reúne a celebração da Ceia do Senhor, da Paixão e da Ressurreição, com seu centro na Vigília Pascal. É o ápice do ano. O Tempo Pascal prolonga a alegria da ressurreição durante cinquenta dias até Pentecostes. A ascensão de Jesus e o dom do Espírito não encerram Sua presença: inauguram a missão da Igreja no mundo.
                """),
                s("4 · Crescer no cotidiano: Tempo Comum e cores", "CIC 1168–1173; 1150–1152", """
                    O Tempo Comum percorre a vida pública de Jesus e aprofunda o seguimento. «Comum» não significa sem importância ou sem graça. Ele se distribui em dois períodos: depois do tempo do Natal e depois de Pentecostes, até o Advento seguinte. Por isso um esquema que o coloca apenas no fim do ano deixa de mostrar a sequência real.

                    As cores orientam o olhar: roxo em Advento e Quaresma; branco nas celebrações de Natal e Páscoa e em muitas festas; verde no Tempo Comum; vermelho em Pentecostes, na Paixão e em celebrações de mártires. Existem exceções e usos próprios, como o rosa em domingos previstos. A cor ajuda a reconhecer o sentido da celebração, mas o calendário local dá a indicação concreta.
                """),
            ),
            scripture = bible("Lucas 4, 16–21; Atos 2, 1–11", "Jesus lê o profeta Isaías na sinagoga e anuncia o cumprimento da promessa. Em Pentecostes, o Espírito fortalece a missão dos discípulos.", "Jesus anuncia a boa notícia aos pobres e a libertação. Em Jerusalém, os discípulos recebem o Espírito e anunciam as maravilhas de Deus a pessoas de diferentes povos. Resumo editorial das passagens.", "O calendário nos leva apenas a recordar ou a participar da missão de Jesus? Relacione escuta da Palavra, dom do Espírito e serviço. Pentecostes envia a comunidade a comunicar o Evangelho para além de seu círculo habitual."),
            example = example("Uma família desmonta toda a oração do Natal em 26 de dezembro, imaginando que o tempo natalino acabou com o feriado.", "O feriado civil e o tempo litúrgico são diferentes. A família pode consultar o calendário paroquial, continuar contemplando a Encarnação e acompanhar as festas desse período. Não precisa prolongar uma decoração por obrigação: o essencial é perceber que a Igreja dedica um tempo à alegria e à compreensão do nascimento do Senhor."),
            takeaways = listOf("A Páscoa é o centro do ano; o domingo celebra semanalmente a ressurreição.", "A sequência inclui dois períodos de Tempo Comum e o Tríduo como ápice.", "Cores e práticas ajudam a oração; celebrações locais seguem o calendário da Igreja."),
            terms = listOf(term("Ano litúrgico", "Percurso anual da celebração do mistério de Cristo e da memória de Seus santos."), term("Tríduo Pascal", "Celebração da Páscoa do Senhor desde a Ceia até o Domingo da Ressurreição."), term("Pentecostes", "Celebração do dom do Espírito Santo, ao concluir os cinquenta dias pascais.")),
            recall = listOf(recall("O Tempo Comum aparece somente depois de Pentecostes?", "Não. Há um primeiro período depois do tempo do Natal e um segundo depois de Pentecostes, antes do novo Advento."), recall("Por que a Quaresma não é uma competição de sacrifícios?", "Sua penitência prepara para a Páscoa e para uma vida convertida, unindo oração, caridade e a disciplina da Igreja com prudência e cuidado da saúde.")),
        ),
        "daily" to FormationStudy(
            sections = listOf(
                s("1 · Um encontro que cabe na vida real", "CIC 2697–2699; 2725–2728", """
                    Um ritmo de oração cria espaço para a relação com Deus, assim como reservamos tempo para pessoas que amamos. Não existe uma única duração adequada a todos. Trabalho, estudo, cuidado de crianças, doença e turnos noturnos pedem adaptações. A regularidade ajuda, mas não deve se transformar em uma medida do próprio valor ou em julgamento sobre os outros.

                    Comece com um compromisso pequeno e concreto: cinco minutos depois do café, uma breve leitura no intervalo ou um Pai-Nosso antes de dormir. Escolha um momento e um lugar possíveis, em vez de esperar as condições perfeitas. Se a rotina mudar, adapte o horário. O objetivo é favorecer um encontro fiel, não proteger um esquema que prejudica deveres importantes.
                """),
                s("2 · Manhã e pausas: oferecer e recordar", "CIC 2659–2660; 2698", """
                    Pela manhã, reconheça o dom da vida e ofereça a Deus as pessoas, decisões e trabalhos do dia. Uma frase basta para começar: «Senhor, guia minhas escolhas e meu modo de tratar os outros». Ler um pequeno trecho do Evangelho pode dar direção à jornada. Não é necessário prever tudo ou pedir uma garantia de que nada difícil acontecerá.

                    Uma pausa no meio do dia ajuda a recordar a presença de Deus. Pode ser gratidão por algo recebido, intercessão por quem encontramos ou pedido de paciência antes de uma conversa. Quem não consegue parar longamente pode rezar em silêncio num deslocamento seguro. A oração acompanha o trabalho sem exigir abandonar a atenção necessária à direção, a uma máquina ou ao cuidado de alguém.
                """),
                s("3 · À noite: agradecer, reconhecer, recomeçar", "CIC 1435; 1454; 2637–2638", """
                    O exame do dia pode seguir quatro movimentos: agradecer um dom, reconhecer uma resposta ao amor ou uma falta, pedir perdão e escolher um passo para amanhã. Procure acontecimentos concretos: uma conversa, uma decisão, uma omissão. O exame não é uma investigação sem fim para obter a sensação de que nenhum detalhe escapou.

                    Se surgir um pecado grave, prepare a Reconciliação sacramental. A oração noturna não substitui o sacramento, mas ajuda a voltar-se a Deus e a organizar um passo possível. Uma nota no diário pode guardar uma descoberta; escrever não é obrigatório. Termine confiando o que permanece inacabado. Descansar e cuidar da saúde também pertencem à responsabilidade pela vida recebida.
                """),
                s("4 · Um ritmo pessoal dentro da Igreja", "CIC 1174–1178; 2180–2188; 2691; 2742–2745", """
                    A oração pessoal cresce com a Missa, a Palavra e a comunidade. O domingo oferece um centro semanal; a confissão acompanha a conversão; a oração em família ou com um grupo ajuda a perseverar. A Liturgia das Horas é oração da Igreja que santifica o dia e pode ser conhecida gradualmente. Não é necessário acrescentar todas as devoções de uma vez.

                    Depois de uma semana, avalie o ritmo: ele é possível? Ajuda a escutar e amar? Está alimentando culpa ou afastando de responsabilidades? Ajuste com liberdade e, se necessário, com orientação. Quando perder um dia, retorne no próximo momento possível sem compensações exaustivas. A fidelidade cristã aprende a recomeçar e a deixar a oração produzir paciência, verdade e serviço.
                """),
            ),
            scripture = bible("Marcos 1, 32–39; 1 Tessalonicenses 5, 16–24", "Marcos mostra Jesus entre o cuidado dos enfermos, a oração em lugar retirado e o anúncio em outras cidades. Paulo orienta a vida da comunidade.", "Jesus procura um lugar para rezar e depois continua Sua missão. Paulo convida a perseverar na oração, agradecer e discernir o bem, confiando na fidelidade de Deus. Resumo para acompanhar a leitura bíblica.", "Como Jesus une recolhimento e serviço? Rezar sem cessar não significa repetir palavras sem interrupção, mas orientar a vida para Deus, retomando Seu chamado no meio das atividades e em momentos próprios de oração."),
            example = example("Uma mãe que trabalha à noite não consegue rezar no horário que escolheu. Passa a dormir menos para compensar e se sente sempre em falta.", "Ela pode escolher outro momento e uma forma menor, preservando o descanso e o cuidado da família. Uma oferta breve ao começar o turno e um agradecimento antes de dormir podem formar um ritmo real. Oração não exige negligenciar a saúde. Se a culpa permanece desproporcional, um acompanhamento estável pode ajudar a discernir as cobranças."),
            takeaways = listOf("Um compromisso pequeno e concreto favorece a fidelidade sem medir o valor pessoal.", "Oferecer, recordar e examinar integram a oração aos momentos do dia.", "A oração pessoal cresce com a Igreja e produz cuidado, verdade e serviço."),
            terms = listOf(term("Oferecimento do dia", "Oração que entrega a Deus atividades, encontros e escolhas da jornada."), term("Exame do dia", "Revisão orante que agradece, reconhece faltas e prepara um recomeço."), term("Liturgia das Horas", "Oração pública da Igreja que santifica as horas com salmos, leituras e preces.")),
            recall = listOf(recall("Perder um horário exige compensar com privação de sono?", "Não. Adapte a prática às responsabilidades e à saúde. Recomeçar com uma forma possível serve melhor ao encontro com Deus."), recall("Como distinguir exame do dia de uma cobrança sem fim?", "O exame olha fatos concretos, agradece, reconhece o que precisa mudar e termina na confiança. Uma busca interminável por certeza e culpa pede acompanhamento.")),
        ),
    )
}

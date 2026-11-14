package com.sertumrosarum.domain.formation

import com.sertumrosarum.domain.formation.FormationCurriculum.bible
import com.sertumrosarum.domain.formation.FormationCurriculum.example
import com.sertumrosarum.domain.formation.FormationCurriculum.recall
import com.sertumrosarum.domain.formation.FormationCurriculum.s
import com.sertumrosarum.domain.formation.FormationCurriculum.term

internal object FormationCelebration {
    val studies = mapOf(
        "sacraments" to FormationStudy(
            sections = listOf(
                s("1 · Sinais que realizam o que anunciam", "CIC 1113–1134", """
                    Usamos sinais todos os dias: uma palavra comunica, um abraço acolhe, a água lava. Nos sacramentos, Cristo assume sinais sensíveis para comunicar Sua graça. A água do Batismo não apenas recorda uma limpeza interior: no rito sacramental, Deus realmente faz renascer para a vida em Cristo. Por isso falamos em sinais eficazes, instituídos por Jesus e confiados à Igreja.

                    A eficácia vem de Cristo, não da simpatia ou da santidade pessoal do ministro. Isso não torna a celebração automática ou mágica: é preciso respeitar o sinal e a intenção da Igreja, e a disposição de quem recebe importa para os frutos da graça. A fé acolhe o dom; não fabrica o poder do sacramento.
                """),
                s("2 · A iniciação: nascer, fortalecer-se, alimentar-se", "CIC 1210–1213; 1262–1274; 1285", """
                    O Batismo perdoa os pecados, faz nascer como filho adotivo de Deus e incorpora à Igreja. Seu caráter espiritual é permanente: não se repete. A Confirmação aprofunda a graça batismal e fortalece para testemunhar Cristo no Espírito. Não é uma formatura que encerra a catequese. A Eucaristia alimenta continuamente a comunhão com o Senhor e Seu povo.

                    No Batismo de crianças, pais, padrinhos e comunidade assumem a responsabilidade de educar na fé. A criança recebe um dom que deverá aprender a viver livremente. Para adultos, a iniciação normalmente inclui o catecumenato: anúncio, conversão, formação e inserção na comunidade. Os detalhes de preparação são acompanhados pela paróquia e pela disciplina da Igreja.
                """),
                s("3 · A cura: perdão e presença na enfermidade", "CIC 1420–1421; 1499–1532", """
                    A Penitência reconcilia com Deus e com a Igreja quem pecou depois do Batismo. A Unção dos Enfermos oferece a graça de Cristo na doença grave e na fragilidade da velhice: fortalece, consola e une à Sua Páscoa. Não é reservada aos últimos minutos de vida, nem substitui o cuidado médico ou promete necessariamente uma cura física.

                    Quem enfrenta doença séria, cirurgia por enfermidade grave ou crescente fragilidade pode conversar com o sacerdote sobre a Unção. Ela pode ser recebida novamente em situações previstas pela Igreja. A comunidade participa visitando, ajudando e rezando, sem pressionar a pessoa enferma a demonstrar uma força ou uma alegria que ela não sente.
                """),
                s("4 · A serviço da comunhão e da missão", "CIC 1533–1535; 1536; 1601; 1667–1676", """
                    Pela Ordem, bispos, presbíteros e diáconos são constituídos para serviços distintos na Igreja. No Matrimônio, um homem e uma mulher batizados estabelecem, por consentimento livre, uma aliança de toda a vida, ordenada ao bem dos esposos e à geração e educação dos filhos. Esses sacramentos não concedem prestígio: chamam a uma entrega a serviço dos outros.

                    Bênçãos, água benta, medalhas e outras práticas não são novos sacramentos. Os sacramentais dispõem para receber a graça e santificam circunstâncias da vida pela oração da Igreja. Um objeto abençoado não funciona como amuleto. Pergunte sempre como aquele sinal conduz a Cristo, à participação na Igreja e à caridade concreta.
                """),
            ),
            scripture = bible("Mateus 28, 18–20; Atos 2, 37–42", "O Ressuscitado envia Seus discípulos; depois de Pentecostes, os que acolhem o anúncio entram na vida da comunidade.", "Jesus manda fazer discípulos, batizar e ensinar. Em Atos, os que recebem a Palavra são batizados e perseveram no ensinamento dos apóstolos, na comunhão, na fração do pão e nas orações. Resumo para leitura acompanhada.", "A passagem termina no rito ou mostra uma vida que continua? Identifique anúncio, sacramento e perseverança. O Batismo inicia uma pertença que cresce na Palavra, na Eucaristia, na oração e no cuidado mútuo."),
            example = example("Uma família pensa em esperar que o avô esteja inconsciente para chamar o sacerdote e pedir a Unção dos Enfermos.", "A Unção pode acompanhar a pessoa durante uma enfermidade grave e permitir que participe conscientemente da celebração. A família pode procurar a paróquia com antecedência, manter o tratamento médico e perguntar como preparar a visita. O sacramento é presença de Cristo na fragilidade, não uma declaração de que a esperança acabou."),
            takeaways = listOf("Cristo comunica a graça por sinais eficazes; sacramento não é magia.", "Iniciação: Batismo, Confirmação e Eucaristia. Cura: Penitência e Unção.", "Ordem e Matrimônio servem à comunhão; sacramentais são distintos dos sete sacramentos."),
            terms = listOf(term("Graça sacramental", "Dom do Espírito próprio de cada sacramento para a vida em Cristo."), term("Caráter", "Marca espiritual permanente do Batismo, da Confirmação e da Ordem; esses sacramentos não se repetem."), term("Sacramental", "Sinal sagrado instituído pela Igreja que prepara para acolher a graça e santifica situações da vida.")),
            recall = listOf(recall("Por que a Confirmação não encerra a formação cristã?", "Ela fortalece a graça batismal e a missão de testemunhar; exige uma vida que continua crescendo na fé, na Eucaristia e na caridade."), recall("Uma medalha abençoada é um oitavo sacramento?", "Não. É um sacramental, que remete à oração e à fé da Igreja. Não possui um poder mágico independente de Deus.")),
        ),
        "mass" to FormationStudy(
            sections = listOf(
                s("1 · Reunidos como um só povo", "CIC 1136–1144; 1348–1349", """
                    Participar da Missa começa antes de procurar um lugar: chegamos como membros de um povo convocado por Cristo. O canto, a saudação, o ato penitencial, o Glória quando previsto e a oração inicial dispõem a assembleia para celebrar. O ato penitencial expressa arrependimento, mas não substitui a confissão sacramental dos pecados graves.

                    Participação ativa não significa falar ou realizar uma tarefa a todo instante. Inclui escutar, responder, cantar, oferecer-se e guardar silêncio. Os gestos do corpo ajudam a oração comum, com adaptações para quem tem limitações de saúde. A pessoa que não consegue ficar de pé pode participar com reverência sentada, sem precisar se expor ou competir com os outros.
                """),
                s("2 · A Palavra pede escuta e resposta", "CIC 103–104; 1349; 1154", """
                    Na liturgia da Palavra, as leituras bíblicas, o salmo e o Evangelho anunciam a história da salvação e a pessoa de Jesus. A homilia ajuda a compreender e viver a Palavra. A profissão de fé, nos dias previstos, e a oração dos fiéis são respostas da comunidade. Não assistimos a uma palestra preliminar antes da parte importante.

                    Ler o Evangelho antes da celebração pode facilitar a atenção. Durante a Missa, procure ouvir a passagem inteira, sem ficar preso a uma palavra que não entendeu. Depois, retome a pergunta com a Bíblia e um auxílio seguro. A mesa da Palavra e a mesa eucarística pertencem a uma única celebração; a escuta prepara nossa oferta e nossa comunhão.
                """),
                s("3 · A ação de graças e a oferta de Cristo", "CIC 1350–1354; 1362–1372", """
                    Apresentam-se pão e vinho e, com eles, nossa vida e o trabalho humano. Na oração eucarística, a Igreja dá graças ao Pai, invoca o Espírito e celebra o memorial da entrega de Jesus. O relato da instituição e a consagração não são uma encenação: pela palavra de Cristo e pela ação do Espírito, Ele Se torna sacramentalmente presente.

                    Memorial bíblico é mais que uma lembrança mental. Na Missa, o único sacrifício da cruz torna-se presente sacramentalmente; Jesus não morre outra vez. Unimos a Ele nossas alegrias, trabalhos e sofrimentos, sem pensar que uma oferta humana compra a salvação. O «Amém» que conclui a oração eucarística expressa a participação da assembleia nessa ação de graças.
                """),
                s("4 · Comunhão e envio", "CIC 1355; 1384–1390; 1397; 2180–2183", """
                    O Pai-Nosso, o rito da paz e a fração do pão preparam a Comunhão. Comungar exige a preparação própria, aprofundada na próxima lição. Quem não comunga pode participar da escuta, da oferta e da oração, pedindo a graça de unir-se a Cristo. Não use a fila da Comunhão para julgar a vida espiritual de outra pessoa.

                    Depois da ação de graças e da oração final, a bênção e a despedida nos enviam à vida cotidiana. O cuidado com os pobres, a honestidade e a reconciliação mostram os frutos da celebração. A participação dominical é dever e alimento da vida católica; doença, cuidado necessário de alguém e outras causas sérias podem impedir a presença. Leve dúvidas concretas ao sacerdote.
                """),
            ),
            scripture = bible("Lucas 24, 13–35; 1 Coríntios 11, 23–26", "Emaús mostra a escuta das Escrituras e o reconhecimento ao partir o pão. Paulo transmite à comunidade a tradição da Ceia do Senhor.", "Os discípulos encontram Jesus na Palavra e na mesa. Paulo recorda o pão e o cálice recebidos do Senhor e ensina que a comunidade anuncia Sua morte até que Ele venha. Resumo editorial dessas passagens.", "Como Palavra, mesa e missão aparecem nos textos? Observe que os discípulos voltam aos irmãos: o encontro não se encerra em uma experiência privada. A celebração fortalece a comunhão e o testemunho."),
            example = example("Clara chega à Missa e pensa que só participa se receber a Comunhão. Neste domingo, ela precisa antes procurar a confissão.", "Ela pode escutar a Palavra, responder, oferecer a vida com a assembleia e fazer uma oração de desejo de união com Cristo. Deve buscar a Reconciliação para voltar à Comunhão devidamente preparada. Sua presença não é inútil: a Missa é ação de Cristo e da Igreja inteira, não apenas o momento de entrar na fila."),
            takeaways = listOf("A Missa reúne escuta, ação de graças, oferta, Comunhão e envio.", "O único sacrifício de Cristo torna-se presente; não acontece uma nova crucifixão.", "Participar inclui silêncio e escuta e produz frutos na vida e no serviço."),
            terms = listOf(term("Liturgia", "Ação de Cristo com Sua Igreja, pela qual participamos de Sua obra de salvação."), term("Memorial", "Celebração que torna presente sacramentalmente o acontecimento salvífico, além de apenas recordá-lo."), term("Epiclese", "Invocação do Espírito Santo na ação sacramental da Igreja.")),
            recall = listOf(recall("O ato penitencial da Missa substitui a confissão de pecado grave?", "Não. Ele dispõe a assembleia ao arrependimento, mas não substitui o sacramento da Reconciliação necessário nesse caso."), recall("Como o envio final se liga à Eucaristia?", "Recebemos e celebramos o amor de Cristo para vivê-lo: a comunhão pede serviço, justiça e atenção aos irmãos.")),
        ),
        "eucharist" to FormationStudy(
            sections = listOf(
                s("1 · Um dom que nasce da entrega de Jesus", "CIC 1322–1330; 1337–1344", """
                    Eucaristia significa ação de graças. Na Última Ceia, Jesus dá aos discípulos Seu Corpo e Seu Sangue sob os sinais do pão e do vinho e manda celebrar Sua memória. O dom está ligado à entrega da cruz e à ressurreição. Não se trata de um objeto sagrado separado da vida de Jesus, mas do próprio Senhor que Se oferece e reúne Sua Igreja.

                    A Eucaristia é fonte e ápice da vida cristã. Os outros sacramentos e a missão da Igreja conduzem a essa comunhão e dela recebem força. A adoração eucarística prolonga a atenção ao Senhor celebrado na Missa, sem substituir a participação na celebração. O mesmo amor que nos faz ajoelhar diante de Cristo nos envia a reconhecer os irmãos.
                """),
                s("2 · Presença real e mudança substancial", "CIC 1373–1381", """
                    Cristo está presente de diversos modos na Igreja: em Sua Palavra, na comunidade reunida, nos pobres e na ação sacramental. Sua presença eucarística é singular: verdadeira, real e substancial. Pela consagração, toda a substância do pão e do vinho se converte no Corpo e no Sangue de Cristo. A Igreja chama essa conversão de transubstanciação.

                    Permanecem as aparências sensíveis do pão e do vinho, como sabor e cor. O termo «substância» não pretende descrever uma mudança química visível ao microscópio: indica o que a realidade é em seu fundamento. Sob cada espécie está Cristo inteiro. Receber somente a hóstia não significa receber uma parte menor de Jesus; a presença não é dividida em pedaços de Sua pessoa.
                """),
                s("3 · Preparar-se para receber", "CIC 1384–1390; Código de Direito Canônico, cân. 916 e 919", """
                    A preparação inclui fé na presença do Senhor, reconciliação, recolhimento e respeito ao jejum eucarístico. Quem tem consciência de pecado mortal deve receber a absolvição antes de comungar. A exceção prevista pela Igreja requer motivo grave, impossibilidade de confessar-se e contrição perfeita com propósito de confessar-se quanto antes; não é uma dispensa genérica por vergonha ou conveniência.

                    Em regra, guarda-se uma hora de jejum antes da Comunhão; água e remédios não o quebram. A disciplina prevê exceções para idosos, enfermos e quem cuida deles. Não transforme a preparação em vigilância ansiosa sobre segundos, pensamentos involuntários ou situações alheias. Em dúvidas persistentes, procure orientação estável do sacerdote e conheça a disciplina da sua comunidade.
                """),
                s("4 · Receber para tornar-se comunhão", "CIC 1391–1401; 1415", """
                    A Comunhão aprofunda a união com Cristo, fortalece a caridade, perdoa pecados veniais e preserva dos pecados mortais; não substitui a confissão de um pecado grave já cometido. Seu fruto pede nossa cooperação. A ação de graças pode ser simples: silêncio, louvor e pedido de que o amor recebido transforme uma atitude concreta.

                    A unidade eucarística envolve também a fé e a comunhão da Igreja. Por isso o acesso à Comunhão tem uma disciplina e não é apenas sinal de simpatia entre pessoas. Acolha com respeito visitantes e pessoas que não comungam, sem constrangê-los. Perguntas sobre uma situação matrimonial ou sacramental particular devem ser tratadas pessoalmente com o sacerdote, sem conclusões apressadas a partir de uma tela.
                """),
            ),
            scripture = bible("João 6, 51–58; 1 Coríntios 11, 23–29", "Jesus apresenta o pão da vida. Paulo trata da Ceia numa comunidade em que divisões e humilhação dos pobres contradizem o dom recebido.", "Jesus oferece Sua carne para a vida do mundo. Paulo transmite as palavras da Ceia e pede que cada um se examine e reconheça o Corpo do Senhor. Resumo para acompanhar a leitura, sem pretender esgotar os textos.", "Por que Paulo associa o discernimento à maneira de tratar os irmãos? A reverência eucarística inclui a fé na presença real e uma vida que abandona o desprezo por quem tem menos."),
            example = example("Depois da consagração, Lucas diz que nada aconteceu porque a hóstia ainda tem a mesma aparência.", "Os sentidos percebem o que permanece: as aparências do pão. A fé acolhe a palavra de Cristo sobre o que mudou: a substância. A Igreja não pede imaginar uma aparência de carne nem procurar uma reação emocional. A reverência nasce da confiança no Senhor, e não de fabricar uma sensação extraordinária."),
            takeaways = listOf("Na Eucaristia, recebemos o próprio Cristo, inteiro sob cada espécie.", "Transubstanciação nomeia a mudança substancial, permanecendo as aparências.", "A preparação e a ação de graças ligam a Comunhão à conversão e à caridade."),
            terms = listOf(term("Espécies eucarísticas", "As aparências sensíveis do pão e do vinho que permanecem após a consagração."), term("Transubstanciação", "Conversão de toda a substância do pão e do vinho no Corpo e no Sangue de Cristo."), term("Comunhão espiritual", "Oração de desejo de união com Cristo, especialmente quando não se pode comungar sacramentalmente.")),
            recall = listOf(recall("Receber somente a hóstia significa receber somente parte de Cristo?", "Não. Cristo está inteiro sob cada espécie eucarística. A comunhão sob uma só espécie recebe o próprio Senhor."), recall("A Comunhão substitui a confissão de um pecado mortal?", "Não. Quem tem consciência de pecado mortal deve procurar a Reconciliação antes de comungar, observada a exceção estrita prevista pela Igreja.")),
        ),
        "reconciliation" to FormationStudy(
            sections = listOf(
                s("1 · A misericórdia nos devolve à verdade", "CIC 1422–1429; 1440–1449", """
                    Deus não espera uma biografia sem falhas para acolher alguém. A Penitência é encontro com Cristo que perdoa e reconcilia com a Igreja. O pecado nunca é apenas uma infração privada: fere a comunhão e pode atingir outras pessoas. O perdão sacramental não apaga a responsabilidade de reparar o dano possível e de aceitar as consequências justas de um ato.

                    Jesus confiou aos apóstolos o ministério da reconciliação. O sacerdote age como ministro de Cristo e da Igreja, não como dono da consciência de quem se confessa. O sigilo sacramental é inviolável. A pessoa pode dizer que não sabe como começar e pedir ajuda; não precisa apresentar uma fala elegante ou uma história completa de toda a vida.
                """),
                s("2 · Examinar sem alimentar o medo", "CIC 1454; 1857–1860", """
                    O exame de consciência olha a vida diante da Palavra: relação com Deus, verdade nas palavras, justiça, cuidado com a vida, sexualidade, bens e deveres concretos. Procure fatos e escolhas, em vez de uma sensação vaga de que «tudo em mim é errado». Para o pecado mortal, devem estar juntas matéria grave, plena consciência e consentimento deliberado.

                    Um pensamento que surge sem consentimento não equivale a uma decisão. Esquecer sem querer um pecado, depois de um exame sincero, não é o mesmo que escondê-lo deliberadamente. Se depois lembrar um pecado grave não confessado, apresente-o na próxima confissão. Quem sofre de escrúpulos deve evitar exames intermináveis e buscar um confessor estável, seguindo sua orientação concreta.
                """),
                s("3 · Arrependimento, confissão e absolvição", "CIC 1450–1458; 1480–1484", """
                    Contrição é a dor pelo pecado com o propósito de não voltar a pecar. Não exige lágrimas nem a certeza de jamais cair de novo; pede uma decisão sincera de mudar e de evitar o que favorece a queda. A contrição perfeita nasce do amor de Deus e inclui o propósito de recorrer à confissão sacramental assim que possível.

                    Confesse os pecados graves de que tem consciência em espécie e número, tanto quanto possível, sem detalhes desnecessários e sem acusar terceiros. A confissão de pecados veniais também ajuda a crescer. Escute a orientação, acolha a penitência e participe da oração de arrependimento conforme o rito. Na absolvição, é Cristo quem oferece o perdão por meio do ministro.
                """),
                s("4 · Um recomeço que se torna concreto", "CIC 1459–1460; 1468–1470", """
                    Cumpra a penitência recebida; se não entendeu ou não pode cumpri-la, diga isso ao confessor. Quando possível, repare um dano: devolver algo injustamente tomado, corrigir uma mentira ou pedir perdão. A reparação não compra a absolvição. É fruto de uma conversão que procura reconstruir o bem e assumir a responsabilidade.

                    Depois de uma confissão sincera, agradeça e siga o caminho. Não é necessário repetir pecados já absolvidos para obter uma sensação perfeita de certeza. Uma nova queda pode pedir novo arrependimento e ajuda para mudar hábitos. A frequência da confissão é caminho de cura, não um placar. Acompanhe a vida sacramental com escolhas práticas e com o apoio humano necessário.
                """),
            ),
            scripture = bible("Lucas 15, 11–32; João 20, 19–23", "A parábola apresenta o pai e seus dois filhos. Depois da Páscoa, Jesus envia os apóstolos e lhes comunica o Espírito para o ministério do perdão.", "O filho mais novo reconhece sua miséria e volta; o pai o acolhe e convida também o filho mais velho a entrar na alegria. O Ressuscitado confia aos apóstolos o perdão dos pecados. Resumo editorial.", "Observe o retorno do filho e a iniciativa do pai. Quem ainda precisa entrar na festa? A misericórdia não só recebe quem volta: também converte o olhar de quem não aceita o perdão oferecido ao outro."),
            example = example("Após um exame sincero e a absolvição, Beatriz lembra um pecado grave que esquecera sem querer e pensa que toda a confissão foi inválida.", "O esquecimento involuntário não é ocultação deliberada. Ela pode confiar na absolvição recebida e mencionar o pecado na próxima confissão. Não precisa repetir toda a vida. Se a dúvida se torna repetitiva e angustiante, o acompanhamento regular com um confessor ajuda a distinguir responsabilidade de escrúpulo."),
            takeaways = listOf("Cristo perdoa e reconcilia por meio do ministério da Igreja.", "Exame, contrição, confissão e satisfação expressam a resposta ao perdão.", "A misericórdia pede reparação possível e permite um recomeço sem exames intermináveis."),
            terms = listOf(term("Contrição", "Arrependimento pelo pecado com propósito sincero de mudança."), term("Absolvição", "Ato sacramental pelo qual o sacerdote, em nome de Cristo, comunica o perdão."), term("Satisfação", "Penitência e reparação que ajudam a restaurar o bem ferido pelo pecado.")),
            recall = listOf(recall("Arrependimento exige sentir uma emoção forte?", "Não. A contrição é uma disposição real de rejeitar o pecado e voltar a Deus, com propósito de mudança, mesmo sem lágrimas."), recall("Por que reparar um dano depois da absolvição?", "O perdão restaura a relação com Deus, mas certas consequências permanecem. Reparar o possível é fruto da conversão e da justiça, não pagamento pelo perdão.")),
        ),
    )
}

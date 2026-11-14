package com.sertumrosarum.domain.formation

import com.sertumrosarum.domain.formation.FormationCurriculum.bible
import com.sertumrosarum.domain.formation.FormationCurriculum.example
import com.sertumrosarum.domain.formation.FormationCurriculum.recall
import com.sertumrosarum.domain.formation.FormationCurriculum.s
import com.sertumrosarum.domain.formation.FormationCurriculum.term

internal object FormationLife {
    val studies = mapOf(
        "dignity" to FormationStudy(
            sections = listOf(
                s("1 · Um valor que ninguém precisa conquistar", "CIC 355–361; 1700–1715; 2258", """
                    Ser imagem de Deus significa receber uma dignidade própria, anterior ao desempenho. Uma criança ainda sem autonomia, uma pessoa com deficiência, alguém doente ou um idoso dependente não têm menos valor. A dignidade não aumenta com a renda nem diminui com um erro moral. Uma pessoa pode agir de modo gravemente errado e continuar merecendo tratamento humano e justiça.

                    A fé cristã reconhece a unidade de corpo e alma. O corpo não é um objeto descartável; nossa vida corporal participa da vocação ao amor. Proteger a vida, cuidar da saúde, respeitar limites e rejeitar a violência fazem parte da resposta a Deus. Esse cuidado alcança cada etapa da existência e não depende de afinidade, religião ou nacionalidade.
                """),
                s("2 · Liberdade para o bem e consciência formada", "CIC 1730–1748; 1776–1802", """
                    Liberdade não é apenas poder escolher qualquer coisa. Ela amadurece quando aprendemos a reconhecer e realizar o bem. Hábitos, medo, pressões e ignorância podem limitar a responsabilidade; por isso não devemos julgar apressadamente a culpa interior de outra pessoa. Isso não transforma uma injustiça objetiva em algo bom: pede distinguir o ato da responsabilidade pessoal.

                    A consciência é o juízo da razão sobre o bem a fazer e o mal a evitar. Não se reduz ao que gosto ou ao que o grupo aprova. Deve ser formada pela Palavra, pela oração, pelo ensinamento da Igreja e por conselhos prudentes. Quando percebemos que erramos, o caminho é procurar a verdade e corrigir a ação, sem entregar a alguém o controle absoluto da nossa vida.
                """),
                s("3 · Os mandamentos guardam o amor", "CIC 2052–2082; 2197–2200; 2464–2474", """
                    Os três primeiros mandamentos orientam a relação com Deus: adorá-Lo, respeitar Seu nome e santificar o dia do Senhor. Os outros protegem o próximo: honrar os pais, respeitar a vida, viver a castidade, respeitar os bens, dizer a verdade e ordenar os desejos. Jesus não elimina essa direção; mostra sua plenitude no amor a Deus e ao próximo.

                    Honrar pai e mãe não obriga a obedecer a uma ordem imoral nem a permanecer num ambiente violento. Respeitar os bens inclui pagar o que é justo e reparar o que foi roubado. Dizer a verdade inclui não espalhar boatos, expor sem motivo a intimidade alheia ou transformar suspeitas em acusações. Os mandamentos se tornam concretos em relações, trabalho e comunicação digital.
                """),
                s("4 · As bem-aventuranças e o bem comum", "CIC 1716–1729; 1803–1811; 1905–1917", """
                    Nas bem-aventuranças, Jesus anuncia o caminho da felicidade em comunhão com Deus: mansidão, misericórdia, pureza de coração, fome de justiça e construção da paz. Elas não elogiam a miséria ou a violência sofrida. Revelam uma esperança que sustenta quem sofre e uma forma de agir que recusa reproduzir a crueldade.

                    A prudência discerne o bem concreto; a justiça dá ao outro o que lhe é devido; a fortaleza sustenta o bem nas dificuldades; a temperança ordena os desejos. Essas virtudes crescem pela graça e pelo exercício. O bem comum pede condições para todos viverem com dignidade: atenção a quem é excluído, trabalho honesto e responsabilidade pelas instituições e pela criação. A moral cristã forma pessoas e relações.
                """),
            ),
            scripture = bible("Gênesis 1, 26–31; Mateus 5, 1–12", "O relato da criação apresenta o ser humano diante de Deus. No Sermão da Montanha, Jesus anuncia as bem-aventuranças aos discípulos.", "Homem e mulher são criados à imagem de Deus. Jesus chama bem-aventurados os pobres em espírito, os misericordiosos, os que buscam a justiça e os que promovem a paz. Resumo editorial; leia as passagens para perceber sua amplitude.", "O valor humano é apresentado como prêmio por produtividade? Que bem-aventurança oferece direção para uma escolha desta semana? Relacione a origem da dignidade com a maneira de tratar uma pessoa concreta."),
            example = example("Num grupo de mensagens, alguém publica uma história humilhante sobre um colega. Todos compartilham, dizendo que ele merece porque já errou.", "A dignidade não desaparece com uma falta. Antes de compartilhar, verifique a verdade, a necessidade e o respeito à intimidade. Se existe uma denúncia séria, use os canais responsáveis para apurar e proteger pessoas. Humilhação pública e boatos não substituem justiça; recusar a exposição pode ser um ato de prudência e de caridade."),
            takeaways = listOf("A dignidade vem de sermos imagem de Deus e não precisa ser conquistada.", "Consciência pede formação; liberdade amadurece na escolha do bem.", "Mandamentos, bem-aventuranças e virtudes orientam o amor concreto e o bem comum."),
            terms = listOf(term("Consciência", "Juízo da razão pelo qual reconhecemos a qualidade moral de uma ação concreta."), term("Virtude", "Disposição habitual e firme para praticar o bem."), term("Bem comum", "Condições sociais que permitem às pessoas e aos grupos alcançar mais plenamente seu desenvolvimento.")),
            recall = listOf(recall("Uma pessoa que comete um crime perde sua dignidade humana?", "Não. Deve responder com justiça pelos seus atos, mantendo o direito a um tratamento humano. Dignidade e responsabilidade não se excluem."), recall("Seguir a consciência dispensa a busca da verdade?", "Não. A consciência precisa ser formada e corrigida. Preferências e aprovação do grupo não bastam para tornar uma ação boa.")),
        ),
        "grace-freedom" to FormationStudy(
            sections = listOf(
                s("1 · A vida de Deus é dom", "CIC 1987–2005", """
                    A graça é o auxílio gratuito de Deus para respondermos ao Seu chamado e participarmos de Sua vida. A graça santificante é um dom estável que nos faz viver em amizade com Ele; as graças atuais são ajudas em situações concretas. Nenhuma delas é uma substância material ou um saldo acumulado por tarefas religiosas.

                    A justificação perdoa o pecado e renova interiormente. Seu início é dom de Deus, que não podemos merecer antecipadamente. As boas obras de uma vida em Cristo são resposta sustentada pela graça, e não compra da salvação. Quando a Igreja fala de mérito cristão, reconhece primeiro a ação de Deus e, depois, a cooperação livre da pessoa que Ele tornou capaz de amar.
                """),
                s("2 · Cooperar com uma liberdade real", "CIC 1730–1742; 2001–2002", """
                    A ação de Deus não trata a pessoa como um objeto. Ele chama, ilumina e fortalece uma resposta livre. Uma comparação com ajuda humana pode iluminar algo desse encontro, mas não explica toda a relação entre graça e liberdade. O essencial é manter as duas afirmações: dependemos de Deus e somos chamados a responder com responsabilidade.

                    Na prática, pedir a graça de ser paciente inclui rever palavras, afastar uma ocasião de agressão e reparar uma ofensa. Esperar que Deus mude tudo sem nenhuma resposta nossa não é confiança madura. Por outro lado, depender apenas da própria força e desprezar a oração também empobrece o caminho. A conversão envolve dom recebido, decisão e perseverança.
                """),
                s("3 · Distinguir pecado, tentação e culpa", "CIC 1846–1864; 1735", """
                    O pecado é uma falta contra a razão, a verdade e a consciência reta; fere o amor a Deus e ao próximo. O pecado mortal destrói a caridade no coração quando reúne matéria grave, plena consciência e consentimento deliberado. O pecado venial fere a caridade sem romper dessa maneira a aliança. Essas distinções orientam o cuidado espiritual e não uma caça ansiosa a falhas.

                    Tentação não é consentimento. Uma imagem involuntária, uma emoção forte ou um pensamento intrusivo não equivalem automaticamente a uma escolha moral. Medo, coação, ignorância e outros fatores podem diminuir a responsabilidade. A gravidade objetiva de um ato e a culpa pessoal precisam ser distinguidas. Converse com o confessor quando uma situação for delicada, evitando diagnosticar a alma de outras pessoas.
                """),
                s("4 · Converter hábitos e alimentar a esperança", "CIC 1817–1821; 1865–1869; 2012–2016", """
                    A repetição de escolhas forma hábitos. Virtudes facilitam o bem; vícios inclinam à repetição de condutas nocivas. Recomeçar envolve mais do que sentir remorso: identificar ocasiões, pedir auxílio, estabelecer limites e praticar um bem concreto. A oração e os sacramentos sustentam esse trabalho; apoio psicológico ou tratamento de uma dependência podem ser necessários e não demonstram falta de fé.

                    A esperança cristã evita dois extremos: achar que tudo está perdido e presumir que nenhuma conversão é necessária. Deus oferece misericórdia e pede uma resposta real. Um pequeno passo honesto vale mais que um projeto grandioso nunca iniciado. Se cair de novo, reconheça a verdade, procure a Reconciliação quando necessária e retome o caminho sem transformar o fracasso em identidade.
                """),
            ),
            scripture = bible("Efésios 2, 8–10; João 15, 1–8", "Paulo fala da salvação recebida em Cristo. Jesus usa a imagem da videira e dos ramos para ensinar a comunhão com Ele.", "A salvação é dom, e a vida nova nos orienta às boas obras. Os ramos dão fruto permanecendo unidos à videira; separados, não produzem por si mesmos essa vida. Resumo das duas passagens para leitura na Bíblia.", "Paulo elimina as boas obras ou as coloca como fruto do dom? Na imagem da videira, quem é a fonte da vida? A metáfora ilumina dependência e fecundidade, mas não transforma pessoas em ramos sem liberdade."),
            example = example("Renato se distrai durante a oração e tem um pensamento que não queria. Conclui que cometeu pecado mortal e que Deus o abandonou.", "Um pensamento involuntário não reúne por si só as condições do pecado mortal. Ele pode retomar a oração com calma. Se a angústia se repete, convém procurar um confessor estável e, se necessário, ajuda profissional. Examinar uma escolha concreta é diferente de vigiar toda emoção em busca de uma culpa escondida."),
            takeaways = listOf("A graça precede e sustenta a resposta; a salvação não é comprada por obras.", "A cooperação humana é livre e se expressa em escolhas, hábitos e caridade.", "Pecado mortal requer juntas matéria grave, plena consciência e consentimento deliberado."),
            terms = listOf(term("Justificação", "Ação misericordiosa de Deus que perdoa e renova a pessoa para a vida em Cristo."), term("Graça santificante", "Dom habitual que nos faz participar da vida divina e viver em amizade com Deus."), term("Tentação", "Solicitação ao pecado; experimentá-la não é o mesmo que consentir nela.")),
            recall = listOf(recall("As boas obras compram o primeiro dom da salvação?", "Não. Elas são fruto da graça e cooperação livre com Deus. A iniciativa e a capacidade de responder vêm de Seu dom."), recall("Por que matéria grave não basta para afirmar a culpa mortal pessoal?", "Também são necessárias plena consciência e consentimento deliberado. A avaliação da responsabilidade exige considerar a situação da pessoa.")),
        ),
        "mercy" to FormationStudy(
            sections = listOf(
                s("1 · Reconhecer Cristo em quem precisa", "CIC 1822–1829; 2443–2447", """
                    A caridade é o amor a Deus sobre todas as coisas e ao próximo por amor de Deus. Não é apenas simpatia ou vontade de ajudar quem nos agrada. Na parábola do juízo, Jesus Se identifica com quem tem fome, sede, está sem acolhimento, doente ou preso. O encontro com Ele torna a necessidade do outro uma responsabilidade concreta.

                    A ajuda cristã começa pela pessoa, e não pela satisfação de sentir-se generoso. Pergunte o que ela precisa, respeite sua liberdade e preserve sua imagem. Uma fotografia publicada sem consentimento pode transformar um gesto bom em exposição. Escutar antes de agir evita oferecer algo inútil, humilhante ou que coloque quem recebe em uma obrigação de gratidão.
                """),
                s("2 · Cuidar do corpo e das necessidades reais", "CIC 2447–2449", """
                    As obras corporais de misericórdia incluem alimentar os famintos, dar de beber aos sedentos, vestir quem precisa, acolher os que não têm abrigo, visitar enfermos e presos e sepultar os mortos. A lista educa o olhar para necessidades concretas; não exige que cada pessoa realize tudo sozinha ou sem preparo. Organizações responsáveis ajudam a oferecer continuidade e cuidado.

                    Uma visita a um enfermo deve respeitar consentimento, horários e orientações de saúde. O apoio a pessoas presas pode passar por iniciativas reconhecidas e pelo cuidado de suas famílias. Acolher não implica abrir a própria casa em qualquer circunstância: pode significar encaminhar a serviços adequados e acompanhar. Prudência e misericórdia cooperam para proteger quem ajuda e quem recebe ajuda.
                """),
                s("3 · Cuidar também do coração e da verdade", "CIC 2447; 1829; 2690", """
                    As obras espirituais incluem ensinar quem precisa, aconselhar na dúvida, corrigir com caridade, consolar quem sofre, perdoar ofensas, suportar com paciência as fraquezas do próximo e rezar pelos vivos e pelos mortos. Não autorizam dar conselhos sobre tudo. Ensinar pede conhecimento; corrigir pede humildade e oportunidade; consolar frequentemente começa por ficar e escutar.

                    Diante de um luto, uma presença respeitosa pode ajudar mais que uma explicação imediata sobre o sofrimento. Diante de uma situação grave, saber encaminhar a um profissional também é cuidado. Suportar com paciência não significa aceitar abuso ou abandonar a proteção de alguém. O amor cristão une compaixão, verdade e responsabilidade, sem exigir que a pessoa sofra em silêncio.
                """),
                s("4 · Da ajuda pontual à justiça", "CIC 1928–1942; 2401; 2446", """
                    A misericórdia não dispensa a justiça. Pagar um salário devido não é um favor, e devolver o que se tomou não é uma doação. Uma comunidade pode oferecer alimentos e também perguntar por que famílias não conseguem se alimentar: desemprego, doença, desperdício, discriminação ou falta de acesso a direitos podem exigir respostas mais duradouras.

                    Escolha uma ação possível, com prazo, responsabilidade e continuidade. Em vez de prometer que vai resolver tudo, combine uma visita, uma contribuição regular ou algumas horas de serviço. Depois, avalie com quem recebeu a ajuda se o gesto foi útil. A oração acompanha a ação e transforma nosso olhar; não deve ser usada para encerrar uma conversa quando podemos oferecer socorro concreto.
                """),
            ),
            scripture = bible("Lucas 10, 25–37; Mateus 25, 31–46", "Jesus responde à pergunta sobre o próximo com a história do samaritano. No discurso sobre o juízo, relaciona o cuidado dos necessitados ao encontro com Ele.", "O samaritano vê, aproxima-se, cuida das feridas e providencia continuidade. No juízo, gestos concretos de alimento, acolhida e visita são reconhecidos como feitos ao próprio Cristo. Resumo editorial para acompanhar a leitura.", "Quais verbos mostram uma compaixão que se torna ação? O samaritano não apenas sente pena: oferece tempo, recursos e acompanhamento. Que parte desse percurso está ao seu alcance nesta semana?"),
            example = example("Uma comunidade entrega cestas de alimentos, mas quer publicar o rosto de cada família para provar que a campanha funcionou.", "A prestação de contas pode mostrar quantidades e recibos sem expor pessoas. Se uma imagem for realmente necessária, o consentimento deve ser livre e não condicionar o recebimento da ajuda. A pessoa atendida tem dignidade e voz; a comunidade pode perguntar que apoio seria útil e planejar continuidade com discrição."),
            takeaways = listOf("A caridade reconhece Cristo no próximo e respeita sua liberdade e dignidade.", "Obras corporais e espirituais cuidam da pessoa inteira, com prudência.", "A misericórdia inclui justiça, continuidade e atenção às causas do sofrimento."),
            terms = listOf(term("Caridade", "Virtude teologal de amar a Deus sobre tudo e ao próximo por amor dEle."), term("Solidariedade", "Compromisso com o bem de todos, reconhecendo nossa interdependência e responsabilidade."), term("Justiça", "Vontade firme de dar a Deus e ao próximo o que lhes é devido.")),
            recall = listOf(recall("Pagar um salário atrasado pode ser apresentado como obra de generosidade?", "O pagamento devido é obrigação de justiça. A caridade não permite chamar de favor aquilo que já pertence ao outro."), recall("Como consolar alguém sem oferecer uma resposta pronta para sua dor?", "Escutar, respeitar o silêncio, oferecer presença e ajuda concreta, e reconhecer quando é necessário encaminhamento pastoral ou profissional.")),
        ),
        "forgiveness" to FormationStudy(
            sections = listOf(
                s("1 · O perdão recebido torna-se chamado", "CIC 2838–2845", """
                    No Pai-Nosso, pedimos perdão e nos comprometemos a perdoar. Não compramos a misericórdia de Deus pelo nosso desempenho, mas pedimos que o coração se abra ao mesmo amor que deseja receber. Jesus revela uma misericórdia que rompe a lógica da vingança e recusa reduzir alguém ao mal que praticou.

                    Perdoar pode começar como uma decisão pequena: não alimentar um plano de retaliação, pedir ajuda para não repetir a agressão ou apresentar a ferida a Deus. Uma dor profunda não se resolve por uma frase. O processo pode ser longo, com avanços e recaídas emocionais. Não sentir paz imediatamente não prova que a decisão de buscar o perdão foi falsa.
                """),
                s("2 · Nomear o mal sem viver da vingança", "CIC 2302–2303; 2843", """
                    O perdão não transforma uma injustiça em algo aceitável. Para perdoar com verdade, pode ser necessário nomear o que aconteceu, reconhecer o dano e lamentar o que foi perdido. A raiva pode indicar uma ferida que precisa de cuidado; alimentar deliberadamente o desejo de destruir o outro é uma direção diferente. Sentimento e decisão moral não são a mesma coisa.

                    Renunciar à vingança é recusar fazer do sofrimento alheio a nossa reparação. Isso pode coexistir com uma denúncia, um processo judicial ou a exigência de restituição. Uma vítima pode desejar que o agressor pare, assuma as consequências e se converta. A misericórdia não pede impunidade nem exige colocar outras pessoas em risco para demonstrar benevolência.
                """),
                s("3 · Perdão, reconciliação e confiança", "CIC 1459; 2264–2265; 2840–2845", """
                    Perdão, reconciliação e confiança estão relacionados, mas são distintos. O perdão pode começar no coração de quem sofreu, mesmo quando o ofensor não reconhece o mal. A reconciliação envolve a relação entre as pessoas e precisa de verdade, mudança e condições seguras. A confiança se reconstrói com comportamentos consistentes, não apenas com uma promessa.

                    Em situações de violência ou abuso, afastar-se e procurar proteção pode ser necessário. Ninguém deve usar uma orientação religiosa para exigir contato com quem ameaça sua integridade. Procure pessoas e serviços confiáveis e acompanhamento pastoral que respeite sua segurança. A decisão sobre um encontro não deve ser imposta pela culpa. Limites justos protegem também quem poderia sofrer novas agressões.
                """),
                s("4 · Se eu fui quem feriu", "CIC 1451–1460; 2487", """
                    Pedir perdão começa por assumir uma ação sem colocar a responsabilidade na reação da vítima. «Sinto muito se você se ofendeu» pode evitar reconhecer o próprio ato. Uma fala mais verdadeira identifica o que foi feito, admite o dano e apresenta uma disposição concreta de reparar. Não pressione a outra pessoa a oferecer proximidade ou confiança imediatamente.

                    Procure a Reconciliação sacramental quando necessária e aceite as consequências justas. Reparar uma mentira pode exigir corrigir o que foi dito; uma apropriação indevida, devolver; uma agressão repetida, buscar ajuda para interromper o comportamento. O perdão recebido não dá direito de cobrar silêncio. A mudança se verifica no tempo pela verdade e pelos atos, com respeito aos limites estabelecidos por quem foi ferido.
                """),
            ),
            scripture = bible("Mateus 18, 21–35; Romanos 12, 17–21", "Pedro pergunta sobre os limites do perdão. Paulo orienta a comunidade a não retribuir o mal com o mal.", "Jesus conta a história de um servo que recebe grande perdão, mas recusa misericórdia ao companheiro. Paulo pede que o mal não seja vencido por outra maldade e que se busque a paz no que depende de nós. Resumo para leitura bíblica.", "O que a parábola revela sobre a incoerência do coração? Em Romanos, observe a expressão sobre o que depende de nós: não controlamos a resposta do outro nem conseguimos produzir sozinhos uma relação segura."),
            example = example("Um familiar exige que Joana visite imediatamente alguém que a agrediu, dizendo que sem isso ela não perdoou de verdade.", "Uma visita não mede a autenticidade do perdão. Joana pode desejar não se vingar e ainda precisar de distância, proteção e acompanhamento. Reconciliação requer condições reais; contato não deve ser imposto. Uma orientação pastoral responsável ajuda a cuidar da ferida e a discernir passos seguros, sem substituir as medidas de proteção necessárias."),
            takeaways = listOf("Perdoar é renunciar à vingança; sentimentos dolorosos podem permanecer.", "Reconciliação pede verdade, mudança e segurança; confiança cresce por atos.", "Quem ofendeu deve assumir responsabilidade e reparar, sem exigir silêncio ou proximidade."),
            terms = listOf(term("Vingança", "Busca de fazer sofrer o outro como retaliação pelo mal recebido."), term("Reconciliação", "Restauração de uma relação na verdade, com mudança e condições adequadas."), term("Reparação", "Esforço concreto para corrigir ou compensar, no possível, o dano causado.")),
            recall = listOf(recall("É possível perdoar e procurar a justiça?", "Sim. Renunciar à vingança não elimina a denúncia, a reparação devida, a proteção e as consequências justas."), recall("Uma pessoa perdoada pode exigir confiança imediata?", "Não. A confiança depende de atos consistentes e de condições seguras. O perdão não concede direito de impor contato ou silêncio.")),
        ),
    )
}

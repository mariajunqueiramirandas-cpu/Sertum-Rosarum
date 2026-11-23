package com.sertumrosarum.domain.lumen

import com.sertumrosarum.domain.prayer.MysteryContent
import com.sertumrosarum.domain.prayer.MysterySet
import java.text.Normalizer

/**
 * Deterministic offline contemplative engine so every Lumen capability works gracefully
 * in a chapel, on an airplane, or whenever no API key / network connection is available.
 *
 * Every reply adapts authentically to the chosen [CatechistPersona].
 */
object OfflineCatechistEngine {

    fun reply(
        userText: String,
        persona: CatechistPersona,
        context: CatechistContext,
    ): CatechistTurnResult {
        val clean = LumenGuardrails.sanitizeInput(userText)
        val norm = normalize(clean)

        return when {
            containsAny(norm, "distrai", "foco", "seco", "secura", "vontade de rezar", "travado", "cabeca nao para") ->
                distractionReply(persona)
            containsAny(norm, "cansad", "exaust", "sono", "sem forca", "esgotad", "fim do dia") ->
                fatigueReply(persona, context)
            containsAny(norm, "ansios", "aflit", "medo", "angusti", "preocup", "nervos") ->
                anxietyReply(persona, clean)
            containsAny(norm, "cirurgia", "doent", "enferm", "hospital", "saude", "exame", "mae", "pai") ->
                healthReply(persona, clean)
            containsAny(norm, "confiss", "pecad", "culpa", "perdao", "perdoar", "reconcili") ->
                confessionReply(persona)
            containsAny(norm, "maria", "nossa senhora", "terco", "rosario", "repeti", "ave maria") ->
                marianReply(persona, context)
            containsAny(norm, "sofr", "dor", "luto", "perdi", "morreu", "cruz", "triste") ->
                sufferingReply(persona, clean)
            containsAny(norm, "grat", "agradec", "alegria", "feliz", "graca alcancada") ->
                gratitudeReply(persona, clean)
            context.activeMysteryTitle != null && context.activeMysteryIndex != null ->
                mysteryContextReply(persona, context)
            else ->
                generalReply(persona, clean, context)
        }
    }

    fun generateCustomRosary(
        intentionRaw: String,
        mysterySet: MysterySet,
        mood: LumenMood?,
        persona: CatechistPersona,
    ): CustomRosarySupplement {
        val intention = LumenGuardrails.sanitizeInput(intentionRaw).ifBlank {
            "Pela paz do coração e pela fidelidade no dia de hoje"
        }
        val norm = normalize(intention)
        val shortNote = intention.take(120)
        val prep = when (mood) {
            LumenMood.DRY_UNFOCUSED -> "Vinde, Espírito Santo, mesmo quando o coração parece silencioso e distraído. Sustentai em mim a simples vontade de permanecer convosco."
            LumenMood.ANXIOUS -> "Espírito de paz, aquietai os pensamentos agitados e ajudai-me a entregar cada preocupação nas mãos do Pai."
            LumenMood.TIRED -> "Senhor, recebei o cansaço deste dia como oferta sincera. Que cada conta seja um descanso no Vosso coração."
            LumenMood.GRATEFUL -> "Vinde, Espírito Santo, e transformai minha gratidão em louvor humilde junto a Maria."
            null -> null
        }

        val offering = when (persona) {
            CatechistPersona.BENTO ->
                "Senhor Jesus, unidos ao coração de Maria Santíssima, colocamos diante de Vós esta intenção: $shortNote. Fazei florescer em nossa vida a paz que só Vós podeis dar."
            else ->
                "Senhor Jesus, por mãos de Maria Santíssima, oferecemos este terço por aquilo que trazemos hoje no coração: $shortNote. Acolhei cada Ave-Maria como gesto concreto de confiança em Vossa providência."
        }

        val themePhrase = when {
            containsAny(norm, "cirurgia", "saude", "doent", "hospital", "enferm") -> "confiando a cura e o cuidado às mãos providentes do Pai"
            containsAny(norm, "familia", "filh", "espos", "marid", "mae", "pai", "casa") -> "pedindo proteção, paciência e unidade para a família"
            containsAny(norm, "trabalh", "empreg", "cansac", "estudo", "decis") -> "oferecendo o esforço diário e pedindo sabedoria no caminho"
            containsAny(norm, "grat", "agradec", "graca") -> "em sincera ação de graças pelas maravilhas que Deus realiza"
            else -> "entregando esta intenção ao coração misericordioso de Cristo"
        }

        val concise = mood == LumenMood.DRY_UNFOCUSED || mood == LumenMood.TIRED
        val decades = mysterySet.mysteries.mapIndexed { idx, mystery ->
            val base = MysteryContent.contemplationFor(mysterySet, idx)
            val reflection = buildString {
                append("Ao contemplar ")
                append(mystery.title)
                append(" (")
                append(mystery.scripture)
                append("), traga para diante de Jesus o que você vive hoje, ")
                append(themePhrase)
                append(". ")
                append(base.meaning)
            }
            val fruit = "${mystery.fruit}: viver este fruto hoje, $themePhrase."
            val beads = base.beads.mapIndexed { beadIdx, line ->
                when {
                    beadIdx == 1 -> "Apresentamos a Jesus a nossa intenção: ${shortNote.take(80)}."
                    beadIdx == 9 -> "Com Maria, confiamos tudo ao Pai, $themePhrase."
                    concise -> line.split('.').first().trim().plus(".")
                    else -> line
                }
            }
            CustomRosaryDecade(
                mysteryNumber = idx + 1,
                adaptedReflection = reflection,
                spiritualFruitApplication = fruit,
                beadMeditations = beads,
            )
        }

        return CustomRosarySupplement(
            mysterySet = mysterySet,
            userIntentionSummary = shortNote,
            personalizedOffering = offering,
            preparatoryPrayer = prep,
            suggestEyesClosed = mood?.suggestEyesClosed == true,
            decades = decades,
            personaId = persona.id,
            isOfflineFallback = true,
        )
    }

    /**
     * Checks saved intentions from Meu Jardim and surfaces one quiet recall prompt
     * for an active (non-archived, non-thanksgiving) intention prayed 5 to 14 days ago.
     */
    fun recallIntention(
        intentions: List<Triple<String, String, Long>>, // (id, title, createdAtMillis)
        nowMillis: Long = System.currentTimeMillis(),
    ): MemoryRecallPrompt? {
        val dayMs = 86_400_000L
        val candidate = intentions
            .map { (id, title, created) ->
                val days = ((nowMillis - created) / dayMs).toInt()
                Triple(id, title, days)
            }
            .firstOrNull { (_, _, days) -> days in 5..14 }
            ?: return null

        val whenLabel = if (candidate.third <= 8) "Na semana passada" else "Há ${candidate.third} dias"
        return MemoryRecallPrompt(
            intentionId = candidate.first,
            intentionTitle = candidate.second,
            daysAgo = candidate.third,
            heading = "$whenLabel você colocou em oração: ${candidate.second}.",
            body = "Como está o seu coração hoje? Você pode incluir uma ação de graças ou renovar este pedido na coroa de hoje.",
        )
    }

    private fun distractionReply(persona: CatechistPersona): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "Olha, vou te contar um segredo que alivia muita gente: rezar distraído não é fracasso — é treino de amor.\n\nAté Santa Teresa de Ávila chamava a imaginação de “a louca da casa” correndo pra todo lado. Toda vez que você percebe que voou longe e volta com calma pra Ave-Maria, esse pequeno retorno já vale ouro diante de Deus."
            CatechistPersona.LUCAS ->
                "Fica tranquilo, isso acontece com todo mundo — ainda mais com o ritmo do dia e mil abas abertas na cabeça.\n\nRezar com foco zero e mesmo assim ficar ali é como visitar um amigo mesmo cansado: o valor está em aparecer, não em fazer um discurso perfeito."
            CatechistPersona.CLARA ->
                "Boa notícia: se distração desqualificasse a oração, o Céu estaria meio vazio! São Filipe Néri ria dessas nossas lutas e lembrava que Deus olha o coração que quer ficar, não uma nota de concentração.\n\nO único erro seria ir embora porque não ficou “bonito”. Volta com um sorriso e segue a próxima conta."
            CatechistPersona.BENTO ->
                "Não se inquiete com a mente agitada. São Francisco de Sales ensinava que, se o coração se distrai cem vezes, basta reconduzi-lo a Deus com mansidão cem vezes, sem jamais perder a paz.\n\nDeus não pede uma oração impecável, mas uma presença sincera."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "CIC §2729 · Santa Teresa de Ávila · São Francisco de Sales",
            actions = listOf(CatechistAction.StartQuickDecade(
                label = "Rezar uma dezena sem pressão",
                subtitle = "1º Mistério Doloroso · meditações curtas de um fôlego.",
                mysterySet = MysterySet.SORROWFUL,
                mysteryIndex = 0,
                meditated = true,
            ),),
            suggestedFollowUp = "Como lidar com o cansaço na hora de rezar?",
            isOfflineFallback = true,
        )
    }

    private fun fatigueReply(persona: CatechistPersona, context: CatechistContext): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "Pensa num amigo que chega exausto na sua casa e só senta no sofá do seu lado em silêncio. Você ficaria bravo com ele? Claro que não — Deus muito menos.\n\nSe você só tem 5% de bateria hoje e entrega esses 5% a Ele, você entregou 100% do que tinha. Uma dezena bem calma, até de olhos fechados, já é um presentão."
            CatechistPersona.LUCAS ->
                "Dia pesado, né? Nessas horas a pior armadilha é achar que precisa rezar um terço inteiro em perfeito silêncio ou não rezar nada.\n\nFaz o simples: respira fundo, liga o modo Olhos fechados e entrega o cansaço no primeiro mistério."
            CatechistPersona.CLARA ->
                "Lembra que até Pedro, Tiago e João caíram no sono no Getsêmani — e Jesus continuou amando e confiando neles!\n\nCulpa paralisante nunca vem do Espírito Santo. Descansa em Deus por cinco minutos com uma dezena bem breve."
            CatechistPersona.BENTO ->
                "Ofereça o próprio cansaço como oração. No Horto das Oliveiras, Cristo acolheu o peso do corpo e da alma humana.\n\nPermanecer diante d’Ele sem muitas palavras, repousando em Sua misericórdia, já é verdadeira contemplação."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "Mt 11, 28 · CIC §2728",
            actions = listOf(CatechistAction.StartQuickDecade(
                label = "Rezar uma dezena serena",
                subtitle = "Mistérios ${context.activeMysterySet.label} · ritmo leve para hoje.",
                mysterySet = context.activeMysterySet,
                mysteryIndex = 0,
                meditated = true,
            ),),
            suggestedFollowUp = "Me ajuda a montar um terço pelo que estou vivendo?",
            isOfflineFallback = true,
        )
    }

    private fun anxietyReply(persona: CatechistPersona, clean: String): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "Quando a ansiedade aperta, a mente tenta resolver amanhã com a força que só existe pra hoje. Jesus foi super realista quando disse: “basta a cada dia o seu cuidado”.\n\nNão precisa fingir calma pra rezar: traz a aflição exatamente como ela está e coloca no colo de Maria. A repetição mansa da Ave-Maria ajuda até o corpo a desacelerar."
            CatechistPersona.LUCAS ->
                "Sei como essa sensação de aperto cansa. O bom da oração católica é que você não precisa inventar palavras bonitas quando está ansioso — a Igreja empresta as palavras pra você.\n\nVamos guardar isso como intenção e entregar uma dezena com calma."
            CatechistPersona.CLARA ->
                "Santa Teresa de Ávila tinha um recado direto para dias assim: “Nada te perturbe, nada te espante; quem a Deus tem, nada lhe falta”.\n\nNão é negar o problema — é lembrar que você não está segurando o mundo sozinho nas costas."
            CatechistPersona.BENTO ->
                "Acolha o momento presente sem temer o que ainda não chegou. São Paulo nos lembra de apresentar a Deus todas as nossas preocupações em oração, para que a paz de Deus guarde o coração.\n\nEntreguemos esta aflição ao Senhor no silêncio do terço."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "Fl 4, 6–7 · Mt 6, 34 · Santa Teresa de Ávila",
            actions = listOf(CatechistAction.SaveIntention(
                label = "Guardar esta intenção no Meu Jardim",
                subtitle = "Para oferecer no seu próximo terço.",
                intentionTitle = clean.take(64),
                intentionDescription = "Confiando a ansiedade e pedindo paz ao coração.",
                category = "Família",
            ),),
            suggestedFollowUp = "Qual oração do Tesouro ajuda na angústia?",
            isOfflineFallback = true,
        )
    }

    private fun healthReply(persona: CatechistPersona, clean: String): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "Sinto muito por esse aperto — ver quem a gente ama passando por um momento de saúde delicado mexe com tudo dentro da gente.\n\nNo Evangelho, Jesus nunca passou reto por alguém doente ou por uma família aflita. Vamos colocar esse nome diretamente no seu Caderno de Intenções e rezar pedindo luz para os médicos e paz pra sua casa."
            CatechistPersona.LUCAS ->
                "Tô com você nessa. Quando envolve saúde de quem a gente ama, o coração fica mesmo em alerta o tempo inteiro.\n\nA oração não substitui os médicos — ela pede a Deus que guie as mãos deles e dê força pra vocês atravessarem esse dia."
            CatechistPersona.CLARA ->
                "A Igreja sempre viu Maria como Saúde dos Enfermos — não porque tira mágica e instantaneamente toda cruz, mas porque fica de pé junto de nós quando o medo aparece.\n\nVamos registrar essa intenção para oferecê-la no terço de hoje."
            CatechistPersona.BENTO ->
                "Uni-vos a Maria junto à cruz: ela conhece a dor de velar por quem se ama. Entreguemos esta pessoa ao coração misericordioso de Cristo Médico das almas e dos corpos, pedindo serenidade e cura segundo a vontade do Pai."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "Tg 5, 14–15 · CIC §1503–1505",
            actions = listOf(CatechistAction.SaveIntention(
                label = "Guardar intenção de saúde",
                subtitle = "Salvar em Meu Jardim na categoria Saúde.",
                intentionTitle = clean.take(64),
                intentionDescription = "Pedindo cura, amparo aos médicos e paz à família.",
                category = "Saúde",
            ),),
            suggestedFollowUp = "Quero montar o terço de hoje por essa intenção",
            isOfflineFallback = true,
        )
    }

    private fun confessionReply(persona: CatechistPersona): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "Muita gente trava na Confissão porque imagina um tribunal — quando na verdade ela é um pronto-socorro de misericórdia!\n\nO padre já ouviu de tudo e está ali na pessoa de Cristo pra te devolver a paz, não pra te julgar. E vale lembrar: eu posso te ajudar a preparar o exame de consciência, mas o sacramento acontece mesmo pessoalmente com o sacerdote."
            CatechistPersona.LUCAS ->
                "Se faz tempo ou dá vergonha de ir se confessar, relaxa: começar dizendo “padre, faz tempo e tô nervoso” já quebra o gelo na hora.\n\nAqui no Sertum tem um passo a passo super prático pra te ajudar a preparar o coração antes de ir à paróquia."
            CatechistPersona.CLARA ->
                "São João Maria Vianney dizia que a misericórdia de Deus é uma torrente que arrasta tudo — e a gente fica com medo de molhar a ponta do pé!\n\nPreparar uma boa confissão é olhar a verdade sem drama, confiar no perdão de Cristo e procurar um padre na paróquia."
            CatechistPersona.BENTO ->
                "O Sacramento da Reconciliação é o abraço do Pai ao filho que retorna. Não tema a própria fragilidade: prepare o exame de consciência em paz e procure um sacerdote para receber a absolvição sacramental."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "CIC §1422–1449 · Lc 15, 20",
            actions = listOf(CatechistAction.OpenFormationLesson(
                label = "Ler lição sobre Reconciliação",
                subtitle = "Na aba Catequese · como preparar uma boa confissão.",
                lessonId = "reconciliation",
            ),),
            suggestedFollowUp = "Como fazer um exame de consciência simples?",
            isOfflineFallback = true,
        )
    }

    private fun marianReply(persona: CatechistPersona, context: CatechistContext): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "O Rosário parece repetitivo só por fora — por dentro ele funciona como uma música de fundo que acalma a mente enquanto o coração olha pra Jesus junto com Maria.\n\nSão João Paulo II dizia que rezar o Terço é contemplar o rosto de Cristo com os olhos da Mãe d’Ele. Cada Ave-Maria é como segurar a mão dela enquanto passeamos pelo Evangelho."
            CatechistPersona.LUCAS ->
                "Pensa quando você ama alguém: dizer “eu te amo” várias vezes nunca fica velho. A Ave-Maria é feita quase toda das próprias palavras da Bíblia (Lucas 1)!\n\nSe quiser sentir isso na prática, liga o Terço Meditado conta a conta — cada conta traz uma cena curtinha do Evangelho."
            CatechistPersona.CLARA ->
                "Quem acha o Terço monótono ainda não descobriu o truque: as Ave-Marias são o ritmo dos passos, mas a paisagem são os Mistérios de Cristo!\n\nÉ como caminhar pelas montanhas rezando: o passo se repete, mas a vista muda a cada curva."
            CatechistPersona.BENTO ->
                "No Rosário, Maria nos ensina a guardar e meditar todas as coisas no coração. A repetição mansa da saudação angélica pacifica a alma e nos conduz ao centro, que é sempre Jesus Cristo."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "Rosarium Virginis Mariae §1–3 · Lc 1, 28.42 · CIC §2678",
            actions = listOf(CatechistAction.StartQuickDecade(
                label = "Experimentar uma dezena meditada",
                subtitle = "Mistérios ${context.activeMysterySet.label} · conta a conta.",
                mysterySet = context.activeMysterySet,
                mysteryIndex = 0,
                meditated = true,
            ),),
            suggestedFollowUp = "Por que os católicos pedem a intercessão de Maria?",
            isOfflineFallback = true,
        )
    }

    private fun sufferingReply(persona: CatechistPersona, clean: String): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "Não existe resposta de manual que apague uma dor de verdade — e a fé católica não pede que você finja que está tudo bem.\n\nA resposta de Deus ao sofrimento não foi uma palestra: foi entrar nele conosco na Cruz e abrir um caminho de ressurreição. Se o peso estiver grande demais, conte também com pessoas de confiança e ajuda humana."
            CatechistPersona.LUCAS ->
                "Sinto muito pelo que você está atravessando. Nessas horas, até o próprio Jesus chorou diante do túmulo do amigo Lázaro.\n\nVocê pode trazer essa dor crua pra oração — sem precisar enfeitar nada."
            CatechistPersona.CLARA ->
                "C. S. Lewis dizia que a dor é real e não precisa de frases prontas. O consolo cristão é saber que Deus não assiste de camarote: Ele conhece o gosto das lágrimas por dentro.\n\nVamos oferecer uma prece sincera por você hoje."
            CatechistPersona.BENTO ->
                "Na cruz e na ressurreição, Cristo assumiu toda lágrima humana para que nenhuma dor ficasse abandonada. Permaneça sob o olhar compassivo do Senhor e permita-se ser cuidado também por quem o ama."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "Jo 11, 35 · Rm 8, 28–39 · CIC §309–314",
            actions = listOf(CatechistAction.SaveIntention(
                label = "Colocar esta dor em oração",
                subtitle = "Guardar em Meu Jardim para lembrar no terço.",
                intentionTitle = clean.take(64),
                intentionDescription = "Pedindo consolo, esperança e presença de Cristo.",
                category = "Família",
            ),),
            suggestedFollowUp = "Qual mistério do terço conversa com a dor?",
            isOfflineFallback = true,
        )
    }

    private fun gratitudeReply(persona: CatechistPersona, clean: String): CatechistTurnResult {
        val text = when (persona) {
            CatechistPersona.MATEUS ->
                "Que alegria ouvir isso! A gratidão é um dos gestos mais bonitos da fé — lembra dos dez leprosos no Evangelho, quando só um voltou pra agradecer e Jesus ficou encantado?\n\nQue tal registrar essa graça em Ação de Graças no seu jardim e rezar uma dezena dos Mistérios Gozosos ou Gloriosos celebrando?"
            CatechistPersona.LUCAS ->
                "Coisa boa demais! A gente lembra fácil de pedir quando aperta, e voltar pra agradecer faz o coração crescer muito na fé.\n\nVamos guardar essa ação de graças no Meu Jardim pra nunca esquecer o bem que Deus fez."
            CatechistPersona.CLARA ->
                "Alegria pura! Chesterton dizia que a gratidão é a forma mais alta de pensamento — e Maria respondeu à graça de Deus cantando o Magnificat!\n\nVale demais oferecer uma dezena de louvor hoje."
            CatechistPersona.BENTO ->
                "Bendito seja Deus por esta graça! Como Maria no Magnificat, reconhecer os dons recebidos abre a alma para uma paz ainda mais profunda."
        }
        return CatechistTurnResult(
            replyText = text,
            reference = "Lc 1, 46–55 · 1Ts 5, 18 · CIC §2637",
            actions = listOf(CatechistAction.SaveIntention(
                label = "Guardar em Ação de Graças",
                subtitle = "Registrar no Caderno de Intenções.",
                intentionTitle = clean.take(64),
                intentionDescription = "Em sincera ação de graças ao Senhor.",
                category = "Ação de Graças",
            ),),
            suggestedFollowUp = "Quero rezar o Magnificat ou uma dezena de louvor",
            isOfflineFallback = true,
        )
    }

    private fun mysteryContextReply(persona: CatechistPersona, context: CatechistContext): CatechistTurnResult {
        val idx = (context.activeMysteryIndex ?: 0).coerceIn(0, 4)
        val mystery = context.activeMysterySet.mysteries[idx]
        val contemplation = MysteryContent.contemplationFor(context.activeMysterySet, idx)
        val intro = when (persona) {
            CatechistPersona.MATEUS -> "Esse mistério — ${mystery.title} — é incrível quando a gente olha de perto."
            CatechistPersona.LUCAS -> "Olha que bonito o que acontece em ${mystery.title}:"
            CatechistPersona.CLARA -> "Adoro quando a gente para pra saborear ${mystery.title} sem pressa!"
            CatechistPersona.BENTO -> "Contemplemos com serenidade ${mystery.title}."
        }
        val text = "$intro ${contemplation.meaning}\n\nO fruto espiritual aqui é ${mystery.fruit.lowercase()}: ${contemplation.practice}"
        return CatechistTurnResult(
            replyText = text,
            reference = "${mystery.scripture} · Mistérios ${context.activeMysterySet.label}",
            actions = listOf(CatechistAction.StartQuickDecade(
                label = "Voltar a rezar este mistério",
                subtitle = "${idx + 1}º Mistério · ${mystery.title}",
                mysterySet = context.activeMysterySet,
                mysteryIndex = idx,
                meditated = true,
            ),),
            suggestedFollowUp = "Como viver o fruto desse mistério hoje?",
            isOfflineFallback = true,
        )
    }

    private fun generalReply(
        persona: CatechistPersona,
        clean: String,
        context: CatechistContext,
    ): CatechistTurnResult {
        val intro = when (persona) {
            CatechistPersona.MATEUS -> "Que bom você trazer isso! A vida cristã acontece exatamente aí no meio da realidade concreta — nunca num mundo perfeito imaginário."
            CatechistPersona.LUCAS -> "Obrigado por compartilhar isso comigo. Deus caminha exatamente no meio do que você está vivendo hoje, sem filtro."
            CatechistPersona.CLARA -> "Ótima conversa pra trazer pro jardim! A fé católica tem raízes fundas justamente para iluminar essas perguntas reais."
            CatechistPersona.BENTO -> "Acolho com carinho o que você partilha. Tudo aquilo que apresentamos com sinceridade diante de Deus pode tornar-se lugar de graça."
        }
        val second = "Hoje a Igreja nos convida a contemplar os Mistérios ${context.activeMysterySet.label.lowercase()} (${context.activeMysterySet.invitation.lowercase()}). Podemos transformar isso em uma intenção concreta ou rezar uma dezena meditada juntos."
        return CatechistTurnResult(
            replyText = "$intro\n\n$second",
            reference = "CIC §2559 · Mistérios ${context.activeMysterySet.label}",
            actions = listOf(CatechistAction.StartQuickDecade(
                label = "Rezar uma dezena com isso",
                subtitle = "Mistérios ${context.activeMysterySet.label} · ${context.activeMysterySet.mysteries.first().title}",
                mysterySet = context.activeMysterySet,
                mysteryIndex = 0,
                meditated = true,
            ),),
            suggestedFollowUp = if (clean.length < 40) "Me explica os mistérios de hoje?" else "Guardar isso como intenção no Meu Jardim?",
            isOfflineFallback = true,
        )
    }

    private fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

    private fun containsAny(text: String, vararg terms: String): Boolean =
        terms.any { text.contains(it) }
}

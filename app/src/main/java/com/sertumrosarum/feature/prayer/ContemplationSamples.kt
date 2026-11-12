package com.sertumrosarum.feature.prayer

import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.Mystery
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.PrayerStep

/** The three written layers of a mystery, plus one line for each Ave-Maria when available. */
data class MysteryContemplation(
    val scene: String,
    val meaning: String,
    val practice: String,
    val beads: List<String> = emptyList(),
)

/**
 * Sample copy that lets the Phase 2 screens be seen with real words.
 *
 * Only the first mystery of each set is written out. Every other mystery receives the
 * gentle prompts of [invitation], built from the title, passage, and fruit it already has.
 * The complete dataset (20 mysteries, 200 bead meditations, the full litany) replaces
 * this file; the screens read it only through [contemplationFor], [catecheticalNote],
 * and [LitanySample].
 */
internal object ContemplationSamples {
    private val written = mapOf(
        (MysterySet.JOYFUL to 0) to MysteryContemplation(
            scene = "Em Nazaré, uma aldeia pequena da Galileia, o anjo Gabriel é enviado a uma jovem chamada Maria. Ela se perturba com a saudação, escuta, pergunta e então responde: “Faça-se em mim segundo a tua palavra.”",
            meaning = "Deus não se impõe: pede licença e espera uma resposta livre. No sim de Maria, o Filho eterno se faz homem, e a salvação começa no silêncio de uma casa simples.",
            practice = "Hoje, acolha sem resistência algo que você não escolheu, e diga a Deus um sim pequeno e sincero.",
            beads = listOf(
                "Deus olha para Nazaré, um lugar que ninguém notava.",
                "O anjo entra na casa de Maria.",
                "“Alegra-te, cheia de graça, o Senhor está contigo.”",
                "Maria se perturba e guarda a saudação no coração.",
                "“Não tenhas medo, Maria: encontraste graça diante de Deus.”",
                "Ela dará à luz um filho, e o chamará Jesus.",
                "Maria pergunta com simplicidade: como será isso?",
                "O Espírito Santo virá sobre ela; para Deus nada é impossível.",
                "“Eis aqui a serva do Senhor.”",
                "O Verbo se faz carne, e o anjo se retira.",
            ),
        ),
        (MysterySet.LUMINOUS to 0) to MysteryContemplation(
            scene = "Às margens do Jordão, Jesus entra na fila dos pecadores e pede o batismo a João. Ao sair da água, o céu se abre, o Espírito desce como uma pomba e a voz do Pai se faz ouvir: “Este é o meu Filho amado.”",
            meaning = "Aquele que não tem pecado se coloca ao lado de quem precisa de perdão. No Jordão a Trindade se revela, e no nosso Batismo o Pai diz também a cada um de nós: você é meu filho amado.",
            practice = "Hoje, faça o Sinal da Cruz devagar, lembrando o seu Batismo, e peça ao Espírito Santo que conduza uma decisão do seu dia.",
            beads = listOf(
                "João prega no deserto e prepara o caminho do Senhor.",
                "Jesus vem da Galileia até o Jordão.",
                "Ele entra na fila, no meio dos pecadores.",
                "João hesita: “Eu é que preciso ser batizado por ti.”",
                "Jesus desce às águas do rio.",
                "Ao sair da água, o céu se abre.",
                "O Espírito desce sobre ele como uma pomba.",
                "A voz do Pai: “Este é o meu Filho amado.”",
                "No Batismo, também eu me tornei filho amado do Pai.",
                "Jesus parte do Jordão para começar a sua missão.",
            ),
        ),
        (MysterySet.SORROWFUL to 0) to MysteryContemplation(
            scene = "Depois da ceia, Jesus vai com os discípulos ao Getsêmani, um jardim de oliveiras. Tomado de tristeza, afasta-se um pouco, cai com o rosto por terra e reza, enquanto os amigos adormecem.",
            meaning = "Jesus sente de verdade o peso do medo e da angústia, e os entrega ao Pai: “Não seja como eu quero, mas como tu queres.” Confiar não é deixar de sofrer; é não sofrer sozinho.",
            practice = "Hoje, entregue a Deus uma preocupação pelo nome, e repita devagar: seja feita a vossa vontade.",
            beads = listOf(
                "Jesus entra no jardim com os seus discípulos.",
                "“A minha alma está triste até a morte.”",
                "Ele pede aos amigos que fiquem e vigiem com ele.",
                "Afasta-se um pouco e cai com o rosto por terra.",
                "Reza ao Pai: se é possível, que este cálice passe.",
                "“Não seja como eu quero, mas como tu queres.”",
                "Volta e encontra os amigos dormindo.",
                "Pede que vigiem e rezem, para não caírem em tentação.",
                "Jesus reza pela segunda e pela terceira vez.",
                "Ele se levanta em paz: a hora chegou.",
            ),
        ),
        (MysterySet.GLORIOUS to 0) to MysteryContemplation(
            scene = "No primeiro dia da semana, ainda escuro, Maria Madalena vai ao sepulcro e encontra a pedra removida. Pedro e o outro discípulo correm até lá e veem os panos de linho. Ela fica chorando no jardim, até que o Ressuscitado a chama pelo nome.",
            meaning = "A morte não teve a última palavra. Jesus vive, e a sua vitória é também a nossa: a fé nasce quando nos deixamos chamar pelo nome e reconhecemos a sua voz.",
            practice = "Hoje, leve uma palavra de esperança a alguém que esteja desanimado.",
            beads = listOf(
                "É madrugada, e ainda está escuro.",
                "Maria Madalena vai ao sepulcro.",
                "A pedra foi removida.",
                "Pedro e o outro discípulo correm juntos.",
                "Os panos de linho estão ali; o discípulo vê e crê.",
                "Maria permanece junto ao túmulo, chorando.",
                "“Mulher, por que choras? A quem procuras?”",
                "Jesus a chama pelo nome: “Maria!”",
                "Ela o reconhece e responde: “Mestre!”",
                "“Eu vi o Senhor!”, anuncia aos discípulos.",
            ),
        ),
    )

    /** True for any mystery: an invitation to pray with what the app already knows about it. */
    private fun invitation(mystery: Mystery) = MysteryContemplation(
        scene = "Leia ${mystery.scripture} sem pressa e deixe a cena se formar diante de você: o lugar, os rostos, as palavras.",
        meaning = "Pergunte a Maria o que este mistério revela de Jesus, e o que ele diz à sua vida hoje.",
        practice = "Peça esta graça ao longo da dezena e escolha um gesto simples para vivê-la hoje.",
    )

    fun contemplationFor(set: MysterySet, decade: Int): MysteryContemplation =
        written[set to decade] ?: invitation(set.mysteries[decade])

    /** A short reason for each prayer, shown by the catechetical guide the first time it appears in a group. */
    fun catecheticalNote(step: PrayerStep): String? = when (step.prayer) {
        null -> "Anunciar o mistério é escolher a cena do Evangelho que vamos contemplar com Maria durante a dezena."
        FixedPrayer.SIGN_OF_CROSS -> "Começamos em nome da Trindade. Traçar a cruz sobre nós é lembrar que pertencemos a Deus e que foi pela cruz que Jesus nos salvou."
        FixedPrayer.OFFERING -> "Antes de rezar, dizemos a Deus por quem e por que rezamos. A oração ganha direção quando é oferecida."
        FixedPrayer.CREED -> "O Creio reúne o essencial da fé que recebemos dos apóstolos. Rezá-lo é dizer: é nesta fé que eu quero rezar."
        FixedPrayer.OUR_FATHER -> "É a oração que o próprio Jesus ensinou (Mt 6, 9–13). Ela abre cada dezena, porque tudo começa no Pai."
        FixedPrayer.HAIL_MARY -> when {
            (step.beadIndex ?: 0) > 0 -> null
            step.decadeIndex == null -> "As três primeiras Ave-Marias pedem as virtudes que sustentam toda a oração: a fé, a esperança e a caridade."
            else -> "As primeiras palavras vêm do Evangelho: a saudação do anjo e a de Isabel (Lc 1, 28.42). Enquanto os lábios repetem, o coração contempla o mistério."
        }
        FixedPrayer.GLORY_BE -> "Cada dezena termina em louvor à Trindade. Tudo o que contemplamos volta para a glória de Deus."
        FixedPrayer.FATIMA -> "Nossa Senhora ensinou esta oração aos pastorinhos de Fátima, em 1917, para rezarmos pelos que mais precisam de misericórdia."
        FixedPrayer.THANKSGIVING -> "Agradecemos a Maria por rezar conosco e nos colocamos sob a sua proteção."
        FixedPrayer.HAIL_HOLY_QUEEN -> "Uma das orações marianas mais antigas da Igreja. Nela pedimos a Maria que nos mostre Jesus."
    }
}

/** A few invocations to show how the litany is laid out; the full Ladainha Lauretana arrives with its guided prayer. */
internal val LitanySample = listOf(
    "Senhor, tende piedade de nós." to "Senhor, tende piedade de nós.",
    "Santa Maria," to "rogai por nós.",
    "Rosa mística," to "rogai por nós.",
    "Rainha da paz," to "rogai por nós.",
)

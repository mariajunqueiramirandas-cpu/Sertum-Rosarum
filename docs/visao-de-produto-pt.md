# 🌹 Sertum Rosarum: O Santo Terço
**Documento de Visão de Produto, Identidade Visual e Arquitetura de Experiência**

---

## 1. Visão Geral e Propósito
O **Sertum Rosarum** (*"Coroa de Rosas"*, expressão original em latim que deu origem à palavra Rosário) não é apenas um contador digital de Ave-Marias, mas um **santuário silencioso no bolso** e uma **escola de oração contemplativa**.

O aplicativo resolve o maior desafio de quem reza o terço — seja o iniciante que ainda não sabe a sequência das orações, seja o fiel experiente que luta contra as distrações e o "piloto automático" — unindo um design sacro minimalista, explicações teológicas em camadas e um motor de Inteligência Artificial (**Lumen**) que conecta os mistérios da vida de Cristo diretamente às dores, dúvidas e intenções diárias do usuário.

---

## 2. Identidade da Marca (Branding)

### Nome e Adaptações
* **Nome Oficial (Loja e Splash Screen):** `Sertum Rosarum: O Santo Terço`
* **Nome Curto (Ícone no Android):** `Sertum` (evita cortes de texto na tela inicial do sistema).
* **Nome do Motor de IA:** `Lumen` (*Luz para contemplação*).

### Slogans (Taglines)
* **Principal:** *"Teça a sua coroa diária de contemplação."*
* **Secundários:** *"Uma coroa de rosas a cada oração."* | *"Silêncio, ritmo e devoção."*

### Conceito do Ícone
Sobre um fundo **Azul Vigília** (`#0F172A`), uma rosa geométrica estilizada em traço único dourado (`#C59B27`), vista de cima — inspirada nas **rosáceas das catedrais góticas** — cercada por um anel de 10 pequenos pontos dourados que simbolizam tanto uma coroa quanto uma dezena do terço.

---

## 3. Direção Visual: "Santuário Silencioso"
Longe de interfaces carregadas ou frias, a estética **Sacra Contemporânea** busca o conforto visual da luz de velas, do papel pergaminho, da madeira e dos vitrais clássicos.

### Paleta de Cores (Tokens Principais)
| Elemento | Modo Claro (Dia) | Modo Escuro (Vigília) | Função na Interface |
| :--- | :--- | :--- | :--- |
| **Fundo Principal** | Pergaminho Quente (`#F9F6F0`) | Azul Vigília (`#0F172A`) | Conforto absoluto de leitura sem cansar a vista. |
| **Superfícies / Cartões** | Linho Puro (`#EFECE6`) | Ardósia Noturna (`#1E293B`) | Cartões de mistérios, gavetas explicativas e menus. |
| **Texto Principal** | Tinta Escura (`#1C1917`) | Marfim Suave (`#F8FAFC`) | Orações, textos bíblicos e leitura longa. |
| **Destaque Primário** | Ouro Velho (`#C59B27`) | Ouro Luminoso (`#EAB308`) | Conta ativa, rosácea de progresso e ícones sagrados. |
| **Destaque Secundário** | Rubi Litúrgico (`#881337`) | Rosa Antigo (`#FDA4AF`) | Botão de início de oração e contas "florescidas". |
| **Cor Institucional** | Azul Mariano (`#1E3A8A`) | Azul Celeste (`#93C5FD`) | Títulos solenes, cabeçalhos e referências a Nossa Senhora. |

### Atmosfera Dinâmica por Mistério
O cabeçalho do aplicativo adapta sutilmente sua tonalidade conforme os mistérios do dia:
* **Mistérios Gozosos (Seg / Sáb):** Tons de Âmbar e Dourado Suave (*Alegria da Encarnação*).
* **Mistérios Luminosos (Qui):** Dourado Solar e Branco (*Luz da Vida Pública de Cristo*).
* **Mistérios Dolorosos (Ter / Sex):** Violeta Profundo e Vinho (*Paixão e Sacrifício*).
* **Mistérios Gloriosos (Qua / Dom):** Azul Celeste e Ouro (*Ressurreição e Glória*).

### Tipografia
* **Logotipo e Títulos Solenes:** `Cinzel` ou `Cormorant Garamond` (Serifada clássica, caixa-alta com espaçamento amplo para o título `SERTUM`).
* **Textos de Oração e Meditações:** `Lora` ou `Cormorant Garamond` (Leitura editorial, acolhedora e contemplativa).
* **Interface, Botões e Contadores:** `Plus Jakarta Sans` ou `Inter` (Sem-serifa limpa para máxima legibilidade em tamanhos menores).

---

## 4. Experiência de Oração e Mecânicas de Uso

### A Metáfora da "Coroa de Rosas"
1. **Contas que Florescem:** Na dezena atual, as 10 Ave-Marias são exibidas como um arco fluido de pequenos círculos vazados. A cada Ave-Maria rezada, a conta se preenche em tom dourado/rubi e desabrocha sutilmente como uma pequena rosácea minimalista.
2. **O Arco dos 5 Mistérios:** No topo da tela, um anel dividido em 5 partes se ilumina gradualmente a cada dezena concluída, fechando a coroa completa ao chegar na *Salve Rainha*.
3. **A Entrega da Coroa (Tela Final):** Ao concluir o terço, a coroa completa brilha na tela com a mensagem *"Sua Coroa de Rosas foi entregue"*, exibindo as intenções colocadas pelo usuário no início da oração.

### Modo "Olhos Fechados" (Navegação Tátil)
* **Área de Toque Ampla:** Durante as dezenas, tocar ou deslizar em qualquer parte da metade inferior da tela avança uma conta, sem exigir que o usuário olhe para o celular.
* **Feedback Háptico (Vibração Diferenciada):**
  * *Ave-Maria:* Um pulso tátil curto e suave.
  * *Pai-Nosso / Glória / Troca de Mistério:* Uma vibração dupla mais encorpada, avisando pelo tato que a dezena terminou.

---

## 5. Arquitetura Educacional e Explicativa

O **Sertum Rosarum** ensina a rezar enquanto conduz a oração, utilizando **camadas progressivas** para nunca poluir a tela principal.

### Os Mistérios em 4 Dimensões
Antes de cada Pai-Nosso, o cartão do mistério apresenta:
1. **A Cena Bíblica (Narrativa):** O trecho do Evangelho acompanhado de uma contextualização histórica e sensorial da cena.
2. **O Sentido da Contemplação:** Explicação teológica acessível sobre o que aquele acontecimento significa para a nossa fé.
3. **O Fruto do Mistério:** A virtude específica daquela dezena (ex: *Humildade*, *Perseverança*, *Confiança em Deus*) traduzida em uma atitude prática para o dia.
4. **Terço Meditado Conta a Conta (Opcional):** Quando ativado, exibe uma pequena frase contemplativa diferente em cada uma das 10 Ave-Marias, guiando a imaginação passo a passo pela cena bíblica.

### O Tesouro de Orações ("Toque para Entender")
Um acervo categorizado por momento de vida (*Ao acordar, Antes de dormir, Na angústia, Ação de graças, Pós-Comunhão*) e devoções tradicionais:
* **Anatomia da Oração:** Em orações como o *Credo*, *Salve Rainha* ou *Anima Christi*, o usuário pode tocar em termos marcados (ex: *"Vale de lágrimas"*, *"Consubstancial ao Pai"*) para abrir uma gaveta inferior (*bottom sheet*) explicando o significado bíblico e histórico da expressão.
* **Modo Bilingue (Português / Latim):** Alternância instantânea ou visão lado a lado das grandes orações da Igreja (*Pater Noster, Ave Maria, Gloria Patri, Salve Regina, Sub Tuum Praesidium, Regina Caeli, Angelus*), explicando a tradução literal dos termos em latim.

### Roteiros de Oração Prontos
* **Roteiro Catequético (Iniciante):** Explica o porquê de cada etapa (Sinal da Cruz, Oferecimento, as 3 Ave-Marias pelas virtudes da Fé, Esperança e Caridade).
* **Roteiro Tradicional Completo:** Inclui todas as jaculatórias (*Ó meu Jesus*, *Maria, Mãe da Graça*), *Infinitas Graças*, *Salve Rainha* e a **Ladainha Lauretana**.
* **Outras Coroas Devocionais:** Roteiros estruturados para o *Terço da Divina Misericórdia*, *Terço de São José*, *Coroa das Lágrimas* e *Coroa Franciscana*.

---

## 6. O Agente do Santo Terço ("Lumen")
O diferencial tecnológico do **Sertum Rosarum** é um agente de IA especialista em espiritualidade católica, invisivelmente integrado à interface (sem parecer um robô de chat comum).

### Funcionalidades do Agente Lumen
1. **Geração de Roteiros Sob Medida (Intenção Viva):**
   * O usuário compartilha livremente o que está vivendo antes de começar (ex: *"Estou aflito com uma cirurgia da minha mãe e cansado no trabalho"*).
   * O agente gera instantaneamente:
     * Um **Oferecimento Inicial personalizado**, colocando aquelas intenções em linguagem orante e solene.
     * **Reflexões adaptadas nos 5 Mistérios do dia**, iluminando a dor ou gratidão do usuário através da vida de Cristo.
     * **As 10 meditações curtas de cada dezena** conectando a cena do Evangelho com a graça que o usuário precisa naquele dia.
2. **Catequista Contextual ("Me explique este Mistério"):**
   * Dentro de qualquer mistério ou oração, o usuário pode pedir ao Lumen explicações sob medida:
     * *"Explique de forma simples para quem está começando."*
     * *"Qual o contexto histórico e judaico desta passagem?"*
     * *"O que os Santos (São Luís de Montfort, Santo Tomás de Aquino, Santa Teresa d'Ávila) ensinam sobre este mistério?"*
3. **Curadoria por Estado de Espírito:**
   * O usuário diz como se sente (*"Estou seco espiritualmente e sem foco"*), e o Lumen sugere uma breve oração preparatória ao Espírito Santo e ajusta o ritmo do terço para frases mais curtas e diretas.
4. **Memória Contemplativa (Diário de Intenções):**
   * O agente recorda intenções anteriores e sugere suavemente retornos de gratidão (*"Na semana passada você rezou pela cirurgia de sua mãe; deseja incluir uma ação de graças na coroa de hoje?"*).

### Diretrizes de Segurança e Fidelidade Doutrinária (Guardrails)
* **Orações Fixas Imutáveis:** O agente **nunca** altera o texto oficial do *Pai-Nosso*, *Ave-Maria*, *Glória*, *Credo* ou *Salve Rainha*. Esses textos vêm direto do banco de dados fixo do app.
* **Ancoragem Teológica:** Todas as meditações e explicações geradas pelo Lumen seguem estritamente o Catecismo da Igreja Católica, as Sagradas Escrituras e documentos pontifícios como a Carta Apostólica *Rosarium Virginis Mariae*.
* **Saída Estruturada na UI:** O agente devolve os textos formatados diretamente dentro dos cartões visuais do aplicativo, mantendo a estética de "Santuário Silencioso".

---

## 7. Arquitetura de Telas e Navegação
O aplicativo organiza-se em **4 abas principais** na barra inferior + a **Tela Imersiva de Oração**:

1. **Início (O Santuário do Dia):**
   * Saudação litúrgica, indicação do Mistério do Dia e botão principal de destaque: **"Tecer a Coroa de Hoje"**.
   * Campo rápido do **Lumen**: *"O que você traz no coração para este terço?"* (para gerar o roteiro personalizado do dia).
2. **Roteiros (Trilhas e Terços):**
   * Escolha entre Terço Tradicional, Terço Iniciante Explicado, Terço Meditado, Terços Temáticos (Pela Família, Pelos Enfermos, Pela Paz) e outras Coroas Devocionais.
3. **Tesouro (Orações e Formação):**
   * Biblioteca de orações com os recursos *"Toque para Entender"* e modo *Português / Latim*, além do guia completo de estudo dos 20 Mistérios do Rosário.
4. **Meu Jardim (Diário e Intenções):**
   * Histórico visual das "Coroas de Rosas" concluídas (sem gamificação ansiosa, focado em constância espiritual) e caderno de intenções pessoais e familiares.
5. **Tela Imersiva de Oração (Full Screen):**
   * Sem barras de navegação inferiores; exibe o arco dos 5 mistérios no topo, o texto da oração/meditação ao centro, a coroa das 10 contas florescendo e o botão discreto do **Lumen** (*"Compreender este mistério"*) no canto superior.

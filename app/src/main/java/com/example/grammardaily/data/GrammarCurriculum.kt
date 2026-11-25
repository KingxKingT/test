package com.example.grammardaily.data

object GrammarCurriculum {

    val topicsByLevel: Map<Level, List<Topic>> = buildMap {
        put(Level.A1, listOf(
            Topic(
                id = "A1:be",
                title = "Verb \u201Cto be\u201D (am / is / are)",
                explanation = "The verb \u201Cto be\u201D tells who or what someone is. Use \u201Cam\u201D with I, \u201Cis\u201D with he/she/it and singular nouns, and \u201Care\u201D with you/we/they and plural nouns.",
                examples = listOf(
                    "I am a student.",
                    "She is my sister.",
                    "They are at home."
                ),
                tip = "For questions, just flip it: \u201CYou are late.\u201D \u2192 \u201CAre you late?\u201D"
            ),
            Topic(
                id = "A1:present-simple",
                title = "Present simple for habits and facts",
                explanation = "Use the base verb for I/you/we/they, and add -s or -es for he/she/it. \u201CDon't\u201D and \u201Cdoesn't\u201D make negatives, \u201Cdo/does\u201D makes questions.",
                examples = listOf(
                    "I play tennis on Sundays.",
                    "He works in a bank.",
                    "Do you like coffee?"
                ),
                tip = "Never add two -s endings: \u201CHe doesn't works\u201D is wrong, say \u201CHe doesn't work\u201D."
            ),
            Topic(
                id = "A1:articles",
                title = "a, an and the",
                explanation = "Use \u201Ca\u201D before a consonant sound and \u201Can\u201D before a vowel sound for one of many. Use \u201Cthe\u201D for something specific that both speakers know.",
                examples = listOf(
                    "I have a dog and a cat.",
                    "She is an engineer.",
                    "The sun is bright today."
                ),
                tip = "\u201CAn hour\u201D is correct because the h is silent \u2014 it is about the sound, not the letter."
            ),
            Topic(
                id = "A1:plurals-there",
                title = "Plurals and there is / there are",
                explanation = "Most nouns form plurals with -s, but some change (child \u2192 children, man \u2192 men). \u201CThere is\u201D introduces one thing, \u201Cthere are\u201D introduces many.",
                examples = listOf(
                    "I have two sisters.",
                    "There is a park near my house.",
                    "There are three cars in the street."
                ),
                tip = "For questions use \u201CIs there\u2026?\u201D or \u201CAre there\u2026?\u201D, and for negatives \u201Cthere isn't / there aren't\u201D."
            )
        ))
        put(Level.A2, listOf(
            Topic(
                id = "A2:present-continuous",
                title = "Present continuous (am/is/are + -ing)",
                explanation = "Use am/is/are plus the verb with -ing for actions happening right now or around this time.",
                examples = listOf(
                    "I am reading a great book.",
                    "Look! It is raining.",
                    "They aren't sleeping; they are studying."
                ),
                tip = "Verbs like know, like, want and need are almost never continuous: say \u201CI know\u201D, not \u201CI am knowing\u201D."
            ),
            Topic(
                id = "A2:past-simple",
                title = "Past simple",
                explanation = "For finished actions in the past, use the verb + -ed for regular verbs, or an irregular form like go \u2192 went. Questions and negatives use \u201Cdid\u201D with the base verb.",
                examples = listOf(
                    "We watched a film yesterday.",
                    "I went to London in 2020.",
                    "Did you call her last night?"
                ),
                tip = "After \u201Cdid\u201D, always use the base form: \u201CDid you went\u201D is wrong, say \u201CDid you go\u201D."
            ),
            Topic(
                id = "A2:comparatives",
                title = "Comparatives and superlatives",
                explanation = "Short adjectives take -er/-est with than/the (tall, taller, the tallest). Longer adjectives take more/most (expensive, more expensive, the most expensive).",
                examples = listOf(
                    "This car is faster than that one.",
                    "She is the tallest girl in the class.",
                    "This phone is more expensive than mine."
                ),
                tip = "Never mix the two forms: \u201Cmore taller\u201D and \u201Cmost fastest\u201D are wrong."
            ),
            Topic(
                id = "A2:going-to",
                title = "Future with going to",
                explanation = "Use am/is/are + going to + base verb for plans and intentions, and for things you can see are about to happen.",
                examples = listOf(
                    "I'm going to visit my grandma tomorrow.",
                    "Look at those clouds \u2014 it's going to rain.",
                    "We are going to paint the kitchen next week."
                ),
                tip = "Questions flip \u201Cbe\u201D: \u201CAre you going to come tonight?\u201D"
            )
        ))
        put(Level.B1, listOf(
            Topic(
                id = "B1:present-perfect",
                title = "Present perfect (have/has + participle)",
                explanation = "Use have/has plus the third form of the verb for experiences (ever, never), unfinished time (since, for, this week) and recent news with present results.",
                examples = listOf(
                    "I have never eaten sushi.",
                    "She has lived here since 2019.",
                    "I've lost my keys \u2014 I can't get in."
                ),
                tip = "Don't use present perfect with finished past times: say \u201CI saw him yesterday\u201D, not \u201CI have seen him yesterday\u201D."
            ),
            Topic(
                id = "B1:gerunds-infinitives",
                title = "Gerunds and infinitives",
                explanation = "After verbs like enjoy, avoid, finish, mind and prepositions, use the verb + -ing. After verbs like want, decide, hope, plan and need, use to + verb.",
                examples = listOf(
                    "I enjoy watching old films.",
                    "She decided to buy a new car.",
                    "He's very good at cooking."
                ),
                tip = "Learn them in pairs: \u201Cwant to go\u201D but \u201Cenjoy going\u201D \u2014 there is almost no rule, just memory."
            ),
            Topic(
                id = "B1:conditionals-1-2",
                title = "First and second conditionals",
                explanation = "First conditional (If + present, will) talks about real future possibilities. Second conditional (If + past, would) talks about unlikely or imaginary situations.",
                examples = listOf(
                    "If it rains tomorrow, we will stay home.",
                    "If I had a lot of money, I would travel the world.",
                    "If she studies hard, she will pass the exam."
                ),
                tip = "Never use will/would directly after if: \u201CIf it will rain\u201D is wrong in this meaning."
            ),
            Topic(
                id = "B1:modals",
                title = "must, have to and should",
                explanation = "\u201CMust\u201D expresses a strong personal obligation, \u201Chave to\u201D an external rule, and \u201Cshould\u201D friendly advice. \u201CMustn't\u201D forbids, while \u201Cdon't have to\u201D means it is not necessary.",
                examples = listOf(
                    "You should see a doctor about that cough.",
                    "Students have to wear a uniform here.",
                    "It's a secret \u2014 you mustn't tell anyone."
                ),
                tip = "\u201CYou mustn't come\u201D = it is forbidden. \u201CYou don't have to come\u201D = it's optional. Completely different meanings."
            )
        ))
        put(Level.B2, listOf(
            Topic(
                id = "B2:past-narrative",
                title = "Telling stories: past simple, continuous and perfect",
                explanation = "Use past continuous for background actions, past simple for the events that move the story, and past perfect for actions that happened earlier than the events.",
                examples = listOf(
                    "I was cooking dinner when the phone rang.",
                    "By the time we arrived, the film had already finished.",
                    "She was walking home when it started to snow."
                ),
                tip = "The past continuous sets the scene; the past simple interrupts it and pushes the story forward."
            ),
            Topic(
                id = "B2:reported-speech",
                title = "Reported speech",
                explanation = "When you report what someone said, move the tense one step into the past (am \u2192 was, will \u2192 would, can \u2192 could) and change pronouns and time words.",
                examples = listOf(
                    "\u201CI am tired,\u201D he said. \u2192 He said he was tired.",
                    "\u201CI will call you.\u201D \u2192 She said she would call me.",
                    "\u201CDon't open it!\u201D \u2192 He told me not to open it."
                ),
                tip = "Have to often replaces must when reporting: \u201CI must go\u201D \u2192 \u201CHe said he had to go.\u201D"
            ),
            Topic(
                id = "B2:passive",
                title = "The passive voice (be + participle)",
                explanation = "In the passive, the object of the action becomes the subject: be in the right tense + third form of the verb. You mention the doer with by only when it matters.",
                examples = listOf(
                    "The letter was sent yesterday.",
                    "English is spoken all over the world.",
                    "My car is being repaired right now."
                ),
                tip = "This car was made in Germany\u201D \u2014 use the passive when who did it is obvious or unknown."
            ),
            Topic(
                id = "B2:relative-clauses",
                title = "Relative clauses (who, which, that, whose, where)",
                explanation = "Relative clauses add information to a noun. Use who for people, which for things, that for both, whose for possession and where for places. The pronoun can be dropped when it is the object.",
                examples = listOf(
                    "The woman who lives next door is a doctor.",
                    "This is the book (that) I told you about.",
                    "The house whose roof is red belongs to my uncle."
                ),
                tip = "You can drop who/which/that only when it is followed by a subject: \u201Cthe film (that) I saw\u201D but not \u201Cthe film saw me\u201D."
            )
        ))
        put(Level.C1, listOf(
            Topic(
                id = "C1:inversion",
                title = "Inversion after negative adverbials",
                explanation = "Start the sentence with a negative or limiting adverbial (Never, Rarely, Hardly, Not only, No sooner) and the word order becomes question-like without a question mark: auxiliary + subject + verb.",
                examples = listOf(
                    "Never have I seen such a beautiful city.",
                    "Hardly had I arrived home when it started to rain.",
                    "Not only was he late, he also forgot the tickets."
                ),
                tip = "Only invert after the fronted adverbial. \u201CI have never seen\u2026\u201D is neutral; \u201CNever have I seen\u2026\u201D is emphatic and formal."
            ),
            Topic(
                id = "C1:third-mixed",
                title = "Third and mixed conditionals",
                explanation = "Third conditional (If + had + participle, would have + participle) regrets the past. Mixed conditionals connect a past cause with a present result (If I had studied, I would be rich now).",
                examples = listOf(
                    "If I had studied harder, I would have passed.",
                    "Had I known, I would have come earlier.",
                    "If she had saved money then, she would be comfortable now."
                ),
                tip = "Check the time of each half: past condition + present result needs no \u201Chave\u201D in the main clause."
            ),
            Topic(
                id = "C1:cleft",
                title = "Cleft sentences for emphasis",
                explanation = "Cleft sentences split information to highlight one part, using patterns like \u201CWhat\u2026 is/was\u2026\u201D, \u201CIt was\u2026 that\u2026\u201D and \u201CThe reason why\u2026 is that\u2026\u201D.",
                examples = listOf(
                    "What I want is a quiet weekend.",
                    "It was money that motivated him, not passion.",
                    "The reason why she left was the constant noise."
                ),
                tip = "Clefts sound more natural in speech and writing than simply putting stress on the word \u2014 English often moves the emphasis into structure."
            ),
            Topic(
                id = "C1:participle-clauses",
                title = "Participle clauses (-ing, -ed, having + participle)",
                explanation = "Participle clauses shorten clauses with the same subject: present participles for active actions, past participles for passive meaning, and \u201Chaving + participle\u201D for earlier actions.",
                examples = listOf(
                    "Having missed the bus, she ran to the stop.",
                    "The man standing by the window is my uncle.",
                    "Built in 1990, the bridge is now under repair."
                ),
                tip = "The participle's subject must match the main clause subject, otherwise you get the classic dangling error."
            )
        ))
    }

    val questionBank: List<QuizQuestion> = listOf(
        // A1:be
        QuizQuestion("A1:be", "___ your brother at school?", listOf("Is", "Are", "Am"), 0, "\u201CBrother\u201D is he, so we use \u201Cis\u201D."),
        QuizQuestion("A1:be", "I ___ from Brazil.", listOf("am", "is", "are"), 0, "\u201CI\u201D always pairs with \u201Cam\u201D."),
        QuizQuestion("A1:be", "We ___ very hungry after the trip.", listOf("are", "is", "am"), 0, "\u201CWe\u201D pairs with \u201Care\u201D."),
        // A1:present-simple
        QuizQuestion("A1:present-simple", "She ___ tea every morning.", listOf("drinks", "drink", "drinking"), 0, "He/she/it needs the -s ending in the present simple."),
        QuizQuestion("A1:present-simple", "___ they live in London?", listOf("Do", "Does", "Are"), 0, "Questions with they use \u201CDo\u201D."),
        QuizQuestion("A1:present-simple", "Tom ___ football on Fridays.", listOf("doesn't play", "don't play", "doesn't plays"), 0, "After \u201Cdoesn't\u201D we use the base form."),
        // A1:articles
        QuizQuestion("A1:articles", "I ate ___ apple this morning.", listOf("an", "a", "the"), 0, "\u201CApple\u201D starts with a vowel sound, so \u201Can\u201D."),
        QuizQuestion("A1:articles", "___ moon looked huge last night.", listOf("The", "A", "An"), 0, "\u201CThe moon\u201D \u2014 there is only one, so \u201Cthe\u201D."),
        QuizQuestion("A1:articles", "He is ___ very good teacher.", listOf("a", "an", "the"), 0, "\u201CVery\u201D starts with a consonant sound, so \u201Ca\u201D."),
        // A1:plurals-there
        QuizQuestion("A1:plurals-there", "There ___ two apples on the table.", listOf("are", "is", "have"), 0, "\u201CTwo apples\u201D is plural, so \u201Cthere are\u201D."),
        QuizQuestion("A1:plurals-there", "The plural of \u201Cchild\u201D is ___.", listOf("children", "childs", "childrens"), 0, "\u201CChild\u201D is irregular: child \u2192 children."),
        QuizQuestion("A1:plurals-there", "There ___ a big park near my house.", listOf("is", "are", "be"), 0, "\u201CA big park\u201D is singular, so \u201Cthere is\u201D."),
        // A2:present-continuous
        QuizQuestion("A2:present-continuous", "I ___ a fantastic book at the moment.", listOf("am reading", "read", "reading"), 0, "At the moment \u2192 am/is/are + -ing."),
        QuizQuestion("A2:present-continuous", "Look outside! It ___ hard.", listOf("is raining", "rains", "rain"), 0, "Happening right now \u2192 present continuous."),
        QuizQuestion("A2:present-continuous", "Be quiet! The baby ___.", listOf("is sleeping", "sleeps", "sleep"), 0, "An action in progress now \u2192 \u201Cis sleeping\u201D."),
        // A2:past-simple
        QuizQuestion("A2:past-simple", "Yesterday we ___ to the cinema.", listOf("went", "go", "goed"), 0, "\u201CGo\u201D is irregular: go \u2192 went."),
        QuizQuestion("A2:past-simple", "I ___ the book last night.", listOf("finished", "finish", "did finished"), 0, "Regular verb: add -ed for finished past actions."),
        QuizQuestion("A2:past-simple", "___ you call me yesterday?", listOf("Did", "Do", "Was"), 0, "Past questions use \u201Cdid\u201D + base verb."),
        // A2:comparatives
        QuizQuestion("A2:comparatives", "This car is ___ than that one.", listOf("faster", "more fast", "fastest"), 0, "Short adjective \u2192 -er + than."),
        QuizQuestion("A2:comparatives", "She is ___ student in the class.", listOf("the best", "the better", "the most good"), 0, "\u201CGood\u201D is irregular: good \u2192 better \u2192 the best."),
        QuizQuestion("A2:comparatives", "This bag is ___ than mine.", listOf("more expensive", "expensiver", "most expensive"), 0, "Long adjective \u2192 \u201Cmore\u201D + adjective + than."),
        // A2:going-to
        QuizQuestion("A2:going-to", "We ___ Rome next summer.", listOf("are going to visit", "will to visit", "going visit"), 0, "Plan for the future \u2192 be + going to + base verb."),
        QuizQuestion("A2:going-to", "Careful! That box ___ fall.", listOf("is going to", "goes to", "is going"), 0, "Something about to happen \u2192 \u201Cis going to\u201D."),
        QuizQuestion("A2:going-to", "What ___ you ___ do this evening?", listOf("are / going to", "will / going", "are / go"), 0, "Questions: be + subject + going to."),
        // B1:present-perfect
        QuizQuestion("B1:present-perfect", "I ___ never ___ sushi before.", listOf("have / eaten", "have / ate", "has / eaten"), 0, "Experience with \u201Cnever\u201D \u2192 have + third form (eaten)."),
        QuizQuestion("B1:present-perfect", "She ___ in Madrid since 2019.", listOf("has lived", "lives", "lived"), 0, "\u201CSince + past time\u201D with an unfinished situation \u2192 present perfect."),
        QuizQuestion("B1:present-perfect", "___ you ever ___ to Japan?", listOf("Have / been", "Did / be", "Have / be"), 0, "Ever + experience \u2192 \u201CHave you ever been\u2026?\u201D."),
        // B1:gerunds-infinitives
        QuizQuestion("B1:gerunds-infinitives", "I really enjoy ___ old films.", listOf("watching", "to watch", "watch"), 0, "\u201CEnjoy\u201D is always followed by the -ing form."),
        QuizQuestion("B1:gerunds-infinitives", "She decided ___ a new laptop.", listOf("to buy", "buying", "buy"), 0, "\u201CDecide\u201D is followed by to + verb."),
        QuizQuestion("B1:gerunds-infinitives", "After \u201Cgood at\u201D we use ___.", listOf("verb + -ing", "to + verb", "base verb"), 0, "After prepositions (at, in, of) we use the -ing form."),
        // B1:conditionals-1-2
        QuizQuestion("B1:conditionals-1-2", "If it rains tomorrow, we ___ stay home.", listOf("will", "would", "would have"), 0, "Real future possibility \u2192 first conditional with will."),
        QuizQuestion("B1:conditionals-1-2", "If I ___ a million euros, I would stop working.", listOf("had", "have", "will have"), 0, "Imaginary situation \u2192 If + past simple, would."),
        QuizQuestion("B1:conditionals-1-2", "If she ___ harder, she'll pass easily.", listOf("studies", "will study", "studied"), 0, "After \u201Cif\u201D we use the present, never \u201Cwill\u201D."),
        // B1:modals
        QuizQuestion("B1:modals", "That cough sounds bad. You ___ see a doctor.", listOf("should", "mustn't", "don't have to"), 0, "Friendly advice \u2192 \u201Cshould\u201D."),
        QuizQuestion("B1:modals", "At this school, students ___ wear a uniform.", listOf("have to", "shouldn't", "can"), 0, "External rule \u2192 \u201Chave to\u201D."),
        QuizQuestion("B1:modals", "It's a secret! You ___ tell anybody.", listOf("mustn't", "don't have to", "shouldn't have to"), 0, "Strong prohibition \u2192 \u201Cmustn't\u201D."),
        // B2:past-narrative
        QuizQuestion("B2:past-narrative", "I ___ dinner when the phone rang.", listOf("was cooking", "cooked", "had cooked"), 0, "Background action in progress \u2192 past continuous."),
        QuizQuestion("B2:past-narrative", "By the time we arrived, the film ___.", listOf("had already finished", "already finished", "was already finishing"), 0, "Earlier than another past event \u2192 past perfect."),
        QuizQuestion("B2:past-narrative", "She was walking home when it ___ to snow.", listOf("started", "was starting", "had started"), 0, "The event that interrupts the background \u2192 past simple."),
        // B2:reported-speech
        QuizQuestion("B2:reported-speech", "\u201CI am tired,\u201D he said. \u2192 He said he ___ tired.", listOf("was", "is", "has been"), 0, "Present in direct speech moves back to past."),
        QuizQuestion("B2:reported-speech", "\u201CI'll call you.\u201D \u2192 She said she ___ call me.", listOf("would", "will", "is going to"), 0, "\u201CWill\u201D becomes \u201Cwould\u201D in reported speech."),
        QuizQuestion("B2:reported-speech", "\u201CDon't open it!\u201D \u2192 He told me ___ open it.", listOf("not to", "to not", "don't"), 0, "Reported negative commands: told + object + not to + verb."),
        // B2:passive
        QuizQuestion("B2:passive", "The letter ___ yesterday.", listOf("was delivered", "delivered", "is delivered"), 0, "Passive in the past: was/were + third form."),
        QuizQuestion("B2:passive", "English ___ all over the world.", listOf("is spoken", "speaks", "is speaking"), 0, "General present fact in passive: is + spoken."),
        QuizQuestion("B2:passive", "My car ___ repaired right now.", listOf("is being", "is", "has been"), 0, "Passive in progress: is being + third form."),
        // B2:relative-clauses
        QuizQuestion("B2:relative-clauses", "The woman ___ lives next door is a pilot.", listOf("who", "which", "whose"), 0, "Person as subject of the clause \u2192 who."),
        QuizQuestion("B2:relative-clauses", "This is the book ___ I told you about.", listOf("that", "who", "whose"), 0, "Thing as object \u2192 that (or which)."),
        QuizQuestion("B2:relative-clauses", "That's the author ___ book won the prize.", listOf("whose", "which", "who's"), 0, "Possession \u2192 whose."),
        // C1:inversion
        QuizQuestion("C1:inversion", "Never ___ such a beautiful city.", listOf("have I seen", "I have seen", "I saw"), 0, "After \u201CNever\u201D we invert: have + I + seen."),
        QuizQuestion("C1:inversion", "Hardly ___ home when it started to rain.", listOf("had I arrived", "I had arrived", "did I arrive"), 0, "\u201CHardly when\u2026\u201D triggers inversion with the past perfect."),
        QuizQuestion("C1:inversion", "Not only ___ late, he also forgot the tickets.", listOf("was he", "he was", "he is"), 0, "After \u201CNot only\u201D the verb comes before the subject."),
        // C1:third-mixed
        QuizQuestion("C1:third-mixed", "If I ___ harder, I would have passed.", listOf("had studied", "studied", "would study"), 0, "Third conditional: If + had + participle."),
        QuizQuestion("C1:third-mixed", "Had I known, I ___ earlier.", listOf("would have come", "would come when", "had come"), 0, "Past result of a past condition \u2192 would have + participle."),
        QuizQuestion("C1:third-mixed", "If she had saved money then, she ___ rich now.", listOf("would be", "would have been", "will be"), 0, "Mixed conditional: past cause, present result \u2192 would be."),
        // C1:cleft
        QuizQuestion("C1:cleft", "___ I want is a quiet weekend.", listOf("What", "Which", "That"), 0, "\u201CWhat + clause + is\u2026\u201D is the standard cleft pattern."),
        QuizQuestion("C1:cleft", "It ___ money that motivates him, not passion.", listOf("was", "were", "has"), 0, "\u201CIt + be\u201D cleft: \u201CIt was\u2026 that\u2026\u201D."),
        QuizQuestion("C1:cleft", "The ___ why she quit was the salary.", listOf("reason", "because", "cause of"), 0, "\u201CThe reason why\u2026 was that\u2026\u201D clefts reasons."),
        // C1:participle-clauses
        QuizQuestion("C1:participle-clauses", "___ the bus, she ran to the stop.", listOf("Having missed", "Missed", "Have missed"), 0, "Earlier completed action \u2192 having + participle."),
        QuizQuestion("C1:participle-clauses", "The man ___ by the window is my uncle.", listOf("standing", "stands", "stood by"), 0, "Active action in progress \u2192 present participle."),
        QuizQuestion("C1:participle-clauses", "___ in 1990, the bridge needs repairs.", listOf("Built", "Building", "Having built"), 0, "Passive meaning \u2192 past participle.")
    )

    fun levelIsMastered(level: Level, masteredTopicIds: Set<String>): Boolean =
        topicsByLevel[level]?.all { it.id in masteredTopicIds } ?: true

    fun nextTopic(level: Level, masteredTopicIds: Set<String>): Topic? =
        topicsByLevel[level]?.firstOrNull { it.id !in masteredTopicIds }

    fun questionsForTopic(topicId: String): List<QuizQuestion> =
        questionBank.filter { it.topicId == topicId }

    fun levelUpQuestions(level: Level): List<QuizQuestion> {
        val inLevel = topicsByLevel[level].orEmpty().map { questionsForTopic(it.id) }
        return inLevel.mapNotNull { it.getOrNull(1) } + inLevel.mapNotNull { it.getOrNull(2) }
    }
}

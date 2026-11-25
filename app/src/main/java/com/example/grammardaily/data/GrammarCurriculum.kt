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
                tip = "Only one -s per sentence: it goes on \u201Cworks\u201D or in \u201Cdoesn't\u201D, never both."
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
                tip = "Learn them in pairs: \u201Cwant to go\u201D but \u201Cenjoy going\u201D \u2014 memory beats rules here."
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
                explanation = "When you report what someone said, move the tense one step into the past (am \u2192 was, will \u2192 would) and change pronouns and time words. Also \u201Csay\u201D never takes \u201Cme\u201D; \u201Ctell\u201D does.",
                examples = listOf(
                    "\u201CI am tired,\u201D he said. \u2192 He said he was tired.",
                    "\u201CI will call you.\u201D \u2192 She said she would call me.",
                    "\u201CDon't open it!\u201D \u2192 He told me not to open it."
                ),
                tip = "\u201CSaid me\u201D is always wrong. Say \u201Csaid to me\u201D or \u201Ctold me\u201D."
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
                tip = "The passive always needs a form of be: \u201CThe window broken\u201D is missing its \u201Cwas\u201D."
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
                tip = "You can drop who/which/that only when it is followed by a subject: \u201Cthe film (that) I saw\u201D, but not \u201Cthe film saw me\u201D."
            )
        ))
        put(Level.C1, listOf(
            Topic(
                id = "C1:inversion",
                title = "Inversion after negative adverbials",
                explanation = "Start the sentence with a negative or limiting adverbial (Never, Rarely, Hardly, Not only, No sooner, Little) and the word order becomes question-like without a question mark: auxiliary + subject + verb.",
                examples = listOf(
                    "Never have I seen such a beautiful city.",
                    "Hardly had I arrived home when it started to rain.",
                    "Not only was he late, he also forgot the tickets."
                ),
                tip = "The auxiliary moves in front of the subject: \u201CNever I have seen\u201D is wrong; \u201CNever have I seen\u201D is right."
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
                tip = "In \u201CIt was\u2026 that\u2026\u201D clefts, don't replace that with where: \u201CIt was this morning that we met.\u201D"
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
                tip = "The participle's subject must match the main clause subject \u2014 \u201CWalking down the street, the trees\u2026\u201D wrongly says the trees were walking."
            )
        ))
    }

    fun levelIsMastered(level: Level, masteredTopicIds: Set<String>): Boolean =
        topicsByLevel[level]?.all { it.id in masteredTopicIds } ?: true
}

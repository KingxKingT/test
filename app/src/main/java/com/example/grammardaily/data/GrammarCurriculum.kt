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
            ),
            Topic(
                id = "A2:past-continuous",
                title = "Past continuous (was / were + -ing)",
                explanation = "Use was/were plus the verb with -ing for an action that was already running at a past moment. It paints the background, and a shorter finished action often interrupts it.",
                examples = listOf(
                    "I was cooking when the phone rang.",
                    "They were playing football all afternoon.",
                    "What were you doing at ten last night?"
                ),
                tip = "Long background action = was/were + -ing. The short action that interrupts it = past simple."
            ),
            Topic(
                id = "A2:will-future",
                title = "Future with will",
                explanation = "Use will plus the plain verb for promises, offers and decisions made right now. It also predicts the future when there is no plan yet.",
                examples = listOf(
                    "I will call you tonight, I promise.",
                    "It will be cold tomorrow.",
                    "Don't worry, I will help you."
                ),
                tip = "Going to = plan already made. Will = decide now, or just predict."
            )
        ))

        // ---------------- B1: the real teaching path starts here ----------------
        put(Level.B1, listOf(
            Topic(
                id = "B1:present-perfect",
                title = "Present perfect vs past simple",
                explanation = "Present perfect (have/has + third form) connects the past to NOW \u2014 experiences, \u201Csince/for\u201D periods, and news with present results. Past simple is for finished time (\u201Cyesterday\u201D, \u201Cin 2015\u201D, \u201Cago\u201D). The moment you say WHEN, use past simple.",
                examples = listOf(
                    "I have seen that film three times. (up to now)",
                    "I saw it last Friday. (finished time)",
                    "She has lived here since 2019. (still true)",
                    "She lived in Rome in 2015. (that period is over)"
                ),
                tip = "See \u201Cyesterday / in 2010 / ago\u201D \u2192 past simple. See \u201Csince / for / ever / already / yet\u201D \u2192 present perfect.",
                discovery = Discovery(
                    examples = listOf(
                        "I lived in Cairo in 2017.",
                        "I have lived in Cairo since 2017.",
                        "I never ate sushi when I was a child.",
                        "I have never eaten sushi."
                    ),
                    question = "Which sentence describes a period that is STILL TRUE now?",
                    options = listOf(
                        "I have lived in Cairo since 2017.",
                        "I lived in Cairo in 2017.",
                        "I never ate sushi when I was a child.",
                        "None of them"
                    ),
                    correctIndex = 0,
                    explanation = "\u201CSince 2017\u201D + \u201Chave lived\u201D means the living there continues now. \u201CIn 2017\u201D is a closed box in the past \u2192 past simple."
                )
            ),
            Topic(
                id = "B1:gerunds-infinitives",
                title = "Gerunds and infinitives (-ing vs to)",
                explanation = "After verbs like enjoy, avoid, finish, mind, keep, suggest and after prepositions, use verb + -ing. After verbs like want, decide, hope, need, plan and learn, use to + verb. Some verbs change meaning: \u201Cremember to lock\u201D (future duty) vs \u201Cremember locking\u201D (past memory).",
                examples = listOf(
                    "I enjoy cooking after work.",
                    "She decided to study abroad.",
                    "They avoided answering directly.",
                    "Remember to call me tomorrow. / I remember calling him last week."
                ),
                tip = "Prepositions hide everywhere: \u201Cgood at swimming\u201D, \u201Cinstead of waiting\u201D \u2014 after a preposition it is ALWAYS -ing.",
                discovery = Discovery(
                    examples = listOf(
                        "I avoided telling her the truth.",
                        "I wanted to tell her the truth.",
                        "He finished cleaning the kitchen.",
                        "He hoped to see her again."
                    ),
                    question = "Look at the four sentences: what decides between -ing and to?",
                    options = listOf(
                        "The verb before it (avoid/finish \u2192 -ing, want/hope \u2192 to)",
                        "The tense of the sentence",
                        "Whether the subject is a person or thing",
                        "It is random \u2014 both are always fine"
                    ),
                    correctIndex = 0,
                    explanation = "The first verb is the boss: avoid and finished demand -ing; want and hoped demand to. The tense doesn't matter."
                )
            ),
            Topic(
                id = "B1:conditionals-1-2",
                title = "Conditionals: real and imaginary (first & second)",
                explanation = "First conditional \u2014 If + present, will \u2014 for real future chances. Second conditional \u2014 If + past simple, would \u2014 for imaginary, unlikely or hypothetical situations. In the second conditional, \u201CIf I were\u2026\u201D is the classic literary form.",
                examples = listOf(
                    "If it rains tomorrow, we will stay home. (real chance)",
                    "If I won the lottery, I would open a caf\u00E9. (imagination)",
                    "If she calls, I will tell her.",
                    "If I were you, I would apologize."
                ),
                tip = "Ask yourself: is this likely? \u2192 will. Just dreaming? \u2192 would. And never let will/would sit right after if.",
                discovery = Discovery(
                    examples = listOf(
                        "If it snows tonight, the school will close.",
                        "If I had wings, I would fly over the city.",
                        "If she passes the exam, her parents will be proud.",
                        "If I were a professional singer, I would tour the world."
                    ),
                    question = "Sentences 2 and 4 use \u201Cwould\u201D. What do they have in common?",
                    options = listOf(
                        "They describe situations that are NOT real or very unlikely",
                        "They are about the past",
                        "They are polite requests",
                        "They are questions"
                    ),
                    correctIndex = 0,
                    explanation = "Wings and being a pro singer are imaginary \u2192 If + past, would. Rain tomorrow and exam results are real possibilities \u2192 If + present, will."
                )
            ),
            Topic(
                id = "B1:modals",
                title = "must, have to, should & their negatives",
                explanation = "\u201CMust\u201D = strong personal obligation; \u201Chave to\u201D = external rule; \u201Cshould\u201D = advice. \u201CMustn't\u201D = forbidden; \u201Cdon't have to\u201D = optional. In the past: \u201Chad to\u201D / \u201Cdidn't have to\u201D. \u201CMust\u201D has no past and no \u201Cdon't must\u201D.",
                examples = listOf(
                    "I must finish this today \u2014 it's my own deadline.",
                    "You have to wear a helmet here \u2014 it's the law.",
                    "You should rest \u2014 that's my advice.",
                    "Yesterday I had to work until nine."
                ),
                tip = "mustn't \u2260 don't have to. \u201CYou mustn't tell\u201D = DON'T do it. \u201CYou don't have to tell\u201D = it's up to you.",
                discovery = Discovery(
                    examples = listOf(
                        "Visitors mustn't touch the paintings.",
                        "Visitors don't have to pay on Sundays.",
                        "Visitors have to leave before 6 p.m.",
                        "Visitors should bring an umbrella."
                    ),
                    question = "Which sentence means the action is FORBIDDEN?",
                    options = listOf(
                        "Visitors mustn't touch the paintings.",
                        "Visitors don't have to pay on Sundays.",
                        "Visitors have to leave before 6 p.m.",
                        "Visitors should bring an umbrella."
                    ),
                    correctIndex = 0,
                    explanation = "Mustn't = prohibited. Don't have to = allowed but not necessary. Have to = required. Should = advice."
                )
            ),
            Topic(
                id = "B1:used-to",
                title = "used to \u2014 past habits that changed",
                explanation = "\u201CUsed to + base verb\u201D describes a past habit or state that is no longer true now: \u201CI used to smoke\u201D (I don't now). Negative: \u201Cdidn't use to\u201D; question: \u201CDid you use to\u2026?\u201D (no -d on the question form!). For still-true habits use present simple + usually instead.",
                examples = listOf(
                    "I used to play outside all day when I was small.",
                    "She didn't use to like spicy food, but now she loves it.",
                    "Did you use to wear a uniform at school?",
                    "I usually walk to work. (a habit that continues now)"
                ),
                tip = "\u201CUsed to\u201D ONLY looks backwards. For now say \u201CI usually\u2026\u201D \u2014 \u201CI use to walk to work\u201D is wrong.",
                discovery = Discovery(
                    examples = listOf(
                        "I used to bite my nails \u2014 I stopped years ago.",
                        "I used to live near the sea as a child.",
                        "There didn't use to be a lift in this building.",
                        "Did people use to write letters instead of texting?"
                    ),
                    question = "What do ALL four \u201Cused to\u201D sentences have in common?",
                    options = listOf(
                        "They describe a past situation that is different now.",
                        "They describe what the speaker does every day.",
                        "They tell you exactly when in the past it happened.",
                        "They are only used in negative sentences."
                    ),
                    correctIndex = 0,
                    explanation = "\u201CUsed to\u201D always signals: true before, not true (or unknown) now. No exact past time \u2014 that's why there is no \u201CI used to see him yesterday\u201D."
                )
            ),
            Topic(
                id = "B1:future-forms",
                title = "will vs going to vs present continuous",
                explanation = "Three futures, three jobs: \u201Cwill\u201D for decisions made at the moment of speaking and predictions with opinions (\u201CI think\u2026\u201D); \u201Cbe going to\u201D for plans before speaking and predictions with present evidence; present continuous for fixed arrangements \u2014 often with a time and place already agreed.",
                examples = listOf(
                    "The phone's ringing \u2014 I'll get it! (decided NOW)",
                    "I'm going to study medicine next year. (decided before)",
                    "We're meeting Ana at the station at 6. (arranged,details fixed)",
                    "I think Brazil will win. (prediction / opinion)"
                ),
                tip = "Saw evidence in the sky? \u2192 going to rain. Only an opinion? \u2192 will rain. Already agreed with someone? \u2192 are meeting.",
                discovery = Discovery(
                    examples = listOf(
                        "A: \u201CWe have no milk.\u201D B: \u201CReally? I'll buy some on my way home.\u201D",
                        "A: \u201CWhat are your plans for Sunday?\u201D B: \u201CI'm going to visit my aunt.\u201D",
                        "A: \u201CAre you free tomorrow?\u201D B: \u201CSorry, I'm playing tennis with Leo.\u201D",
                        "A: \u201CDo you think it'll snow?\u201D B: \u201CMaybe. It's very cold.\u201D"
                    ),
                    question = "In dialogue 1, WHY does B say \u201CI'll buy some\u201D?",
                    options = listOf(
                        "It's a decision made at the exact moment of speaking",
                        "It was B's plan all week",
                        "It's an arrangement with a shop",
                        "It's a prediction about the weather"
                    ),
                    correctIndex = 0,
                    explanation = "The milk news is new information, so the decision is born right then \u2192 will. An existing plan would use going to, a fixed appointment the present continuous."
                )
            ),
            Topic(
                id = "B1:question-tags",
                title = "Question tags (\u2026, aren't you?)",
                explanation = "A question tag turns a statement into a friendly question. Rules: the tag is the opposite of the sentence: positive \u2192 negative tag (are \u2192 aren't, can \u2192 can't); with no helper verb borrow do/does/did; the subject becomes a pronoun. Falling tone = confirming; rising tone = real question.",
                examples = listOf(
                    "You're tired, aren't you?",
                    "She isn't coming, is she?",
                    "They left early, didn't they?",
                    "I'm late, aren't I? (special form!)"
                ),
                tip = "No helper verb in the sentence? Borrow do/does/did. And \u201CI'm\u2026\u201D takes \u201Caren't I?\u201D \u2014 the one impossible-sounding exception.",
                discovery = Discovery(
                    examples = listOf(
                        "It's cold today, isn't it?",
                        "You can swim, can't you?",
                        "He doesn't eat meat, does he?",
                        "They arrived late, didn't they?"
                    ),
                    question = "Complete: \u201CShe works here, ___?\u201D",
                    options = listOf(
                        "doesn't she", "does she", "isn't she", "won't she"
                    ),
                    correctIndex = 0,
                    explanation = "Statement is positive \u2192 negative tag; with no helper verb \u2192 borrow does; subject \u201Cshe\u201D \u2192 she. Positive \u2192 she works, tag \u2192 doesn't she."
                )
            ),
            Topic(
                id = "B1:connectors",
                title = "Linking words: but / although / because / so / however",
                explanation = "Connectors show the LOGIC between ideas: contrast (but, although, however, even though), cause (because, since, as), result (so, therefore). \u201CAlthough\u201D starts a clause with a subject and verb; \u201Chowever\u201D starts a NEW sentence with a comma; \u201Cdespite/in spite of\u201D + noun or -ing.",
                examples = listOf(
                    "I was tired, but I finished the report. (contrast)",
                    "Although I was tired, I finished the report.",
                    "It was raining, so we stayed in. (result)",
                    "We stayed in because of the rain. (cause + noun)"
                ),
                tip = "\u201CBecause of\u201D + noun, \u201Cbecause\u201D + subject + verb. \u201CAlthough I was tired\u201D gets a comma-free main clause; \u201CHowever,\u201D always opens with a comma.",
                discovery = Discovery(
                    examples = listOf(
                        "He studied medicine because his mother was a doctor.",
                        "He was afraid of blood. However, he kept working.",
                        "Although he was afraid, he kept working.",
                        "He was afraid, so he asked for help."
                    ),
                    question = "Which connector says: \u201Cthis result happened BECAUSE of the first idea\u201D?",
                    options = listOf(
                        "so (He was afraid, so he asked for help.)",
                        "because (He studied medicine because\u2026)",
                        "However, (He was afraid. However,\u2026)",
                        "Although (Although he was afraid,\u2026)"
                    ),
                    correctIndex = 0,
                    explanation = "\u201CSo\u201D points forward to the RESULT. \u201CBecause\u201D looks backward to the CAUSE. However/Although build contrast, not cause-effect."
                )
            ),
            Topic(
                id = "B1:quantifiers",
                title = "Much, many, a lot of",
                explanation = "Words for amounts split in two: things you can count (friends, days) take many and few. Things you cannot count (water, time) take much and little. Not sure? A lot of works with both, mostly in positive sentences.",
                examples = listOf(
                    "I don\u2019t have **much** time. (time = no count)",
                    "She has **many** friends here. (friends = count)",
                    "We made **a lot of** mistakes. (both work)",
                    "There is **little** milk left. (no count)"
                ),
                tip = "Not sure if it counts? Use \u201Ca lot of\u201D \u2014 it is almost always safe.",
                discovery = Discovery(
                    examples = listOf(
                        "How much water do you drink?",
                        "How many cups do you have?"
                    ),
                    question = "Complete: \u201CHow ___ money do you need?\u201D",
                    options = listOf("much", "many", "few", "several"),
                    correctIndex = 0,
                    explanation = "Money is one mass, like water \u2014 no count \u2192 much. Many only joins things you can count: coins, bills."
                )
            ),
            Topic(
                id = "B1:prepositions",
                title = "Dependent prepositions (good at, depend on)",
                explanation = "Some words always pull the same small word after them: good AT maths, depend ON the weather, wait FOR the bus. The pair is fixed \u2014 learn it as one word, not as a rule.",
                examples = listOf(
                    "She is **good at** languages.",
                    "It **depends on** the weather.",
                    "I am **waiting for** my friend.",
                    "He is **interested in** history."
                ),
                tip = "Hear the pair, say the pair \u2014 your ear remembers pairs better than lists.",
                discovery = Discovery(
                    examples = listOf(
                        "It depends on you.",
                        "She is good at chess."
                    ),
                    question = "Complete: \u201CI am waiting ___ the bus.\u201D",
                    options = listOf("for", "at", "of", "on"),
                    correctIndex = 0,
                    explanation = "Wait always carries \u201Cfor\u201D before the thing you wait for. The pair is fixed."
                )
            ),
            Topic(
                id = "B1:articles-deep",
                title = "A, the, or nothing?",
                explanation = "First mention takes a/an. When we both know which one, take the. Talking about all of something \u2014 dogs, music, life \u2014 takes no word at all.",
                examples = listOf(
                    "I saw **a** dog. **The** dog was huge. (first, then known)",
                    "**Dogs** are loyal. (all dogs, no word)",
                    "She plays **the** piano. (instruments take \u201Cthe\u201D)",
                    "**Life** here is quiet. (general, no word)"
                ),
                tip = "Can you point at it? Say \u201Cthe\u201D. Talking about everything of a kind? Say nothing.",
                discovery = Discovery(
                    examples = listOf(
                        "Life in a village is quiet.",
                        "The village has one shop."
                    ),
                    question = "Complete for ALL villages: \u201C___ villages are noisy.\u201D",
                    options = listOf("(no word)", "The", "A", "One"),
                    correctIndex = 0,
                    explanation = "All villages in general \u2192 no word at all: \u201CVillages are noisy.\u201D The/A would point at special villages."
                )
            ),,
            Topic(
                id = "B1:present-perfect-continuous",
                title = "Present perfect continuous (have been + -ing)",
                explanation = "Use have/has been plus the verb with -ing for an action that started in the past and is still running or just stopped. It answers \"how long?\" or explains a result you can see now.",
                examples = listOf(
                    "I have been learning English for two years.",
                    "She is tired because she has been working all day.",
                    "How long have you been waiting?"
                ),
                tip = "for + a length of time (for an hour), since + a starting point (since Monday)."
            )
        ))

        put(Level.B2, listOf(
            Topic(
                id = "B2:past-narrative",
                title = "Narrative tenses: painting with the past",
                explanation = "Stories mix four brushes: past simple = the events that move the plot; past continuous = the scenery that was in progress; past perfect = the flashback (an action BEFORE the story's time); past perfect continuous = long backgrounds leading to an event (\u201Chad been waiting for hours when\u2026\u201D).",
                examples = listOf(
                    "The sun was setting when Dana arrived at the farm.",
                    "By then, her uncle had already sold the fields.",
                    "He had been saving for years, yet the bank refused.",
                    "She spilled the tea as she stood up."
                ),
                tip = "Ask \u201Cwhich happened FIRST?\u201D \u2014 the earliest action goes to the past perfect; the story's \u201Ccamera time\u201D stays in past simple.",
                discovery = Discovery(
                    examples = listOf(
                        "I was cooking when the lights went out.",
                        "When I got home, my wife had already eaten.",
                        "He had been driving for hours when the car died.",
                        "She opened the window and looked outside."
                    ),
                    question = "In sentence 2, why \u201Chad eaten\u201D and not \u201Cate\u201D?",
                    options = listOf(
                        "She ate BEFORE the speaker got home \u2014 the earliest action takes the past perfect",
                        "\u201CEaten\u201D is simply the formal past of eat",
                        "The sentence describes a habit",
                        "It must agree with \u201Chome\u201D"
                    ),
                    correctIndex = 0,
                    explanation = "Two past moments: her eating, then my arrival. English marks the earlier one with had + the after-have form (eaten, seen) \u2014 the earlier action, the flashback."
                )
            ),
            Topic(
                id = "B2:reported-speech",
                title = "Reported speech & reporting verbs",
                explanation = "Shift tenses one step back (am\u2192was, will\u2192would, can\u2192could, must\u2192had to) and change pronouns and time words (now\u2192then, tomorrow\u2192the next day). Reporting verbs add nuance: \u201CHe suggested going\u201D, \u201CShe offered to drive\u201D, \u201CThey warned me not to wait\u201D. Questions: no do/did, statement order (\u201Cwhere I lived\u201D).",
                examples = listOf(
                    "\u201CI must leave now.\u201D \u2192 He said he had to leave then.",
                    "\u201CDon't touch the wire!\u201D \u2192 She warned me not to touch the wire.",
                    "\u201CLet's share a taxi.\u201D \u2192 He suggested sharing a taxi.",
                    "\u201CI'll pay for lunch.\u201D \u2192 She offered to pay for lunch."
                ),
                tip = "\u201CTell\u201D needs a person (told me), \u201Csay\u201D never takes one directly (said to me). And reported questions never keep did/do.",
                discovery = Discovery(
                    examples = listOf(
                        "\u201CI'll call you tomorrow,\u201D she said. \u2192 She said she would call me the next day.",
                        "\u201CI saw your brother yesterday,\u201D he said. \u2192 He said he had seen my brother the day before.",
                        "\u201CWe're leaving now,\u201D they said. \u2192 They said they were leaving then.",
                        "\u201CDid you enjoy the show?\u201D \u2192 He asked me if I had enjoyed the show."
                    ),
                    question = "Look at all four reported sentences: what THREE things always change?",
                    options = listOf(
                        "Tense, pronouns, and time words",
                        "Only the tense",
                        "Only the pronouns",
                        "Nothing changes if the verb is \u201Csay\u201D"
                    ),
                    correctIndex = 0,
                    explanation = "will\u2192would, I\u2192he/she, tomorrow\u2192the next day. All three shift together because you're describing the world from a later point of view."
                )
            ),
            Topic(
                id = "B2:passive",
                title = "Passive voice across tenses + have something done",
                explanation = "Passive = be (in the needed tense) + third form: is grown, was sent, has been stolen, is being repaired, will be opened, can't be trusted. Use it when the doer is unknown, obvious or unimportant. Causative \u201Chave/get something done\u201D = a paid service: \u201CI had my car repaired\u201D (I didn't fix it myself).",
                examples = listOf(
                    "The roof has been repaired. (present perfect passive)",
                    "The results will be published on Friday.",
                    "My wallet was stolen on the bus.",
                    "I'm having the kitchen painted next week."
                ),
                tip = "Suspicious short passive? Check there's a be before the after-have form. And \u201CI repaired my car\u201D vs \u201CI had my car repaired\u201D \u2014 money changes hands only in the second one.",
                discovery = Discovery(
                    examples = listOf(
                        "Somebody fixed my bike yesterday. \u2192 I had my bike fixed yesterday.",
                        "Somebody is painting my flat now. \u2192 I'm having my flat painted now.",
                        "Arabic is spoken here.",
                        "The bridge was built in 1890."
                    ),
                    question = "Sentences 1-2 convert to \u201Chave something done\u201D. What must be true?",
                    options = listOf(
                        "A service person did the job, not the speaker",
                        "The speaker did it alone at home",
                        "The object is broken forever",
                        "The action happens in the future"
                    ),
                    correctIndex = 0,
                    explanation = "\u201Chave + object + the after-have form\u201D (have the kitchen painted) means you arranged and (usually) paid for the service \u2014 perfect for mechanics, painters, hairdressers."
                )
            ),
            Topic(
                id = "B2:relative-clauses",
                title = "Relative clauses: defining, non-defining, reduced",
                explanation = "Defining clauses identify which one you mean (\u201Cthe woman who called\u201D) \u2014 no commas, that/who/which, object-pronoun can be dropped. Non-defining clauses add extra info (\u201Cmy boss, who loves jazz,\u2026\u201D) \u2014 commas, and NEVER use that. Prepositions can sit before whom/which (formal): \u201Cthe colleague with whom I trust\u2026\u201D better as \u201Cwho(m) I trust\u201D. Reduced: \u201Cthe man sitting there\u201D, \u201Cthe bridge built in 1890\u201D.",
                examples = listOf(
                    "The keys that I lost were in my coat. (defining)",
                    "My uncle, who lives in Oslo, is a musician. (extra info \u2014 no \u201Cthat\u201D!)",
                    "The committee rejected the plan, which surprised us. (which = the whole fact)",
                    "Anyone wanting a ticket should email us. (reduced: who want \u2192 wanting)"
                ),
                tip = "Commas around the clause? Use who/which, never that, and you can't drop them. Sentence ending in \u201C\u2026, which means\u2026\u201D \u2014 which can refer to the whole idea.",
                discovery = Discovery(
                    examples = listOf(
                        "My brother who lives in Sydney is a chef. (I have several brothers \u2014 this picks one)",
                        "My brother, who lives in Sydney, is a chef. (I have ONE brother + bonus fact)",
                        "The film that we saw won an Oscar.",
                        "The film, which lasted three hours, felt short."
                    ),
                    question = "What changes when the commas appear?",
                    options = listOf(
                        "The clause becomes extra info \u2014 removing it leaves a true sentence",
                        "The clause becomes essential \u2014 it identifies which one",
                        "\u201CThat\u201D becomes compulsory",
                        "The tense must change"
                    ),
                    correctIndex = 0,
                    explanation = "Commas = brackets around extra news. \u201CMy brother is a chef\u201D still stands alone. No commas = we NEED the clause to know which brother/film."
                )
            ),
            Topic(
                id = "B2:future-perfect",
                title = "Future perfect & future continuous",
                explanation = "Future perfect \u2014 will have finished \u2014 looks back from a future point: done by then (\u201CBy 2030 I will have finished\u201D). Future continuous \u2014 will be + -ing \u2014 in progress at a future moment (\u201CAt 10 tonight I will be flying\u201D), also for polite current plans. Signal words: by / by the time / this time next week.",
                examples = listOf(
                    "By June, she will have worked here for ten years.",
                    "Don't call at 8 \u2014 I'll be having dinner.",
                    "By the time you land, we will have finished the exam.",
                    "This time tomorrow, I'll be lying on a beach."
                ),
                tip = "\u201CBy + future date\u201D almost always asks for \u201cwill have finished\u201D. \u201CThis time + future\u201D asks for will be + -ing.",
                discovery = Discovery(
                    examples = listOf(
                        "By 2030, doctors will have discovered new treatments.",
                        "At midnight tonight, we will be crossing the border.",
                        "By the time you read this, I will have left the city.",
                        "In an hour, the guests will be arriving."
                    ),
                    question = "Which sentence maps ACTION COMPLETED by a future moment?",
                    options = listOf(
                        "By 2030, doctors will have discovered new treatments.",
                        "At midnight tonight, we will be crossing the border.",
                        "In an hour, the guests will be arriving.",
                        "Both sentences 2 and 3"
                    ),
                    correctIndex = 0,
                    explanation = "\u201CBy 2030\u201D = deadline; finished from the future's point of view \u2192 will have finished. Sentences 2-3 paint an action in mid-air \u2192 will be + -ing."
                )
            ),
            Topic(
                id = "B2:modals-deduction",
                title = "Modals of deduction: must / can't / might have",
                explanation = "Deductions from evidence: must = almost sure it's true; can't = almost sure it's false; might/could/may = possible. About the past, say must/can't/might + have + the after-have form: \u201Cmust have missed\u201D, \u201Ccan't have seen\u201D, \u201Cmight have left\u201D. Note: \u201Cmustn't\u201D is NOT the deduction-opposite of must \u2014 \u201Ccan't\u201D is.",
                examples = listOf(
                    "The ground is wet \u2014 it must have rained.",
                    "Emma can't be 40 \u2014 she's in our class!",
                    "He's late; he might have missed the bus.",
                    "You saw a ghost? You must have been dreaming."
                ),
                tip = "Logic test: mustn't = forbidden (rule), can't = impossible (thinking). Never use \u201Cmustn't be\u201D for \u201CY-i-t can't be true\u201D.",
                discovery = Discovery(
                    examples = listOf(
                        "The lights are on \u2014 someone must be home.",
                        "That can't be Tom; Tom is in Iceland.",
                        "The gate is open \u2014 the dog might have escaped.",
                        "You mustn't open that gate! (rule, not deduction)"
                    ),
                    question = "You're nearly CERTAIN something is impossible. Which modal?",
                    options = listOf(
                        "can't (That can't be Tom.)",
                        "mustn't (You mustn't open that gate!)",
                        "might (The dog might have escaped.)",
                        "must (Someone must be home.)"
                    ),
                    correctIndex = 0,
                    explanation = "Can't = \u201Cimpossible, the evidence says no\u201D. Mustn't kills permission, not logic. Might = maybe; must = almost certain yes."
                )
            ),
            Topic(
                id = "B2:unreal-past",
                title = "wish / if only / it's time / would rather",
                explanation = "Unreal past = past forms talking about NOW or the past, not actual time: wish + past simple = I want a different present (\u201CI wish I knew\u201D); wish + past perfect = regret about the past (\u201CI wish I had known\u201D); \u201CIt's time we left\u201D and \u201CI'd rather you didn't smoke\u201D use past for politeness/urgency. \u201Cwish + would\u201D criticizes behavior (\u201CI wish you would listen\u201D).",
                examples = listOf(
                    "I wish I had more free time. (now \u2192 past simple)",
                    "I wish I had accepted that job. (past \u2192 past perfect)",
                    "It's time you went to bed!",
                    "I'd rather you didn't park here."
                ),
                tip = "Two wishes, two distances: unhappy about NOW \u2192 past simple. Unhappy about BEFORE \u2192 past perfect. The form is past; the meaning is regret.",
                discovery = Discovery(
                    examples = listOf(
                        "I wish I had a bigger kitchen. (my kitchen is small NOW)",
                        "I wish I had bought a bigger kitchen table. (I didn't \u2014 regret)",
                        "If only I could drive! (I can't now)",
                        "I wish it would stop raining. (annoying behavior around now)"
                    ),
                    question = "Which wish is a REGRET about the past?",
                    options = listOf(
                        "I wish I had bought a bigger kitchen table.",
                        "I wish I had a bigger kitchen.",
                        "If only I could drive!",
                        "I wish it would stop raining."
                    ),
                    correctIndex = 0,
                    explanation = "Past perfect (had bought) reaches back to a finished chance. Past simple (had, could) repaints the present; wish + would complains about others' habits."
                )
            ),
            Topic(
                id = "B2:conditionals-advanced",
                title = "unless, as long as, provided, in case",
                explanation = "Conditionals have cousins: \u201Cunless\u201D = if\u2026not (\u201CUnless you hurry\u201D = If you don't hurry); \u201Cas long as / provided (that) / on condition that\u201D = only if (a condition you accept); \u201Cin case\u201D = precaution BEFORE (\u201CTake a jacket in case it gets cold\u201D \u2260 if it gets cold!). Even if = whether or not.",
                examples = listOf(
                    "Unless you book now, seats will sell out.",
                    "You can borrow the car as long as you fill the tank.",
                    "I'll take an umbrella in case it rains.",
                    "Even if it rains, the match will happen."
                ),
                tip = "English never puts a negative after unless: \u201CUnless you don't hurry\u2026\u201D is wrong \u2014 unless already carries the 'not'.",
                discovery = Discovery(
                    examples = listOf(
                        "Unless you leave now, you'll miss the train.",
                        "If you don't leave now, you'll miss the train.",
                        "Take the spare key in case you lose the first one.",
                        "Take the spare key if you lose the first one."
                    ),
                    question = "Sentence 3 and 4 differ only in \u201Cin case/if\u201D. What changes?",
                    options = listOf(
                        "In case = precaution taken BEFORE something might go wrong; if = react only when it actually happens",
                        "They mean exactly the same thing",
                        "\u201CIn case\u201D refers to the past",
                        "\u201CIf\u201D is more polite"
                    ),
                    correctIndex = 0,
                    explanation = "You pack the spare key now as insurance (in case). If you lose the first one, you take the spare only afterwards. in case = pre-empt, if = react."
                )
            ),
            Topic(
                id = "B2:phrasal-verbs",
                title = "Phrasal verbs (give up, run out of)",
                explanation = "A small word can change everything: give UP = stop, look AFTER = care for, run OUT OF = have none left. With it/them, the small word moves after: give it up, never give up it.",
                examples = listOf(
                    "Don\u2019t **give up** now. (= stop trying)",
                    "She **looks after** her brother. (= cares for)",
                    "We **ran out of** milk. (= none left)",
                    "Turn **it** off \u2014 never \u201Cturn off it\u201D."
                ),
                tip = "Learn each phrasal verb as one new word with its own meaning \u2014 not as verb + grammar.",
                discovery = Discovery(
                    examples = listOf(
                        "I gave up smoking last year.",
                        "I gave it up last year."
                    ),
                    question = "Choose the correct order:",
                    options = listOf("Don\u2019t give up it.", "Don\u2019t give it up.", "Don\u2019t it give up.", "Don\u2019t up give it."),
                    correctIndex = 1,
                    explanation = "With it/them, the small word jumps after the object: give it up. With full nouns it stays: give up smoking."
                )
            ),
        ))
        put(Level.C1, listOf(
            Topic(
                id = "C1:inversion",
                title = "Inversion after negative adverbials",
                explanation = "Start the sentence with a negative or limiting adverbial (Never, Rarely, Hardly, Not only, No sooner, Little) and the word order becomes question-like without a question mark: helper verb first, then subject: Never have I seen.",
                examples = listOf(
                    "Never have I seen such a beautiful city.",
                    "Hardly had I arrived home when it started to rain.",
                    "Not only was he late, he also forgot the tickets."
                ),
                tip = "The helper verb moves in front of the subject: \u201CNever I have seen\u201D is wrong; \u201CNever have I seen\u201D is right."
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
            ),
            Topic(
                id = "C1:verb-patterns",
                title = "Verb + small word + -ing patterns",
                explanation = "Some verbs need a small word before another verb, and that second verb takes -ing: accuse somebody of lying, insist on paying, prevent somebody from leaving.",
                examples = listOf(
                    "They accused him of lying.",
                    "She insisted on paying for dinner.",
                    "Nothing can prevent us from trying."
                ),
                tip = "Learn the whole chain as one block: accuse of, insist on, prevent from \u2014 the next verb always ends in -ing."
            ),
            Topic(
                id = "C1:ellipsis",
                title = "Short answers: so do I, neither have I",
                explanation = "English drops repeated words or swaps them for tiny helpers: So do I, Neither have I, I hope not. The missing words are understood from the first sentence.",
                examples = listOf(
                    "\"I love coffee.\" \u2014 So do I.",
                    "\"I haven't finished.\" \u2014 Neither have I.",
                    "Will it rain? \u2014 I hope not."
                ),
                tip = "Agreeing with a positive: so + helper + subject. Agreeing with a negative: neither + helper + subject."
            ),
            Topic(
                id = "C1:discourse-linkers",
                title = "Grown-up linkers: furthermore, nonetheless, consequently",
                explanation = "Formal writing uses stronger connectors: furthermore adds a reason, nonetheless pushes against it, consequently shows the result. They lift simple sentences into essays.",
                examples = listOf(
                    "The plan is cheap; furthermore, it is quick.",
                    "The odds were low; nonetheless, she tried.",
                    "Prices rose; consequently, sales fell."
                ),
                tip = "Swap them into a sentence you already know: but = nonetheless, so = consequently."
            )
        ))
    }

    fun levelIsMastered(level: Level, masteredTopicIds: Set<String>): Boolean =
        topicsByLevel[level]?.all { it.id in masteredTopicIds } ?: true
}

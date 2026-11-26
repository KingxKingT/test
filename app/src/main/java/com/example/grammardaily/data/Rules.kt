package com.example.grammardaily.data

data class RuleSpec(
    val pattern: String,
    val examples: List<String> = emptyList(),
    val short: String = "",
    val wrong: String = "",
    val right: String = "",
    val why: String = "",
    val timeline: String = ""
)

object Rules {

    val map: Map<String, RuleSpec> = mapOf(
        "A1:be" to RuleSpec(
            pattern = "[I] + [am]   [He/She] + [is]   [You/We/They] + [are]",
            examples = listOf("I **am** tired.", "She **is** a doctor.", "We **are** friends."),
            short = "One verb, three shapes. Pick the shape that matches the person before it.",
            wrong = "They is late.",
            right = "They are late.",
            why = "\u201cThey\u201d always takes \u201care\u201d."
        ),
        "A1:present-simple" to RuleSpec(
            pattern = "[I/You/We/They] + [work]   [He/She/It] + [works]",
            examples = listOf("I **play** tennis on Sundays.", "He **works** in a bank.", "She **doesn\u2019t like** tea."),
            short = "He, she and it add -s to the verb. Nobody else does.",
            wrong = "He work in a bank.",
            right = "He works in a bank.",
            why = "He/she/it needs the -s on the verb."
        ),
        "A1:articles" to RuleSpec(
            pattern = "[a] + consonant sound   [an] + vowel sound   [the] + the one we both know",
            examples = listOf("I have **a** dog and **a** cat.", "She is **an** engineer.", "**The** sun is bright today."),
            short = "A/an means \u201cone of many\u201d. The means \u201cthat one, you know which\u201d.",
            wrong = "She is a engineer.",
            right = "She is an engineer.",
            why = "Before a vowel sound (e\u2011\u2011en\u2011gineer) use \u201can\u201d."
        ),
        "A1:plurals-there" to RuleSpec(
            pattern = "[There is] + one thing   [There are] + many things",
            examples = listOf("**There is** a park near my house.", "**There are** three cars outside.", "Two **children** are playing."),
            short = "One thing \u2192 there is. Two or more \u2192 there are.",
            wrong = "There are a park near my house.",
            right = "There is a park near my house.",
            why = "\u201cA park\u201d is one thing, so \u201cthere is\u201d."
        ),
        "A2:present-continuous" to RuleSpec(
            pattern = "[am/is/are] + [verb-ing]",
            examples = listOf("I **am reading** now.", "Look! It **is raining**.", "They **are studying**."),
            short = "Actions happening right now use am/is/are plus -ing.",
            wrong = "I am knowing the answer.",
            right = "I know the answer.",
            why = "\u201cKnow\u201d is a state, not an action, so no -ing.",
            timeline = "PAST \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u25cf\u2500\u2192 NOW \u2500\u2500\u2500\u2500\u2500\u2192 FUTURE\n           (action is still going)"
        ),
        "A2:past-simple" to RuleSpec(
            pattern = "[watched] or [went, saw] \u2014 used with finished times   questions: [Did you watch?]",
            examples = listOf("We **watched** a film yesterday.", "I **went** to London in 2020.", "Did you **call** her last night?"),
            short = "Finished time \u2192 verb-ed (or an irregular form like went).",
            wrong = "Did you went home?",
            right = "Did you go home?",
            why = "After \u201cdid\u201d always use the base form.",
            timeline = "PAST \u2500\u2500\u2500\u25cf\u2500\u2500\u2500\u2500\u2500\u2192 NOW \u2500\u2500\u2500\u2192 FUTURE\n  (finished \u2014 it is over)"
        ),
        "A2:comparatives" to RuleSpec(
            pattern = "[short adj] + [-er / the -est]   [long adj] + [more / the most]",
            examples = listOf("This car is **faster** than that one.", "She is **the tallest** in the class.", "A laptop is **more expensive** than a book."),
            short = "Short words take -er/-est. Long words take more/the most.",
            wrong = "This book is more big.",
            right = "This book is bigger.",
            why = "Short adjectives take -er, not \u201cmore\u201d."
        ),
        "A2:going-to" to RuleSpec(
            pattern = "[am/is/are] + [going to] + [verb]",
            examples = listOf("I **am going to start** a course.", "It **is going to rain** \u2014 see the clouds.", "They **are going to move** house."),
            short = "A plan or a clear sign before it happens \u2192 going to.",
            wrong = "I going to sleep now.",
            right = "I am going to sleep now.",
            why = "You need am/is/are before \u201cgoing to\u201d.",
            timeline = "NOW \u2500\u2500plan\u2500\u2192 \u25cf\u2500\u2500\u2500\u2192 FUTURE\n  (decided before it happens)"
        ),
        "B1:present-perfect" to RuleSpec(
            pattern = "[have lost / has seen] = result now, no time   |   [lost / saw] + yesterday, in 2020",
            examples = listOf("I **have lost** my keys. (I don\u2019t have them now)", "I **lost** my keys yesterday. (finished time)", "She **has worked** here since 2020."),
            short = "Result matters now \u2192 have lost, has seen. The sentence says when (yesterday, in 2020) \u2192 lost, saw.",
            wrong = "I have seen him yesterday.",
            right = "I saw him yesterday.",
            why = "\u201cYesterday\u201d is a finished time, so this is past simple.",
            timeline = "PAST \u2500\u2500\u25cf\u2500\u2500\u2500?\u2500\u2500\u2500\u2192 NOW\n have seen (no time said) \u2192 still matters now\n saw yesterday \u2192 over, done"
        ),
        "B1:gerunds-infinitives" to RuleSpec(
            pattern = "[enjoy / avoid / finish] + [verb-ing]   [want / decide / hope] + [to verb]",
            examples = listOf("I **enjoy swimming**.", "She **decided to leave**.", "He **avoided answering** me."),
            short = "Some verbs pull -ing after them, others pull to + verb. Learn them in pairs.",
            wrong = "I want going home.",
            right = "I want to go home.",
            why = "\u201cWant\u201d needs \u201cto + verb\u201d, never -ing."
        ),
        "B1:conditionals-1-2" to RuleSpec(
            pattern = "Real:  [If + present] , [will + verb]     Imagined:  [If + past] , [would + verb]",
            examples = listOf("If it **rains**, I **will stay** home. (possible)", "If I **had** money, I **would travel**. (not real now)", "If she **studies**, she **will pass**."),
            short = "Possible future \u2192 if + present, will. Only in your head \u2192 if + past, would.",
            wrong = "If I will see him, I will tell him.",
            right = "If I see him, I will tell him.",
            why = "No \u201cwill\u201d in the if-part \u2014 use present there."
        ),
        "B1:modals" to RuleSpec(
            pattern = "[can / must / should / might] + [verb]  (no \u201cto\u201d)",
            examples = listOf("I **can swim**.", "You **must stop** when it\u2019s red.", "You **should rest** today."),
            short = "can, must, should are helper words; the main verb follows bare: can swim, must stop.",
            wrong = "You should to see a doctor.",
            right = "You should see a doctor.",
            why = "Modal + verb, never modal + to + verb."
        ),
        "B1:used-to" to RuleSpec(
            pattern = "[used to] + [verb]  = true in the past, not now",
            examples = listOf("I **used to smoke**, but I stopped.", "She **used to live** in Rome.", "He **didn\u2019t use to** cook."),
            short = "\u201cUsed to\u201d looks back at an old habit or state that is finished now.",
            wrong = "She use to play piano.",
            right = "She used to play piano.",
            why = "It keeps the -d even with she/he/it: used to.",
            timeline = "PAST \u25cf\u2550\u2550\u2550\u2550\u2550\u255d\u2500\u2500\u2500\u2192 NOW\n old habit \u2014 it stopped"
        ),
        "B1:future-forms" to RuleSpec(
            pattern = "[will] = decide now / promise   [going to] = plan or evidence   [present continuous] = fixed plan with a time",
            examples = listOf("I\u2019**ll help** you! (decided this second)", "Look at those clouds \u2014 it **is going to rain**.", "We\u2019**re meeting** Anne at six."),
            short = "Timescale decides the form: instant choice \u2192 will, plan \u2192 going to, diary entry \u2192 present continuous.",
            wrong = "I will to meet her tomorrow.",
            right = "I will meet her tomorrow.",
            why = "After \u201cwill\u201d use the base verb, no \u201cto\u201d.",
            timeline = "NOW \u2500\u2500\u2500\u2500\u2500\u2192 \u25cf FUTURE\n decided now / planned / fixed"
        ),
        "B1:question-tags" to RuleSpec(
            pattern = "[positive sentence] + [\u2013 tag?]   [negative sentence] + [+ tag?]",
            examples = listOf("You are tired, **aren\u2019t you**?", "She can swim, **can\u2019t she**?", "He doesn\u2019t smoke, **does he**?"),
            short = "The tag mirrors the sentence: opposite sign, same verb, same subject as a pronoun.",
            wrong = "You are tired, isn\u2019t it?",
            right = "You are tired, aren\u2019t you?",
            why = "The tag copies \u201care\u201d and \u201cyou\u201d from the sentence."
        ),
        "B1:connectors" to RuleSpec(
            pattern = "[although] + full clause   [despite] + noun / -ing   [However,] + new sentence",
            examples = listOf("**Although** it was cold, we swam.", "**Despite** the cold, we swam.", "It was cold. **However**, we still swam."),
            short = "Same idea, different words: although needs a little sentence, despite needs just a noun.",
            wrong = "Despite it was cold, we swam.",
            right = "Although it was cold, we swam.",
            why = "\u201cDespite\u201d takes a noun; for a full clause use \u201calthough\u201d."
        ),
        "B2:past-narrative" to RuleSpec(
            pattern = "[was cooking] = the long background   [rang, left] = the short event",
            examples = listOf("I **was cooking** when the phone **rang**.", "The sun **was shining** all morning.", "We **were talking**, then he suddenly **left**."),
            short = "Long action in the background \u2192 was/were + -ing. The short thing that happened \u2192 past simple.",
            wrong = "I cooked when the phone was ringing.",
            right = "I was cooking when the phone rang.",
            why = "Cooking is the long background, ring is the short event.",
            timeline = "PAST \u2500\u2500\u2500\u2500\u2500\u2192 \u2739 \u2500\u2500\u2192 NOW\n was cooking (long) \u2500\u2500\u2500\u2500\u2500\u2506\n              the phone rang \u2739"
        ),
        "B2:reported-speech" to RuleSpec(
            pattern = "He said (that) \u2026 / He told + person \u2026  \u2014 tense moves one step back",
            examples = listOf("\u201cI am tired.\u201d \u2192 He said he **was** tired.", "\u201cI will call.\u201d \u2192 She said she **would** call.", "He **told me** the truth."),
            short = "Report it one step back in time: am \u2192 was, will \u2192 would. Say + words, tell + person.",
            wrong = "He said me he was tired.",
            right = "He told me he was tired.",
            why = "\u201cSay\u201d takes no person; use \u201ctell\u201d for that."
        ),
        "B2:passive" to RuleSpec(
            pattern = "[is / was / are] + [broken, made, spoken]  (+ by Ana\u2026)",
            examples = listOf("The cake **was made** by Ana.", "English **is spoken** here.", "The road **is being repaired**."),
            short = "The thing matters more than who did it: it goes first, then is/was + the after-have form.",
            wrong = "The window was broke.",
            right = "The window was broken.",
            why = "After is/was say broken, not broke: He broke it \u2192 It was broken."
        ),
        "B2:relative-clauses" to RuleSpec(
            pattern = "[who] = people   [which] = things   [that] = both   [whose] = owner",
            examples = listOf("The man **who** called is my boss.", "The film **that** we saw was long.", "The girl **whose** dog ran away is crying."),
            short = "These little glue words connect extra information to a noun.",
            wrong = "The film what we saw was long.",
            right = "The film that we saw was long.",
            why = "\u201cWhat\u201d cannot glue clauses; use \u201cthat\u201d."
        ),
        "B2:future-perfect" to RuleSpec(
            pattern = "[will have finished] + (by 2030, by nine)",
            examples = listOf("By 2030 I **will have finished** the course.", "She **will have arrived** by nine.", "We **will have saved** enough by June."),
            short = "Picture the future moment (by nine). The action is finished before it: will have arrived.",
            wrong = "By nine I will finished it.",
            right = "By nine I will have finished it.",
            why = "\u201cBy a future time\u201d needs \u201cwill have finished\u201d \u2014 not \u201cwill finished\u201d.",
            timeline = "NOW \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2713\u2500\u2713\u2500\u2192 \u25cf by 2030\n (both ticks before \u25cf)"
        ),
        "B2:modals-deduction" to RuleSpec(
            pattern = "[must] = I\u2019m sure it\u2019s true   [might/could] = maybe   [can\u2019t] = I\u2019m sure it\u2019s false",
            examples = listOf("He **must be** tired \u2014 he ran 10 km.", "She **might be** at home.", "That **can\u2019t be** John \u2014 he\u2019s in Rome."),
            short = "Judging how sure you are: must (sure yes), might (maybe), can\u2019t (sure no).",
            wrong = "He mustn\u2019t be the thief.",
            right = "He can\u2019t be the thief.",
            why = "\u201cCan\u2019t\u201d = sure it is false; \u201cmustn\u2019t\u201d means don\u2019t do it."
        ),
        "B2:unreal-past" to RuleSpec(
            pattern = "[wish / if only] + [past simple] = I want it now   [wish] + [past perfect] = I regret the past",
            examples = listOf("I wish I **knew** the answer. (I don\u2019t know now)", "I wish I **had studied** harder. (too late now)", "If only she **had called** me!"),
            short = "Everything after \u201cwish\u201d moves one step back: reality is not what you say.",
            wrong = "I wish I have more time.",
            right = "I wish I had more time.",
            why = "After \u201cwish\u201d, have steps back to had."
        ),
        "B2:conditionals-advanced" to RuleSpec(
            pattern = "[If I had left], [I would have caught] \u2014 both about the past",
            examples = listOf("If I **had left** earlier, I **would have caught** the train.", "If I **had studied** medicine, I **would be** a doctor now. (mixed)", "**Had I known**, I **would have said** something."),
            short = "The truth: I didn\u2019t leave, so I missed the train. Speak that way: if + had left, would have caught.",
            wrong = "If I would have known, I would help.",
            right = "If I had known, I would have helped.",
            why = "Never \u201cIf I would have known\u201d \u2014 say \u201cIf I had known\u201d."
        ),
        "C1:inversion" to RuleSpec(
            pattern = "[Never] + [have I seen]   [Only then] + [did I understand]",
            examples = listOf("**Never have I seen** such a mess.", "**Only then did** I understand.", "**Not only did he lie**, but he also ran."),
            short = "After a drama word like Never at the front, the verb jumps before the subject.",
            wrong = "Never I have seen such a mess.",
            right = "Never have I seen such a mess.",
            why = "It becomes question word order: Never have I seen. When the verb has no helper, use did: Only then did I understand."
        ),
        "C1:third-mixed" to RuleSpec(
            pattern = "[If she had taken], [she wouldn\u2019t have got]   mixed: [If they hadn\u2019t missed], [they would be here now]",
            examples = listOf("If she **had taken** the map, she **wouldn\u2019t have got** lost.", "If they **hadn\u2019t missed** the bus, they **would be** here now. (mixed)", "If I **hadn\u2019t met** you, we **wouldn\u2019t be** friends. (mixed)"),
            short = "All past: had taken \u2192 would have got. Mixed: past cause (had missed), result now (would be).",
            wrong = "If you had asked me, I would tell you.",
            right = "If you had asked me, I would have told you.",
            why = "The past result needs \u201cwould have told\u201d \u2014 would + have + the after-have form."
        ),
        "C1:cleft" to RuleSpec(
            pattern = "[It is/was X that/who \u2026]   [What I need is \u2026]",
            examples = listOf("**It was Ana who** solved it.", "**What I need is** a holiday.", "**The reason why** he left is money."),
            short = "Put the important word first and glue the rest on with who/that/what.",
            wrong = "It was Ana solved it.",
            right = "It was Ana who solved it.",
            why = "\u201cWho/that\u201d connects the person to the rest of the sentence."
        ),
        "C1:participle-clauses" to RuleSpec(
            pattern = "[Having finished], she closed\u2026   [Walking home], I saw\u2026",
            examples = listOf("**Having finished** the email, she closed the laptop.", "**Walking home**, I saw an accident.", "**Written** in 1920, the novel still feels modern."),
            short = "Two actions, one person? Shrink the first action: Walking home\u2026 or Having finished\u2026",
            wrong = "Having wrote the email, she closed the laptop.",
            right = "Having written the email, she closed the laptop.",
            why = "After \u201chaving\u201d say written, not wrote."
        ),
        "B1:quantifiers" to RuleSpec(
            pattern = "[much / little] + water, time   |   [many / few] + friends, days   |   [a lot of] + both",
            examples = listOf("I don\u2019t have **much** time.", "She has **many** friends here.", "We made **a lot of** mistakes."),
            short = "Can you count it? \u2192 many, few. One mass? \u2192 much, little. Not sure? \u2192 a lot of.",
            wrong = "I have much friends here.",
            right = "I have many friends here.",
            why = "\u201CFriends\u201D can be counted, so it takes \u201Cmany\u201D."
        ),
        "B1:prepositions" to RuleSpec(
            pattern = "[good / bad] + at   [interested] + in   [depend / rely] + on   [wait / look] + for",
            examples = listOf("She is **good at** maths.", "It **depends on** the weather.", "I\u2019m **waiting for** the bus."),
            short = "Some words drag the same little word with them every time. Learn the pair, not the logic.",
            wrong = "I am waiting the bus.",
            right = "I am waiting for the bus.",
            why = "\u201CWait\u201D always carries \u201Cfor\u201D before the thing you wait for."
        ),
        "B1:articles-deep" to RuleSpec(
            pattern = "[a] = first time   [the] = we know which   [no word] = all of them: dogs, music, life",
            examples = listOf("I bought **a** coat. **The** coat was expensive.", "**Life** is strange sometimes.", "He plays **the** guitar."),
            short = "First time \u2192 a. Known which \u2192 the. Talking about everything of a kind \u2192 no word.",
            wrong = "The life in cities is fast.",
            right = "Life in cities is fast.",
            why = "\u201CLife\u201D in general takes no word at all."
        ),
        "B2:phrasal-verbs" to RuleSpec(
            pattern = "[verb] + [small word] = new meaning   [give up] = stop   [run out of] = have no more",
            examples = listOf("Don\u2019t **give up** now.", "She **looks after** her brother.", "We **ran out of** milk."),
            short = "Small words build new verbs. With \u201Cit\u201D, the small word jumps after: give it up.",
            wrong = "Don\u2019t give up it.",
            right = "Don\u2019t give it up.",
            why = "With \u201Cit/them\u201D, the small word moves after the object."
        ),
    )

    fun forTopic(id: String): RuleSpec? = map[id]
}

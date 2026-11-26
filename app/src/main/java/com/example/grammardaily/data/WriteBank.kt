package com.example.grammardaily.data

data class WriteTask(
    val starter: String,
    val example: String
)

object WriteBank {
    val tasks: Map<String, WriteTask> = mapOf(
        "A1:be" to WriteTask(
            "Write a true sentence about you or a friend. Start with: my ",
            "My brother is a teacher, and we are very close."
        ),
        "A1:present-simple" to WriteTask(
            "Write your morning. Start with: every morning, I ",
            "Every morning, I wake up at seven and make tea."
        ),
        "A1:articles" to WriteTask(
            "Write about something in your room. Start with: there is ",
            "There is an old chair next to the window."
        ),
        "A1:plurals-there" to WriteTask(
            "Write what is in your kitchen. Start with: there are ",
            "There are two apples and some milk in the fridge."
        ),
        "A2:present-continuous" to WriteTask(
            "Write what is happening in your home right now. Start with: I am ",
            "I am sitting by the window, and my mother is cooking."
        ),
        "A2:past-simple" to WriteTask(
            "Write what you did yesterday. Start with: yesterday, I ",
            "Yesterday, I called my cousin and played football."
        ),
        "A2:comparatives" to WriteTask(
            "Compare two things you know. Start with: my phone is ",
            "My phone is cheaper than my laptop, but the laptop is better for films."
        ),
        "A2:going-to" to WriteTask(
            "Write your plans for tomorrow. Start with: tomorrow, I am going to ",
            "Tomorrow, I am going to clean my desk and call my grandmother."
        ),
        "B1:present-perfect" to WriteTask(
            "Write about your life, with no day named. Start with: I have ",
            "I have visited five countries, but I have never seen the sea."
        ),
        "B1:gerunds-infinitives" to WriteTask(
            "Write what you enjoy and what you want. Start with: I like ",
            "I like swimming, but I want to learn tennis."
        ),
        "B1:conditionals-1-2" to WriteTask(
            "Write one real plan and one dream. Start with: if it rains, ",
            "If it rains, we will stay in. If I had a garden, I would grow peppers."
        ),
        "B1:modals" to WriteTask(
            "Write two house rules, one gentle and one strict. Start with: you ",
            "You should open the windows every day. You must not smoke inside."
        ),
        "B1:used-to" to WriteTask(
            "Write how your life changed. Start with: I used to ",
            "I used to be shy, but now I talk to strangers every day."
        ),
        "B1:future-forms" to WriteTask(
            "Write one fixed plan and one guess. Start with: I am meeting ",
            "I am meeting Sam at six. I think the bus will be late."
        ),
        "B1:question-tags" to WriteTask(
            "Write two small checks you would ask a friend. Start with: you are coming, ",
            "You are coming, are not you? She works there, does not she?"
        ),
        "B1:connectors" to WriteTask(
            "Connect two true things about your day. Start with: I was tired, but ",
            "I was tired, but I finished the report because tomorrow is the deadline."
        ),
        "B2:past-narrative" to WriteTask(
            "Tell a small story in the past. Start with: I was walking ",
            "I was walking home when I saw a fox; it had crossed the road and was watching me."
        ),
        "B2:reported-speech" to WriteTask(
            "Repeat what someone told you. Start with: yesterday, she told me ",
            "Yesterday, she told me that she would call me the next day."
        ),
        "B2:passive" to WriteTask(
            "Describe how something gets done where you work or study. Start with: every morning, the emails ",
            "Every morning, the emails are checked by the manager, and new tasks are sent out."
        ),
        "B2:relative-clauses" to WriteTask(
            "Describe a person in one connected sentence. Start with: the woman ",
            "The woman who lives next door, whose dog barks all night, is a nurse."
        ),
        "B2:future-perfect" to WriteTask(
            "Write where you will be by next year. Start with: by next year, I will have ",
            "By next year, I will have finished my studies and moved to a new city."
        ),
        "B2:modals-deduction" to WriteTask(
            "Guess about a friend who is not answering. Start with: he ",
            "He must be at work, because his bike is gone. He cannot be at home; the lights are off."
        ),
        "B2:unreal-past" to WriteTask(
            "Write one small regret. Start with: I wish I ",
            "I wish I had said sorry, because then she would not have left."
        ),
        "B2:conditionals-advanced" to WriteTask(
            "Build one long sentence about a past choice. Start with: if I had left earlier, ",
            "If I had left earlier, I would not have missed the train, and I would be home now."
        ),
        "C1:inversion" to WriteTask(
            "Start with a negative word for strong style. Start with: never ",
            "Never had I seen such silence; only then did I understand."
        ),
        "C1:third-mixed" to WriteTask(
            "Mix a past cause with the present result. Start with: if I had studied ",
            "If I had studied law, I would be a lawyer today, and I would have saved years."
        ),
        "C1:cleft" to WriteTask(
            "Put the surprising part first. Start with: what surprised me ",
            "What surprised me was how quickly she adapted; it was her calm attitude that helped most."
        ),
        "C1:participle-clauses" to WriteTask(
            "Shrink a long sentence into a short one. Start with: walking home, ",
            "Walking home, I met my boss; tired as I was, I smiled anyway."
        )
    )
}

object ChunkBank {
    val byTopic: Map<String, List<String>> = mapOf(
        "A1:be" to listOf(
            "I'm hungry \u2014 in chat say I'm; I am is for careful writing.",
            "She's / They're \u2014 's and 're glue who to is/are."
        ),
        "A1:present-simple" to listOf(
            "I / you / we / they wake up \u2014 but he / she / it wakes up.",
            "Usually sits right after the doer: we usually eat."
        ),
        "A1:articles" to listOf(
            "a / an = any one \u2014 the = we both know which.",
            "a box \u00b7 an apple \u00b7 the sun"
        ),
        "A1:plurals-there" to listOf(
            "one dog \u2192 two dogs (just add s).",
            "There's a dog \u00b7 but two dogs \u2192 there are"
        ),
        "A2:present-continuous" to listOf(
            "I am + verb-ing \u00b7 we/you/they are \u00b7 he/she/it is",
            "Right now = am/is/are + -ing."
        ),
        "A2:past-simple" to listOf(
            "yesterday, last night, in 2019 \u2192 past form: called, played, went",
            "Talked already became talked \u2014 no extra helper needed."
        ),
        "A2:comparatives" to listOf(
            "cheap \u2192 cheaper \u00b7 big \u2192 bigger (double the last letter)",
            "long words: more beautiful than \u00b7 top word: the best"
        ),
        "A2:going-to" to listOf(
            "I'm going to + action = I decided this before.",
            "Those clouds are heavy \u2014 it's going to rain."
        ),
        "B1:present-perfect" to listOf(
            "have visited = done, result now.",
            "never / ever / already / yet stay with have or has."
        ),
        "B1:gerunds-infinitives" to listOf(
            "like / love / hate + verb-ing",
            "want / need / hope + to + verb"
        ),
        "B1:conditionals-1-2" to listOf(
            "If it rains \u2192 will stay.",
            "If it rained \u2192 would stay (never 'if it will')."
        ),
        "B1:modals" to listOf(
            "must = the boss said \u00b7 must not = do not do it!",
            "do not have to = free to skip."
        ),
        "B1:used-to" to listOf(
            "used to = old habit, finished now: I used to smoke.",
            "Ask it: did you use to? Say no: I did not use to."
        ),
        "B1:future-forms" to listOf(
            "will be = a guess \u00b7 going to = the plan on paper.",
            "I'm meeting = already in my calendar."
        ),
        "B1:question-tags" to listOf(
            "positive sentence \u2192 negative tail: is not it? do not you?",
            "the little question at the end wants a yes or no."
        ),
        "B1:connectors" to listOf(
            "but = opposite \u00b7 because = the why.",
            "although / though = a surprise."
        ),
        "B2:past-narrative" to listOf(
            "was walking = background \u00b7 saw = sudden action.",
            "had crossed = even earlier than that."
        ),
        "B2:reported-speech" to listOf(
            "said + what she said \u00b7 told me + what she said.",
            "yesterday \u2192 the day before \u00b7 I'll call \u2192 she would call"
        ),
        "B2:passive" to listOf(
            "are checked = the doer is not the news.",
            "was built / is made \u2014 the maker often disappears."
        ),
        "B2:relative-clauses" to listOf(
            "who = person \u00b7 which = thing \u00b7 whose = owns it.",
            "that can replace who or which in tight sentences."
        ),
        "B2:future-perfect" to listOf(
            "by 2030 \u2192 will have finished = done before then.",
            "by + future time is the signal for will have + form."
        ),
        "B2:modals-deduction" to listOf(
            "must be = I am sure it is true.",
            "cannot be = impossible \u00b7 might be = maybe."
        ),
        "B2:unreal-past" to listOf(
            "I wish I had known = the past was different, I am sad.",
            "If I had known \u2192 would have come"
        ),
        "B2:conditionals-advanced" to listOf(
            "the earlier past (had left) steers everything after it.",
            "past cause, result now: if I had slept, I would feel fine today."
        ),
        "C1:inversion" to listOf(
            "Never put I first: never have I seen.",
            "only then did I understand \u2014 the helper jumps before the doer."
        ),
        "C1:third-mixed" to listOf(
            "different times, one sentence: had studied (past) \u2192 would be (now).",
            "the if part never takes would \u2014 only the result part does."
        ),
        "C1:cleft" to listOf(
            "What I need is \u2026 \u2014 slide what matters out front.",
            "It was Sam who fixed it."
        ),
        "C1:participle-clauses" to listOf(
            "seeing the house, I stopped \u2014 two sentences become one.",
            "the -ing / -ed part keeps the same doer as the main sentence."
        )
    )
}

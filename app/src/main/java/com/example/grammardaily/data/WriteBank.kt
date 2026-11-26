package com.example.grammardaily.data

data class WriteTask(
    val starter: String,
    val example: String
)

object WriteBank {
    val tasks: Map<String, List<WriteTask>> = mapOf(
        "A1:articles" to listOf(
            WriteTask(
                "Write about something in your room. Start with: there is ",
                "There is an old chair next to the window."
            ),
            WriteTask(
                "Write about your city. Start with: my city has ",
                "My city has a big park and an old market."
            ),
            WriteTask(
                "Write about a meal you love and why. Start with: the ",
                "The bread here is fresh because the bakery opens at five."
            )
        ),
        "A1:be" to listOf(
            WriteTask(
                "Write a true sentence about you or a friend. Start with: my ",
                "My brother is a teacher, and we are very close."
            ),
            WriteTask(
                "Describe a friend in one honest line. Start with: my friend ",
                "My friend is very kind, but she is always late."
            ),
            WriteTask(
                "Say where your family is from. Start with: we ",
                "We are from the north, and our town is small."
            )
        ),
        "A1:plurals-there" to listOf(
            WriteTask(
                "Write what is in your kitchen. Start with: there are ",
                "There are two apples and some milk in the fridge."
            ),
            WriteTask(
                "Write what is missing from your kitchen. Start with: in my kitchen, there is no ",
                "In my kitchen, there is no sugar, but there are three cups."
            ),
            WriteTask(
                "Count something you own. Start with: I have ",
                "I have two pairs of shoes and many old books."
            )
        ),
        "A1:present-simple" to listOf(
            WriteTask(
                "Write your morning. Start with: every morning, I ",
                "Every morning, I wake up at seven and make tea."
            ),
            WriteTask(
                "Write what a person in your family does every day. Start with: he ",
                "He works at a shop, and he closes at eight."
            ),
            WriteTask(
                "Write what you never do on Sundays. Start with: on sundays, I ",
                "On Sundays, I never wake up early."
            )
        ),
        "A2:comparatives" to listOf(
            WriteTask(
                "Compare two things you know. Start with: my phone is ",
                "My phone is cheaper than my laptop, but the laptop is better for films."
            ),
            WriteTask(
                "Compare your town with a town nearby. Start with: my town is ",
                "My town is smaller, but the streets are cleaner."
            ),
            WriteTask(
                "Compare you now with you five years ago. Start with: today I am ",
                "Today I am calmer than I was five years ago."
            )
        ),
        "A2:going-to" to listOf(
            WriteTask(
                "Write your plans for tomorrow. Start with: tomorrow, I am going to ",
                "Tomorrow, I am going to clean my desk and call my grandmother."
            ),
            WriteTask(
                "Write your plans for the weekend. Start with: this weekend, we are going to ",
                "This weekend, we are going to visit the lake if the weather is good."
            ),
            WriteTask(
                "Write one thing you decided to stop doing. Start with: I am not going to ",
                "I am not going to check my phone at dinner anymore."
            )
        ),
        "A2:past-simple" to listOf(
            WriteTask(
                "Write what you did yesterday. Start with: yesterday, I ",
                "Yesterday, I called my cousin and played football."
            ),
            WriteTask(
                "Write two things that went wrong last week. Start with: last week, ",
                "Last week, I lost my keys and missed the bus."
            ),
            WriteTask(
                "Write about your last birthday. Start with: on my birthday, we ",
                "On my birthday, we cooked together and told old stories."
            )
        ),
        "A2:present-continuous" to listOf(
            WriteTask(
                "Write what is happening in your home right now. Start with: I am ",
                "I am sitting by the window, and my mother is cooking."
            ),
            WriteTask(
                "Write what the people around you are doing at this moment. Start with: my neighbours ",
                "My neighbours are arguing about a parking space."
            ),
            WriteTask(
                "Describe a photo you took this week. Start with: in the photo, I ",
                "In the photo, I am laughing at something silly."
            )
        ),
        "B1:conditionals-1-2" to listOf(
            WriteTask(
                "Write one real plan and one dream. Start with: if it rains, ",
                "If it rains, we will stay in. If I had a garden, I would grow peppers."
            ),
            WriteTask(
                "Write comfort for a friend with a problem. Start with: if you rest, ",
                "If you rest, you will feel better. If you talked to her, she would understand."
            ),
            WriteTask(
                "Write one possible future and one impossible one. Start with: if I win, ",
                "If I win, I will travel slowly. If I were rich, I would buy my mother a house."
            )
        ),
        "B1:connectors" to listOf(
            WriteTask(
                "Connect two true things about your day. Start with: I was tired, but ",
                "I was tired, but I finished the report because tomorrow is the deadline."
            ),
            WriteTask(
                "Say one thing happened despite another. Start with: although I was busy, ",
                "Although I was busy, I called my grandmother because I miss her."
            ),
            WriteTask(
                "Chain three events with so and because. Start with: the bus was late, so ",
                "The bus was late, so I walked, because the traffic was worse than ever."
            )
        ),
        "B1:future-forms" to listOf(
            WriteTask(
                "Write one fixed plan and one guess. Start with: I am meeting ",
                "I am meeting Sam at six. I think the bus will be late."
            ),
            WriteTask(
                "Write what you will not do tonight, plus one guess. Start with: tonight, I am not ",
                "Tonight, I am not working late. I think my friends will call."
            ),
            WriteTask(
                "Write a promise and a guess about next month. Start with: next month, I will ",
                "Next month, I will save money, and I believe prices will rise."
            )
        ),
        "B1:gerunds-infinitives" to listOf(
            WriteTask(
                "Write what you enjoy and what you want. Start with: I like ",
                "I like swimming, but I want to learn tennis."
            ),
            WriteTask(
                "Write a plan and what blocks it. Start with: I want to ",
                "I want to start running again, but I do not feel like waking up early."
            ),
            WriteTask(
                "Write what you avoid and why. Start with: I avoid ",
                "I avoid arguing at night because sleeping angry is worse."
            )
        ),
        "B1:modals" to listOf(
            WriteTask(
                "Write two house rules, one gentle and one strict. Start with: you ",
                "You should open the windows every day. You must not smoke inside."
            ),
            WriteTask(
                "Write advice to your younger self. Start with: you should ",
                "You should worry less, and you must not leave school."
            ),
            WriteTask(
                "Write what is allowed where you live. Start with: here, you ",
                "Here, you can park at night, but you must not make noise after ten."
            )
        ),
        "B1:present-perfect" to listOf(
            WriteTask(
                "Write about your life, with no day named. Start with: I have ",
                "I have visited five countries, but I have never seen the sea."
            ),
            WriteTask(
                "Write about something brand new in your life, with no date. Start with: I have just ",
                "I have just started a new job, and my desk is still empty."
            ),
            WriteTask(
                "Write the news about a friend. Start with: she has ",
                "She has moved to Berlin, and she has already found a flat."
            )
        ),
        "B1:question-tags" to listOf(
            WriteTask(
                "Write two small checks you would ask a friend. Start with: you are coming, ",
                "You are coming, are not you? She works there, does not she?"
            ),
            WriteTask(
                "Check the weather and a plan. Start with: it is hot today, ",
                "It is hot today, is not it? We are still meeting at seven, are not we?"
            ),
            WriteTask(
                "Check the news about a friend's week. Start with: your interview went well, ",
                "Your interview went well, did not it? You start on Monday, do not you?"
            )
        ),
        "B1:used-to" to listOf(
            WriteTask(
                "Write how your life changed. Start with: I used to ",
                "I used to be shy, but now I talk to strangers every day."
            ),
            WriteTask(
                "Write about an old habit that is gone. Start with: I did not use to ",
                "I did not use to like coffee, but now I drink two cups."
            ),
            WriteTask(
                "Write how your town used to be. Start with: my town used to ",
                "My town used to be quiet, but now the traffic never stops."
            )
        ),
        "B2:conditionals-advanced" to listOf(
            WriteTask(
                "Build one long sentence about a past choice. Start with: if I had left earlier, ",
                "If I had left earlier, I would not have missed the train, and I would be home now."
            ),
            WriteTask(
                "Warn a friend who almost signed a bad contract. Start with: if you had not checked ",
                "If you had not checked the contract, you would have lost the deposit."
            ),
            WriteTask(
                "One job choice, two lives. Start with: if I had taken that job, ",
                "If I had taken that job, I would not be living here, and I would never have met you."
            )
        ),
        "B2:future-perfect" to listOf(
            WriteTask(
                "Write where you will be by next year. Start with: by next year, I will have ",
                "By next year, I will have finished my studies and moved to a new city."
            ),
            WriteTask(
                "Write what will be done by the end of this month. Start with: by the end of this month, I will have ",
                "By the end of this month, I will have read four books and saved some money."
            ),
            WriteTask(
                "Write what will be done before a small person grows up. Start with: by the time my cousin starts school, I will have ",
                "By the time my cousin starts school, I will have finished my course and found work."
            )
        ),
        "B2:modals-deduction" to listOf(
            WriteTask(
                "Guess about a friend who is not answering. Start with: he ",
                "He must be at work, because his bike is gone. He cannot be at home; the lights are off."
            ),
            WriteTask(
                "A note appeared on your door. Work out who left it. Start with: the note ",
                "The note must be from my neighbour; it cannot be from the postman, because he never writes."
            ),
            WriteTask(
                "Someone has eaten the cake. Say who. Start with: it ",
                "It must have been my brother; he cannot have been at his friend's house."
            )
        ),
        "B2:passive" to listOf(
            WriteTask(
                "Describe how something gets done where you work or study. Start with: every morning, the emails ",
                "Every morning, the emails are checked by the manager, and new tasks are sent out."
            ),
            WriteTask(
                "Write how your favourite dish is made. Start with: in my country, this dish ",
                "In my country, this dish is cooked slowly, and it is served with rice."
            ),
            WriteTask(
                "Write a news line you might read, in the passive. Start with: the bridge ",
                "The bridge was closed last night after the storm, and it will be repaired next week."
            )
        ),
        "B2:past-narrative" to listOf(
            WriteTask(
                "Tell a small story in the past. Start with: I was walking ",
                "I was walking home when I saw a fox; it had crossed the road and was watching me."
            ),
            WriteTask(
                "Write a story with a surprise. Start with: I had just closed the door when ",
                "I had just closed the door when my phone rang from inside the flat."
            ),
            WriteTask(
                "Write about a journey that almost went wrong. Start with: we were driving ",
                "We were driving home when the rain started; by the time we saw the river, it had covered the road."
            )
        ),
        "B2:relative-clauses" to listOf(
            WriteTask(
                "Describe a person in one connected sentence. Start with: the woman ",
                "The woman who lives next door, whose dog barks all night, is a nurse."
            ),
            WriteTask(
                "Describe a place in one connected sentence. Start with: the cafe ",
                "The cafe where we first met, which is near the station, has closed."
            ),
            WriteTask(
                "Describe an object you love in one sentence. Start with: the jacket ",
                "The jacket that I bought in Istanbul, which was too expensive, is still my favourite."
            )
        ),
        "B2:reported-speech" to listOf(
            WriteTask(
                "Repeat what someone told you. Start with: yesterday, she told me ",
                "Yesterday, she told me that she would call me the next day."
            ),
            WriteTask(
                "Report a promise someone made you. Start with: he promised me that ",
                "He promised me that he would help me move the next weekend."
            ),
            WriteTask(
                "Report a question someone asked you. Start with: she asked me whether ",
                "She asked me whether I had seen her keys that morning."
            )
        ),
        "B2:unreal-past" to listOf(
            WriteTask(
                "Write one small regret. Start with: I wish I ",
                "I wish I had said sorry, because then she would not have left."
            ),
            WriteTask(
                "Write what you wish you had done sooner. Start with: I wish I had ",
                "I wish I had listened to my father, because he was right about the job."
            ),
            WriteTask(
                "Write a wish with if only. Start with: if only we ",
                "If only we had taken the earlier train; we would not have missed her."
            )
        ),
        "C1:cleft" to listOf(
            WriteTask(
                "Put the surprising part first. Start with: what surprised me ",
                "What surprised me was how quickly she adapted; it was her calm attitude that helped most."
            ),
            WriteTask(
                "Point at the one frightening thing. Start with: it was the silence ",
                "It was the silence that frightened me; all I wanted was a normal evening."
            ),
            WriteTask(
                "Explain your reason, front-loaded. Start with: the reason I stayed ",
                "The reason I stayed was the students; what they taught me was patience."
            )
        ),
        "C1:inversion" to listOf(
            WriteTask(
                "Start with a negative word for strong style. Start with: never ",
                "Never had I seen such silence; only then did I understand."
            ),
            WriteTask(
                "Start with a rare-event word, inverted. Start with: rarely ",
                "Rarely does a week pass without a new idea; not once have I been bored."
            ),
            WriteTask(
                "Start with only after, inverted. Start with: only after ",
                "Only after the meeting did I understand him; hardly had we sat down when the news arrived."
            )
        ),
        "C1:participle-clauses" to listOf(
            WriteTask(
                "Shrink a long sentence into a short one. Start with: walking home, ",
                "Walking home, I met my boss; tired as I was, I smiled anyway."
            ),
            WriteTask(
                "Start with having done something. Start with: having read the report, ",
                "Having read the report, she called a meeting; the decision, having been delayed twice, could not wait."
            ),
            WriteTask(
                "Start with a built/made description. Start with: built in 1890, ",
                "Built in 1890, the house leaned slightly, its windows rattling in the wind."
            )
        ),
        "C1:third-mixed" to listOf(
            WriteTask(
                "Mix a past cause with the present result. Start with: if I had studied ",
                "If I had studied law, I would be a lawyer today, and I would have saved years."
            ),
            WriteTask(
                "Invert the if for style. Start with: had she known ",
                "Had she known the cost, she would not have signed, and today she would still own her shop."
            ),
            WriteTask(
                "A storm changes everything, past and present. Start with: if the storm had not come, ",
                "If the storm had not come, the harvest would have been fine, and we would not be borrowing money now."
            )
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

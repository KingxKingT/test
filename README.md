# Grammar Daily

A native Android app that tests your English grammar, finds your level, and then
teaches you one grammar lesson every day until you have mastered the whole path.

## How it works

1. **Teaching first — the test is optional** — you can skip straight into daily
   lessons ("No test — start from B1"). The short 15-question check (mixed
   type / reorder / fix / multiple-choice across 5 bands, A1→C1) is there only if
   you want your level and a "study this next" list. Daily practice has **no pass
   or fail gate**: every finished set counts as a lesson done, and mistakes just
   schedule more teaching.
2. **One lesson per day — show, don't explain** — every rule fits on one screen:
   a color-coded pattern ([She] + [goes], not "go"), 2-3 examples with the changing
   word highlighted, one common mistake (✘ wrong vs ✔ right) and a monospace
   timeline for tenses. Max 3 short sentences of explanation, in simple A2-level
   words. B1/B2 lessons open with a guided-discovery "Notice" step. The lesson flow
   is **See → Try (1 easy question, instant feedback) → Practice (5 mixed) →
   Review (spaced repetition over days)**.
3. **Real exercises, not just picking** — multiple choice is capped at ~20% of any
   set. The rest: **type the missing word**, **rebuild the sentence** (tap the words
   in order), **fix the mistake** and **listen & type** (offline text-to-speech).
   Wrong answer → the right answer plus a one-line why.
4. **AI judge for written answers (offline, open source)** — typed answers are not
   graded by rigid string matching. A layered checker accepts typos, free word
   order and different wording that carries the target grammar; you can import a
   small open-source model (Gemma 3 1B .task or Qwen 0.6B GGUF, MediaPipe on-device
   runtime, like Layla) and it judges every written answer YES/NO, fully offline,
   with no chat. **Download it right in the app** (one button, ~529 MB Gemma 3 1B)
   or import any compatible file. Still unconvinced? "My answer makes sense too"
   accepts your own answer and shows the model answer next to it.
5. **AI grammar report after the test** — at the end of the level check, the AI
   reads your actual written answers (not just right/wrong marks) and answers:
   what your grammar level is and what to learn next, as a short list. Without a
   model installed it gives the same report from your per-topic scores.
6. **Weak-spot tracking** — mistakes are counted per rule; weak rules come back more
   often in reviews and extra practice, and the home screen shows a "Weak spots" card.
7. **Spaced repetition (mastery = proof over days, not one session)** — after the
   lesson, a review waits tomorrow, in 3 days and in 7 days (Leitner boxes), using a
   different exercise type each time. Fail one and the rule is re-taught before a
   fresh question. A lesson is only "mastered" after all reviews pass.
8. **Daily Verb Widget** — pick up to 3 verbs (or use the level-based suggestions);
   a second home-screen widget shows one verb at a time in a random tense
   (present simple → conditional), random form (positive / negative / question),
   with the sentence, the tense name, a "when to use it" line, one extra example and
   the verb's forms (go / went / gone / going). Tapping opens a mini exercise on that
   exact card. Mark cards "**I know this**" or "**Show again**" — show-again cards
   return about twice as often. Rotation honors your chosen times-per-day (1/2/3/6)
   and quiet hours. All sentences are generated offline — no AI calls.
9. **Level-up test** — all topics of a level mastered → 8 hard questions, need 6
   right to move up (A1 → A2 → B1 → B2 → C1).
10. **Reminders without opening the app** — a notification with a micro-review roughly
   every 2 hours, plus a level/streak home-screen widget showing progress. Tapping
   either widget opens the app.

## Built on learning science (how people learn grammar fastest)

Every method in this app is backed by published second-language-acquisition research:

- **Study a rule on purpose, then practice it** — meta-analyses (Norris & Ortega 2000; Spada & Tomita 2010) found explicit instruction beats hoping to absorb grammar from exposure. Every lesson here shows a short rule first.
- **Retrieve from memory, don't re-read** — Kim & Webb's 2022 meta-analysis (48 experiments) showed self-testing and spaced recall beat re-studying. Exercises force production; reviews come back on expanding intervals.
- **Self-correct before seeing the answer** — Lyster & Saito's 2010 meta-analysis found prompts (pushing learners to repair their own error) beat recasts (showing the correct version). So one wrong answer gets a nudge and a second try before the model answer appears.
- **Produce the pattern yourself, in your own words** — structural priming studies (McDonough and colleagues) show that producing a just-learned pattern, varied across your own vocabulary, builds the abstract rule. That is why every practice set ends with a "write your own sentence" exercise read by the AI judge.
- **Learn chunks, not just rules** — research on formulaic language (Wray, Conklin & Schmitt) estimates 50–80% of fluent speech is fixed multi-word blocks. Every lesson card includes "say these as one block" chunks.

## Curriculum (v1)

| Level | Topics |
|---|---|
| A1 Beginner | verb to be · present simple · a/an/the · plurals + there is/are |
| A2 Elementary | present continuous · past simple · comparatives/superlatives · going to |
| B1 Intermediate | present perfect vs past simple · used to & would · future forms · question tags · gerunds & infinitives · 1st/2nd conditionals · modals · connectors (although/however/despite) |
| B2 Upper-Intermediate | narrative tenses · reported speech · passive · relative clauses · future perfect & continuous · modals of deduction · unreal past (wish/if only) · advanced conditionals (inverted/mixed) |
| C1 Advanced | inversion · third/mixed conditionals · cleft sentences · participle clauses |

The main teaching path is **B1 → B2**: all 16 topics at those levels carry a guided
-discovery "Notice" step and richer, harder question sets, aligned with the CEFR
B1/B2 grammar syllabus (British Council / English Grammar Profile).

148 practice questions + the 15-question optional placement test are bundled offline in
`app/src/main/java/com/example/grammardaily/data/GrammarCurriculum.kt` and
`QuestionBank.kt`.

## Tech

- Kotlin + Jetpack Compose (Material 3), single Activity
- Jetpack Glance for the home-screen widget
- WorkManager for the 2-hourly reminder + widget refresh
- SharedPreferences for progress (offline-first, no account needed)

## Build

```bash
# in Android Studio: File > Open, select this folder, then Run
./gradlew assembleDebug
```

Requires JDK 17. Android Studio will set up the Gradle wrapper automatically on first
sync (or run `gradle wrapper` once if you build from the command line).

## Project layout

```
app/src/main/java/com/example/grammardaily/
  MainActivity.kt        # navigation between home / test / lesson / quiz
  GrammarApp.kt          # channel + reminder scheduling on startup
  data/
    Models.kt            # Level, Topic, QuizQuestion
    GrammarCurriculum.kt # lessons + question bank (offline content)
    Placement.kt         # full test assembly + score-to-level mapping
    ProgressStore.kt     # persistent progress (level, mastery, streak)
  notify/
    ReminderWorker.kt    # periodic notifications + widget refresh
  ui/
    Screens.kt           # Home, Lesson, Quiz composables
  widget/
    GrammarWidget.kt     # Glance home-screen widget
```

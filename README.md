# Grammar Daily

A native Android app that tests your English grammar, finds your level, and then
teaches you one grammar lesson every day until you have mastered the whole path.

## How it works

1. **Full grammar test (harder)** — 25 questions in 5 difficulty bands (A1→C1),
   4 options each, with trap distractors that catch guessing (e.g. "Did you went…?",
   "Never I have…", dangling participles). Scoring is per band: a band only counts
   if you get 3 of its 5 right, and a failed band caps your result.
2. **One lesson per day, taught for slow learners** — every lesson follows the
   *guided discovery* method used by the best ELT courses: you first see contrast
   sentences (right vs. wrong wording) and answer a noticing question that makes you
   spot the pattern yourself — only then does the rule unfold in plain language
   (explanation, examples, one memory tip). Research on inductive learning + explicit
   instruction shows this "notice before the rule" order sticks far better than
   reading a rule first. Then a 5-question practice where a hint reveals a simplified
   rule before you answer. Pass 4 of 5 to unlock the reviews. Different angles each
   time: fill-in, find-the-mistake, trap forms.
3. **Spaced repetition (mastery = proof over days, not one session)** — after the
   lesson, a 1-question review waits for you tomorrow, then in 3 days, then in 7 days
   (Leitner boxes). Fail a review and the app re-teaches the rule and asks a
   *different* question until you get it right — repetition that teaches, not repeats.
   A lesson is only "mastered" after all reviews pass.
4. **Level-up test** — all topics of a level mastered → 8 hard questions, need 6
   right to move up (A1 → A2 → B1 → B2 → C1).
5. **Reminders without opening the app** — a notification with a micro-review roughly
   every 2 hours, plus a home-screen widget that always shows your level, streak,
   what's due and your progress. Tapping it opens the app.

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

148 practice questions + the 25-question placement test are bundled offline in
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

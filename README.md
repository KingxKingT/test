# Grammar Daily

A native Android app that tests your English grammar, finds your level, and then
teaches you one grammar lesson every day until you have mastered the whole path.

## How it works

1. **Full grammar test** — 20 multiple-choice questions that climb from Beginner (A1)
   to Advanced (C1). Your score sets your starting level.
2. **One lesson per day** — each lesson explains a topic (explanation, examples, a
   memory tip) and ends with a 3-question practice round. Score 2/3 or better and the
   topic is marked as mastered.
3. **Level-up test** — when all 4 topics of a level are mastered, a 6-question test
   moves you up (A1 → A2 → B1 → B2 → C1).
4. **Reminders that never sleep** — a WorkManager job fires a notification with a
   micro-review (the current lesson's memory tip) roughly every 2 hours, and the
   **home-screen widget** always shows your level, streak, today's lesson and
   progress. Tapping it opens the app. Even on days with no lesson done, the widget
   and notifications keep nudging you so you don't forget what you learned.
5. **Streaks** — study on consecutive days and the streak counter (in the app and on
   the widget) keeps you going.

## Curriculum (v1)

| Level | Topics |
|---|---|
| A1 Beginner | verb to be · present simple · a/an/the · plurals + there is/are |
| A2 Elementary | present continuous · past simple · comparatives/superlatives · going to |
| B1 Intermediate | present perfect · gerunds & infinitives · 1st/2nd conditionals · modals |
| B2 Upper-Intermediate | narrative tenses · reported speech · passive · relative clauses |
| C1 Advanced | inversion · third/mixed conditionals · cleft sentences · participle clauses |

60 practice questions + the 20-question placement test are bundled offline in
`app/src/main/java/com/example/grammardaily/data/GrammarCurriculum.kt`.

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

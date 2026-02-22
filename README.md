# Habit Tracker (Java Swing)

Simple, modular Habit Tracker app with strict layered design.

## Stack
- Java 17+
- Swing UI
- JSON persistence (`data/habits.json`)
- Jackson for JSON read/write

## Structure
- `src/model` - data model
- `src/service` - business logic
- `src/repository` - file and JSON handling
- `src/ui` - presentation layer
- `src/Main.java` - app entry point

## Features
- Add habit
- View habits
- Edit habit name
- Delete habit
- Mark habit complete for today
- View current streak, longest streak, completion %
- Save/load from JSON file

## Rules Implemented
- Habit name required, 1-50 characters
- No duplicate names
- One completion per habit per date
- No habit deadlines (ongoing habits)
- If streak breaks, current streak resets and starts again when user marks completion later

## Corrupted JSON Handling
If `data/habits.json` is corrupted:
- App shows a short warning
- App starts with empty habits
- User can continue and save to replace bad data

## Build and Run
### Prerequisites
- Java 17+ installed
- Maven installed

### Commands
```powershell
mvn clean compile
mvn exec:java -Dexec.mainClass=Main
```

If `exec:java` is unavailable, run after compile using your IDE.

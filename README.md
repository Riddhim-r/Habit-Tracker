# Habit Tracker (Java Swing)

A simple, modular desktop Habit Tracker built with Java Swing.

This project is designed to be readable and beginner-friendly while still following proper layered architecture.

## Table of Contents
- Overview
- Features
- Tech Stack
- Architecture
- Project Structure
- Setup
- How to Run
- How to Use
- Business Rules
- Data Storage
- Error Handling
- Manual Testing Guide
- Future Improvements

## Overview
Habit Tracker helps users:
- Create habits
- Track daily completion
- View streak progress
- View completion percentage
- Persist data locally in JSON

The app uses exactly **3 pages**:
1. Dashboard
2. Add/Edit Habit
3. Habit Details

## Features
### Core Features
- Add habit
- View all habits
- Edit habit name
- Delete habit
- Mark habit completed for today
- View current streak
- View longest streak
- View completion percentage
- Save and load data from JSON

### UI Features
- Simple Swing interface
- Lavender and blue color palette
- Clear page navigation

## Tech Stack
- **Language:** Java 17+ (project configured for Java 17)
- **UI:** Java Swing
- **Data Format:** JSON
- **Persistence:** Local file (`data/habits.json`)
- **JSON Library:** Jackson (`jackson-databind`, `jackson-datatype-jsr310`)
- **Build Tool:** Maven

## Architecture
This project follows a strict layered structure:

Presentation Layer (`ui`)  
-> Service Layer (`service`)  
-> Data Access Layer (`repository`)  
-> File Storage (`data/habits.json`)

### Layer Responsibilities
- `model`: Data model only (`Habit`)
- `service`: Business logic only (`HabitService`)
- `repository`: File/JSON read-write only (`HabitRepository`)
- `ui`: Swing pages and user interactions only

No business logic is placed in UI classes.

## Project Structure
```text
Habit Tracker/
|-- src/
|   |-- model/
|   |   `-- Habit.java
|   |-- service/
|   |   |-- HabitService.java
|   |   `-- ServiceResult.java
|   |-- repository/
|   |   `-- HabitRepository.java
|   |-- ui/
|   |   |-- HabitTrackerFrame.java
|   |   |-- DashboardPage.java
|   |   |-- AddEditHabitPage.java
|   |   `-- HabitDetailsPage.java
|   `-- Main.java
|-- data/
|   `-- habits.json
|-- pom.xml
`-- README.md
```

## Setup
## Prerequisites
- Java 17 or higher installed
- Maven installed and available in PATH

### Verify Installations
```powershell
java -version
javac -version
mvn -v
```

If `mvn` is not recognized on Windows:
```powershell
winget install -e --id Apache.Maven
```
Then restart terminal and check again with `mvn -v`.

## How to Run
From project root (`Habit Tracker`):

```powershell
mvn clean compile
mvn exec:java
```

If running from IDE:
- Open as Maven project
- Run `Main.java`

## How to Use
### 1. Dashboard Page
- View all habits
- View quick stats:
  - Total habits
  - Completed today
  - Average completion %
- Navigate to Add/Edit page or Habit Details page

### 2. Add/Edit Habit Page
- Enter habit name and click **Add Habit**
- Select a habit and click **Update Selected** to rename
- Select a habit and click **Delete Selected** to remove

### 3. Habit Details Page
- Select a habit from dropdown
- Click **Mark Today Complete**
- View:
  - Created date
  - Current streak
  - Longest streak
  - Completion %

## Business Rules
- Habit name is required
- Habit name length: 1 to 50 characters
- Habit names must be unique (case-insensitive)
- One completion per habit per date
- Habits are ongoing (no deadline/end date)
- If a streak breaks:
  - Current streak resets
  - A new current streak starts when user completes again
- Longest streak remains historical maximum

## Data Storage
Data is saved in:
- `data/habits.json`

### JSON Shape
```json
[
  {
    "id": 1,
    "name": "Exercise",
    "createdDate": "2026-02-01",
    "completedDates": [
      "2026-02-02",
      "2026-02-03"
    ]
  }
]
```

## Error Handling
- If `data/habits.json` does not exist:
  - App creates it automatically
- If JSON is corrupted:
  - App shows a short warning
  - App starts with empty habits
  - App continues running (no crash)
  - User can save new data to replace corrupted file
- If save fails:
  - App shows a short error message

## Streak and Completion Logic
### Current Streak
Counts consecutive completed days backward from today.

### Longest Streak
Counts the maximum consecutive run in completion history.

### Completion Percentage
```text
completionRate = (completedDays / totalDaysSinceCreation) * 100
```
Displayed rounded to 2 decimals.

## Manual Testing Guide
1. Add a new habit -> should appear in lists.
2. Try empty or long name -> should show validation warning.
3. Try duplicate name -> should show duplicate warning.
4. Mark today complete -> should update streak/stats.
5. Mark again same day -> should show "Already marked for today".
6. Edit habit name -> should update in all pages.
7. Delete habit -> should remove from all pages.
8. Restart app -> data should persist.
9. Corrupt `data/habits.json` manually -> app should warn and continue with empty list.

## Future Improvements
- Unit tests (JUnit)
- CSV export
- Weekly/monthly analytics
- Better filtering and sorting
- Packaging as runnable `.jar` or installer

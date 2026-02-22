package service;

import model.Habit;
import repository.HabitRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HabitService {
    private final HabitRepository habitRepository;
    private List<Habit> habits;
    private int nextId;
    private String loadWarningMessage;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
        this.habits = new ArrayList<>();
        this.nextId = 1;
    }

    public void loadHabits() {
        HabitRepository.LoadResult result = habitRepository.loadHabits();
        habits = result.getHabits();
        loadWarningMessage = result.getWarningMessage();

        nextId = 1;
        for (Habit habit : habits) {
            if (habit.getId() >= nextId) {
                nextId = habit.getId() + 1;
            }
            if (habit.getCompletedDates() == null) {
                habit.setCompletedDates(new ArrayList<>());
            }
        }
    }

    public String getLoadWarningMessage() {
        return loadWarningMessage;
    }

    public List<Habit> getAllHabits() {
        List<Habit> copy = new ArrayList<>(habits);
        copy.sort(Comparator.comparing(Habit::getName, String.CASE_INSENSITIVE_ORDER));
        return copy;
    }

    public Habit findHabitById(int id) {
        for (Habit habit : habits) {
            if (habit.getId() == id) {
                return habit;
            }
        }
        return null;
    }

    public ServiceResult addHabit(String habitName) {
        String cleaned = cleanHabitName(habitName);
        if (cleaned == null) {
            return new ServiceResult(false, "Habit name must be 1 to 50 characters.");
        }
        if (isDuplicateName(cleaned, -1)) {
            return new ServiceResult(false, "Habit name already exists.");
        }

        Habit habit = new Habit(nextId++, cleaned, LocalDate.now(), new ArrayList<>());
        habits.add(habit);

        return saveWithMessage("Habit added.");
    }

    public ServiceResult editHabit(int id, String newName) {
        Habit habit = findHabitById(id);
        if (habit == null) {
            return new ServiceResult(false, "Habit not found.");
        }

        String cleaned = cleanHabitName(newName);
        if (cleaned == null) {
            return new ServiceResult(false, "Habit name must be 1 to 50 characters.");
        }
        if (isDuplicateName(cleaned, id)) {
            return new ServiceResult(false, "Habit name already exists.");
        }

        habit.setName(cleaned);
        return saveWithMessage("Habit updated.");
    }

    public ServiceResult deleteHabit(int id) {
        Habit habit = findHabitById(id);
        if (habit == null) {
            return new ServiceResult(false, "Habit not found.");
        }

        habits.remove(habit);
        return saveWithMessage("Habit deleted.");
    }

    public ServiceResult markHabitCompletedToday(int id) {
        Habit habit = findHabitById(id);
        if (habit == null) {
            return new ServiceResult(false, "Habit not found.");
        }

        boolean marked = habit.markCompleted(LocalDate.now());
        if (!marked) {
            return new ServiceResult(false, "Already marked for today.");
        }

        return saveWithMessage("Marked complete for today.");
    }

    public int getTotalHabits() {
        return habits.size();
    }

    public double getAverageCompletionPercentage() {
        if (habits.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        LocalDate today = LocalDate.now();

        for (Habit habit : habits) {
            total += habit.getCompletionRate(today);
        }

        double average = total / habits.size();
        return Math.round(average * 100.0) / 100.0;
    }

    public int getHabitsCompletedTodayCount() {
        int count = 0;
        LocalDate today = LocalDate.now();

        for (Habit habit : habits) {
            if (habit.getCompletedDates().contains(today)) {
                count++;
            }
        }

        return count;
    }

    private ServiceResult saveWithMessage(String successMessage) {
        try {
            habitRepository.saveHabits(habits);
            return new ServiceResult(true, successMessage);
        } catch (IOException e) {
            return new ServiceResult(false, "Could not save data. Please try again.");
        }
    }

    private String cleanHabitName(String name) {
        if (name == null) {
            return null;
        }

        String cleaned = name.trim();
        if (cleaned.isEmpty() || cleaned.length() > 50) {
            return null;
        }

        return cleaned;
    }

    private boolean isDuplicateName(String name, int currentHabitId) {
        for (Habit habit : habits) {
            if (habit.getId() != currentHabitId && habit.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}

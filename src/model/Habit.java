package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Habit {
    private int id;
    private String name;
    private LocalDate createdDate;
    private List<LocalDate> completedDates;

    public Habit() {
        this.completedDates = new ArrayList<>();
    }

    public Habit(int id, String name, LocalDate createdDate, List<LocalDate> completedDates) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.completedDates = completedDates == null ? new ArrayList<>() : completedDates;
    }

    public boolean markCompleted(LocalDate date) {
        if (date == null) {
            return false;
        }
        if (!completedDates.contains(date)) {
            completedDates.add(date);
            Collections.sort(completedDates);
            return true;
        }
        return false;
    }

    public int calculateCurrentStreak(LocalDate today) {
        if (today == null || completedDates.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate cursor = today;
        while (completedDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    public int calculateLongestStreak() {
        if (completedDates.isEmpty()) {
            return 0;
        }

        List<LocalDate> sorted = new ArrayList<>(completedDates);
        Collections.sort(sorted);

        int longest = 1;
        int current = 1;

        for (int i = 1; i < sorted.size(); i++) {
            LocalDate previous = sorted.get(i - 1);
            LocalDate currentDate = sorted.get(i);

            if (previous.plusDays(1).equals(currentDate)) {
                current++;
            } else {
                current = 1;
            }

            if (current > longest) {
                longest = current;
            }
        }

        return longest;
    }

    public double getCompletionRate(LocalDate today) {
        if (today == null || createdDate == null || today.isBefore(createdDate)) {
            return 0.0;
        }

        long totalDays = ChronoUnit.DAYS.between(createdDate, today) + 1;
        if (totalDays <= 0) {
            return 0.0;
        }

        double rawRate = (completedDates.size() * 100.0) / totalDays;
        return Math.round(rawRate * 100.0) / 100.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<LocalDate> getCompletedDates() {
        if (completedDates == null) {
            completedDates = new ArrayList<>();
        }
        return completedDates;
    }

    public void setCompletedDates(List<LocalDate> completedDates) {
        this.completedDates = completedDates == null ? new ArrayList<>() : completedDates;
    }

    @Override
    public String toString() {
        return name;
    }
}

package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Habit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class HabitRepository {
    private final Path filePath;
    private final ObjectMapper objectMapper;

    public HabitRepository(String filePath) {
        this.filePath = Path.of(filePath);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public LoadResult loadHabits() {
        try {
            ensureFileExists();

            String raw = Files.readString(filePath).trim();
            if (raw.isEmpty()) {
                Files.writeString(filePath, "[]", StandardOpenOption.TRUNCATE_EXISTING);
                return new LoadResult(new ArrayList<>(), null);
            }

            List<Habit> habits = objectMapper.readValue(raw, new TypeReference<List<Habit>>() {
            });
            if (habits == null) {
                habits = new ArrayList<>();
            }

            return new LoadResult(habits, null);
        } catch (Exception e) {
            String message = "Could not read saved data. Starting with empty habits. You can continue and save to replace bad data.";
            return new LoadResult(new ArrayList<>(), message);
        }
    }

    public void saveHabits(List<Habit> habits) throws IOException {
        ensureFileExists();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(habits);
        Files.writeString(filePath, json, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void ensureFileExists() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(filePath)) {
            Files.writeString(filePath, "[]", StandardOpenOption.CREATE);
        }
    }

    public static class LoadResult {
        private final List<Habit> habits;
        private final String warningMessage;

        public LoadResult(List<Habit> habits, String warningMessage) {
            this.habits = habits;
            this.warningMessage = warningMessage;
        }

        public List<Habit> getHabits() {
            return habits;
        }

        public String getWarningMessage() {
            return warningMessage;
        }
    }
}

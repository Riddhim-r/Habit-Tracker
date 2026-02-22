import repository.HabitRepository;
import service.HabitService;
import ui.HabitTrackerFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HabitRepository repository = new HabitRepository("data/habits.json");
            HabitService service = new HabitService(repository);
            service.loadHabits();

            HabitTrackerFrame frame = new HabitTrackerFrame(service);
            frame.setVisible(true);
            frame.showLoadWarningIfNeeded();
        });
    }
}

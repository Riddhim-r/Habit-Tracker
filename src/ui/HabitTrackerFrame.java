package ui;

import service.HabitService;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

public class HabitTrackerFrame extends JFrame {
    public static final String PAGE_DASHBOARD = "dashboard";
    public static final String PAGE_ADD_EDIT = "addEdit";
    public static final String PAGE_DETAILS = "details";

    private final HabitService habitService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final DashboardPage dashboardPage;
    private final AddEditHabitPage addEditHabitPage;
    private final HabitDetailsPage habitDetailsPage;

    public HabitTrackerFrame(HabitService habitService) {
        this.habitService = habitService;

        setTitle("Habit Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        dashboardPage = new DashboardPage(this, habitService);
        addEditHabitPage = new AddEditHabitPage(this, habitService);
        habitDetailsPage = new HabitDetailsPage(this, habitService);

        cardPanel.add(dashboardPage, PAGE_DASHBOARD);
        cardPanel.add(addEditHabitPage, PAGE_ADD_EDIT);
        cardPanel.add(habitDetailsPage, PAGE_DETAILS);

        add(cardPanel);

        refreshAllPages();
        showDashboard();
    }

    public void showDashboard() {
        refreshAllPages();
        cardLayout.show(cardPanel, PAGE_DASHBOARD);
    }

    public void showAddEdit() {
        addEditHabitPage.refreshData();
        cardLayout.show(cardPanel, PAGE_ADD_EDIT);
    }

    public void showDetails(Integer habitId) {
        habitDetailsPage.refreshData();
        if (habitId != null) {
            habitDetailsPage.setSelectedHabit(habitId);
        }
        cardLayout.show(cardPanel, PAGE_DETAILS);
    }

    public void refreshAllPages() {
        dashboardPage.refreshData();
        addEditHabitPage.refreshData();
        habitDetailsPage.refreshData();
    }

    public void showLoadWarningIfNeeded() {
        String warning = habitService.getLoadWarningMessage();
        if (warning != null && !warning.isBlank()) {
            JOptionPane.showMessageDialog(this, warning, "Data Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}

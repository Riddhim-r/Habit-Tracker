package ui;

import model.Habit;
import service.HabitService;
import service.ServiceResult;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

public class HabitDetailsPage extends JPanel {
    private static final Color LAVENDER = new Color(230, 230, 250);
    private static final Color LIGHT_BLUE = new Color(221, 235, 255);
    private static final Color BUTTON_BLUE = new Color(91, 134, 229);

    private final HabitTrackerFrame parentFrame;
    private final HabitService habitService;

    private final DefaultComboBoxModel<Habit> habitComboModel;
    private final JComboBox<Habit> habitComboBox;

    private final JLabel createdDateValue;
    private final JLabel currentStreakValue;
    private final JLabel longestStreakValue;
    private final JLabel completionValue;

    public HabitDetailsPage(HabitTrackerFrame parentFrame, HabitService habitService) {
        this.parentFrame = parentFrame;
        this.habitService = habitService;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(LAVENDER);

        JLabel title = new JLabel("Habit Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        habitComboModel = new DefaultComboBoxModel<>();
        habitComboBox = new JComboBox<>(habitComboModel);
        habitComboBox.addActionListener(e -> updateDetailsPanel());

        JPanel selectorPanel = new JPanel(new BorderLayout(8, 8));
        selectorPanel.setBackground(LIGHT_BLUE);
        selectorPanel.setBorder(BorderFactory.createTitledBorder("Select Habit"));
        selectorPanel.add(habitComboBox, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        detailsPanel.setBackground(LIGHT_BLUE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        detailsPanel.add(new JLabel("Created Date:"));
        createdDateValue = new JLabel("-");
        detailsPanel.add(createdDateValue);

        detailsPanel.add(new JLabel("Current Streak:"));
        currentStreakValue = new JLabel("-");
        detailsPanel.add(currentStreakValue);

        detailsPanel.add(new JLabel("Longest Streak:"));
        longestStreakValue = new JLabel("-");
        detailsPanel.add(longestStreakValue);

        detailsPanel.add(new JLabel("Completion %:"));
        completionValue = new JLabel("-");
        detailsPanel.add(completionValue);

        JPanel center = new JPanel(new GridLayout(2, 1, 12, 12));
        center.setOpaque(false);
        center.add(selectorPanel);
        center.add(detailsPanel);

        add(center, BorderLayout.CENTER);

        JButton markButton = createButton("Mark Today Complete");
        markButton.addActionListener(e -> markTodayComplete());

        JButton backButton = createButton("Back to Dashboard");
        backButton.addActionListener(e -> parentFrame.showDashboard());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.setOpaque(false);
        buttonBar.add(markButton);
        buttonBar.add(backButton);

        add(buttonBar, BorderLayout.SOUTH);
    }

    public void refreshData() {
        Habit selected = (Habit) habitComboBox.getSelectedItem();
        Integer selectedId = selected == null ? null : selected.getId();

        habitComboModel.removeAllElements();
        List<Habit> habits = habitService.getAllHabits();
        for (Habit habit : habits) {
            habitComboModel.addElement(habit);
        }

        if (selectedId != null) {
            setSelectedHabit(selectedId);
        }

        updateDetailsPanel();
    }

    public void setSelectedHabit(int id) {
        for (int i = 0; i < habitComboModel.getSize(); i++) {
            Habit habit = habitComboModel.getElementAt(i);
            if (habit.getId() == id) {
                habitComboBox.setSelectedIndex(i);
                break;
            }
        }
        updateDetailsPanel();
    }

    private void updateDetailsPanel() {
        Habit selected = (Habit) habitComboBox.getSelectedItem();
        if (selected == null) {
            createdDateValue.setText("-");
            currentStreakValue.setText("0");
            longestStreakValue.setText("0");
            completionValue.setText("0.0%");
            return;
        }

        LocalDate today = LocalDate.now();
        createdDateValue.setText(String.valueOf(selected.getCreatedDate()));
        currentStreakValue.setText(String.valueOf(selected.calculateCurrentStreak(today)));
        longestStreakValue.setText(String.valueOf(selected.calculateLongestStreak()));
        completionValue.setText(selected.getCompletionRate(today) + "%");
    }

    private void markTodayComplete() {
        Habit selected = (Habit) habitComboBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No habit selected.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ServiceResult result = habitService.markHabitCompletedToday(selected.getId());
        int messageType = result.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(this, result.getMessage(), "Habit Tracker", messageType);

        parentFrame.refreshAllPages();
        setSelectedHabit(selected.getId());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_BLUE);
        button.setForeground(Color.WHITE);
        return button;
    }
}

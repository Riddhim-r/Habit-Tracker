package ui;

import model.Habit;
import service.HabitService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class DashboardPage extends JPanel {
    private static final Color LAVENDER = new Color(230, 230, 250);
    private static final Color LIGHT_BLUE = new Color(221, 235, 255);
    private static final Color BUTTON_BLUE = new Color(91, 134, 229);

    private final HabitTrackerFrame parentFrame;
    private final HabitService habitService;

    private final DefaultListModel<Habit> habitListModel;
    private final JList<Habit> habitList;

    private final JLabel totalHabitsLabel;
    private final JLabel completedTodayLabel;
    private final JLabel averageCompletionLabel;

    public DashboardPage(HabitTrackerFrame parentFrame, HabitService habitService) {
        this.parentFrame = parentFrame;
        this.habitService = habitService;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(LAVENDER);

        JLabel title = new JLabel("Habit Tracker Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        habitListModel = new DefaultListModel<>();
        habitList = new JList<>(habitListModel);
        habitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        habitList.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(habitList);
        scrollPane.setPreferredSize(new Dimension(380, 300));

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane);

        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        statsPanel.setBackground(LIGHT_BLUE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Quick Stats"));

        totalHabitsLabel = new JLabel();
        completedTodayLabel = new JLabel();
        averageCompletionLabel = new JLabel();

        statsPanel.add(totalHabitsLabel);
        statsPanel.add(completedTodayLabel);
        statsPanel.add(averageCompletionLabel);

        centerPanel.add(statsPanel);
        add(centerPanel, BorderLayout.CENTER);

        JButton addEditButton = createButton("Go to Add/Edit");
        addEditButton.addActionListener(e -> parentFrame.showAddEdit());

        JButton detailsButton = createButton("Open Habit Details");
        detailsButton.addActionListener(e -> openSelectedHabitDetails());

        JButton refreshButton = createButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.setOpaque(false);
        buttonBar.add(addEditButton);
        buttonBar.add(detailsButton);
        buttonBar.add(refreshButton);

        add(buttonBar, BorderLayout.SOUTH);
    }

    public void refreshData() {
        habitListModel.clear();

        List<Habit> habits = habitService.getAllHabits();
        for (Habit habit : habits) {
            habitListModel.addElement(habit);
        }

        totalHabitsLabel.setText("Total habits: " + habitService.getTotalHabits());
        completedTodayLabel.setText("Completed today: " + habitService.getHabitsCompletedTodayCount());
        averageCompletionLabel.setText("Average completion: " + habitService.getAverageCompletionPercentage() + "%");
    }

    private void openSelectedHabitDetails() {
        Habit selectedHabit = habitList.getSelectedValue();
        if (selectedHabit == null) {
            JOptionPane.showMessageDialog(this, "Select a habit first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        parentFrame.showDetails(selectedHabit.getId());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_BLUE);
        button.setForeground(Color.WHITE);
        return button;
    }
}

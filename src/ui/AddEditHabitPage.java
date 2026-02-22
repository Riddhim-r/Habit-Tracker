package ui;

import model.Habit;
import service.HabitService;
import service.ServiceResult;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class AddEditHabitPage extends JPanel {
    private static final Color LAVENDER = new Color(230, 230, 250);
    private static final Color LIGHT_BLUE = new Color(221, 235, 255);
    private static final Color BUTTON_BLUE = new Color(91, 134, 229);

    private final HabitTrackerFrame parentFrame;
    private final HabitService habitService;

    private final DefaultListModel<Habit> habitListModel;
    private final JList<Habit> habitList;
    private final JTextField nameField;

    public AddEditHabitPage(HabitTrackerFrame parentFrame, HabitService habitService) {
        this.parentFrame = parentFrame;
        this.habitService = habitService;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(LAVENDER);

        JLabel title = new JLabel("Add / Edit Habits");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        habitListModel = new DefaultListModel<>();
        habitList = new JList<>(habitListModel);
        habitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        habitList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Habit selected = habitList.getSelectedValue();
                if (selected != null) {
                    nameField.setText(selected.getName());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(habitList);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        formPanel.setBackground(LIGHT_BLUE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Habit Input"));

        JLabel nameLabel = new JLabel("Habit Name (1-50 chars):");
        nameField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setOpaque(false);
        center.add(scrollPane);
        center.add(formPanel);

        add(center, BorderLayout.CENTER);

        JButton addButton = createButton("Add Habit");
        addButton.addActionListener(e -> addHabit());

        JButton updateButton = createButton("Update Selected");
        updateButton.addActionListener(e -> updateHabit());

        JButton deleteButton = createButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteHabit());

        JButton backButton = createButton("Back to Dashboard");
        backButton.addActionListener(e -> parentFrame.showDashboard());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.setOpaque(false);
        buttonBar.add(addButton);
        buttonBar.add(updateButton);
        buttonBar.add(deleteButton);
        buttonBar.add(backButton);

        add(buttonBar, BorderLayout.SOUTH);
    }

    public void refreshData() {
        Habit currentSelection = habitList.getSelectedValue();
        Integer selectedId = currentSelection == null ? null : currentSelection.getId();

        habitListModel.clear();
        List<Habit> habits = habitService.getAllHabits();
        for (Habit habit : habits) {
            habitListModel.addElement(habit);
        }

        if (selectedId != null) {
            for (int i = 0; i < habitListModel.size(); i++) {
                if (habitListModel.get(i).getId() == selectedId) {
                    habitList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void addHabit() {
        ServiceResult result = habitService.addHabit(nameField.getText());
        showResult(result);
        if (result.isSuccess()) {
            nameField.setText("");
            parentFrame.refreshAllPages();
        }
    }

    private void updateHabit() {
        Habit selected = habitList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a habit to update.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ServiceResult result = habitService.editHabit(selected.getId(), nameField.getText());
        showResult(result);
        if (result.isSuccess()) {
            parentFrame.refreshAllPages();
        }
    }

    private void deleteHabit() {
        Habit selected = habitList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a habit to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete habit: " + selected.getName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        ServiceResult result = habitService.deleteHabit(selected.getId());
        showResult(result);
        if (result.isSuccess()) {
            nameField.setText("");
            parentFrame.refreshAllPages();
        }
    }

    private void showResult(ServiceResult result) {
        int messageType = result.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(this, result.getMessage(), "Habit Tracker", messageType);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_BLUE);
        button.setForeground(Color.WHITE);
        return button;
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpenseTrackerGUI extends JFrame {
    private final ExpenseTracker expenseTracker;
    private JComboBox<Category> categoryComboBox; // Maintain reference to the JComboBox
    private static final String DATA_FILE = "Data_File.txt";

    public ExpenseTrackerGUI() {
        expenseTracker = ExpenseTracker.loadExpensesFromFile(DATA_FILE);
        setTitle("Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Expense", createAddExpensePanel());
        tabbedPane.addTab("Add Category", createAddCategoryPanel());
        tabbedPane.addTab("View Reports", createViewReportsPanel());

        add(tabbedPane);

        // Save data when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                expenseTracker.saveExpensesToFile(DATA_FILE);
            }
        });
    }

    private JPanel createAddExpensePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel dateLabel = new JLabel("Date (yyyy-mm-dd):");
        JTextField dateField = new JTextField();

        JLabel amountLabel = new JLabel("Amount (₦):");
        JTextField amountField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>(); // Initialize the JComboBox here

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Date date = parseDate(dateField.getText());
                    double amount = parseAmount(amountField.getText());
                    Category category = (Category) categoryComboBox.getSelectedItem();
                    String description = descriptionField.getText();

                    if (date == null || category == null || description.isEmpty()) {
                        throw new IllegalArgumentException("All fields must be filled correctly.");
                    }

                    expenseTracker.addExpense(date, amount, category, description);
                    JOptionPane.showMessageDialog(null, "Expense added successfully.");
                } catch (ParseException pe) {
                    JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-mm-dd.");
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a numeric value.");
                } catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(null, iae.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage());
                }
            }
        });

        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(categoryLabel);
        panel.add(categoryComboBox); // Add the JComboBox to the panel
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(new JLabel());
        panel.add(addButton);

        // Populate categoryComboBox with categories
        updateCategoryComboBox();

        return panel;
    }

    private JPanel createAddCategoryPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Category Name:");
        JTextField nameField = new JTextField();

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        JButton addButton = new JButton("Add Category");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String description = descriptionField.getText();

                    if (name.isEmpty() || description.isEmpty()) {
                        throw new IllegalArgumentException("All fields must be filled.");
                    }

                    expenseTracker.addCategory(name, description);
                    JOptionPane.showMessageDialog(null, "Category added successfully.");
                    updateCategoryComboBox(); // Update the category combo box in the Add Expense panel
                } catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(null, iae.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage());
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(new JLabel());
        panel.add(addButton);

        return panel;
    }

    private JPanel createViewReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel monthLabel = new JLabel("Month (e.g., 1-12):");
        JTextField monthField = new JTextField();

        JLabel yearLabel = new JLabel("Year (e.g., 2023):");
        JTextField yearField = new JTextField();

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int month = parseMonth(monthField.getText());
                    int year = parseYear(yearField.getText());

                    List<Expense> monthlyExpenses = expenseTracker.getMonthlyReport(month, year);
                    StringBuilder report = new StringBuilder();
                    for (Expense expense : monthlyExpenses) {
                        report.append(expense).append("\n");
                    }
                    reportArea.setText(report.toString());
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Invalid month or year. Please enter numeric values.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage());
                }
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(monthLabel);
        inputPanel.add(monthField);
        inputPanel.add(yearLabel);
        inputPanel.add(yearField);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(generateButton, BorderLayout.CENTER);
        panel.add(new JScrollPane(reportArea), BorderLayout.SOUTH);

        return panel;
    }



    private void updateCategoryComboBox() {
        categoryComboBox.removeAllItems();
        for (Category category : expenseTracker.getCategories()) {
            categoryComboBox.addItem(category);
        }
    }

    private Date parseDate(String dateText) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // Strict date parsing
        return sdf.parse(dateText);
    }

    private double parseAmount(String amountText) throws NumberFormatException {
        return Double.parseDouble(amountText);
    }

    private int parseMonth(String monthText) throws NumberFormatException {
        int month = Integer.parseInt(monthText);
        if (month < 1 || month > 12) {
            throw new NumberFormatException("Month must be between 1 and 12.");
        }
        return month - 1; // Adjust for zero-based months
    }

    private int parseYear(String yearText) throws NumberFormatException {
        return Integer.parseInt(yearText);
    }

}
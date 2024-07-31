import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ExpenseTracker {
    private final List<Expense> expenses;
    private final List<Category> categories;

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public void addExpense(Date date, double amount, Category category, String description) {
        expenses.add(new Expense(date, amount, category, description));
    }

    public void addCategory(String name, String description) {
        categories.add(new Category(name, description));
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Expense> getMonthlyReport(int month, int year) {
        List<Expense> monthlyExpenses = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getDate().getMonth() == month && expense.getDate().getYear() == year - 1900) {
                monthlyExpenses.add(expense);
            }
        }
        return monthlyExpenses;
    }

    public void saveExpensesToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Category category : categories) {
                writer.write("CATEGORY," + category.getName() + "," + category.getDescription());
                writer.newLine();
            }
            for (Expense expense : expenses) {
                writer.write("EXPENSE," + expense.getDate().getTime() + "," + expense.getAmount() + "," +
                        expense.getCategory().getName() + "," + expense.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExpenseTracker loadExpensesFromFile(String filename) {
        ExpenseTracker expenseTracker = new ExpenseTracker();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("CATEGORY")) {
                    expenseTracker.addCategory(parts[1], parts[2]);
                } else if (parts[0].equals("EXPENSE")) {
                    long dateMillis = Long.parseLong(parts[1]);
                    double amount = Double.parseDouble(parts[2]);
                    Category category = expenseTracker.getCategoryByName(parts[3]);
                    String description = parts[4];
                    expenseTracker.addExpense(new Date(dateMillis), amount, category, description);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expenseTracker;
    }

    private Category getCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }
}
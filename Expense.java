import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Date date;
    private final double amount;
    private final Category category;
    private final String description;

    public Expense(Date date, double amount, Category category, String description) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("Date: %s, Amount: %.2f, Category: %s, Description: %s",
                date.toString(), amount, category.getName(), description);
    }
}

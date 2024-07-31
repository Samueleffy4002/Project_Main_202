import java.io.Serial;
import java.io.Serializable;

public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}

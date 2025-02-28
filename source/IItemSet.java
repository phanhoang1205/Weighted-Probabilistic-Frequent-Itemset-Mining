
import java.util.Set;

public interface IItemSet{
    void addItem(Item value);
    Set<Item> getItems();
    boolean contains(Item item);
    int size();
    void setItems(Set<Item> items);
    boolean isEmpty();
    void print();
    void setMuy(double muy);
    double getMuy();
    boolean removeAll(IItemSet x);
}

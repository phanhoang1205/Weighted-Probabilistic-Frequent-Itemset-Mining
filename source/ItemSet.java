import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ItemSet implements IItemSet{
    public Set<Item> items;
    public double muy;

    /**
	 * Default constructor
	 */
	public ItemSet(){
		items = new HashSet<Item>();
	}

    public ItemSet(IItemSet x) {
        this.items = new HashSet<>(x.getItems());
    }

    /**
	 * Add an item to that itemset
	 * @param value the item to be added
	 */
	public void addItem(Item value){
        items.add(value);
    }

    /**
	 * Get items from that itemset.
	 * @return a set of item.
	 */
	public Set<Item> getItems(){
		return items;
	}

    /**
	 * Check if this itemset contains a given item.
	 * @param item the item
	 * @return true, if yes, otherwise false.
	 */
    public boolean contains(Item item) {
		return items.contains(item);
	}

    /**
	 * Get the number of items in this itemset
	 * @return the item count (int)
	 */
	public int size(){
		return items.size();
	}


    /**
	 * Set the items in this itemsets.
	 * @param items a set of items.
	 */
	public void setItems(Set<Item> items) {
		this.items = items;
	}

    @Override
    public void setMuy(double muy) {
        this.muy = muy;
    }

    /**
	 * check the set is empty or not
	 * @return true, if yes, otherwise false.
	 */
    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public void print() {
        System.out.println(toString()); 
    }
    
    /**
	 * Get a string representation of the items in this itemset.
	 */
	public String toString(){
		StringBuilder r = new StringBuilder ();
        r.append('(');
		for(Item attribute : items){
			r.append(attribute.toString());
			r.append(' ');
		}
        r.append(')');
		return r.toString();
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ItemSet itemSet = (ItemSet) obj;
        return items.equals(itemSet.items);
    }

    /**
	 * Generate an hash code for that item.
	 * @return an hash code as a int.
	 */
    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public boolean removeAll(IItemSet x) {
        return items.removeAll(x.getItems());
    }

    @Override
    public double getMuy() {
        return this.muy;
    }
}

import java.util.Objects;

public class Item {
    public int item;
    public double probability;
    /**
	 * Default constructor
	 */
    public Item() {}
    public Item(int item, double probability) {
        this.item = item;
        this.probability = probability;
    }

    /**
     * Get the item.
     * @return an integer value of the item.
     */
    public int getItem() {
        return item;
    }

    /**
     * Set the probability.
     * @param probability the probability to item.
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * get the probability.
     * @return probability of item.
     */
    public double getProbability() {
        return probability;
    }

    /**
     * equals: Checks if this object is equal to another object.
     * @param o the object to compare with.
     * @return boolean - True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item1 = (Item) o;
        return Objects.equals(this.item, item1.item);
    }

    /**
     * Compute the hash code of this object.
     * @return the hash code of the item.
     */
    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "" + item + " (" + probability + ")";
    }
}

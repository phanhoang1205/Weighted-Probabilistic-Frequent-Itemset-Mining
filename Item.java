import java.util.Objects;
import java.util.Random;

public class Item<E extends Comparable<E>> implements Comparable<Item<E>>{
    private E itemName;
    private double prob;

    
    public Item(E name) {
        this.itemName = name;
        this.prob = generateProb();
    }

    public void setItemName(E itemName) {
        this.itemName = itemName;
       
    }

    public E getItemName() {
        return itemName;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public double getProb() {
        return prob;
    }

    private static double generateProb() {
        Random random = new Random();
        double prob = random.nextGaussian() * Math.sqrt(0.125) + 0.5;
        if (prob > 1) {
            return 1.0;
        }
        
        if (prob < 0) {
            return 0.001;
        }
        return Math.round(prob*1000)/(Double)1000.0;   
    } 

    @Override
    public String toString() {
        return "Item(" + this.itemName + ", " + this.prob + ")";
    }

    @Override
    public int compareTo(Item<E> other) {
        return this.itemName.compareTo(other.itemName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Item<?> other = (Item<?>) obj;
        return Objects.equals(itemName, other.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName);
    }
}

    
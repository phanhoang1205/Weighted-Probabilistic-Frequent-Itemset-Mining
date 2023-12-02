import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        FrequentItemsetProblem<String> problem = new FrequentItemsetProblem<>("test.txt");
        problem.printData();

        Map<String, Double> w = problem.getW();
        System.out.println(w.get("1"));
        // Item<Integer> i1 = new Item<>(1);
        // Item<Integer> i2 = new Item<>(2);
        // Item<Integer> i3 = new Item<>(3);
        // Item<Integer> i4 = new Item<>(4);
        // Item<Integer> i5 = new Item<>(5);
        // Item<Integer> i6 = new Item<>(6);

        // Set<Item<Integer>> s = new HashSet<Item<Integer>>();
        // s.add(i2);
        // s.add(i1);
        // s.add(i3);
        // s.add(i4);
        // s.add(i5);
        // s.add(i6);
        // Set<Item<Integer>> t = new HashSet<Item<Integer>>();
        // Item<Integer> t1 = new Item<>(1);
        // Item<Integer> t2 = new Item<>(2);
        // Item<Integer> t3 = new Item<>(3);
        // t.add(t2);
        // t.add(t1);
        // t.add(t3);
        // // System.out.println(s);
        // System.out.println(t.containsAll(s));

        // Random random = new Random();
        // double weight = random.nextDouble() + 0.000001;
        // System.out.println(weight);
    }
}

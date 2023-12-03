import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        FrequentItemsetProblem<String> problem = new FrequentItemsetProblem<>("mushrooms.txt", 0.02, 0.6, 0.6);
        Set<Set<String>> wPFI = problem.solve("Algorithm2");
        System.out.println(wPFI);
        // Map<String, Double> w = problem.getW();
        // System.out.println(w.get("1"));


        /*
         * code test uniform distribution random for weight table
         */

        // Random random = new Random();
        // double weight = random.nextDouble() + 0.000001;
        // System.out.println(weight);

        /*
         * code test Pr
         */

        // ArrayList<Map<String, Double>> DB = new ArrayList<>();
        // Map<String, Double> t = new HashMap<>();
        // t.put("Milk", 0.4);
        // t.put("Fruit", 1.0);
        // t.put("Video", 0.3);
        // DB.add(t);
        // Map<String, Double> t1 = new HashMap<>();
        // t1.put("Milk", 1.0);
        // t1.put("Fruit", 0.8);
        // DB.add(t1);
        // Find_wPFI<String> f = new Find_wPFI<>(null, null, null, 0, 0);
        // Set<String> X = new HashSet<>();
        // X.add("Milk");
        // X.add("Fruit");
        // System.out.println(f.Pr(DB, X, 1));


    }
}

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        FrequentItemsetProblem<String> problem = new FrequentItemsetProblem<>("mushrooms.txt", 0.1, 0.6, 0.6);

        Map<Double, Long> time_algorithm2 = new HashMap<>();
        Map<Double, Long> time_algorithm3 = new HashMap<>();
        

        double ratio = 0.1;

        while (ratio <= 0.3) {
            problem.setMinSup(ratio);
            long startTime = System.nanoTime();
            Set<Set<String>> wPFI = problem.solve("Algorithm_2");
            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1000000000;
            System.out.println("took: "
                                + executionTime + "s");
            time_algorithm2.put(ratio, executionTime);
            ratio += 0.05;
        }


        ratio = 0.1;
        while (ratio <= 0.3) {
            problem.setMinSup(ratio);
            long startTime = System.nanoTime();
            Set<Set<String>> wPFI = problem.solve("Algorithm_3");
            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1000000000;
            System.out.println("took: "
                            + executionTime + "s");
            time_algorithm3.put(ratio, executionTime);
            ratio += 0.05;
        }
        
        System.out.println(time_algorithm2);
        System.out.println(time_algorithm3);
    }
}

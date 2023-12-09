import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        FrequentItemsetProblem<String> problem = new FrequentItemsetProblem<>("mushrooms.txt", 0.2, 0.6, 0.5);
        // problem.readDataTest("test.txt");
        // System.out.println(problem.data);
        // System.out.println(problem.minSup);
        // Find_wPFI<String> f = new Find_wPFI<>(problem.I, problem.data, problem.w, problem.minSup, problem.t);
        // Set<String> set = new HashSet<>();
        // set.add("1");
        // BigDecimal prob = BigDecimal.ONE;
        // for (Map<String, Double> transaction : problem.data) {
        //     prob = prob.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(transaction.get("88"))));
        //     // System.out.println(prob);
        //     // try {
        //     //     Thread.sleep(5);
        //     // } catch (InterruptedException e) {
        //     //     // TODO Auto-generated catch block
        //     //     e.printStackTrace();
        //     // }
        // }
        // System.out.println(BigDecimal.ONE.subtract(prob));
        // System.out.println("cal Pr 1");
        // System.out.println(f.Pr(problem.data, set, 1));
        // System.out.println(f.Pr1(problem.data, set, 1));
        // System.out.println(f.Probability(3, problem.data.size(), set));

        Set<Set<String>> wPFI = problem.solve("Algorithm_2");
        System.out.println(wPFI);
    }
}

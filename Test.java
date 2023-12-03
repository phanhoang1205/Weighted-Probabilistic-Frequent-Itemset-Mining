import java.util.Set;

public class Test {
    public static void main(String[] args) {
        FrequentItemsetProblem<String> problem = new FrequentItemsetProblem<>("mushrooms.txt", 0.2, 0.6, 0.6);
        Set<Set<String>> wPFI = problem.solve("Algorithm_3");
        System.out.println(wPFI);
    }
}

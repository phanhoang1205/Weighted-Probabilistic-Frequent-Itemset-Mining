import java.io.*;
import java.util.*;

public class FrequentItemsetProblem <E> {
    private int minSup;
    private double t;
    private double alpha;
    private Set<E> I;
    ArrayList<Map<E, Double>> data;
    private Map<E, Double> w;

    public FrequentItemsetProblem(String filePath, Double minSupRatio, Double t, Double alpha) {
        readData(filePath);
        createWeight();
        this.t = t;
        this.alpha = alpha;
        this.minSup = (int) (minSupRatio * data.size());
    };

    private void readData(String filePath) {
        FileReader fr =null;
        BufferedReader br = null;
        this.data = new ArrayList<>();
        this.I = new HashSet<>();
  
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line; 
        
            while((line = br.readLine()) != null) {
                Map<E, Double> transaction = new HashMap<>();
                String[] items = line.split(" ");
                for (String e : items) {
                    try {
                        E itemName = (E)e;
                        transaction.put(itemName, generateProb());
                        this.I.add(itemName);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
                this.data.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void createWeight() {
        this.w = new HashMap<E, Double>();
        Random random = new Random();
        for (E item : I) {
            double weight = random.nextDouble() + 0.000001;
            w.put(item, weight);
        }
    }
    
    public void printData() {
        for (Map<E, Double> transaction : this.data) {
            System.out.print("[");
            System.out.print(transaction);
            System.out.println("]");
        }
    }

    public Map<E, Double> getW() {
        return w;
    }

    public Set<Set<E>> solve(String Algorithm) {
        Map<Integer, Set<Set<E>>> WPFI = new HashMap<>();

        Find_wPFI<E> wPFI = new Find_wPFI<>(this.I, this.data, this.w, this.minSup, this.t);
        WPFI.put(1, wPFI.Scan_Find_Size_1_wPFI());
        int k = 2;

        if (WPFI.get(k-1) == null) {
            return null;
        }

        System.out.printf("Running 1-th iteration with %d WPFI %d-itemset \n", WPFI.get(k-1).size(), k-1);
        while (!WPFI.get(k-1).isEmpty()) {
            wPFIAprioriGen<E> generator = new wPFIAprioriGen<>(WPFI.get(k-1), this.alpha, wPFI);
            Set<Set<E>> Ck = generator.algorithm2();
            WPFI.put(k, wPFI.Scan_Find_Size_k_wPFI(Ck));
            k += 1;
            System.out.printf("Running %d-th iteration with %d WPFI %d-itemset \n", k-1, WPFI.get(k-1).size(), k-1);
        }
        
        return WPFI.get(k-2);
    }
}

import java.io.*;
import java.util.*;

    /*
        Class: FrequentItemsetProblem<E>
        This class represents a problem of finding frequent itemsets in uncertain data using weighted Apriori algorithm

        Fields:
        minSup: int - Minimum support.
        t: double - Threshold.
        alpha: double - scale factor.
        I: Set<E> - Set of items in the dataset.
        data: ArrayList<Map<E, Double>> - List of transactions, each represented as a map of items to their probabilities.
        w: Map<E, Double> - Weight for each item.
        PFI: Set<Set<E>> - Frequent itemsets. 
    */

public class FrequentItemsetProblem <E> {
    public int minSup;
    public double t;
    public double alpha;
    public Set<E> I;
    public ArrayList<Map<E, Double>> data;
    public Map<E, Double> w;
    private Set<Set<E>> PFI;
    private int n;

    // Constructors:
    public FrequentItemsetProblem(String filePath, Double minSupRatio, Double t, Double alpha) {
        File f = new File("fullyuncertaindb.txt");
        if(!f.exists() || f.isDirectory()) { 
            this.n = getNumOfLines(filePath);
            CreateDatabase<E> c = new CreateDatabase<>(n, this.I);
            c.createFullyDB();
            readData();
            createWeight();
            this.t = t;
            this.alpha = alpha;
            this.minSup = (int) (minSupRatio * n);
        } else {
            readData();
            createWeight();
            this.t = t;
            this.alpha = alpha;
            this.minSup = (int) (minSupRatio * data.size());
        }
    };

    /*
        Methods:
        getNumOfLines(String filePath): Reads the dataset file, counts the number of lines, and extracts unique items.

        Input:
        filePath: String - Path to the dataset file.

        Output:
        int - Number of lines in the file.
        I: Set<E> - Set of items in the dataset.

        Time Complexity: O(N * M), where N is the number of lines in the file, and M is the average number of items in a transaction.
        Space Complexity: O(M), where M is the number of unique items in the dataset.
    */
    private int getNumOfLines(String filePath) {
        FileReader fr =null;
        BufferedReader br = null;
        this.I = new HashSet<>();
        int count = 0;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line; 
            
            while((line = br.readLine()) != null) {
                count += 1;
                String[] items = line.split(" ");
                for (String e : items) {
                    try {
                        E itemName = (E) e;
                        this.I.add(itemName);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    /*
        Method to read dataset from file 
    */
    private void readData() {
        FileReader fr =null;
        BufferedReader br = null;
        this.data = new ArrayList<>();
        this.I = new HashSet<>();
  
        try {
            fr = new FileReader("fullyuncertaindb.txt");
            br = new BufferedReader(fr);
            String line; 
        
            while((line = br.readLine()) != null) {
                Map<E, Double> transaction = new HashMap<>();
                String[] items = line.split(" ");
                for (String e : items) {
                    try {
                        String[] item = e.split(":");
                        E itemName = (E) item[0];
                        transaction.put(itemName, Double.parseDouble(item[1]));
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


    /*
        CreateWeight(): Creates random weights for each item in the dataset
        Output:
        •	w: Map of items of type E and their associated weights (doubles).

        Time Complexity: O(|I|), where |I| is the number of unique items in the dataset.
        Space Complexity: O(M), where M is the number of unique items in the dataset.
    */

    private void createWeight() {
        this.w = new HashMap<E, Double>();
        Random random = new Random();
        for (E item : I) {
            double weight = random.nextDouble() + 0.000001;
            w.put(item, weight);
        }
    }
    

    /*
        Method to print dataset from file 
    */
    public void printData() {
        for (Map<E, Double> transaction : this.data) {
            System.out.print("[");
            System.out.print(transaction);
            System.out.println("]");
        }
    }

    // GetW(): Gets the map of items to their weights.
    public Map<E, Double> getW() {
        return w;
    }

    
    /*
        Algorithm 1
    */
    public Set<Set<E>> solve(String Algorithm) {
        int algorithm = 0;
        if (Algorithm.equals("Algorithm_2")) {
            algorithm = 2;
        } else if (Algorithm.equals("Algorithm_3")) {
            algorithm = 3;
        } else {
            System.out.println("solve method only support two algorithm: 'Algorithm_2', 'Algorithm_3'");
            System.exit(0);
        }

        Map<Integer, Set<Set<E>>> WPFI = new HashMap<>();

        Find_wPFI<E> wPFI = new Find_wPFI<>(this.I, this.data, this.w, this.minSup, this.t);
        WPFI.put(1, wPFI.Scan_Find_Size_1_wPFI());
        int k = 2;

        if (WPFI.get(k-1) == null) {
            return null;
        }

        System.out.printf("Running 1-th iteration with %d WPFI %d-itemset \n", WPFI.get(k-1).size(), k-1);
        while (!WPFI.get(k-1).isEmpty()) {

            if (algorithm == 2) {
                Set<Set<E>> Ck = new wPFIAprioriGen<>(WPFI.get(k-1), this.alpha, wPFI).algorithm2();
                System.out.println("Size Ck: " + Ck.size());
                WPFI.put(k, wPFI.Scan_Find_Size_k_wPFI(Ck));
            } else {
                Set<Set<E>> Ck = new wPFIAprioriGen<>(WPFI.get(k-1), this.alpha, wPFI).algorithm3();
                System.out.println("Size Ck: " + Ck.size());        
                WPFI.put(k, wPFI.Scan_Find_Size_k_wPFI(Ck));
            }
               
            k += 1;
            System.out.printf("Running %d-th iteration with %d WPFI %d-itemset \n", k-1, WPFI.get(k-1).size(), k-1);
        }

        return WPFI.get(k-2);
    }

    public void setMinSup(double ratio) {
        this.minSup = (int) (this.minSup * this.n);
    }
}

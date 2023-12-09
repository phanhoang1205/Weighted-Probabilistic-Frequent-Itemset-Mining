import java.io.*;
import java.util.*;

public class FrequentItemsetProblem <E> {
    public int minSup;
    public double t;
    public double alpha;
    public Set<E> I;
    public ArrayList<Map<E, Double>> data;
    public Map<E, Double> w;
    private Set<Set<E>> PFI;

    public FrequentItemsetProblem(String filePath, Double minSupRatio, Double t, Double alpha) {
        File f = new File("fullyuncertaindb.txt");
        if(!f.exists() || f.isDirectory()) { 
            int n = getNumOfLines(filePath);
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
                System.out.println(wPFI.mu_k);
                WPFI.put(k, wPFI.Scan_Find_Size_k_wPFI(Ck));
            } else {
                Set<Set<E>> Ck = new wPFIAprioriGen<>(WPFI.get(k-1), this.alpha, wPFI).algorithm3();
                System.out.println("Size Ck: " + Ck.size());
                System.out.println(wPFI.mu_k);
                WPFI.put(k, wPFI.Scan_Find_Size_k_wPFI(Ck));
            }
               
            k += 1;
            System.out.printf("Running %d-th iteration with %d WPFI %d-itemset \n", k-1, WPFI.get(k-1).size(), k-1);
        }

        return WPFI.get(k-2);
    }

    // public Set<Set<E>> topKFrequent(int k, Find_wPFI<E> wPFI) {
    //     PriorityQueue<ItemSet<E>> AIQ = new PriorityQueue<>(k);
    //     int m = 0;
    //     for (E item : this.I) {
    //         Set<E> singleItemset = new HashSet<>();
    //         singleItemset.add(item);
    //         AIQ.add(new ItemSet<E>(singleItemset, wPFI.Pr(this.data, singleItemset, this.minSup)[0]));
    //     }

    //     while(getNext(AIQ, k, m) != null) {

    //     }
    //     return null;
    // }

    // public Set<E> getNext(PriorityQueue<ItemSet<E>> AIQ, int k, int m) {
    //     if (AIQ.isEmpty() || m == k) {
    //         return null;
    //     }

    //     ItemSet<E> X = AIQ.poll();
    //     m += 1;
    //     return null;
    // }

    public void readDataTest(String filename) {
        FileReader fr =null;
        BufferedReader br = null;
        this.data = new ArrayList<>();
        this.I = new HashSet<>();
  
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            String line; 
        
            while((line = br.readLine()) != null) {
                Map<E, Double> transaction = new HashMap<>();
                String[] items = line.split(" ");
                for (String e : items) {
                    try {
                        E itemName = (E) e;
                        transaction.put(itemName, generateProb());
                        this.I.add(itemName);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
                this.data.add(transaction);
            }
            createWeight();
            this.minSup = 1;
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
        double prob;
        do {
            prob = random.nextGaussian() * Math.sqrt(0.125) + 0.5;
        } while (prob <= 0 || prob >= 1);
        prob = Math.round(prob*1000)/(Double)1000.0;
        if (prob == 1.0) {
            return 0.999;
        }
        return (prob == 0) ? 0.001 : prob;   
    }
}

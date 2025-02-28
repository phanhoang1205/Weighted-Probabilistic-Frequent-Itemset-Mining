import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WPFI_Algorithm {
    Map<Item, Double> mu_1 = new HashMap<>();
    public static void main(String[] args) {
        UncertainDatabase database = new UncertainDatabase(10000);
        Map<Double, Long> time_algorithm2 = new HashMap<>();
        Map<Double, Long> time_algorithm3 = new HashMap<>();

        // modify input parameter
        double threshold = 0.7;
        double alpha = 0.6;

        // modify list of ratio for minimum support parameter
        double[] ratios = new double[]{0.1, 0.15, 0.2, 0.25, 0.3};

        try{
            database.loadUnData("data/connect.txt");
            WPFI_Algorithm apriori = new WPFI_Algorithm();

            // BenchMark for algorithm 2
            for (double ratio : ratios) {
                int minSup = (int)(ratio * database.size());
                long startTime = System.nanoTime();
                Set<IItemSet> WPFI = apriori.runAlgorithm(database, minSup, threshold, alpha, false);
                long endTime = System.nanoTime();
                long executionTime = (endTime - startTime) / 1000000000;
                System.out.println("took: "
                                        + executionTime + "s");
                System.out.println("===========finish===========");
                time_algorithm2.put(ratio, executionTime);
            }

            System.out.println("\n======================Algorithm3==========================\n");

            // BenchMark for algorithm 3
            for (double ratio : ratios) {
                int minSup = (int)(ratio * database.size());
                long startTime = System.nanoTime();
                Set<IItemSet> WPFI = apriori.runAlgorithm(database, minSup, threshold, alpha, true);
                long endTime = System.nanoTime();
                long executionTime = (endTime - startTime) / 1000000000;
                System.out.println("took: "
                                        + executionTime + "s");
                System.out.println("===========finish===========");
                time_algorithm3.put(ratio, executionTime);
            }
            

            // print the benchmark result for both algorithm
            System.out.println(time_algorithm2);
            System.out.println(time_algorithm3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Default constructor
     */
    public WPFI_Algorithm() {}

    /**
     * Apriori: Find the WPFI in uncertain database
     * @param database uncertain database.
     * @param minSup minimum support.
     * @param t threshold.
     * @param a scale factor.
     * @return Set<IItemSet> representing WPFI.
    */
    public Set<IItemSet> runAlgorithm(UncertainDatabase database, int minSup, double t, double a, boolean useProbabilityModel) {
        Map<Integer, Set<IItemSet>> WPFI = new HashMap<>();
        Map<Integer, Double> weightTable = database.getW();

        Set<IItemSet> WPFI_1 = Scan_Find_Size_1_wPFI(database, weightTable, minSup, t, this.mu_1);
        WPFI.put(1, WPFI_1);

        System.out.printf("Running 1-th iteration with %d WPFI %d-itemset \n", WPFI.get(1).size(), 1);
        if (useProbabilityModel) {
            return useAlgorithm3(WPFI, database, minSup, t, a, this.mu_1);
        } else {
            return useAlgorithm2(WPFI, database, minSup, t);
        }
    }

    /**
     * This is a function that uses algorithm 2 to support the runAlgorithm method.
     * @param WPFI weighted probabilistic frequent itemset
     * @param database uncertain database.
     * @param minSup minimum support.
     * @param t a float representing the minimum confidence threshold.
     * @param a float representing the scaling factor for the probability model.
     * @return Set<IItemSet> A weighted probabilistic frequent itemset WPFI.
    */
    static Set<IItemSet> useAlgorithm2(Map<Integer, Set<IItemSet>> WPFI, UncertainDatabase database, int minSup, double t) {
        int k = 2;
        while(!WPFI.get(k-1).isEmpty()) {
            Set<IItemSet> Ck = Algorithm2(WPFI.get(k-1), database.getAllItems(), database.getW(), minSup, minSup, t);
            System.out.println("Size Ck: " + Ck.size());
            Set<IItemSet> WPFI_k = Scan_Find_Size_k_wPFI(Ck, database, database.getW(), minSup, t);
            WPFI.put(k, WPFI_k);
            k += 1;
            System.out.printf("Running %d-th iteration with %d WPFI %d-itemset \n", k-1, WPFI.get(k-1).size(), k-1);
            System.out.println("======================");
        }
        return WPFI.get(k-2);
    }

    /**
     * This is a function that uses algorithm 3 to support the runAlgorithm method.
     * @param WPFI weighted probabilistic frequent itemset
     * @param database uncertain database.
     * @param minSup minimum support.
     * @param t a float representing the minimum confidence threshold.
     * @param a float representing the scaling factor for the probability model.
     * @param mu_1 Total probability of itemset wPFI size-1 appearing in transaction.
     * @return Set<IItemSet> A weighted probabilistic frequent itemset WPFI.
    */
    static Set<IItemSet> useAlgorithm3(Map<Integer, Set<IItemSet>> WPFI, UncertainDatabase database, int minSup, double t, double a,
                                            Map<Item, Double> mu_1) {
        int k = 2;
        while(!WPFI.get(k-1).isEmpty()) {
            Set<IItemSet> Ck = Algorithm3(WPFI.get(k-1), database.getAllItems(), database.getW(), database.size(), minSup, t, a, mu_1);
            System.out.println("Size Ck: " + Ck.size());
            Set<IItemSet> WPFI_k = Scan_Find_Size_k_wPFI(Ck, database, database.getW(), minSup, t);
            WPFI.put(k, WPFI_k);
            k += 1;
            System.out.printf("Running %d-th iteration with %d WPFI %d-itemset \n", k-1, WPFI.get(k-1).size(), k-1);
            System.out.println("======================");
        }
        return WPFI.get(k-2);
    }

    /**
     * Algorithm2: The implementation of Algorithm 2 in the research paper.
     *                      Generate candidate PFI of size k from PFI of size k-1.
     * @param WPFIk_minus_1 set of wPFIs of size (k-1).
     * @param I set of all items.
     * @param w A weight table that assigns a real-valued weight to each item in an itemset
     * @param n number of transactions in the database.
     * @param minSup minimum support for frequent itemsets.
     * @param t threshold value.
     * @return set of itemsets representing candidate PFI of size k
    */

    static Set<IItemSet> Algorithm2(Set<IItemSet> WPFIk_minus_1, Set<Item> I, 
                                            Map<Integer, Double> w, int n, int minSup, double t) {
        Set<IItemSet> Ck = new HashSet<>();
        IItemSet I0 = get_All_I0(WPFIk_minus_1);
        for (IItemSet X : WPFIk_minus_1) {
            IItemSet I0_X = new ItemSet(I0);
            I0_X.removeAll(X);

            for (Item Ii : I0_X.getItems()) {
                IItemSet union = new ItemSet(X);
                union.addItem(Ii);
                if (calculateWeight(union, w) >= t) {
                    Ck.add(union);
                }
            }

            Item Im = argmin(X, w);
            Set<Item> prunning = new HashSet<>(I); 
            prunning.removeAll(I0.getItems());
            for (Item Ii : prunning) {
                IItemSet union = new ItemSet(X);
                union.addItem(Ii);
                if (calculateWeight(union, w) >= t && w.get(Ii.getItem()) < w.get(Im.getItem())) {
                    Ck.add(union);
                }
            }
        }
        return Ck;
    }


    /**
     * Algorithm3: The implementation of Algorithm 3 in the research paper.
     *                      Generate candidate PFI of size k from PFI of size k-1.
     * @param WPFIk_minus_1 set of wPFIs of size (k-1).
     * @param I set of all items.
     * @param w A weight table that assigns a real-valued weight to each item in an itemset
     * @param n length of database
     * @param minSup: Minimum support for frequent itemsets.
     * @param alpha: scale factor.
     * @param t: threshold
     * @param mu_k: representing the mean of a Poisson binomial distribution associated with itemsets in wPFI size k-1.
     * @param mu_1: representing the mean of a Poisson binomial distribution associated with itemsets in wPFI size 1.
     * @return set of itemsets representing candidate PFI of size k
    */
    static Set<IItemSet> Algorithm3(Set<IItemSet> WPFIk_minus_1, Set<Item> I, 
                                    Map<Integer, Double> w, int n, int minSup, double t, double a, Map<Item, Double> mu) {
        Set<IItemSet> Ck = new HashSet<>();
        Double m = Collections.max(w.values());
        Double mu_hat = findMuHat(minSup, t, m);
        IItemSet I0 = get_All_I0(WPFIk_minus_1);
        for (IItemSet X : WPFIk_minus_1) {
            IItemSet I0_X = new ItemSet(I0);
            I0_X.removeAll(X);
            for (Item Ii : I0_X.getItems()) {
                IItemSet union = new ItemSet(X);
                union.addItem(Ii);
                if (calculateWeight(union, w) >= t) {
                    if ((min(X.getMuy(), mu.get(Ii)) >= mu_hat) && (X.getMuy()*mu.get(Ii) >= a*n*mu_hat)) {
                        Ck.add(union);
                    } 
                }
            }
            Item Im = argmin(X, w);
            Set<Item> prunning = new HashSet<>(I); 
            prunning.removeAll(I0.getItems());
            for (Item Ii : prunning) {
                IItemSet union = new ItemSet(X);
                union.addItem(Ii);
                if (calculateWeight(union, w) >= t && w.get(Ii.getItem()) < w.get(Im.getItem())) {
                    if ((min(X.getMuy(), mu.get(Ii)) >= mu_hat) && (X.getMuy()*mu.get(Ii) >= a*n*mu_hat)) {
                        Ck.add(union);
                    } 
                }
            }
        }
        return Ck;
    }


    /**
     * get all items from the wPFI of size (k-1). 
     * @param WPFIk_minus_1: set of weighted probabilistic frequent itemsets of size (k-1).
     * @return IItemSet - The itemset containing all items from WPFI size (k-1).
    */
    static IItemSet get_All_I0(Set<IItemSet> WPFIk_minus_1) {
        IItemSet I0 = new ItemSet();
        for (IItemSet itemSet : WPFIk_minus_1) {
            for (Item i : itemSet.getItems()) {
                I0.addItem(i);
            }
        }
        return I0;
    }


    /**
     * Calculate the weight of an itemset.
     * @param itemSet: Set of item. 
     * @param w: representing the weights for each item.
     * @return double average weight of the items in the itemset X .
     */
    static double calculateWeight(IItemSet itemSet, Map<Integer, Double> w) {
        double sum = 0;
        for (Item e : itemSet.getItems()) {
            sum += w.get(e.getItem());
        }
        return sum / itemSet.size();
    }


    /** 
     * Finds wPFIs of size 1
     * @param minSup: Minimum support for frequent itemsets.
     * @param t: threshold
     * @param alpha: scale factor.
     * @param mu_k: representing the mean of a Poisson binomial distribution associated with itemsets in wPFI size (k-1).
     * @param mu_1: representing the mean of a Poisson binomial distribution associated with itemsets in wPFI size 1.
     * @param DB uncertain database.
     * @return returns a set of itemset representing weighted probabilistic frequent itemsets of size 1.
    */
    static Set<IItemSet> Scan_Find_Size_1_wPFI(UncertainDatabase DB, Map<Integer, Double> w, 
                                                            int minSup, double t, Map<Item, Double> mu) {
        Set<IItemSet> L1 = new HashSet<>();
        for (Item i : DB.allItems) {
            ItemSet itemSet = new ItemSet();
            itemSet.addItem(i);
            double weightItem = calculateWeight(itemSet, w);
            double[] pr_mu = Pr(DB, itemSet, minSup, t);
            double pr = pr_mu[0];
            double mu_itemset = pr_mu[1];
            mu.put(i, mu_itemset);
            // System.out.println(""+ i.item + "\t" + pr);
            if (weightItem * pr >= t) {
                itemSet.setMuy(mu_itemset);
                L1.add(itemSet);
            }
        }
        return L1;
    }


    /** 
     * Identify PFIs of size k from a set of candidate PFI.
     * @param Ck: Set of itemsets representing candidate PFI size k.
     * @param minSup: Minimum support for frequent itemsets.
     * @param DB uncertain database.
     * @return a set of itemsets representing wPFI of size k.
    */
    static Set<IItemSet> Scan_Find_Size_k_wPFI(Set<IItemSet> Ck, UncertainDatabase DB, 
                                                    Map<Integer, Double> w, int minSup, double t) {
        Set<IItemSet> Lk = new HashSet<>();
        for (IItemSet itemSet : Ck) {
            double weightItem = calculateWeight(itemSet, w);
            double[] pr_mu = Pr(DB, itemSet, minSup, t);
            double pr = pr_mu[0];
            double mu_itemSet = pr_mu[1];
            if (weightItem * pr >= t) {
                itemSet.setMuy(mu_itemSet);
                Lk.add(itemSet);
            }
        }
        return Lk;
    }


    /** 
     * Calculate the probability of a given itemset within a specific transaction.
     * @param X: Set of items of type Item.
     * @param T: a transaction
     * @return a double value representing the probability of the given itemset
     *            occurring in the specified transaction
    */
    static double calculate_prob(Set<Item> X, IItemSet T) {
        double prob = 1.0;
        if (T.getItems().containsAll(X)) {
            for (Item item : X) {
                for (Item i : T.getItems()) {
                    if (i.equals(item)) {
                        prob *= i.getProbability();
                    }
                }
            }
            return prob;
        } else {
            return 0;
        }
    }

    /** 
     * Calculate the probability of a given itemset occurring at least minsup times
     *             in a transaction. and mean of an element entry X in an uncertain database
     * 
     * @param DB: uncertain database
     * @param X: Set of items
     * @param minSup: Minimum support for frequent itemsets.
     * @return a double value representing the probability of the given itemset
     *         occurring at least minsup times in a transaction.
    */
    static double[] Pr(UncertainDatabase DB, IItemSet X, int minSup, double t) {
        int dbSize = DB.size();
        double[][] P = new double[minSup + 1][dbSize + 1];
        
        // Pre-calculate probabilities
        double mu_itemset = 0;
        double[] probabilities = new double[dbSize];
        for (int j = 0; j < dbSize; j++) {
            probabilities[j] = calculate_prob(X.getItems(), DB.transactions.get(j));
            mu_itemset += probabilities[j];
        }
        
        P[0][0] = 1.0;
        for (int j = 1; j <= dbSize; j++) {
            P[0][j] = 1.0;
            P[1][j] = P[1][j-1] + probabilities[j-1] * (1 - P[1][j-1]);
        }
        
        for (int i = 2; i <= minSup; i++) {
            // if (P[i-1][dbSize - i + 1] < t) {
            //     return new double[]{0.0, mu_itemset};
            // }
    
            for (int j = i; j <= dbSize; j++) {
                P[i][j] = P[i-1][j-1] * probabilities[j-1] + P[i][j-1] * (1 - probabilities[j-1]);
            }
        }

        double[] result = new double[]{P[minSup][dbSize], mu_itemset};
        return result;
    }
    
    /**
     * Find the minimum weight of the items within the given itemset.
     *
     * @param itemset set of item
     * @param weight a weight table of all items
     * @return a Item which have the minimum weight of any item in the
     *         given itemset.
    */
    static Item argmin(IItemSet itemset, Map<Integer, Double> weight) {
        Item minKey = null;
        Double minValue = Double.MAX_VALUE;
        for (Item key : itemset.getItems()) {
            Double value = weight.get(key.getItem());
            if (value != null && value < minValue) {
                minKey = key;
                minValue = value;
            }
        }
        return minKey;
    }

    /**
     * Calculate the factorial of a given non-negative integer n.
     * @param n an integer representing the non-negative number for which the
     *          factorial is to be calculated
     * @return a double value representing the factorial of n.
     */

    static int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        else {
            return n * factorial(n - 1);
        }
    }

    /** 
    * Calculate the CDF of Poisson Distribution at a given k value.
    * @param K an integer representing the number of occurences.
    * @param Lamda a double value representing the average rate of occurences.
    * @return a double value representing the CDF at step k  
    */
    static double F(int k, double lambda) {
        double result = 0;
        for (int i = 0; i <= k; i++) {
          result += Math.pow(lambda, i) * Math.exp(-lambda) / factorial(i);
        }
        return result;
    }

    /** 
     * This method approximates the mu_hat threshold using a binary search algorithm.
     * @param minSup: minimum support.
     * @param t: threshold.
     * @param m: a double value representing the maximum weight in the weight table.
     * @return a double value representing the mu_hat threshold
    */
    static Double findMuHat(int minSup, double t, double m) {
        double lower = 0.0;
        double upper = minSup;
        double epsilon = 0.0000000001;
        double mid = 0.0;

        while (upper - lower > epsilon) {
            mid = (lower + upper) / 2.0;
            double value = 1 - F(minSup - 1, mid) - t/m;
            if (value > 0) {
                upper = mid;
            } else if (value < 0) {
                lower = mid;
            } else {
                break;
            }
        }
        return mid;
    }

    /** 
    * Finds the minimum of two Double values
    * @return a double value representing the smallest value between 2 parameters
    */
    static Double min(Double mu_X, Double mu_Ii) {
        if (mu_X > mu_Ii) {
            return mu_Ii;
        } else {
            return mu_X;
        }
    }
}

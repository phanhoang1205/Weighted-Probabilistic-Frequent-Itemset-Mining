import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Find_wPFI <E>{
    protected Set<E> I;
    public ArrayList<Map<E, Double>> DB;
    protected Map<E, Double> w;
    protected int minSup;
    protected double t;
    protected Map<E, Double> mu_1;
    protected Map<Set<E>, Double> mu_k;

    public Find_wPFI(Set<E> I, ArrayList<Map<E, Double>> DB, Map<E, Double> w, int minSup, double t) {
        this.I = I;
        this.DB = DB;
        this.w = w;
        this.minSup = minSup;
        this.t = t;
    }

    public Set<Set<E>> Scan_Find_Size_1_wPFI() {
        Set<Set<E>> L1 = new HashSet<>();
        this.mu_k = new HashMap<Set<E>, Double>();
        this.mu_1 = new HashMap<>();

        for (E i : this.I) {
            double prob = 0.0;
            for (Map<E, Double> transaction : this.DB) {
                prob += transaction.get(i);   
            }
            this.mu_1.put(i, prob);
     
            Set<E> itemSet = new HashSet<>();
            itemSet.add(i);
            double w = calculateWeight(itemSet);
            // long startTime = System.nanoTime();
            double[] pr_mu = Pr(this.DB, itemSet, this.minSup);
            double pr = pr_mu[0];
            double mu_itemset = pr_mu[1];
            // long endTime = System.nanoTime();
            // long executionTime = (endTime - startTime) / 1000000;
            // System.out.println("took: "
            //                + executionTime + "ms");
            // System.out.println(pr);
            if (w*pr >= this.t) {
                L1.add(itemSet);
                this.mu_k.put(itemSet, mu_itemset);
            }
        }
        return L1;
    }

    public Set<Set<E>> Scan_Find_Size_k_wPFI(Set<Set<E>> Ck) {
        Set<Set<E>> Lk = new HashSet<>();
        this.mu_k = new HashMap<>();

        for (Set<E> itemSet : Ck) {
            double w = calculateWeight(itemSet);
            // long startTime = System.nanoTime();
            double[] pr_mu = Pr(this.DB, itemSet, this.minSup);
            // long endTime = System.nanoTime();
            // long executionTime = (endTime - startTime) / 1000000;
            // System.out.println("took: "
            //                + executionTime + "ms");
            // pr.multiply(BigDecimal.valueOf(w)).compareTo(BigDecimal.valueOf(t)) >=0
            double pr = pr_mu[0];
            double mu_itemSet = pr_mu[1];
            if (w*pr >= this.t) {
                Lk.add(itemSet);
                this.mu_k.put(itemSet, mu_itemSet);
            }
        }
        return Lk;
    }

    protected double calculateWeight(Set<E> itemSet) {
        double sum = 0;
        for (E e : itemSet) {
            sum += this.w.get(e);
        }
        return sum / itemSet.size();
    }

    private double calculate_prob(Set<E> X, Map<E, Double> T) {
        double prob = 1.0;
        for (E item : X) {
            prob *= T.get(item);
        }
        return prob;
    }

    public double[] Pr(ArrayList<Map<E, Double>> DB, Set<E> X, int minSup) {
        int dbSize = DB.size();
        double[][] P = new double[minSup + 1][dbSize + 1];
        
        // Pre-calculate probabilities
        double mu_itemset = 0;
        double[] probabilities = new double[dbSize];
        for (int j = 0; j < dbSize; j++) {
            probabilities[j] = calculate_prob(X, DB.get(j));
            mu_itemset += probabilities[j];
        }
        
        P[0][0] = 1.0;
        for (int j = 1; j <= dbSize; j++) {
            P[0][j] = 1.0;
            P[1][j] = P[1][j-1] + probabilities[j-1] * (1 - P[1][j-1]);
        }
        
        for (int i = 2; i <= minSup; i++) {
            if (P[i-1][dbSize - i + 1] < this.t) {
                return new double[]{0.0, mu_itemset};
            }
    
            for (int j = i; j <= dbSize; j++) {
                P[i][j] = P[i-1][j-1] * probabilities[j-1] + P[i][j-1] * (1 - probabilities[j-1]);
            }
        }

        double[] result = new double[]{P[minSup][dbSize], mu_itemset};
        return result;
    } 

    // private BigDecimal calculate_prob1(Set<E> X, Map<E, Double> T) {
    //     BigDecimal prob = BigDecimal.ONE;
    //     for (E item : X) {
    //         prob = prob.multiply(BigDecimal.valueOf(T.get(item)));
    //     }
    //     return prob;
    // }
    
    // public BigDecimal Pr1(ArrayList<Map<E, Double>> DB, Set<E> X, int minSup) {
    //     int dbSize = DB.size();
    //     BigDecimal[][] P = new BigDecimal[minSup + 1][dbSize + 1];
    //     for (int i = 0; i <= minSup; i++) {
    //         for (int j = 0; j <= dbSize; j++) {
    //             P[i][j] = BigDecimal.ZERO;
    //         }
    //     }        
        
    //     // Pre-calculate probabilities
    //     BigDecimal[] probabilities = new BigDecimal[dbSize];
    //     for (int j = 0; j < dbSize; j++) {
    //         probabilities[j] = calculate_prob1(X, DB.get(j));
    //     }
        
    //     P[0][0] = BigDecimal.ONE;
    //     for (int j = 1; j <= dbSize; j++) {
    //         P[0][j] = BigDecimal.ONE;
    //         P[1][j] = P[1][j-1].add(probabilities[j-1].multiply(BigDecimal.ONE.subtract(P[1][j-1])));
    //     }
        
    //     for (int i = 2; i <= minSup; i++) {
    //         if (P[i-1][dbSize - i + 1].compareTo(BigDecimal.valueOf(this.t)) < 0) {
    //             return BigDecimal.ZERO;
    //         }
        
    //         for (int j = i; j <= dbSize; j++) {
    //             P[i][j] = P[i-1][j-1].multiply(probabilities[j-1]).add(P[i][j-1].multiply(BigDecimal.ONE.subtract(probabilities[j-1])));
    //         }
    //     }
    //     return P[minSup][dbSize];
    // }
    


    public Map<Set<E>, Double> getMu_k() {
        return mu_k;
    }
}

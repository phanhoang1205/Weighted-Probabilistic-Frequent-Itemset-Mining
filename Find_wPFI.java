import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
    Class: Find_wPFI<E>
    Constructor: Find_wPFI(Set<E> I, ArrayList<Map<E, Double>> DB, Map<E, Double> w, int minSup, double t)

    Parameter: 
    •   I, DB, W, minSup, t.

    Inputs:
    •	I: Set of generic type E representing the item set.
    •	DB: ArrayList of Maps, where each map represents a transaction with items of type E and associated probabilities.
    •	w: Map of items of type E and their associated weights (doubles).
    •	minSup: Minimum support for frequent itemsets.
    •	t: Threshold value.
 */

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

    /*
        Method: Scan_Find_Size_1_wPFI()

        Input:
        •	I: Set of generic type E representing the item set.
        •	minSup: Minimum support for frequent itemsets.
        •	DB: ArrayList of Maps, where each map represents a transaction with items of type E and associated probabilities

        Output:
        •	Returns a set of sets (Set<Set<E>>) representing frequent itemsets of size 1.
        •	Behavior:
        •	Scans the database (DB) for frequent itemsets of size 1.
        •	Computes probabilities and weights.
        •	Filters itemsets based on minimum support (minSup) and a threshold (t).

        Time Complexity:
        bacsic operation: double[] pr_mu = Pr(this.DB, itemSet, this.minSup);
        Loop over I: O(|I|), where |I| is the number of unique items.
        Inner loop over transactions in DB: O(N), where N is the number of transactions.
        Calculating calculateWeight, Pr: O(minSup * N).
        Overall: O(minSup * N * |I|).

        Space Complexity:
        Storage of L1, mu_k, mu_1: O(M), where M is the number of unique items. 
    */

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
            double[] pr_mu = Pr(this.DB, itemSet, this.minSup);
            double pr = pr_mu[0];
            double mu_itemset = pr_mu[1];
            if (w*pr >= this.t) {
                L1.add(itemSet);
                this.mu_k.put(itemSet, mu_itemset);
            }
        }
        return L1;
    }

    /*
        Method: Scan_Find_Size_k_wPFI(Set<Set<E>> Ck)

        Inputs:
        •	Ck: Set of sets representing candidate itemsets of size k.
        •	I: Set of generic type E representing the item set.
        •	minSup: Minimum support for frequent itemsets.
        •	DB: ArrayList of Maps, where each map represents a transaction with items of type E and associated probabilities
        
        Output:
        •	Returns a set of sets (Set<Set<E>>) representing frequent itemsets of size k.
        •	Behavior:
        •	Scans the database (DB) for frequent itemsets of size k using candidate itemsets (Ck).
        •	Computes probabilities and weights.
        •	Filters itemsets based on minimum support (minSup) and a threshold (t).

        Time Complexity:
        basic operation: Pr(this.DB, itemSet, this.minSup);
        Loop over Ck: O(K), where K is the number of sets in Ck.
        Calculating calculateWeight, Pr: O(minSup * N).
        Overall: O(minSup * N * K), where K is the number of sets in Ck.

        Space Complexity:
        Storage of Lk, mu_k: O(K * M), where K is the number of sets in Ck.

    */

    public Set<Set<E>> Scan_Find_Size_k_wPFI(Set<Set<E>> Ck) {
        Set<Set<E>> Lk = new HashSet<>();
        this.mu_k = new HashMap<>();

        for (Set<E> itemSet : Ck) {
            double w = calculateWeight(itemSet);
            double[] pr_mu = Pr(this.DB, itemSet, this.minSup);
            double pr = pr_mu[0];
            double mu_itemSet = pr_mu[1];
            if (w*pr >= this.t) {
                Lk.add(itemSet);
                this.mu_k.put(itemSet, mu_itemSet);
            }
        }
        return Lk;
    }

    /*
        Input:
        •	Set<E> itemset: Set of item type E.
        •	w: Map of items of type E and their associated weights (doubles).

        Output:
        •	Average weight of the items in the itemset X .

        Time Complexity:
        basic operation: sum += this.w.get(e);
        Loop over items in itemSet: O(|X|), where |X| is the number of items in itemset.

        Space Complexity:
        Storage of sum: O(1). 
    */

    protected double calculateWeight(Set<E> itemSet) {
        double sum = 0;
        for (E e : itemSet) {
            sum += this.w.get(e);
        }
        return sum / itemSet.size();
    }

    /*
        Method: calculate_prob(Set<E> X, Map<E, Double> T)
        Inputs:
        •	X: Set of items of type E.
        •	T: Map representing a transaction with items of type E and associated probabilities.

        Output:
        •	Returns a double representing the calculated probability for the input item set in the given transaction.

        Behavior:
        •	Calculates the probability of the given item set in the context of a specific transaction.

        Time Complexity:
        Loop over items in X: O(M), where M is the number of unique items.

        Space Complexity:
        Storage of prob: O(1). 
    */

    private double calculate_prob(Set<E> X, Map<E, Double> T) {
        double prob = 1.0;
        for (E item : X) {
            prob *= T.get(item);
        }
        return prob;
    }

    /*
        Method: Pr(ArrayList<Map<E, Double>> DB, Set<E> X, int minSup)

        Inputs:
        •	DB: ArrayList of Maps representing the database.
        •	X: Set of items of type E.
        •	minSup: Minimum support for frequent itemsets.

        Output:
        •	Returns a double representing the probability of the given item set in the database.
        •	Behavior:
        •	Computes the probability of the given item set in the context of the database using dynamic programming.

        Time Complexity:
        basic operation: P[i][j] = P[i-1][j-1] * probabilities[j-1] + P[i][j-1] * (1 - probabilities[j-1]);
        Loop over transactions in DB: O(|N|), where |N| is the number of transactions.
        Pre-calculation loop: O(|N|).

        Dynamic programming loop: O(minSup * |N|).
        Overall: O(minSup * |N| + |N|) = O(minSup * |N|).

        Space Complexity:
        Storage of P, probabilities: O(minSup * N). 
    */

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

    public Map<Set<E>, Double> getMu_k() {
        return mu_k;
    }
}

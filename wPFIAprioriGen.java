import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class wPFIAprioriGen<E> extends Find_wPFI<E>{
    private Set<Set<E>> Lk;
    private double alpha;
    private int n;

    /*
        This class extends Find_wPFI<E> and provides implementations for the weighted Apriori generation algorithm.
        Input Parameters:
        Lk: Set<Set<E>> - Frequent itemsets of the previous level.
        alpha: double - A parameter used in the algorithm.
        wPFI: Find_wPFI<E> - An instance of the Find_wPFI class providing information about the dataset and other parameters.
    */

    public wPFIAprioriGen(Set<Set<E>> Lk, double alpha, Find_wPFI<E> wPFI){
        super(wPFI.I, wPFI.DB, wPFI.w, wPFI.minSup, wPFI.t);
        this.Lk = Lk;
        this.alpha = alpha;
        this.mu_k = wPFI.mu_k;
        this.mu_1 = wPFI.mu_1;
        this.n = DB.size();
    }

    /*
        Methods:
        algorithm2(): Generates candidate itemsets based on Algorithm

        Input:
        None (Relies on class fields Lk, t, w, and DB)

        Output:
        Set<Set<E>> - Candidate itemset 
        Time Complexity:
        Constructing I0 set: O(M).
        Loop over Lk: O(K * M), where K is the number of sets in Lk.
        Inner loop constructing difference1: O(K * M).
        Inner loop constructing union: O(K * M).
        Calculating calculateWeight: O(K * M).
        Overall: O(K * M    
        Space Complexity:
        Storage of Ck, I0, difference1, union: O(M), where M is the number of unique items.
    */

    public Set<Set<E>> algorithm2() {
        Set<Set<E>> Ck = new HashSet<>();
        Set<E> I0 = new HashSet<>();

        for (Set<E> itemSet : Lk) {
            for (E i : itemSet) {
                I0.add(i);
            }
        }

        for (Set<E> X : Lk) {
            Set<E> difference1 = new HashSet<>(I0);
            difference1.removeAll(X);
            for (E Ii : difference1) {
                Set<E> union = new HashSet<>(X);
                union.add(Ii);
                if (calculateWeight(union) >= t) {
                    Ck.add(union);
                }
            }

            E Im = argmin(X, w);
            Set<E> prunning = new HashSet<>(I);
            prunning.removeAll(I0);
            prunning.removeAll(X);
            for (E Ii : prunning) {
                Set<E> union = new HashSet<>(X);
                union.add(Ii);
                if (calculateWeight(union) >= t && w.get(Ii) < w.get(Im)) {
                    Ck.add(union);
                }
            }
        }
        return Ck;
    };

    /*
        Algorithm3(): Generates candidate itemsets based on Algorithm 3
        Input:

        Input:
        •	Ck: Set of sets representing candidate itemsets of size k.
        •	I: Set of generic type E representing the item set.
        •	minSup: Minimum support for frequent itemsets.
        •	DB: ArrayList of Maps, where each map represents a transaction with items of type E and associated probabilities
        •	alpha: scale factor.
        •	mu_k: representing the mean of a Poisson binomial distribution associated with itemsets in wPFIk_minus_1.
        •	mu_1: representing the mean of a Poisson binomial distribution associated with itemsets in wPFI_1.

        Output:
        Set<Set<E>> - Candidate itemsets

        Time Complexity:
        basic operation: calculateWeight(union) >= t

        Constructing I0 set: O(M).
        Loop over Lk: O(X), where X is the number of sets in Lk.
        Inner loop constructing difference1: O(N) where N is a number of item in I0 - X.
        Inner loop constructing union: O(M) where M is a number of item in I - I0 - X
        Calculating calculateWeight, findMuHat: O(log(minSup)).
        Overall: O(X *(M + N) + log(minSup)) = O(X *(M + N))

        Space Complexity:
        Storage of Ck, I0, difference1, union: O(M), where M is the number of unique items.
    */

    public Set<Set<E>> algorithm3() {
        Set<Set<E>> Ck = new HashSet<>();
        Set<E> I0 = new HashSet<>();
        double Is = Collections.min(w.values());

        for (Set<E> itemSet : Lk) {
            for (E i : itemSet) {
                I0.add(i);
            }
        }

        for (Set<E> X : Lk) {
            Double m = max(calculateWeight(X) , Is);
            Double mu_hat = findMuHat(minSup, t, m);

            Set<E> difference1 = new HashSet<>(I0);
            difference1.removeAll(X);
            for (E Ii : difference1) {
                Set<E> union = new HashSet<>(X);
                union.add(Ii);
                if (calculateWeight(union) >= t) {
                    if ((min(mu_k.get(X), mu_1.get(Ii)) >= mu_hat) && (mu_k.get(X)*mu_1.get(Ii) >= alpha*n*mu_hat)) {
                        Ck.add(union);
                    } 
                }
            }

            E Im = argmin(X, w);
            Set<E> prunning = new HashSet<>(I);
            prunning.removeAll(I0);
            prunning.removeAll(X);
            for (E Ii : prunning) {
                Set<E> union = new HashSet<>(X);
                union.add(Ii);
                if (calculateWeight(union) >= t && w.get(Ii) < w.get(Im)) {
                    if ((min(mu_k.get(X), mu_1.get(Ii)) >= mu_hat) && (mu_k.get(X)*mu_1.get(Ii) >= alpha*n*mu_hat)) {
                        Ck.add(union);
                    }
                }
            }
        }
        return Ck;
    };

    /*
        Min(Double mu_X, Double mu_Ii): Finds the minimum of two Double values
        Time Complexity: O(1).
        Space Complexity: O(1).
    */

    private Double min(Double mu_X, Double mu_Ii) {
        if (mu_X > mu_Ii) {
            return mu_Ii;
        } else {
            return mu_X;
        }
    }

    /*
        Max(Double w_X, Double w_Is): Finds the maximum of two Double values.
        Time Complexity: O(1).
        Space Complexity: O(1).
    */

    private Double max(Double w_X, Double w_Is) {
        if (w_X > w_Is) {
            return w_X;
        } else {
            return w_Is;
        }
    }

    /*
        Argmin(Set<E> set, Map<E, Double> weight): Finds the key with the minimum value in a map for a given set of key 
        Input:
        •	set: Set of items type E.
        •	w: Map of items of type E and their associated weights (doubles).
        Output:
            Return an item have smallest weight in itemset X

        Time Complexity:
        basic operation: value != null && value < minValue
        Loop over set: O(|X|), where |X| is the number of items in itemset X  

        Space Complexity:
        Storage of minKey, minValue: O(1).
    */

    private E argmin(Set<E> set, Map<E, Double> weight) {
        E minKey = null;
        Double minValue = Double.MAX_VALUE;
        for (E key : set) {
            Double value = weight.get(key);
            if (value != null && value < minValue) {
                minKey = key;
                minValue = value;
            }
        }
        return minKey;
    }

    /*
        FindMuHat(int minSup, double t, double m): Finds the value of mu_hat based on input parameters
        Input:
        •	minSup: minimum support.
        •	t: threshold.
        •	m: take a max weight between itemset X and smallest Is in I.

        Time Complexity:
        basic operation: double value = 1 - F(minSup - 1, mid) - t/m
        Binary search loop: O(log(minSup)).
        Calculating F: O(minSup).
        Overall: O(log(minSup))

        Space Complexity:
        Storage of lower, upper, epsilon, mid, value: O(1). 
    */

    private Double findMuHat(int minSup, double t, double m) {
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

    /*
        F(int k, double lambda): Computes the F function used in the calculation of mu_ha
        Input:
        •	K: 
        •	lambda: 

        Output:
        •	cdf of Poisson Distribution   

        Time Complexity:
        Loop over k: O(k).
        Overall: O(k)

        Space Complexity:
        Storage of result: O(1). 
    */

    private double F(int k, double lambda) {
        double result = 0;
        for (int i = 0; i <= k; i++) {
          result += Math.pow(lambda, i) * Math.exp(-lambda) / factorial(i);
        }
        return result;
    }

    /*
        Factorial(int n): Computes the factorial of a given integer
        
        Time Complexity:
        Recursive loop: O(n).
        Overall: O(n)

        Space Complexity:
        Recursive call stack: O(n).
    */
      
    private int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        else {
            return n * factorial(n - 1);
        }
    }
}

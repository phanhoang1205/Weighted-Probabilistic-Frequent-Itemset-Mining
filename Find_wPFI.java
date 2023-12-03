import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Find_wPFI <E>{
    protected Set<E> I;
    protected ArrayList<Map<E, Double>> DB;
    protected Map<E, Double> w;
    protected int minSup;
    protected double t = 0.6;

    public Find_wPFI(Set<E> I, ArrayList<Map<E, Double>> DB, Map<E, Double> w, int minSup, double t) {
        this.I = I;
        this.DB = DB;
        this.w = w;
        this.minSup = minSup;
        this.t = t;
    }

    public Set<Set<E>> Scan_Find_Size_1_wPFI() {
        Set<Set<E>> L1 = new HashSet<>();
        for (E i : this.I) {
            int supItemSet = 0;
            for (Map<E, Double> transaction : this.DB) {
                if (transaction.keySet().contains(i)) {
                    supItemSet += 1;
                }     
            }

            if (supItemSet >= this.minSup) {
                Set<E> itemSet = new HashSet<>();
                itemSet.add(i);
                double w = calculateWeight(itemSet);
                double pr = Pr(this.DB, itemSet, this.minSup);
                if (w*pr >= this.t) {
                    L1.add(itemSet);
                }
            }
        }
        return L1;
    }

    public Set<Set<E>> Scan_Find_Size_k_wPFI(Set<Set<E>> Ck) {
        Set<Set<E>> Lk = new HashSet<>();
        for (Set<E> itemSet : Ck) {
            int supItemSet = 0;
            for (Map<E, Double> transaction : this.DB) {
                if (transaction.keySet().containsAll(itemSet)) {
                    supItemSet += 1;
                }
            }

            if (supItemSet >= this.minSup) {
                double w = calculateWeight(itemSet);
                if (w*Pr(this.DB, itemSet, this.minSup) >= this.t) {
                    Lk.add(itemSet);
                }
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
        if (T.keySet().containsAll(X)) {
            for (E item : X) {
                prob *= T.get(item);
            }
        } else {
            return 0.0;
        }
        return prob;
    }

    public double Pr(ArrayList<Map<E, Double>> DB, Set<E> X, int minSup) {
        Double[][] P = new Double[minSup + 1][DB.size() + 1];
        Arrays.parallelSetAll(P, i -> new Double[DB.size() + 1]);
        try {
            for (Double[] row : P) {
                Arrays.fill(row, 0.0);
            }
            
            P[0][0] = 1.0;
            for (int j = 1; j <= DB.size(); j++) {
                P[0][j] = 1.0;
                P[1][j] = P[1][j-1] + calculate_prob(X, DB.get(j-1)) * (1 - P[1][j-1]);
            }
            
            for (int i = 2; i <= minSup; i++) {
                if (P[i-1][DB.size()-i+1] < this.t) {
                    return 0;
                }

                for (int j = i; j <= DB.size(); j++) {
                    P[i][j] = P[i-1][j-1] * calculate_prob(X, DB.get(j-1)) + P[i][j-1] * (1 - calculate_prob(X, DB.get(j-1)));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return P[minSup][DB.size()];
    }
}

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class wPFIAprioriGen<E> extends Find_wPFI<E>{
    private Set<Set<E>> Lk;
    private double alpha;

    public wPFIAprioriGen(Set<Set<E>> Lk, double alpha, Find_wPFI<E> wPFI){
        super(wPFI.I, wPFI.DB, wPFI.w, wPFI.minSup, wPFI.t);
        this.Lk = Lk;
        this.alpha = alpha;
        this.mu_k = wPFI.mu_k;
        this.mu_1 = wPFI.mu_1;
    }

    // public wPFIAprioriGen(Set<Set<E>> Lk, Map<Set<E>, Double> mu_k, double alpha, Find_wPFI<E> wPFI) {
    //     super(wPFI.I, wPFI.DB, wPFI.w, wPFI.minSup, wPFI.t);
    //     this.Lk = Lk;
    //     this.alpha = alpha;
    // }

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

    public Set<Set<E>> algorithm3() {
        Set<Set<E>> Ck = new HashSet<>();
        Set<E> I0 = new HashSet<>();
        Double m = Collections.max(w.values());
        Double mu_hat = findMuHat(minSup, t, m);

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
                    if (min(mu_k.get(X), mu_1.get(Ii)) > mu_hat && mu_k.get(X)*mu_1.get(Ii) >= alpha*DB.size()*mu_hat) {
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
                    if (min(mu_k.get(X), mu_1.get(Ii)) > mu_hat && mu_k.get(X)*mu_1.get(Ii) >= alpha*DB.size()*mu_hat) {
                        Ck.add(union);
                    }
                }
            }
        }
        return Ck;
    };

    private Double min(Double mu_X, Double mu_Ii) {
        if (mu_X > mu_Ii) {
            return mu_Ii;
        } else {
            return mu_X;
        }
    }

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

    private Double findMuHat(int minSup, double t, double m) {
        double lower = 0.0;
        double upper = (double) DB.size();
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

    private double F(int k, double lambda) {
        double result = 0;
        for (int i = 0; i <= k; i++) {
          result += Math.pow(lambda, i) * Math.exp(-lambda) / factorial(i);
        }
        return result;
    }
      
    private int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        else {
            return n * factorial(n - 1);
        }
    }
}

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class wPFIAprioriGen<E> extends Find_wPFI<E>{
    private Set<Set<E>> Lk;
    private double alpha;
    private Map<Set<E>, Double> mu_k;

    public wPFIAprioriGen(Set<Set<E>> Lk, double alpha, Find_wPFI<E> wPFI){
        super(wPFI.I, wPFI.DB, wPFI.w, wPFI.minSup, wPFI.t);
        this.Lk = Lk;
        this.alpha = alpha;
    }

    public wPFIAprioriGen(Set<Set<E>> Lk, Map<Set<E>, Double> mu_k, double alpha, Find_wPFI<E> wPFI) {
        super(wPFI.I, wPFI.DB, wPFI.w, wPFI.minSup, wPFI.t);
        this.Lk = Lk;
        this.alpha = alpha;
    }

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
    public void algorithm3() {
        Set<Set<E>> Ck = new HashSet<>();
        Set<E> I0 = new HashSet<>();

        for (Set<E> itemSet : Lk) {
            for (E i : itemSet) {
                I0.add(i);
            }
        }

    };

    private E argmin(Set<E> set, Map<E, Double> map) {
        E minKey = null;
        Double minValue = Double.MAX_VALUE;
        for (E key : set) {
            Double value = map.get(key);
            if (value != null && value < minValue) {
                minKey = key;
                minValue = value;
            }
        }
        return minKey;
    }
}

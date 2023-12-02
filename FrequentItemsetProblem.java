import java.io.*;
import java.util.*;

public class FrequentItemsetProblem <E extends Comparable<E>> {
    private int minSup;
    private float t;
    private float alpha;
    private Set<E> I;
    ArrayList<Set<Item<E>>> data;
    private Map<E, Double> w;

    public FrequentItemsetProblem(String filePath) {
        readData(filePath);
        createWeight();
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
                Set<Item<E>> set = new HashSet<>();
                String[] items = line.split(" ");
                System.out.println(line);
                for (String e : items) {
                    try {
                        E itemName = (E)e;
                        this.I.add(itemName);
                        set.add(new Item<E>(itemName));
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
                this.data.add(set);
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
        for (Set<Item<E>> set : this.data) {
            System.out.print("[");
            System.out.print(set);
            System.out.println("]");
        }
    }

    public Map<E, Double> getW() {
        return w;
    }

    public void solve() {}
}

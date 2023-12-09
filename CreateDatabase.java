import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

public class CreateDatabase<E> {
    private int numOfLines;
    private Set<E> I;

    public CreateDatabase(int n, Set<E> I) {
        this.numOfLines = n;
        this.I = I;
    }

    public void createFullyDB() {
        File file = new File("fullyuncertaindb.txt");
        FileWriter fr = null;
        BufferedWriter br = null;
        
        try{
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            for(int i = 0; i < numOfLines; i++){
                String line = "";
                for(E item : I) {
                    line += String.valueOf(item) + ":" + String.valueOf(generateProb()) + " ";
                }
                br.write(line.substring(0, line.length() - 1) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
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



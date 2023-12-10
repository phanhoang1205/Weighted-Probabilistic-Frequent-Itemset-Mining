import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

public class CreateDatabase<E> {
    private int numOfLines;
    private Set<E> I;

    /*
        Constructor
    */

    public CreateDatabase(int n, Set<E> I) {
        this.numOfLines = n;
        this.I = I;
    }

    /*
        Input: 
        I: number of Item in the database

        Output:
        Generates a file named "fullyuncertaindb.txt" containing uncertain transactions.

        Behavior:
        Creates a new file or overwrites an existing one.
        For each line (transaction), generates a space-separated string of items along with associated probabilities.
        Writes the generated lines to the file.

        Time Complexity:
        basic operation: line += String.valueOf(item) + ":" + String.valueOf(generateProb()) + " ";
        The outer loop iterates over numOfLines transactions.
        The inner loop iterates over each item in the set I.
        Generating a line involves multiple operations, including string concatenation.
        Therefore, the overall time complexity is O(numOfLines * |I|).

        Space Complexity:
        The space complexity mainly involves the file and buffer writer.
        No significant additional data structures are used that grow with the input size.
        Therefore, the overall space complexity is O(1) or constant.
    
    */

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

    /*
        Output:
        Returns a probability value of double type between 0.001 and 0.999.

        Behavior:
        Generates a random double using a Gaussian distribution with a mean of 0.5 and a standard deviation of √0.125.
        Rounds the generated value to three decimal places.
        Ensures the generated probability is within the range (0.001, 0.999).
        Maps the value 1.0 to 0.999.

        Time Complexity:
        The time complexity is dominated by the generation of a random number using nextGaussian(), which is generally constant time.
        Other operations, such as rounding and checks, are also constant time.
        Therefore, the overall time complexity is O(1) or constant.

        Space Complexity:
        The space complexity is constant, as there are no data structures that grow with the input size.
        Therefore, the overall space complexity is O(1) or constant.
    */
    
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



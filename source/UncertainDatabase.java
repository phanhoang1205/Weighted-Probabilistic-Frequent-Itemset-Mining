import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class UncertainDatabase {
    
    public List<IItemSet> fullTransactions = new ArrayList<>();
    public List<IItemSet> partlyTransactions = new ArrayList<>();
    public List<IItemSet> transactions = new ArrayList<>();
    public final Set<Item> allItems = new HashSet<>();
    public Map<Integer, Double> w = new HashMap<>();
    // number of lines you want to read in database
    public Integer lines;
    // length of database
    public int n;

    public UncertainDatabase() {}
    public UncertainDatabase(int lines) {
        this.lines = lines;
    }

    /**
	 * Load a transaction database from a file.
	 * @param path the path of the file
	 * @throws IOException exception if error while reading the file.
	 */
    public void loadData(String path) throws IOException {
        File f = new File(path);
        BufferedReader br = null;
        int count = 0;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;     
            while((line = br.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                if (this.lines != null) {
                    if(count == this.lines) {
                        break;
                    }
                    processTransaction(line, this.allItems, this.transactions);
                    count += 1;
                } else {
                    processTransaction(line, this.allItems, this.transactions);
                }
            }
            this.partlyTransactions = transactions;
            this.n = transactions.size();
            this.w = createWeight(allItems, f.getName());
            createUnDB(f.getName(), this.transactions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
	 * Load a data form uncertain database in a file
	 * @param path the path of the file
	 * @throws IOException exception if error while reading the file.
	 */
    public void loadUnData(String path) throws IOException {
        File f = new File(path);
        BufferedReader br = null;
        int count = 0;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;     
            while((line = br.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                if (this.lines != null) {
                    if(count == this.lines) {
                        break;
                    }
                    processUnTransaction(line, this.allItems, this.transactions);
                    count += 1;
                } else {
                    processUnTransaction(line, this.allItems, this.transactions);
                }
            }
            this.partlyTransactions = transactions;
            this.n = transactions.size();
            this.w = loadWeight(f.getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a uncertain database based on the deterministic database and store it into the "data" folder located in the same root
     * @param fileName name of uncertain database that user wants to set
     * @param transactions list of transactions in database
     */
    static void createUnDB(String fileName, List<IItemSet> transactions) {
        BufferedWriter br = null;       
        try{
            br = new BufferedWriter(new FileWriter(new File("./data", fileName)));
            for(IItemSet items : transactions) {
                String line = "";
                for (Item item : items.getItems()) {
                    line += String.valueOf(item.getItem()) + ":" + String.valueOf(item.getProbability()) + " ";
                }
                br.write(line.substring(0, line.length() - 1) + "\n");
            }     
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ProcessTransaction: create Itemset associated with a probability and adding it to the transaction list.
     * @param line is a string representing a line of items.
     * @param allItems set of all items.
     * @param transactions list of itemsets representing transactions.
     */
    static void processTransaction(String line, Set<Item> allItems, List<IItemSet> transactions) {
        String[] items = line.split(" ");
        IItemSet transaction = new ItemSet();
        
        for (String itemString : items) {
            int itemName = Integer.parseInt(itemString);
            double prob = generateProb();
            Item item = new Item(itemName, prob);
            transaction.addItem(item);
            allItems.add(item);
        }
        transactions.add(transaction);
    }

    /**
     * ProcessUnTransaction: reading an ItemSet from uncertain database and adding it to the transaction list.
     * @param line is a string representing a line of items.
     * @param allItems set of all items.
     * @param transactions list of itemsets representing transactions.
     */
    static void processUnTransaction(String line, Set<Item> allItems, List<IItemSet> transactions) {
        String[] items = line.split(" ");
        IItemSet transaction = new ItemSet();
        for (String itemString : items) {
            String[] item_prob = itemString.split(":");
            int itemName = Integer.parseInt(item_prob[0]);
            Item item = new Item(itemName, Double.parseDouble(item_prob[1]));
            transaction.addItem(item);
            allItems.add(item);
        }
        transactions.add(transaction);
    }  

    /**
	 * Get the number of transactions.
	 * @return a int
	 */
	public int size() {
        return this.n;
	}

    /**
	 * Print this database to the console
	 */
	public void printDatabase() {
		System.out
				.println("===================  UNCERTAIN DATABASE ===================");
		int count = 0;
		// for each transaction
		for (IItemSet itemset : this.transactions) {
			// print the transaction
			System.out.print("0" + count + ":  ");
			itemset.print();
			System.out.println("");
			count++;
		}
	}


    /**
	 * convert partly uncertain database to fully uncertain database
	 */
    public void convertFullyDB() {
        IItemSet transaction = new ItemSet();
        for (int i = 0; i < this.n; i++) {
            for (Item item : allItems) {
                item.setProbability(generateProb());
                transaction.addItem(item);
            }
            this.fullTransactions.add(transaction);
        }
    }

    /**
     * CreateWeight: generates a random weight for each item and random double value greater than 0 and less than 1.
     *                  then create a file to store the weight table in the itemweight folder
     * @param allItems set of all items.
     * @param fileName name of weight table that user wants to set
     * @return Map<Integer, Double> - A map where the key is an item and the value is its weight.
     */
    static Map<Integer, Double> createWeight(Set<Item> allItems, String fileName) {
        Map<Integer, Double> w = new HashMap<Integer, Double>();
        Random random = new Random();
        BufferedWriter br = null;       
        try{
            br = new BufferedWriter(new FileWriter(new File("./itemweight", fileName)));
            for (Item item : allItems) {
                String line = "";
                double weight = random.nextDouble() + 0.000001;
                line += String.valueOf(item.getItem()) + ":" + weight + " ";
                br.write(line.substring(0, line.length() - 1) + "\n");
                w.put(item.getItem(), weight);
            }
            return w;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return w;
    }

    /**
	 * Load a weight of all item form weight table in a file
	 * @param fileName Name of the weight table that user wants to read
	 * @throws IOException exception if error while reading the file.
	 */
    static Map<Integer, Double> loadWeight(String fileName) throws IOException {
        Map<Integer, Double> w = new HashMap<Integer, Double>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("itemweight/" + fileName));
            String line;     
            while((line = br.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                String[] item_prob = line.split(":");
                w.put(Integer.parseInt(item_prob[0]), Double.parseDouble(item_prob[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return w;
    }

    /**
    * GenerateProb: Generates a random probability value, which is then rounded to three decimal places.
    * If the rounded value is 1.0, it returns 0.999 and if the rounded value is 0, it returns 0.001.
    * @return double the generated probability value. 
	 */
    static double generateProb() {
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

    /**
     * get the weight table after using loadData methods
     * @return weight table of all itemset
     */
    public Map<Integer, Double> getW() {
        return w;
    }

    /**
     * get all items exist in the database
     * @return all items
     */
    public Set<Item> getAllItems() {
        return allItems;
    }

    /**
     * change the partly database to fully database (converFullDB method is required if you want to use this method)
     */
    public void changeFully() {
        transactions = fullTransactions;
    }

    /**
     * change the fully database to partly database
     */
    public void changePartly() {
        transactions = partlyTransactions;
    }
}   

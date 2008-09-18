import java.util.ArrayList;

/**
 * Tests the BoggleEvolution class. Takes as command line arguments:
 * dictionaryPath [sideLength [-]]
 * @author Ankur Dave
 */
public class BogglePopulationTester {
	/**
     * Method called when program is run.
     * @param args
     *            arguments to the program
     */
	static int SIDE_LENGTH = 4, START_POP = 20, POP_CAP = 20,
	        AVG_CHILDREN_PER_COUPLE = 5;
	static String DICT_PATH = "words.txt";
	
	public static void main(String[] args) throws GenerationEmptyException {
		// initialize population
		Dictionary dict = new Dictionary();
		dict.buildDictionary(DICT_PATH);
		BogglePopulation bp = new BogglePopulation(SIDE_LENGTH, START_POP,
		        AVG_CHILDREN_PER_COUPLE, POP_CAP, dict);
		
		// start timer
		long startTime = System.currentTimeMillis();
		
		Boggle highest = bp.highest();
		
		// start evolution
		do {
			try {
				bp.evolve();
			}
			catch (GenerationEmptyException e) {
				System.err.println(e);
				break;
			}
			
			if (highest.compareTo(bp.highest()) < 0) {
				highest = bp.highest();
				// System.out.print(highest.getScore() + " ");
			}
			
			/*
             * System.out.println(bp); ArrayList<Boggle> gen =
             * bp.getCurrentGeneration(); for (int i = 0; i < gen.size(); i++) {
             * System.err.println(gen.get(i).gridToString() + " " +
             * gen.get(i).getScore()); }
             */
		} while ((highest.getScore() < 3500)
		        && (System.currentTimeMillis() - startTime < 1000000));
		
		// stop the timer
		long stopTime = System.currentTimeMillis();
		
		// System.out.println();
		System.out.println(highest + " " + (stopTime - startTime));
	}
}

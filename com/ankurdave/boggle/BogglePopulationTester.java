package com.ankurdave.boggle;

/**
 * Tests the BoggleEvolution class. Takes as command line arguments:
 * dictionaryPath [sideLength [-]]
 * @author Ankur Dave
 */
public class BogglePopulationTester {
	static String DICT_PATH = "words.txt";
	/**
     * Method called when program is run.
     * @param args arguments to the program
     */
	static int SIDE_LENGTH = 4, START_POP = 20, POP_CAP = 20,
	        AVG_CHILDREN_PER_COUPLE = 5;
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
			}
		} while ((highest.getScore() < 3500)
		        && (System.currentTimeMillis() - startTime < 1000000));
		// stop the timer
		long stopTime = System.currentTimeMillis();
		System.out.println(highest + " " + (stopTime - startTime));
	}
}

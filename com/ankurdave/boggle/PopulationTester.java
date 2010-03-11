package com.ankurdave.boggle;

/**
 * Tests the BoggleEvolution class. Takes as command line arguments:
 * dictionaryPath [sideLength [-]]
 * 
 * @author Ankur Dave
 */
public class PopulationTester {
	static String DICT_PATH = "words.txt";
	static int SIDE_LENGTH = 4, START_POP = 20, POP_CAP = 20,
			AVG_CHILDREN_PER_COUPLE = 5;
	
	public static void main(String[] args) throws GenerationEmptyException {
		// initialize population
		Dictionary dict = new Dictionary();
		dict.buildDictionary(DICT_PATH);
		Population bp;
		
		for (int i = 0; i < 100; i++) {
			bp = new Population(SIDE_LENGTH, START_POP,
					AVG_CHILDREN_PER_COUPLE, POP_CAP, dict);
			
			// start timer
			long startTime = System.currentTimeMillis();
			Board highest = bp.highest();
			// start evolution
			do {
				try {
					bp.evolve();
					System.err.println(bp);
				} catch (GenerationEmptyException e) {
					System.err.println(e);
					break;
				}
				if (highest.compareTo(bp.highest()) < 0) {
					highest = bp.highest();
					System.err.println(highest);
				}
			} while ((highest.getScore() < 3500)
					&& (System.currentTimeMillis() - startTime < 1000000));
			// stop the timer
			long stopTime = System.currentTimeMillis();
			System.out.println(highest + " " + (stopTime - startTime));
		}
	}
}

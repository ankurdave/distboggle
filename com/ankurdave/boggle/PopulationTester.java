package com.ankurdave.boggle;

/**
 * Evolves a Population of Boards and displays the output.
 */
public class PopulationTester {
	public static void main(String[] args) throws GenerationEmptyException {
		// Set variables from args
		String dictPath = "words.txt";
		if (args.length >= 1) {
			dictPath = args[0];
		}
		
		// Initialize
		Dictionary dict = new Dictionary();
		dict.buildDictionary(dictPath);
		Population bp = new Population();
		bp.setDictionary(dict);
		
		// Start the evolution
		try {
			while (true) {
				bp.evolve();
				System.err.println(bp);
			}
		} catch (GenerationEmptyException e) {
			System.err.println(e);
		}
	}
}

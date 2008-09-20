package com.ankurdave.boggle;

public class HillClimber {
	public static void main(String[] args) {
		// create the starting Boggle
		char[][] start = Util.randomGrid(4);
		Dictionary dict = new Dictionary();
		dict.buildDictionary("words.txt");
		Board current = new Board(start, dict);
		current.generate(); // find score of current Boggle
		Board trial;
		System.err.println("#" + current);
		// start the timer
		long startTime = System.currentTimeMillis();
		// begin hill climbing
		int i = 1, lastImproved = 1;
		while ((i - lastImproved) < 20000 && current.getScore() < 3500) {
			trial = current.mutate(10);
			trial.generate();
			if (trial.getScore() > current.getScore()) {
				current = trial;
				lastImproved = i;
			}
			i++;
		}
		// stop the timer
		long stopTime = System.currentTimeMillis();
		System.err.println("#" + current);
		System.err.println("# Time elapsed (ms): " + (stopTime - startTime));
	}
}

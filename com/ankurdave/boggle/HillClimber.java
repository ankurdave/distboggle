package com.ankurdave.boggle;

public class HillClimber {
	public static void main(String[] args) {
		Dictionary dict = new Dictionary();
		dict.buildDictionary("words.txt");
		for (int trialNum = 0; trialNum < 100; trialNum++) {
			// create the starting Boggle
			char[][] start = Util.randomGrid(4);
			MutatingBoard current = new MutatingBoard(start);
			current.setDictionary(dict);
			current.generate();
			MutatingBoard trial;
			// start the timer
			long startTime = System.currentTimeMillis();
			// begin hill climbing
			while (current.getScore() < 3500 && (System.currentTimeMillis() - startTime) < 1000000) {
				trial = current.mutate(10);
				trial.generate();
				if (trial.getScore() > current.getScore()) {
					current = trial;
					System.err.println(current);
				}
			}
			// stop the timer
			long stopTime = System.currentTimeMillis();
			System.out.println(current + " " + (stopTime - startTime));
		}
	}
}

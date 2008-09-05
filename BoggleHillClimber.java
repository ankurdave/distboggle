/**
 * Attempts to find a high-scoring Boggle board by mutating the characters of a
 * Boggle board and checking if the resulting mutant scores higher than the
 * original board.
 * @author ankur
 */
public class BoggleHillClimber {
	public static void main(String[] args) {
		// create the starting Boggle 
		char[][] start = BogglePopulation.randomGrid(4);
		Dictionary dict = new Dictionary();
		dict.buildDictionary("words.txt");
		Boggle current = new Boggle(start, dict);
		
		current.generate(); // find score of current Boggle
		
		Boggle trial;
		System.err.println("#" + current.getScore() + " " + current.gridToString());
		
		// start the timer
		long startTime = System.currentTimeMillis();
		
		// begin hill climbing
		System.out.println("0 " + current.getScore());
		int i = 1, lastImproved = 1;
		while ((i - lastImproved) < 5000 && current.getScore() < 3500) {
			if (i % 1000 == 0) {
				System.err.println("# Mutation attempt " + i);
			}
			trial = current.mutate(10);
			trial.generate();
			if (trial.getScore() > current.getScore()) {
				current = trial;
				lastImproved = i;
				System.out.println(i + " " + current.getScore());
			}
			i++;
		}
		
		// stop the timer
		long stopTime = System.currentTimeMillis();
		
		System.err.println("#" + current.getScore() + " " + current.gridToString());
		System.err.println("# Time elapsed (ms): " + (stopTime - startTime));
	}
}

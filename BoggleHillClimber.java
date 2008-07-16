/**
 * Attempts to find a high-scoring Boggle board by mutating the characters of a
 * Boggle board and checking if the resulting mutant scores higher than the
 * original board.
 * @author ankur
 */
public class BoggleHillClimber {
	public static void main(String[] args) {
		char[][] start = BogglePopulation.randomGrid(4);
		Dictionary dict = new Dictionary();
		dict.buildDictionary("words.txt");
		Boggle best = new Boggle(start, dict);
		best.generate();
		Boggle trial;
		for (int i = 2;; i++) {
			if (i % 1000 == 0) {
				System.out.println("# Mutation attempt " + i);
			}
			trial = best.mutate(50);
			trial.generate();
			// replace best with trial if trial is better
			if (trial.getScore() > best.getScore()) {
				best = trial;
				System.out.println(i + " " + best.getScore());
			}
		}
	}
}

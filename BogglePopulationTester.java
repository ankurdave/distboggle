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
	static int SIDE_LENGTH = 4, START_POP = 20, POP_CAP = 20, AVG_CHILDREN_PER_COUPLE = 2;
	static String DICT_PATH = "words.txt";
	

	public static void main(String[] args) throws GenerationEmptyException {
		// initialize population
		Dictionary dict = new Dictionary();
		dict.buildDictionary(DICT_PATH);
		BogglePopulation bp = new BogglePopulation(SIDE_LENGTH, START_POP, AVG_CHILDREN_PER_COUPLE, POP_CAP, dict);
		System.out.println(bp.getGeneration() + " " + bp.highest().getScore() + " " + bp.averageScore() + bp.lowest().getScore());

		// start timer
		long startTime = System.currentTimeMillis();
		
		// start evolution
		while (bp.highest().getScore() < 3500) {
			try {
				bp.evolve();
			}
			catch (GenerationEmptyException e) {
				System.err.println(e);
				break;
			}
			
/*			System.out.println(bp);			
			ArrayList<Boggle> gen = bp.getCurrentGeneration();
            for (int i = 0; i < gen.size(); i++) {
                    System.err.println(gen.get(i).gridToString() + " " + gen.get(i).getScore());
            }
*/
			System.out.println(bp.getGeneration() + " " + bp.highest().getScore() + " " + bp.averageScore() + " " + bp.lowest().getScore());
		}
		
		// stop the timer
		long stopTime = System.currentTimeMillis();
		
		System.err.println("#" + bp.highest().gridToString() + " " + bp.highest().getScore());
		System.err.println("# Time elapsed (ms): " + (stopTime - startTime));
	}
}

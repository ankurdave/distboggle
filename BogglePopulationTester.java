import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

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
	static int SIDE_LENGTH;

	static Scanner in = new Scanner(System.in);

	static char[][] grid1;

	static char[][] grid2;

	static String gridImage = "";

	static Scanner tempIn;

	public static void main(String[] args) {
		// need at least 1 argument
		if (args.length < 1) {
			System.out
			        .println("Usage: java BoggleTester dictionaryPath [sideLength]");
			System.exit(-1);
		}
		// first argument: path of dictionary file
		String path = args[0];
		// second argument (optional): side length
		if (args.length >= 2) {
			SIDE_LENGTH = Integer.parseInt(args[1]);
		} else {
			System.out.print("Length of a side of the Boggle board: ");
			SIDE_LENGTH = in.nextInt();
		}
		// make a board so we can get its dictionaries
		Dictionary dict = new Dictionary();
		dict.buildDictionary(path);
		// make a BogglePopulation
		BogglePopulation bp = new BogglePopulation(SIDE_LENGTH, 5, 5, 20, dict);
		// set up timer
		Calendar c = Calendar.getInstance();
		int start = c.getTime().getMinutes() * 60 + c.getTime().getSeconds();
		while (true) {
			c = Calendar.getInstance();
			int now = c.getTime().getMinutes() * 60 + c.getTime().getSeconds();
			System.out.println(now - start);
			if (now - start >= 60) {
				break;
			}
			System.out.println(bp);
			try {
				ArrayList<Boggle> gen = bp.getCurrentGeneration();
				for (int i = 0; i < gen.size(); i++) {
					System.err.println(Integer.toString(bp.getGeneration()) + "\t" + i + "\t" + gen.get(i).getScore());
				}
				bp.evolve();
			}
			catch (GenerationEmptyException e) {
				System.err.println(e);
			}
		}
	}
}

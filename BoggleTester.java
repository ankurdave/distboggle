
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Tests the Boggle class. Takes as command line arguments:<BR>
 * <CODE>dictionaryPath [sideLength [gridPath]]</CODE>
 * @author Ankur Dave
 */
public class BoggleTester {
	/**
     * Method called when program is run.
     * @param args
     *            arguments to the program
     */
	static int SIDE_LENGTH;

	static Scanner in = new Scanner(System.in);

	static char[][] grid;

	static String gridImage = "";

	static Scanner tempIn;

	public static void main(String[] args) {
		// need at least 1 argument
		if (args.length < 1) {
			System.out
			        .println("Usage: java BoggleTester dictionaryPath [sideLength [gridPath]]");
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
		// third argument (optional):
		if (args.length >= 3) {
			// can be either a path or a -
			// if it's a -, prompt the user for the board
			// otherwise, read it in from the given path
			if (args[2].equals("-")) {
				tempIn = new Scanner(System.in);
				System.out.println("Enter a " + SIDE_LENGTH + "x" + SIDE_LENGTH
				        + " Boggle board:");
			} else {
				try {
					tempIn = new Scanner(new File(args[2]));
				}
				catch (FileNotFoundException e) {
					System.out.println("File " + path + " not found!");
					System.exit(-1);
				}
			}
			String temp;
			grid = new char[SIDE_LENGTH][SIDE_LENGTH];
			for (int i = 0; i < SIDE_LENGTH; i++) {
				temp = tempIn.nextLine();
				for (int j = 0; j < temp.length(); j++) {
					grid[i][j] = temp.charAt(j);
				}
			}
		} else {
			// make a random grid
			System.out.println("Randomly generated Boggle board:");
			grid = new char[SIDE_LENGTH][SIDE_LENGTH];
			gridImage = "";
			for (int i = 0; i < SIDE_LENGTH; i++) {
				for (int j = 0; j < SIDE_LENGTH; j++) {
					grid[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
					gridImage += grid[i][j] + " ";
				}
				gridImage += "\n";
			}
			// show the user the grid
			System.out.println(gridImage);
		}
		// make the Boggle board from the above information
		Boggle board = new Boggle(grid, path);
		board.generate();
/*		// get the output of the computations
		Object[] words = board.getWordsSorted();
		// print output
		for (Object word : words) {
			System.out.println(word);
		}
*/		// print # of words
		System.out.println(board);
	}
}

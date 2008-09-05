import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates the evolution of a population of Boggles.
 * @author ankur
 */
public class BogglePopulation {
	private static final int AGE_LIMIT = 5;

	/**
     * Creates a character grid filled with random uppercase letters.
     * @param sideLength
     *            length of one side of the random grid
     * @return the random grid
     */
	public static char[][] randomGrid(int sideLength) {
		char[][] temp = new char[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				// rand 65-90
				temp[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
			}
		}
		return temp;
	}

	/**
     * Length of one side of each Boggle board.
     */
	private int sideLength;

	/**
     * How many generations have passed.
     */
	private int generation;

	/**
     * How many children for each Boggle couple.
     */
	private int childrenPerCouple;

	/**
     * Maximum number of Boggles allowed at any time
     */
	private int popCap;

	/**
     * The Boggles in the current generation.
     */
	private ArrayList<Boggle> currentGeneration;

	/**
     * Dictionary to check possible words against.
     */
	private Dictionary dict;

	/**
     * @param sideLength
     *            length of one side of each Boggle board
     * @param startingPopulation
     *            how many Boggle boards the first generation has
     * @param childrenPerCouple
     *            how many children for every Boggle couple
     * @param popCap
     *            maximum number of Boggles at any time
     * @param dict
     *            pre-filled dictionary
     */
	public BogglePopulation(int sideLength, int startingPopulation,
	        int childrenPerCouple, int popCap, Dictionary dict) {
		assert sideLength > 0;
		assert startingPopulation >= 0;
		assert childrenPerCouple >= 0;
		assert popCap >= startingPopulation;
		assert dict != null;
		// copy params to object fields
		this.sideLength = sideLength;
		this.childrenPerCouple = childrenPerCouple;
		this.popCap = popCap;
		this.dict = dict;
		// make the first generation
		generation = 1;
		currentGeneration = new ArrayList<Boggle>();
		Boggle temp;
		for (int i = 0; i < startingPopulation; i++) {
			temp = new Boggle(randomGrid(sideLength), dict);
			temp.generate();
			currentGeneration.add(temp);
		}
	}

	/**
     * Adds a Boggle to the current generation.
     * @param boggle
     *            Boggle to add
     */
	public void add(Boggle boggle) {
		assert boggle != null;
		currentGeneration.add(boggle);
	}

	/**
     * Adds a Boggle to the current generation.
     * @param grid
     *            character grid to add
     */
	public void add(char[][] grid) {
		assert grid.length == sideLength;
		currentGeneration.add(new Boggle(grid, dict));
	}

	/**
     * Finds the average score of the current generation.
     * @return the average score of the current generation
     */
	public int averageScore() throws GenerationEmptyException {
		if (numBoggles() <= 0) {
			throw new GenerationEmptyException(
			        "not enough Boggles in current generation to find average");
		}
		int counter = 0;
		int total = 0;
		for (Boggle b : currentGeneration) {
			counter++;
			total += b.getScore();
		}
		return total / counter;
	}

	/**
     * Replaces the current generation of Boggles with their children.
     */
	public void evolve() throws GenerationEmptyException {
		if (numBoggles() <= 1) {
			throw new GenerationEmptyException(
			        "not enough Boggles in current generation to evolve");
		}
		// sort the current generation by score
		Collections.sort(currentGeneration);

		// make children
		ArrayList<Boggle> children = new ArrayList<Boggle>();
		Boggle parent1;
		Boggle parent2;
		Boggle child;
		// make, on average, childrenPerCouple children per couple
		for (int i = 0; i < childrenPerCouple * currentGeneration.size(); i++) {  		
			parent1 = weightedRandomFromList(currentGeneration);
			do {
				parent2 = weightedRandomFromList(currentGeneration);
			} while (parent1 == parent2);
			child = parent1.merge(parent2);
			children.add(child);
		}
/*		 for (int i = 0; i < this.numBoggles() - 1; i += 2) {
             // get the next two parents
             parent1 = currentGeneration.get(i);
             parent2 = currentGeneration.get(i + 1);
             // mate them childrenPerCouple times
             for (int j = 0; j < childrenPerCouple; j++) {
                     child = parent1.merge(parent2);
                     children.add(child);
             }
     }
*/

		// do elitist selection
		// highest() seems to clone the object or something and so age is not
		// preserved
		Boggle highest = currentGeneration.get(currentGeneration.size() - 1);
		if (highest.getAge() < AGE_LIMIT) {
			highest.incrementAge();
			children.add(highest);
		} else {
			highest.incrementAge();
		}

		// make sure there are no duplicates, then rank the boggles
		ArrayList<String> uniqueGrids = new ArrayList<String>();
		for (int i = 0; i < children.size(); i++) {
			Boggle b = children.get(i);
			if (uniqueGrids.contains(b.gridToString())) {
				children.remove(b);
				continue;
			} else {
				uniqueGrids.add(b.gridToString());
			}
			b.generate();
		}
	
		// reduce number of children to popCap through weighted random selection
		Collections.sort(children);
		while (children.size() > popCap) {
			children.remove(weightedRandomWorstFromList(children));
		}
/*		// make sure number of children <= popCap by removing the worst few
		Collections.sort(children);
		while (children.size() > popCap) {
			children.remove(0);
		}
*/
		// apply changes
		currentGeneration.clear();
		currentGeneration.addAll(children);
		// record generation change
		generation++;
	}
	
	/** Taken from http://www.perlmonks.org/?node_id=158482
	 * How it works: on the first iteration, the if will always be true,
	 * establishing the first Boggle as the random one (unless the first boggle scores 0).
	 * On successive iterations, every other Boggle gets a weighted chance at
	 * replacing the previous Boggle.
	 */
	private Boggle weightedRandomFromList(List<Boggle> list) {
		int sum = 0;
		Boggle result = null;
		for (Boggle b : list) {
			if (Math.random() * (sum += b.getScore()) < b.getScore()) {
				result = b;
			}
		}
		assert result != null;
		return result;
	}
	
	private Boggle weightedRandomWorstFromList(List<Boggle> list) {
		int sum = 0;
		Boggle result = null;
		int maxScore = Collections.max(list).getScore();
		for (Boggle b : list) {
			if (Math.random() * (sum += (maxScore - b.getScore())) < (maxScore - b.getScore())) {
				result = b;
			}
		}
		assert result != null;
		return result;
	}

	/**
     * Accessor method for the current generation of Boggles.
     * @return the current generation of Boggles
     */
	public ArrayList<Boggle> getCurrentGeneration() {
		return currentGeneration;
	}

	public int getGeneration() {
		return generation;
	}

	public int getPopCap() {
		return popCap;
	}

	/**
     * Finds the highest-scoring Boggle in the current generation.
     * @return the highest-scoring Boggle in the current generation
     */
	public Boggle highest() throws GenerationEmptyException {
		if (numBoggles() <= 0) {
			throw new GenerationEmptyException(
			        "not enough Boggles in current generation to find maximum");
		}
		return Collections.max(currentGeneration);
	}

	/**
     * Finds the lowest-scoring Boggle in the current generation.
     * @return the lowest-scoring Boggle in the current generation
     */
	public Boggle lowest() throws GenerationEmptyException {
		if (numBoggles() <= 0) {
			throw new GenerationEmptyException(
			        "not enough Boggles in current generation to find minimum");
		}
		return Collections.min(currentGeneration);
	}

	/**
     * Accessor method for the number of Boggles in the current generation.
     * @return the number of Boggles in the current generation
     */
	public int numBoggles() {
		return currentGeneration.size();
	}

	public Boggle removeHighest() throws GenerationEmptyException {
		if (numBoggles() <= 0) {
			throw new GenerationEmptyException(
			        "not enough Boggles in current generation to find maximum");
		}
		Boggle highest = Collections.max(currentGeneration);
		currentGeneration.remove(highest);
		return highest;
	}

	public void setPopCap(int popCap) {
		this.popCap = popCap;
	}
	
	public Boggle random() {
		return currentGeneration.get((int)(Math.random() * numBoggles()));
	}

	/**
     * Represents this BogglePopulation as a String.
     * @return representation of this BogglePopulation
     */
	@Override
	public String toString() {
		String s = "";
		try {
			s = generation + " " + highest().getScore() + " " + averageScore()
			        + " " + lowest().getScore();
		}
		catch (GenerationEmptyException e) {
			System.err.println(e);
		}
		return s;
	}
}

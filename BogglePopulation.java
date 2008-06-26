import java.util.ArrayList;
import java.util.Collections;

/**
 * Simulates the evolution of a population of Boggles.
 * 
 * @author ankur
 */
public class BogglePopulation
{
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
	 *        length of one side of each Boggle board
	 * @param startingPopulation
	 *        how many Boggle boards the first generation has
	 * @param childrenPerCouple
	 *        how many children for every Boggle couple
	 * @param popCap
	 *        maximum number of Boggles at any time
	 * @param dict
	 *        pre-filled dictionary
	 */
	public BogglePopulation(int sideLength,
							int startingPopulation,
							int childrenPerCouple,
							int popCap,
							Dictionary dict)
	{
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
		for (int i = 0; i < startingPopulation; i++)
		{
			temp = new Boggle(randomGrid(sideLength), dict);
			temp.generate();
			currentGeneration.add(temp);
		}
	}

	/**
	 * Adds a Boggle to the current generation.
	 * 
	 * @param boggle
	 *        Boggle to add
	 */
	public void add(Boggle boggle)
	{
		assert boggle != null;
		currentGeneration.add(boggle);
	}

	/**
	 * Adds a Boggle to the current generation.
	 * 
	 * @param grid
	 *        character grid to add
	 */
	public void add(char[][] grid)
	{
		assert grid.length == sideLength;
		currentGeneration.add(new Boggle(grid, dict));
	}

	/**
	 * Replaces the current generation of Boggles with their children.
	 */
	public void evolve() throws GenerationEmptyException
	{
		if (numBoggles() <= 1)
			throw new GenerationEmptyException(
				"not enough Boggles in current generation to evolve");
		// sort the current generation by score
		Collections.sort(currentGeneration);
		// make children
		ArrayList<Boggle> children = new ArrayList<Boggle>();
		Boggle parent1;
		Boggle parent2;
		Boggle child;
		for (int i = 0; i < this.numBoggles() - 1; i += 2)
		{
			// get the next two parents
			parent1 = currentGeneration.get(i);
			parent2 = currentGeneration.get(i + 1);
			// mate them childrenPerCouple times
			for (int j = 0; j < childrenPerCouple; j++)
			{
				child = parent1.merge(parent2);
				child.generate();
				children.add(child);
			}
		}
		// do elitist selection
//		children.add(highest());
		// make sure number of children <= popCap by removing the worst few
		Collections.sort(children);
		while (children.size() > popCap)
		{
			children.remove(0);
		}
		// apply changes
		currentGeneration.clear();
		currentGeneration.addAll(children);
		// record generation change
		generation++;
	}

	/**
	 * Finds the highest-scoring Boggle in the current generation.
	 * 
	 * @return the highest-scoring Boggle in the current generation
	 */
	public Boggle highest() throws GenerationEmptyException
	{
		if (numBoggles() <= 0)
			throw new GenerationEmptyException(
				"not enough Boggles in current generation to find maximum");
		return Collections.max(currentGeneration);
	}
	
	/**
	 * Finds the lowest-scoring Boggle in the current generation.
	 * 
	 * @return the lowest-scoring Boggle in the current generation
	 */
	public Boggle lowest() throws GenerationEmptyException
	{
		if (numBoggles() <= 0)
			throw new GenerationEmptyException(
				"not enough Boggles in current generation to find minimum");
		return Collections.min(currentGeneration);
	}

	/**
	 * Finds the average score of the current generation.
	 * 
	 * @return the average score of the current generation
	 */
	public int averageScore() throws GenerationEmptyException
	{
		if (numBoggles() <= 0)
			throw new GenerationEmptyException(
				"not enough Boggles in current generation to find average");
		int counter = 0;
		int total = 0;
		for (Boggle b : currentGeneration)
		{
			counter++;
			total += b.getScore();
		}
		return total / counter;
	}

	/**
	 * Accessor method for the number of Boggles in the current generation.
	 * 
	 * @return the number of Boggles in the current generation
	 */
	public int numBoggles()
	{
		return currentGeneration.size();
	}

	/**
	 * Creates a character grid filled with random uppercase letters.
	 * 
	 * @param sideLength
	 *        length of one side of the random grid
	 * @return the random grid
	 */
	public static char[][] randomGrid(int sideLength)
	{
		char[][] temp = new char[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++)
			for (int j = 0; j < sideLength; j++)
				// rand 65-90
				temp[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
		return temp;
	}

	/**
	 * Accessor method for the current generation of Boggles.
	 * 
	 * @return the current generation of Boggles
	 */
	public ArrayList<Boggle> getCurrentGeneration()
	{
		return currentGeneration;
	}

	/**
	 * Represents this BogglePopulation as a String.
	 * 
	 * @return representation of this BogglePopulation
	 */
	public String toString()
	{
		String s = "";
		try
		{
			s = "BogglePopulation[" + "generation=" + generation + "; "
				+ "popCap=" + popCap + "; " + "number of Boggles="
				+ currentGeneration.size() + "; " + "high score="
				+ highest().getScore() + "; " + "avg score=" + averageScore()
				+ "; " + "low score=" + lowest().getScore() + "]";
		}
		catch (GenerationEmptyException e)
		{
			System.err.println(e);
		}
		return s;
	}
}

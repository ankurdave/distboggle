package com.ankurdave.boggle;

/**
 * Board with modifications that allow it to evolve.
 */
public class GeneticBoard extends Board {
	private int age = 1;
	
	public GeneticBoard(char[][] grid) {
		super(grid);
	}
	
	public GeneticBoard(String s) {
		super(s);
	}
	
	/**
	 * Gets the age of this Board within the context of a {@link Population}.
	 */
	public int getAge() {
		return age;
	}
	
	/**
	 * Increments the age of this Board by one. Call each time a generation passes.
	 */
	public void incrementAge() {
		age++;
	}
	
	/**
	 * Merges two Boards randomly. On each character in the grid, chooses randomly between three choices for the child:
	 * 
	 * <UL>
	 * <LI>use the character from the higher-scoring grid (weighted 6.6/10, or 6/10 if incestuous)
	 * <LI>use the character from the lower-scoring grid (weighted 3.3/10, or 3/10 if incestuous)
	 * <LI>use a random character (weighted 0.1/10, or 1/10 if incestuous -- to increase genetic diversity)
	 * </UL>
	 * 
	 * Two boards are incestuous when the number of same letters is equal to or greater than 85%.
	 * 
	 * @param that Boggle board to merge with the calling board
	 * @return the child board, or null if the two boards have different dimensions
	 */
	public GeneticBoard merge(GeneticBoard that) { /* @ \label{Board.java:merge} @ */
		// Make sure the dimensions are the same
		if (this.getWidth() != that.getWidth() || this.getHeight() != that.getHeight()) {
			return null;
		}
		
		// Determine some relationships between the two boards
		// Which is higher-scoring and which is lower-scoring?
		Board higher = (this.getScore() > that.getScore()) ? this : that;
		Board lower = (this.getScore() > that.getScore()) ? that : this;
		
		// Are they too similar (incestuous)? /*@ \label{Board.java:incest} @*/
		// TODO: take rotations/reflections into account
		int sameLetters = 0;
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				if (higher.getCharAt(i, j) == lower.getCharAt(i, j)) {
					sameLetters++;
				}
			}
		}
		boolean incest = (float) sameLetters / (getWidth() * getHeight()) >= 0.85;
		
		// Calculate the weights
		double higherChance = incest ? 6 : 6.6;
		double lowerChance = incest ? 3 : 3.3;
		
		// Do the actual merging into a grid
		char[][] childGrid = new char[getWidth()][getHeight()];
		double rand;
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				rand = Math.random() * 10; // 0-9.9
				
				// With P(higherChance), choose the letter from the higher-scoring board
				// With P(lowerChance), choose from lower-scoring
				// With P(10 - higherChance - lowerChance), choose random
				if (rand >= 0 && rand < higherChance) {
					childGrid[i][j] = higher.getCharAt(i, j);
				} else if (rand >= higherChance && rand < (higherChance + lowerChance)) {
					// 6-9
					childGrid[i][j] = lower.getCharAt(i, j);
				} else {
					// 9.9-10 or 9-10
					childGrid[i][j] = Util.randomLetter();
				}
			}
		}
		
		// Convert it into a board and return it
		GeneticBoard child = new GeneticBoard(childGrid);
		child.setDictionary(getDictionary());
		return child;
	}
}

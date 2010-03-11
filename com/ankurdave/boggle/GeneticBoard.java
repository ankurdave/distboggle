/**
 * 
 */
package com.ankurdave.boggle;

/**
 * Board with modifications that allow it to evolve.
 * 
 */
public class GeneticBoard extends Board {
	private int age = 1;
	
	public GeneticBoard(char[][] grid, Dictionary dict) {
		super(grid, dict);
	}
	
	public GeneticBoard(String s, int sideLength, Dictionary dict) {
		super(s, sideLength, dict);
	}
	
	/**
	 * Gets the age of this Board within the context of a population of Boards.
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
	 * Merges two Boggle boards randomly.<BR>
	 * Calculates the score of each board and on each character in the grid,
	 * chooses randomly between three choices for the child:
	 * <UL>
	 * <LI>use the character from the higher-scoring grid (weighted 6.6/10, or 6/10 if incestuous)
	 * <LI>use the character from the lower-scoring grid (weighted 3.3/10, or 3/10 if incestuous)
	 * <LI>use a random character (weighted 0.1/10, or 1/10 if incestuous)
	 * </UL>
	 * 
	 * @param that Boggle board to merge with the calling board
	 * @return the child board
	 */
	public GeneticBoard merge(GeneticBoard that) { /* @ \label{Board.java:merge} @ */
		if (this.sideLength != that.sideLength) {
			return null;
		}
		// init child
		char[][] childGrid = new char[sideLength][sideLength];
		// determine which one is higher or lower
		Board higher;
		Board lower;
		// caller is higher
		if (this.getScore() > that.getScore()) {
			higher = this;
			lower = that;
		}
		// parameter is higher
		else if (that.getScore() < this.getScore()) {
			higher = that;
			lower = this;
		}
		// they are equal; choose randomly
		else {
			if ((int) (Math.random() * 2) == 0) {
				higher = this;
				lower = that;
			} else {
				higher = that;
				lower = this;
			}
		}
		// check if the parents are too similar /*@ \label{Board.java:incest} @*/
		int sameLetters = 0;
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				if (higher.grid[i][j] == lower.grid[i][j]) {
					sameLetters++;
				}
			}
		}
		// if they are, mark it as incestuous
		boolean incest = (float) sameLetters / (sideLength * sideLength) >= 0.85;
		double higherChance = 6.6, lowerChance = 3.3;
		if (incest) {
			higherChance = 6;
			lowerChance = 3;
		}
		// construct the child grid
		double temp;
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				temp = Math.random() * 10; // 0-9.9
				// higher
				if (temp >= 0 && temp < higherChance) {
					childGrid[i][j] = higher.grid[i][j];
				} else if (temp >= higherChance
						&& temp < (higherChance + lowerChance)) {
					// 6-9
					childGrid[i][j] = lower.grid[i][j];
				} else {
					// 9.9-10 or 9-10
					childGrid[i][j] = randomLetter();
				}
			}
		}
		// make the child board
		Board child = new Board(childGrid, dict);
		return child;
	}
	
}

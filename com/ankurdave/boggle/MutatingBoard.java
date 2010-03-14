package com.ankurdave.boggle;

/**
 * Board with modifications that allow it to mutate.
 */
public class MutatingBoard extends Board {
	
	public MutatingBoard(char[][] grid) {
		super(grid);
	}
	
	public MutatingBoard(String s) {
		super(s);
	}
	
	/**
	 * Mutates (randomizes) each character of a Board with #{@code mutationProbability} percent chance, returning the resulting board. (Leaves the original board untouched.)
	 */
	public MutatingBoard mutate(int mutationProbability) {
		// Make the mutated grid
		char[][] gridMutated = new char[getWidth()][getHeight()];
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				// With P(mutationProbability / 100), set to a random letter
				// With P(1 - mutationProbability / 100), keep the original letter
				if ((int) (Math.random() * 100) < mutationProbability) {
					gridMutated[i][j] = Util.randomLetter();
				} else {
					gridMutated[i][j] = getCharAt(i, j);
				}
			}
		}
		
		// Convert it into a board and return it
		MutatingBoard thisMutated = new MutatingBoard(gridMutated);
		thisMutated.setDictionary(getDictionary());
		return thisMutated;
	}
}

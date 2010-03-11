package com.ankurdave.boggle;

/**
 * Board with modifications that allow it to mutate.
 */
public class MutatingBoard extends Board {
	
	public MutatingBoard(char[][] grid, Dictionary dict) {
		super(grid, dict);
	}
	
	public MutatingBoard(String s, int sideLength, Dictionary dict) {
		super(s, sideLength, dict);
	}
	
	public MutatingBoard mutate(int mutationProbability) {
		assert mutationProbability >= 0 && mutationProbability <= 100;
		char[][] gridMutated = new char[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				if ((int) (Math.random() * 100) < mutationProbability) {
					gridMutated[i][j] = randomLetter();
				} else {
					gridMutated[i][j] = grid[i][j];
				}
			}
		}
		MutatingBoard thisMutated = new MutatingBoard(gridMutated, dict);
		return thisMutated;
	}
}

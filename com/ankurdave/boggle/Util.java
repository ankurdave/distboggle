package com.ankurdave.boggle;

import java.util.ArrayList;

public class Util {
	/**
	 * Creates a character grid filled with random uppercase letters.
	 * 
	 * @param sideLength length of one side of the random grid
	 * @return the random grid
	 */
	public static char[][] randomGrid(int sideLength) {
		char[][] temp = new char[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				temp[i][j] = randomLetter();
			}
		}
		return temp;
	}
	
	/**
	 * Generates a random lowercase letter from a to z.
	 */
	public static char randomLetter() {
		return (char) (Math.random() * (90 - 65 + 1) + 65);
	}
	
	/**
	 * Taken from http://www.perlmonks.org/?node_id=158482 How it works: on the
	 * first iteration, the if will always be true, establishing the first
	 * Boggle as the random one (unless the first boggle scores 0). On
	 * successive iterations, every other Boggle gets a weighted chance at
	 * replacing the previous Boggle.
	 */
	public static <T extends Board> T weightedRandomFromList(ArrayList<T> list) {
		int sum = 0;
		T result = null;
		for (T b : list) {
			if (Math.random() * (sum += b.getScore() * b.getScore()) < b.getScore() * b.getScore()) {
				result = b;
			}
		}
		assert result != null;
		return result;
	}
}

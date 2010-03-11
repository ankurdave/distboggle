package com.ankurdave.boggle;

import java.util.Random;


/**
 * Represents a Boggle board.
 */
public class Board implements Comparable<Board> {
	private Letter[][] board;
	private Dictionary dict;
	private int sideLength;
	private int score;
	private boolean scoreHasBeenCalculated = false;
	
	/**
	 * Creates a Board from a square array of characters.
	 */
	public Board(char[][] grid, Dictionary dict) {
		assert grid.length == grid[0].length;
		assert grid.length > 0;
		assert dict != null;
		
		this.sideLength = grid.length;
		this.dict = dict;
		this.board = makeLettersFromGrid(grid, sideLength);
	}
	
	/**
	 * Creates a Board from a string of the form "ABCDEFGHIJKLMNOP 25".
	 */
	public Board(String s, int sideLength, Dictionary dict) {
		String[] parts = s.split(" ", 2);
		this.board = makeLettersFromString(parts[0], sideLength);
		this.score = Integer.parseInt(parts[1]);
		this.sideLength = sideLength;
		this.dict = dict;
	}
	
	/**
	 * Converts a square array of characters into a square array of Letters.
	 */
	private Letter[][] makeLettersFromGrid(char[][] grid, int sideLength) {
		Letter[][] letters = new Letter[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				letters[i][j] = new Letter(grid[i][j], i, j);
			}
		}
		return letters;
	}
	
	/**
	 * Converts a string like "ABCDEFGHIJKLMNOP" into a square array of Letters.
	 */
	private Letter[][] makeLettersFromString(String s, int sideLength) {
		Letter[][] letters = new Letter[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				letters[i][j] = new Letter(s.charAt(i * sideLength + j), i, j);
			}
		}
		
		return letters;
	}
	
	/**
	 * Compares two Boards based on score.
	 */
	public int compareTo(Board that) {
		this.generate();
		that.generate();
		
		if (this.getScore() > that.getScore()) {
			return 1;
		} else if (this.getScore() < that.getScore()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Calculates the score of the Board based on how many words it contains. Caches the data, avoiding recalculation when possible.
	 * 
	 */
	public void generate() { /* @ \label{Board.java:generate} @ */
		// Don't recalculate
		if (scoreHasBeenCalculated) {
			return;
		}
		
		// Traverse the possible words recursively, starting with each Letter, and accumulate the score
		int score = 0;
		Random rand = new Random();
		int randID = rand.nextInt();
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				score += board[i][j].traverse(dict.getRoot(), randID, 0);
			}
		}
		this.score = score;
	}
	
	/**
	 * Returns the score of a word with a given length according to Boggle rules.
	 */
	public static int scoreWordLength(int length) {
		if (length < 3) {
			return 0;
		} else if (length == 3 || length == 4) {
			return 1;
		} else if (length == 5) {
			return 2;
		} else if (length == 6) {
			return 3;
		} else if (length == 7) {
			return 5;
		} else {
			return 11;
		}
	}
	
	/**
	 * Retrieves the Board's score. Make sure to call generate() first.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Converts the array of Letters in this Board into a string like "ABCDEFGHIJKLMNOP".
	 */
	public String gridToString() {
		StringBuilder gridString = new StringBuilder();
		for (Letter row[] : board) {
			for (Letter letter : row) {
				gridString.append(letter.getData());
			}
		}
		return gridString.toString();
	}
	
	@Override
	public String toString() {
		return gridToString() + " " + getScore();
	}
	
	/**
	 * Generates a random lowercase letter from a to z.
	 */
	public static char randomLetter() {
		return (char) (Math.random() * (90 - 65 + 1) + 65);
	}
	
	protected class Letter {
		private char data;
		private boolean hasBeenHit = false;
		private int X;
		private int Y;
		
		public Letter(char data, int X, int Y) {
			this.data = data;
			this.X = X;
			this.Y = Y;
		}
		
		public char getData() {
			return data;
		}
		
		/**
		 * Finds the score resulting from a recursive traversal of the board, starting with this Letter.
		 * 
		 * @param parentNode the dictionary letter node associated with the parent of this Letter (allows dictionary lookup)
		 * @param traversalID an integer unique to this set of traversal calls (allows trie annotation)
		 * @param wordLengthSoFar the current traversal depth (allows score calculation)
		 */
		public int traverse(TrieNode parentNode, int traversalID, int wordLengthSoFar) {
			// If this Letter has already been used in the current traversal, don't reuse it
			if (hasBeenHit) {
				return 0;
			}
			
			// Traverse the trie in parallel with the board traversal
			TrieNode currentNode = parentNode.getChild(data);
			
			// If, according to the trie, there are no valid words with the current prefix, don't bother traversing
			if (currentNode == null) {
				return 0;
			}
			
			// If the current node ended a word that hasn't already been found, add to the total score
			int totalScore = 0;
			if (currentNode.endsWord() && currentNode.getData() != traversalID) {
				currentNode.setData(traversalID);
				totalScore += Board.scoreWordLength(wordLengthSoFar);
			}
			
			// Recurse, accumulating the total score
			// Make sure we don't traverse back onto this node during recursion
			hasBeenHit = true;
			// Letter above
			if (Y - 1 >= 0 && Y - 1 < sideLength) {
				totalScore += board[X][Y - 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter below
			if (Y + 1 >= 0 && Y + 1 < sideLength) {
				totalScore += board[X][Y + 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter right
			if (X + 1 >= 0 && X + 1 < sideLength) {
				totalScore += board[X + 1][Y].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter left
			if (X - 1 >= 0 && X - 1 < sideLength) {
				totalScore += board[X - 1][Y].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter up-left
			if (X - 1 >= 0 && X - 1 < sideLength && Y - 1 >= 0 && Y - 1 < sideLength) {
				totalScore += board[X - 1][Y - 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter up-right
			if (X + 1 >= 0 && X + 1 < sideLength && Y - 1 >= 0 && Y - 1 < sideLength) {
				totalScore += board[X + 1][Y - 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter down-left
			if (X - 1 >= 0 && X - 1 < sideLength && Y + 1 >= 0 && Y + 1 < sideLength) {
				totalScore += board[X - 1][Y + 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter down-right
			if (X + 1 >= 0 && X + 1 < sideLength && Y + 1 >= 0 && Y + 1 < sideLength) {
				totalScore += board[X + 1][Y + 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Now that the traversal has finished, it's OK for other traversals to use these letters
			hasBeenHit = false;
			
			return totalScore;
		}
	}
}

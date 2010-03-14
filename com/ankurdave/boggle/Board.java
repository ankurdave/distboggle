package com.ankurdave.boggle;

import java.util.Random;


/**
 * Represents a Boggle board.
 */
public class Board implements Comparable<Board> {
	private Letter[][] board;
	private int score;
	private boolean scoreHasBeenCalculated = false;
	private Dictionary dict;
	
	/**
	 * Creates a {@link Board} from a square array of characters.
	 */
	public Board(char[][] grid) {
		this.board = makeLettersFromGrid(grid);
	}
	
	/**
	 * Creates a {@link Board} from a string of the form "ABCDEFGHIJKLMNOP 25". Sets the score, avoiding recalculation.
	 */
	public Board(String s) {
		String[] parts = s.split(" ", 2);
		
		this.board = makeLettersFromString(parts[0]);
		this.score = Integer.parseInt(parts[1]);
		this.scoreHasBeenCalculated = true;
	}
	
	/**
	 * Gets the {@link Dictionary} used by this Board to generate word lists.
	 */
	public Dictionary getDictionary() {
		return dict;
	}
	
	/**
	 * Sets the {@link Dictionary} for use in score calculation when calling {@link Board#generate(boolean)}.
	 */
	public void setDictionary(Dictionary dict) {
		this.dict = dict;
	}
	
	/**
	 * Converts a 2D array of characters into a 2D array of Letters.
	 */
	private Letter[][] makeLettersFromGrid(char[][] grid) {
		int height = grid.length;
		int width = grid[0].length;
		Letter[][] letters = new Letter[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				letters[i][j] = new Letter(grid[i][j], i, j);
			}
		}
		return letters;
	}
	
	/**
	 * Converts a string like "ABCDEFGHIJKLMNOP" into a square array of Letters. Note: only supports square arrays.
	 */
	private Letter[][] makeLettersFromString(String s) {
		int sideLength = (int) Math.sqrt(s.length());
		
		Letter[][] letters = new Letter[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				letters[i][j] = new Letter(s.charAt(i * sideLength + j), i, j);
			}
		}
		
		return letters;
	}
	
	/**
	 * Compares two {@link Board}s based on score.
	 */
	public int compareTo(Board that) {
		if (this.score > that.score) {
			return 1;
		} else if (this.score < that.score) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Calculates the score of the {@link Board} based on how many words it contains. If {@code force} is false, caches the data, avoiding recalculation when possible. If {@link Board#setDictionary(Dictionary)} has not been called and a score has not already been calculated, will throw a {@link NullPointerException}.
	 * 
	 */
	public void generate(boolean force) { /* @ \label{Board.java:generate} @ */
		// Don't recalculate unless force is set
		if (scoreHasBeenCalculated && !force) {
			return;
		}
		
		// Traverse the possible words recursively, starting with each Letter, and accumulate the score
		int score = 0;
		Random rand = new Random();
		int traversalID = rand.nextInt();
		for (Letter[] row : board) {
			for (Letter letter : row) {
				score += letter.traverse(dict.getRoot(), traversalID); // TODO: breaks if traversalID is 0
			}
		}
		this.score = score;
	}
	
	/**
	 * Calculates the score without forcing a recalculation.
	 * 
	 * @see Board#generate(boolean)
	 */
	public void generate() {
		generate(false);
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
	 * Retrieves the Board's score.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Retrieves the character in the board at the given grid coordinates.
	 */
	public char getCharAt(int x, int y) {
		return board[x][y].getData();
	}
	
	/**
	 * Gets the Board's width.
	 */
	public int getWidth() {
		return board.length;
	}
	
	/**
	 * Gets the Board's height.
	 */
	public int getHeight() {
		return board[0].length;
	}
	
	/**
	 * Converts the array of Letters in this {@link Board} into a string like "ABCDEFGHIJKLMNOP".
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
	
	/**
	 * Returns the {@link String} representation of this Board in the form "ABCDEFGHABCDEFGH 22".
	 */
	@Override
	public String toString() {
		return gridToString() + " " + score;
	}
	
	/**
	 * Represents a letter in a Boggle board.
	 */
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
		
		/**
		 * Gets the character associated with this {@link Letter}.
		 */
		public char getData() {
			return data;
		}
		
		/**
		 * Finds the score resulting from a recursive traversal of the board, starting with this {@link Letter}.
		 * 
		 * @param traversalID an integer unique to this set of traversal calls (allows trie annotation)
		 */
		public int traverse(TrieNode rootNode, int traversalID) {
			return traverse(rootNode, traversalID, 1);
		}
		
		/**
		 * Finds the score resulting from a recursive traversal of the board, starting with this {@link Letter}.
		 * 
		 * @param parentNode the dictionary letter node associated with the parent of this {@link Letter} (allows dictionary lookup)
		 * @param traversalID an integer unique to this set of traversal calls (allows trie annotation)
		 * @param wordLengthSoFar the current traversal depth (allows score calculation)
		 */
		private int traverse(TrieNode parentNode, int traversalID, int wordLengthSoFar) {
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
			if (yInRange(Y - 1)) {
				totalScore += board[X][Y - 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter below
			if (yInRange(Y + 1)) {
				totalScore += board[X][Y + 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter right
			if (xInRange(X + 1)) {
				totalScore += board[X + 1][Y].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter left
			if (xInRange(X - 1)) {
				totalScore += board[X - 1][Y].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter up-left
			if (xInRange(X - 1) && yInRange(Y - 1)) {
				totalScore += board[X - 1][Y - 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter up-right
			if (xInRange(X + 1) && yInRange(Y - 1)) {
				totalScore += board[X + 1][Y - 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter down-left
			if (xInRange(X - 1) && yInRange(Y + 1)) {
				totalScore += board[X - 1][Y + 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Letter down-right
			if (xInRange(X + 1) && yInRange(Y + 1)) {
				totalScore += board[X + 1][Y + 1].traverse(currentNode, traversalID, wordLengthSoFar + 1);
			}
			// Now that the traversal has finished, it's OK for other traversals to use these letters
			hasBeenHit = false;
			
			return totalScore;
		}
		
		/**
		 * Checks whether or not the given x coordinate is within the bounds of the board.
		 */
		private boolean xInRange(int x) {
			return x > 0 && x < getWidth();
		}
		
		/**
		 * Checks whether or not the given y coordinate is within the bounds of the board.
		 */
		private boolean yInRange(int y) {
			return y > 0 && y < getHeight();
		}
	}
}

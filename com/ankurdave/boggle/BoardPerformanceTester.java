package com.ankurdave.boggle;

/**
 * Tests the performance of Board and Dictionary by timing the scoring of many Boards.
 */
public class BoardPerformanceTester {
	public static void main(String[] args) {
		// Set variables from args
		int numBoards = 10000;
		if (args.length >= 1) {
			numBoards = Integer.parseInt(args[0]);
		}
		
		int sideLength = 4;
		if (args.length >= 2) {
			sideLength = Integer.parseInt(args[1]);
		}
		
		String dictPath = "words.txt";
		if (args.length >= 3) {
			dictPath = args[2];
		}
		
		char[][] grid = new char[sideLength][sideLength];
		Board board;
		Dictionary dict = new Dictionary();
		dict.buildDictionary(dictPath);
		
		long startTime = System.nanoTime();
		for (int iter = 0; iter < numBoards; iter++) {
			for (int i = 0; i < sideLength; i++) {
				for (int j = 0; j < sideLength; j++) {
					grid[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
				}
			}
			board = new Board(grid);
			board.setDictionary(dict);
			
			board.generate();
			
			System.out.println(board);
		}
		long time = System.nanoTime() - startTime;
		double boardsPerSec = (double) numBoards / (time * 1e-9);
		System.err.println(numBoards + " in " + time + " ns. " + boardsPerSec + " boards/sec.");
	}
}
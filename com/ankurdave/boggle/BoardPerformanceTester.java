package com.ankurdave.boggle;

/**
 * Tests the performance of Board and Dictionary by timing the scoring of many Boards.
 */
public class BoardPerformanceTester {
	private static final int SIDE_LENGTH = 4;
	private static final int NUM_BOARDS = 10000;
	
	public static void main(String[] args) {
		char[][] grid = new char[SIDE_LENGTH][SIDE_LENGTH];
		Board board;
		Dictionary dict = new Dictionary();
		dict.buildDictionary("/home/ankur/notes/twl.txt");
		
		long startTime = System.nanoTime();
		for (int iter = 0; iter < NUM_BOARDS; iter++) {
			for (int i = 0; i < SIDE_LENGTH; i++) {
				for (int j = 0; j < SIDE_LENGTH; j++) {
					grid[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
				}
			}
			board = new Board(grid, dict);
			
			board.generate();
			
			System.out.println(board);
		}
		long time = System.nanoTime() - startTime;
		double boardsPerSec = (double) NUM_BOARDS / (time * 1e-9);
		System.out.println(NUM_BOARDS + " in " + time + " ns. " + boardsPerSec + " boards/sec.");
	}
}
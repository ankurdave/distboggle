package com.ankurdave.boggle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BoardTester {
	static char[][] grid;
	static String gridImage = "";
	static Scanner in = new Scanner(System.in);
	static Scanner tempIn;
	
	public static void main(String[] args) {
		// Set variables from args
		String gridPath = "";
		if (args.length >= 1) {
			gridPath = args[0];
		}
		
		int sideLength = 4;
		if (args.length >= 2) {
			sideLength = Integer.parseInt(args[1]);
		}
		
		String dictionaryPath = "words.txt";
		if (args.length >= 3) {
			dictionaryPath = args[2];
		}
		
		// If gridPath is -, prompt the user for the board.
		// If it's something else, read from that file.
		// If it's empty, make a random board.
		if (!gridPath.isEmpty()) {
			if (gridPath.equals("-")) {
				tempIn = new Scanner(System.in);
				System.out.println("Enter a " + sideLength + "x" + sideLength
						+ " Boggle board:");
			} else {
				try {
					tempIn = new Scanner(new File(args[2]));
				} catch (FileNotFoundException e) {
					System.out.println("File " + dictionaryPath + " not found!");
					System.exit(-1);
				}
			}
			String temp;
			grid = new char[sideLength][sideLength];
			for (int i = 0; i < sideLength; i++) {
				temp = tempIn.nextLine();
				for (int j = 0; j < temp.length(); j++) {
					grid[i][j] = temp.charAt(j);
				}
			}
		} else {
			System.out.println("Randomly generated Boggle board:");
			grid = new char[sideLength][sideLength];
			gridImage = "";
			for (int i = 0; i < sideLength; i++) {
				for (int j = 0; j < sideLength; j++) {
					grid[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
					gridImage += grid[i][j] + " ";
				}
				gridImage += "\n";
			}
			// show the user the grid
			System.out.println(gridImage);
		}
		
		// Make the Boggle board from the above information
		Dictionary dict = new Dictionary();
		dict.buildDictionary(dictionaryPath);
		Board board = new Board(grid);
		board.setDictionary(dict);
		board.generate();
		
		// TODO: print out the list of found words
		
		System.out.println(board);
	}
}

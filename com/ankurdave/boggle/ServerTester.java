package com.ankurdave.boggle;

import java.io.IOException;

public class ServerTester {
	public static void main(String[] args) {
		// Set variables from args
		int serverPort = 4444;
		if (args.length >= 1) {
			serverPort = Integer.parseInt(args[0]);
		}
		
		String dictPath = "words.txt";
		if (args.length >= 2) {
			dictPath = args[1];
		}
		
		// Build the dictionary
		Dictionary dict = new Dictionary();
		dict.buildDictionary(dictPath);
		
		// Set up the server
		Server s = new Server();
		s.setMaxHighestBoards(10);
		
		// Start listening for clients
		try {
			s.listen(serverPort);
		} catch (IOException e) {
			System.err.println("Error while listening: " + e);
			System.exit(1);
		}
	}
}

package com.ankurdave.boggle;

import java.io.IOException;

public class GeneticClientTester {
	public static void main(String[] args) {
		// Set variables from args
		String serverIP = "192.168.1.123";
		if (args.length >= 1) {
			serverIP = args[0];
		}
		
		int serverPort = 4444;
		if (args.length >= 2) {
			serverPort = Integer.parseInt(args[1]);
		}
		
		String dictPath = "words.txt";
		if (args.length >= 3) {
			dictPath = args[2];
		}
		
		// Build the dictionary
		Dictionary dict = new Dictionary();
		dict.buildDictionary(dictPath);
		
		// Set up and run the GeneticClient
		GeneticClient gc = new GeneticClient();
		gc.setDictionary(dict);
		
		// Keep trying to connect to the server
		while (true) {
			try {
				gc.connect(serverIP, serverPort);
				break; // If successful
			} catch (IOException e) {
				System.err.println("Connection to server failed: " + e);
				try {
					Thread.sleep(1000); // If unsuccessful, wait a second to avoid hogging resources
				} catch (InterruptedException e1) {}
			}
		}
		
		gc.run();
	}
}

package com.ankurdave.boggle;

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
		
		new GeneticClient(serverIP, serverPort, dictPath, 4, 20, 5, 20)
				.run();
	}
}

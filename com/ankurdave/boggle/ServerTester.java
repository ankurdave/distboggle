package com.ankurdave.boggle;

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
		
		new Server(serverPort, dictPath).listen();
	}
}

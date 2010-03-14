package com.ankurdave.boggle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class HillClimbClient {
	static int clientID;
	
	public static void main(String[] args) throws IOException {
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
		
		Dictionary dict = new Dictionary();
		dict.buildDictionary(dictPath);
		// connect to the server
		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			echoSocket = new Socket(serverIP, serverPort);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket
					.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Couldn't connect: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't connect: " + e.getMessage());
			System.exit(1);
		}
		String inputLine, outputLine;
		// get client id
		inputLine = in.readLine();
		clientID = Integer.parseInt(inputLine);
		// get boggle from server
		out.println(""); // tell server we're ready for a migrant
		inputLine = in.readLine();
		MutatingBoard best = new MutatingBoard(inputLine);
		best.setDictionary(dict);
		best.generate();
		System.err.println("Start boggle: " + inputLine);
		MutatingBoard trial;
		for (int i = 0;; i++) {
			if (i % 1000 == 0) {
				System.err.println("Mutation attempt " + i);
			}
			// mutate the current boggle
			trial = best.mutate(75);
			trial.generate();
			// replace best with trial if trial is better
			if (trial.getScore() > best.getScore() || i >= 20000) {
				best = trial;
				System.out.println(i + " " + best.getScore());
				// send improved boggle to the server
				outputLine = clientID + " " + best;
				out.println(outputLine); // tells server we've finished a
				// "generation" and sends migrant
				System.err.println("Sent boggle: " + outputLine);
				// get a migrant from the server, replacing the current one with
				// the new one
				inputLine = in.readLine();
				if (inputLine == null) {
					System.err.println("Server closed connection.");
					break;
				}
				if (!inputLine.isEmpty()) { // if the server has no migrants,
					// keep mutating the current one
					best = new MutatingBoard(inputLine);
					best.setDictionary(dict);
					best.generate();
					System.err.println("Got boggle: " + inputLine);
				}
				i = 0; // reset the counter so we can work on the next one
			}
		}
		out.close();
		in.close();
		echoSocket.close();
	}
}

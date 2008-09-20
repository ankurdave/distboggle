package com.ankurdave.boggle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GeneticClient {
	private static final Pattern pair = Pattern
	        .compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");
	private Population bp;
	private Dictionary dict;
	private Board highest;
	private BufferedReader in;
	private PrintWriter out;
	private String serverAddress;
	private int serverPort;
	private int sideLength;
	private int startingPopulation, startingChildrenPerCouple, startingPopCap;
	public GeneticClient(String serverAddress, int serverPort, String dictPath,
	        int sideLength, int startingPopulation, int childrenPerCouple,
	        int popCap) {
		this.sideLength = sideLength;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.startingPopulation = startingPopulation;
		this.startingChildrenPerCouple = childrenPerCouple;
		this.startingPopCap = popCap;
		// init dictionary
		dict = new Dictionary();
		dict.buildDictionary(dictPath);
		// init population
		bp = new Population(sideLength, this.startingPopulation,
		        startingChildrenPerCouple, startingPopCap, dict);
	}
	public void connect() {
		// connect to server
		try {
			Socket socket = new Socket(serverAddress, serverPort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
			        .getInputStream()));
		}
		catch (IOException e) {
			System.err.println("Couldn't connect to server: " + e);
			System.exit(1);
		}
		while (true) {
			// complete a generation
			try {
				bp.evolve();
				System.out.println(bp);
				for (Board b : bp.getCurrentGeneration()) {
					System.out.println(b);
				}
				System.out.println();
				if (highest == null
				        || bp.highest().getScore() > highest.getScore()) {
					highest = bp.highest();
				}
				giveServerOutput();
				readServerInput();
			}
			catch (GenerationEmptyException e) {
				break;
			}
			catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			}
		}
	}
	private void giveServerOutput() throws GenerationEmptyException {
		out.println("Highest:" + highest);
		// send a migrant to the server
		// TODO analyze migration frequency
		Board migrant;
		if (Math.random() < 0.25) {
			migrant = bp.highest();
		} else {
			migrant = bp.random();
		}
		out.println("Migrant:" + migrant);
		// end of transmission
		out.println();
		out.flush();
	}
	private void readServerInput() throws GenerationEmptyException, IOException {
		String line;
		Matcher m;
		// for each line in the input
		while (!(line = in.readLine()).isEmpty()) {
			if (line == null) { throw new IOException(
			        "Server closed connection"); }
			// try to find data in it
			m = pair.matcher(line);
			if (m.matches()) {
				storeServerData(m.group(1), m.group(2));
				if (m.group(1).equalsIgnoreCase("reset")) {
					// throw away the rest of the message
					do {
						line = in.readLine();
					} while (!line.isEmpty());
				}
			}
		}
	}
	private void storeServerData(String name, String value) {
		if (name.equalsIgnoreCase("migrant")) {
			Board migrant = new Board(value, sideLength, dict);
			bp.add(migrant);
		} else if (name.equalsIgnoreCase("pop-cap")) {
			bp.setPopCap(Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("reset")) {
			bp = new Population(sideLength, this.startingPopulation,
			        startingChildrenPerCouple, startingPopCap, dict);
			highest = null;
		}
	}
}

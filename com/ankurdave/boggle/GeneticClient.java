package com.ankurdave.boggle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneticClient {
	private BufferedReader in;
	private PrintWriter out;
	private String serverAddress;
	private int serverPort;
	private static final Pattern pair = Pattern
			.compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");
	private GeneticClientThread worker;
	private GeneticBoard highest;
	private GeneticBoard outboundMigrant;
	private Boolean highestChanged = true, migrantChanged = true;
	private Socket socket;
	
	public GeneticClient(String serverAddress, int serverPort, String dictPath,
			int sideLength, int startingPopulation, int childrenPerCouple,
			int popCap) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		worker = new GeneticClientThread(dictPath, sideLength,
				startingPopulation, childrenPerCouple, popCap, this);
		connect();
	}
	
	public void connect() {
		while (true) {
			try {
				socket = new Socket(serverAddress, serverPort);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket
						.getInputStream()));
			} catch (IOException e) {
				System.err.println("Couldn't connect to server: " + e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {
					break;
				}
				continue; // retry if failure
			}
			break; // terminate if success
		}
	}
	
	public void run() {
		worker.start();
		try {
			while (true) {
				// communicate with server
				if (highestChanged || migrantChanged) {
					giveServerOutput();
				}
				readServerInput();
			}
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			worker.terminate();
			out.close();
			try {
				in.close();
			} catch (IOException e) {}
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}
	
	public void setHighest(GeneticBoard b) {
		if (highest == null || b.getScore() > highest.getScore()) {
			highest = b;
		}
		highestChanged = true;
	}
	
	public void setOutboundMigrant(GeneticBoard b) {}
	
	// TODO send server the score
	private void giveServerOutput() {
		if (highestChanged && highest != null) {
			highestChanged = false;
			out.println("Highest:" + highest);
		}
		if (migrantChanged && outboundMigrant != null) {
			migrantChanged = false;
			out.println("Migrant:" + outboundMigrant);
		}
		// end of transmission
		out.println();
		out.flush();
	}
	
	private void readServerInput() throws IOException {
		String line;
		Matcher m;
		while (true) {
			// for each line in the input
			line = in.readLine();
			if (line == null) {
				throw new IOException("Server closed connection");
			} else if (line.isEmpty()) {
				break;
			}
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
			GeneticBoard migrant = new GeneticBoard(value, worker.getSideLength(), worker
					.getDictionary());
			worker.setInboundMigrant(migrant);
		} else if (name.equalsIgnoreCase("pop-cap")) {
			worker.setPopCap(Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("reset")) {
			highest = null;
			worker.reset();
		}
	}
}

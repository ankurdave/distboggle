package com.ankurdave.boggle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses a GeneticClientThread to do optimization using genetic algorithms, and communicates the results with Server.
 */
public class GeneticClient {
	private static final Pattern pair = Pattern.compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");
	private PrintWriter out;
	private GeneticClientThread worker;
	private Socket socket;
	
	public GeneticClient() {
		worker = new GeneticClientThread(this);
	}
	
	/**
	 * Attempts to open the connection to the server.
	 */
	public void connect(String serverAddress, int serverPort) throws IOException {
		socket = new Socket(serverAddress, serverPort);
		out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	/**
	 * Begins the computation in a worker thread, communicating with the server as necessary. Make sure to call {@link GeneticClient#connect(String, int)} first.
	 */
	public void run() {
		worker.start();
		
		BufferedReader in = null;
		try {
			// Initialize the IO facilities for the socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Continuously read the server's input
			while (true) {
				readServerInputLine(in);
			}
		} catch (IOException e) {
			System.err.println("Error while reading from server: " + e);
		} finally {
			// Kill the worker, since there's no point in doing any more calculations
			worker.interrupt();
			
			// Close all connections to the server
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				socket.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * Adds a migrating Board to be sent to another client via the server.
	 */
	public void migrate(Board migrant) {
		sendFieldToServer("Migrant", migrant);
	}
	
	public void sendPotentialHighest(Board potentialHighest) {
		sendFieldToServer("PotentialHighest", potentialHighest);
	}
	
	/**
	 * Sets the {@link Dictionary} for the worker thread to use. Must be set before calling {@link GeneticClient#run()}, otherwise a {@link NullPointerException} will occur.
	 */
	public void setDictionary(Dictionary dict) {
		worker.setDictionary(dict);
	}
	
	/**
	 * Sets the side length for the worker thread to use. If not called before calling {@link GeneticClient#run()}, the default value will be used. Cannot be called after this, otherwise a {@link BoardDimensionMismatchException} may occur.
	 */
	public void setSideLength(int sideLength) {
		worker.setSideLength(sideLength);
	}
	
	/**
	 * Sets the number of {@link Board}s in the starting population for the worker thread to use. Will have no effect unless called before calling {@link GeneticClientThread#run()}.
	 */
	public void setStartingPopulation(int startingPopulation) {
		worker.setStartingPopulation(startingPopulation);
	}
	
	/**
	 * Sets the number of children per couple for the worker thread to use. If not called before calling {@link GeneticClient#run()}, the default value will be used. Can be set at any time.
	 */
	public void setChildrenPerCouple(int childrenPerCouple) {
		worker.setChildrenPerCouple(childrenPerCouple);
	}
	
	/**
	 * Sets the population cap for the worker thread to use. If not called before calling {@link GeneticClient#run()}, the default value will be used. Can be set at any time.
	 */
	public void setPopCap(int popCap) {
		worker.setPopCap(popCap);
	}
	
	/**
	 * Sends the given field to the server, with the value returned by value.toString(). The field name or value must not contain any newlines.
	 */
	private synchronized void sendFieldToServer(String fieldName, Object value) {
		// TODO: check for newlines and throw an exception
		out.println(fieldName + ":" + value);
		out.flush();
	}
	
	/**
	 * Processes the given field sent by the server and performs the appropriate action.
	 */
	private void processServerData(String name, String value) {
		if (name.equalsIgnoreCase("immigrant")) {
			GeneticBoard immigrant = new GeneticBoard(value);
			worker.addBoard(immigrant);
		}
	}
	
	/**
	 * Reads and acts on the next line of data that the server has sent, using the given Reader. Blocks until a transmission is received.
	 */
	private void readServerInputLine(BufferedReader in) throws IOException {
		// Read the line
		String line = in.readLine();
		if (line == null) {
			throw new IOException("Server closed connection");
		}
		
		// Act upon the data
		Matcher m = pair.matcher(line);
		if (m.matches()) {
			processServerData(m.group(1), m.group(2));
		}
	}
}

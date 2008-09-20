package com.ankurdave.boggle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
/**
 * Server component of DistBoggle. Manages BoggleClients.
 * @author ankur
 */
public class BoggleServer {
	private static final int DEFAULT_POP_CAP = 20;
	private static final int POP_CAP_RANGE = 0;
	private int curClientID = 0;
	private Dictionary dict;
	private Boggle highest;
	private ServerSocket socket;
	private long startTime;
	private ArrayList<BoggleServerThread> threads = new ArrayList<BoggleServerThread>();
	public BoggleServer(int port) {
		// create the socket
		socket = null;
		try {
			socket = new ServerSocket(port);
		}
		catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(1);
		}
		// create the dictionary
		dict = new Dictionary();
		dict.buildDictionary("words.txt");
	}
	public synchronized void addMigrant(Boggle migrant,
	        BoggleServerThread caller) {
		Collections.sort(threads);
		for (BoggleServerThread c : threads) {
			if ((c.getMigrant() == null || c.getMigrant().getScore() < migrant
			        .getScore())
			        && caller != c) {
				c.setMigrant(migrant);
				break;
			}
		}
	}
	public Dictionary getDictionary() {
		return dict;
	}
	public synchronized int getPopCapForClient(int clientID) {
		Collections.sort(threads);
		if (threads.size() == 1) { return DEFAULT_POP_CAP; }
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).getId() == clientID) { return (DEFAULT_POP_CAP + POP_CAP_RANGE / 2)
			        - i * (POP_CAP_RANGE / (threads.size() - 1)); }
		}
		return DEFAULT_POP_CAP;
	}
	/**
     * Starts listening for clients. Starts a new thread for each client. Never
     * returns.
     */
	public void listen() {
		try {
			while (true) {
				Socket s = socket.accept();
				BoggleServerThread serverThread = new BoggleServerThread(this,
				        s, curClientID++);
				threads.add(serverThread);
				serverThread.start();
				// start the timer
				if (startTime == 0) { // if not already started
					startTime = System.currentTimeMillis();
				}
			}
		}
		catch (IOException e) {
			System.err.println("Error while listening: " + e);
			System.exit(1);
		}
	}
	public synchronized void setHighest(Boggle b) {
		if (highest == null || b.getScore() > highest.getScore()) {
			highest = b;
			if (highest.getScore() >= 3500) {
				reset();
			}
		}
	}
	private void reset() {
		System.out.println(highest + " "
		        + Long.toString(System.currentTimeMillis() - startTime));
		// reset state
		highest = null;
		for (BoggleServerThread t : threads) {
			t.reset();
		}
		startTime = System.currentTimeMillis(); // restart timer
	}
}

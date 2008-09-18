package com.ankurdave.distboggle;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Server component of DistBoggle. Manages BoggleClients.
 * @author ankur
 */
public class BoggleServer {
	/**
     * Client ID counter.
     */
	private int curClientID = 0;
	
	/**
     * List associating client IDs with each client's average population scores.
     */
	private ArrayList<BoggleServerThread> threads = new ArrayList<BoggleServerThread>();
	
	/**
     * Network socket that the server uses to communicate.
     */
	private ServerSocket socket;
	
	private static final int DEFAULT_POP_CAP = 20;
	private static final int POP_CAP_RANGE = 0;
	
	private Boggle highest;
	
	private Dictionary dict;
	
	private long startTime;
	
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
	
	public Dictionary getDictionary() {
		return dict;
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
		System.out.println(highest + " " + Long.toString(System.currentTimeMillis() - startTime));
		
		// reset state
		highest = null;
		
		for (BoggleServerThread t : threads) {
			t.reset();
		}
		
		startTime = System.currentTimeMillis(); // restart timer		
	}
	
	public synchronized void addMigrant(Boggle migrant, BoggleServerThread caller) {
		Collections.sort(threads);
		for (BoggleServerThread c : threads) {
			if ((c.getMigrant() == null || c.getMigrant().getScore() < migrant.getScore()) && caller != c) {
				c.setMigrant(migrant);
				break;
			}
		}
	}
	
	public synchronized int getPopCapForClient(int clientID) {
		Collections.sort(threads);
		
		if (threads.size() == 1) {
			return DEFAULT_POP_CAP;
		}
		
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).getId() == clientID) {
				return (DEFAULT_POP_CAP + POP_CAP_RANGE / 2) - i
				        * (POP_CAP_RANGE / (threads.size() - 1));
			}
		}
		
		return DEFAULT_POP_CAP;
	}
	
}

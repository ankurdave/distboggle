package com.ankurdave.boggle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Creates and coordinates ServerThreads, which each communicate with one client over the network. Provides highest-board-list and migration functionality.
 */
public class Server {
	// TODO: add a facility to store or print the highest boards
	
	private int curClientID = 0;
	private ArrayList<Board> highestBoards = new ArrayList<Board>();
	private int maxHighestBoards = 10;
	private ArrayList<ServerThread> threads = new ArrayList<ServerThread>();
	
	public Server() {}
	
	/**
	 * Starts listening for clients on the given port, starting a new thread for each client. Never returns.
	 */
	public void listen(int port) throws IOException {
		ServerSocket socket = new ServerSocket(port);
		
		while (true) {
			// Wait for a connection to be made
			Socket s = socket.accept();
			
			// Start up a new ServerThread on that connection
			ServerThread serverThread = new ServerThread(s, this, curClientID++);
			threads.add(serverThread);
			serverThread.start();
		}
	}

	/**
	 * Sends a migrant Board to a random client.
	 */
	public synchronized void migrate(Board migrant) {
		ServerThread randomThread = threads.get((int) (Math.random() * threads.size()));
		randomThread.sendImmigrant(migrant);
	}
	
	/**
	 * Considers the given Board as a candidate for the high score list.
	 */
	public synchronized void considerHighest(Board b) {
		// If there are no highest Boards yet, just insert it
		if (highestBoards.size() == 0) {
			highestBoards.add(b);
		}
		
		// Otherwise, use linear insertion and truncate the list
		for (int i = 0; i < highestBoards.size(); i++) {
			if (b.compareTo(highestBoards.get(i)) > 0) {
				highestBoards.add(i, b);
				break;
			}
		}
		while (highestBoards.size() > maxHighestBoards) {
			highestBoards.remove(0); // The list is stored in ascending order, so remove the worst Board
		}
	}
	
	/**
	 * Removes the given thread from the list of threads. Called by the threads themselves when their connection ends.
	 */
	public synchronized void removeThread(ServerThread t) {
		threads.remove(t);
	}

	/**
	 * Sets the maximum number of high-scoring Boards to store. The default is 10.
	 */
	public void setMaxHighestBoards(int maxHighestBoards) {
		this.maxHighestBoards = maxHighestBoards;
	}
}

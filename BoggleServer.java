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
	private ArrayList<Client> clients = new ArrayList<Client>();

	/**
     * Network socket that the server uses to communicate.
     */
	private ServerSocket socket;

	// stats info for gnuplot
	private int n = 0;

	private static final int DEFAULT_POP_CAP = 20;
	private static final int POP_CAP_RANGE = 20;

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
	}

	/**
     * Starts listening for clients. Starts a new thread for each client. Never
     * returns.
     */
	public void listen() {
		try {
			while (true) {
				new BoggleServerThread(this, socket.accept()).start();
			}
		}
		catch (IOException e) {
			System.err.println("Error while listening: " + e);
			System.exit(1);
		}
	}

	/**
     * @return next available client ID
     */
	public synchronized int getNextClientID() {
		// add entry to client table
		clients.add(new Client(curClientID));

		return curClientID++;
	}

	public synchronized void setClientScore(int clientID, int score) {
		for (Client c : clients) {
			if (c.getId() == clientID) {
				c.setScore(score);
			}
		}

		// stats info for gnuplot
		Collections.sort(clients);
		System.out.println(n++ + " " + clients.get(0).getScore());
	}

	public synchronized void addMigrant(String migrantStr, int clientID) {
		Migrant m = new Migrant(migrantStr);
		Client source = getClient(clientID);
		if (source == null) {
			return;
		}
		Collections.sort(clients);
		for (Client c : clients) {
			if (c.getId() != clientID
			        && (c.getMigrant() == null || c.getMigrant().getScore() < m
			                .getScore()) && c.getScore() > source.getScore()) {
				c.setMigrant(m);
				break;
			}
		}
	}

	public synchronized String getMigrantForClient(int clientID) {
		String migrant = null;
		Client c = getClient(clientID);
		if (c == null) {
			return null;
		}
		if (c.getMigrant() != null) {
			migrant = c.getMigrant().toString();
			c.setMigrant(null);
		}
		return migrant;
	}

	public synchronized int getPopCapForClient(int clientID) {
		Collections.sort(clients);

		if (clients.size() == 1) {
			return DEFAULT_POP_CAP;
		}

		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getId() == clientID) {
				return (DEFAULT_POP_CAP + POP_CAP_RANGE / 2) - i
				        * (POP_CAP_RANGE / (clients.size() - 1));
			}
		}

		return DEFAULT_POP_CAP;
	}

	private Client getClient(int clientID) {
		for (Client c : clients) {
			if (c.getId() == clientID) {
				return c;
			}
		}
		return null;
	}
}

class Client implements Comparable<Client> {
	private int id;
	private int score;
	private Migrant migrant;

	public Client(int id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Migrant getMigrant() {
		return migrant;
	}

	public void setMigrant(Migrant migrant) {
		this.migrant = migrant;
	}

	public int getId() {
		return id;
	}

	public int compareTo(Client that) {
		return that.getScore() - this.getScore(); // descending order
	}
}

class Migrant {
	private String grid;
	private int score;
	public Migrant(String grid, int score) {
		this.grid = grid;
		this.score = score;
	}
	public Migrant(String str) {
		String[] parts = str.split(" ", 2);
		this.grid = parts[0];
		this.score = Integer.parseInt(parts[1]);
	}
	public int getScore() {
		return score;
	}
	@Override
	public String toString() {
		return grid + " " + score;
	}
}

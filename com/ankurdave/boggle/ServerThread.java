package com.ankurdave.boggle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Thread started by BoggleServer to handle BoggleClients.
 * @author ankur
 */
public class ServerThread extends Thread implements Comparable<ServerThread> {
	private static final Pattern pair = Pattern
	        .compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");
	private int clientID;
	private BufferedReader in;
	private Board migrant;
	private PrintWriter out;
	private int score;
	private Server server;
	/**
     * The socket used to communicate with the client.
     */
	private Socket socket;
	public ServerThread(Server server, Socket socket, int clientID) {
		super("BoggleServerThread");
		this.server = server;
		this.socket = socket;
		this.clientID = clientID;
	}
	public int compareTo(ServerThread that) {
		return that.getScore() - this.getScore(); // descending order by
		// default
	}
	public Board getMigrant() {
		return migrant;
	}
	public int getScore() {
		return score;
	}
	public void reset() {
		out.println("Reset: yes");
		// end the transmission
		out.println();
		out.flush();
		score = 0;
		migrant = null;
	}
	@Override public void run() {
		try {
			// init the IO facilities for the socket
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
			        .getInputStream()));
			while (true) {
				readClientInput();
				giveClientOutput();
			}
		}
		catch (IOException e) {
			System.err.println(e);
		}
		finally {
			server.removeThread(this);
			out.close();
			try {
				in.close();
			}
			catch (IOException e) {}
			try {
				socket.close();
			}
			catch (IOException e) {}
		}
	}
	public void setMigrant(Board migrant) {
		this.migrant = migrant;
	}
	private void giveClientOutput() {
		// give the migrant if there is one
		if (migrant != null) {
			out.println("Migrant: " + migrant);
			migrant = null;
		}
		// give the new pop cap
		out.println("Pop-Cap: " + server.getPopCapForClient(clientID));
		// end the transmission
		out.println();
		out.flush();
	}
	private void readClientInput() throws IOException {
		String line;
		Matcher m;
		while (true) {
			// for each line in the input
			line = in.readLine();
			if (line == null) {
				throw new IOException("Client closed connection");
			} else if (line.isEmpty()) {
				break;
			}
			// try to find data in it
			m = pair.matcher(line);
			if (m.matches()) {
				storeClientData(m.group(1), m.group(2));
			}
		}
	}
	private void storeClientData(String name, String value) {
		if (name.equalsIgnoreCase("score")) {
			score = Integer.parseInt(value);
		} else if (name.equalsIgnoreCase("migrant")) {
			Board migrant = new Board(value, 4, server.getDictionary());
			server.addMigrant(migrant, this);
		} else if (name.equalsIgnoreCase("highest")) {
			Board highest = new Board(value, 4, server.getDictionary());
			server.setHighest(highest);
		}
	}
}

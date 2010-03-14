package com.ankurdave.boggle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Communicates with a single client. Performs inter-client communications by routing data through Server.
 */
public class ServerThread extends Thread {
	private static final Pattern pair = Pattern.compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");
	private int clientID;
	private Server server;
	private Socket socket;
	private PrintWriter out;
	
	public ServerThread(Socket socket, Server server, int clientID) throws IOException {
		super("BoggleServerThread");
		this.server = server;
		this.socket = socket;
		this.clientID = clientID;
		
		this.out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	/**
	 * Communicates with the client in a loop, listening to it and then replying. Never returns.
	 */
	@Override
	public void run() {
		BufferedReader in = null;
		try {
			// Initialize the IO facilities for the socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Continuously read the client's input
			while (true) {
				readClientInputLine(in);
			}
		} catch (IOException e) {
			System.err.println("Error while reading from client " + clientID + ": " + e);
		} finally {
			// Sever the ties with Server so it can't ask for any more actions
			server.removeThread(this);
			
			// Close all connections to the client
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
	 * Adds an immigrating Board for the client to take.
	 */
	public void sendImmigrant(Board immigrant) {
		sendFieldToClient("Immigrant", immigrant);
	}
	
	/**
	 * Reads and acts on the next line of data that the client has sent, using the given Reader. Blocks until a transmission is received.
	 */
	private void readClientInputLine(BufferedReader in) throws IOException {
		// Read the line
		String line = in.readLine();
		if (line == null) {
			throw new IOException("Client closed connection");
		}
		
		// Act upon the data
		Matcher m = pair.matcher(line);
		if (m.matches()) {
			processClientData(m.group(1), m.group(2));
		}
	}
	
	/**
	 * Processes the given field sent by the client and performs the appropriate action.
	 */
	private void processClientData(String name, String value) {
		if (name.equalsIgnoreCase("migrant")) {
			Board migrant = new Board(value);
			server.migrate(migrant);
		} else if (name.equalsIgnoreCase("potentialHighest")) {
			Board b = new Board(value);
			server.considerHighest(b);
		}
	}
	
	/**
	 * Sends the given field to the client, with the value returned by value.toString(). Newlines in the field or the value will cause it to break.
	 */
	private synchronized void sendFieldToClient(String fieldName, Object value) {
		out.println(fieldName + ":" + value);
		out.flush();
	}
}

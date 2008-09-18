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
public class BoggleServerThread extends Thread
        implements
            Comparable<BoggleServerThread> {
	/**
     * The socket used to communicate with the client.
     */
	private Socket socket;
	
	/**
     * The ID of the client that this thread serves.
     */
	private int clientID;
	
	/**
     * A reference to the main server thread that started this worker thread.
     */
	private BoggleServer server;
	
	private PrintWriter out;
	
	private BufferedReader in;
	
	private int score;
	private Boggle migrant;
	
	private static final Pattern pair = Pattern
	        .compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");
	
	public BoggleServerThread(BoggleServer server, Socket socket, int clientID) {
		super("BoggleServerThread");
		
		this.server = server;
		this.socket = socket;
		this.clientID = clientID;
	}
	public int getScore() {
		return score;
	}
	public int compareTo(BoggleServerThread that) {
		return that.getScore() - this.getScore(); // descending order by
                                                    // default
	}
	public Boggle getMigrant() {
		return migrant;
	}
	
	public void setMigrant(Boggle migrant) {
		this.migrant = migrant;
	}
	@Override
	public void run() {
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
			System.exit(1);
		}
	}
	
	private void readClientInput() throws IOException {
		String line;
		Matcher m;
		// for each line in the input
		while (!(line = in.readLine()).isEmpty()) {
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
			Boggle migrant = new Boggle(value, 4, server.getDictionary());
			server.addMigrant(migrant, this);
		} else if (name.equalsIgnoreCase("highest")) {
			Boggle highest = new Boggle(value, 4, server.getDictionary());
			server.setHighest(highest);
		}
	}
	
	public void reset() {
		out.println("Reset: yes");
		out.println();
		
		score = 0;
		migrant = null;
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
	}
	
}

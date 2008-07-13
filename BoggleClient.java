import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoggleClient {
	private int sideLength;
	private String serverAddress;
	private int serverPort;
	private PrintWriter out;
	private BufferedReader in;
	private BogglePopulation bp;
	private Dictionary dict;

	private static Pattern pair = Pattern
	        .compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");

	public BoggleClient(String serverAddress, int serverPort, String dictPath,
	        int sideLength) {
		this.sideLength = sideLength;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;

		// init dictionary
		dict = new Dictionary();
		dict.buildDictionary(dictPath);

		// init population
		bp = new BogglePopulation(sideLength, 5, 5, 20, dict);
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
				
				for (Boggle b : bp.getCurrentGeneration()) {
					System.out.println(b.getScore() + " " + b.gridToString());
				}
				System.out.println();

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
		// tell server the score
		out.println("Score:" + bp.highest().getScore());
		System.err.println("Score:" + bp.highest().getScore());

		// send a migrant to the server
		Boggle highest = bp.highest();
		out.println("Migrant:" + highest.gridToString() + " "
		        + highest.getScore());
		System.err.println("Migrant:" + highest.gridToString() + " "
		        + highest.getScore());

		// end of transmission
		out.println();
		System.err.println();
	}

	private void readServerInput() throws GenerationEmptyException, IOException {
		String line;
		Matcher m;
		// for each line in the input
		while (!(line = in.readLine()).isEmpty()) {
			if (line == null) {
				throw new IOException("Server closed connection");
			}
			// try to find data in it
			m = pair.matcher(line);
			if (m.matches()) {
				storeServerData(m.group(1), m.group(2));
			}
		}
	}

	private void storeServerData(String name, String value) {
		if (name.equalsIgnoreCase("migrant")) {
			String[] parts = value.split(" ", 2);
			Boggle migrant = new Boggle(parts[0], sideLength, Integer
			        .parseInt(parts[1]), dict);
			bp.add(migrant);
		}
	}
}

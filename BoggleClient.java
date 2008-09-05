import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
	private Boggle highest;

	private static final Pattern pair = Pattern
	        .compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");

	public BoggleClient(String serverAddress, int serverPort, String dictPath,
	        int sideLength, int startingPopulation, int childrenPerCouple,
	        int popCap) {
		this.sideLength = sideLength;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;

		// init dictionary
		dict = new Dictionary();
		dict.buildDictionary(dictPath);

		// init population
		bp = new BogglePopulation(sideLength, startingPopulation,
		        childrenPerCouple, popCap, dict);
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

				if (highest == null
				        || bp.highest().getScore() > highest.getScore()) {
					highest = bp.highest();
				}

				giveServerOutput();
				readServerInput();

				System.out.println("Gen " + bp.getGeneration() + ": pop cap="
				        + bp.getPopCap() + "; all time highest="
				        + highest.getScore() + " " + highest.gridToString());
				ArrayList<Boggle> gen = bp.getCurrentGeneration();
				for (Boggle b : gen) {
					System.out.println(b.gridToString() + " " + b.getScore());
				}
				System.out.println();
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

		// send a migrant to the server
		Boggle migrant;
		if (Math.random() < 0.25) {
			migrant = bp.highest();
		} else {
			migrant = bp.random();
		}
		
		out.println("Migrant:" + migrant.gridToString() + " "
		        + migrant.getScore());

		// end of transmission
		out.println();
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
		} else if (name.equalsIgnoreCase("pop-cap")) {
			bp.setPopCap(Integer.parseInt(value));
		}
	}
}

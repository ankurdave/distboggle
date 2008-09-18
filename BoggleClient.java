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
	private int startingPopulation, startingChildrenPerCouple, startingPopCap;
	
	private static final Pattern pair = Pattern
	        .compile("^\\s*([\\w-]+)\\s*:\\s*([\\w -]+)\\s*$");
	
	public BoggleClient(String serverAddress, int serverPort, String dictPath,
	        int sideLength, int startingPopulation, int childrenPerCouple,
	        int popCap) {
		this.sideLength = sideLength;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.startingPopulation = startingPopulation;
		this.startingChildrenPerCouple = childrenPerCouple;
		this.startingPopCap = popCap;
		
		// init dictionary
		dict = new Dictionary();
		dict.buildDictionary(dictPath);
		
		// init population
		bp = new BogglePopulation(sideLength, this.startingPopulation,
		        startingChildrenPerCouple, startingPopCap, dict);
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
		out.println("Highest:" + highest);
		
		// send a migrant to the server
		// TODO analyze migration frequency
		Boggle migrant;
		if (Math.random() < 0.25) {
			migrant = bp.highest();
		} else {
			migrant = bp.random();
		}
		
		out.println("Migrant:" + migrant);
		
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
			Boggle migrant = new Boggle(value, sideLength, dict);
			bp.add(migrant);
		} else if (name.equalsIgnoreCase("pop-cap")) {
			bp.setPopCap(Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("reset")) {
			bp = new BogglePopulation(sideLength, this.startingPopulation,
			        startingChildrenPerCouple, startingPopCap, dict);
		}
	}
}

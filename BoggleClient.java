import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BoggleClient {
	static int SIDE_LENGTH;
	static Scanner in = new Scanner(System.in);
	static char[][] grid1;
	static char[][] grid2;
	static String gridImage = "";
	static Scanner tempIn;

    public static void main(String[] args) throws IOException {
    	String path = "words.txt";
    	SIDE_LENGTH = 4;
    	
    	System.out.println("Started client.");
    	
		// make a board so we can get its dictionaries
		Dictionary dict = new Dictionary();
		dict.buildDictionary(path);
		// make a BogglePopulation
		BogglePopulation bp = new BogglePopulation(SIDE_LENGTH, 5, 5, 20, dict);

		// connect to the server
		System.out.print("Connecting to server... ");
		Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            echoSocket = new Socket("192.168.1.223", 4444);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Couldn't connect: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't connect: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("done.");

		try {
	        String inputLine, outputLine;
		    inputLine = in.readLine();
		    System.out.println("Started evolution.");
			for (int i = 1;; i++) {
				// complete a generation
				System.out.print("Evolving... ");
				bp.evolve();
				System.out.println("done: " + bp);
				
				// send a migrant to the server
				System.out.print("Sending migrant... ");
			    outputLine = bp.highest().gridToString();
		    	out.println(outputLine);
		    	System.out.println("done: " + bp.highest());
		    	
		    	// get a migrant from the server
		    	System.out.print("Getting migrant... ");
			    inputLine = in.readLine();
			    if (inputLine == null) {
			    	System.out.println("Server closed connection.");
			    	break;
			    }
			    if (!inputLine.isEmpty()) {
			    	Boggle migrant = new Boggle(inputLine, SIDE_LENGTH, dict);
			    	bp.add(migrant);
			    	System.out.println("done: " + migrant);
			    } else {
			    	System.out.println("none available.");
			    }
			}
		} catch (GenerationEmptyException e) {
			System.err.println(e);
			System.exit(1);
		}
		out.close();
		in.close();
		echoSocket.close();
    }
}

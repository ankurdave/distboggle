import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class BoggleServer {
	private static int SOCKET = 4444;

	public static int curClientID = 0;

	// holds boards who are migrating from one population to another
	public static ArrayList<String> migrantQueue;

	public static void main(String[] args) throws IOException {
		migrantQueue = new ArrayList<String>();

		// create the server
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(SOCKET);
		}
		catch (IOException e) {
			System.err.println("Could not listen on port " + SOCKET);
			System.exit(1);
		}
		System.out.println("Started server.");

		// listen for clients
		boolean listening = true;
		while (listening) {
			new BoggleServerThread(serverSocket.accept()).start();
		}

		serverSocket.close();

	}
}

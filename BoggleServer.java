import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class BoggleServer {
	private static int SOCKET = 4444;
	
	// holds boards who are migrating from one population to another
	public static Queue<String> migrantQueue;
	
	public static void main(String[] args) throws IOException {
		migrantQueue = new LinkedList<String>();
		
		// create the server
		ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SOCKET);
        } catch (IOException e) {
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

class BoggleServerThread extends Thread {
	private Socket socket = null;

	public BoggleServerThread(Socket socket) {
		super("BoggleServerThread");
		this.socket = socket;
    	System.out.println("New client " + socket.hashCode() + " addr " + socket.getInetAddress());
    }

	public void run() {		
		try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		    String inputLine, outputLine;
		    outputLine = "Go!";
		    out.println(outputLine);
		
		    while ((inputLine = in.readLine()) != null) {
	    		System.out.println(socket.hashCode() + " finished a generation.");
	    		
	    		// take migrants (if any) off queue into outputLine
	    		if (!BoggleServer.migrantQueue.isEmpty()) {
	    			outputLine = BoggleServer.migrantQueue.remove();
		    		System.out.println("Sending migrant: " + outputLine);
	    		} else {
	    			outputLine = "";
	    		}
			    out.println(outputLine);
	    		
	    		// add migrant to queue from inputLine
			    BoggleServer.migrantQueue.add(inputLine);
	    		System.out.println("Receiving migrant: " + inputLine);
		    }
		    
		    out.close();
		    in.close();
		    socket.close();
		
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }

}
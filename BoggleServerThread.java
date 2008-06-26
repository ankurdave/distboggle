import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class BoggleServerThread extends Thread {
	private Socket socket = null;
	private int clientID;

	public BoggleServerThread(Socket socket) {
		super("BoggleServerThread");
		this.socket = socket;
		this.clientID = BoggleServer.curClientID++;
    	System.out.println("New client " + clientID + " addr " + socket.getInetAddress());
    }

	public void run() {		
		try {
			ArrayList<String> mq = BoggleServer.migrantQueue;
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		    String inputLine, outputLine;
		    outputLine = Integer.toString(clientID);
		    out.println(outputLine);
		
		    while ((inputLine = in.readLine()) != null) {
	    		System.out.println(clientID + " finished a generation.");
	    		
	    		// take migrants (if any) off queue into outputLine
	    		if (!mq.isEmpty()) {
	    			Collections.sort(mq, new ByBoardScore());
	    			
	    			String migrant = "";
	    			for (int i = 0; i < mq.size(); i++) {
	    				if (Integer.parseInt(mq.get(i).split(" ")[0]) != clientID) {
	    					migrant = mq.get(i);
	    					mq.remove(i);
	    		    		System.out.println("Sending migrant: " + migrant);
	    					break;
	    				}
	    			}
	    			outputLine = migrant;
	    		} else {
	    			outputLine = "";
	    		}
			    out.println(outputLine);
	    		
	    		// add migrant to queue from inputLine
			    mq.add(inputLine);
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

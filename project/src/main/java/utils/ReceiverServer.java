package utils;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Class for ReceiverServers : UDP servers communication between the application and Java.
 * The ReceiverSocket listen to requests (JSON) from the user (HMI) and instantiates Animators to send the desired information to the app via the SenderServer.
 * 
 * @author ThibaultH
 *
 */

public class ReceiverServer extends Thread {
	
	/** ReceiverSocket */
    private DatagramSocket socket;
    
    /** byte array to wrap the messages */
    private byte[] buf = new byte[256];
 
	/** Mode of request from the HMI : 
	 * 	   -1 - Test case : checks that the connection with the IHM works
	 * 		0 - Shutdown : the user (HMI) requests to close the app : ReceiverSocket is closed
	 *  	1 - Listening : listens to the requests of the HMI
	 * 		2 - Pause : the user (HMI) requests a "pause" of the animation : ReceiverSocket listens until order "resume"
	 */ 
    private int runningMode;
//    private Animator animator; 

    /**
     * Constructor of the ReceiverSocket
     * @throws SocketException
     */
    public ReceiverServer() throws SocketException {
    	
        socket = new DatagramSocket(4433);
    }

    
    /**
     * Runs the ReceiverSocket : for each request, the socket enters a mode.
     * By default, the ReceiverSocket is in mode 1 "listening" : 
     * 		- the user (HMI) requests the step-n constellation of the Genetic optimization : 
	 * 				ReceiverSocket instantiates the corresponding animator and waits for other instructions ; 
	 * 		- the user (HMI) requests to close the app : 
	 * 				ReceiverSocket enters mode 0 "shutdown";
	 * 		- the user (HMI) requests a "pause" of the animation : 
	 * 				ReceiverSocket enters mode 2 "pause" listens until order "resume"
     */
    public void run() {
    	
        runningMode = 1; // mode 1 - "Listening" 
        
       
        while (runningMode==1) {
        	
            DatagramPacket packet 
              = new DatagramPacket(buf, buf.length);
            try {
				socket.receive(packet); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received 
              = new String(packet.getData(), 0, packet.getLength()); //String version of the received JSON
            

            
            //the user (HMI) orders to stop the animation
            /////////////////////to modify : parser problems !! && close the SenderServer as well
            if (received.equals("stop")) {
                runningMode = 0; // enters mode 0 - "Shutdown"
                continue;
            }
            
            
            /////////////////////to modify : parser problems !!
    		JSONParser jsonParser = new JSONParser();
    		JSONObject receivedJSON= null;
    	
    		Object obj=null;
			try {
				obj = jsonParser.parse(received);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		receivedJSON = (JSONObject) obj;
    		System.out.println(receivedJSON.get("request"));
    		
    		
            if (receivedJSON.get("request")=="testConnection") { 
            	System.out.println(received);
                runningMode = -1;
                continue;
            }

        }
        socket.close();
    }
    
    /**
     * closes the ReceiverSocket
     */
	public void close() {
		socket.close();
	}
    
    /**
     * returns the value of running mode
     */
    public int getRunningMode() {
    	return runningMode;
    }
    
}


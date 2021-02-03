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
    private byte[] buf = new byte[Parameters.senderBufferSize];
 
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
    	
        socket = new DatagramSocket(Parameters.receiverPort);
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
    
            String received 
              = new String(packet.getData(), 0, packet.getLength()); //String version of the received JSON
            
            System.out.println("message received : \n"+received+"\n length : " +received.length());
            
            if (received.length()==Parameters.senderBufferSize) {
            	System.out.println("WARNING: The message received may have reached the buffer size limit,\n"
            			+ "pleaser check that the whole message has been received \n"
            			+ "and if not, increase the Buffer size in utils.Parameters");
            }
            
            if(!String.valueOf(received.charAt(received.length()-1)).equals("}")) {
            	System.out.println("WARNING: invalid JSON received. Please check the syntax and the buffer size");
            }
            
            //the user (HMI) orders to stop the animation
            
            if (received.equals("stop")) {
                runningMode = 0; // enters mode 0 - "Shutdown"
                continue;
            }
            
          
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
    		
    		
            if (receivedJSON.get("request").equals("testResult")) { 
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


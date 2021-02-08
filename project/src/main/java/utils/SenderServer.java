package utils;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class for SenderServers : UDP servers communication between the application and Java.
 * The ServerSocket sends to the HMI Animators-generated JSON files with the constellation data at a time t.
 * 
 * @author ThibaultH
 *
 */

public class SenderServer {

	/** SenderSocket */
	private DatagramSocket socket;
	
	/** Internet address : here, localhost */
	private InetAddress address;
	
	/** byte array to wrap the messages */
	private byte[] buf;

	 /**
     * Constructor of the ReceiverSocket
     * @throws SocketException, UnknownHostException
     */
	public SenderServer() throws SocketException, UnknownHostException {
		socket = new DatagramSocket();
		address = InetAddress.getByName("localhost");
	}

	/**
	 * sends to the IHM server a JSON file with constellation data at a time t
	 * @param msgJSON
	 * @throws IOException
	 */
	public void sendJSON(JSONObject msgJSON) throws IOException {
		
		String msg=msgJSON.toJSONString();
		buf = msg.getBytes();
		
		DatagramPacket packet 
		= new DatagramPacket(buf, buf.length, address, Parameters.senderPort);
		socket.send(packet);
		
	}


	/**
	 * closes the ReceiverSocket
	 */
	public void close() {
		socket.close();
	}
}


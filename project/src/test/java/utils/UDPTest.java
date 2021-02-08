package utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.orekit.bodies.GeodeticPoint;

import decisionVector.DecisionVariable;
import utils.JsonReader;
import zone.Zone;

class UDPTest {

	

	@Test
	public void testUDPConnection() throws IOException {
		

		//initialization of the servers
		ReceiverServer receiver = new ReceiverServer();
		receiver.start();		
		SenderServer sender = new SenderServer();		
		
		//creation of the JSON request file
        JSONObject obj = new JSONObject();
        obj.put("request", "testConnection");
        
        //sending the request
		sender.sendJSON(obj);
		

		//We wait for 1 s before asserting in order to let the connection being made
		try {
			Thread.sleep(100);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(receiver.getRunningMode(),-1);
		receiver.close();
		sender.close();
	}

	

}

package org.camunda.consulting.message_exchange.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMessage{
	private final static Logger LOGGER = LoggerFactory.getLogger(SendMessage.class);
	
	// This function sends messages between the different business process tasks
	// A basic URL Connection is built, which calls the REST API of Camunda
	
	public static void send(URL urltoread, String json, Boolean Bproxy) {		
	    HttpURLConnection conn;
		Proxy proxy;
		
		try {
			if(Bproxy) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8080));
				conn = (HttpURLConnection) urltoread.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) urltoread.openConnection();
			}
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
		
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
			        new OutputStreamWriter(os, "UTF-8"));
			writer.write(json);
			writer.flush();
			writer.close();
			os.close();

			conn.connect();
			
			String responseCode = Integer.toString(conn.getResponseCode());

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			LOGGER.info("send message json string");
		    LOGGER.info(json.toString());
		    LOGGER.info("POST response code");
		    LOGGER.info(responseCode);
			LOGGER.info(response.toString());
	    }
	    catch (Exception e) {
	    	LOGGER.info("send message exception");
	    	LOGGER.info(e.getMessage());
	    	LOGGER.info(urltoread.toString());
	    	LOGGER.info(json);
	    } 
	}
	public static String receive(URL urltoread, Boolean Bproxy) {
		String res = null; 
		HttpURLConnection conn;
		Proxy proxy;
		
		try {
			StringBuilder result = new StringBuilder();
			
			if(Bproxy) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.41.188.198", 8080));
				conn = (HttpURLConnection) urltoread.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) urltoread.openConnection();
			}
			
			conn.setRequestMethod("GET");
		    conn.setRequestProperty("Accept-Charset", "UTF-8");
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			res = result.toString();
		} catch (Exception e) {
			res = "";
		}
		
		return res;
	}
}

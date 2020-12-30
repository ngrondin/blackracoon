package io.blackracoon;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import io.firebus.utils.DataMap;


public class Server extends NanoHTTPD {

    public Server() throws IOException {
        super(8081);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.POST) {
        	try {
        		DataMap map = new DataMap(session.getInputStream());
        		Executor executor = new Executor(map);
        		executor.start();
                return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"result\":\"ok\", \"id\":\"" + executor.id + "\"}");
            } catch (Exception e) {
            	return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"result\":\"error\",\"message\":\"" + e.getMessage() + "\"");
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, 
          "The requested resource does not exist");
    }
    
    public static void main(String[] args ) throws IOException {
		String chromeDriverPath = args[0];
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        new Server();
    }
}

package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class App 
{
    static int PORT = 8080;
    public static void main(String[] args) throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        // TODO: two lines of code are expected to be added here
        // please refer to the HTML server example 
        //Utils utils =new Utils();
        //server.createContext("/api/v1/", (HttpHandler) utils);
        //server.createContext("/api/v1/"+utils.getBody());
        //server.createContext("/api/v1", utils::getBody);
        //server.start();
//        Utils utils =new Utils();
//        server.createContext("/api/v1", new HttpHandler() {
//        	public void handle(HttpExchange request)throws IOException{
//        		String met=exchange.getRequestMethod();
//        		if(met)
//        	}
//        });
        Handler hand = new Handler();
        server.createContext("/api/v1", hand::handle);
        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
}

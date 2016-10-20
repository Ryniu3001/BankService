package bsr.bank;

import bsr.bank.services.BankService;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        NetworkListener networkListener = new NetworkListener("jaxws-listener", "127.0.0.1", 8088);


        HttpHandler httpHandler = new JaxwsHandler(new BankService());
        httpServer.getServerConfiguration().addHttpHandler(httpHandler, "/bankService");
        httpServer.addListener(networkListener);

        httpServer.start();
        System.in.read();
        httpServer.shutdown();

    }
}

package bsr.bank;

import bsr.bank.dao.AccountDAO;
import bsr.bank.rest.AuthenticationFilter;
import bsr.bank.service.BankService;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class App extends Application{
    public static String THIS_BANK = "05109711";

    public static void main(String[] args) throws IOException {

        System.out.println("Inicializacja bazy: " + AccountDAO.getInstance().createTables());

        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8088).build();
        ResourceConfig config = new ResourceConfig().packages("bsr.bank.rest");
        config.register(AuthenticationFilter.class);

        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);

        HttpHandler httpHandler = new JaxwsHandler(new BankService());
        httpServer.getServerConfiguration().addHttpHandler(httpHandler, "/soap");
        NetworkListener networkListener = new NetworkListener("jaxws-listener", "127.0.0.1", 8188);
        httpServer.addListener(networkListener);

        httpServer.start();


    }
}

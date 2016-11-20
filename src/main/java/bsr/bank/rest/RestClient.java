package bsr.bank.rest;

import bsr.bank.service.Utils;
import bsr.bank.service.message.TransferRequest;
import bsr.bank.service.message.exception.BankServiceException;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RestClient {

    private static Properties prop = new Properties();
    private static InputStream input = null;

    static {
        try {
            input = new FileInputStream(new File(RestClient.class.getClassLoader().getResource("banks.properties").getFile()));
            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (input != null) {
                try { input.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    public static void invokeTransfer(TransferRequest msg) throws BankServiceException {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        String bankId = Utils.getBankId(msg.getTargetAccountNumber());
        String bankIp = prop.getProperty(bankId);
        if (bankIp == null)
            throw new BankServiceException("Brak definicji dla banku o ID: " + bankId, BankServiceException.VALIDATION_ERROR);
        System.out.println(bankId + ": " + bankIp);
        WebTarget target = client.target(bankIp);
        Response response = target.path("transfer")
                                .request()
                                .post(Entity.entity(new TransferExternalMsg(), MediaType.APPLICATION_JSON), Response.class);

        System.out.println(response);
        System.out.println(response.readEntity(String.class));

    }
}

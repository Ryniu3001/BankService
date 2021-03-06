package bsr.bank.rest;

import bsr.bank.service.message.TransferRequest;
import bsr.bank.service.message.exception.BankServiceException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static bsr.bank.rest.RestHelper.getProperties;

public class RestClient {



    public static void invokeTransfer(TransferRequest msg, String bankId) throws BankServiceException {
        ClientConfig config = new ClientConfig();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(getProperties().getProperty("login"), getProperties().getProperty("pass"));
        config.register(feature);
        Client client = ClientBuilder.newClient(config);

        //String bankId = Utils.getBankId(msg.getTargetAccountNumber());
        String bankIp = getProperties().getProperty(bankId);
        if (bankIp == null)
            throw new BankServiceException("Brak definicji dla banku o ID: " + bankId, BankServiceException.VALIDATION_ERROR);
        System.out.println(bankId + ": " + bankIp);
        WebTarget target = client.target(bankIp);
        TransferExternalMsg request = prepareRequest(msg);

        Response response = target.path("transfer")
                                .request()
                                .post(Entity.entity(request, MediaType.APPLICATION_JSON), Response.class);

        System.out.println(response);
        if (response.getStatus() != Response.Status.CREATED.getStatusCode()){
            String errorMsg = response.readEntity(String.class);

            System.out.println(errorMsg);
            throw new BankServiceException("Bank zewnętrzny zwrócił błąd operacji: " + errorMsg, BankServiceException.REST_SERVICE_ERROR);
        }

    }

    private static TransferExternalMsg prepareRequest(TransferRequest request){
        TransferExternalMsg externalMsg = new TransferExternalMsg();
        externalMsg.setTitle(request.getTitle());
        externalMsg.setAmount(request.getAmount());
        externalMsg.setReceiverAccount(request.getTargetAccountNumber());
        externalMsg.setSenderAccount(request.getSourceAccountNumber());
        return externalMsg;
    }
}

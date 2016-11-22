package bsr.bank.rest;

import bsr.bank.dao.OperationDAO;
import bsr.bank.dao.message.OperationMsg;
import bsr.bank.service.message.exception.BankServiceException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class RestService {
    @POST
    @Path("/executeOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfer(TransferExternalMsg msg) {
        try {
            transferMoneyExternal(msg);
        } catch (BankServiceException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMsg(e.getFaultInfo().getDetails())).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    private static void transferMoneyExternal(TransferExternalMsg request) throws BankServiceException {
        OperationMsg operationMsg = new OperationMsg(request.getReceiverAccount());
        operationMsg.setTitle(request.getTitle());
        operationMsg.setAmount(request.getAmount());
        operationMsg.setNrb(request.getSenderAccount());
        operationMsg.setType(OperationMsg.typeTransfer);
        operationMsg.setDate(System.currentTimeMillis() / 1000L);
        OperationDAO.getInstance().executeOperation(operationMsg);
    }
}
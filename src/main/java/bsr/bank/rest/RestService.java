package bsr.bank.rest;

import bsr.bank.dao.OperationDAO;
import bsr.bank.dao.message.OperationEnum;
import bsr.bank.dao.message.OperationMsg;
import bsr.bank.service.message.exception.BankServiceException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Usługa REST odpowiedzialna za odbieranie przelewów z innych banków
 */
@Path("/")
public class RestService {
    /**
     * Implementacja usługi transfer
     * @param msg
     * @return
     */
    @POST
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfer(TransferExternalMsg msg) {
        try {
            if (!validate(msg))
                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMsg("Bad request")).build();
            if (hasLetter(msg.getSenderAccount()) || hasLetter(msg.getReceiverAccount())){
                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMsg("Account number cannot contain letters")).build();
            }
            transferMoneyExternal(msg);
        } catch (BankServiceException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMsg(e.getFaultInfo().getDetails())).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Przygotowuje operacje odbioru przelewu z innego banku i zleca jej wykonanie na bazie danych
     * @param request
     * @throws BankServiceException
     */
    private static void transferMoneyExternal(TransferExternalMsg request) throws BankServiceException {
        OperationMsg operationMsg = new OperationMsg(request.getReceiverAccount());
        operationMsg.setTitle(request.getTitle());
        operationMsg.setAmount(request.getAmount());
        operationMsg.setNrb(request.getSenderAccount());
        operationMsg.setType(OperationEnum.przelew.getValue());
        operationMsg.setDate(System.currentTimeMillis());
        OperationDAO.getInstance().executeOperation(operationMsg);
    }

    /**
     * Prosta walidacja danych wejściowych
     * @param msg
     * @return
     */
    private boolean validate(TransferExternalMsg msg){
        if (nullOrEmpty(msg.getReceiverAccount())
                || nullOrEmpty(msg.getSenderAccount())
                || nullOrEmpty(msg.getTitle())
                || msg.getAmount() == null
                || msg.getAmount() < 0 ){
            return false;
        }
        return true;
    }

    private boolean nullOrEmpty(String str){
        if (str != null && !str.isEmpty())
            return false;
        return true;
    }

    private boolean hasLetter(String str){
        return str.chars().anyMatch(Character::isAlphabetic);
    }
}
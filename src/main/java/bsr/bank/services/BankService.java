package bsr.bank.services;

import bsr.bank.services.message.BankServiceException;
import bsr.bank.services.message.RegisterRequest;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

@WebService(name = "BankPortType", portName = "BankPort", serviceName = "BankService", targetNamespace = "http://bsr.bank.pl")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.LITERAL)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class BankService {

    @WebMethod(operationName = "register")
    public void register(RegisterRequest request) throws BankServiceException{



    }
}

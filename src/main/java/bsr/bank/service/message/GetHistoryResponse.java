package bsr.bank.service.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetHistoryResponse {

    List<Operation> operations;

    public GetHistoryResponse() {
    }

    public GetHistoryResponse(List<Operation> operations) {
        this.operations = operations;
    }

    public List<Operation> getOperations() {
        return operations;
    }
}

package bsr.bank.rest.errorproviders;

import com.fasterxml.jackson.databind.JsonMappingException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by marcin on 13.01.17.
 */
@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    @Override
    public Response toResponse(JsonMappingException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getPath().get(0).getFieldName() + " is invalid\"}")
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}

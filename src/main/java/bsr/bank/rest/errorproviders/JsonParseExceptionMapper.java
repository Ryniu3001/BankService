package bsr.bank.rest.errorproviders;

import com.fasterxml.jackson.core.JsonParseException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by marcin on 13.01.17.
 */
@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
    @Override
    public Response toResponse(JsonParseException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"invalid JSON format\"}")
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}

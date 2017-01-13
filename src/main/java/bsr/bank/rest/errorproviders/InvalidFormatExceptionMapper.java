package bsr.bank.rest.errorproviders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by student on 13.01.2017.
 */
//np string z literkami dla pola int
@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {
    @Override
    public Response toResponse(InvalidFormatException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"invalid JSON format\"}")
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}

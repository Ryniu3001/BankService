package bsr.bank.rest.errorproviders;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by marcin on 13.01.17.
 */
@Provider
public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException>{

        @Override
        public Response toResponse(UnrecognizedPropertyException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getPath().get(0).getFieldName() + " is unrecognized\"}")
                    .type(MediaType.APPLICATION_JSON_TYPE).build();
        }

}

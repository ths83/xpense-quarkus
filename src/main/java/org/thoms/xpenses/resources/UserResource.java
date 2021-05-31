package org.thoms.xpenses.resources;

import org.thoms.xpenses.services.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/users")
public class UserResource {

    @Inject
    UserService service;

    @GET
    @Path("{username}")
    public Response get(@PathParam("username") final String username) {
        return Optional.ofNullable(Response.ok(service.get(username)))
                .orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
                .build();
    }
}

package org.thoms.xpenses.controller;

import org.thoms.xpenses.services.UserService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/users")
public class UserController {

    @Inject
    UserService service;

    @GET
    @Path("{username}")
    public Response get(@PathParam("username") final String username) {
        return Optional.ofNullable(
                Response
                        .status(Response.Status.OK)
                        .entity(service.get(username)))
                .orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
                .build();
    }
}

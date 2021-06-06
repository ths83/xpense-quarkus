package org.thoms.xpenses.controller;

import org.thoms.xpenses.model.request.activities.CreateActivityRequest;
import org.thoms.xpenses.model.request.activities.UpdateActivityRequest;
import org.thoms.xpenses.services.ActivityService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/activities")
public class ActivityController {

    @Inject
    ActivityService service;

    @POST
    public Response create(final CreateActivityRequest request) {
        return Response.ok(service.create(request.getName(), request.getCreatedBy())).build();
    }

    @GET
    @Path("{activityId}")
    public Response get(@PathParam("activityId") final String activityId) {
        return Optional.ofNullable(Response.ok(service.get(activityId)))
                .orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
                .build();
    }

    @GET
    public Response getByUsername(@QueryParam("username") final String username) {
        return Optional.ofNullable(Response.ok(service.getByUsername(username)))
                .orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
                .build();
    }

    @PUT
    @Path("{activityId}")
    public Response update(@PathParam("activityId") final String activityId, final UpdateActivityRequest request) {
        service.update(activityId, request.getName(), request.getDate());
        return Response.noContent().build();
    }

    @DELETE
    @Path("{activityId}")
    public Response delete(@PathParam("activityId") final String activityId) {
        service.delete(activityId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("{activityId}")
    public Response close(@PathParam("activityId") final String activityId) {
        service.close(activityId);
        return Response.noContent().build();
    }
}
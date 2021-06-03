package org.thoms.xpenses.resources;

import org.thoms.xpenses.model.request.CreateRequestBody;
import org.thoms.xpenses.model.request.UpdateRequestBody;
import org.thoms.xpenses.services.ActivityService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/activities")
public class ActivityResource {

    @Inject
    ActivityService service;

    @POST
    public Response create(final CreateRequestBody request) {
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
    public void update(@PathParam("activityId") final String activityId, final UpdateRequestBody request) {
        service.update(activityId, request.getName(), request.getDate());
    }

    @DELETE
    @Path("{activityId}")
    public void delete(@PathParam("activityId") final String activityId) {
        service.delete(activityId);
    }

    @PATCH
    @Path("{activityId}")
    public void close(@PathParam("activityId") final String activityId) {
        service.close(activityId);
    }
}
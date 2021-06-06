package org.thoms.xpenses.controller;

import org.thoms.xpenses.model.Expense;
import org.thoms.xpenses.model.request.expenses.UpdateExpenseRequest;
import org.thoms.xpenses.services.ExpenseService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/expenses")
public class ExpenseController {

    @Inject
    ExpenseService service;

    @POST
    public Response create(final Expense request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(service.create(request))
                .build();
    }

    @GET
    @Path("{expenseId}")
    public Response get(@PathParam("expenseId") final String expenseId) {
        return Optional.ofNullable(Response.ok(service.get(expenseId)))
                .orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
                .build();
    }

    @GET
    public Response getByActivity(@QueryParam("activityId") final String activityId) {
        return Optional.ofNullable(Response.ok(service.getByActivity(activityId)))
                .orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
                .build();
    }

    @PUT
    @Path("/expenses/{expenseId}")
    public Response update(@PathParam("expenseId") final String expenseId, final UpdateExpenseRequest request) {
        service.update(expenseId, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{activityId}/expenses/{expenseId}")
    public Response delete(@PathParam("activityId") final String activityId, @PathParam("expenseId") final String expenseId) {
        service.delete(activityId, expenseId);
        return Response.noContent().build();
    }
}
